package com.example.rohan.popularmovies;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Rohan on 26-Jan-16.
 */
public class MainActivityFragment extends Fragment
{

    ArrayList<Movie> movies;
    GridView gridView;
    ProgressDialog progressDialog;
    PosterAdapter adapter;
    Comparator<Movie> highestPopularityComparator;
    Comparator<Movie> highestRatedComparator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View outputView = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);

        movies = new ArrayList<>();

        GetMoviesAsyncTask asyncTask = new GetMoviesAsyncTask(getActivity());
        asyncTask.execute();

        highestPopularityComparator = new Comparator<Movie>()
        {
            @Override
            public int compare(Movie lhs, Movie rhs)
            {
                return (int)(rhs.getPopularity()*10000 - lhs.getPopularity()*10000);
            }
        };

        highestRatedComparator = new Comparator<Movie>()
        {
            @Override
            public int compare(Movie lhs, Movie rhs)
            {
                return (int)(rhs.getVoteAverage()*10000 - lhs.getVoteAverage()*10000);
            }
        };

        gridView = (GridView) outputView.findViewById(R.id.gridView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
//                Toast.makeText(getActivity(), itemClicked.getTitle() + "", Toast.LENGTH_SHORT).show();
                Movie itemClicked = movies.get(position);

                Bundle b = new Bundle();
                b.putSerializable(Constants.MOVIE_POSTER_CLICKED, itemClicked);

                Intent posterClicked = new Intent(getActivity(), DetailedActivity.class);
                posterClicked.putExtras(b);
                startActivity(posterClicked);
            }
        });

        return outputView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
//         Handle action bar item clicks here. The action bar will
//         automatically handle clicks on the Home/Up button, so long
//         as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        noinspection SimplifiableIfStatement
        if (id == R.id.highest_rated)
        {
            Collections.sort(movies, highestRatedComparator);
            adapter.notifyDataSetChanged();
            return true;
        }
        else if (id == R.id.most_popular)
        {
            Collections.sort(movies, highestPopularityComparator);
            adapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class GetMoviesAsyncTask extends AsyncTask<String, Void, ArrayList<Movie>>
    {

        Context context;

        public GetMoviesAsyncTask(Context context)
        {
            this.context = context;
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params)
        {
            try
            {
                URL url = new URL(Constants.POPULAR_MOVIES_LIST_BASE_URL + Constants.API_KEY);
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Response response = okHttpClient.newCall(request).execute();
                String JsonFromURL = response.body().string();

                if(response.isSuccessful())
                {
                    ArrayList<Movie> movieList = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(JsonFromURL);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");

                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject currentJsonObject = jsonArray.getJSONObject(i);

                        String title = currentJsonObject.getString("title");
                        long id = currentJsonObject.getLong("id");
                        long voteCount = currentJsonObject.getLong("vote_count");
                        double voteAverage = currentJsonObject.getDouble("vote_average");
                        double popularity = currentJsonObject.getDouble("popularity");
                        String releaseDate = currentJsonObject.getString("release_date");
                        boolean isAdult = currentJsonObject.getBoolean("adult");
                        String overview = currentJsonObject.getString("overview");
                        String posterPath = currentJsonObject.getString("poster_path");
                        String originalLanguage = currentJsonObject.getString("original_language");

                        Movie currentMovie = new Movie(title, id, voteCount, voteAverage, popularity, releaseDate, isAdult, overview, posterPath, originalLanguage);
                        movieList.add(currentMovie);

                    }

                    return movieList;
                }

            }

            catch (Exception e)
            {
                Log.v(" API_KEY_ERROR", "Invalid API KEY");
                e.printStackTrace();
            }

            return null;

        }

        //check if net is on
        public boolean isOnline(Context context)
        {
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

            if(activeNetwork.isConnectedOrConnecting())
                return activeNetwork != null;

            return false;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Fetching movies...");

            if(!isOnline(context))
            {
                cancel(true);
                Toast.makeText(context, "Unable to connect to the internet!", Toast.LENGTH_LONG).show();
            }

            else
                progressDialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> listOfMovies)
        {
            //send movie list to grid adapter
            movies = listOfMovies;
            adapter = new PosterAdapter(context, movies);
            gridView.setAdapter(adapter);
            gridView.setAdapter(adapter);

            progressDialog.dismiss();
        }
    }

}