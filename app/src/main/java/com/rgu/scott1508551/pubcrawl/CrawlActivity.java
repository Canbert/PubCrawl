package com.rgu.scott1508551.pubcrawl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

public class CrawlActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener{

    private GoogleMap map;
    private ImageView barImage;
    private TextView barName;
    private TextView barLocation;
    private Button btnNext;

    private Bundle data;

    private MapRoute mapRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crawl);

        //set the map
        MapFragment mapFrag = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);

        //set all other elements
        barImage = (ImageView)this.findViewById(R.id.imageViewBarPicture);
        barName = (TextView)this.findViewById(R.id.textViewBarName);
        barLocation = (TextView)this.findViewById(R.id.textViewBarLocation);
        btnNext = (Button)this.findViewById(R.id.btnNext);

        //button listener
        btnNext.setOnClickListener(this);

        //set the bundle to the bundle sent from generate activity
        data = getIntent().getExtras();

        Log.d("BUNDLE PONTOS", String.valueOf(data.getParcelableArrayList("pontos")));
//        Log.d("BUNDLE BARS", );

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapRoute = new MapRoute(googleMap,data.getStringArrayList("bars"), data.<LatLng>getParcelableArrayList("pontos"));

        mapRoute.getMap().clear();
        mapRoute.drawRoute();
        mapRoute.addWaypoints();
        mapRoute.moveCameraToWaypoint(0);
    }
}
