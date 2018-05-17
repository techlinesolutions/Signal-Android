/** 
 * Copyright (C) 2011 Whisper Systems
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.techlinemobile.securesms.mms;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.techlinemobile.securesms.R;
import com.techlinemobile.securesms.attachments.Attachment;
import com.techlinemobile.securesms.attachments.UriAttachment;
import com.techlinemobile.securesms.database.AttachmentDatabase;
import com.techlinemobile.securesms.util.MediaUtil;
import com.techlinemobile.securesms.util.ResUtil;


public class AudioSlide extends Slide {

  public AudioSlide(Context context, Uri uri, long dataSize, boolean voiceNote) {
    super(context, constructAttachmentFromUri(context, uri, MediaUtil.AUDIO_UNSPECIFIED, dataSize, 0, 0, false, null, voiceNote, false));
  }

  public AudioSlide(Context context, Uri uri, long dataSize, String contentType, boolean voiceNote) {
    super(context,  new UriAttachment(uri, null, contentType, AttachmentDatabase.TRANSFER_PROGRESS_STARTED, dataSize, 0, 0, null, null, voiceNote, false));
  }

  public AudioSlide(Context context, Attachment attachment) {
    super(context, attachment);
  }

  @Override
  @Nullable
  public Uri getThumbnailUri() {
    return null;
  }

  @Override
  public boolean hasPlaceholder() {
    return true;
  }

  @Override
  public boolean hasImage() {
    return true;
  }

  @Override
  public boolean hasAudio() {
    return true;
  }

  @NonNull
  @Override
  public String getContentDescription() {
    return context.getString(R.string.Slide_audio);
  }

  @Override
  public @DrawableRes int getPlaceholderRes(Theme theme) {
    return ResUtil.getDrawableRes(theme, R.attr.conversation_icon_attach_audio);
  }
}
