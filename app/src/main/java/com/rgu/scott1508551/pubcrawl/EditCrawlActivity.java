package com.rgu.scott1508551.pubcrawl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

public class EditCrawlActivity extends AppCompatActivity implements OnMapReadyCallback {

    Bundle data;
    LatLng latLng;
    int numPubs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_crawl);

        MapFragment mapFrag = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);

        //set the bundle to the bundle sent from generate activity
        data = getIntent().getExtras();
        Log.d("Edit Crawl Bundle", data.toString());

        //set the values of latLng and numPubs from the bundle
        latLng = new LatLng(data.getDouble("lat"), data.getDouble("lng"));
        numPubs = data.getInt("numPubs");

        Log.d("Edit Crawl latLng", String.valueOf(latLng));
        Log.d("Edit Crawl numPubs", String.valueOf(numPubs));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
