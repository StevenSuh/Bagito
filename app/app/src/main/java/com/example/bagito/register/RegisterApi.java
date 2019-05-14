package com.example.bagito.register;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.bagito.Enums;
import com.example.bagito.HttpUtils;
import com.example.bagito.MainActivity;
import com.example.bagito.login.LoginActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class RegisterApi {
    public static void executeRegister(final Context context, String name, String email, String password, String city, String state) {
        RequestParams rp = new RequestParams();
        rp.add("name", name);
        rp.add("email", email);
        rp.add("password", password);
        rp.add("city", city);
        rp.add("state", state);

        HttpUtils.post("/api/register", rp, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                ((RegisterActivity) context).showProgress(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                successLogin(context);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                failureLogin(context, null);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse == null) {
                    failureLogin(context, null);
                    return;
                }

                try {
                    String message = errorResponse.getString(HttpUtils.ERROR_MSG);
                    failureLogin(context, message);
                } catch (JSONException e) {
                    failureLogin(context, null);
                }
            }

            @Override
            public void onFinish() {
                ((RegisterActivity) context).showProgress(false);
            }
        });
    }

    private static void successLogin(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Enums.SHARED_PREFS.toString(), Context.MODE_PRIVATE);
        prefs.edit().putBoolean(Enums.IS_LOGGED_IN.toString(), true).apply();

        Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show();

        try {
            // fake delay
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    private static void failureLogin(Context context, String error) {
        if (TextUtils.isEmpty(error)) {
            error = "Failed server response";
        }

        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
    }

}
