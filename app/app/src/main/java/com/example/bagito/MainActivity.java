package com.example.bagito;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bagito.login.LoginActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private View wrapperView;
    private ProgressBar progressView;

    private TextView nearText;
    private View modalBg;
    private View modal;
    private EditText filterCity;
    private AutoCompleteTextView filterState;

    private PartnerAdapter partnerAdapter;

    private SharedPreferences prefs;
    private boolean isLoggedIn;

    private boolean isLoadingList = false;
    private String query = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(Enums.SHARED_PREFS.toString(), MODE_PRIVATE);
        isLoggedIn = prefs.getBoolean(Enums.IS_LOGGED_IN.toString(), false);

        if (!isLoggedIn) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        ArrayList<PartnerItem> partners = new ArrayList<>();
        partnerAdapter = new PartnerAdapter(this, partners);

        ListView listView = findViewById(R.id.main_list);
        listView.setAdapter(partnerAdapter);
        listView.setScrollContainer(false);

        wrapperView = findViewById(R.id.main_wrapper);
        progressView = findViewById(R.id.progress);

        LinearLayout homeButton = findViewById(R.id.home_button);
        LinearLayout rentButton = findViewById(R.id.rent_button);
        LinearLayout returnButton = findViewById(R.id.return_button);
        LinearLayout accountButton = findViewById(R.id.account_button);
        LinearLayout aboutButton = findViewById(R.id.account_button);

        Utils.initNavbar(this,
                Enums.HOME_BUTTON.toString(),
                homeButton,
                rentButton,
                returnButton,
                accountButton,
                aboutButton);

        initModal();
        DataHolder.initDataHolder(this, new Runnable() {
            @Override
            public void run() {
                DataHolder.User user = DataHolder.getInstance().getUser();
                query = user.city + ", " + user.state;

                if (TextUtils.isEmpty(user.city) || TextUtils.isEmpty(user.state)) {
                    query = "Santa Cruz, CA";
                }

                getPartnerList();
            }
        });
    }

    private void getPartnerList() {
        nearText.setText("Near " + query);
        findViewById(R.id.empty_list).setVisibility(View.GONE);

        RequestParams rp = new RequestParams();
        rp.add("query", query);

        HttpUtils.get("/api/partner", rp, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                showProgress(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray data = response.getJSONArray("data");
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

    private void successGetList(JSONArray data) {
        int len = data.length();

        if (len == 0) {
            findViewById(R.id.empty_list).setVisibility(View.VISIBLE);
            return;
        }

        try {
            for (int i = 0; i < len; i++) {
                JSONObject item = data.getJSONObject(i);

                String name = item.getString("name");
                String address = item.getString("address");
                String hours = item.getString("hours");
                String mapLink = item.getString("map_link");

                PartnerItem partnerItem = new PartnerItem(name, address, hours, mapLink);
                partnerAdapter.add(partnerItem);
            }
        } catch (JSONException e) {
            failureGetList();
            partnerAdapter.clear();
        }
    }

    private void failureGetList() {
        Toast.makeText(this, "Server error", Toast.LENGTH_SHORT).show();
    }

    public void showProgress(final boolean show) {
        isLoadingList = show;
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        wrapperView.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 0 : 1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        wrapperView.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
                    }
                });

        progressView.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        progressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                    }
                });
    }

    public class PartnerAdapter extends ArrayAdapter<PartnerItem> {
        public PartnerAdapter(Context context, ArrayList<PartnerItem> partners) {
            super(context, 0, partners);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final PartnerItem partnerItem = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.partner_list_item, parent, false);
            }

            TextView partnerName = convertView.findViewById(R.id.partner_name);
            TextView partnerAddress = convertView.findViewById(R.id.partner_address);
            TextView partnerHours = convertView.findViewById(R.id.partner_hours);

            partnerName.setText(partnerItem.name);
            partnerAddress.setText(partnerItem.address);
            partnerHours.setText(partnerItem.hours);

            Utils.setTouchEffect(convertView, true, true);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(partnerItem.mapLink));
                    startActivity(i);
                }
            });

            return convertView;
        }
    }

    public class PartnerItem {
        public String name;
        public String address;
        public String hours;
        public String mapLink;

        public PartnerItem(String name, String address, String hours, String mapLink) {
            this.name = name;
            this.address = address;
            this.hours = hours;
            this.mapLink = mapLink;
        }
    }

    private void initModal() {
        modalBg = findViewById(R.id.modal_bg);
        modal = findViewById(R.id.modal);
        nearText = findViewById(R.id.near_text);
        filterCity = findViewById(R.id.filter_city);
        filterState = findViewById(R.id.filter_state);

        final String[] states = getResources().getStringArray(R.array.states);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, states);

        filterState.setAdapter(adapter);
        filterState.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    filterState.showDropDown();
                }
            }
        });

        View filterSubmit = findViewById(R.id.filter_submit_button);
        Utils.setTouchEffect(filterSubmit, true, true);
        filterSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterState.setError(null);

                String city = filterCity.getText().toString();
                String state = filterState.getText().toString();
                query = city + ", " + state;

                if (TextUtils.isEmpty(city) || TextUtils.isEmpty(state)) {
                    query = "Santa Cruz, CA";
                }

                if (!TextUtils.isEmpty(state) && !Arrays.asList(states).contains(state)) {
                    filterState.setError("Invalid state");
                    return;
                }

                modalBg.animate()
                        .setDuration(200)
                        .alpha(0);
                modal.animate()
                        .setDuration(200)
                        .alpha(0)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                modal.setVisibility(View.GONE);
                            }
                        });
                partnerAdapter.clear();
                getPartnerList();
            }
        });

        TextView filterButton = findViewById(R.id.main_list_filter);
        Utils.setTouchEffect(filterButton, true, true);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataHolder.User user = DataHolder.getInstance().getUser();

                filterCity.setText(user.city);
                filterState.setText(user.state);

                modalBg.animate()
                        .setDuration(200)
                        .alpha(0.5f);
                modal.animate()
                        .setDuration(200)
                        .alpha(1)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                modal.setVisibility(View.VISIBLE);
                            }
                        });
            }
        });
    }
}
