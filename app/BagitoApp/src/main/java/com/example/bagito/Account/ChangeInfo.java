package com.example.bagito.Account;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.example.bagito.DataHolder;
import com.example.bagito.Enums;
import com.example.bagito.HttpUtils;
import com.example.bagito.R;
import com.example.bagito.Utils;
import com.example.bagito.login.LoginApi;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.PaymentIntentParams;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.PaymentMethodCreateParams;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class ChangeInfo extends AppCompatActivity {

    private boolean isSaving = false;
    private boolean isCancelling = false;

    private EditText mNameView;
    private EditText mEmailView;
    private EditText mPassword;
    private EditText mNewPassword;
    private CheckBox mNotifications;
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

        SharedPreferences prefs = getSharedPreferences(Enums.SHARED_PREFS.toString(), MODE_PRIVATE);
        boolean allowNotifications = prefs.getBoolean(Enums.ALLOW_NOTIFICATIONS.toString(), true);

        ImageView mBack = findViewById(R.id.back);
        mBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Utils.setTouchEffect(mBack, true, true);

        mNameView = findViewById(R.id.name);
        mEmailView = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mNewPassword = findViewById(R.id.confirm_password);
        mNotifications = findViewById(R.id.checkBox);
        mCardNumberView = findViewById(R.id.card_number);
        mMonthYearView = findViewById(R.id.month_year);
        mCVVNumberView = findViewById(R.id.cvv_number);

        DataHolder.User user = DataHolder.getInstance().getUser();

        mNameView.setText(user.name);
        mEmailView.setText(user.email);

        if (user.hasPayment) {
            mCardNumberView.setText("****************");
            mMonthYearView.setText("**/**");
            mCVVNumberView.setText("***");
        }

        mNotifications.setChecked(allowNotifications);

        RelativeLayout mSaveButton = findViewById(R.id.save_changes_button);
        mSaveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSave();
            }
        });

        RelativeLayout mCancelSubButton = findViewById(R.id.cancel_subscription_button);
        mCancelSubButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmCancel();
            }
        });

        mSaveTextView = findViewById(R.id.save_changes_text);
        mSaveProgressbar = findViewById(R.id.save_changes_progress);

        mCancelSubView = findViewById(R.id.cancel_subscription_text);
        mCancelSubProgressbar = findViewById(R.id.cancel_subscription_progress);

        LinearLayout homeButton = findViewById(R.id.home_button);
        LinearLayout rentButton = findViewById(R.id.rent_button);
        LinearLayout returnButton = findViewById(R.id.return_button);
        LinearLayout accountButton = findViewById(R.id.account_button);

        Utils.initNavbar(this,
                Enums.ACCOUNT_BUTTON.toString(),
                homeButton,
                rentButton,
                returnButton,
                accountButton);
    }

    private void attemptSave() {
        if (isSaving) {
            return;
        }

        mNameView.setError(null);
        mEmailView.setError(null);
        mPassword.setError(null);
        mNewPassword.setError(null);
        mCardNumberView.setError(null);
        mMonthYearView.setError(null);
        mCVVNumberView.setError(null);

        if (!HttpUtils.isNetworkAvailable(this.getApplicationContext())) {
            Toast.makeText(this.getApplicationContext(), "No internet", Toast.LENGTH_SHORT).show();
            return;
        }

        final String name = mNameView.getText().toString();
        final String email = mEmailView.getText().toString();
        final String password = mPassword.getText().toString();
        final String newPassword = mNewPassword.getText().toString();
        final boolean allowNotifications = mNotifications.isChecked();
        String cardNumber = mCardNumberView.getText().toString();
        String monthYear = mMonthYearView.getText().toString();
        String cvv = mCVVNumberView.getText().toString();
        Card card = null;

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(email) && !Utils.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (!TextUtils.isEmpty(password) && !Utils.isPasswordValid(password)) {
            mPassword.setError(getString(R.string.error_invalid_password));
            focusView = mPassword;
            cancel = true;
        }

        if(!TextUtils.isEmpty(password)){
            if (TextUtils.isEmpty(newPassword)) {
                mNewPassword.setError(getString(R.string.error_field_required));
                focusView = mNewPassword;
                cancel = true;
            } else if(!password.equals(newPassword)){
                mNewPassword.setError(getString(R.string.error_password_mismatch_with_confirm));
                focusView = mNewPassword;
                cancel = true;
            } else if(!Utils.isPasswordValid(newPassword)) {
                //should be a valid password
                mNewPassword.setError(getString(R.string.error_invalid_password));
                focusView = mNewPassword;
                cancel = true;
            }
        }

        boolean isCardNumberEmpty = TextUtils.isEmpty(cardNumber);
        boolean isMonthYearEmpty = TextUtils.isEmpty(monthYear);
        boolean isCVVEmpty = TextUtils.isEmpty(cvv);

        if (isCardNumberEmpty && (!isMonthYearEmpty || !isCVVEmpty)) {
            mCardNumberView.setError(getString(R.string.error_field_required));
            focusView = mCardNumberView;
            cancel = true;
        }
        if (!isCardNumberEmpty && !Utils.isCardNumberValid(cardNumber)){
            mCardNumberView.setError(getString(R.string.error_invalid_card_number));
            focusView = mCardNumberView;
            cancel = true;
        }

        if (isMonthYearEmpty && (!isCardNumberEmpty || !isCVVEmpty)) {
            mMonthYearView.setError(getString(R.string.error_field_required));
            focusView = mMonthYearView;
            cancel = true;
        }
        if (!isMonthYearEmpty && !Utils.isMonthYearValid(monthYear)) {
            mMonthYearView.setError(getString(R.string.error_invalid_month_year));
            focusView = mMonthYearView;
            cancel = true;
        }

        if (isCVVEmpty && (!isCardNumberEmpty || !isMonthYearEmpty)) {
            mCVVNumberView.setError(getString(R.string.error_field_required));
            focusView = mCVVNumberView;
            cancel = true;
        }
        if (!isCVVEmpty && !Utils.isCVVValid(cvv)){
            mCVVNumberView.setError(getString(R.string.error_invalid_cvv));
            focusView = mCVVNumberView;
            cancel = true;
        }

        if (!isCardNumberEmpty && !isMonthYearEmpty && !isCVVEmpty) {
            if (Utils.canParseInt(monthYear.substring(0, 2)) && Utils.canParseInt(monthYear.substring(3, 5))) {
                card = new Card(
                        cardNumber,
                        Integer.parseInt(monthYear.substring(0, 2)),
                        Integer.parseInt(monthYear.substring(3, 5)),
                        cvv
                );

                if (!card.validateCard()) {
                    mCardNumberView.setError(getString(R.string.error_invalid_card));
                    mCardNumberView.requestFocus();
                    return;
                }
            }
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            final Card finalCard = card;
            Runnable execute = new Runnable() {
                @Override
                public void run() {
                    ChangeApi.executeSave(ChangeInfo.this, name, email, password, allowNotifications, finalCard);
                }
            };

            if (card != null) {
                confirmCardSave(execute);
            } else {
                execute.run();
            }
        }
    }

    private void confirmCardSave(final Runnable callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Bagito Subscription");
        builder.setMessage(getString(R.string.subscription_details));

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showSaveProgress(true);
                callback.run();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void confirmCancel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm subscription cancellation");
        builder.setMessage(getString(R.string.subscription_cancel_details));

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showCancelProgress(true);
                ChangeApi.executeCancel(ChangeInfo.this);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showSaveProgress(final boolean show) {
        isSaving = show;
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mSaveTextView.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 0 : 1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mSaveTextView.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
                    }
                });

        mSaveProgressbar.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mSaveProgressbar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                    }
                });
    }

    public void showCancelProgress(final boolean show) {
        isCancelling = show;
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mCancelSubView.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 0 : 1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mCancelSubView.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
                    }
                });

        mCancelSubProgressbar.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mCancelSubProgressbar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                    }
                });
    }
}
