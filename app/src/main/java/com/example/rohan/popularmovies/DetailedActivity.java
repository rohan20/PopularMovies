package com.example.rohan.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DetailedActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        DetailedActivityFragment detailedActivityFragment = new DetailedActivityFragment();
        detailedActivityFragment.setArguments(bundle);

        getFragmentManager().beginTransaction().add(R.id.portraitContainer, detailedActivityFragment).commit();
    }
}
