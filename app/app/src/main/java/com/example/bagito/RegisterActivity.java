package com.example.bagito;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class RegisterActivity extends AppCompatActivity {

    private UserRegisterTask mRegisterTask = null;

    private ImageView mBack;
    private EditText mNameView;
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private EditText mCityView;
    private EditText mStateView;

    private RelativeLayout mRegisterButton;
    private TextView mRegisterTextView;
    private ProgressBar mRegisterProgressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mBack = findViewById(R.id.back);
        mBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        mBack.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setAlpha(0.5f);
                } else {
                    v.setAlpha(1f);
                }
                return false;
            }
        });

        mNameView = findViewById(R.id.name);
        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        mConfirmPasswordView = findViewById(R.id.confirm_password);
        mCityView = findViewById(R.id.city);

        mStateView = findViewById(R.id.state);
        mStateView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });
    }

    private void attemptRegister() {
        if (mRegisterTask != null) {
            return;
        }

        mNameView.setError(null);

        String name = mNameView.getText().toString();
        String email = mEmailView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError("This field is required");
            focusView = mEmailView;
            cancel = true;
        } else if (!Utils.isEmailValid(email)) {
            mEmailView.setError("This email address is invalid");
            focusView = mEmailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(name)) {
            mNameView.setError("This field is required");
            focusView = mNameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mName;
        private final String mEmail;
        private final String mPassword;
        private final String mCity;
        private final String mState;

        UserRegisterTask(String name, String email, String password, String city, String state) {
            mName = name;
            mEmail = email;
            mPassword = password;
            mCity = city;
            mState = state;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            RequestParams rp = new RequestParams();
            rp.add("name", mName);
            rp.add("email", mEmail);
            rp.add("password", mPassword);
            rp.add("city", mCity);
            rp.add("state", mState);

            try {
                // Simulate network access.
                Thread.sleep(2000);

                // Example async request
//                HttpUtils.post("/api/register", rp, new JsonHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                        try {
//                            JSONObject serverResp = new JSONObject(response.toString());
//                        } catch (JSONException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                    }
//                });
            } catch (InterruptedException e) {
                return false;
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRegisterTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                // some error with request
            }
        }

        @Override
        protected void onCancelled() {
            mRegisterTask = null;
            showProgress(false);
        }
    }
}
