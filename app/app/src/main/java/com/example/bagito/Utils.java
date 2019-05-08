package com.example.bagito;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class Utils {

    public static boolean isEmailValid(String email) {
        return email.contains("@");
    }

    public static boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    public static void initActionBar(Context context, ActionBar actionBar) {
        if (actionBar != null) {
            actionBar.setTitle(Html.fromHtml("<b><font color=\"" + context.getResources().getColor(R.color.colorPrimary) + "\">" + context.getString(R.string.app_name) + "</font></b>"));
            actionBar.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.colorLight)));
        }
    }

    public static void initNavbar(final Context context, String currentPage) {
        final LinearLayout homeButton = ((Activity) context).findViewById(R.id.home_button);
        final LinearLayout rentButton = ((Activity) context).findViewById(R.id.rent_button);
        final LinearLayout returnButton = ((Activity) context).findViewById(R.id.return_button);
        final LinearLayout accountButton = ((Activity) context).findViewById(R.id.account_button);

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
                context.startActivity(intent);
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
}
