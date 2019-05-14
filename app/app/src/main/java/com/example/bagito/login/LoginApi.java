package com.example.bagito.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.bagito.DataHolder;
import com.example.bagito.Enums;
import com.example.bagito.HttpUtils;
import com.example.bagito.MainActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginApi {
    public static void executeLogin(final Context context, String email, String password) {
        RequestParams rp = new RequestParams();
        rp.add("email", email);
        rp.add("password", password);

        HttpUtils.post("/api/login", rp, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                ((LoginActivity) context).showProgress(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    int id = response.getInt(DataHolder.User.ID);
                    String name = response.getString(DataHolder.User.NAME);
                    String email = response.getString(DataHolder.User.EMAIL);
                    String city = response.getString(DataHolder.User.CITY);
                    String state = response.getString(DataHolder.User.STATE);

                    DataHolder.User user = new DataHolder.User(id, name, email, city, state);
                    successLogin(context, user);
                } catch (JSONException e) {
                    failureLogin(context, null);
                }
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
                ((LoginActivity) context).showProgress(false);
            }
        });
    }

    private static void successLogin(Context context, DataHolder.User user) {
        SharedPreferences prefs = context.getSharedPreferences(Enums.SHARED_PREFS.toString(), Context.MODE_PRIVATE);
        prefs.edit().putBoolean(Enums.IS_LOGGED_IN.toString(), true).apply();
        DataHolder.getInstance().setUser(user, prefs);

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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
