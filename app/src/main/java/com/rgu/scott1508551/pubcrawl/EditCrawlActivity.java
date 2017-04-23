package com.rgu.scott1508551.pubcrawl;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EditCrawlActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private Bundle data;

    private ListView barsList;
    private ArrayAdapter adapter;
    private Button btnSave;
    private Button btnStart;
    private ProgressDialog pDialog;

    private String stringUrl;

    private MapRoute mapRoute;

    private DatabaseUtility db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_crawl);

        MapFragment mapFrag = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);

        //set the bars list
        barsList = (ListView)this.findViewById(R.id.listViewPubs);

        //set the database
        db = new DatabaseUtility(this);

        //set the buttons
        btnSave = (Button)this.findViewById(R.id.btnSaveCrawl);
        btnStart = (Button)this.findViewById(R.id.btnStartCrawl);

        //set button on click listeners
        btnSave.setOnClickListener(this);
        btnStart.setOnClickListener(this);

        //set the bundle to the bundle sent from generate activity
        data = getIntent().getExtras();
        Log.d("Edit Crawl Bundle", data.toString());

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mapRoute = new MapRoute(googleMap,data.getStringArrayList("bars"));

        // bars list set the array for it
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,barsToArrayList());
        barsList.setAdapter(adapter);

        //set the start end end points
        String origin = null;
        String destination = null;
        try {
            origin = mapRoute.getBar(0).getString("place_id");
            destination = mapRoute.getBar(mapRoute.getBars().size() - 1).getString("place_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("ORIGIN",origin.toString());
        Log.d("DESTINATION", destination.toString());

        stringUrl = "https://maps.googleapis.com/maps/api/directions/json?"
                + "origin=place_id:" + origin
                + "&destination=place_id:" + destination
                + "&waypoints=optimize:true|" + waypointsString()
                + "&key=" + getResources().getString(R.string.google_maps_key);

        Log.d("URL",stringUrl);

        new GetDirection().execute();
    }

    private String waypointsString(){
        String str = "";

        // start after the first and stop before the last
        try {
            for (int i = 1; i < mapRoute.getBars().size() - 1; i++) {
                if (i == mapRoute.getBars().size() - 1) {
                        str += "place_id:" + mapRoute.getBar(i).getString("place_id");

                } else {
                    str += "place_id:" + mapRoute.getBar(i).getString("place_id") + "|";
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return str;
    }

    private ArrayList barsToArrayList(){

        ArrayList arrayList = new ArrayList();

        for(int i = 0; i < mapRoute.getBars().size(); i++){
            try {
                arrayList.add(mapRoute.getBar(i).getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return arrayList;
    }



    @Override
    public void onClick(View v) {

        Intent in;

        LayoutInflater li = LayoutInflater.from(this);
        View promptView = li.inflate(R.layout.prompts,null);

        if(v.getId() == R.id.btnSaveCrawl){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog);// R.style.AppTheme
            builder.setTitle(R.string.save_crawl);
            builder.setView(promptView);

            final EditText input = (EditText)promptView.findViewById(R.id.editTextDialogUserInput);

            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    db.putCrawl(input.getText().toString(),data.getStringArrayList("bars").toString());
                }
            });

            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = builder.create();
//            dialog.getWindow().setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
            dialog.show();


        } else{
            data.putParcelableArrayList("pontos", (ArrayList<? extends Parcelable>) mapRoute.getPontos());

            in = new Intent(EditCrawlActivity.this, CrawlActivity.class);
            in.putExtras(data);
            startActivity(in);
        }
    }

    class GetDirection extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditCrawlActivity.this);
            pDialog.setMessage("Drawing the route, please wait!");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            StringBuilder response = new StringBuilder();
            try {
                URL url = new URL(stringUrl);
                HttpURLConnection httpconn = (HttpURLConnection) url
                        .openConnection();
                if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader input = new BufferedReader(
                            new InputStreamReader(httpconn.getInputStream()),
                            8192);
                    String strLine = null;

                    while ((strLine = input.readLine()) != null) {
                        response.append(strLine);
                    }
                    input.close();
                }

                String jsonOutput = response.toString();

                JSONObject jsonObject = new JSONObject(jsonOutput);

                // routesArray contains ALL routes
                JSONArray routesArray = jsonObject.getJSONArray("routes");
                // Grab the first route
                JSONObject route = routesArray.getJSONObject(0);

                JSONObject poly = route.getJSONObject("overview_polyline");
                String polyline = poly.getString("points");
                mapRoute.setPontos(mapRoute.decodePoly(polyline));

            } catch (Exception e) {

            }

            return null;

        }

        protected void onPostExecute(String file_url) {
            try{

                mapRoute.getMap().clear();
                mapRoute.drawRoute();
                mapRoute.addWaypoints();
                mapRoute.moveCameraToWaypoint(0);

            }catch(NullPointerException e){
                Log.e("Error", "NullPointerException onPostExecute: " + e.toString());
            }catch (Exception e2) {
                Log.e("Error", "Exception onPostExecute: " + e2.toString());
            }
            pDialog.dismiss();

        }
    }
}
