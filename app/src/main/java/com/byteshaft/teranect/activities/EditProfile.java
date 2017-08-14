
package com.byteshaft.teranect.activities;

import android.Manifest;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.requests.FormData;
import com.byteshaft.requests.HttpRequest;
import com.byteshaft.teranect.R;
import com.byteshaft.teranect.profile.ProfileSettings;
import com.byteshaft.teranect.utils.AppGlobals;
import com.byteshaft.teranect.utils.Helpers;
import com.byteshaft.teranect.utils.RotateUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity implements View.OnClickListener,
        HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private EditText mMobileNumber;
    private EditText mUserName;
    private TextView mLocationTextView;
    private EditText mAddress;
    private CircleImageView mProfilePicture;
    private ImageView mBackButton;

    private File destination;
    private Uri selectedImageUri;
    private static String imageUrl = "";
    private Bitmap profilePic;

    private static final int LOCATION_PERMISSION = 4;
    private static final int REQUEST_CAMERA = 3;
    private static final int SELECT_FILE = 2;
    private static final int STORAGE_CAMERA_PERMISSION = 1;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private int locationCounter = 0;

    private String mMobileNumberString;
    private String mUserNameString;
    private String mLocationString;
    private HttpRequest request;

    private Button mSaveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mMobileNumber = (EditText) findViewById(R.id.mobile_number_edit_text);
        mUserName = (EditText) findViewById(R.id.name_edit_text);
        mAddress = (EditText) findViewById(R.id.location_edit_text);
        mLocationTextView = (TextView) findViewById(R.id.pick_for_current_location);
        mSaveButton = (Button) findViewById(R.id.button_save);
        mBackButton = (ImageView) findViewById(R.id.back_button);
        mProfilePicture = (CircleImageView) findViewById(R.id.user_dp);

        mSaveButton.setOnClickListener(this);
        mProfilePicture.setOnClickListener(this);
        mLocationTextView.setOnClickListener(this);
        mBackButton.setOnClickListener(this);

        if (imageUrl.trim().isEmpty() && imageUrl != null) {
            profilePic = Helpers.getBitMapOfProfilePic(imageUrl);
            mProfilePicture.setImageBitmap(profilePic);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserName.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_USER_NAME));
        mMobileNumber.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_PHONE_NUMBER));
        mAddress.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_LOCATION));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_dp:
                checkPermissions();
                break;
            case R.id.pick_for_current_location:
                locationCounter = 0;
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EditProfile.this);
                    alertDialogBuilder.setTitle(getResources().getString(R.string.permission_dialog_title));
                    alertDialogBuilder.setMessage(getResources().getString(R.string.permission_dialog_message))
                            .setCancelable(false).setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        LOCATION_PERMISSION);
                            }
                        }
                    });
                    alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                } else {
                    if (Helpers.locationEnabled()) {
                        new LocationTask().execute();
                    } else {
                        Helpers.dialogForLocationEnableManually(this);
                    }
                }
                break;
            case R.id.button_save:
                mMobileNumberString = mMobileNumber.getText().toString();
                mUserNameString = mUserName.getText().toString();
                editProfileDataToServer();
                break;
            case R.id.back_button:
                onBackPressed();
                break;
        }

    }

    private void editProfileDataToServer() {
        FormData data = new FormData();
        data.append(FormData.TYPE_CONTENT_TEXT, "name", mUserNameString);
        data.append(FormData.TYPE_CONTENT_TEXT, "phone_number", mMobileNumberString);
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            data.append(FormData.TYPE_CONTENT_FILE, "user_details.photo", imageUrl);
        }

        if (mLocationString != null){
            Log.e("location String ", mLocationString);
            data.append(FormData.TYPE_CONTENT_TEXT, "user_details.location", mLocationString);
        }
        request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("PUT", String.format("%sme", AppGlobals.BASE_URL));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send(data);
        Helpers.showProgressDialog(EditProfile.this, "Updating profile..");
    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {
        Helpers.dismissProgressDialog();
        switch (readyState) {
            case HttpRequest.ERROR_CONNECTION_TIMED_OUT:
                Helpers.showSnackBar(findViewById(android.R.id.content), "connection time out");
                break;
            case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                Helpers.showSnackBar(findViewById(android.R.id.content), exception.getLocalizedMessage());

        }
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                Helpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_OK:
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        System.out.println(request.getResponseText() + "working ");
                        try {
                            JSONObject mainObject = new JSONObject(request.getResponseText());
                            JSONObject jsonObject = mainObject.getJSONObject("user_details");
                            String name = mainObject.getString(AppGlobals.KEY_USER_NAME);
                            String photoUrl = jsonObject.getString(AppGlobals.KEY_IMAGE_URL);
                            String location = jsonObject.getString(AppGlobals.KEY_LOCATION);
                            String phoneNumber = jsonObject.getString(AppGlobals.KEY_PHONE_NUMBER);

                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_NAME, name);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_IMAGE_URL, photoUrl);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_LOCATION, location);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_PHONE_NUMBER, phoneNumber);
                            Log.e("ImageURL ", photoUrl  + " saved url  " + AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_IMAGE_URL));

                            ProfileSettings.getInstance().finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
        }

    }

    private void checkPermissions() {
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(
                    new String[listPermissionsNeeded.size()]), STORAGE_CAMERA_PERMISSION);
        }

        if (listPermissionsNeeded.isEmpty()) {
            selectImage();
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }

            }
        });
        builder.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Helpers.locationEnabled()) {
                        new LocationTask().execute();
                    } else {
                        Helpers.dialogForLocationEnableManually(this);
                    }
                } else {
                    Helpers.showSnackBar(findViewById(android.R.id.content), R.string.permission_denied);
                }

                break;
            case STORAGE_CAMERA_PERMISSION:
                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) ||
                                    shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                showDialogOK("Camera and Storage Permission required for this app",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case DialogInterface.BUTTON_POSITIVE:
                                                        checkPermissions();
                                                        break;
                                                    case DialogInterface.BUTTON_NEGATIVE:
                                                        // proceed with logic by disabling the related features or quit the app.
                                                        break;
                                                }
                                            }
                                        });
                            }
                            //permission is denied (and never ask again is  checked)
                            //shouldShowRequestPermissionRationale will return false
                            else {
                                Toast.makeText(getApplicationContext(), "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                        .show();
                                //                            //proceed with logic by disabling the related features or quit the
                                Helpers.showSnackBar(findViewById(android.R.id.content), R.string.permission_denied);
                            }
                        }
                        break;

                    }

                }
        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getApplicationContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                imageUrl = destination.getAbsolutePath();
                FileOutputStream fileOutputStream;
                try {
                    destination.createNewFile();
                    fileOutputStream = new FileOutputStream(destination);
                    fileOutputStream.write(bytes.toByteArray());
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                profilePic = Helpers.getBitMapOfProfilePic(destination.getAbsolutePath());
                Bitmap orientedBitmap = RotateUtil.rotateBitmap(destination.getAbsolutePath(), profilePic);
                mProfilePicture.setImageBitmap(orientedBitmap);
            } else if (requestCode == SELECT_FILE) {
                selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(this,
                        selectedImageUri, projection, null, null,
                        null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);
                profilePic = Helpers.getBitMapOfProfilePic(selectedImagePath);
                Bitmap orientedBitmap = RotateUtil.rotateBitmap(selectedImagePath, profilePic);
                mProfilePicture.setImageBitmap(orientedBitmap);
                imageUrl = String.valueOf(selectedImagePath);
            }
        }
    }

    public void getAddress(double latitude, double longitude) {
        final StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(AppGlobals.getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result.append(address.getLocality()).append(" ").append(address.getCountryName());
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAddress.setText(result.toString());
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        locationCounter++;
        if (locationCounter > 1) {
            stopLocationUpdate();
            mLocationString = location.getLatitude() + "," + location.getLongitude();
            System.out.println("Lat: " + location.getLatitude() + "Long: " + location.getLongitude());
            getAddress(location.getLatitude(), location.getLongitude());
        }
    }

    class LocationTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Helpers.showSnackBar(findViewById(android.R.id.content), R.string.acquiring_location);
        }

        @Override
        protected String doInBackground(String... strings) {
            buildGoogleApiClient();
            return null;
        }
    }

    public void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(AppGlobals.getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void stopLocationUpdate() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }


    protected void createLocationRequest() {
        long INTERVAL = 1000;
        long FASTEST_INTERVAL = 1000;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
}
