package com.osh.apps.movies.poster;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.osh.apps.movies.R;
import com.osh.apps.movies.movieInfo.MovieInfoBinder;

/**
 * Created by oshri-n on 21/05/2016.
 */
public class PosterLayout extends RelativeLayout implements OnLoadListener
{
private ImageView poster;
private ProgressBar loading;


    public PosterLayout(Context context, AttributeSet attrs)
    {
    super(context, attrs);

    poster=null;
    loading=null;
    }


    @Override
    protected void onFinishInflate()
    {
    super.onFinishInflate();

    poster= (ImageView) findViewById(R.id.iv_poster);
    loading= (ProgressBar) findViewById(R.id.pb_loading);
    }


    public void setMovieInfo(MovieInfoBinder movieInfoBinder)
    {
    movieInfoBinder.setOnLoadListener(this);
    }


    public void clearPoster()
    {
    poster.setImageBitmap(null);
    poster.setBackgroundColor(Color.WHITE);
    loading.setVisibility(VISIBLE);
    }


    @Override
    public void preLoad()
    {
    clearPoster();
    }


    @Override
    public void onLoad(Bitmap bitmap)
    {

    if(bitmap!=null)
        {
        poster.setImageBitmap(bitmap);
        }else
            {
            poster.setImageResource(R.drawable.noimage);
            }

    poster.setBackgroundColor(Color.TRANSPARENT);
    loading.setVisibility(GONE);
    }

}
