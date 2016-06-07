package com.osh.apps.movies.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;

import com.osh.apps.movies.R;


/**
 * Created by oshri-n on 28/05/2016.
 */
public class AddPosterDialog extends Dialog implements View.OnClickListener
{
private OnDialogItemClickListener itemClickListener;


    public AddPosterDialog(@NonNull Context context, @NonNull OnDialogItemClickListener itemClickListener)
    {
    super(context);
    this.itemClickListener = itemClickListener;

    requestWindowFeature(Window.FEATURE_NO_TITLE);

    setContentView(R.layout.dialog_add_poster_layout);

    findViewById(R.id.ll_gallery_layout).setOnClickListener(this);
    findViewById(R.id.ll_camera_layout).setOnClickListener(this);
    }


    @Override
    public void onClick(View v)
    {
    switch(v.getId())
      {
      case R.id.ll_gallery_layout:
      itemClickListener.onGalleryClick();
      break;

      case R.id.ll_camera_layout:
      itemClickListener.onCameraClick();
      break;
      }

    dismiss();
    }


    public interface OnDialogItemClickListener
    {
        void onGalleryClick();
        void onCameraClick();
    }

}
