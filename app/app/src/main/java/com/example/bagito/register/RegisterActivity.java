package com.example.bagito.register;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bagito.Enums;
import com.example.bagito.HttpUtils;
import com.example.bagito.MainActivity;
import com.example.bagito.R;
import com.example.bagito.Utils;
import com.example.bagito.login.LoginActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class RegisterActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private boolean isRegistering = false;

    private EditText mNameView;
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private EditText mCityView;
    private AutoCompleteTextView mStateView;

    private TextView mRegisterTextView;
    private ProgressBar mRegisterProgressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ImageView mBack = findViewById(R.id.back);
        mBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

        String[] states = getResources().getStringArray(R.array.states);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, states);

        mStateView = findViewById(R.id.state);
        mStateView.setAdapter(adapter);
        mStateView.setInputType(0);
        mStateView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mStateView.showDropDown();
                }
            }
        });
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

        RelativeLayout mRegisterButton = findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mRegisterTextView = findViewById(R.id.register_text);
        mRegisterProgressbar = findViewById(R.id.register_progress);
    }

    private void attemptRegister() {
        if (isRegistering) {
            return;
        }

        mNameView = findViewById(R.id.name);
        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        mConfirmPasswordView = findViewById(R.id.confirm_password);
        mCityView = findViewById(R.id.city);
        mStateView = findViewById(R.id.state);

        mNameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);
        mCityView.setError(null);
        mStateView.setError(null);

        String name = mNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirmPassword = mConfirmPasswordView.getText().toString();
        String city = mCityView.getText().toString();
        String state = mStateView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!Utils.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!Utils.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            mConfirmPasswordView.setError(getString(R.string.error_field_required));
            focusView = mConfirmPasswordView;
            cancel = true;
        } else if (!password.equals(confirmPassword)) {
            mConfirmPasswordView.setError(getString(R.string.error_password_mismatch_with_confirm));
            focusView = mConfirmPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(city)) {
            mCityView.setError(getString(R.string.error_field_required));
            focusView = mCityView;
            cancel = true;
        }

        if (TextUtils.isEmpty(state)) {
            mStateView.setError(getString(R.string.error_field_required));
            focusView = mStateView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mPasswordView.getWindowToken(), 0);

            RegisterApi.executeRegister(this, name, email, password, city, state);
        }
    }

    public void showProgress(final boolean show) {
        isRegistering = show;
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mRegisterTextView.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 0 : 1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mRegisterTextView.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
                    }
                });

        mRegisterProgressbar.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mRegisterProgressbar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                    }
                });
    }
}
