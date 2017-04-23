package com.rgu.scott1508551.pubcrawl;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GenerateActivity extends FragmentActivity implements OnMapReadyCallback, SeekBar.OnSeekBarChangeListener, View.OnClickListener, GoogleMap.OnCameraMoveListener {

    private GoogleMap map;
    private int numPubs;
    private Button btnGenerate;
    private SeekBar seekBarPubs;
    private Bundle data;

    private ProgressDialog pDialog;
    private AlertDialog errorDialog;
    private String url;

    private JSONArray results;
    private ArrayList bars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate);
        MapFragment mapFrag = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);

        //assign the button, and seekbar
        btnGenerate = (Button)this.findViewById(R.id.btnCreate);
        seekBarPubs = (SeekBar)this.findViewById(R.id.seekBarPubs);

        bars = new ArrayList();

        //set the num pubs
        setNumPubs();

        //set button on click listener
        btnGenerate.setOnClickListener(this);

        //set seekbar on change listener
        seekBarPubs.setOnSeekBarChangeListener(this);

    }

    private void setNumPubs(){
        //set the number of pubs, add one so the minimum is 1 instead of 0
        numPubs = seekBarPubs.getProgress() + 1;
        ((TextView)this.findViewById(R.id.textViewNumPubs)).setText(String.valueOf(seekBarPubs.getProgress() + 1));
    }

    //set a marker to the center of the map
    private void updatePositionMarker(){
        //clear the map of markers
        map.clear();
        //add the centered marker
        map.addMarker(new MarkerOptions().position(map.getCameraPosition().target));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //set map camera movement listener
        map.setOnCameraMoveListener(this);

        //add the centered marker
        updatePositionMarker();
    }

    @Override
    public void onCameraMove() {
        updatePositionMarker();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        setNumPubs();
        Log.d("Num Pubs",String.valueOf(seekBarPubs.getProgress() + 1));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onClick(View v) {
        url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
                + "location=" + map.getCameraPosition().target.latitude + "," +  map.getCameraPosition().target.longitude
                + "&radius=2000"
                + "&types=bar"
                + "&key=" + getResources().getString(R.string.google_maps_key);


        new GetPubs().execute();
    }

    private class GetPubs extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(GenerateActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.d("JSON Response", "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    results = jsonObj.getJSONArray("results");

                    Log.d("JSON RESULTS",results.toString());
                }
                catch (final JSONException e) {
                    Log.d("JSON", "Json parsing error: " + e.getMessage());
                }
            }
            else{
                Log.d("JSON","Couldn't get json");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            Intent in;
            data = new Bundle();

            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            if(results.length() > 0){
                Log.d("JSON","NOT EMPTY");

                try {
                    JSONArray json = new JSONArray(results.toString());

                    Log.d("JSON ARRAY", json.toString());

                    for(int i = 0;i<numPubs;i++){
                        JSONObject bar = json.getJSONObject(i);

                        Log.d("JSON OBJ", bar.toString());

                        bars.add(bar);

                        Log.d("JSON PLACEID", bar.getString("place_id"));
                    }

                    data.putStringArrayList("bars",bars);

                    Log.d("BARS ARRAY", bars.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                in = new Intent(GenerateActivity.this, EditCrawlActivity.class);
                in.putExtras(data);
                Log.d("Data Bundle", data.toString());
                startActivity(in);
            }
            else{
                errorDialog = new AlertDialog.Builder(GenerateActivity.this, R.style.AppTheme).create();
                errorDialog.setTitle("No Bars Found");
                errorDialog.setMessage("Please Try Again");
                errorDialog.getWindow().setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
                errorDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                errorDialog.show();
                Log.d("JSON","EMPTY");
            }

            Log.d("JSON","DONE TASK");
        }
    }
}
