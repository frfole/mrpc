package com.frfole.mrpc.app.translation;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.ResourceBundle;

public final class I18n {
    private static @NotNull ResourceBundle bundle;

    static {
        bundle = ResourceBundle.getBundle("lang.lang");
    }

    public static void setLang(@NotNull Locale locale) {
        bundle = ResourceBundle.getBundle("lang.lang", locale);
    }

    public static String translate(@NotNull String key, Object... args) {
        if (bundle.containsKey(key)) {
            return String.format(bundle.getLocale(), bundle.getString(key), args);
        } else {
            return key;
        }
    }

    public static @NotNull String translate(@NotNull String key) {
        if (bundle.containsKey(key)) {
            return bundle.getString(key);
        } else {
            return key;
        }
    }
}
