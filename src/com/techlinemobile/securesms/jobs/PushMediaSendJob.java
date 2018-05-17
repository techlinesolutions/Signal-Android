package com.techlinemobile.securesms.jobs;

import android.content.Context;
import android.util.Log;

import com.techlinemobile.securesms.ApplicationContext;
import com.techlinemobile.securesms.attachments.Attachment;
import com.techlinemobile.securesms.database.Address;
import com.techlinemobile.securesms.database.DatabaseFactory;
import com.techlinemobile.securesms.database.MmsDatabase;
import com.techlinemobile.securesms.database.NoSuchMessageException;
import com.techlinemobile.securesms.dependencies.InjectableType;
import com.techlinemobile.securesms.mms.DecryptableStreamUriLoader;
import com.techlinemobile.securesms.mms.MediaConstraints;
import com.techlinemobile.securesms.mms.MmsException;
import com.techlinemobile.securesms.mms.OutgoingMediaMessage;
import com.techlinemobile.securesms.service.ExpiringMessageManager;
import com.techlinemobile.securesms.transport.InsecureFallbackApprovalException;
import com.techlinemobile.securesms.transport.RetryLaterException;
import com.techlinemobile.securesms.transport.UndeliverableMessageException;
import com.techlinemobile.securesms.util.BitmapDecodingException;
import com.techlinemobile.securesms.util.BitmapUtil;
import com.techlinemobile.securesms.util.MediaUtil;
import org.whispersystems.libsignal.util.guava.Optional;
import org.whispersystems.signalservice.api.SignalServiceMessageSender;
import org.whispersystems.signalservice.api.crypto.UntrustedIdentityException;
import org.whispersystems.signalservice.api.messages.SignalServiceAttachment;
import org.whispersystems.signalservice.api.messages.SignalServiceAttachmentPointer;
import org.whispersystems.signalservice.api.messages.SignalServiceDataMessage;
import org.whispersystems.signalservice.api.push.SignalServiceAddress;
import org.whispersystems.signalservice.api.push.exceptions.UnregisteredUserException;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

public class PushMediaSendJob extends PushSendJob implements InjectableType {

  private static final long serialVersionUID = 1L;

  private static final String TAG = PushMediaSendJob.class.getSimpleName();

  @Inject transient SignalServiceMessageSender messageSender;

  private final long messageId;

  public PushMediaSendJob(Context context, long messageId, Address destination) {
    super(context, constructParameters(context, destination));
    this.messageId = messageId;
  }

  @Override
  public void onAdded() {

  }

  @Override
  public void onPushSend()
      throws RetryLaterException, MmsException, NoSuchMessageException,
             UndeliverableMessageException
  {
    ExpiringMessageManager expirationManager = ApplicationContext.getInstance(context).getExpiringMessageManager();
    MmsDatabase            database          = DatabaseFactory.getMmsDatabase(context);
    OutgoingMediaMessage   message           = database.getOutgoingMessage(messageId);

    try {
      deliver(message);
      database.markAsSent(messageId, true);
      markAttachmentsUploaded(messageId, message.getAttachments());

      if (message.getExpiresIn() > 0 && !message.isExpirationUpdate()) {
        database.markExpireStarted(messageId);
        expirationManager.scheduleDeletion(messageId, true, message.getExpiresIn());
      }

    } catch (InsecureFallbackApprovalException ifae) {
      Log.w(TAG, ifae);
      database.markAsPendingInsecureSmsFallback(messageId);
      notifyMediaMessageDeliveryFailed(context, messageId);
      ApplicationContext.getInstance(context).getJobManager().add(new DirectoryRefreshJob(context, false));
    } catch (UntrustedIdentityException uie) {
      Log.w(TAG, uie);
      database.addMismatchedIdentity(messageId, Address.fromSerialized(uie.getE164Number()), uie.getIdentityKey());
      database.markAsSentFailed(messageId);
    }
  }

  @Override
  public boolean onShouldRetryThrowable(Exception exception) {
    if (exception instanceof RequirementNotMetException) return true;
    if (exception instanceof RetryLaterException)        return true;

    return false;
  }

  @Override
  public void onCanceled() {
    DatabaseFactory.getMmsDatabase(context).markAsSentFailed(messageId);
    notifyMediaMessageDeliveryFailed(context, messageId);
  }

  private void deliver(OutgoingMediaMessage message)
      throws RetryLaterException, InsecureFallbackApprovalException, UntrustedIdentityException,
             UndeliverableMessageException
  {
    if (message.getRecipient() == null) {
      throw new UndeliverableMessageException("No destination address.");
    }

    try {
      SignalServiceAddress                     address           = getPushAddress(message.getRecipient().getAddress());
      MediaConstraints                         mediaConstraints  = MediaConstraints.getPushMediaConstraints();
      List<Attachment>                         scaledAttachments = scaleAndStripExifFromAttachments(mediaConstraints, message.getAttachments());
      List<SignalServiceAttachment>            attachmentStreams = getAttachmentsFor(scaledAttachments);
      Optional<byte[]>                         profileKey        = getProfileKey(message.getRecipient());
      Optional<SignalServiceDataMessage.Quote> quote             = getQuoteFor(message);
      SignalServiceDataMessage                 mediaMessage      = SignalServiceDataMessage.newBuilder()
                                                                                           .withBody(message.getBody())
                                                                                           .withAttachments(attachmentStreams)
                                                                                           .withTimestamp(message.getSentTimeMillis())
                                                                                           .withExpiration((int)(message.getExpiresIn() / 1000))
                                                                                           .withProfileKey(profileKey.orNull())
                                                                                           .withQuote(quote.orNull())
                                                                                           .asExpirationUpdate(message.isExpirationUpdate())
                                                                                           .build();

      messageSender.sendMessage(address, mediaMessage);
    } catch (UnregisteredUserException e) {
      Log.w(TAG, e);
      throw new InsecureFallbackApprovalException(e);
    } catch (FileNotFoundException e) {
      Log.w(TAG, e);
      throw new UndeliverableMessageException(e);
    } catch (IOException e) {
      Log.w(TAG, e);
      throw new RetryLaterException(e);
    }
  }

}