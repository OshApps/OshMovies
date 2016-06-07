package com.osh.apps.movies.listMovie;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.osh.apps.movies.listMovie.item.SimpleMovieItemView;
import com.osh.apps.movies.movieInfo.MovieInfoBinder;

import java.util.ArrayList;



/**
 * Created by oshri-n on 16/05/2016.
 */
public class ListMovieAdapter extends RecyclerView.Adapter<ListMovieAdapter.MovieHolder>
{
private ArrayList<MovieInfoBinder> movies;
private LayoutInflater inflater;
private int layout;


    public ListMovieAdapter(Context context, int layout)
    {
    this.layout=layout;

    inflater = LayoutInflater.from(context);

    movies = new ArrayList<>();
    }


    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
    SimpleMovieItemView itemView;

    itemView=(SimpleMovieItemView) inflater.inflate(layout, null);

    return new MovieHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MovieHolder holder, int position)
    {
    holder.bindMovieInfo(movies.get(position));
    }


    public MovieInfoBinder getItem(int position)
    {
    return movies.get(position);
    }


    public void setMovies(@NonNull ArrayList<MovieInfoBinder> movies)
    {
    clearMovies();
    addMovies(movies);
    }


    public void addMovies(@NonNull ArrayList<MovieInfoBinder> movies)
    {
    int lastItemPosition;

    if(movies!=null)
        {
        lastItemPosition=this.movies.size();

        this.movies.addAll(movies);

        notifyItemRangeInserted(lastItemPosition, movies.size());
        }
    }


    public void removeMovie(int positoin)
    {
    movies.remove(positoin);
    notifyItemRemoved(positoin);
    }


    public void clearMovies()
    {
    int size;

    if(!movies.isEmpty())
        {
        size=movies.size();
        movies.clear();

        notifyItemRangeRemoved(0 , size);
        }
    }


    @Override
    public int getItemCount()
    {
    return movies.size();
    }


    public class MovieHolder extends RecyclerView.ViewHolder
    {
    private SimpleMovieItemView itemView;


        public MovieHolder(SimpleMovieItemView itemView)
        {
        super(itemView);

        this.itemView=itemView;
        }


        public void bindMovieInfo(MovieInfoBinder movieInfoBinder)
        {
        itemView.setMovieInfo(movieInfoBinder);
        }


        public SimpleMovieItemView getItemView()
        {
        return itemView;
        }
    }
}
