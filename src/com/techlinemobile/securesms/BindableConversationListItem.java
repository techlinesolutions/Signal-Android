package com.techlinemobile.securesms;

import android.support.annotation.NonNull;

import com.techlinemobile.securesms.crypto.MasterSecret;
import com.techlinemobile.securesms.database.model.ThreadRecord;
import com.techlinemobile.securesms.mms.GlideRequests;

import java.util.Locale;
import java.util.Set;

public interface BindableConversationListItem extends Unbindable {

  public void bind(@NonNull ThreadRecord thread,
                   @NonNull GlideRequests glideRequests, @NonNull Locale locale,
                   @NonNull Set<Long> selectedThreads, boolean batchMode);
}
