package com.example.rohan.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Rohan on 27-Jan-16.
 */
public class PosterAdapter extends BaseAdapter
{
    ArrayList<Movie> movies;
    Context context;

    public PosterAdapter(Context context, ArrayList<Movie> movieList)
    {
        movies = new ArrayList<>();

        this.context = context;

        if(movieList != null)
            movies = movieList;
    }

    @Override
    public int getCount()
    {
        return movies.size();
    }

    @Override
    public Movie getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View outputView = convertView;

        if(outputView == null)
        {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            outputView = inflater.inflate(R.layout.grid_view_layout, parent, false);
        }

        ImageView imageView = (ImageView)outputView.findViewById(R.id.moviePoster);

        Movie movie = getItem(position);
        String url = Constants.POSTER_BASE_URL + Constants.POSTER_SIZE_W185 + movie.getPosterPath();

        Picasso.with(context).load(url).into(imageView);

        return outputView;
    }
}
