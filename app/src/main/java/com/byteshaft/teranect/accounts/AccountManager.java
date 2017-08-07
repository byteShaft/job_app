package com.byteshaft.teranect.accounts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.byteshaft.teranect.MainActivity;
import com.byteshaft.teranect.R;
import com.byteshaft.teranect.utils.AppGlobals;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;


public class AccountManager extends AppCompatActivity {

    private static AccountManager sInstance;
    CallbackManager callbackManager;

    public static AccountManager getInstance() {
        return sInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_account_manager);
        if (!AppGlobals.isLogin()) {
            if (getIntent().getExtras() != null) {
                loadFragmentWithOutBackStack(new Register());
            } else {
                loadFragmentWithOutBackStack(new Login());
            }
        } else {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        sInstance = this;
    }

    public void loadFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fragmentTransaction.replace(R.id.container, fragment, backStateName);
        FragmentManager manager = getSupportFragmentManager();
        Log.i("TAG", backStateName);
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
        if (!fragmentPopped) {
            fragmentTransaction.addToBackStack(backStateName);
            fragmentTransaction.commit();
        }
    }

    public void loadFragmentWithOutBackStack(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        System.out.println("working");
    }
}
