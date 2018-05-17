package com.techlinemobile.securesms.dependencies;

import android.content.Context;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import com.techlinemobile.securesms.BuildConfig;
import com.techlinemobile.securesms.CreateProfileActivity;
import com.techlinemobile.securesms.DeviceListFragment;
import com.techlinemobile.securesms.crypto.storage.SignalProtocolStoreImpl;
import com.techlinemobile.securesms.events.ReminderUpdateEvent;
import com.techlinemobile.securesms.jobs.AttachmentDownloadJob;
import com.techlinemobile.securesms.jobs.AvatarDownloadJob;
import com.techlinemobile.securesms.jobs.CleanPreKeysJob;
import com.techlinemobile.securesms.jobs.CreateSignedPreKeyJob;
import com.techlinemobile.securesms.jobs.GcmRefreshJob;
import com.techlinemobile.securesms.jobs.MultiDeviceBlockedUpdateJob;
import com.techlinemobile.securesms.jobs.MultiDeviceContactUpdateJob;
import com.techlinemobile.securesms.jobs.MultiDeviceGroupUpdateJob;
import com.techlinemobile.securesms.jobs.MultiDeviceProfileKeyUpdateJob;
import com.techlinemobile.securesms.jobs.MultiDeviceReadReceiptUpdateJob;
import com.techlinemobile.securesms.jobs.MultiDeviceReadUpdateJob;
import com.techlinemobile.securesms.jobs.MultiDeviceVerifiedUpdateJob;
import com.techlinemobile.securesms.jobs.PushGroupSendJob;
import com.techlinemobile.securesms.jobs.PushGroupUpdateJob;
import com.techlinemobile.securesms.jobs.PushMediaSendJob;
import com.techlinemobile.securesms.jobs.PushNotificationReceiveJob;
import com.techlinemobile.securesms.jobs.PushTextSendJob;
import com.techlinemobile.securesms.jobs.RefreshAttributesJob;
import com.techlinemobile.securesms.jobs.RefreshPreKeysJob;
import com.techlinemobile.securesms.jobs.RequestGroupInfoJob;
import com.techlinemobile.securesms.jobs.RetrieveProfileAvatarJob;
import com.techlinemobile.securesms.jobs.RetrieveProfileJob;
import com.techlinemobile.securesms.jobs.RotateSignedPreKeyJob;
import com.techlinemobile.securesms.jobs.SendReadReceiptJob;
import com.techlinemobile.securesms.preferences.AppProtectionPreferenceFragment;
import com.techlinemobile.securesms.preferences.SmsMmsPreferenceFragment;
import com.techlinemobile.securesms.push.SecurityEventListener;
import com.techlinemobile.securesms.push.SignalServiceNetworkAccess;
import com.techlinemobile.securesms.service.MessageRetrievalService;
import com.techlinemobile.securesms.service.WebRtcCallService;
import com.techlinemobile.securesms.util.TextSecurePreferences;
import org.whispersystems.libsignal.util.guava.Optional;
import org.whispersystems.signalservice.api.SignalServiceAccountManager;
import org.whispersystems.signalservice.api.SignalServiceMessageReceiver;
import org.whispersystems.signalservice.api.SignalServiceMessageSender;
import org.whispersystems.signalservice.api.util.CredentialsProvider;
import org.whispersystems.signalservice.api.websocket.ConnectivityListener;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, injects = {CleanPreKeysJob.class,
                                     CreateSignedPreKeyJob.class,
                                     PushGroupSendJob.class,
                                     PushTextSendJob.class,
                                     PushMediaSendJob.class,
                                     AttachmentDownloadJob.class,
                                     RefreshPreKeysJob.class,
                                     MessageRetrievalService.class,
                                     PushNotificationReceiveJob.class,
                                     MultiDeviceContactUpdateJob.class,
                                     MultiDeviceGroupUpdateJob.class,
                                     MultiDeviceReadUpdateJob.class,
                                     MultiDeviceBlockedUpdateJob.class,
                                     DeviceListFragment.class,
                                     RefreshAttributesJob.class,
                                     GcmRefreshJob.class,
                                     RequestGroupInfoJob.class,
                                     PushGroupUpdateJob.class,
                                     AvatarDownloadJob.class,
                                     RotateSignedPreKeyJob.class,
                                     WebRtcCallService.class,
                                     RetrieveProfileJob.class,
                                     MultiDeviceVerifiedUpdateJob.class,
                                     CreateProfileActivity.class,
                                     RetrieveProfileAvatarJob.class,
                                     MultiDeviceProfileKeyUpdateJob.class,
                                     SendReadReceiptJob.class,
                                     MultiDeviceReadReceiptUpdateJob.class,
                                     AppProtectionPreferenceFragment.class})
public class SignalCommunicationModule {

  private static final String TAG = SignalCommunicationModule.class.getSimpleName();

  private final Context                      context;
  private final SignalServiceNetworkAccess   networkAccess;

  private SignalServiceAccountManager  accountManager;
  private SignalServiceMessageSender   messageSender;
  private SignalServiceMessageReceiver messageReceiver;

  public SignalCommunicationModule(Context context, SignalServiceNetworkAccess networkAccess) {
    this.context       = context;
    this.networkAccess = networkAccess;
  }

  @Provides
  synchronized SignalServiceAccountManager provideSignalAccountManager() {
    if (this.accountManager == null) {
      this.accountManager = new SignalServiceAccountManager(networkAccess.getConfiguration(context),
                                                            new DynamicCredentialsProvider(context),
                                                            BuildConfig.USER_AGENT);
    }

    return this.accountManager;
  }

  @Provides
  synchronized SignalServiceMessageSender provideSignalMessageSender() {
    if (this.messageSender == null) {
      this.messageSender = new SignalServiceMessageSender(networkAccess.getConfiguration(context),
                                                          new DynamicCredentialsProvider(context),
                                                          new SignalProtocolStoreImpl(context),
                                                          BuildConfig.USER_AGENT,
                                                          Optional.fromNullable(MessageRetrievalService.getPipe()),
                                                          Optional.of(new SecurityEventListener(context)));
    } else {
      this.messageSender.setMessagePipe(MessageRetrievalService.getPipe());
    }

    return this.messageSender;
  }

  @Provides
  synchronized SignalServiceMessageReceiver provideSignalMessageReceiver() {
    if (this.messageReceiver == null) {
      this.messageReceiver = new SignalServiceMessageReceiver(networkAccess.getConfiguration(context),
                                                              new DynamicCredentialsProvider(context),
                                                              BuildConfig.USER_AGENT,
                                                              new PipeConnectivityListener());
    }

    return this.messageReceiver;
  }

  private static class DynamicCredentialsProvider implements CredentialsProvider {

    private final Context context;

    private DynamicCredentialsProvider(Context context) {
      this.context = context.getApplicationContext();
    }

    @Override
    public String getUser() {
      return TextSecurePreferences.getLocalNumber(context);
    }

    @Override
    public String getPassword() {
      return TextSecurePreferences.getPushServerPassword(context);
    }

    @Override
    public String getSignalingKey() {
      return TextSecurePreferences.getSignalingKey(context);
    }
  }

  private class PipeConnectivityListener implements ConnectivityListener {

    @Override
    public void onConnected() {
      Log.w(TAG, "onConnected()");
    }

    @Override
    public void onConnecting() {
      Log.w(TAG, "onConnecting()");
    }

    @Override
    public void onDisconnected() {
      Log.w(TAG, "onDisconnected()");
    }

    @Override
    public void onAuthenticationFailure() {
      Log.w(TAG, "onAuthenticationFailure()");
      TextSecurePreferences.setUnauthorizedReceived(context, true);
      EventBus.getDefault().post(new ReminderUpdateEvent());
    }

  }

}
