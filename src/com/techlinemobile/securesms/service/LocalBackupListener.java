package com.techlinemobile.securesms.service;


import android.content.Context;
import android.content.Intent;

import com.techlinemobile.securesms.ApplicationContext;
import com.techlinemobile.securesms.jobs.LocalBackupJob;
import com.techlinemobile.securesms.util.TextSecurePreferences;

import java.util.concurrent.TimeUnit;

public class LocalBackupListener extends PersistentAlarmManagerListener {

  private static final long INTERVAL = TimeUnit.DAYS.toMillis(1);

  @Override
  protected long getNextScheduledExecutionTime(Context context) {
    return TextSecurePreferences.getNextBackupTime(context);
  }

  @Override
  protected long onAlarm(Context context, long scheduledTime) {
    if (TextSecurePreferences.isBackupEnabled(context)) {
      ApplicationContext.getInstance(context).getJobManager().add(new LocalBackupJob(context));
    }

    long nextTime = System.currentTimeMillis() + INTERVAL;
    TextSecurePreferences.setNextBackupTime(context, nextTime);

    return nextTime;
  }

  public static void schedule(Context context) {
    if (TextSecurePreferences.isBackupEnabled(context)) {
      new LocalBackupListener().onReceive(context, new Intent());
    }
  }
}
