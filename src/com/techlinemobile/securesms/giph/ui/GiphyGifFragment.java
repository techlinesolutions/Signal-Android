package com.techlinemobile.securesms.giph.ui;


import android.os.Bundle;
import android.support.v4.content.Loader;

import com.techlinemobile.securesms.giph.model.GiphyImage;
import com.techlinemobile.securesms.giph.net.GiphyGifLoader;

import java.util.List;

public class GiphyGifFragment extends GiphyFragment {

  @Override
  public Loader<List<GiphyImage>> onCreateLoader(int id, Bundle args) {
    return new GiphyGifLoader(getActivity(), searchString);
  }

}
