package com.byteshaft.teranect;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.byteshaft.teranect.fragments.Home;
import com.byteshaft.teranect.fragments.Me;
import com.byteshaft.teranect.fragments.Search;
import com.byteshaft.teranect.utils.AppGlobals;
import com.byteshaft.teranect.utils.LoginDialog;

public class MainActivity extends AppCompatActivity {


    private LoginDialog loginDialog;
    public static MainActivity sInstance;

    public static MainActivity getInstance() {
        return sInstance;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.enter, R.anim.exit);
        super.onCreate(savedInstanceState);
        sInstance = this;
        setContentView(R.layout.activity_main);
        loginDialog = new LoginDialog(MainActivity.this);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        loadThisFragment(new Home(), "", "");
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    loadThisFragment(new Home(), "", "");
                    return true;
                case R.id.navigation_search:
                    loadThisFragment(new Search(), "", "");
                    return true;
                case R.id.navigation_me:
                    if (!AppGlobals.isLogin()) {
                        loginDialog.show();
                    } else {
                        loadThisFragment(new Me(), "", "");
                    }

                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void loadThisFragment(Fragment fragment, String category, String type) {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("category", category);
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        tx.replace(R.id.fragment_container, fragment);
        tx.commit();
    }
}
