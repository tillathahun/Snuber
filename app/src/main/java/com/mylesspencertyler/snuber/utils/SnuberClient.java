package com.mylesspencertyler.snuber.utils;

import android.util.Log;

import com.loopj.android.http.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by mspen on 2/27/2017.
 */

public class SnuberClient {
    public static void login(String username, String password, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("username", username);
        params.put("password", password);
        SnuberRestClient.post("api/login/", params, responseHandler);
    }

    public static void register(String firstName, String lastName, String email, String username, String password, ByteArrayInputStream avatar, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("first_name", firstName);
        params.put("last_name", lastName);
        params.put("email", email);
        params.put("username", username);
        params.put("password", password);
        params.put("avatar", avatar, username + ".jpg");
        SnuberRestClient.post("api/register/", params, responseHandler);
    }

    public static void updateLocation(double latitude, double longitude, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        SnuberRestClient.post("api/update-location/", params, responseHandler);
    }

    public static void requestRide(double destinationLatitude, double destinationLongitude, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        params.put("destination_latitude", destinationLatitude);
        params.put("destination_longitude", destinationLongitude);
        SnuberRestClient.post("ride-requests/api/request/", params, responseHandler);
    }

    public static void cancelRide(int rideId, AsyncHttpResponseHandler responseHandler) {
        SnuberRestClient.get("ride-requests/api/" + rideId + "/cancel/", null, responseHandler);
    }
}
