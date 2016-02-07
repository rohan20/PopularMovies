package com.example.rohan.popularmovies;

import java.io.Serializable;

/**
 * Created by Rohan on 26-Jan-16.
 */
public class Movie implements Serializable
{
    private String title;
    private long id;
    private long voteCount;
    private double voteAverage;
    private double popularity;
    private String releaseDate;
    private boolean isAdult;
    private String overview;
    private String posterPath;
    private String originalLanguage;

    public Movie(String title, long id, long voteCount, double voteAverage, double popularity, String releaseDate, boolean isAdult, String overview, String posterPath, String originalLanguage)
    {
        this.title = title;
        this.id = id;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.popularity = popularity;
        this.releaseDate = releaseDate;
        this.isAdult = isAdult;
        this.overview = overview;
        this.posterPath = posterPath;
        this.originalLanguage = originalLanguage;
    }

    public String getTitle()
    {
        return title;
    }

    public boolean isAdult()
    {
        return isAdult;
    }

    public double getPopularity()
    {
        return popularity;
    }

    public double getVoteAverage()
    {
        return voteAverage;
    }

    public long getVoteCount()
    {
        return voteCount;
    }

    public long getId()
    {
        return id;
    }

    public String getPosterPath()
    {
        return posterPath;
    }

    public String getOverview()
    {
        return overview;
    }

    public String getReleaseDate()
    {
        return releaseDate;
    }

    public String getOriginalLanguage()
    {
        return originalLanguage;
    }
}
