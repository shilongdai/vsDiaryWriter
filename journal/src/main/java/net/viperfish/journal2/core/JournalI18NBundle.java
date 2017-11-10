package net.viperfish.journal2.core;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

public final class JournalI18NBundle {

    private JournalI18NBundle() {

    }

    private static ResourceBundle bundle;

    static {
        bundle = ResourceBundle.getBundle("journal", Locale.US);
    }

    public static void updateLocale(Locale l) {
        bundle = ResourceBundle.getBundle("journal", l);
    }

    public static String getBaseBundleName() {
        return bundle.getBaseBundleName();
    }

    public static final String getString(String key) {
        return bundle.getString(key);
    }

    public static final String[] getStringArray(String key) {
        return bundle.getStringArray(key);
    }

    public static final Object getObject(String key) {
        return bundle.getObject(key);
    }

    public static Locale getLocale() {
        return bundle.getLocale();
    }

    public static Enumeration<String> getKeys() {
        return bundle.getKeys();
    }

    public static boolean containsKey(String key) {
        return bundle.containsKey(key);
    }

    public static Set<String> keySet() {
        return bundle.keySet();
    }

    public static ResourceBundle getBundle() {
        return bundle;
    }

}
