package com.osh.apps.movies.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.osh.apps.movies.R;
import com.osh.apps.movies.app.AppData;
import com.osh.apps.movies.appDB.AppDB;
import com.osh.apps.movies.clickableRecyclerView.ClickableRecyclerView;
import com.osh.apps.movies.listMovie.ListMovieAdapter;
import com.osh.apps.movies.movieInfo.MovieInfoBinder;
import com.osh.apps.movies.movieInfo.SimpleMovieInfo;
import com.osh.apps.movies.network.connection.JsonConnection;
import com.osh.apps.movies.network.SearchRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class OMDbActivity extends AppCompatActivity implements  ClickableRecyclerView.OnItemClickListener
{
private SwipyRefreshLayout swipyRefreshLayout;
private ClickableRecyclerView recyclerView;
private int lastItemClickedPosition;
private SearchRequest searchRequest;
private WebSearchTask searchTask;
private ImageButton searchButton;
private ListMovieAdapter adapter;
private EditText searchQuery;
private AppDB database;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_omdb);

    lastItemClickedPosition=AppData.NULL_DATA;
    searchTask=null;
    searchRequest=null;

    AppData.setItemPosterHeight(getResources());

    database= AppDB.getInstance(this);

    setViews();
    }


    private void setViews()
    {

    ActionBar actionBar=getSupportActionBar();
    actionBar.setTitle("Search Movie From Web");
    actionBar.setHomeButtonEnabled(true);
    actionBar.setDisplayHomeAsUpEnabled(true);

    searchQuery =(EditText) findViewById(R.id.et_keywords);
    searchQuery.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
            if(actionId == EditorInfo.IME_ACTION_SEARCH)
                {
                onSearch();
                return true;
                }

            return false;
            }
        });

    searchButton =(ImageButton) findViewById(R.id.ib_search);

    searchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            onSearch();
            }
        });

    swipyRefreshLayout= (SwipyRefreshLayout) findViewById(R.id.swipyrefreshlayout);
    swipyRefreshLayout.setEnabled(false);
    swipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction)
            {
            onLoadNextPage();
            }
        });

    recyclerView = (ClickableRecyclerView) findViewById(R.id.recyclerView);

    adapter=new ListMovieAdapter(this,R.layout.rv_search_movie_item);

    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(adapter);
    recyclerView.setOnItemClickListener(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

    switch (item.getItemId())
        {
        case android.R.id.home:
        finish();
        break;
        }

    return true;
    }


    @Override
    protected void onResume()
    {
    super.onResume();

    SimpleMovieInfo simpleMovieInfo;

    if(lastItemClickedPosition!=AppData.NULL_DATA)
        {
        simpleMovieInfo=adapter.getItem(lastItemClickedPosition);

        simpleMovieInfo.setId(database.getMovieID(simpleMovieInfo.getImdbId()));

        adapter.notifyItemChanged(lastItemClickedPosition);

        lastItemClickedPosition=AppData.NULL_DATA;
        }
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
    public void onItemClick(RecyclerView.ViewHolder viewHolder, int position, int viewType)
    {
    MovieInfoBinder movieInfoBinder;
    Intent intent;

    movieInfoBinder=adapter.getItem(position);

    lastItemClickedPosition=position;

    intent= new Intent(this, MovieInfoActivity.class);

    if(movieInfoBinder.hasDatabaseID())
        {
        intent.putExtra(AppData.IntentKey.MOVIE_ID_KEY, movieInfoBinder.getId());
        }else
            {
            intent.putExtra(AppData.IntentKey.IMDB_ID_KEY, movieInfoBinder.getImdbId());
            }

    startActivity(intent);
    }


    private void onSearch()
    {
    String query;

    query= searchQuery.getText().toString();

    if(query.length() > 1)
        {

        if(searchRequest!=null)
            {
            searchRequest.cancel();
            }

        searchRequest=new SearchRequest(query);

        searchTask=new WebSearchTask(searchRequest);
        searchTask.execute();
        }else
            {
            Toast.makeText(OMDbActivity.this, "Must Provide More Than One Character" , Toast.LENGTH_SHORT).show();
            }

    hideKeyboard();
    }


    private void onLoadNextPage()
    {

    if(searchTask==null && searchRequest!=null && !searchRequest.isCanceled() && searchRequest.nextPage()  )
        {
        searchTask=new WebSearchTask(searchRequest);
        searchTask.execute();
        }

    }


    private class WebSearchTask extends AsyncTask<Void, String, ArrayList<MovieInfoBinder>>
    {
    private ProgressDialog dialog;
    private SearchRequest request;
    private boolean isSearchFirstPage;


        public WebSearchTask(SearchRequest request)
        {
        this.request = request;

        isSearchFirstPage=request.getCurrentPage()== 1;
        }


        @Override
        protected void onPreExecute()
        {

        if(isSearchFirstPage)
            {
            searchButton.setClickable(false);

            adapter.clearMovies();

            dialog=ProgressDialog.show(OMDbActivity.this, "Search Movies", "Please wait...", true);
            swipyRefreshLayout.setEnabled(false);
            }

        }


        @Override
        protected ArrayList<MovieInfoBinder> doInBackground(Void... requests)
        {
        ArrayList<MovieInfoBinder> movies;
        String jsonResult,OMDbURL;
        JSONObject json,movie;
        JSONArray search;

        Log.d("WebSearchTask","searching movie... ");

        movies=null;

        try {
            OMDbURL=AppData.OMDB.OMDB_REQUEST_URL + AppData.OMDB.OMDB_SEARCH_PARAMETER + AppData.OMDB.getSearchFormat(request.getQuery()) + AppData.OMDB.OMDB_FILTER_MOVIE_PARAMETER + AppData.OMDB.OMDB_PAGE_PARAMETER + request.getCurrentPage();

            jsonResult= JsonConnection.getJsonResult(OMDbURL);

            json=new JSONObject(jsonResult);

            if(json.getBoolean("Response") && !request.isCanceled())
                {
                search=json.getJSONArray("Search");

                request.setTotalResults(json.getInt("totalResults"));

                movies=new ArrayList<>();

                for (int i = 0; i < search.length() && !request.isCanceled(); i++)
                    {
                    movie=getJSONMovie(search.getJSONObject(i).getString("imdbID"));

                    movies.add(getMovieInfoBinder(movie) );
                    }

                }else
                    {
                    Log.d("WebSearchTask","Error: The search failed - " + json.getString("Error"));
                    publishProgress(json.getString("Error"));
                    }


            }catch (Exception e)
                {
                Log.d("WebSearchTask","Error: The search failed - "+e.getMessage());
                e.printStackTrace();
                publishProgress("The search failed");
                }

        return movies;
        }


        private JSONObject getJSONMovie(@NonNull String imdbID) throws Exception
        {
        String OMDbURL;

        OMDbURL=AppData.OMDB.OMDB_REQUEST_URL + AppData.OMDB.OMDB_ID_PARAMETER + imdbID;

        return new JSONObject(JsonConnection.getJsonResult(OMDbURL));
        }


        private MovieInfoBinder getMovieInfoBinder(JSONObject movie) throws JSONException
        {
        MovieInfoBinder movieInfoBinder;
        float rating;

        try
            {
            rating=Float.parseFloat(movie.getString("imdbRating"));
            }catch(Exception e)
                {
                rating=0;
                }

        movieInfoBinder=new MovieInfoBinder( movie.getString("imdbID"), movie.getString("Title"), movie.getString("Year"), movie.getString("Poster"), rating );

        movieInfoBinder.setId(database.getMovieID(movieInfoBinder.getImdbId()));

        return movieInfoBinder;
        }


        @Override
        protected void onProgressUpdate(String... values)
        {
        Toast.makeText(OMDbActivity.this, values[0] , Toast.LENGTH_SHORT).show();
        }


        @Override
        protected void onPostExecute(ArrayList<MovieInfoBinder> movies)
        {

        if(!request.isCanceled())
            {
            adapter.addMovies(movies);
            }

        if(isSearchFirstPage)
            {
            searchButton.setClickable(true);
            }else
                {
                swipyRefreshLayout.setRefreshing(false);
                }

        searchTask=null;
        swipyRefreshLayout.setEnabled(movies!=null && request.hasNext());

        if(dialog!=null)
            {
            dialog.dismiss();
            dialog=null;
            }
        }
    }
}
