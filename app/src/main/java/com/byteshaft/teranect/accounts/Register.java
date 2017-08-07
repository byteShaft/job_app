package com.byteshaft.teranect.accounts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.teranect.R;
import com.byteshaft.teranect.utils.AppGlobals;
import com.byteshaft.teranect.utils.Helpers;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;


public class Register extends Fragment implements View.OnClickListener,
        HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener{

    private View mBaseView;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mVerifyPassword;
    private Button mSignUpButton;
    private TextView mLoginTextView;

    private String mEmailAddressString;
    private String mPasswordString;
    private String mVerifyPasswordString;
    private String mFullNameString;
    private String mNameString;
    private String mPhoneString;

    private LoginButton mFbLoginButton;
    private CallbackManager callbackManager;
    private FacebookCallback<LoginResult> callback;
    private HttpRequest request;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_register, container, false);
        setHasOptionsMenu(true);
        mEmail = (EditText) mBaseView.findViewById(R.id.email_edit_text);
        mPassword = (EditText) mBaseView.findViewById(R.id.password_edit_text);
        mVerifyPassword = (EditText) mBaseView.findViewById(R.id.verify_password_edit_text);
        mSignUpButton = (Button) mBaseView.findViewById(R.id.sign_up_button);
        mLoginTextView = (TextView) mBaseView.findViewById(R.id.login_text_view);

        mSignUpButton.setOnClickListener(this);
        mLoginTextView.setOnClickListener(this);
//        setupView(mBaseView);
//        System.out.println("onCreateView");
        return mBaseView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_up_button:
                System.out.println("signUp button");
                if (validateEditText()) {
                    registerUser(2, mFullNameString, mEmailAddressString, mPasswordString);
                }
                break;
            case R.id.login_text_view:
                AccountManager.getInstance().loadFragment(new Login());
                break;
        }
    }


    private boolean validateEditText() {
        boolean valid = true;
        mEmailAddressString = mEmail.getText().toString();
        mPasswordString = mPassword.getText().toString();
        mVerifyPasswordString = mVerifyPassword.getText().toString();

        if (mEmailAddressString.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailAddressString).matches()) {
            mEmail.setError("please provide a valid email");
            valid = false;
        } else {
            mEmail.setError(null);
        }
        if (mPasswordString.trim().isEmpty() || mPasswordString.length() < 4) {
            mPassword.setError("enter at least 4 characters");
            valid = false;
        } else {
            mPassword.setError(null);
        }

        if (mVerifyPasswordString.trim().isEmpty() || mVerifyPasswordString.length() < 4 ||
                !mVerifyPasswordString.equals(mPasswordString)) {
            mVerifyPassword.setError("password does not match");
            valid = false;
        } else {
            mVerifyPassword.setError(null);
        }
        return valid;
    }

    private void registerUser(int usertype, String fullName, String email, String password) {
        request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%sregister", AppGlobals.BASE_URL));
        request.send(getRegisterData(usertype, fullName, email, password));
        Helpers.showProgressDialog(getActivity(), "Registering User ");
    }

    private String getRegisterData(int type, String fullName, String email, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("full_name", fullName);
            jsonObject.put("email", email);
            jsonObject.put("password", password);
            jsonObject.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();

    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                Helpers.dismissProgressDialog();
                Log.i("TAG", "Response " + request.getResponseText());
                switch (request.getStatus()) {
                    case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                        AppGlobals.alertDialog(getActivity(), "Registration Failed!", "please check your internet connection");
                        break;
                    case HttpURLConnection.HTTP_BAD_REQUEST:
                        AppGlobals.alertDialog(getActivity(), "Registration Failed!", "Email already in use");
                        break;
                    case HttpURLConnection.HTTP_CREATED:
                        Toast.makeText(getActivity(), "Activation code has been sent to you! Please check your Email", Toast.LENGTH_SHORT).show();
                        System.out.println(request.getResponseText() + "working ");
                        try {
                            JSONObject jsonObject = new JSONObject(request.getResponseText());
                            String userId = jsonObject.getString(AppGlobals.KEY_USER_ID);
                            String email = jsonObject.getString(AppGlobals.KEY_EMAIL);

                            //saving values
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_EMAIL, email);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_ID, userId);
                            AccountManager.getInstance().loadFragment(new AccountActivationCode());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
        }
    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {
        Helpers.dismissProgressDialog();
        switch (readyState) {
            case HttpRequest.ERROR_CONNECTION_TIMED_OUT:
                Helpers.showSnackBar(getView(), "connection time out");
                break;
            case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                Helpers.showSnackBar(getView(), exception.getLocalizedMessage());
                break;
        }
    }

    public void setupView(View view) {
        callbackManager = CallbackManager.Factory.create();
        LoginButton facebookLoginButton = (LoginButton) view.findViewById(R.id.login_button);
        callback = new FacebookCallback<LoginResult>() {
            //ERROR
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                //ERROR - I can't get access token and user name
                Log.d("facebook id", accessToken.getApplicationId());
                Log.d("profile name", profile.getFirstName());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        };
//        facebookLoginButton.setReadPermissions("public_profile", "user_friends","user_birthday","user_about_me","email");
//        facebookLoginButton.registerCallback(callbackManager, callback);
    }

}
