package com.rgu.scott1508551.pubcrawl;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
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

public class EditCrawlActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;

    private Bundle data;
    private ArrayList bars;

    private ProgressDialog pDialog;

    private String stringUrl;

    private List<LatLng> pontos;

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
        String origin = null;
        String destination = null;
        try {
            origin = getBar(0).getString("place_id");
            destination = getBar(bars.size() - 1).getString("place_id");
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


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        new GetDirection().execute();
    }

    private JSONObject getBar(int index){

        JSONObject bar = null;
        try {
            bar = new JSONObject(bars.get(index).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bar;
    }

    private String waypointsString(){
        String str = "";

        // start after the first and stop before the last
        try {
            for (int i = 1; i < bars.size() - 1; i++) {
                if (i == bars.size() - 1) {
                        str += "place_id:" + getBar(i).getString("place_id");

                } else {
                    str += "place_id:" + getBar(i).getString("place_id") + "|";
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return str;
    }

    private void addWapointsToMap(){

        for(int i = 0; i < bars.size(); i++){
            try {
                map.addMarker(new MarkerOptions()
                        .position(new LatLng(getBar(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat")
                                ,getBar(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng")))
                        .title(getBar(i).getString("name"))
                );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        CameraUpdate center = null;
        try {
            center = CameraUpdateFactory.newLatLng(
                    new LatLng(getBar(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat")
                    ,getBar(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(13);

        map.moveCamera(center);
        map.animateCamera(zoom);
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
                pontos = decodePoly(polyline);

            } catch (Exception e) {

            }

            return null;

        }

        protected void onPostExecute(String file_url) {
            for (int i = 0; i < pontos.size() - 1; i++) {
                LatLng src = pontos.get(i);
                LatLng dest = pontos.get(i + 1);
                try{
                    //here is where it will draw the polyline in your map
                    Polyline line = map.addPolyline(new PolylineOptions()
                            .add(new LatLng(src.latitude, src.longitude),
                                    new LatLng(dest.latitude,                dest.longitude))
                            .width(2).color(Color.RED).geodesic(true));

                    addWapointsToMap();

                }catch(NullPointerException e){
                    Log.e("Error", "NullPointerException onPostExecute: " + e.toString());
                }catch (Exception e2) {
                    Log.e("Error", "Exception onPostExecute: " + e2.toString());
                }

            }
            pDialog.dismiss();

        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}
