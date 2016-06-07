package com.osh.apps.movies.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.osh.apps.movies.R;
import com.osh.apps.movies.app.AppData;
import com.osh.apps.movies.app.PermissionManager;
import com.osh.apps.movies.appDB.AppDB;
import com.osh.apps.movies.dialog.AddPosterDialog;
import com.osh.apps.movies.file.FileManager;
import com.osh.apps.movies.movieInfo.MovieInfo;
import com.osh.apps.movies.poster.OnLoadListener;
import com.osh.apps.movies.poster.PosterLayout;
import com.osh.apps.movies.poster.PosterRequest;

import java.io.File;


public class EditMovieActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, OnLoadListener, View.OnClickListener, AddPosterDialog.OnDialogItemClickListener
{
private static final int ADD_POSTER_PERMISSIONS_REQUEST = 0;

private static final int GALLERY_REQUEST_CODE= 1;
private static final int CAMERA_REQUEST_CODE= 2;

private static final int WEB_POSITION =0 ;
private static final int LOCAL_POSITION =1 ;

private EditText title,year,genre,language,plot,posterURL;
private String lastPosterUrl,currentCameraPosterUrl;
private RelativeLayout webLayout,localLayout;
private AddPosterDialog addPosterDialog;
private ProgressDialog waitingDialog;
private Bitmap currentPosterBitmap;
private MovieInfo movieInfoFromDB;
private FileManager fileManager;
private PosterRequest request;
private ActionBar actionBar;
private PosterLayout poster;
private RatingBar rating;
private AppDB database;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_edit_movie);

    currentPosterBitmap =null;
    movieInfoFromDB=null;
    lastPosterUrl =null;
    currentCameraPosterUrl=null;

    database=AppDB.getInstance(this);
    fileManager=FileManager.getInstance(this);

    waitingDialog =new ProgressDialog(this);
    waitingDialog.setMessage("Please wait...");
    waitingDialog.setIndeterminate(true);
    waitingDialog.setCancelable(false);

    addPosterDialog= new AddPosterDialog(this,this);

    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    setViews();
    init();
    }


    private void setViews()
    {
    actionBar=getSupportActionBar();
    actionBar.setHomeButtonEnabled(true);
    actionBar.setDisplayHomeAsUpEnabled(true);

    title=(EditText) findViewById(R.id.et_title);
    year=(EditText) findViewById(R.id.et_year);
    genre=(EditText) findViewById(R.id.et_genre);
    language=(EditText) findViewById(R.id.et_language);
    plot=(EditText) findViewById(R.id.et_plot);
    posterURL=(EditText) findViewById(R.id.et_poster);

    rating=(RatingBar) findViewById(R.id.rb_rating);
    rating.setRating(0);

    ((Spinner) findViewById(R.id.s_options_add)).setOnItemSelectedListener(this);

    webLayout= (RelativeLayout) findViewById(R.id.rl_web_layout);
    localLayout= (RelativeLayout) findViewById(R.id.rl_local_layout);

    findViewById(R.id.b_show_poster).setOnClickListener(this);
    findViewById(R.id.b_last_poster).setOnClickListener(this);
    findViewById(R.id.b_add_poster).setOnClickListener(this);

    poster=(PosterLayout) findViewById(R.id.poster_layout);
    }


    private void init()
    {
    long movieID;
    int titleRes;

    request=new PosterRequest(getResources().getDimensionPixelSize(R.dimen.movie_info_poster_width), getResources().getDimensionPixelSize(R.dimen.movie_info_poster_height), this);

    //set movie info + action bar title
    movieID=getIntent().getLongExtra(AppData.IntentKey.MOVIE_ID_KEY, AppData.NULL_DATA);

    if(movieID != AppData.NULL_DATA)
        {
        movieInfoFromDB=database.getMovieInfo(movieID);
        setMovieInfo(movieInfoFromDB);
        titleRes=R.string.edit_title;
        }else
            {
            titleRes=R.string.create_title;
            request.post(null);
            }

    actionBar.setTitle(titleRes);
    }


    private void setMovieInfo(MovieInfo movieInfo)
    {

    if(movieInfo !=null)
        {
        title.setText(movieInfo.getTitle());
        year.setText(movieInfo.getYear());

        if(movieInfo.getImdbId() != null)
            {
            title.setEnabled(false);
            year.setEnabled(false);
            }

        genre.setText(movieInfo.getGenre());
        language.setText(movieInfo.getLanguage());
        plot.setText(movieInfo.getPlot());

        lastPosterUrl =movieInfo.getPosterURL();

        if(lastPosterUrl!=null && lastPosterUrl.startsWith("http"))
            {
            posterURL.setText(lastPosterUrl);
            }

        rating.setRating(movieInfo.getRating()/2);

        request.post(movieInfo.getPosterURL());
        }

    }


    @Override
    protected void onDestroy()
    {
    super.onDestroy();
    request.cancel();
    fileManager.clearTempFiles();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    MenuItem item;

    getMenuInflater().inflate(R.menu.menu_edit_movie_activity, menu);

    item=menu.findItem(R.id.m_add);

    item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
    item.setVisible(movieInfoFromDB == null);

    item=menu.findItem(R.id.m_save);

    item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
    item.setVisible(movieInfoFromDB != null);

	return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    final MovieInfo movieInfo;

    switch (item.getItemId())
        {
        case R.id.m_save:

        movieInfo=createMovie();

        if(movieInfo != null)
            {

            if(request.isDone())
                {
                saveMovie(movieInfo);
                }else
                    {
                    waitingDialog.setTitle("Save Movie");
                    waitingDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
                        {
                            @Override
                            public void onDismiss(DialogInterface dialog)
                            {
                            saveMovie(movieInfo);
                            }
                        });

                    waitingDialog.show();
                    }
            }

        break;

        case R.id.m_add:

        movieInfo=createMovie();

        if(movieInfo!=null)
            {

            if(request.isDone())
                {
                insertCurrentMovie(movieInfo);
                }else
                    {
                    waitingDialog.setTitle("Add Movie");
                    waitingDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
                        {
                            @Override
                            public void onDismiss(DialogInterface dialog)
                            {
                            insertCurrentMovie(movieInfo);
                            }
                        });

                    waitingDialog.show();
                    }
            }

        break;

        case android.R.id.home:

        finish();

        break;

        }

    return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    super.onActivityResult(requestCode, resultCode, data);

    if(resultCode == RESULT_OK )
        {

        if(requestCode==GALLERY_REQUEST_CODE && data!=null)
            {
            request.post( getRealPathFromURI(data.getData()) );
            }

        if(requestCode==CAMERA_REQUEST_CODE)
            {
            request.post(fileManager.moveFileToTempFolder(currentCameraPosterUrl));
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {

    switch(requestCode)
        {
        case ADD_POSTER_PERMISSIONS_REQUEST:

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
            addPosterDialog.show();
            }

        break;
        }

    }


    private String getRealPathFromURI(Uri data)
    {
    String[] filePathColumn = { MediaStore.Images.Media.DATA };
	String imagePath;
	Cursor cursor;

	cursor = getContentResolver().query(data, filePathColumn, null, null, null);
	cursor.moveToFirst();

	imagePath = cursor.getString( cursor.getColumnIndex(filePathColumn[0]) );
	cursor.close();

	return imagePath;
    }


    private void saveMovie(MovieInfo movieInfo)
    {
    database.updateMovie(movieInfo, currentPosterBitmap, lastPosterUrl);
    Toast.makeText(EditMovieActivity.this, "the movie '"+ movieInfo.getTitle()+"' updated", Toast.LENGTH_SHORT).show();
    finish();
    }


    private void insertCurrentMovie(MovieInfo movieInfo)
    {
    database.insertMovie(movieInfo, currentPosterBitmap);
    Toast.makeText(this, "the movie '"+ movieInfo.getTitle()+"' added to your favorites ", Toast.LENGTH_SHORT).show();
    finish();
    }


    @Override
    public void onClick(View v)
    {
    hideKeyboard();

    switch(v.getId())
        {
        case R.id.b_show_poster:
        request.post(getTextFromEditText(posterURL));
        break;

        case R.id.b_last_poster:
        request.post(lastPosterUrl);
        break;

        case R.id.b_add_poster:

        if(PermissionManager.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE , ADD_POSTER_PERMISSIONS_REQUEST))
            {
            addPosterDialog.show();
            }

        break;
        }
    }


    private MovieInfo createMovie()
    {
    String titleText,yearText,genreText,languageText,plotText,posterURLText;
    MovieInfo movieInfo=null;
    float rate;

    titleText= getTextFromEditText(title);
    yearText= getTextFromEditText(year);


    if(titleText!=null && yearText!=null)
        {
        genreText=getTextFromEditText(genre);
        languageText=getTextFromEditText(language);
        plotText=getTextFromEditText(plot);

        posterURLText=getTextFromEditText(posterURL);
        posterURLText= ( posterURLText!=null && posterURLText.startsWith("http") )? posterURLText : null;

        rate=2*rating.getRating();

        movieInfo=movieInfoFromDB;

        if(movieInfo!=null)
            {
            movieInfo.setTitle(titleText);
            movieInfo.setGenre(genreText);
            movieInfo.setYear(yearText);
            movieInfo.setLanguage(languageText);
            movieInfo.setPlot(plotText);
            movieInfo.setPosterURL(posterURLText);
            movieInfo.setRating(rate);

            }else
                {
                movieInfo=new MovieInfo(null, titleText, genreText, languageText, plotText, yearText, posterURLText, rate);
                }
        }else
            {
            Toast.makeText(EditMovieActivity.this, "You Must Enter a Title And Year!!", Toast.LENGTH_SHORT).show();
            }


    hideKeyboard();

    return movieInfo;
    }


    private String getTextFromEditText(EditText editText)
    {
    String text;

    text=editText.getText().toString();

    if(text.isEmpty())
        {
        text=null;
        }

    return text;
    }


    private void hideKeyboard()
    {
    View view = getCurrentFocus();

    if (view != null)
        {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
    switch(position)
        {
        case WEB_POSITION:
        localLayout.setVisibility(View.GONE);
        webLayout.setVisibility(View.VISIBLE);
        break;

        case LOCAL_POSITION:
        webLayout.setVisibility(View.GONE);
        localLayout.setVisibility(View.VISIBLE);
        break;
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent){}


    @Override
    public void onGalleryClick()
    {
    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    startActivityForResult(intent,GALLERY_REQUEST_CODE);
    }


    @Override
    public void onCameraClick()
    {
    Intent cameraIntent;
    File posterFile;

    cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    if(cameraIntent.resolveActivity(getPackageManager()) != null)
        {
        posterFile = fileManager.createTempPoster();

        if(posterFile != null)
            {
            currentCameraPosterUrl=posterFile.getAbsolutePath();

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(posterFile));
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        }
    }


    @Override
    public void preLoad()
    {
    poster.preLoad();
    }


    @Override
    public void onLoad(Bitmap bitmap)
    {
    poster.onLoad(bitmap);

    currentPosterBitmap=bitmap;

    if(waitingDialog.isShowing())
        {
        waitingDialog.dismiss();
        }
    }


    
}
