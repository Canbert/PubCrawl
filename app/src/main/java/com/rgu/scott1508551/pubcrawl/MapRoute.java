package com.rgu.scott1508551.pubcrawl;

import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapRoute {

    private GoogleMap map;
    private ArrayList bars;
    private List<LatLng> pontos;

    public MapRoute(GoogleMap map, ArrayList bars) {
        this.map = map;
        this.bars = bars;
    }

    public GoogleMap getMap() {
        return map;
    }

    public void setMap(GoogleMap map) {
        this.map = map;
    }

    public ArrayList getBars() {
        return bars;
    }

    public void setBars(ArrayList bars) {
        this.bars = bars;
    }

    public List<LatLng> getPontos() {
        return pontos;
    }

    public void setPontos(List<LatLng> pontos) {
        this.pontos = pontos;
    }

    public void addWaypoints(){

        for(int i = 0; i < bars.size(); i++){
            try {
                map.addMarker(new MarkerOptions()
                        .position(new LatLng(getBar(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat")
                                ,getBar(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng")))
                        .title(getBar(i).getString("name"))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
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

    public JSONObject getBar(int index){

        JSONObject bar = null;
        try {
            bar = new JSONObject(bars.get(index).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bar;
    }

    public void drawRoute(){
        for (int i = 0; i < pontos.size() - 1; i++) {
            LatLng src = pontos.get(i);
            LatLng dest = pontos.get(i + 1);

            //here is where it will draw the polyline in your map
            Polyline line = map.addPolyline(new PolylineOptions()
                    .add(new LatLng(src.latitude, src.longitude),
                            new LatLng(dest.latitude, dest.longitude))
                    .width(5).color(Color.RED).geodesic(true));
        }
    }

    public List<LatLng> decodePoly(String encoded) {

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
