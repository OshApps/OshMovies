package com.osh.apps.movies.appDB.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.osh.apps.movies.app.AppData;


public abstract class Database extends SQLiteOpenHelper
{

    public Database(Context context)
    {
    super(context, AppData.Database.DATABASE_NAME, null, AppData.Database.DATABASE_VERSION);
    }


	@Override
	public void onCreate(SQLiteDatabase db)
	{
    db.execSQL("CREATE TABLE "+AppData.Database.Movies.TABLE_NAME+"("+AppData.Database.Movies.getColName(AppData.Database.Movies.COL_MOVIE_ID)+" INTEGER PRIMARY KEY, "+
                                                                      AppData.Database.Movies.getColName(AppData.Database.Movies.COL_IMDB_ID)+" TEXT," +
                                                                      AppData.Database.Movies.getColName(AppData.Database.Movies.COL_TITLE)+" TEXT," +
                                                                      AppData.Database.Movies.getColName(AppData.Database.Movies.COL_GENRE)+" TEXT," +
                                                                      AppData.Database.Movies.getColName(AppData.Database.Movies.COL_LANGUAGE)+" TEXT," +
                                                                      AppData.Database.Movies.getColName(AppData.Database.Movies.COL_PLOT)+" TEXT," +
                                                                      AppData.Database.Movies.getColName(AppData.Database.Movies.COL_YEAR)+" TEXT," +
                                                                      AppData.Database.Movies.getColName(AppData.Database.Movies.COL_POSTER)+" TEXT," +
                                                                      AppData.Database.Movies.getColName(AppData.Database.Movies.COL_RATING)+" FLOAT)" +
                                                                      ";");
	}



	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{

	}


    protected long insert(String tableName, ContentValues values) throws SQLException
    {
    long id;
    SQLiteDatabase db=getWritableDatabase();

    try {

		id=db.insertOrThrow(tableName, null, values);

	    }catch(SQLException e)
		    {
		    throw new SQLException("Insert failed - "+e.getMessage());
		    }

    finally
		{
	    db.close();
		}

    return id;
    }


    protected boolean updateDatabase(String sql)
    {
    SQLiteDatabase db=getWritableDatabase();
    boolean flag = true;

    try {

        db.execSQL(sql);

        } catch (Exception e)
            {
            Log.e("Database", "error in Database-updateDatabase sql=" + sql + " :" + e.getMessage());
            flag = false;
            }
    finally
		{
	    db.close();
		}

    return flag;
    }


    protected Cursor getCursor(String sql)
    {
    SQLiteDatabase db=getReadableDatabase();
    Cursor cursor=null;

    try {
        cursor =  db.rawQuery(sql,null);

        if (!cursor.moveToFirst())
            {
            cursor.close();
            cursor=null;
            }

        }catch (Exception e)
            {
            Log.e("Database", "error in Database-getCursor sql=" + sql + " :" + e.getMessage());

            if(cursor!=null)
                {
                cursor.close();
                }
            }

    return cursor;
    }


    protected boolean isExist(String sql)
    {
    boolean isExist=false;
    Cursor cursor=getCursor(sql);

    if(cursor!=null)
        {
        isExist=true;
        cursor.close();
        }


    return isExist;
	}

}
