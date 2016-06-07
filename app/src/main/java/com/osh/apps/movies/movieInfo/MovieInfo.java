package com.osh.apps.movies.movieInfo;

import com.osh.apps.movies.app.AppData;

/**
 * Created by oshri-n on 17/05/2016.
 */
public class MovieInfo extends SimpleMovieInfo
{
private String genre,language,plot;


    public MovieInfo(String imdbId, String title, String genre, String language, String plot, String year, String posterURL, float rating)
    {
    this(AppData.NULL_DATA, imdbId, title, genre, language, plot, year, posterURL, rating);
    }


    public MovieInfo(long id, String imdbId, String title, String genre, String language, String plot, String year, String posterURL, float rating)
    {
    super(id,imdbId, title, year, posterURL, rating);

    this.genre=genre;
    this.language=language;
    this.plot=plot;
    }


    public String getGenre()
    {
    return genre;
    }


    public String getLanguage()
    {
    return language;
    }


    public String getPlot()
    {
    return plot;
    }


    public void setGenre(String genre)
    {
    this.genre = genre;
    }


    public void setLanguage(String language)
    {
    this.language = language;
    }


    public void setPlot(String plot)
    {
    this.plot = plot;
    }
}
