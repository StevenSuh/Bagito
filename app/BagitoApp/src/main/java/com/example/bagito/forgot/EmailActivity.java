package com.example.bagito.forgot;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bagito.HttpUtils;
import com.example.bagito.R;
import com.example.bagito.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class EmailActivity extends AppCompatActivity {

    private boolean isSubmitting = false;

    private TextView mSubmitTextView;
    private ProgressBar mSubmitProgressbar;

    private EditText mEmailEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        ImageView mBack = findViewById(R.id.back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBack.setOnTouchListener(new View.OnTouchListener() {
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

        mEmailEdit = findViewById(R.id.email);

        RelativeLayout mSubmitButton = findViewById(R.id.submit_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSubmit();
            }
        });

        mSubmitTextView = findViewById(R.id.forgot_text);
        mSubmitProgressbar = findViewById(R.id.forgot_progress);
    }

    public void attemptSubmit() {
        if (isSubmitting) {
            return;
        }

        String email = mEmailEdit.getText().toString();
        mEmailEdit.setError(null);

        if (TextUtils.isEmpty(email)) {
            mEmailEdit.setError(getString(R.string.error_field_required));
            mEmailEdit.requestFocus();
            return;
        } else if (!Utils.isEmailValid(email)) {
            mEmailEdit.setError(getString(R.string.error_invalid_email));
            mEmailEdit.requestFocus();
            return;
        }

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEmailEdit.getWindowToken(), 0);

        executeSubmit(email);
    }

    public void executeSubmit(final String email) {
        RequestParams rp = new RequestParams();
        rp.add("email", email);

        HttpUtils.post("/api/forgot", rp, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                showProgress(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                successSubmit(email);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                failSubmit(null);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse == null) {
                    failSubmit(null);
                    return;
                }

                try {
                    String message = errorResponse.getString(HttpUtils.ERROR_MSG);
                    failSubmit(message);
                } catch (JSONException e) {
                    failSubmit(null);
                }
            }

            @Override
            public void onFinish() {
                showProgress(false);
            }
        });
    }

    public void successSubmit(String email) {
        Toast.makeText(this, "Email sent", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, CodeActivity.class);
        intent.putExtra("EMAIL", email);
        startActivity(intent);
    }

    public void failSubmit(String error) {
        if (error == null) {
            error = "Server error";
        }

        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    public void showProgress(final boolean show) {
        isSubmitting = show;
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mSubmitTextView.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 0 : 1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mSubmitTextView.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
                    }
                });

        mSubmitProgressbar.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mSubmitProgressbar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                    }
                });
    }
}
