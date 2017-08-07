
package com.byteshaft.teranect.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.teranect.MainActivity;
import com.byteshaft.teranect.R;
import com.byteshaft.teranect.activities.EditProfile;
import com.byteshaft.teranect.utils.AppGlobals;


public class ProfileSettings extends AppCompatActivity implements View.OnClickListener {

    public static ProfileSettings sInstance;

    private TextView mLogoutTextView;
    private LinearLayout mEditProfileLayout;
    private LinearLayout mLanguageLayout;
    private LinearLayout mTermsLayout;
    private LinearLayout mAppVersionLayout;
    private LinearLayout mLegalNoticesLayout;
    private LinearLayout mAppPrivacyLayout;

    private ImageView mBackButtonImageView;

    public static ProfileSettings getInstance() {
        return sInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sInstance = this;
        setContentView(R.layout.activity_settings);
        mLogoutTextView = (TextView) findViewById(R.id.logout_text_view);
        mEditProfileLayout = (LinearLayout) findViewById(R.id.edit_profile_layout);
        mLanguageLayout = (LinearLayout) findViewById(R.id.language_layout);
        mTermsLayout = (LinearLayout) findViewById(R.id.terms_layout);
        mAppVersionLayout = (LinearLayout) findViewById(R.id.app_version_layout);
        mAppPrivacyLayout = (LinearLayout) findViewById(R.id.privacy_policy_layout);
        mLegalNoticesLayout = (LinearLayout) findViewById(R.id.legal_notices_layout);
        mBackButtonImageView = (ImageView) findViewById(R.id.back_button);

        mLogoutTextView.setOnClickListener(this);
        mEditProfileLayout.setOnClickListener(this);
        mLanguageLayout.setOnClickListener(this);
        mTermsLayout.setOnClickListener(this);
        mAppVersionLayout.setOnClickListener(this);
        mAppPrivacyLayout.setOnClickListener(this);
        mLegalNoticesLayout.setOnClickListener(this);
        mBackButtonImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logout_text_view:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileSettings.this);
                alertDialogBuilder.setTitle("Confirmation");
                alertDialogBuilder.setMessage("Do you really want to logout?")
                        .setCancelable(false).setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AppGlobals.clearSettings();
                                startActivity(new Intent(ProfileSettings.this, MainActivity.class));
                                dialog.dismiss();
                            }
                        });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
            case R.id.edit_profile_layout:
                startActivity(new Intent(getApplicationContext(), EditProfile.class));
                break;
            case R.id.language_layout:
                Toast.makeText(getApplicationContext(), "soon it will be implement", Toast.LENGTH_SHORT).show();
                break;
            case R.id.terms_layout:
                Toast.makeText(getApplicationContext(), "soon it will be implement", Toast.LENGTH_SHORT).show();
                break;
            case R.id.app_version_layout:
                Toast.makeText(getApplicationContext(), "soon it will be implement", Toast.LENGTH_SHORT).show();
                break;
            case R.id.privacy_policy_layout:
                Toast.makeText(getApplicationContext(), "soon will it be implement", Toast.LENGTH_SHORT).show();
                break;
            case R.id.legal_notices_layout:
                Toast.makeText(getApplicationContext(), "soon will it be implement", Toast.LENGTH_SHORT).show();
                break;
            case R.id.back_button:
                onBackPressed();
                break;

        }

    }
}


