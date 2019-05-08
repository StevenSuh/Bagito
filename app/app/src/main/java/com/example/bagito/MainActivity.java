package com.example.bagito;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.bagito.login.LoginActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressView;
    private ListView listView;

    private SharedPreferences prefs;
    private boolean isLoggedIn;

    private boolean isLoadingList = false;
    private int index = 0;
    private String query = "";

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

        initActionBar();
        initNavbar(Enums.HOME_BUTTON.toString());

        Button mLogoutButton = findViewById(R.id.logout_button);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit().putBoolean(Enums.IS_LOGGED_IN.toString(), false).apply();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        getPartnerList();
    }

    public void showProgress(final boolean show) {
        isLoadingList = show;
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

//        listView.animate()
//                .setDuration(shortAnimTime)
//                .alpha(show ? 0 : 1)
//                .setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        listView.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
//                    }
//                });
//
//        progressView.animate()
//                .setDuration(shortAnimTime)
//                .alpha(show ? 1 : 0)
//                .setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        progressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
//                    }
//                });
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(Html.fromHtml("<b><font color=\"" + getResources().getColor(R.color.colorPrimary) + "\">" + getString(R.string.app_name) + "</font></b>"));
            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorLight)));
        }
    }

    private void initNavbar(String currentPage) {
        final LinearLayout homeButton = findViewById(R.id.home_button);
        final LinearLayout rentButton = findViewById(R.id.rent_button);
        final LinearLayout returnButton = findViewById(R.id.return_button);
        final LinearLayout accountButton = findViewById(R.id.account_button);

        homeButton.setAlpha(0.5f);
        rentButton.setAlpha(0.5f);
        returnButton.setAlpha(0.5f);
        accountButton.setAlpha(0.5f);

        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setAlpha(1);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.setAlpha(0.5f);
                }
                return true;
            }
        };

        homeButton.setOnTouchListener(onTouchListener);
        rentButton.setOnTouchListener(onTouchListener);
        returnButton.setOnTouchListener(onTouchListener);
        accountButton.setOnTouchListener(onTouchListener);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        rentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    Intent intent = new Intent(v.getContext(), RentActivity.class);
//                    startActivity(intent);
            }
        });
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    Intent intent = new Intent(v.getContext(), ReturnActivity.class);
//                    startActivity(intent);
            }
        });
        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    Intent intent = new Intent(v.getContext(), AccountActivity.class);
//                    startActivity(intent);
            }
        });

        if (currentPage.equals(Enums.HOME_BUTTON.toString())) {
            homeButton.setAlpha(1);
            homeButton.setOnTouchListener(null);
            homeButton.setOnClickListener(null);
        } else if (currentPage.equals(Enums.RENT_BUTTON.toString())) {
            rentButton.setAlpha(1);
            rentButton.setOnTouchListener(null);
            rentButton.setOnClickListener(null);
        } else if (currentPage.equals(Enums.RETURN_BUTTON.toString())) {
            returnButton.setAlpha(1);
            returnButton.setOnTouchListener(null);
            returnButton.setOnClickListener(null);
        } else if (currentPage.equals(Enums.ACCOUNT_BUTTON.toString())) {
            accountButton.setAlpha(1);
            accountButton.setOnTouchListener(null);
            accountButton.setOnClickListener(null);
        }
    }

    private void getPartnerList() {
        RequestParams rp = new RequestParams();
        rp.add("query", query);
        rp.add("index", Integer.toString(index));

        HttpUtils.post("/api/partner", rp, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                showProgress(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject data = response.getJSONObject("data");
                    successGetList(data);
                } catch (JSONException e) {
                    failureGetList();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                failureGetList();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                failureGetList();
            }

            @Override
            public void onFinish() {
                showProgress(false);
            }
        });
    }

    private void successGetList(JSONObject data) {
        // use array adapter to display data on listview
    }

    private void failureGetList() {
        // show "No Results" view
    }
}
