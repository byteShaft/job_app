package com.byteshaft.teranect.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.byteshaft.teranect.R;
import com.byteshaft.teranect.accounts.AccountManager;

public class LoginDialog extends Dialog implements View.OnClickListener {

    private Button registerButton;
    private TextView loginButton;

    public LoginDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_login);
        registerButton = (Button) findViewById(R.id.dialog_register_button);
        loginButton = (TextView) findViewById(R.id.login_text_button);
        registerButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_register_button:
                Intent registerActivity = new Intent(getContext(), AccountManager.class);
                registerActivity.putExtra("register", "Register");
                getContext().startActivity(registerActivity);
                dismiss();
                break;
            case R.id.login_text_button:
                Intent i = new Intent(getContext(), AccountManager.class);
                getContext().startActivity(i);
                dismiss();
                break;
        }
    }
}
