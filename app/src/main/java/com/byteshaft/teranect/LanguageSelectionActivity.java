package com.byteshaft.teranect;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

public class LanguageSelectionActivity extends Activity implements View.OnClickListener {

    private TextView malay;
    private TextView chinies;
    private TextView english;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.activity_language_selection);
        malay = (TextView) findViewById(R.id.malay_lang);
        english = (TextView) findViewById(R.id.eng_lang);
        chinies = (TextView) findViewById(R.id.jap_lang);

        malay.setOnClickListener(this);
        english.setOnClickListener(this);
        chinies.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.malay_lang:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.jap_lang:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.eng_lang:
                Locale locale = new Locale("en_US");
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getApplicationContext().getResources().updateConfiguration(config, null);
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
