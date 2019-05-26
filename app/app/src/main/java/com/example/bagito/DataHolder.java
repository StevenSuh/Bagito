package com.example.bagito;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.bagito.login.LoginActivity;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class DataHolder {

    private User user = null;


    public static class User {

        public static String ID = "user_id";
        public static String NAME = "user_name";
        public static String EMAIL= "user_email";
        public static String CITY = "user_city";
        public static String STATE = "user_state";
        public static String HAS_PAYMENT = "user_has_payment";

        public int id;
        public String name;
        public String email;
        public String city;
        public String state;
        public boolean hasPayment;

        public User(int id,
                    String name,
                    String email,
                    String city,
                    String state,
                    boolean hasPayment) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.city = city;
            this.state = state;
            this.hasPayment = hasPayment;
        }
    }

    private static final DataHolder dataHolder = new DataHolder();

    public static DataHolder getInstance() {
        return dataHolder;
    }

    public void setUser(User user, SharedPreferences prefs) {
        this.user = user;
        prefs.edit()
            .putInt(User.ID, user.id)
            .putString(User.NAME, user.name)
            .putString(User.EMAIL, user.email)
            .putString(User.CITY, user.city)
            .putString(User.STATE, user.state)
            .putBoolean(User.HAS_PAYMENT, user.hasPayment)
            .apply();
    }

    public User getUser() {
        return user;
    }

    public static void initDataHolder(final Context context, final Runnable callback) {
        if (!HttpUtils.isNetworkAvailable(context)) {
            Toast.makeText(context, "No internet", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dataHolder.getUser() == null) {
            SharedPreferences prefs = context.getSharedPreferences(Enums.SHARED_PREFS.toString(), Context.MODE_PRIVATE);

            int id = prefs.getInt(User.ID, -1);
            if (id == -1) {
                initFail(context, prefs);
                return;
            }

            String name = prefs.getString(User.NAME, "");
            String email = prefs.getString(User.EMAIL, "");
            String city = prefs.getString(User.CITY, "");
            String state = prefs.getString(User.STATE, "");
            boolean hasPayment = prefs.getBoolean(User.HAS_PAYMENT, false);

            User user = new DataHolder.User(id, name, email, city, state, hasPayment);

            initSuccess(user, prefs);
        }
        callback.run();
    }

    private static void initSuccess(User user, SharedPreferences prefs) {
        dataHolder.setUser(user, prefs);
    }

    private static void initFail(Context context, SharedPreferences prefs) {
        Utils.nukePrefs(prefs);
        Toast.makeText(context, "Session expired", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
