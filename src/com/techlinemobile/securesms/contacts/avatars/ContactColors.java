package com.techlinemobile.securesms.contacts.avatars;

import android.support.annotation.NonNull;

import com.techlinemobile.securesms.color.MaterialColor;
import com.techlinemobile.securesms.color.MaterialColors;

public class ContactColors {

  public static final MaterialColor UNKNOWN_COLOR = MaterialColor.GREY;

  public static MaterialColor generateFor(@NonNull String name) {
    return MaterialColors.CONVERSATION_PALETTE.get(Math.abs(name.hashCode()) % MaterialColors.CONVERSATION_PALETTE.size());
  }

}
