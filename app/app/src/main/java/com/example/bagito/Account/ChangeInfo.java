package com.example.bagito.Account;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.example.bagito.HttpUtils;
import com.example.bagito.R;
import com.example.bagito.Utils;
import com.example.bagito.login.LoginApi;

public class ChangeInfo extends AppCompatActivity {

    private boolean isSaving = false;
    private boolean isCancelling = false;

    private EditText mNameView;
    private EditText mEmailView;
    private EditText mPassword;
    private EditText mNewPassword;
    private EditText mCardNumberView;
    private EditText mMonthYearView;
    private EditText mCVVNumberView;

    private TextView mSaveTextView;
    private ProgressBar mSaveProgressbar;

    private TextView mCancelSubView;
    private ProgressBar mCancelSubProgressbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_info);

        mNameView = findViewById(R.id.name);
        mEmailView = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mNewPassword = findViewById(R.id.confirm_password);
        mCardNumberView = findViewById(R.id.card_number);
        mMonthYearView = findViewById(R.id.month_year);
        mCVVNumberView = findViewById(R.id.cvv_number);

        RelativeLayout mSaveButton = findViewById(R.id.save_changes_button);
        mSaveButton.setOnClickListener(new OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                attemptSave();
            }
        });

        mSaveTextView = findViewById(R.id.save_changes_text);
        mSaveProgressbar = findViewById(R.id.save_changes_progress);


    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void attemptSave() {
        if (isSaving) {
            return;
        }

        mNameView = findViewById(R.id.name);
        mEmailView = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mNewPassword = findViewById(R.id.confirm_password);
        mCardNumberView = findViewById(R.id.card_number);
        mMonthYearView = findViewById(R.id.month_year);
        mCVVNumberView = findViewById(R.id.cvv_number);

        mNameView.setError(null);
        mEmailView.setError(null);
        mPassword.setError(null);
        mNewPassword.setError(null);
        mCardNumberView.setError(null);
        mMonthYearView.setError(null);
        mCVVNumberView.setError(null);

        String name = mNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPassword.getText().toString();
        String newPassword = mNewPassword.getText().toString();
        String cardNumber = mCardNumberView.getText().toString();
        String monthYear = mMonthYearView.getText().toString();
        String cvv = mCVVNumberView.getText().toString();


        boolean cancel = false;
        View focusView = null;

        if (!Utils.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if(TextUtils.isEmpty(password)){
            //if the field is empty, should show some error
            mPassword.setError(getString(R.string.error_field_required));
            focusView = mPassword;
            cancel = true;
        }
        else if (!Utils.isPasswordValid(password)) {
            mPassword.setError(getString(R.string.error_invalid_password));
            focusView = mPassword;
            cancel = true;
        }

        if(TextUtils.isEmpty(newPassword)){
            //if the field is empty, should show some error
            mNewPassword.setError(getString(R.string.error_field_required));
            focusView = mNewPassword;
            cancel = true;
        }
        else if(!password.equals(newPassword)){
            //making sure that both passwords are the same
            mNewPassword.setError(getString(R.string.error_password_mismatch_with_confirm));
            focusView = mNewPassword;
            cancel = true;
        }
        else if(!Utils.isPasswordValid(newPassword)){
            //should be a valid password
            mNewPassword.setError(getString(R.string.error_invalid_password));
            focusView = mNewPassword;
            cancel = true;

        }

        if(TextUtils.isEmpty(cardNumber)){
            mCardNumberView.setError(getString(R.string.error_missing_card_info));
            focusView = mCardNumberView;
            cancel = true;
        }
        else if(!Utils.isCardNumberValid(cardNumber)){
            mCardNumberView.setError(getString(R.string.error_invalid_card_number));
            focusView = mCardNumberView;
            cancel = true;
        }

        if(TextUtils.isEmpty(monthYear)){
            mMonthYearView.setError(getString(R.string.error_invalid_month_year));
            focusView = mMonthYearView;
            cancel = true;
        }
        else if(!Utils.isMonthYearValid(monthYear)){
            mMonthYearView.setError(getString(R.string.error_invalid_month_year));
            focusView = mMonthYearView;
            cancel = true;
        }

        if(TextUtils.isEmpty(cvv)){
            mCVVNumberView.setError(getString(R.string.error_invalid_cvv));
            focusView = mCVVNumberView;
            cancel = true;
        }
        else if(!Utils.isCVVValid(cvv)){
            mCVVNumberView.setError(getString(R.string.error_invalid_cvv));
            focusView = mCVVNumberView;
            cancel = true;
        }


        if (!HttpUtils.isNetworkAvailable(this.getApplicationContext())) {
            Toast.makeText(this.getApplicationContext(), "No internet", Toast.LENGTH_SHORT).show();
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mPassword.getWindowToken(), 0);

            ChangeApi.executeChange(this, name, email, password, cardNumber, monthYear, cvv);
        }
    }
}
