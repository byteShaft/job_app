package com.byteshaft.teranect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.teranect.R;
import com.byteshaft.teranect.gettersetters.JobDetails;
import com.byteshaft.teranect.utils.AppGlobals;
import com.byteshaft.teranect.utils.Helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class JobsList extends Fragment implements View.OnClickListener {

    private View mBaseView;
    private TextView title;
    private Toolbar toolbarTop;
    private ImageButton backButton;
    private ListView listView;
    private ArrayList<JobDetails> jobsArrayList;
    private JobListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.jobs_list_fragment, container, false);
        String categoryValue = getArguments().getString("category");
        String type = getArguments().getString("type");
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        listView = (ListView) mBaseView.findViewById(R.id.jobs_list);
        toolbarTop = (Toolbar) mBaseView.findViewById(R.id.my_toolbar);
        title = (TextView) toolbarTop.findViewById(R.id.toolbar_title);
        backButton = (ImageButton) toolbarTop.findViewById(R.id.back_button);
        backButton.setOnClickListener(this);
        title.setText(categoryValue.toUpperCase());
        jobsArrayList = new ArrayList<>();
        Log.e("Test", categoryValue + " " + type);
        getJobsList(type, categoryValue);
        return mBaseView;
    }

    private void getJobsList(String category, String jobType) {
        HttpRequest request = new HttpRequest(AppGlobals.getContext());
        Helpers.showProgressDialog(getActivity(), "Please wait...");
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        Log.i("MY URLlllll", request.getResponseURL());
                        Helpers.dismissProgressDialog();
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_OK:
                                Log.i("MY Category", request.getResponseText());
                                try {
                                    JSONArray jsonArray = new JSONArray(request.getResponseText());
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        JobDetails jobDetails = new JobDetails();
                                        jobDetails.setJobId(jsonObject.getInt("id"));
                                        jobDetails.setJobType(jsonObject.getString("type"));
                                        jobDetails.setJobTitle(jsonObject.getString("title"));
                                        jobDetails.setCreatorName(jsonObject.getString("creator_name"));
                                        jobDetails.setScope(jsonObject.getString("scope"));
                                        jobDetails.setSalary(jsonObject.getString("salary"));
                                        jobDetails.setRequirement(jsonObject.getString("requirement"));
                                        jobDetails.setLocation(jsonObject.getString("location"));
                                        jobDetails.setLocation_name(jsonObject.getString("location_name"));
                                        jobDetails.setDetailDescription(jsonObject.getString("detailed_description"));
                                        jobsArrayList.add(jobDetails);
                                    }
                                    adapter = new JobListAdapter(jobsArrayList);
                                    listView.setAdapter(adapter);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        }
                }
            }
        });
        request.open("GET", String.format("%sjobs/?type=%s&category=%s", AppGlobals.BASE_URL, category, jobType));
        request.send();
    }

    public void saveJob(int id) {
        Helpers.showProgressDialog(getActivity(), "Please wait...");
        HttpRequest httpRequest = new HttpRequest(getActivity().getApplicationContext());
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
                                Snackbar.make(mBaseView, "Job Saved", Snackbar.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                FragmentManager manager = getFragmentManager();
                manager.popBackStack();
                break;
        }
    }

    private class JobListAdapter extends BaseAdapter {
        private ViewHolder viewHolder;
        private ArrayList<JobDetails> jobDetails;


        public JobListAdapter(ArrayList<JobDetails> jobDetails) {
            this.jobDetails = jobDetails;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.delegate_jobs_list, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.companyName = (TextView) convertView.findViewById(R.id.company_name);
                viewHolder.jobTitle = (TextView) convertView.findViewById(R.id.job_title);
                viewHolder.jobLocation = (TextView) convertView.findViewById(R.id.company_location);
                viewHolder.urgentTag = (ImageView) convertView.findViewById(R.id.urgent_tag);
                viewHolder.saveButton = (Button) convertView.findViewById(R.id.button_save_job);
                viewHolder.applyButton = (Button) convertView.findViewById(R.id.button_apply_job);
//                viewHolder.companyLogo = (CircleImageView) convertView.findViewById(R.id.job_icon);
                viewHolder.salary = (TextView) convertView.findViewById(R.id.salary);
                viewHolder.jobCategory = (TextView) convertView.findViewById(R.id.part_time_button);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final JobDetails jobDetail = jobDetails.get(position);
            viewHolder.companyName.setText(jobDetail.getCreatorName());
            viewHolder.jobTitle.setText(jobDetail.getJobTitle());
            viewHolder.jobLocation.setText(jobDetail.getLocation_name());
            viewHolder.salary.setText(jobDetail.getSalary());
            viewHolder.jobCategory.setText(jobDetail.getJobType());
            viewHolder.saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveJob(jobDetail.getJobId());
                }
            });
            viewHolder.applyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"recipient@example.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
                    i.putExtra(Intent.EXTRA_TEXT   , "body of email");
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(AppGlobals.getContext(),
                                "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return convertView;
        }

        @Override
        public int getCount() {
            return jobDetails.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }
    }

    class ViewHolder {
        private Button saveButton;
        private Button applyButton;
        private ImageView urgentTag;
        private TextView companyName;
        private TextView jobTitle;
        private TextView jobLocation;
        private CircleImageView companyLogo;
        private TextView salary;
        private TextView jobCategory;
    }
}
