package com.techlinemobile.securesms.mms;

import android.support.annotation.Nullable;

import com.techlinemobile.securesms.attachments.Attachment;
import com.techlinemobile.securesms.recipients.Recipient;

import java.util.List;

public class OutgoingSecureMediaMessage extends OutgoingMediaMessage {

  public OutgoingSecureMediaMessage(Recipient recipient, String body,
                                    List<Attachment> attachments,
                                    long sentTimeMillis,
                                    int distributionType,
                                    long expiresIn,
                                    @Nullable QuoteModel quote)
  {
    super(recipient, body, attachments, sentTimeMillis, -1, expiresIn, distributionType, quote);
  }

  public OutgoingSecureMediaMessage(OutgoingMediaMessage base) {
    super(base);
  }

  @Override
  public boolean isSecure() {
    return true;
  }
}
