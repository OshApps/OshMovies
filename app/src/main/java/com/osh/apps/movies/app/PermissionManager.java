package com.osh.apps.movies.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


public class PermissionManager
{



    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(Activity activity, String permission, int requestCode )
    {
    boolean hasPermission=true;

    if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
        {
        if(ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_DENIED)
            {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
            hasPermission=false;
            }
        }

    return hasPermission;
    }
}
