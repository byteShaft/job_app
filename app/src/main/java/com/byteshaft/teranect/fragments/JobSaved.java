package com.byteshaft.teranect.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.teranect.MainActivity;
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

public class JobSaved extends Fragment implements OnClickListener{

    private View mBaseView;
    private ListView mListView;
    private ArrayList<JobDetails> jobsArrayList;
    private SavedJobListAdapter adapter;

    private ImageButton backButton;
    private TextView mFilterTextView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_job_saved, container, false);
        mListView = (ListView) mBaseView.findViewById(R.id.saved_jobs_list);
        mFilterTextView = (TextView) mBaseView.findViewById(R.id.button_filter);
        backButton = (ImageButton) mBaseView.findViewById(R.id.back_button);
        jobsArrayList = new ArrayList<>();
        backButton.setOnClickListener(this);
        mFilterTextView.setOnClickListener(this);
        getJobsList();
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                System.out.println(position);
                return false;
            }
        });
        return mBaseView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_filter:
                MainActivity.getInstance().loadThisFragment(new Filter(), "", "");
                break;
            case R.id.back_button:
                FragmentManager manager = getFragmentManager();
                manager.popBackStack();
                break;
        }
    }


    private void getJobsList() {
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
                                    adapter = new SavedJobListAdapter(jobsArrayList);
                                    mListView.setAdapter(adapter);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        }
                }
            }
        });
        request.open("GET", String.format("%sjobs/saved/", AppGlobals.BASE_URL));
        request.setRequestHeader("Authorization", "Token " +
                AppGlobals.getStringFromSharedPreferences(AppGlobals.KEY_TOKEN));
        request.send();
    }

    /// SavedJobsAdapter

    private class SavedJobListAdapter extends BaseAdapter {
        private ViewHolder viewHolder;
        private ArrayList<JobDetails> jobDetails;


        public SavedJobListAdapter(ArrayList<JobDetails> jobDetails) {
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
                viewHolder.urgentTag = (ImageView) convertView.findViewById(R.id.urgent_tag);
//                viewHolder.companyLogo = (CircleImageView) convertView.findViewById(R.id.job_icon);
                viewHolder.salary = (TextView) convertView.findViewById(R.id.salary);
                viewHolder.jobCategory = (TextView) convertView.findViewById(R.id.part_time_button);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            JobDetails jobDetail = jobDetails.get(position);
            viewHolder.companyName.setText(jobDetail.getCreatorName());
            viewHolder.jobTitle.setText(jobDetail.getJobTitle());
            viewHolder.jobLocation.setText(jobDetail.getLocation_name());
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
        private ImageView urgentTag;
        private TextView companyName;
        private TextView jobTitle;
        private TextView jobLocation;
        private CircleImageView companyLogo;
        private TextView salary;
        private TextView jobCategory;
    }
}
