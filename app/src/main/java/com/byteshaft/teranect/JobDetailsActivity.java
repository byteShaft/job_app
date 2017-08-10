package com.byteshaft.teranect;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.teranect.utils.AppGlobals;
import com.byteshaft.teranect.utils.Helpers;
import com.byteshaft.teranect.utils.LoginDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class JobDetailsActivity extends AppCompatActivity implements View.OnClickListener {


    private ImageButton backButton;
    private TextView companyName;
    private TextView jobDescription;
    private TextView jobRequirement;
    private TextView createdAt;
    private TextView jobLocation;
    private TextView phoneNumber;
    private TextView website;
    private Button jobSaveButton;
    private Button jobApplyButton;
    private int jobId;
    private LoginDialog loginDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);
        String id = getIntent().getStringExtra("id");
        loginDialog = new LoginDialog(JobDetailsActivity.this);
        companyName = (TextView) findViewById(R.id.tv_company_name);
        jobDescription = (TextView) findViewById(R.id.tv_job_description);
        jobLocation = (TextView) findViewById(R.id.tv_location);
        jobRequirement = (TextView) findViewById(R.id.tv_job_requirement);
        phoneNumber = (TextView) findViewById(R.id.tv_phone);
        createdAt = (TextView) findViewById(R.id.date);

        jobSaveButton = (Button) findViewById(R.id.button_job_save);
        jobApplyButton = (Button) findViewById(R.id.button_apply);
        jobSaveButton.setOnClickListener(this);
        jobApplyButton.setOnClickListener(this);
        backButton = (ImageButton) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        jobId = Integer.parseInt(id);
        getJobDetails(jobId);
    }

    public void saveJob(int id) {
        Helpers.showProgressDialog(JobDetailsActivity.this, "Please wait...");
        HttpRequest httpRequest = new HttpRequest(getApplicationContext());
        httpRequest.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        Helpers.dismissProgressDialog();
                        Log.i("MY URLl", request.getResponseURL());
                        Helpers.dismissProgressDialog();
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_CREATED:
                                Snackbar.make(companyName, "Job Saved", Snackbar.LENGTH_SHORT).show();
                                jobSaveButton.setText("Saved");
                                jobSaveButton.setTextColor(getResources().getColor(R.color.colorAccent));
                                jobSaveButton.setEnabled(false);
                        }
                }
            }
        });
        httpRequest.open("POST", String.format("%sjobs/saved/", AppGlobals.BASE_URL));
        httpRequest.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("job", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpRequest.send(jsonObject.toString());
    }

    private void getJobDetails(int id) {
        HttpRequest request = new HttpRequest(getApplicationContext());
        Helpers.showProgressDialog(JobDetailsActivity.this, "Please wait...");
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        Helpers.dismissProgressDialog();
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_OK:
                                Log.e("TTtttttttt", request.getResponseText());
                                try {
                                    JSONObject jsonObject = new JSONObject(request.getResponseText());
                                    String location = jsonObject.getString("location");
                                    String date = jsonObject.getString("created_at");
                                    String scope = jsonObject.getString("scope");
                                    String title = jsonObject.getString("title");
                                    String requirement = jsonObject.getString("requirement");
                                    String description = jsonObject.getString("detailed_description");

                                    jobLocation.setText(location);
                                    createdAt.setText(date);
                                    companyName.setText(title);
                                    jobRequirement.setText(requirement);
                                    jobDescription.setText(description);

                                    System.out.println(location + "  " + title);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        }
                }
            }
        });
        request.open("GET", String.format("%sjobs/%s", AppGlobals.BASE_URL, id));
        request.send();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_job_save:
                if (AppGlobals.isLogin()) {
                    saveJob(jobId);
                } else {
                    loginDialog.show();
                }
                break;

            case R.id.button_apply:
                break;
        }
    }
}
