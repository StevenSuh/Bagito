package com.example.bagito;

public enum Enums {
    HOME_BUTTON("home_button"),
    RENT_BUTTON("rent_button"),
    RETURN_BUTTON("return_button"),
    ACCOUNT_BUTTON("account_button"),
    ABOUT_BUTTON("about_button"),
    SHARED_PREFS("SHARED_PREFS"),
    IS_LOGGED_IN("IS_LOGGED_IN");

    private String enumType;

    Enums(String enumType) {
        this.enumType = enumType;
    }

    @Override
    public String toString() {
        return enumType;
    }
}
