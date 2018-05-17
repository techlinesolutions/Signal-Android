package com.techlinemobile.securesms.database.loaders;

import android.content.Context;
import android.database.Cursor;

import com.techlinemobile.securesms.database.DatabaseFactory;
import com.techlinemobile.securesms.util.AbstractCursorLoader;

public class BlockedContactsLoader extends AbstractCursorLoader {

  public BlockedContactsLoader(Context context) {
    super(context);
  }

  @Override
  public Cursor getCursor() {
    return DatabaseFactory.getRecipientDatabase(getContext())
                          .getBlocked();
  }

}
