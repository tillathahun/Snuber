package com.mylesspencertyler.snuber.activity;

import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.text.Text;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mylesspencertyler.snuber.R;
import com.mylesspencertyler.snuber.utils.SnuberClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import cz.msebera.android.httpclient.Header;


/**
 * Created by smbeaupre on 2/26/2017.
 */

public class StudentActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    private Button requestButton;
    private Button cancelButton;
    private TextView estimatedTimeLine;
    private EditText numberInputLine;
    private EditText nameInputLine;
    private double currentLatitude;
    private double currentLongitude;
    private double destLong;
    private double destLat;
    private boolean destinationExists;


    boolean mIsReceiverRegistered = false;

    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 113;


    private void handleNewLocation(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
//        Log.d("PrintLat", "My Lat: " + currentLatitude);
//        Log.d("PrintLong", "My Long: " + currentLongitude);
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("You are here");
        mMap.addMarker(options);
        if(destinationExists){
            LatLng destLatLng = new LatLng(destLat, destLong);
            Log.d("PrintLat", "Dest Lat: " + destLat);
            Log.d("PrintLong", "Dest Long: " + destLong);
            MarkerOptions destOptions = new MarkerOptions()
                    .position(destLatLng)
                    .title("Destination");
            mMap.addMarker(destOptions);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float)16.0));
        SnuberClient.updateLocation(currentLatitude, currentLongitude, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if(!response.getBoolean("success")) {
                        Toast toast = Toast.makeText(getBaseContext(), "Location Update Failed", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast toast = Toast.makeText(getBaseContext(), "Error updating location. Network error", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
    //Returns the estimated arrival time in minutes
    protected int calculateArrivalTime(){
        return 1;
    }

    //returns true if valid address on map, within radius, and if server got the request
    protected boolean validRequest(){
        Location wpiLoc = new Location("");
        wpiLoc.setLatitude(42.27384);
        wpiLoc.setLongitude(-71.807933);

        Location myLoc = new Location("");
        myLoc.setLatitude(currentLatitude);
        myLoc.setLongitude(currentLongitude);

        Location destLoc = new Location("");
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(numberInputLine.getText().toString() + " " + nameInputLine.getText().toString() + "Worcester, Massachusetts", 1);
            if(addresses.size() > 0) {
                destLong = addresses.get(0).getLongitude();
                destLat = addresses.get(0).getLatitude();
                destLoc.setLongitude(destLong);
                destLoc.setLatitude(destLat);
            }
            else return false;
        }catch (IOException e){}

        if(((myLoc.distanceTo(wpiLoc) < 1609) &&
                ((nameInputLine.getText().toString().equals("Washington Square") && numberInputLine.getText().toString().equals("2")) || (destLoc.distanceTo(wpiLoc) < 1609))) &&
                serverRecievedRequest()){ //if distance is less than one mile(in meters) and same for dest or if address is for union station then pass if server gets it
            return true;
        }
        else return false;
    }

    protected boolean serverRecievedRequest(){ //returns true if the server gets the request properly
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        requestButton = (Button)findViewById(R.id.requestButton);
        requestButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                if(validRequest()){  //if Request is successful
                    requestButton.setEnabled(false);
                    numberInputLine.setEnabled(false);
                    nameInputLine.setEnabled(false);
                    estimatedTimeLine.setText("Estimated Arrival Time: " + calculateArrivalTime() + " Minutes");
                    destinationExists = true;
                    LatLng latLng = new LatLng(destLat, destLong);
                    Log.d("PrintLat", "Lat: " + destLat);
                    Log.d("PrintLong", "Long: " + destLong);
                    MarkerOptions options = new MarkerOptions()
                            .position(latLng)
                            .title("Destination");
                    mMap.addMarker(options);
                }
            }
        });
        cancelButton =(Button)findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                destinationExists = false;
                requestButton.setEnabled(true);
                numberInputLine.setEnabled(true);
                numberInputLine.setText("Street #");
                nameInputLine.setEnabled(true);
                nameInputLine.setText("Street Name");
                estimatedTimeLine.setText("No Ride Requested Yet");
            }
        });
        estimatedTimeLine = (TextView) findViewById(R.id.estimatedTimeLine);
        estimatedTimeLine.setText("No Ride Requested Yet");
        numberInputLine = (EditText) findViewById(R.id.numberInputLine);
        nameInputLine = (EditText) findViewById(R.id.nameInputLine);
        // Head
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(0)
                .setMaxWaitTime(1)
                .setFastestInterval(0)
                .setSmallestDisplacement(0);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //setUpMap();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                //e.printStackTrace();
            }
        } else {
            //Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1600);
        } else {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            //if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            //}

            handleNewLocation(location);
        }
    }
    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    //if (location == null) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                    //}

                    handleNewLocation(location);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        MarkerOptions options = new MarkerOptions()
                .position(new LatLng(0,0))
                .title("I am here!");
    }


    @Override
    public void onLocationChanged(Location location) {
        mMap.clear();
        handleNewLocation(location);
    }






}
