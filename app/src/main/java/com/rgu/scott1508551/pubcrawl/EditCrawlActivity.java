package com.rgu.scott1508551.pubcrawl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class EditCrawlActivity extends AppCompatActivity {

    Bundle data;
    LatLng latLng;
    int numPubs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_crawl);

        //set the bundle to the bundle sent from generate activity
        data = getIntent().getExtras();
        Log.d("Edit Crawl Bundle", data.toString());

        //set the values of latLng and numPubs from the bundle
        latLng = new LatLng(data.getDouble("lat"), data.getDouble("lng"));
        numPubs = data.getInt("numPubs");

        Log.d("Edit Crawl latLng", String.valueOf(latLng));
        Log.d("Edit Crawl numPubs", String.valueOf(numPubs));

    }
}
