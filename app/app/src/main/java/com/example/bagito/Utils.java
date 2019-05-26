package com.example.bagito;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.MotionEvent;
import android.view.View;

import com.example.bagito.Account.AccountActivity;

import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Utils {

    // UX: for this too so that the user knows that they need to input
    public static boolean isEmailValid(String email) {
        return email.contains("@");
    }

    // UX: mention this word limit to user when signing up instead of "password too short"
    public static boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    public static boolean isCardNumberValid(String cardNumber){
        return cardNumber.length() == 16;
    }

    public static boolean isMonthYearValid(String monthYear) {
        return monthYear.length() == 5 && monthYear.charAt(2) == '/';
    }

    public static boolean isCVVValid(String cvv){
        return cvv.length() == 3;
    }

    public static boolean canParseInt(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
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
                                  final View accountButton) {
        homeButton.setAlpha(0.5f);
        rentButton.setAlpha(0.5f);
        returnButton.setAlpha(0.5f);
        accountButton.setAlpha(0.5f);

        setTouchEffect(homeButton, false, true);
        setTouchEffect(rentButton, false, true);
        setTouchEffect(returnButton, false, true);
        setTouchEffect(accountButton, false, true);

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
                    Intent intent = new Intent(v.getContext(), RentActivity.class);
                    context.startActivity(intent);
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
                    Intent intent = new Intent(v.getContext(), AccountActivity.class);
                    context.startActivity(intent);
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

    public static void nukePrefs(SharedPreferences prefs) {
        prefs.edit().clear().apply();
    }
}
