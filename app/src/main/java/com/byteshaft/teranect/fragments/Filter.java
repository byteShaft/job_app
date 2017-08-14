package com.byteshaft.teranect.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.teranect.R;
import com.byteshaft.teranect.activities.FilterCategories;
import com.byteshaft.teranect.activities.FilterLocation;
import com.byteshaft.teranect.gettersetters.JobDetails;
import com.byteshaft.teranect.utils.AppGlobals;
import com.byteshaft.teranect.utils.Helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class Filter extends Fragment implements View.OnClickListener {

    private View mBaseView;

    private ImageButton mBackButton;
    private TextView mResetTextView;
    private static TextView mJobLocationTextView;
    private static TextView mJobCategoryTextView;
    private RelativeLayout mLocationLayout;
    private RelativeLayout mCategoriesLayout;

    private Button mAnyButton;
    private Button mFullTimeButton;
    private Button mPartTimeButton;
    private Button mInternshipButton;
    private Button mApplyFilter;

    private String mButtonText;
    private static String mJobLocationTextViewString;
    private static String mJobCategoryTextViewString;

    private ArrayList<JobDetails> jobsArrayList;
    private JobListAdapter adapter;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_filter, container, false);
        mBackButton = (ImageButton) mBaseView.findViewById(R.id.back_button);
        mResetTextView = (TextView) mBaseView.findViewById(R.id.reset_text_view);
        mJobLocationTextView = (TextView) mBaseView.findViewById(R.id.job_location_text_view);
        mJobCategoryTextView = (TextView) mBaseView.findViewById(R.id.jobs_category_text_view);
        mLocationLayout = (RelativeLayout) mBaseView.findViewById(R.id.location_layout);
        mCategoriesLayout = (RelativeLayout) mBaseView.findViewById(R.id.categories_layout);
        mAnyButton = (Button) mBaseView.findViewById(R.id.button_any);
        mFullTimeButton = (Button) mBaseView.findViewById(R.id.button_full_time);
        mPartTimeButton = (Button) mBaseView.findViewById(R.id.button_part_time);
        mInternshipButton = (Button) mBaseView.findViewById(R.id.button_internship);
        mApplyFilter = (Button) mBaseView.findViewById(R.id.apply_filter);

        mBackButton.setOnClickListener(this);
        mResetTextView.setOnClickListener(this);
        mLocationLayout.setOnClickListener(this);
        mCategoriesLayout.setOnClickListener(this);

        mAnyButton.setOnClickListener(this);
        mFullTimeButton.setOnClickListener(this);
        mPartTimeButton.setOnClickListener(this);
        mInternshipButton.setOnClickListener(this);
        mApplyFilter.setOnClickListener(this);
        jobsArrayList = new ArrayList<>();
        setTextForLocation(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_JOB_LOCATION_NAME));
        setTextForCategory(AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_JOB_CATEGORY_NAME));
        return mBaseView;
    }

    public static void setTextForLocation(String value) {
        mJobLocationTextView.setText(value);
        mJobLocationTextViewString = mJobLocationTextView.getText().toString();

    }

    public static void setTextForCategory(String value) {
        mJobCategoryTextView.setText(value);
        mJobCategoryTextViewString = mJobCategoryTextView.getText().toString();
    }

    @Override
    public void onClick(View v) {
        Button[] btns = new Button[4];
        btns[0] = (Button) mBaseView.findViewById(R.id.button_any);
        btns[1] = (Button) mBaseView.findViewById(R.id.button_full_time);
        btns[2] = (Button) mBaseView.findViewById(R.id.button_part_time);
        btns[3] = (Button) mBaseView.findViewById(R.id.button_internship);
        switch (v.getId()) {
            case R.id.back_button:
                FragmentManager manager = getFragmentManager();
                manager.popBackStack();
                break;
            case R.id.reset_text_view:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Confirmation");
                alertDialogBuilder.setMessage("Do you really want to reset?")
                        .setCancelable(false).setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AppGlobals.clearSettings();
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
                break;
            case R.id.location_layout:
                startActivity(new Intent(getActivity(), FilterLocation.class));
                break;
            case R.id.categories_layout:
                startActivity(new Intent(getActivity(), FilterCategories.class));
                break;
            case R.id.button_any:
                mButtonText = ((Button) v).getText().toString();
                break;
            case R.id.button_full_time:
                mButtonText = ((Button) v).getText().toString();
                break;
            case R.id.button_part_time:
                mButtonText = ((Button) v).getText().toString();
                break;
            case R.id.button_internship:
                mButtonText = ((Button) v).getText().toString();
                break;
            case R.id.apply_filter:
                getJobsList(mButtonText, mJobCategoryTextViewString, mJobLocationTextViewString);
                System.out.println();
                break;
        }
        for (Button button : btns) {
            if (button.getText().equals(mButtonText)) {
                button.setTextColor(Color.RED);
            } else {
                button.setTextColor(Color.BLACK);
            }
        }
    }

    private void getJobsList(String jobType, String category, String location) {
        HttpRequest request = new HttpRequest(AppGlobals.getContext());
        Helpers.showProgressDialog(getActivity(), "Please wait...");
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        Helpers.dismissProgressDialog();
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_OK:
                                Log.i("MY Category", request.getResponseText());
                                try {
                                    JSONArray jsonArray = new JSONArray(request.getResponseText());
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        JobDetails jobDetails = new JobDetails();
                                        jobDetails.setJobType(jsonObject.getString("type"));
                                        jobDetails.setJobTitle(jsonObject.getString("title"));
                                        jobDetails.setScope(jsonObject.getString("scope"));
                                        jobDetails.setSalary(jsonObject.getString("salary"));
                                        jobDetails.setRequirement(jsonObject.getString("requirement"));
                                        jobDetails.setLocation(jsonObject.getString("location"));
                                        jobDetails.setDetailDescription(jsonObject.getString("detailed_description"));
                                        jobsArrayList.add(jobDetails);
                                    }
                                    adapter = new JobListAdapter(jobsArrayList);
//                                    listView.setAdapter(adapter);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

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
                        Helpers.showSnackBar(getView(), "connection time out");
                        break;
                    case HttpRequest.ERROR_NETWORK_UNREACHABLE:
                        Helpers.showSnackBar(getView(), exception.getLocalizedMessage());
                        break;
                }
            }
        });
        request.open("GET", String.format("%sjobs/?type=%s&category=%s&location=%s", AppGlobals.BASE_URL, jobType, category, location));
        request.send();
    }

    private class JobListAdapter extends BaseAdapter {
        private ViewHolder viewHolder;
        private ArrayList<JobDetails> jobDetails;


        public JobListAdapter(ArrayList<JobDetails> jobDetails) {
            this.jobDetails = jobDetails;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.delegate_jobs_list, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.companyName = (TextView) convertView.findViewById(R.id.company_name);
                viewHolder.jobTitle = (TextView) convertView.findViewById(R.id.job_title);
                viewHolder.jobLocation = (TextView) convertView.findViewById(R.id.company_location);
//                viewHolder.companyLogo = (CircleImageView) convertView.findViewById(R.id.job_icon);
                viewHolder.salary = (TextView) convertView.findViewById(R.id.salary);
                viewHolder.jobCategory = (TextView) convertView.findViewById(R.id.part_time_button);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            JobDetails jobDetail = jobDetails.get(position);
            viewHolder.companyName.setText(jobDetail.getJobTitle());
            viewHolder.jobTitle.setText(jobDetail.getJobTitle());
            viewHolder.jobLocation.setText(jobDetail.getLocation());
            viewHolder.salary.setText(jobDetail.getSalary());
            viewHolder.jobCategory.setText(jobDetail.getJobType());

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
        private TextView companyName;
        private TextView jobTitle;
        private TextView jobLocation;
        private CircleImageView companyLogo;
        private TextView salary;
        private TextView jobCategory;
    }
}
