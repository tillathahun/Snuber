package com.mylesspencertyler.snuber.utils;

/**
 * Created by mspen on 2/27/2017.
 */

import com.loopj.android.http.*;

public class SnuberRestClient {
    private static final String BASE_URL = "http://192.168.1.100:8000/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
