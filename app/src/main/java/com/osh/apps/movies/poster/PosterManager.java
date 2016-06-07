package com.osh.apps.movies.poster;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import com.osh.apps.movies.network.connection.HttpConnection;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by oshri-n on 21/05/2016.
 */
public class PosterManager
{
private static ArrayList<PosterRequest> requests;
private static boolean isRunning;
private static Handler handler;
private static Runnable action;


    private static void init()
    {
    requests=new ArrayList<>();
    isRunning=false;
    handler=new Handler();

    action=new Runnable()
        {
            @Override
            public void run()
            {
            onBackground();
            }
        };
    }


    static synchronized void addRequest(PosterRequest request)
    {

    if(requests==null)
        {
        init();
        }

    request.preLoad();
    requests.add(request);

    if(!isRunning)
        {
        isRunning=true;
        new Thread(action, "load posters task").start();
        }
    }


    private static void onBackground()
    {
    PosterRequest request;
    Bitmap bitmap;
    String url;

    while(!requests.isEmpty())
        {
        request=requests.remove(0);

        if(!request.isCancelled())
            {
            url=request.getUrl();
            bitmap=null;

            if(url!=null)
                {
                if(url.startsWith("http"))
                    {
                    bitmap=getBitmapFromWeb(url, request.getWidth(), request.getHeight());
                    }else
                        {
                        bitmap=getBitmapFromStorage(url, request.getWidth(), request.getHeight());
                        }
                }

            postResult(request, bitmap);
            }else
                {
                request.onCancelled();
                }
        }

    isRunning=false;
    }


    private static void postResult(final PosterRequest request, final Bitmap bitmap)
    {

    if(!request.isCancelled())
        {
        Log.d("PosterManager","post poster");
        handler.post(new Runnable()
            {

                @Override
                public void run()
                {
                request.onLoad(bitmap);
                }
            });
        }else
            {
            request.onCancelled();
            }
    }


    private static Bitmap getBitmapFromStorage(String url, int width, int height)
    {
    BitmapFactory.Options options;
    Bitmap bitmap;

    options= createBitmapOptions();

    BitmapFactory.decodeFile(url, options);

    scaleBitmap(options, width, height);

    bitmap=BitmapFactory.decodeFile(url, options);

    if(bitmap!=null)
        {
        bitmap=Bitmap.createScaledBitmap(bitmap,width,height,true);
        }

    return bitmap;
    }


    private static Bitmap getBitmapFromWeb(String url, int width, int height)
    {
    BitmapFactory.Options bitmapOptions;
    InputStream inputStream = null;
    Bitmap bitmap = null;

    try {
        inputStream= HttpConnection.getInputStream(url);

        bitmapOptions=getBitmapOptions(inputStream, width ,height);

        inputStream=HttpConnection.getInputStream(url);

        bitmap= BitmapFactory.decodeStream(inputStream, null ,bitmapOptions);

        if(bitmap!=null)
            {
            bitmap=Bitmap.createScaledBitmap(bitmap,width,height,true);
            }

        } catch (Exception e)
            {
            Log.e("PosterManager",""+e.getMessage());
            }

    finally
        {
        if(inputStream!=null)
            {
            try {
                inputStream.close();
                } catch(Exception e) {}
            }
        }

    return bitmap;
    }


    private static BitmapFactory.Options getBitmapOptions(InputStream inputStream, int width, int height) throws IOException
    {
    BitmapFactory.Options options= createBitmapOptions();

    BitmapFactory.decodeStream(inputStream, null ,options);

    scaleBitmap(options, width, height);

    inputStream.close();

    return options;
    }


    private static BitmapFactory.Options createBitmapOptions()
    {
    BitmapFactory.Options options= new BitmapFactory.Options();

    options.inJustDecodeBounds = true;

    return options;
    }


    private static void scaleBitmap(BitmapFactory.Options options, int width, int height)
    {
    options.inJustDecodeBounds = false;

    options.inSampleSize = Math.min(options.outWidth / width, options.outHeight / height);
    }

}
