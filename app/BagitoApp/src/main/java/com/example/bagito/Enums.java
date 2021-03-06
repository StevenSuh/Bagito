package com.example.bagito;

public enum Enums {
    HOME_BUTTON("home_button"),
    RENT_BUTTON("rent_button"),
    RETURN_BUTTON("return_button"),
    ACCOUNT_BUTTON("account_button"),
    SHARED_PREFS("SHARED_PREFS"),
    IS_LOGGED_IN("IS_LOGGED_IN"),
    ALLOW_NOTIFICATIONS("ALLOW_NOTIFICATIONS");

    private String enumType;

    Enums(String enumType) {
        this.enumType = enumType;
    }

    @Override
    public String toString() {
        return enumType;
    }
}
