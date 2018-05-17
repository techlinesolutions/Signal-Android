package com.techlinemobile.securesms;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.techlinemobile.securesms.crypto.MasterSecret;
import com.techlinemobile.securesms.database.model.MessageRecord;
import com.techlinemobile.securesms.database.model.MmsMessageRecord;
import com.techlinemobile.securesms.mms.GlideRequests;
import com.techlinemobile.securesms.recipients.Recipient;

import java.util.Locale;
import java.util.Set;

public interface BindableConversationItem extends Unbindable {
  void bind(@NonNull MessageRecord      messageRecord,
            @NonNull GlideRequests      glideRequests,
            @NonNull Locale             locale,
            @NonNull Set<MessageRecord> batchSelected,
            @NonNull Recipient          recipients,
                     boolean            pulseHighlight);

  MessageRecord getMessageRecord();

  void setEventListener(@Nullable EventListener listener);

  interface EventListener {
    void onQuoteClicked(MmsMessageRecord messageRecord);
  }
}
