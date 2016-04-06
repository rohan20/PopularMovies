package com.example.rohan.popularmovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Rohan on 03-Apr-16.
 */
public class FavouritesOpenHelper extends SQLiteOpenHelper
{

    public final static String FAVOURITES_TABLE = "AllFavourites";
    public final static String FAVOURITES_ID = "IdOfFavourite";


    public FavouritesOpenHelper(Context context, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, "Movies database", factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + FAVOURITES_TABLE + " ( " + FAVOURITES_ID + " varchar(255))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}
