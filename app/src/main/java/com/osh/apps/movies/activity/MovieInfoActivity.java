package com.osh.apps.movies.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.osh.apps.movies.R;
import com.osh.apps.movies.app.AppData;
import com.osh.apps.movies.appDB.AppDB;
import com.osh.apps.movies.dialog.SimpleAlertDialog;
import com.osh.apps.movies.movieInfo.MovieInfo;
import com.osh.apps.movies.network.connection.JsonConnection;
import com.osh.apps.movies.poster.OnLoadListener;
import com.osh.apps.movies.poster.PosterLayout;
import com.osh.apps.movies.poster.PosterRequest;

import org.json.JSONObject;


public class MovieInfoActivity extends AppCompatActivity implements OnLoadListener
{
private TextView year,genre,language,plot;
private WebMovieInfoTask webMovieInfoTask;
private boolean isNewMovie;
private Bitmap currentPosterBitmap;
private PosterRequest request;
private ProgressDialog addDialog;
private MovieInfo movieInfo;
private ActionBar actionBar;
private PosterLayout poster;
private RatingBar rating;
private Menu optionsMenu;
private AppDB database;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_movie_info);

    isNewMovie=false;

    currentPosterBitmap =null;
    webMovieInfoTask=null;
    optionsMenu=null;
    movieInfo=null;

    addDialog=new ProgressDialog(this);
    addDialog.setTitle("Add Movie");
    addDialog.setMessage("Please wait...");
    addDialog.setIndeterminate(true);
    addDialog.setCancelable(false);


    database= AppDB.getInstance(this);

    setViews();

    request=new PosterRequest(getResources().getDimensionPixelSize(R.dimen.movie_info_poster_width), getResources().getDimensionPixelSize(R.dimen.movie_info_poster_height), this);
    }


    private void setViews()
    {
    actionBar=getSupportActionBar();
    actionBar.setTitle("Loading...");
    actionBar.setHomeButtonEnabled(true);
    actionBar.setDisplayHomeAsUpEnabled(true);

    year=(TextView) findViewById(R.id.tv_year);
    genre=(TextView) findViewById(R.id.tv_genre);
    language=(TextView) findViewById(R.id.tv_language);
    plot=(TextView) findViewById(R.id.tv_plot);

    rating=(RatingBar) findViewById(R.id.rb_rating);

    poster=(PosterLayout) findViewById(R.id.poster_layout);
    }


    @Override
    protected void onResume()
    {
    super.onResume();

    if(movieInfo== null)
        {
        init();
        }else if(!isNewMovie)
            {
            movieInfo=database.getMovieInfo(movieInfo.getId());
            setMovieInfo(movieInfo);
            }
    }


    @Override
    protected void onPause()
    {
    super.onPause();

    request.cancel();
    }


    private void init()
    {
    MovieInfo movieInfo;
    Intent intent;
    String imdbID;
    long movieID;

    intent=getIntent();

    imdbID=intent.getStringExtra(AppData.IntentKey.IMDB_ID_KEY);

    if(imdbID!=null)
        {
        //get movie info from web
        isNewMovie=true;

        webMovieInfoTask=new WebMovieInfoTask();
        webMovieInfoTask.execute(imdbID);
        }else
            {
            //get movie info from db
            movieID=intent.getLongExtra(AppData.IntentKey.MOVIE_ID_KEY, AppData.NULL_DATA);

            if(movieID != AppData.NULL_DATA)
                {
                movieInfo=database.getMovieInfo(movieID);

                setMovieInfo(movieInfo);
                }else
                    {
                    onMovieNotFound();
                    }
            }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    optionsMenu=menu;
    getMenuInflater().inflate(R.menu.menu_movie_info_activity, menu);

    menu.findItem(R.id.m_add).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
    menu.findItem(R.id.m_edit).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
    menu.findItem(R.id.m_delete).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);

	return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
    menu.findItem(R.id.m_add).setVisible(isNewMovie);
    menu.findItem(R.id.m_delete).setVisible(!isNewMovie);
    menu.findItem(R.id.m_edit).setVisible(!isNewMovie);

    return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    Intent intent;

    if(movieInfo!=null)
        {
        switch (item.getItemId())
            {
            case R.id.m_add:

            if(isNewMovie)
                {

                if(request.isDone())
                    {
                    insertCurrentMovie();
                    }else
                        {
                        addDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
                            {
                                @Override
                                public void onDismiss(DialogInterface dialog)
                                {
                                insertCurrentMovie();
                                }
                            });

                        addDialog.show();
                        }
                }

            break;

            case R.id.m_delete:

            if(!isNewMovie)
                {
                deleteMovie();
                }

            break;

            case R.id.m_edit:

            if(!isNewMovie)
                {
                //open edit activity
                intent= new Intent(this, EditMovieActivity.class);
                intent.putExtra(AppData.IntentKey.MOVIE_ID_KEY, movieInfo.getId());
                startActivity(intent);
                }
            break;

            case android.R.id.home:
            finish();
            break;

            }
        }


    return true;
    }


    private void setMovieInfo(MovieInfo movieInfo)
    {

    if(movieInfo!=null)
        {
        this.movieInfo=movieInfo;

        actionBar.setTitle(movieInfo.getTitle());

        rating.setRating(movieInfo.getRating()/2);
        year.setText(movieInfo.getYear());

        genre.setText(movieInfo.getGenre());
        language.setText(movieInfo.getLanguage());
        plot.setText(movieInfo.getPlot());

        poster.clearPoster();
        request.post(movieInfo.getPosterURL());
        }else
            {
            onMovieNotFound();
            }
    }


    private void onMovieNotFound()
    {
    Toast.makeText(this, "The movie not found!", Toast.LENGTH_SHORT).show();
    finish();
    }


    private void deleteMovie()
    {
    SimpleAlertDialog.createAlertDialog(this, null, "Are you sure you want to delete '"+movieInfo.getTitle()+"' from your favorites?", "Yes", "No", new SimpleAlertDialog.AlertDialogListener()
            {
                @Override
                public void onPositive(DialogInterface dialog)
                {
                database.removeMovie(movieInfo);
                Toast.makeText(MovieInfoActivity.this, "the movie '"+ movieInfo.getTitle()+"' removed from your favorites ", Toast.LENGTH_SHORT).show();
                finish();
                }


                @Override
                public void onNegative(DialogInterface dialog)
                {

                }
            });
    }


    private void insertCurrentMovie()
    {
    database.insertMovie(movieInfo, currentPosterBitmap);

    isNewMovie=false;
    onPrepareOptionsMenu(optionsMenu);
    Toast.makeText(this, "the movie '"+ movieInfo.getTitle()+"' added to your favorites ", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void preLoad()
    {
    poster.preLoad();
    }


    @Override
    public void onLoad(Bitmap bitmap)
    {
    currentPosterBitmap=bitmap;

    if(addDialog.isShowing())
        {
        addDialog.dismiss();
        }

    poster.onLoad(bitmap);
    }


    private class WebMovieInfoTask extends AsyncTask<String, Void, MovieInfo>
    {

        @Override
        protected MovieInfo doInBackground(String... params)
        {
        String jsonResult,OMDbURL;
        MovieInfo movieInfo;
        JSONObject movie;
        float rating;

        Log.d("WebSearchTask","loading movie info... ");

        movieInfo=null;


        try {
            OMDbURL= AppData.OMDB.OMDB_REQUEST_URL + AppData.OMDB.OMDB_ID_PARAMETER + params[0];

            jsonResult=JsonConnection.getJsonResult(OMDbURL);

            movie=new JSONObject(jsonResult);

            if(movie.getBoolean("Response"))
                {

                try {
                    rating=Float.parseFloat(movie.getString("imdbRating"));
                    }catch(Exception e)
                        {
                        rating=0;
                        }

                movieInfo=new MovieInfo(movie.getString("imdbID"), movie.getString("Title"), movie.getString("Genre"), movie.getString("Language"),movie.getString("Plot"), movie.getString("Year"), movie.getString("Poster"), rating );
                }else
                    {
                    Log.d("WebSearchTask","Error: failed to found ID '"+params[0]+"' - " + movie.getString("Error"));
                    }


            }catch (Exception e)
                {
                Log.d("WebSearchTask","Error: The search failed - "+e.getMessage());
                e.printStackTrace();
                }

        return movieInfo;
        }


        @Override
        protected void onPostExecute(MovieInfo movieInfo)
        {
        setMovieInfo(movieInfo);

        webMovieInfoTask=null;
        }
    }


}
