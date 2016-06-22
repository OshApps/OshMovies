package com.osh.apps.movies.app;

import android.content.res.Resources;

import com.osh.apps.movies.R;

/**
 * Created by oshri-n on 16/05/2016.
 */
public final class AppData
{
public static final int NULL_DATA=-1;
public static int ITEM_POSTER_HEIGHT=NULL_DATA;
public static int ITEM_POSTER_WIDTH=NULL_DATA;

	private AppData(){}


    public static void setItemPosterHeight(Resources res)
    {
    ITEM_POSTER_WIDTH= res.getDimensionPixelSize(R.dimen.movie_item_poster_width);
    ITEM_POSTER_HEIGHT= res.getDimensionPixelSize(R.dimen.movie_item_poster_height);
    }


    public static final class IntentKey
	{
    public static final String IMDB_ID_KEY="imdbID";
    public static final String MOVIE_ID_KEY ="movieID" ;
    }


	public static final class OMDB
	{
    public static final String OMDB_REQUEST_URL="http://www.omdbapi.com/?";
    public static final String OMDB_FILTER_MOVIE_PARAMETER="&type=movie";
    public static final String OMDB_PAGE_PARAMETER="&page=";
    public static final String OMDB_SEARCH_PARAMETER="s=";
    public static final String OMDB_ID_PARAMETER="i=";

		public static String getSearchFormat(String value)
		{
		return "*"+value.trim().replaceAll("\\s","%20")+"*";
		}
	}


	public static final class Database
    {
    public static final String DATABASE_NAME="Osh-Movies.db";
    public static final int DATABASE_VERSION=1;

        public static final class Movies
        {
        public static final String TABLE_NAME="movies";

        private static final String [] NAME_COL={"movieID","imdbID","title","genre","language","plot","year","poster","rating"};

        public static final int COL_MOVIE_ID=0;
        public static final int COL_IMDB_ID=1;
        public static final int COL_TITLE=2;
        public static final int COL_GENRE=3;
        public static final int COL_LANGUAGE=4;
        public static final int COL_PLOT=5;
        public static final int COL_YEAR=6;
        public static final int COL_POSTER=7;
        public static final int COL_RATING=8;



            public static String getColName(int col)
            {
            return NAME_COL[col];
            }

        }
    }

}
