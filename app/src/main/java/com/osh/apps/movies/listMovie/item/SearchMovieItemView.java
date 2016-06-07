package com.osh.apps.movies.listMovie.item;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.osh.apps.movies.R;
import com.osh.apps.movies.movieInfo.MovieInfoBinder;

/**
 * Created by oshri-n on 20/05/2016.
 */
public class SearchMovieItemView extends SimpleMovieItemView
{
private ImageView exist;


    public SearchMovieItemView(Context context, AttributeSet attrs)
	{
	super(context, attrs);

    exist=null;
    }


    @Override
    protected void onFinishInflate()
    {
    super.onFinishInflate();

    exist= (ImageView) findViewById(R.id.iv_ic_exist);
    }


    @Override
    public void setMovieInfo(MovieInfoBinder movieInfoBinder)
    {
    super.setMovieInfo(movieInfoBinder);

    if(movieInfoBinder.hasDatabaseID())
        {
        exist.setVisibility(VISIBLE);
        }else
            {
            exist.setVisibility(GONE);
            }
    }
}
