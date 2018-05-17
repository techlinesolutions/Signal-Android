package com.techlinemobile.securesms.mms;

import com.techlinemobile.securesms.attachments.Attachment;
import com.techlinemobile.securesms.database.ThreadDatabase;
import com.techlinemobile.securesms.recipients.Recipient;

import java.util.LinkedList;

public class OutgoingExpirationUpdateMessage extends OutgoingSecureMediaMessage {

  public OutgoingExpirationUpdateMessage(Recipient recipient, long sentTimeMillis, long expiresIn) {
    super(recipient, "", new LinkedList<Attachment>(), sentTimeMillis,
          ThreadDatabase.DistributionTypes.CONVERSATION, expiresIn, null);
  }

  @Override
  public boolean isExpirationUpdate() {
    return true;
  }

}
