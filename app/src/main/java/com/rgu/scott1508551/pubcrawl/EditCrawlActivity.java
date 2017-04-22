package com.rgu.scott1508551.pubcrawl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class EditCrawlActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Bundle data;
    private ArrayList bars;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_crawl);

        MapFragment mapFrag = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);

        //set the bundle to the bundle sent from generate activity
        data = getIntent().getExtras();
        Log.d("Edit Crawl Bundle", data.toString());

        bars = data.getStringArrayList("bars");

        //set the start end end points
        String origin = bars.get(0).toString();
        String destination = bars.get(bars.size() - 1).toString();

        Log.d("ORIGIN",origin);
        Log.d("DESTINATION", destination);

        //remove first and last locations
        bars.remove(0);
        bars.remove(bars.size() - 1);

        url = "https://maps.googleapis.com/maps/api/directions/json?"
                + "origin=place_id:" + origin
                + "&destination=place_id:" + destination
                + "&waypoints=optimize:true|" + waypointsString()
                + "&key=" + getResources().getString(R.string.google_maps_key);

        Log.d("URL",url);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    private String waypointsString(){
        String str = "";

        for(int i = 0; i < bars.size(); i++){
            if(i == bars.size() - 1){
                str += "place_id:" + bars.get(i);
            }
            else{
                str += "place_id:" + bars.get(i) + "|";
            }

        }

        return str;
    }
}
