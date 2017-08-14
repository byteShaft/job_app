package com.byteshaft.teranect.profile;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.teranect.R;
import com.byteshaft.teranect.gettersetters.Qualification;
import com.byteshaft.teranect.utils.AppGlobals;
import com.byteshaft.teranect.utils.Helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class Education extends AppCompatActivity implements View.OnClickListener {


    private TextView buttonSave;
    private Toolbar toolbarTop;
    private ImageButton backButton;
    private ListView mListView;
    private Button addEducationButton;
    private ArrayList<Qualification> qualificationArrayList;
    private QualificationAdapter adapter;
    private TextView addTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);
        toolbarTop = (Toolbar) findViewById(R.id.add_education_toolbar);
        buttonSave = (TextView) findViewById(R.id.button_save_edu);
        backButton = (ImageButton) findViewById(R.id.back_button);
        mListView = (ListView) findViewById(R.id.education_list);
        addTextView = (TextView) findViewById(R.id.add_text_view);
        addEducationButton = (Button) findViewById(R.id.button_add_education);
        setSupportActionBar(toolbarTop);
        qualificationArrayList = new ArrayList<>();
        backButton.setOnClickListener(this);
        buttonSave.setOnClickListener(this);
        addEducationButton.setOnClickListener(this);
        adapter = new QualificationAdapter(qualificationArrayList);
        mListView.setAdapter(adapter);
        getQualificationList();
        getEducationData();
        if (qualificationArrayList.size() == 0) {
            addTextView.setVisibility(View.VISIBLE);
        } else {
            addTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                onBackPressed();
                break;
            case R.id.button_save_edu:
                for (int i = 0; i < qualificationArrayList.size(); i++) {
                    Qualification education = qualificationArrayList.get(i);
                    if (education.getId() == -1) {
                        addEducation();
                        break;
                    }
                }
                break;
            case R.id.button_add_education:
                addTextView.setVisibility(View.GONE);
                Qualification qualification = new Qualification();
                qualification.setQualification("");
                qualification.setSchool("");
                qualification.setPeriod("");
                qualificationArrayList.add(qualification);
                adapter.notifyDataSetChanged();
                break;
        }
    }

    private void addEducation() {
        Helpers.showProgressDialog(Education.this, "Adding...");
        HttpRequest request = new HttpRequest(this);
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        Helpers.dismissProgressDialog();
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_OK:
                                finish();
                                break;
                            case HttpURLConnection.HTTP_BAD_REQUEST:
                                AppGlobals.alertDialog(Education.this, "Error", "One or more fields are missing\n" +
                                        "please provide complete details");
                        }
                }

            }
        });
        request.setOnErrorListener(new HttpRequest.OnErrorListener() {
            @Override
            public void onError(HttpRequest request, int readyState, short error, Exception exception) {
                Helpers.dismissProgressDialog();
                switch (readyState) {
                    case HttpRequest.ERROR_CONNECTION_TIMED_OUT:
                        AppGlobals.alertDialog(Education.this, "Error", "Connection Timeout");
                        break;
                    case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                        AppGlobals.alertDialog(Education.this, "Error", exception.getLocalizedMessage());
                        break;
                }

            }
        });
        request.open("PUT", String.format("%sme", AppGlobals.BASE_URL));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send(getEducationData());
        Log.i("TAG DATA ", getEducationData());
    }

    private String getEducationData() {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject1 = new JSONObject();
        try {
            for (int i = 0; i < mListView.getCount(); i++) {
                Qualification education = qualificationArrayList.get(i);
                if (education.getId() == -1) {
                    Log.e("LOOP CODE", "Save button code");
                    JSONObject jsonObject = new JSONObject();
                    EditText period = (EditText) mListView.getChildAt(i).findViewById(R.id.et_time_span);
                    EditText qualification = (EditText) mListView.getChildAt(i).findViewById(R.id.et_qualification);
                    EditText school = (EditText) mListView.getChildAt(i).findViewById(R.id.et_school);
                    jsonObject.put("period", period.getText().toString());
                    jsonObject.put("qualification", qualification.getText().toString());
                    jsonObject.put("school", school.getText().toString());
                    jsonArray.put(jsonObject);
                }
                jsonObject1.put("education", jsonArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject1.toString();
    }

    private void getQualificationList() {
        Helpers.showProgressDialog(Education.this, "Please wait...");
        HttpRequest requestQualifications = new HttpRequest(getApplicationContext());
        requestQualifications.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        Helpers.dismissProgressDialog();
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_OK:
                                try {
                                    JSONArray jsonArray = new JSONArray(request.getResponseText());
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        Qualification qualification = new Qualification();
                                        qualification.setId(jsonObject.getInt("id"));
                                        qualification.setQualification(jsonObject.getString("qualification"));
                                        qualification.setPeriod(jsonObject.getString("period"));
                                        qualification.setSchool(jsonObject.getString("school"));
                                        qualificationArrayList.add(qualification);
                                        adapter.notifyDataSetChanged();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        }
                }
            }
        });
        requestQualifications.open("GET", String.format("%seducation/", AppGlobals.BASE_URL));
        requestQualifications.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        requestQualifications.send();
    }


    private class QualificationAdapter extends BaseAdapter {

        private ViewHolder viewHolder;
        private ArrayList<Qualification> qualificationsList;

        public QualificationAdapter(ArrayList<Qualification> qualificationsList) {
            this.qualificationsList = qualificationsList;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.delegate_education, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.educationNumber = (TextView) convertView.findViewById(R.id.tv_education_number);
                viewHolder.period = (EditText) convertView.findViewById(R.id.et_time_span);
                viewHolder.qualification = (EditText) convertView.findViewById(R.id.et_qualification);
                viewHolder.school = (EditText) convertView.findViewById(R.id.et_school);
                viewHolder.removeButton = (TextView) convertView.findViewById(R.id.remove_education);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final Qualification qualification = qualificationsList.get(position);
            if (qualification.getPeriod() != null && !qualification.getPeriod().trim().isEmpty()) {
                viewHolder.period.setText(qualification.getPeriod());
                viewHolder.qualification.setText(qualification.getQualification());
                viewHolder.school.setText(qualification.getSchool());
                viewHolder.educationNumber.setText("Education # " + (position + 1));
            }
            viewHolder.removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Education.this);
                    alertDialogBuilder.setTitle("Confirmation");
                    alertDialogBuilder.setMessage("Do you really want to delete?")
                            .setCancelable(false).setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    int educationId = qualification.getId();
                                    deleteEducation(educationId, position);
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
                }
            });
            return convertView;
        }

        @Override
        public int getCount() {
            return qualificationsList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        private void deleteEducation(int educationId, final int position) {
            Helpers.showProgressDialog(Education.this, "Removing Education...");
            HttpRequest requestQualifications = new HttpRequest(getApplicationContext());
            requestQualifications.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
                @Override
                public void onReadyStateChange(HttpRequest request, int readyState) {
                    switch (readyState) {
                        case HttpRequest.STATE_DONE:
                            Helpers.dismissProgressDialog();
                            switch (request.getStatus()) {
                                case HttpURLConnection.HTTP_NO_CONTENT:
                                    System.out.println(request.getResponseText());
                                    qualificationArrayList.remove(position);
                                    adapter.notifyDataSetChanged();

                                    Toast.makeText(Education.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpURLConnection.HTTP_BAD_REQUEST:
                                    System.out.println(request.getResponseText());
                                    break;
                            }
                    }
                }
            });
            requestQualifications.open("DELETE", String.format("%seducation/%d/", AppGlobals.BASE_URL, educationId));
            requestQualifications.setRequestHeader("Authorization", "Token " +
                    AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
            requestQualifications.send();
        }

        private class ViewHolder {
            private TextView educationNumber;
            private TextView removeButton;
            private EditText period;
            private EditText qualification;
            private EditText school;
        }
    }
}
