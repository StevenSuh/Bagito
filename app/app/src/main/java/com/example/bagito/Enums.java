package com.example.bagito;

public enum Enums {
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
