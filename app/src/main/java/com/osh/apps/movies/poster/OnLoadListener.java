package com.osh.apps.movies.poster;

import android.graphics.Bitmap;

/**
 * Created by oshri-n on 21/05/2016.
 */
public interface OnLoadListener
{
    void preLoad();
    void onLoad(Bitmap bitmap);
}
