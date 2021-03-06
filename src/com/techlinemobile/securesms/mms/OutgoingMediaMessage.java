package com.techlinemobile.securesms.mms;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.techlinemobile.securesms.attachments.Attachment;
import com.techlinemobile.securesms.recipients.Recipient;

import java.util.List;

public class OutgoingMediaMessage {

  private   final Recipient        recipient;
  protected final String           body;
  protected final List<Attachment> attachments;
  private   final long             sentTimeMillis;
  private   final int              distributionType;
  private   final int              subscriptionId;
  private   final long             expiresIn;
  private   final QuoteModel       outgoingQuote;

  public OutgoingMediaMessage(Recipient recipient, String message,
                              List<Attachment> attachments, long sentTimeMillis,
                              int subscriptionId, long expiresIn,
                              int distributionType, @Nullable QuoteModel outgoingQuote)
  {
    this.recipient        = recipient;
    this.body             = message;
    this.sentTimeMillis   = sentTimeMillis;
    this.distributionType = distributionType;
    this.attachments      = attachments;
    this.subscriptionId   = subscriptionId;
    this.expiresIn        = expiresIn;
    this.outgoingQuote    = outgoingQuote;
  }

  public OutgoingMediaMessage(Recipient recipient, SlideDeck slideDeck, String message, long sentTimeMillis, int subscriptionId, long expiresIn, int distributionType, @Nullable QuoteModel outgoingQuote)
  {
    this(recipient,
         buildMessage(slideDeck, message),
         slideDeck.asAttachments(),
         sentTimeMillis, subscriptionId,
         expiresIn, distributionType, outgoingQuote);
  }

  public OutgoingMediaMessage(OutgoingMediaMessage that) {
    this.recipient        = that.getRecipient();
    this.body             = that.body;
    this.distributionType = that.distributionType;
    this.attachments      = that.attachments;
    this.sentTimeMillis   = that.sentTimeMillis;
    this.subscriptionId   = that.subscriptionId;
    this.expiresIn        = that.expiresIn;
    this.outgoingQuote    = that.outgoingQuote;
  }

  public Recipient getRecipient() {
    return recipient;
  }

  public String getBody() {
    return body;
  }

  public List<Attachment> getAttachments() {
    return attachments;
  }

  public int getDistributionType() {
    return distributionType;
  }

  public boolean isSecure() {
    return false;
  }

  public boolean isGroup() {
    return false;
  }

  public boolean isExpirationUpdate() {
    return false;
  }

  public long getSentTimeMillis() {
    return sentTimeMillis;
  }

  public int getSubscriptionId() {
    return subscriptionId;
  }

  public long getExpiresIn() {
    return expiresIn;
  }

  public @Nullable QuoteModel getOutgoingQuote() {
    return outgoingQuote;
  }

  private static String buildMessage(SlideDeck slideDeck, String message) {
    if (!TextUtils.isEmpty(message) && !TextUtils.isEmpty(slideDeck.getBody())) {
      return slideDeck.getBody() + "\n\n" + message;
    } else if (!TextUtils.isEmpty(message)) {
      return message;
    } else {
      return slideDeck.getBody();
    }
  }

}
