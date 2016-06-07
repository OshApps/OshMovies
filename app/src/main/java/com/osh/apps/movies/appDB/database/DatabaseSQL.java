package com.osh.apps.movies.appDB.database;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.osh.apps.movies.app.AppData;


public abstract class DatabaseSQL extends Database
{
private final String AND=" AND ";
private final String OR=" OR ";

    public DatabaseSQL(Context context)
	{
    super(context);
	}


	protected String addColStatement(int col ,@Nullable String value, boolean isColStringType)
	{
	String colStatement;
	
	if(value !=null)
		{

		if (isColStringType)
			{
			value="'"+value+"'";
			}

		}else
			{
			value="NULL";
			}
	
	colStatement= AppData.Database.Movies.getColName(col)+"="+value;
	
	return colStatement;
	}
	
	
	protected boolean updateFromTable(@NonNull String tableName , @NonNull String updateCols, @Nullable String whereCols)
	{
	String sql;
	
	sql = "UPDATE "+tableName+" SET "+updateCols;

	if(whereCols!=null)
		{
		sql+=" WHERE "+whereCols;
		}

	return updateDatabase(sql);
	}

	
	protected boolean isExist(@NonNull String tableName, @Nullable String whereCols)
	{
	String sql = "SELECT * FROM "+tableName;

	if(whereCols!=null)
		{
		sql+=" WHERE "+whereCols;
		}
	
	return isExist(sql);
	}


	protected Cursor selectFromTable(@NonNull String tableName , @Nullable String selectCols, @Nullable String whereCols)
	{
	String sql = "SELECT ";

	if(selectCols!=null)
		{
		sql+=selectCols;
		}else
			{
			sql+="*";
			}

	sql+= " FROM "+tableName;

	if(whereCols!=null)
		{
		sql+=" WHERE "+whereCols;
		}

	return getCursor(sql);
	}


	public boolean deleteFromTable(@NonNull String tableName, @Nullable String whereCols)
	{
	String sql = "DELETE FROM "+tableName;

	if(whereCols!=null)
		{
		sql+=" WHERE "+whereCols;
		}
	
	return updateDatabase(sql);
	}
}
