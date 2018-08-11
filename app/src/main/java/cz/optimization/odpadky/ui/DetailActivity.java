package cz.optimization.odpadky.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.optimization.odpadky.R;
import cz.optimization.odpadky.objects.Container;
import cz.optimization.odpadky.room_data.PlaceWatched;
import cz.optimization.odpadky.room_data.PlacesDatabase;
import cz.optimization.odpadky.room_data.PlacesWatchedViewModel;
import cz.optimization.odpadky.room_data.DialogRecyclerViewAdapter;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {
    private AdView mAdView;

    private String placeId;
    private String title;
    private String containersListString;

    private PlacesDatabase placesDatabase;
    private PlacesWatchedViewModel viewModel;
    private DialogRecyclerViewAdapter recyclerViewAdapter;
    private DetailActivityRecyclerViewAdapter detailActivityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // gets data from MapsActivity as parent activity
        Intent intent = getIntent();
        placeId = intent.getStringExtra("placeId");
        title = intent.getStringExtra("title");
        containersListString = intent.getStringExtra("containersList");

        Type type = new TypeToken<List<Container>>() {
        }.getType();
        Gson gson = new Gson();
        List<Container> containersList = gson.fromJson(containersListString, type);

        //recyclerview for containers
        RecyclerView detailRecyclerView = findViewById(R.id.detail_recycler_view);
        detailRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        detailActivityAdapter = new DetailActivityRecyclerViewAdapter(containersList, this);

        detailRecyclerView.setAdapter(detailActivityAdapter);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Toolbar  toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbar.setTitle(title);

       placesDatabase = Room.databaseBuilder(getApplicationContext(),
                PlacesDatabase.class, "places_db").build();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new InsertPlaceToWatchedAsync().execute();

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_activity_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_watched) {

           // String[] tvshows={"Crisis","Blindspot","BlackList","Game of Thrones","Gotham","Banshee"};

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            View convertView = inflater.inflate(R.layout.fragment_dialog_watched, null);
            alertDialog.setView(convertView);
            alertDialog.setTitle(R.string.watch_list);

            RecyclerView dialogRecyclerView = convertView.findViewById(R.id.recycler_view);
            dialogRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewAdapter = new DialogRecyclerViewAdapter(new ArrayList<PlaceWatched>(), this);


            viewModel = ViewModelProviders.of(this).get(PlacesWatchedViewModel.class);

            viewModel.getPlaceWatchedList().observe(DetailActivity.this, new Observer<List<PlaceWatched>>() {
                @Override
                public void onChanged(@Nullable List<PlaceWatched> watchList) {
                    recyclerViewAdapter.addItems(watchList);
                }
            });

            dialogRecyclerView.setAdapter(recyclerViewAdapter);







            alertDialog.show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(DetailActivity.this, "Click na dialog ok", Toast.LENGTH_SHORT).show();

    }

    private class InsertPlaceToWatchedAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute(); }

        @Override
        protected Void doInBackground(Void... voids) {
            //Add place to db
            PlaceWatched placeWatched = new PlaceWatched(placeId, title, containersListString);
            placesDatabase.placesWatchedModel().insertSinglePlace(placeWatched);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid); }
    }


    /*private class QueryAllPlacesAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute(); }

        @Override
        protected Void doInBackground(Void... voids) {
            //Add place to db
            PlaceWatched placeWatched = new PlaceWatched(placeId, title, containersList);
            placesDatabase.placesWatchedModel().insertSinglePlace(placeWatched);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid); }
    }*/




}
