package com.osh.apps.movies.listMovie.item;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.osh.apps.movies.R;
import com.osh.apps.movies.movieInfo.MovieInfoBinder;
import com.osh.apps.movies.poster.PosterLayout;


/**
 * Created by oshri-n on 16/05/2016.
 */
public class SimpleMovieItemView extends RelativeLayout
{
private PosterLayout poster;
private TextView title,year;
private RatingBar rating;


    public SimpleMovieItemView(Context context, AttributeSet attrs)
	{
	super(context, attrs);

    poster=null;
    title=null;
    year=null;
    rating=null;
    }


    @Override
    protected void onFinishInflate()
    {
    super.onFinishInflate();

    poster= (PosterLayout) findViewById(R.id.poster_layout);

    title= (TextView) findViewById(R.id.tv_title);
    year= (TextView) findViewById(R.id.tv_year);
    rating= (RatingBar) findViewById(R.id.rb_rating);
    }


    public void setMovieInfo(MovieInfoBinder movieInfoBinder)
    {
    title.setText(movieInfoBinder.getTitle());
    year.setText(movieInfoBinder.getYear());

    rating.setRating(movieInfoBinder.getRating()/2);

    poster.setMovieInfo(movieInfoBinder);
    }

}
