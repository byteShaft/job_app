package com.byteshaft.teranect.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.teranect.R;
import com.byteshaft.teranect.gettersetters.JobDetails;
import com.byteshaft.teranect.utils.AppGlobals;
import com.byteshaft.teranect.utils.Helpers;
import com.byteshaft.teranect.utils.LoginDialog;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Search extends Fragment {

    private View mBaseView;
    private GridView mSearchViewElements;
    private ArrayList<JobDetails> jobsArrayList;
    private ListAdapter adapter;
    private EditText searchEditText;
    private boolean userTyping = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_search, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        searchEditText = (EditText) mBaseView.findViewById(R.id.search_edit_text);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                userTyping = true;
                ArrayList<JobDetails> search = new ArrayList<JobDetails>();
                String query = charSequence.toString();
                if (!charSequence.toString().trim().isEmpty()) {
                    adapter = new ListAdapter(search);
                    mSearchViewElements.setAdapter(adapter);
                    for (JobDetails jobDetails : jobsArrayList) {
                        if (StringUtils.containsIgnoreCase(jobDetails.getJobTitle(), query) ||
                                StringUtils.containsIgnoreCase(jobDetails.getLocation_name(), query) ||
                                StringUtils.containsIgnoreCase(jobDetails.getCreatorName(), query) ||
                                StringUtils.containsIgnoreCase(jobDetails.getJobType(), query)) {
                            search.add(jobDetails);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    if (search.size() < 1) {
                        Helpers.showSnackBar(getView(), getString(R.string.no_search_result));
                    }
                } else {
                    adapter = new ListAdapter(jobsArrayList);
                    mSearchViewElements.setAdapter(adapter);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                userTyping = false;

            }
        });
        mSearchViewElements = (GridView) mBaseView.findViewById(R.id.grid_view);
        jobsArrayList = new ArrayList<>();
        getJobsList();
        return mBaseView;
    }

    private void getJobsList() {
        HttpRequest request = new HttpRequest(AppGlobals.getContext());
        Helpers.showProgressDialog(getActivity(), "Please wait...");
        request.setOnErrorListener(new HttpRequest.OnErrorListener() {
            @Override
            public void onError(HttpRequest request, int readyState, short error, Exception exception) {
                Helpers.dismissProgressDialog();
            }
        });
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
                                jobsArrayList = new ArrayList<JobDetails>();
                                adapter = new ListAdapter(jobsArrayList);
                                mSearchViewElements.setAdapter(adapter);
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
                                        adapter.notifyDataSetChanged();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        }
                }
            }
        });
        request.open("GET", String.format("%sjobs/", AppGlobals.BASE_URL));
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

    private class ListAdapter extends BaseAdapter {
        private ViewHolder viewHolder;
        private ArrayList<JobDetails> jobDetails;

        public ListAdapter(ArrayList<JobDetails> jobDetails) {
            this.jobDetails = jobDetails;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.delegate_search_fragment, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.companyName = (TextView) convertView.findViewById(R.id.company_description);
                viewHolder.jobTitle = (TextView) convertView.findViewById(R.id.job_title);
                viewHolder.jobLocation = (TextView) convertView.findViewById(R.id.a_job_location);
                viewHolder.saveButton = (ImageView) convertView.findViewById(R.id.fab_save);
//                viewHolder.companyLogo = (CircleImageView) convertView.findViewById(R.id.job_icon);
                viewHolder.salary = (TextView) convertView.findViewById(R.id.a_salary);
                viewHolder.jobCategory = (Button) convertView.findViewById(R.id.job_type);
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
                    if (AppGlobals.isLogin()) {
                        saveJob(jobDetail.getJobId());
                    } else {
                        LoginDialog loginDialog = new LoginDialog(getActivity());
                        loginDialog.setCancelable(true);
                        loginDialog.setTitle(null);
                        loginDialog.show();
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
        private ImageView saveButton;
        private TextView companyName;
        private TextView jobTitle;
        private TextView jobLocation;
        private CircleImageView companyLogo;
        private TextView salary;
        private Button jobCategory;
    }
}
