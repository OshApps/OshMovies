package com.osh.apps.movies.movieInfo;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.osh.apps.movies.app.AppData;
import com.osh.apps.movies.poster.OnLoadListener;
import com.osh.apps.movies.poster.PosterRequest;

/**
 * Created by oshri-n on 22/05/2016.
 */
public class MovieInfoBinder extends SimpleMovieInfo implements OnLoadListener
{
private PosterRequest posterRequest;
private OnLoadListener loadListener;
private Bitmap poster;


    public MovieInfoBinder(String imdbId, String title, String year, String posterURL, float rating)
    {
    super(imdbId, title, year, posterURL, rating);

    init();
    }


    public MovieInfoBinder(long id, String imdbId, String title, String year, String posterURL, float rating)
    {
    super(id, imdbId, title, year, posterURL, rating);

    init();
    }


    private void init()
    {
    loadListener=null;
    poster=null;

    posterRequest=new PosterRequest(AppData.ITEM_POSTER_WIDTH, AppData.ITEM_POSTER_HEIGHT, this);
    }


    public void setOnLoadListener(@NonNull OnLoadListener loadListener)
    {
    this.loadListener = loadListener;

    if(posterRequest.isDone())
        {
        loadListener.preLoad();//TODO remove if no need
        loadListener.onLoad(poster);
        }else
            {
            posterRequest.post(getPosterURL());
            }
    }


    @Override
    public void preLoad()
    {
    loadListener.preLoad();
    }


    @Override
    public void onLoad(Bitmap bitmap)
    {
    loadListener.onLoad(bitmap);

    poster=bitmap;
    }
}
