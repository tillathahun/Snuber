package com.mylesspencertyler.snuber.utils;

/**
 * Created by mspen on 2/27/2017.
 */

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.*;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SnuberRestClient {
    private static final String BASE_URL = "http://snuber.herokuapp.com/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void setup(Context context) {
        PersistentCookieStore store = new PersistentCookieStore(context);
        SnuberRestClient.client.setCookieStore(store);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(final String url, final RequestParams params, final AsyncHttpResponseHandler responseHandler) {
        client.get(BASE_URL + "api/csrf-token/", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String csrfToken = response.getString("csrf_token");
//                    Log.d("Snuber Networking", csrfToken);
                    params.put("csrfmiddlewaretoken", csrfToken);
                    client.post(getAbsoluteUrl(url), params, responseHandler);
                } catch (JSONException e) {
                    Log.e("Snuber Networking", "Exception");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("Snuber Networking", "Failed to retreive csrf token: " + responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Log.e("Snuber Networking", "Failed to retreive csrf token. status: " + statusCode);
            }
        });
    }

    public static void getAddressFromName(String name, AsyncHttpResponseHandler responseHandler) {
        name = name.replace(' ', '+');
        client.get("https://maps.googleapis.com/maps/api/geocode/json?address=" + name + "&key=AIzaSyDN_xRHr3a70KT6psPnwSoiSPBYPXBXPMg", null, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
