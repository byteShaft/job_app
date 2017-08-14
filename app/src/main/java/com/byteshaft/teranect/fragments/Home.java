package com.byteshaft.teranect.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.byteshaft.teranect.MainActivity;
import com.byteshaft.teranect.R;
import com.byteshaft.teranect.activities.MessagesActivity;
import com.byteshaft.teranect.activities.QRcodeActivity;

import ss.com.bannerslider.banners.DrawableBanner;
import ss.com.bannerslider.views.BannerSlider;


public class Home extends Fragment implements View.OnClickListener {


    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    public static final String ALLOW_KEY = "ALLOWED";
    public static final String CAMERA_PREF = "camera_pref";

    private View mBaseView;
    private BannerSlider bannerSlider;
    private Button partTime;
    private Button fullTime;
    private Button internShip;
    private Toolbar toolbarTop;
    private ImageButton barcodeButton;
    private ImageButton messageButton;

    private LinearLayout health;
    private LinearLayout insurance;
    private LinearLayout informationTechnology;
    private LinearLayout sales;

    private TextView title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_home, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        toolbarTop = (Toolbar) mBaseView.findViewById(R.id.main_toolbar);
        barcodeButton = (ImageButton) mBaseView.findViewById(R.id.button_barcode);
        messageButton = (ImageButton) mBaseView.findViewById(R.id.button_message);
        bannerSlider = (BannerSlider) mBaseView.findViewById(R.id.banner_slider1);

        health = (LinearLayout) mBaseView.findViewById(R.id.all_health_fitness);
        insurance = (LinearLayout) mBaseView.findViewById(R.id.all_insurance);
        informationTechnology = (LinearLayout) mBaseView.findViewById(R.id.all_it);
        sales = (LinearLayout) mBaseView.findViewById(R.id.all_sales);

        health.setOnClickListener(this);
        insurance.setOnClickListener(this);
        informationTechnology.setOnClickListener(this);
        sales.setOnClickListener(this);

        bannerSlider.addBanner(new DrawableBanner(R.drawable.slide_imge));
        bannerSlider.addBanner(new DrawableBanner(R.drawable.slide_imge));
        bannerSlider.addBanner(new DrawableBanner(R.drawable.slide_imge));

        title = (TextView) mBaseView.findViewById(R.id.toolbar_title);
        title.setText(getActivity().getTitle());

        partTime = (Button) mBaseView.findViewById(R.id.button_part_time);
        fullTime = (Button) mBaseView.findViewById(R.id.button_full_time);
        internShip = (Button) mBaseView.findViewById(R.id.button_internship);
        activity.setSupportActionBar(toolbarTop);
        partTime.setOnClickListener(this);
        fullTime.setOnClickListener(this);
        internShip.setOnClickListener(this);
        barcodeButton.setOnClickListener(this);
        messageButton.setOnClickListener(this);
        return mBaseView;
    }


    public static void saveToPreferences(Context context, String key,
                                         Boolean allowed) {
        SharedPreferences myPrefs = context.getSharedPreferences
                (CAMERA_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putBoolean(key, allowed);
        prefsEditor.commit();
    }

    public static Boolean getFromPref(Context context, String key) {
        SharedPreferences myPrefs = context.getSharedPreferences
                (CAMERA_PREF, Context.MODE_PRIVATE);
        return (myPrefs.getBoolean(key, false));
    }

    private void permission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (getFromPref(getActivity(), ALLOW_KEY)) {

                showSettingsAlert();

            } else if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.CAMERA)) {
                    showAlert();
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }
            }
        } else {
            startActivity(new Intent(getActivity(), QRcodeActivity.class));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_part_time:
                loadFragment(new PartTime());
                break;
            case R.id.button_full_time:
                loadFragment(new FullTime());
                break;
            case R.id.button_internship:
                loadFragment(new Internship());
                break;

            case R.id.all_health_fitness:
                MainActivity.getInstance().loadThisFragment(new JobsList(), "Health/Fitness", "");
                break;
            case R.id.all_insurance:
                MainActivity.getInstance().loadThisFragment(new JobsList(), "Insurance", "");
                break;
            case R.id.all_it:
                MainActivity.getInstance().loadThisFragment(new JobsList(), "IT", "");
                break;
            case R.id.all_sales:
                MainActivity.getInstance().loadThisFragment(new JobsList(), "Sales", "");
                break;

            case R.id.button_barcode:
                permission();
                break;
            case R.id.button_message:
                startActivity(new Intent(getActivity(), MessagesActivity.class));
        }
    }

    public void loadFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fragmentTransaction.replace(R.id.fragment_container, fragment, backStateName);
        FragmentManager manager = getActivity().getSupportFragmentManager();
        Log.i("TAG", backStateName);
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
        if (!fragmentPopped) {
            fragmentTransaction.addToBackStack(backStateName);
            fragmentTransaction.commit();
        }
    }

    private void showAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Camera for Scanning Barcode.");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);

                    }
                });
        alertDialog.show();
    }

    private void showSettingsAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Camera.");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DON'T ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SETTINGS",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startInstalledAppDetailsActivity(getActivity());

                    }
                });
        alertDialog.show();
    }

    public static void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }
}
