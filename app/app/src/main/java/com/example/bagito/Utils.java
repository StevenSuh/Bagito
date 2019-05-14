package com.example.bagito;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.example.bagito.About.AboutActivity;

public class Utils {

    // UX: for this too so that the user knows that they need to input
    public static boolean isEmailValid(String email) {
        return email.contains("@");
    }

    // UX: mention this word limit to user when signing up instead of "password too short"
    public static boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    public static void setTouchEffect(View view, final boolean goDark, final boolean hasClick) {
        final float lowOpacity = 0.5f;

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.animate()
                        .setDuration(50)
                        .alpha(goDark ? lowOpacity : 1f);
                    return !hasClick;
                } else {
                    v.animate()
                        .setDuration(50)
                        .alpha(goDark ? 1f : lowOpacity);
                }
                return false;
            }
        });
    }

    public static void initNavbar(final Context context,
                                  String currentPage,
                                  final View homeButton,
                                  final View rentButton,
                                  final View returnButton,
                                  final View accountButton,
                                  final View aboutButton) {
        homeButton.setAlpha(0.5f);
        rentButton.setAlpha(0.5f);
        returnButton.setAlpha(0.5f);
        accountButton.setAlpha(0.5f);
//        aboutButton.setAlpha(0.5f);

        setTouchEffect(homeButton, false, true);
        setTouchEffect(rentButton, false, true);
        setTouchEffect(returnButton, false, true);
        setTouchEffect(accountButton, false, true);
//        setTouchEffect(aboutButton, false, true);

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
//                    context.startActivity(intent);
            }
        });
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    Intent intent = new Intent(v.getContext(), ReturnActivity.class);
//                    context.startActivity(intent);
            }
        });
        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    Intent intent = new Intent(v.getContext(), AccountActivity.class);
//                    context.startActivity(intent);
            }
        });
//        aboutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("I\'m in onclicklistener");
//                Intent intent = new Intent(v.getContext(), AboutActivity.class);
//                context.startActivity(intent);
//            }
//        });

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
//        } else if (currentPage.equals(Enums.ABOUT_BUTTON.toString())) {
//            aboutButton.setAlpha(1);
//            aboutButton.setOnTouchListener(null);
//            aboutButton.setOnClickListener(null);
        }
    }

    public static void nukePrefs(SharedPreferences prefs) {
        prefs.edit().clear().apply();
    }
}
