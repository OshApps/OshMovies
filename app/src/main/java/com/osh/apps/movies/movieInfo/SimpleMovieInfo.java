package com.osh.apps.movies.movieInfo;

import com.osh.apps.movies.app.AppData;

/**
 * Created by oshri-n on 16/05/2016.
 */
public abstract class SimpleMovieInfo
{
private String imdbId,title,posterURL,year;
private float rating;
private long id;


    public SimpleMovieInfo(String imdbId, String title, String year, String posterURL, float rating)
    {
    this(AppData.NULL_DATA, imdbId, title, year, posterURL, rating);
    }


    public SimpleMovieInfo(long id, String imdbId, String title, String year, String posterURL, float rating)
    {
    this.id=id;
    this.imdbId=imdbId;
    this.title=title;
    this.year=year;
    this.posterURL=posterURL;
    this.rating=rating;
    }


    public String getImdbId()
    {
    return imdbId;
    }


    public String getTitle()
    {
    return title;
    }


    public String getYear()
    {
    return year;
    }


    public String getPosterURL()
    {
    return posterURL;
    }


    public float getRating()
    {
    return rating;
    }


    public long getId()
    {
    return id;
    }


    public void setId(long id)
    {
    this.id = id;
    }


    public void setTitle(String title)
    {
    this.title = title;
    }


    public void setPosterURL(String posterURL)
    {
    this.posterURL = posterURL;
    }


    public void setYear(String year)
    {
    this.year = year;
    }


    public void setRating(float rating)
    {
    this.rating = rating;
    }


    public boolean hasDatabaseID()
    {
    return id!=AppData.NULL_DATA;
    }
}
