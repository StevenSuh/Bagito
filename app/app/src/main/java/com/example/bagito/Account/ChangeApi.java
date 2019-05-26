package com.example.bagito.Account;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.bagito.DataHolder;
import com.example.bagito.Enums;
import com.example.bagito.HttpUtils;
import com.example.bagito.R;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.PaymentMethodCreateParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cz.msebera.android.httpclient.Header;

public class ChangeApi {
    public static void executeSave(final Context context,
                                   String name,
                                   String email,
                                   String password,
                                   boolean allowNotifications,
                                   final Card card) {
        SharedPreferences prefs = context.getSharedPreferences(Enums.SHARED_PREFS.toString(), Context.MODE_PRIVATE);
        prefs.edit().putBoolean(Enums.ALLOW_NOTIFICATIONS.toString(), allowNotifications).apply();

        final DataHolder.User user = DataHolder.getInstance().getUser();

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<String> stripeFuture = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                if (card == null) {
                    return null;
                }

                Stripe stripe = new Stripe(context);

                PaymentMethodCreateParams paymentMethodCreateParams = PaymentMethodCreateParams.create(card.toPaymentMethodParamsCard(), null);
                PaymentMethod paymentMethod = stripe.createPaymentMethodSynchronous(paymentMethodCreateParams, context.getString(R.string.stripe_key));

                if (paymentMethod == null) {
                    return null;
                }

                user.hasPayment = true;
                return paymentMethod.id;
            }
        });

        if (user.name.equals(name)) {
            name = "";
        } else {
            user.name = name;
        }
        if (user.email.equals(email)) {
            email = "";
        } else {
            user.email = email;
        }
        DataHolder.getInstance().setUser(user, prefs);

        RequestParams rp = new RequestParams();
        rp.add("name", name);
        rp.add("email", email);
        rp.add("password", password);

        try {
            rp.add("stripe_payment_token", stripeFuture.get());
        } catch (Exception e) {
            e.printStackTrace();
        }

        int userId = DataHolder.getInstance().getUser().id;

        HttpUtils.post("/api/user/" + userId + "/update", rp, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                ((ChangeInfo) context).showSaveProgress(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                successChange(context);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                failureChange(context, null);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse == null) {
                    failureChange(context, null);
                    return;
                }

                try {
                    String message = errorResponse.getString(HttpUtils.ERROR_MSG);
                    failureChange(context, message);
                } catch (JSONException e) {
                    failureChange(context, null);
                }
            }

            @Override
            public void onFinish() {
                ((ChangeInfo) context).showSaveProgress(false);
            }
        });
    }

    public static void executeCancel(final Context context){
        int userId = DataHolder.getInstance().getUser().id;

        HttpUtils.get("/api/user/" + userId + "/cancel", null, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                ((ChangeInfo) context).showCancelProgress(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                successChange(context);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                failureChange(context, null);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse == null) {
                    failureChange(context, null);
                    return;
                }

                try {
                    String message = errorResponse.getString(HttpUtils.ERROR_MSG);
                    failureChange(context, message);
                } catch (JSONException e) {
                    failureChange(context, null);
                }
            }

            @Override
            public void onFinish() {
                ((ChangeInfo) context).showCancelProgress(false);
            }
        });
    }

    private static void successChange(Context context) {
        Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show();
    }

    private static void failureChange(Context context, String error) {
        if (TextUtils.isEmpty(error)) {
            error = "Server error";
        }
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
    }
}
