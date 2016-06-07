package com.osh.apps.movies.appDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.osh.apps.movies.app.AppData;
import com.osh.apps.movies.appDB.database.DatabaseSQL;
import com.osh.apps.movies.movieInfo.MovieInfo;
import com.osh.apps.movies.movieInfo.MovieInfoBinder;

import java.util.ArrayList;


public class DatabaseManager extends DatabaseSQL
{

    protected DatabaseManager(Context context)
    {
    super(context);
    }

	
	public MovieInfo getMovieInfo(long movieID)
	{
	MovieInfo movieInfo=null;
    Cursor cursor;

    cursor=selectFromTable(AppData.Database.Movies.TABLE_NAME, null, addColStatement(AppData.Database.Movies.COL_MOVIE_ID, String.valueOf(movieID),false) );

	if(cursor!=null)
        {
        movieInfo=  new MovieInfo(cursor.getLong(cursor.getColumnIndex(AppData.Database.Movies.getColName(AppData.Database.Movies.COL_MOVIE_ID))),
                                  cursor.getString(cursor.getColumnIndex(AppData.Database.Movies.getColName(AppData.Database.Movies.COL_IMDB_ID))),
                                  cursor.getString(cursor.getColumnIndex(AppData.Database.Movies.getColName(AppData.Database.Movies.COL_TITLE))),
                                  cursor.getString(cursor.getColumnIndex(AppData.Database.Movies.getColName(AppData.Database.Movies.COL_GENRE))),
                                  cursor.getString(cursor.getColumnIndex(AppData.Database.Movies.getColName(AppData.Database.Movies.COL_LANGUAGE))),
                                  cursor.getString(cursor.getColumnIndex(AppData.Database.Movies.getColName(AppData.Database.Movies.COL_PLOT))),
                                  cursor.getString(cursor.getColumnIndex(AppData.Database.Movies.getColName(AppData.Database.Movies.COL_YEAR))),
                                  cursor.getString(cursor.getColumnIndex(AppData.Database.Movies.getColName(AppData.Database.Movies.COL_POSTER))),
                                  cursor.getFloat(cursor.getColumnIndex(AppData.Database.Movies.getColName(AppData.Database.Movies.COL_RATING)))
                                  );
        cursor.close();
        }

	return movieInfo;
	}


    protected void insertMovie(@NonNull MovieInfo movieInfo)
    {
    ContentValues values;
    boolean isExist;
    long movieId;

    isExist=isMovieExist(movieInfo.getImdbId());

    try {

        if(!isExist)
            {
            values=new ContentValues();
            values.put(AppData.Database.Movies.getColName(AppData.Database.Movies.COL_IMDB_ID), movieInfo.getImdbId());
            values.put(AppData.Database.Movies.getColName(AppData.Database.Movies.COL_TITLE), movieInfo.getTitle());
            values.put(AppData.Database.Movies.getColName(AppData.Database.Movies.COL_GENRE), movieInfo.getGenre());
            values.put(AppData.Database.Movies.getColName(AppData.Database.Movies.COL_LANGUAGE), movieInfo.getLanguage());
            values.put(AppData.Database.Movies.getColName(AppData.Database.Movies.COL_PLOT), movieInfo.getPlot());
            values.put(AppData.Database.Movies.getColName(AppData.Database.Movies.COL_YEAR), movieInfo.getYear());
            values.put(AppData.Database.Movies.getColName(AppData.Database.Movies.COL_POSTER), movieInfo.getPosterURL());
            values.put(AppData.Database.Movies.getColName(AppData.Database.Movies.COL_RATING), movieInfo.getRating());

            movieId=insert(AppData.Database.Movies.TABLE_NAME, values);
            movieInfo.setId(movieId);
            }else
                {
                throw new SQLException("The movie already exists");
                }

        }catch (SQLException e)
            {
            Log.e("DatabaseManager","Error: Failed to insert MovieInfo to database - "+e.getMessage());
            }
    }


    public boolean isMovieExist(@NonNull String imdbId)
    {
    return isExist(AppData.Database.Movies.TABLE_NAME, addColStatement(AppData.Database.Movies.COL_IMDB_ID, imdbId, true));
    }


    public long getMovieID(@NonNull String imdbId)
    {
    String selectCols,whereCols;
    long id=AppData.NULL_DATA;
    Cursor cursor;

    selectCols=AppData.Database.Movies.getColName(AppData.Database.Movies.COL_MOVIE_ID);

    whereCols=addColStatement(AppData.Database.Movies.COL_IMDB_ID, String.valueOf(imdbId),true);

    cursor=selectFromTable(AppData.Database.Movies.TABLE_NAME, selectCols, whereCols);

	if(cursor!=null)
        {
        id=cursor.getLong(cursor.getColumnIndex(AppData.Database.Movies.getColName(AppData.Database.Movies.COL_MOVIE_ID)));
        cursor.close();
        }

    return id;
    }


    protected void removeMovie(long movieID)
    {
    deleteFromTable(AppData.Database.Movies.TABLE_NAME, addColStatement(AppData.Database.Movies.COL_MOVIE_ID, String.valueOf(movieID), false));
    }


	public @Nullable ArrayList<MovieInfoBinder> getListMovies()
	{
    ArrayList<MovieInfoBinder> movies=null;
    MovieInfoBinder movieInfoBinder;
    Cursor cursor;

    cursor=selectFromTable(AppData.Database.Movies.TABLE_NAME, null, null);

	if(cursor!=null)
        {
        movies=new ArrayList<>();

        do{
          movieInfoBinder=new MovieInfoBinder(cursor.getLong(cursor.getColumnIndex(AppData.Database.Movies.getColName(AppData.Database.Movies.COL_MOVIE_ID))),
                                   cursor.getString(cursor.getColumnIndex(AppData.Database.Movies.getColName(AppData.Database.Movies.COL_IMDB_ID))),
                                   cursor.getString(cursor.getColumnIndex(AppData.Database.Movies.getColName(AppData.Database.Movies.COL_TITLE))),
                                   cursor.getString(cursor.getColumnIndex(AppData.Database.Movies.getColName(AppData.Database.Movies.COL_YEAR))),
                                   cursor.getString(cursor.getColumnIndex(AppData.Database.Movies.getColName(AppData.Database.Movies.COL_POSTER))),
                                   cursor.getFloat(cursor.getColumnIndex(AppData.Database.Movies.getColName(AppData.Database.Movies.COL_RATING)))
                                   );

          movies.add(movieInfoBinder);

          }while(cursor.moveToNext());

        cursor.close();
        }

	return movies;
	}


    protected boolean updateMovie(MovieInfo movieInfo)
    {
    String updateCols="",whereCols;

    updateCols+=addColStatement(AppData.Database.Movies.COL_TITLE, movieInfo.getTitle() ,true)+",";
    updateCols+=addColStatement(AppData.Database.Movies.COL_GENRE, movieInfo.getGenre() ,true)+",";
    updateCols+=addColStatement(AppData.Database.Movies.COL_LANGUAGE, movieInfo.getLanguage() ,true)+",";
    updateCols+=addColStatement(AppData.Database.Movies.COL_PLOT, movieInfo.getPlot() ,true)+",";
    updateCols+=addColStatement(AppData.Database.Movies.COL_YEAR, movieInfo.getYear() ,true)+",";
    updateCols+=addColStatement(AppData.Database.Movies.COL_POSTER, movieInfo.getPosterURL() ,true)+",";
    updateCols+=addColStatement(AppData.Database.Movies.COL_RATING, String.valueOf(movieInfo.getRating()),false);

    whereCols=addColStatement(AppData.Database.Movies.COL_MOVIE_ID, String.valueOf(movieInfo.getId()) ,false);

    return updateFromTable(AppData.Database.Movies.TABLE_NAME, updateCols, whereCols );
    }


    protected void removeAllMovies()
    {
    deleteFromTable(AppData.Database.Movies.TABLE_NAME, null);
    }

}