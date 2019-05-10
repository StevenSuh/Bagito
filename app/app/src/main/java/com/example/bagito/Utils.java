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

    // UX: for this too so that the user knows that they need to input
    public static boolean isEmailValid(String email) {
        return email.contains("@");
    }

    // UX: mention this word limit to user when signing up instead of "password too short"
    public static boolean isPasswordValid(String password) {
        return password.length() > 4;
    }
}
