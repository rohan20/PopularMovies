package com.example.rohan.popularmovies;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetailedActivityFragment extends Fragment
{

    FavouritesOpenHelper helper;
    SQLiteDatabase dbWritable;
    SQLiteDatabase dbReadable;
    ContentValues cv;
    Cursor c;

    TextView title;
    ImageView poster;
    TextView userRating;
    TextView releaseDate;
    TextView synopsis;
    Button favouritesButton;

    TextView reviewOne;
    TextView reviewOneAuthor;
    TextView reviewTwo;
    TextView reviewTwoAuthor;

    ImageView trailerOne;
    ImageView trailerTwo;

    Movie movie;

    ProgressDialog progressDialogTrailers;
    ProgressDialog progressDialogReviews;

    ScrollView parent;
    ScrollView child1;
    ScrollView child2;

    String id[];
    String selectString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View outputView = inflater.inflate(R.layout.fragment_detailed, container, false);

        helper = new FavouritesOpenHelper(getActivity(), null, 1);
        dbWritable = helper.getWritableDatabase();
        dbReadable = helper.getReadableDatabase();

        cv = new ContentValues();

        title = (TextView) outputView.findViewById(R.id.originalTitleTextView);
        poster = (ImageView) outputView.findViewById(R.id.moviePosterImageView);
        userRating = (TextView) outputView.findViewById(R.id.userRating);
        releaseDate = (TextView) outputView.findViewById(R.id.releaseDate);
        synopsis = (TextView) outputView.findViewById(R.id.synopsisTextView);

        favouritesButton = (Button) outputView.findViewById(R.id.addToFavourites);

        reviewOne = (TextView) outputView.findViewById(R.id.review1);
        reviewOneAuthor = (TextView) outputView.findViewById(R.id.review1Author);
        reviewTwo = (TextView) outputView.findViewById(R.id.review2);
        reviewTwoAuthor = (TextView) outputView.findViewById(R.id.review2Author);

        trailerOne = (ImageView) outputView.findViewById(R.id.trailerOne);
        trailerTwo = (ImageView) outputView.findViewById(R.id.trailerTwo);

        parent = (ScrollView) outputView.findViewById(R.id.parentScrollView);
        child1 = (ScrollView) outputView.findViewById(R.id.childScrollView1);
        child2 = (ScrollView) outputView.findViewById(R.id.childScrollView2);

        //To make movie reviews scrollable
        parent.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                child1.getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        child1.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                // Disallow the touch request for parent scroll on touch of
                // child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        child2.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                // Disallow the touch request for parent scroll on touch of
                // child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        return outputView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        movie = (Movie) getArguments().getSerializable(Constants.MOVIE_POSTER_CLICKED);

        id = new String[]{movie.getId() + ""};
        selectString = "SELECT * FROM " + FavouritesOpenHelper.FAVOURITES_TABLE + " WHERE " + FavouritesOpenHelper.FAVOURITES_ID + " =? ";

        setMovieData(movie);

        //add/remove as favourite
        favouritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                c = dbReadable.rawQuery(selectString, id);

                if(c.moveToFirst()) //Movie exists in favourites
                {
                    //remove movie from favourites
                    dbWritable.delete(FavouritesOpenHelper.FAVOURITES_TABLE, FavouritesOpenHelper.FAVOURITES_ID + " = " + movie.getId() + "", null);
                    Toast.makeText(getActivity(), movie.getTitle() + " removed from favourites.", Toast.LENGTH_SHORT).show();
                    favouritesButton.setText("Add");
                }
                else
                {
                    cv.put("IdOfFavourite", movie.getId());
                    dbWritable.insert(FavouritesOpenHelper.FAVOURITES_TABLE, null, cv);
                    Toast.makeText(getActivity(), movie.getTitle() + " added to favourites.", Toast.LENGTH_SHORT).show();
                    favouritesButton.setText("Remove");
                }

            }
        });

    }

    public void setMovieData(final Movie movie)
    {

        //first check if movie is a favourite
        c = dbReadable.rawQuery(selectString, id);

        if(c.moveToFirst()) //Movie exists in favourites
        {
            favouritesButton.setText("Remove");
        }
        else
        {
            favouritesButton.setText("Add");
        }

        //fetch reviews
        GetReviewsAsyncTask reviewsAsyncTask = new GetReviewsAsyncTask();
        reviewsAsyncTask.execute();

        //fetch trailers
        GetTrailersAsyncTask moviesAsyncTask = new GetTrailersAsyncTask();
        moviesAsyncTask.execute();

        title.setText(movie.getTitle());
        String posterPathForImageView = Constants.POSTER_BASE_URL + Constants.POSTER_SIZE_W185 + movie.getPosterPath();
        Picasso.with(getActivity()).load(posterPathForImageView).into(poster);
        userRating.setText(movie.getVoteAverage() + " / 10");
        releaseDate.setText(movie.getReleaseDate());
        synopsis.setText(movie.getOverview());
    }

    class GetReviewsAsyncTask extends AsyncTask<String, Void, ArrayList<String>>
    {

        ArrayList<String> reviews = new ArrayList<>();

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            URL reviewURL;

            try {
                reviewURL = new URL(Constants.API_BASE_URL + movie.getId() + Constants.REVIEWS_REST_OF_THE_URL + Constants.API_KEY);
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder().url(reviewURL).build();

                Response response = okHttpClient.newCall(request).execute();
                String JSONFromURL = response.body().string();

                if (response.isSuccessful()) {


                    JSONObject jsonObject = new JSONObject(JSONFromURL);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject currentJSONObject = jsonArray.getJSONObject(i);

                        String author = currentJSONObject.getString("author");
                        String content = currentJSONObject.getString("content");

                        reviews.add(author);
                        reviews.add(content);
                    }

                    return reviews;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialogReviews = new ProgressDialog(getActivity());
            progressDialogReviews.setMessage("Fetching movie reviews...");
            progressDialogReviews.show();
        }

        @Override
        protected void onPostExecute(ArrayList<String> movieReview) {

            if (movieReview.size() == 0) {
                reviewOne.setText("Review not available!");
                reviewTwo.setText("Review not available!");
                reviewOneAuthor.setVisibility(View.GONE);
                reviewTwoAuthor.setVisibility(View.GONE);

                progressDialogReviews.dismiss();

                return;
            }

            reviewOneAuthor.setText(movieReview.get(0));
            reviewOne.setText(movieReview.get(1));

            if (movieReview.size() >= 4)
            {
                reviewTwoAuthor.setText(movieReview.get(2));
                reviewTwo.setText(movieReview.get(3));
            }

            else
            {
                reviewTwoAuthor.setVisibility(View.GONE);
                reviewTwo.setVisibility(View.GONE);
            }

            progressDialogReviews.dismiss();
        }


    }

    class GetTrailersAsyncTask extends AsyncTask<String, Void, ArrayList<String>> {

        URL trailersURL;

        @Override
        protected ArrayList<String> doInBackground(String... params) {

            try {
                trailersURL = new URL(Constants.API_BASE_URL + movie.getId() + Constants.TRAILER_REST_OF_THE_URL + Constants.API_KEY);
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder().url(trailersURL).build();

                Response response = okHttpClient.newCall(request).execute();
                String JSONFromURL = response.body().string();

                if (response.isSuccessful()) {
                    ArrayList<String> trailers = new ArrayList<>();

                    JSONObject jsonObject = new JSONObject(JSONFromURL);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject currentJSONObject = jsonArray.getJSONObject(i);

                        String key = currentJSONObject.getString("key");

                        trailers.add(key);
                    }

                    return trailers;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialogTrailers = new ProgressDialog(getActivity());
            progressDialogTrailers.setMessage("Fetching movie trailers...");
            progressDialogTrailers.show();
        }

        @Override
        protected void onPostExecute(ArrayList<String> trailers) {

            if(trailers.size() == 0)
            {
                trailerOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "Trailer 1 not available!", Toast.LENGTH_SHORT).show();
                    }
                });

                trailerTwo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "Trailer 2 not available!", Toast.LENGTH_SHORT).show();
                    }
                });

                progressDialogTrailers.dismiss();

                return;
            }

            String key1 = trailers.get(0);
//            String key1 = "zSWdZVtXT7E";
            String thumbnail1 = Constants.TRAILER_THUMBNAIL_PART_ONE + key1 + Constants.TRAILER_THUMBNAIL_PART_TWO;

            final String urlTrailerOne = Constants.TRAILER_YOUTUBE_BASE_URL + key1;

            Picasso.with(getActivity()).load(thumbnail1).into(trailerOne);

            trailerOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(urlTrailerOne));
                    startActivity(i);
                }
            });


            if (trailers.size() >= 2) {

                final String key2 = trailers.get(1);
//            final String key2 = "zSWdZVtXT7E";
                String thumbnail2 = Constants.TRAILER_THUMBNAIL_PART_ONE + key2 + Constants.TRAILER_THUMBNAIL_PART_TWO;

                final String urlTrailerTwo = Constants.TRAILER_YOUTUBE_BASE_URL + key2;

                Picasso.with(getActivity()).load(thumbnail2).into(trailerTwo);

                trailerTwo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(urlTrailerTwo));
                        startActivity(i);
                    }
                });

            }
            else
            {
                String thumbnail2 = "http://img.youtube.com/vi/video_id/default.jpg";
                Picasso.with(getActivity()).load(thumbnail2).into(trailerTwo);

                trailerTwo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "Trailer 2 not available!", Toast.LENGTH_SHORT).show();
                    }
                });
            }


            progressDialogTrailers.dismiss();

        }
    }

}
