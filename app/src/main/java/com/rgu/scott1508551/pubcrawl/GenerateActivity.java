package com.rgu.scott1508551.pubcrawl;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

public class GenerateActivity extends FragmentActivity implements OnMapReadyCallback, SeekBar.OnSeekBarChangeListener {

    private GoogleMap map;
    private Button btnGenerate;
    private SeekBar seekBarPubs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate);
        MapFragment mapFrag = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);

        //assign the button, and seekbar
        btnGenerate = (Button)this.findViewById(R.id.btnCreate);
        seekBarPubs = (SeekBar)this.findViewById(R.id.seekBarPubs);

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
}
