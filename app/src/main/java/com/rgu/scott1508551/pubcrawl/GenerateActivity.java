package com.rgu.scott1508551.pubcrawl;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

public class GenerateActivity extends FragmentActivity implements OnMapReadyCallback, SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private GoogleMap map;
    private Button btnGenerate;
    private SeekBar seekBarPubs;
    private Bundle data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate);
        MapFragment mapFrag = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);

        //assign the button, and seekbar
        btnGenerate = (Button)this.findViewById(R.id.btnCreate);
        seekBarPubs = (SeekBar)this.findViewById(R.id.seekBarPubs);

        //set button on click listener
        btnGenerate.setOnClickListener(this);

        //set seekbar on change listener
        seekBarPubs.setOnSeekBarChangeListener(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.d("Num Pubs",String.valueOf(seekBarPubs.getProgress() + 1));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onClick(View v) {
        Intent in;
        data = new Bundle();

        //add the number of pubs to the bundle, add one so the minimum is 1 instead of 0
        data.putInt("numPubs",seekBarPubs.getProgress() + 1);
        //add the location that the camera is looking at, for generating pubs around that point
        data.putParcelable("LatLng", map.getCameraPosition().target);

        in = new Intent(this, EditCrawlActivity.class);
        in.putExtras(data);
        Log.d("Data Bundle", data.toString());
        startActivity(in);
    }
}
