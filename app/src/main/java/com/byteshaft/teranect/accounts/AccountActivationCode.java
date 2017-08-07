package com.byteshaft.teranect.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.teranect.MainActivity;
import com.byteshaft.teranect.R;
import com.byteshaft.teranect.utils.AppGlobals;
import com.byteshaft.teranect.utils.Helpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;


public class AccountActivationCode extends Fragment implements View.OnClickListener,
        HttpRequest.OnReadyStateChangeListener, HttpRequest.OnErrorListener {

    private View mBaseView;

    private EditText mEmail;
    private EditText mVerificationCode;
    private Button mLoginButton;
    private String mEmailString;
    private String mVerificationCodeString;
    private HttpRequest request;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_activation_code, container, false);
        setHasOptionsMenu(true);
        mEmail = (EditText) mBaseView.findViewById(R.id.email_edit_text);
        mVerificationCode = (EditText) mBaseView.findViewById(R.id.activation_edit_text);
        mLoginButton = (Button) mBaseView.findViewById(R.id.button_verify);
        mEmail.setText(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL));
        mEmailString = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_EMAIL);

        mEmail.setEnabled(false);
        mLoginButton.setOnClickListener(this);
        return mBaseView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_verify:
                if (validate()) {
                    activateUser(mEmailString, mVerificationCodeString);
                }

                break;
            case R.id.sign_up_text_view:
                break;

        }
    }

    public boolean validate() {
        boolean valid = true;
        mEmailString = mEmail.getText().toString();
        mVerificationCodeString = mVerificationCode.getText().toString();

        System.out.println(mEmailString);
        System.out.println(mVerificationCodeString);

        if (mEmailString.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailString).matches()) {
            mEmail.setError("please provide a valid email");
            valid = false;
        } else {
            mEmail.setError(null);
        }
        if (mVerificationCodeString.trim().isEmpty() || mVerificationCodeString.length() < 6) {
            mVerificationCode.setError("Verification code must be 6 characters");
            valid = false;
        } else {
            mVerificationCode.setError(null);
        }
        return valid;
    }

    private void activateUser(String email, String emailOtp) {
        request = new HttpRequest(getActivity());
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("POST", String.format("%sactivate", AppGlobals.BASE_URL));
        request.send(getUserActivationData(email, emailOtp));
        Helpers.showProgressDialog(getActivity(), "Activating User...");
    }


    private String getUserActivationData(String email, String emailOtp) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("email_otp", emailOtp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                Log.i("TAG", "dismisss");
                Helpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_BAD_REQUEST:
                        Toast.makeText(getActivity(), "Please enter correct account Verification code", Toast.LENGTH_LONG).show();
                        break;
                    case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                        AppGlobals.alertDialog(getActivity(), "Sending Failed!", "please check your internet connection !");
                        break;
                    case HttpURLConnection.HTTP_FORBIDDEN:
                        AppGlobals.alertDialog(getActivity(), "Activation Failed!", "User deactivated by admin!");
                        break;
                    case HttpURLConnection.HTTP_OK:
                        try {
                            JSONObject jsonObject = new JSONObject(request.getResponseText());
                            System.out.println( "data" + jsonObject);
                            String userId = jsonObject.getString(AppGlobals.KEY_USER_ID);
                            String email = jsonObject.getString(AppGlobals.KEY_EMAIL);
                            String token = jsonObject.getString(AppGlobals.KEY_TOKEN);

                            //saving values
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_USER_ID, userId);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_TOKEN, token);
                            AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_EMAIL, email);
                            Log.i("token", " " + AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
                            AppGlobals.loginState(true);
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            startActivity(new Intent(getActivity(), MainActivity.class));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
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
}
