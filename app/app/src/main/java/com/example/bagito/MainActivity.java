package com.example.bagito;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = getSharedPreferences(Enums.SHARED_PREFS.toString(), MODE_PRIVATE);
        isLoggedIn = prefs.getBoolean(Enums.IS_LOGGED_IN.toString(), false);

        if (!isLoggedIn) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setActionBarColor();

        Button mLogoutButton = findViewById(R.id.logout_button);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit().putBoolean(Enums.IS_LOGGED_IN.toString(), false).apply();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    public void setActionBarColor() {
        ActionBar actionBar = getSupportActionBar();
        System.out.println(actionBar);

        if (actionBar != null) {
            actionBar.setTitle(Html.fromHtml("<b><font color=\"" + getResources().getColor(R.color.colorPrimary) + "\">" + getString(R.string.app_name) + "</font></b>"));
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorLight)));
        }
    }
}
