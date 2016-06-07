package com.osh.apps.movies.network.connection;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by oshri-n on 24/05/2016.
 */
public class JsonConnection
{
private static final int JSON_CONNECTION_TIMEOUT = 10000;

    public static String getJsonResult(String webURL) throws Exception
    {
    BufferedReader reader=null;
    String line,result=null;

    try {
        reader = new BufferedReader(new InputStreamReader(HttpConnection.getInputStream(webURL, JSON_CONNECTION_TIMEOUT)));

        result="";

        while((line = reader.readLine()) != null)
            {
            result += line;
            }

        }catch(Exception e)
            {
            throw new Exception("Failed get json from '"+webURL+"'");
            }

    finally
        {
        if(reader!=null)
            {
            reader.close();
            }
        }

    return result;
    }


}
