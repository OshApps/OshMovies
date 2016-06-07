package com.osh.apps.movies.poster;

import android.graphics.Bitmap;

/**
 * Created by oshri-n on 21/05/2016.
 */
public class PosterRequest implements OnLoadListener
{
private String url;
private int width,height;
private OnLoadListener loadListener;
private boolean isCancelled,isRequested,isDone;


    public PosterRequest(int width, int height, OnLoadListener loadListener)
    {
    this.width=width;
    this.height = height;
    this.loadListener = loadListener;

    url = null;

    isCancelled=false;
    isRequested=false;
    isDone=false;
    }


    public String getUrl()
    {
    return url;
    }


    public int getWidth()
    {
    return width;
    }


    public int getHeight()
    {
    return height;
    }


    public boolean isCancelled()
    {
    return isCancelled;
    }


    public void setHeight(int height)
    {
    this.height = height;
    }


    public void cancel()
    {
    isCancelled=true;
    }


    public void onCancelled()
    {
    isRequested=false;

    if(!isCancelled)
        {
        post(url);
        }
    }


    public synchronized void post(String posterURL)
    {
    url=posterURL;



    if(url!=null && !url.isEmpty())
        {

        if(!isRequested )
            {
            PosterManager.addRequest(this);
            }

        }else
            {
            preLoad();
            onLoad(null);
            }
    }


    public boolean isRequested()
    {
    return isRequested;
    }


    public boolean isDone()
    {
    return isDone;
    }


    @Override
    public void preLoad()
    {
    loadListener.preLoad();

    isCancelled=false;
    isRequested=true;
    isDone=false;
    }


    @Override
    public void onLoad(Bitmap bitmap)
    {
    if(!isCancelled)
        {
        isDone=true;
        loadListener.onLoad(bitmap);
        isRequested=false;
        }else
            {
            onCancelled();

            if(bitmap!=null)
                {
                bitmap.recycle();
                }
            }
    }

}
