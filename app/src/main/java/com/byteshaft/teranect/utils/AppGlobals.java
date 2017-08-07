package com.byteshaft.teranect.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class AppGlobals extends Application {

    private static Context sContext;
    public static final String SERVER_IP = "http://46.101.72.82:8000/";
    public static final String SERVER_IP_FOR_IMAGE = " http://46.101.72.82:8000";
    public static final String BASE_URL = String.format("%sapi/", SERVER_IP);
    public static final String KEY_TOKEN = "token";
    public static final String KEY_USER_ACTIVE = "user_active";
    public static final String KEY_USER_NAME = "name";
    public static final String KEY_SKILLS = "skills";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_IMAGE_URL = "photo";
    public static final String KEY_LOGIN = "login";
    public static final String KEY_PHONE_NUMBER = "phone_number";
    public static final String KEY_JOB_CATEGORY_NAME = "category_name";
    public static final String KEY_JOB_LOCATION_NAME = "location_name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_USER_ID = "id";
    public static final int LOCATION_ENABLE = 3;
    public static ImageLoader sImageLoader;

    @Override
    public void onCreate() {
        super.onCreate();
        sImageLoader = ImageLoader.getInstance();
        sImageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        sContext = getApplicationContext();
    }

    public static Context getContext() {
        return sContext;
    }

    public static SharedPreferences getPreferenceManager() {
        return getContext().getSharedPreferences("shared_prefs", MODE_PRIVATE);
    }

    public static void saveDataToSharedPreferences(String key, String value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putString(key, value).apply();
    }

    public static String getStringFromSharedPreferences(String key) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getString(key, "");
    }


    public static void saveUserActive(boolean value) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putBoolean(AppGlobals.KEY_USER_ACTIVE, value).apply();
    }

    public static boolean isUserActive() {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getBoolean(AppGlobals.KEY_USER_ACTIVE, false);
    }

    public static void loginState(boolean type) {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().putBoolean(KEY_LOGIN, type).apply();
    }

    public static boolean isLogin() {
        SharedPreferences sharedPreferences = getPreferenceManager();
        return sharedPreferences.getBoolean(KEY_LOGIN, false);
    }

    public static void clearSettings() {
        SharedPreferences sharedPreferences = getPreferenceManager();
        sharedPreferences.edit().clear().apply();
    }

    public static void alertDialog(Activity activity, String title, String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(msg).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
