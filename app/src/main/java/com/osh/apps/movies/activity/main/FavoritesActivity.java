package com.osh.apps.movies.activity.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.osh.apps.movies.R;
import com.osh.apps.movies.activity.EditMovieActivity;
import com.osh.apps.movies.activity.MovieInfoActivity;
import com.osh.apps.movies.activity.OMDbActivity;
import com.osh.apps.movies.app.AppData;
import com.osh.apps.movies.appDB.AppDB;
import com.osh.apps.movies.dialog.SimpleAlertDialog;
import com.osh.apps.movies.listMovie.ListMovieAdapter;
import com.osh.apps.movies.movieInfo.SimpleMovieInfo;
import com.osh.apps.movies.widget.recyclerview.CustomRecyclerView;


public class FavoritesActivity extends AppCompatActivity implements CustomRecyclerView.OnItemClickListener, CustomRecyclerView.OnItemLongClickListener
{
private CustomRecyclerView recyclerView;
private ListMovieAdapter adapter;
private AppDB database;
private PopupMenu popupMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_favorites);

    database=AppDB.getInstance(this);

    AppData.setItemPosterHeight(getResources());

    setViews();
    }


    private void setViews()
    {
    FloatingActionButton addButton;

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    toolbar.setLogo(R.mipmap.ic_launcher);

    ActionBar actionBar=getSupportActionBar();
    actionBar.setTitle(R.string.title_favorites_activity);

    addButton = (FloatingActionButton) findViewById(R.id.fab);
    addButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            SimpleAlertDialog.createAlertDialog(FavoritesActivity.this, "Add Movie", "How do you want to add a movie?",  "Web", "Manual", new SimpleAlertDialog.AlertDialogListener()
                {
                    @Override
                    public void onPositive(DialogInterface dialog)
                    {
                    Intent intent= new Intent(FavoritesActivity.this, OMDbActivity.class);
                    startActivity(intent);
                    }


                    @Override
                    public void onNegative(DialogInterface dialog)
                    {
                    openEditMovieActivity(null);
                    }
                });


            }
        });

    recyclerView = (CustomRecyclerView) findViewById(R.id.recyclerView);

    adapter=new ListMovieAdapter(this, R.layout.rv_simple_movie_item);

    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(adapter);
    recyclerView.setOnItemClickListener(this);
    recyclerView.setOnItemLongClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    getMenuInflater().inflate(R.menu.menu_favorites_activity, menu);

    menu.findItem(R.id.m_delete_all).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);

	return true;
    }


    @Override
    public void onBackPressed()
    {
    SimpleAlertDialog.createAlertDialog(this, "Quit?", "Are you sure you want to quit?", "Yes", "No", new SimpleAlertDialog.AlertDialogListener()
        {
            @Override
            public void onPositive(DialogInterface dialog)
            {
            finish();
            }

            @Override
            public void onNegative(DialogInterface dialog)
            {
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

    switch (item.getItemId())
        {
        case R.id.m_delete_all:

        SimpleAlertDialog.createAlertDialog(this, "Delete All Movies", "Are you sure you want to delete all movies?", "Yes", "No", new SimpleAlertDialog.AlertDialogListener()
            {
                @Override
                public void onPositive(DialogInterface dialog)
                {
                database.removeAllMovies();
                adapter.clearMovies();
                }

                @Override
                public void onNegative(DialogInterface dialog)
                {

                }
            });



        break;

        }

    return true;
    }


    @Override
    protected void onResume()
    {
    super.onResume();

    adapter.setMovies(database.getListMovies());
    }


    @Override
    public void onItemClick(RecyclerView.ViewHolder viewHolder, int position, int viewType)
    {
    openMovieInfoActivity(adapter.getItem(position));
    }


    @Override
    public void onItemLongClick(RecyclerView.ViewHolder viewHolder, final int position, int viewType)
    {
    final SimpleMovieInfo simpleMovieInfo=adapter.getItem(position);

    if(popupMenu==null)
        {
        popupMenu= new PopupMenu(this, viewHolder.itemView);
        popupMenu.inflate(R.menu.popup_menu_item);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
            {
                @Override
                public boolean onMenuItemClick(MenuItem item)
                {
                switch(item.getItemId())
                    {
                    case R.id.p_open:
                    openMovieInfoActivity(simpleMovieInfo);
                    break;

                    case R.id.p_delete:
                    deleteMovie(simpleMovieInfo, position);
                    break;

                    case R.id.p_edit:
                    openEditMovieActivity(simpleMovieInfo);
                    break;

                    }

                return true;
                }
            });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener()
            {
                @Override
                public void onDismiss(PopupMenu menu)
                {
                popupMenu=null;
                }
            });

        popupMenu.show();
        }
   

    }


    private void openEditMovieActivity(@Nullable SimpleMovieInfo simpleMovieInfo)
    {
    Intent intent= new Intent(this, EditMovieActivity.class);

    if(simpleMovieInfo!=null)
        {
        intent.putExtra(AppData.IntentKey.MOVIE_ID_KEY, simpleMovieInfo.getId());
        }

    startActivity(intent);
    }


    private void openMovieInfoActivity(@NonNull SimpleMovieInfo simpleMovieInfo)
    {
    Intent intent= new Intent(this, MovieInfoActivity.class);
    intent.putExtra(AppData.IntentKey.MOVIE_ID_KEY, simpleMovieInfo.getId());
    startActivity(intent);
    }


    private void deleteMovie(final SimpleMovieInfo simpleMovieInfo, final int position)
    {
    SimpleAlertDialog.createAlertDialog(this, "Delete Movie", "Are you sure you want to delete '"+simpleMovieInfo.getTitle()+"'?", "Yes", "No", new SimpleAlertDialog.AlertDialogListener()
            {
                @Override
                public void onPositive(DialogInterface dialog)
                {
                database.removeMovie(simpleMovieInfo);
                adapter.removeMovie(position);
                }


                @Override
                public void onNegative(DialogInterface dialog)
                {

                }
            });
    }


}
