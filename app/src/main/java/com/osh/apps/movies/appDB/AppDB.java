package com.osh.apps.movies.appDB;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.osh.apps.movies.app.AppData;
import com.osh.apps.movies.file.FileManager;
import com.osh.apps.movies.movieInfo.MovieInfo;
import com.osh.apps.movies.movieInfo.SimpleMovieInfo;


/**
 * Created by oshri-n on 25/05/2016.
 */
public class AppDB extends DatabaseManager
{
private static AppDB instance;

private FileManager fileManager;


    public synchronized static AppDB getInstance(Context context)
    {

    if(instance==null)
        {
        instance=new AppDB(context);
        }

    return instance;
    }


    private AppDB(Context context)
    {
    super(context);

    fileManager=FileManager.getInstance(context);
    }


    public void insertMovie(@NonNull MovieInfo movieInfo, @Nullable Bitmap poster)
    {
    insertMovie(movieInfo);

    updatePosterUrl(movieInfo, poster, null);

    updateFromTable(AppData.Database.Movies.TABLE_NAME, addColStatement(AppData.Database.Movies.COL_POSTER, movieInfo.getPosterURL() ,true), addColStatement(AppData.Database.Movies.COL_MOVIE_ID, String.valueOf(movieInfo.getId()) ,false) );
    }


    public boolean updateMovie(@NonNull MovieInfo movieInfo, @Nullable Bitmap poster,@Nullable String lastPosterUrl)
    {
    updatePosterUrl(movieInfo, poster, lastPosterUrl);

    return updateMovie(movieInfo);
    }


    private void updatePosterUrl(@NonNull MovieInfo movieInfo,@Nullable Bitmap poster,@Nullable String lastPosterUrl)
    {
    String posterUrl=null;

    if(poster!=null)
        {
        posterUrl=fileManager.createPosterFile(poster , movieInfo.getTitle()+" "+movieInfo.getId());
        }else
            {
            fileManager.deletePosterFile(lastPosterUrl);
            }

    movieInfo.setPosterURL(posterUrl);
    }


    public void removeMovie(@NonNull SimpleMovieInfo simpleMovieInfo)
    {
    fileManager.deletePosterFile(simpleMovieInfo.getPosterURL());

    removeMovie(simpleMovieInfo.getId());
    }


    @Override
    public void removeAllMovies()
    {
    fileManager.deleteAllPosters();

    super.removeAllMovies();
    }

}
