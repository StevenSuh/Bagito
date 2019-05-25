package com.example.bagito.Account;

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

public class ChangeApi {

    public static void executeChange(final Context context, String name, String email, String password, String cardNumber, String monthYear, String cvv){

        RequestParams rp = new RequestParams();
        rp.add("name", name);
        rp.add("email", email);
        rp.add("password", password);



    }







}
