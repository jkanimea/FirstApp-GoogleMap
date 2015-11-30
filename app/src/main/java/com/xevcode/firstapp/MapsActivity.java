package com.xevcode.firstapp;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import org.w3c.dom.Document;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    GMapV2GetRouteDirection v2GetRouteDirection;

    private GoogleApiClient client;

    LatLng Point1 = new LatLng(-36.889514, 174.825917);
    LatLng Point2 = new LatLng(-36.885735, 174.762360);
    Document document;
    GoogleMap  mGoogleMap;

   //Async is a separate thread used for short operation, in this example it used to do some task
   // like getting the route from point A to point B,
   // display a msg box when route is being calculated
   //More details on AsyncTask http://developer.android.com/reference/android/os/AsyncTask.html
    private class GetRouteTask extends AsyncTask<String, Void, String> {

        private ProgressDialog Dialog;
        String response = "";
        //PreExecute void run at the beginning of the thread
        @Override
        protected void onPreExecute() {

        }
        //doInBackground runs after PreExecute in the background
        @Override
        protected String doInBackground(String... urls) {
            //Get All Route values and put this in DOM document
            document = v2GetRouteDirection
                       .getDocument(Point1, Point2, GMapV2GetRouteDirection.MODE_DRIVING);
            response = "Success";
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
         //check if response is success from doInBackground
            if(response.equalsIgnoreCase("Success")){
                ArrayList<LatLng> directionPoint = v2GetRouteDirection.getDirection(document);
                PolylineOptions rectLine = new PolylineOptions().width(10).color(
                        Color.GREEN);

                for (int i = 0; i < directionPoint.size(); i++) {
                    rectLine.add(directionPoint.get(i));
                }
                // Adding route on the map
                mGoogleMap.addPolyline(rectLine);
            }

        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
       // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        //Object to calculate the route between two points
         v2GetRouteDirection = new GMapV2GetRouteDirection();

        //Setup googleMap before use
        googleMap.clear();
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.setTrafficEnabled(false);

       //Google map use marker to mark a location. Here we mark 2 location m1 and m2
        Marker marker1 = googleMap.addMarker(new MarkerOptions()
                .position(Point1)
                .title("Start")
                .snippet("Point 1")
                .visible(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        Marker marker2 = googleMap.addMarker(new MarkerOptions()
                .position(Point2)
                .anchor(0.5f, 0.5f)
                .title("End")
                .snippet("Point 2")
                .visible(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));

        //Before u can animate u must set initial position for camera to animate from ..
        LatLngBounds.Builder LatLngB = new LatLngBounds.Builder();

        LatLngB.include(marker1.getPosition());
        LatLngB.include(marker2.getPosition());

        LatLngBounds bounds = LatLngB.build();

       //copy googleMap to global variable mGoogleMap as we are goint to use this in GetRouteTask
        mGoogleMap = googleMap;
      //get route between two paths
        new GetRouteTask().execute();

        //bounds 565 565 20 -> refer to length, width, padding. Length, Width is frame for camera,
        //padding is for clarity in images
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 665, 665, 5);
        //show the locations on google map
        googleMap.animateCamera(cu);

    }



    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.xevcode.firstapp/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.xevcode.firstapp/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}

