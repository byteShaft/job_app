package com.byteshaft.teranect.profile;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.teranect.R;
import com.byteshaft.teranect.gettersetters.WorkExp;
import com.byteshaft.teranect.utils.AppGlobals;
import com.byteshaft.teranect.utils.Helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class WorkExperience extends AppCompatActivity implements View.OnClickListener {

    private TextView buttonSave;
    private Toolbar toolbarTop;
    private ImageButton backButton;
    private ListView mListView;
    private Button addButton;
    private ArrayList<WorkExp> workExperienceArrayList;
    private WorkExpAdapter workExpAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_experience);
        toolbarTop = (Toolbar) findViewById(R.id.add_education_toolbar);
        buttonSave = (TextView) findViewById(R.id.button_exp_save);
        backButton = (ImageButton) findViewById(R.id.back_button);
        mListView = (ListView) findViewById(R.id.work_exp_list);
        addButton = (Button) findViewById(R.id.button_add_work_experience);
        setSupportActionBar(toolbarTop);
        workExperienceArrayList = new ArrayList<>();

        addButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        buttonSave.setOnClickListener(this);
        workExpAdapter = new WorkExpAdapter(workExperienceArrayList);
        mListView.setAdapter(workExpAdapter);
        getWorkExperienceList();
        getWorkExperienceData();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("Item Click");
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("Long Click");
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                onBackPressed();
                break;
            case R.id.button_exp_save:
                for (int i = 0; i < workExperienceArrayList.size(); i++) {
                    WorkExp workExp = workExperienceArrayList.get(i);
                    if (workExp.getId() == -1) {
                        addWorkExp();
                    }
                }
                break;
            case R.id.button_add_work_experience:
                WorkExp workExp = new WorkExp();
                workExp.setComapnyName("");
                workExp.setPeriod("");
                workExp.setJobTitle("");
                workExperienceArrayList.add(workExp);
                workExpAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void addWorkExp() {
        Helpers.showProgressDialog(WorkExperience.this, "Adding...");
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
                                AppGlobals.alertDialog(WorkExperience.this, "Error", "One or more fields are missing\n" +
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
                        AppGlobals.alertDialog(WorkExperience.this, "Error", "Connection Timeout");
                        break;
                    case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                        AppGlobals.alertDialog(WorkExperience.this, "Error", exception.getLocalizedMessage());
                        break;
                }
            }
        });
        request.open("PUT", String.format("%sme", AppGlobals.BASE_URL));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send(getWorkExperienceData());
    }

    private String getWorkExperienceData() {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject1 = new JSONObject();
        try {
            for (int i = 0; i < mListView.getCount(); i++) {
                WorkExp workExp = workExperienceArrayList.get(i);

                if (workExp.getId() == -1) {
                    JSONObject jsonObject = new JSONObject();
                    EditText period = (EditText) mListView.getChildAt(i).findViewById(R.id.et_time_span_work);
                    EditText jobTitle = (EditText) mListView.getChildAt(i).findViewById(R.id.et_job_title);
                    EditText company = (EditText) mListView.getChildAt(i).findViewById(R.id.et_company);
                    jsonObject.put("period", period.getText().toString());
                    jsonObject.put("title", jobTitle.getText().toString());
                    jsonObject.put("company", company.getText().toString());
                    jsonArray.put(jsonObject);
                }
                jsonObject1.put("experience", jsonArray);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject1.toString();
    }

    private void getWorkExperienceList() {
        Helpers.showProgressDialog(WorkExperience.this, "Please wait...");
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
                                        WorkExp workExp = new WorkExp();
                                        workExp.setId(jsonObject.getInt("id"));
                                        workExp.setJobTitle(jsonObject.getString("title"));
                                        workExp.setComapnyName(jsonObject.getString("company"));
                                        workExp.setPeriod(jsonObject.getString("period"));
                                        workExperienceArrayList.add(workExp);
                                        workExpAdapter.notifyDataSetChanged();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        }
                }
            }
        });
        requestQualifications.open("GET", String.format("%sexperience/", AppGlobals.BASE_URL));
        requestQualifications.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        requestQualifications.send();
    }


    private class WorkExpAdapter extends BaseAdapter {

        private ViewHolder viewHolder;
        private ArrayList<WorkExp> workExperiencesList;

        public WorkExpAdapter(ArrayList<WorkExp> workExperiencesList) {
            this.workExperiencesList = workExperiencesList;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.delegate_experience, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.period = (EditText) convertView.findViewById(R.id.et_time_span_work);
                viewHolder.title = (EditText) convertView.findViewById(R.id.et_job_title);
                viewHolder.company = (EditText) convertView.findViewById(R.id.et_company);
                viewHolder.jobNumber = (TextView) convertView.findViewById(R.id.tv_job_number);
                viewHolder.removeButton = (TextView) convertView.findViewById(R.id.remove_job);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final WorkExp workExperience = workExperiencesList.get(position);
            viewHolder.period.setText(workExperience.getPeriod());
            viewHolder.title.setText(workExperience.getJobTitle());
            viewHolder.company.setText(workExperience.getComapnyName());
            viewHolder.jobNumber.setText("Job # " + (position + 1));
            viewHolder.removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(WorkExperience.this);
                    alertDialogBuilder.setTitle("Confirmation");
                    alertDialogBuilder.setMessage("Do you really want to delete?")
                            .setCancelable(false).setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    int workExperienceId = workExperience.getId();
                                    deleteWorkExperience(workExperienceId, position);
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
            return workExperiencesList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        private void deleteWorkExperience(int experienceId, final int position) {
            Helpers.showProgressDialog(WorkExperience.this, "Removing Work Experience...");
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
                                    workExperienceArrayList.remove(position);
                                    workExpAdapter.notifyDataSetChanged();
                                    Toast.makeText(WorkExperience.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                    break;
                                case HttpURLConnection.HTTP_BAD_REQUEST:
                                    System.out.println(request.getResponseText());
                                    break;
                            }
                    }
                }
            });
            requestQualifications.open("DELETE", String.format("%sexperience/%d/", AppGlobals.BASE_URL, experienceId));
            requestQualifications.setRequestHeader("Authorization", "Token " +
                    AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
            requestQualifications.send();
        }

        private class ViewHolder {
            private TextView removeButton;
            private TextView jobNumber;
            private EditText period;
            private EditText title;
            private EditText company;
        }
    }
}
