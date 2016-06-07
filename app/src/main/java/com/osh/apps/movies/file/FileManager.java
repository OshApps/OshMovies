package com.osh.apps.movies.file;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by oshri-n on 24/05/2016.
 */
public class FileManager
{
private static final String POSTERS_DIR="posters";
private static final String TEMP_DIR="temp";
private static final String FILE_SUFFIX =".png";

private static FileManager instance;

private File posterFolder,tempFolder;


    public synchronized static FileManager getInstance(Context context)
    {

    if(instance==null)
        {
        instance=new FileManager(context);
        }

    return instance;
    }


    private FileManager(Context context)
    {
    posterFolder=context.getDir(POSTERS_DIR, Context.MODE_PRIVATE);

    tempFolder=new File(context.getCacheDir(),TEMP_DIR);

    if(!tempFolder.exists())
        {
        tempFolder.mkdir();
        }
    }


    public String createPosterFile(@NonNull Bitmap poster, @NonNull String posterName)
    {
    FileOutputStream fos = null;
    String posterUrl=null;
    boolean isFileCreated;
    File posterFile;

    posterName=posterName.trim().replaceAll("\\s","_") + FILE_SUFFIX;

    try {
        posterFile=new File(posterFolder, posterName);

        fos = new FileOutputStream(posterFile);

        isFileCreated=poster.compress(Bitmap.CompressFormat.PNG, 100, fos);

        if(isFileCreated)
            {
            posterUrl=posterFile.getAbsolutePath();
            }

        } catch (Exception e)
                {
                Log.d("FileManager","ERROR: Failed to create poster file - "+ e.getMessage());
                e.printStackTrace();
                }

    finally
        {
        if(fos!=null)
            {
            try {
                fos.close();
                }catch(IOException e){}
            }
        }

    return posterUrl;
    }


    public void deletePosterFile(String path)
    {
    File posterFile;

    if(path!=null)
        {
        posterFile=new File(path);
        posterFile.delete();
        }
    }


    public void deleteAllPosters()
    {
    deleteAllFilesFromFolder(posterFolder);
    }


    public File createTempPoster()
    {
    String time,imageFileName;
    File storageDir,image;

    time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    imageFileName = "TEMP_" + time + FILE_SUFFIX;

    storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

    image = null;

    try {
        image = new File(storageDir, imageFileName);
        } catch(Exception e)
            {
            e.printStackTrace();
            }

    return image;
    }


    public String moveFileToTempFolder(String path)
    {
    File tempFile,externalFile;

    externalFile=new File(path);

    tempFile=new File(tempFolder, externalFile.getName());

    try {
        transferFile(externalFile, tempFile);
        }catch(Exception e)
            {
            e.printStackTrace();
            }

    externalFile.delete();

    return tempFile.getAbsolutePath();
    }


    private void transferFile(File src, File dst) throws Exception
    {
    FileChannel inChannel=null,outChannel=null;

    try {
        inChannel = new FileInputStream(src).getChannel();
        outChannel = new FileOutputStream(dst).getChannel();

        inChannel.transferTo(0, inChannel.size(), outChannel);
        }

    finally
        {
        if (inChannel != null)
            {
            inChannel.close();
            }

        if (outChannel != null)
            {
            outChannel.close();
            }
        }
    }


    public void clearTempFiles()
    {
    deleteAllFilesFromFolder(tempFolder);
    }


    private void deleteAllFilesFromFolder(File folder)
    {
    File[] posters=folder.listFiles();

    for(int i = 0; i < posters.length; i++)
        {
        posters[i].delete();
        }
    }


}
