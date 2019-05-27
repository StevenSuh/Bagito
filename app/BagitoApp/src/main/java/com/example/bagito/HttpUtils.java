package com.example.bagito;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HttpUtils {

    private static final String BASE_URL = "https://bagito.appspot.com";
    private static ConnectivityManager connectivityManager;
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static String ERROR_MSG = "msg";

    static {
        client.setMaxRetriesAndTimeout(0, 1500);
        client.addHeader("Cache-Control", "no-cache");
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

//    public static void getByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
//        client.get(url, params, responseHandler);
//    }
//
//    public static void postByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
//        client.post(url, params, responseHandler);
//    }

    public static boolean isNetworkAvailable(Context context) {
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
