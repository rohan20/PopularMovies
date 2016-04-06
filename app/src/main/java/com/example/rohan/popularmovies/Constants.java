package com.example.rohan.popularmovies;

/**
 * Created by Rohan on 27-Jan-16.
 */
public class Constants
{
    //TODO remove API KEY!
    public static final String API_KEY = "YOUR_API_KEY";
    public static final String API_BASE_URL = "http://api.themoviedb.org/3/movie/";

    public static final String POPULAR_MOVIES_LIST_BASE_URL = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=";

    public static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String MOVIE_POSTER_CLICKED = "detailed_movie_intent";
    public static final String POSTER_SIZE_W185 = "w185";

    public static final String REVIEWS_REST_OF_THE_URL = "/reviews?api_key=";

    public static final String TRAILER_REST_OF_THE_URL = "/videos?api_key=";
    public static final String TRAILER_YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

    public static final String TRAILER_THUMBNAIL_PART_ONE = "http://img.youtube.com/vi/";
    public static final String TRAILER_THUMBNAIL_PART_TWO = "/default.jpg";
}


