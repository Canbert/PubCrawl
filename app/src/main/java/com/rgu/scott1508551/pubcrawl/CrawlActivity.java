package com.rgu.scott1508551.pubcrawl;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

public class CrawlActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap map;
    private ImageView barImage;
    private TextView barName;
    private TextView barLocation;
    private Button btnNext;

    private ProgressDialog pDialog;

    private Bundle data;

    private MapRoute mapRoute;

    private int currentBar;
    private Boolean started;

    private GoogleApiClient mGoogleApiClient;

    private DatabaseUtility db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crawl);

        //set the map
        MapFragment mapFrag = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);

        //set the database
        db = new DatabaseUtility(this);

        //set all other elements
        barImage = (ImageView)this.findViewById(R.id.imageViewBarPicture);
        barName = (TextView)this.findViewById(R.id.textViewBarName);
        barLocation = (TextView)this.findViewById(R.id.textViewBarLocation);
        btnNext = (Button)this.findViewById(R.id.btnNext);

        //button listener
        btnNext.setOnClickListener(this);

        currentBar = 0;
        started = false;

        //set the bundle to the bundle sent from generate activity
        data = getIntent().getExtras();

        Log.d("BUNDLE PONTOS", String.valueOf(data.getParcelableArrayList("pontos")));

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

    }

    @Override
    public void onClick(View v) {
        if(!started){
            btnNext.setText(R.string.crawl_next);
            started = true;
            mapRoute.moveCameraToWaypoint(currentBar);
        }
        else if(currentBar == mapRoute.getBars().size() - 1){
            btnNext.setText(R.string.crawl_finish);

            LayoutInflater li = LayoutInflater.from(this);
            final View promptView = li.inflate(R.layout.prompts,null);

            AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog);
            builder.setMessage("Save Crawl?");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final AlertDialog.Builder builder2 = new AlertDialog.Builder(CrawlActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog);
                    builder2.setTitle(R.string.save_crawl);
                    builder2.setMessage("");
                    builder2.setView(promptView);

                    final EditText input = (EditText)promptView.findViewById(R.id.editTextDialogUserInput);

                    builder2.setPositiveButton(android.R.string.ok, null);
                    builder2.setNegativeButton(android.R.string.cancel, null);

                    final AlertDialog saveDialog = builder2.create();

                    saveDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                        @Override
                        public void onShow(final DialogInterface dialog) {

                            Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                            button.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {
                                    Log.d("SAVE BARS", data.getStringArrayList("bars").toString());
                                    Log.d("SAVE PONTOS", data.getParcelableArrayList("pontos").toString());

                                    if(db.crawlExists(input.getText().toString())){
                                        ((AlertDialog) dialog).setMessage("Crawl name already used");
                                    }else{
                                        db.putCrawl(input.getText().toString(),
                                                data.getStringArrayList("bars").toString(),
                                                data.getParcelableArrayList("pontos").toString());
                                        //Dismiss once everything is OK.
                                        dialog.dismiss();
                                    }
                                }
                            });
                        }
                    });

                    saveDialog.show();
                }
            });
            builder.setNegativeButton("No", null);

            AlertDialog dialog = builder.create();
            dialog.show();

        }
        else{
            currentBar++;
            Log.d("CURRENT BAR", String.valueOf(currentBar));
            mapRoute.moveCameraToWaypoint(currentBar);
            setBarDetails();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapRoute = new MapRoute(googleMap,data.getStringArrayList("bars"), data.<LatLng>getParcelableArrayList("pontos"));

        Log.d("PONTS ARRAY", String.valueOf(data.<LatLng>getParcelableArrayList("pontos")));

        setBarDetails();

        mapRoute.getMap().clear();
        mapRoute.drawRoute();
        mapRoute.addWaypoints();
        mapRoute.moveCameraToWaypoint(currentBar);
    }

    private void setBarDetails(){
        try {
            barName.setText(mapRoute.getBar(currentBar).getString("name"));
            barLocation.setText(mapRoute.getBar(currentBar).getString("vicinity"));

            placePhotosAsync(mapRoute.getBar(currentBar).getString("place_id"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("CONNECTION", "FAILED");
    }

    private ResultCallback<PlacePhotoResult> mDisplayPhotoResultCallback
            = new ResultCallback<PlacePhotoResult>() {
        @Override
        public void onResult(PlacePhotoResult placePhotoResult) {
            if (!placePhotoResult.getStatus().isSuccess()) {
                return;
            }
            barImage.setImageBitmap(placePhotoResult.getBitmap());
        }
    };

    /**
     * Load a bitmap from the photos API asynchronously
     * by using buffers and result callbacks.
     */
    private void placePhotosAsync(String placeId) {
        Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {

                    @Override
                    public void onResult(PlacePhotoMetadataResult photos) {

                        if (!photos.getStatus().isSuccess()) {
                            Log.d("PHOTO", photos.getStatus().toString());
                            return;
                        }

                        PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                        if (photoMetadataBuffer.getCount() > 0) {
                            // Display the first bitmap in an ImageView in the size of the view
                            photoMetadataBuffer.get(0)
                                    .getScaledPhoto(mGoogleApiClient, barImage.getWidth(),
                                            barImage.getHeight())
                                    .setResultCallback(mDisplayPhotoResultCallback);
                        }
                        photoMetadataBuffer.release();
                    }
                });
    }
}
