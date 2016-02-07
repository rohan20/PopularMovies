package com.example.rohan.popularmovies;

import android.app.Fragment;
import android.media.Image;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailedActivityFragment extends Fragment
{

    TextView title;
    ImageView poster;
    TextView userRating;
    TextView releaseDate;
    TextView synopsis;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View outputView = inflater.inflate(R.layout.fragment_detailed, container, false);

        title = (TextView) outputView.findViewById(R.id.originalTitleTextView);
        poster = (ImageView) outputView.findViewById(R.id.moviePosterImageView);
        userRating = (TextView) outputView.findViewById(R.id.userRating);
        releaseDate = (TextView) outputView.findViewById(R.id.releaseDate);
        synopsis = (TextView) outputView.findViewById(R.id.synopsisTextView);

        return outputView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Movie movie = (Movie)getArguments().getSerializable(Constants.MOVIE_POSTER_CLICKED);
        setMovieData(movie);
    }

    public void setMovieData(Movie movie)
    {
        title.setText(movie.getTitle());
        String posterPathForImageView = Constants.POSTER_BASE_URL + Constants.POSTER_SIZE_W185 + movie.getPosterPath();
        Picasso.with(getActivity()).load(posterPathForImageView).into(poster);
        userRating.setText(movie.getVoteAverage() + " / 10");
        releaseDate.setText(movie.getReleaseDate());
        synopsis.setText(movie.getOverview());
    }
}
