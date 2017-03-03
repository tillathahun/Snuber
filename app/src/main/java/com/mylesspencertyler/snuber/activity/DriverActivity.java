package com.mylesspencertyler.snuber.activity;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mylesspencertyler.snuber.R;
import com.mylesspencertyler.snuber.utils.SnuberClient;
import com.mylesspencertyler.snuber.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

import cz.msebera.android.httpclient.Header;

/**
 * Created by smbeaupre on 2/26/2017.
 */

public class DriverActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    private Button switchActivityButton;
    private TextView destinationLine;
    private String destAddress;
    private double destLat;
    private double destLong;
    private boolean hasDest;
    private Button nextDestinationButton;
    private double currentLatitude;
    private double currentLongitude;
    boolean mIsReceiverRegistered = false;
    private boolean goingToStart;

    private ImageView profileIcon;
    private TextView profileName;

    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 112;


    private void handleNewLocation(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        //here we should call to the database to see if the driver is close to the destination of user
        // with a snuber client call... on backend, if the driver is close to the student, fire a notification driver has arrived

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        Log.d("PrintLat", "Lat: " + currentLatitude);
        Log.d("PrintLong", "Long: " + currentLongitude);
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("You are here");
        mMap.addMarker(options);
        if(hasDest){
            LatLng destLatLng = new LatLng(destLat, destLong);
            MarkerOptions destOptions = new MarkerOptions()
                    .position(destLatLng)
                    .title("Destination");
            mMap.addMarker(destOptions);
        }
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float) 16.0));
        String refreshToken = Utils.readSharedSetting(this, LoginActivity.PREF_APP_FIREBASE_TOKEN, "");
        SnuberClient.updateLocation(currentLatitude, currentLongitude, refreshToken, new JsonHttpResponseHandler() {
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

        SnuberClient.getDriverDetails(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if(response.getBoolean("success")) {
                        JSONObject ride = response.getJSONObject("ride");
                        if(ride.getBoolean("is_queued")) {
                            hasDest = true;
                            destLat = ride.getDouble("latitude");
                            destLong = ride.getDouble("longitude");
                            profileName.setText(ride.getString("user_name"));
                            try {
                                URL url = new URL(ride.getString("user_image"));
                                Log.d("Snuber", url.getPath());
//                                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inSampleSize = 2;
                                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);
                                profileIcon.setImageBitmap(bitmap);
                            } catch (java.io.IOException e) {
                                e.printStackTrace();
                                Log.e("Snuber Networking", "Problem getting user image");
                            }
                            String destLine = "";
                            switch (ride.getString("status")) {
                                case "AC":
                                    destLine = "Ride Accepted";
                                    break;
                                case "EN":
                                    destLine = "Going to Student";
                                    break;
                                case "IN":
                                    destLine = "Going to Student's Destination";
                                    break;
                                case "CN":
                                    destLine = "Ride Canceled";
                                default:
                                    destLine = "";
                            }
                            destinationLine.setText(destLine);
                        } else {
                            hasDest = false;
                            profileName.setText("No student queued");
                            destinationLine.setText("No destination currently");
                            profileIcon.setImageResource(R.drawable.ic_account_circle_24px);
                        }
                    } else {
                        Toast toast = Toast.makeText(getBaseContext(), "Unable to get user details", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast toast = Toast.makeText(getBaseContext(), "Error getting user details. Network error", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
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

        destinationLine = (TextView) findViewById(R.id.destinationLine);
        hasDest = false;

        switchActivityButton = (Button)findViewById(R.id.switchActivityButton);
        switchActivityButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Switch to student activity
                startActivity(new Intent(DriverActivity.this, StudentActivity.class));

            }
        });

        profileIcon = (ImageView) findViewById(R.id.list_icon);
        profileName = (TextView) findViewById(R.id.pickupStudentName_EditText);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
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
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        } else {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            //if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            //}

            handleNewLocation(location);
        }
    }

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
    public void onConnectionSuspended(int i) {}

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
