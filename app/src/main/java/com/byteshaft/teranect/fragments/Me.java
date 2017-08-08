package com.byteshaft.teranect.fragments;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.teranect.R;
import com.byteshaft.teranect.gettersetters.Qualification;
import com.byteshaft.teranect.gettersetters.WorkExp;
import com.byteshaft.teranect.profile.Education;
import com.byteshaft.teranect.profile.PersonalSkills;
import com.byteshaft.teranect.profile.ProfileSettings;
import com.byteshaft.teranect.profile.WorkExperience;
import com.byteshaft.teranect.utils.AppGlobals;
import com.byteshaft.teranect.utils.Helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Me extends Fragment implements View.OnClickListener {

    private View mBaseView;
    private Toolbar toolbarTop;
    private ImageButton settingsButton;
    private TextView title;
    private CircleImageView jobAppliedButton;
    private CircleImageView jobSavedButton;
    private CircleImageView jobResumeButton;

    private CircleImageView mProfileImage;
    private TextView mUserName;
    private TextView mLocation;

    private TextView workExperienceEditTextView;
    private TextView educationEditTextView;
    private TextView personalSkillsEditTextView;

    private TextView skillsTextViews;

    private ListView workList;
    private ListView educationList;
    private ArrayList<Qualification> qualificationArrayList;
    private QualificationAdapter adapter;


    private ArrayList<WorkExp> workExperienceArrayList;
    private WorkExpAdapter workExpAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_me, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        toolbarTop = (Toolbar) mBaseView.findViewById(R.id.profile_toolbar);
        settingsButton = (ImageButton) toolbarTop.findViewById(R.id.button_settings);
        jobAppliedButton = (CircleImageView) mBaseView.findViewById(R.id.job_applied);
        jobSavedButton = (CircleImageView) mBaseView.findViewById(R.id.job_saved);
        jobResumeButton = (CircleImageView) mBaseView.findViewById(R.id.resume);
        mProfileImage = (CircleImageView) mBaseView.findViewById(R.id.user_dp);
        mUserName = (TextView) mBaseView.findViewById(R.id.name_text_view);
        mLocation = (TextView) mBaseView.findViewById(R.id.user_location);
        workList = (ListView) mBaseView.findViewById(R.id.work_list);
        educationList = (ListView) mBaseView.findViewById(R.id.education_list);

        skillsTextViews = (TextView) mBaseView.findViewById(R.id.skills_text_view);

        title = (TextView) toolbarTop.findViewById(R.id.profile_title);
        workExperienceEditTextView = (TextView) mBaseView.findViewById(R.id.work_experience_edit_text_view);
        educationEditTextView = (TextView) mBaseView.findViewById(R.id.education_edit_text_view);
        personalSkillsEditTextView = (TextView) mBaseView.findViewById(R.id.personal_skills_edit_text_view);
        activity.setSupportActionBar(toolbarTop);
        qualificationArrayList = new ArrayList<>();
        adapter = new QualificationAdapter(qualificationArrayList);
        educationList.setAdapter(adapter);
        workExperienceArrayList = new ArrayList<>();
        workExpAdapter = new WorkExpAdapter(workExperienceArrayList);
        workList.setAdapter(workExpAdapter);
        settingsButton.setOnClickListener(this);
        workExperienceEditTextView.setOnClickListener(this);
        educationEditTextView.setOnClickListener(this);
        personalSkillsEditTextView.setOnClickListener(this);

        jobAppliedButton.setOnClickListener(this);
        jobSavedButton.setOnClickListener(this);
        jobResumeButton.setOnClickListener(this);
        return mBaseView;
    }

    private void getQualificationList() {
        HttpRequest requestQualifications = new HttpRequest(getApplicationContext());
        requestQualifications.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
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

    private void getWorkExperienceList() {
        HttpRequest requestQualifications = new HttpRequest(getApplicationContext());
        requestQualifications.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
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

    @Override
    public void onResume() {
        super.onResume();
        qualificationArrayList.clear();
        workExperienceArrayList.clear();

        getQualificationList();
        getWorkExperienceList();

        skillsTextViews.setText(AppGlobals.getStringFromSharedPreferences("skills"));
        if (AppGlobals.isLogin() && !AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_IMAGE_URL).trim().isEmpty()
                && AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_IMAGE_URL) != null) {
            String url = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_IMAGE_URL);
            System.out.println(url + " server image");
            Helpers.getBitMap(url, mProfileImage);

        }

        if (AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_USER_NAME) != null &&
                !AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_USER_NAME).isEmpty()) {
            String userName = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_USER_NAME);
            userName = userName.toLowerCase();
            userName = userName.substring(0, 1).toUpperCase() + userName.substring(1).toLowerCase();

            mUserName.setText(userName);
        }
        if (AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_LOCATION) != null &&
                !AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_LOCATION).trim().isEmpty()) {
            String location = AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_LOCATION);
            String[] latlng = location.split(",");
            double latitude = Double.parseDouble(latlng[0]);
            double longitude = Double.parseDouble(latlng[1]);
            getAddress(latitude, longitude);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_settings:
                startActivity(new Intent(getActivity(), ProfileSettings.class));
                break;
            case R.id.work_experience_edit_text_view:
                startActivity(new Intent(getActivity(), WorkExperience.class));
                break;
            case R.id.education_edit_text_view:
                startActivity(new Intent(getActivity(), Education.class));
                break;
            case R.id.personal_skills_edit_text_view:
                startActivity(new Intent(getActivity(), PersonalSkills.class));
                break;

            case R.id.job_applied:
                loadFragment(new JobApplied());
                break;
            case R.id.job_saved:
                loadFragment(new JobSaved());
                break;
//            case R.id.job_resume:
//                break;

        }

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
                convertView = getActivity().getLayoutInflater().inflate(R.layout.delegate_qualification, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.period = (TextView) convertView.findViewById(R.id.time_period);
//                viewHolder.qualification = (TextView) convertView.findViewById(R.id.et_qualification);
                viewHolder.school = (TextView) convertView.findViewById(R.id.text_school);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final Qualification qualification = qualificationsList.get(position);
            if (qualification.getPeriod() != null && !qualification.getPeriod().trim().isEmpty()) {
                viewHolder.period.setText(qualification.getPeriod());
//                viewHolder.qualification.setText(qualification.getQualification());
                viewHolder.school.setText(qualification.getSchool());
            }
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
                convertView = getActivity().getLayoutInflater().inflate(R.layout.delegate_work_exp, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.period = (TextView) convertView.findViewById(R.id.work_period);
//                viewHolder.title = (TextView) convertView.findViewById(R.id.et_job_title);
                viewHolder.company = (TextView) convertView.findViewById(R.id.text_company);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final WorkExp workExperience = workExperiencesList.get(position);
            viewHolder.period.setText(workExperience.getPeriod());
//            viewHolder.title.setText(workExperience.getJobTitle());
            viewHolder.company.setText(workExperience.getComapnyName());
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
    }

    private class ViewHolder {
        private TextView qualification;
        private TextView school;

        private TextView period;

        private TextView title;
        private TextView company;
    }

    public void loadFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fragmentTransaction.replace(R.id.fragment_container, fragment, backStateName);
        FragmentManager manager = getActivity().getSupportFragmentManager();
        Log.i("TAG", backStateName);
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
        if (!fragmentPopped) {
            fragmentTransaction.addToBackStack(backStateName);
            fragmentTransaction.commit();
        }
    }

    public void getAddress(double latitude, double longitude) {
        final StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(AppGlobals.getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                result.append(address.getLocality()).append(" ").append(address.getCountryName());
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLocation.setText(result.toString());
            }
        });
    }
}