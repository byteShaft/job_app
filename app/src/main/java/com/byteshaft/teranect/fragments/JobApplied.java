package com.byteshaft.teranect.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.byteshaft.teranect.MainActivity;
import com.byteshaft.teranect.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class JobApplied extends Fragment implements View.OnClickListener {

    private View mBaseView;
    private ListView mListView;
    private ArrayList<String[]> jobsArrayList;
    private Adapter adapter;
    private ImageButton backButton;
    private TextView mFilterTextView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.fragment_job_applied, container, false);
        mListView = (ListView) mBaseView.findViewById(R.id.jobs_list);
        mFilterTextView = (TextView) mBaseView.findViewById(R.id.button_filter);
        backButton = (ImageButton) mBaseView.findViewById(R.id.back_button);
        jobsArrayList = new ArrayList<>();

        jobsArrayList.add(new String[]{"", "Bilal", "Hello world", "12:00"});
        jobsArrayList.add(new String[]{"", "Bilal", "Hello world", "12:00"});
        jobsArrayList.add(new String[]{"", "Bilal", "Hello world", "12:00"});
        jobsArrayList.add(new String[]{"", "Bilal", "Hello world", "12:00"});
        jobsArrayList.add(new String[]{"", "Bilal", "Hello world", "12:00"});
        jobsArrayList.add(new String[]{"", "Bilal", "Hello world", "12:00"});
        jobsArrayList.add(new String[]{"", "Bilal", "Hello world", "12:00"});
        jobsArrayList.add(new String[]{"", "Bilal", "Hello world", "12:00"});
        jobsArrayList.add(new String[]{"", "Bilal", "Hello world", "12:00"});
        jobsArrayList.add(new String[]{"", "Bilal", "Hello world", "12:00"});
        jobsArrayList.add(new String[]{"", "Bilal", "Hello world", "12:00"});
        adapter = new Adapter(getActivity(), jobsArrayList);
        mListView.setAdapter(adapter);
        backButton.setOnClickListener(this);
        mFilterTextView.setOnClickListener(this);
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


    private class Adapter extends ArrayAdapter<String> {

        private ArrayList<String[]> jobsList;
        private ViewHolder viewHolder;

        public Adapter(Context context, ArrayList<String[]> messagesList) {
            super(context, R.layout.delegate_jobs_list);
            this.jobsList = messagesList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.delegate_jobs_applied,
                        parent, false);
                viewHolder = new ViewHolder();

                viewHolder.companyImage = (CircleImageView) convertView.findViewById(R.id.job_icon);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // set
            return convertView;
        }

        @Override
        public int getCount() {
            return jobsList.size();
        }
    }

    private class ViewHolder {
        private TextView companyTitle;
        private TextView jobTitle;
        private TextView jobLocation;
        private TextView sallery;
        private TextView companyName;
        private Button partTimeButton;
        private Button jobSaveButton;
        private Button jobApplyButton;
        private CircleImageView companyImage;
    }
}
