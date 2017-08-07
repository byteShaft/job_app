package com.byteshaft.teranect.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.byteshaft.teranect.MainActivity;
import com.byteshaft.teranect.R;

public class Internship extends Fragment implements View.OnClickListener {

    private View mBaseView;
    private TextView title;
    private Toolbar toolbarTop;
    private ImageButton backButton;


    private LinearLayout healthNfitnes;
    private LinearLayout insurance;
    private LinearLayout it;
    private LinearLayout media;
    private LinearLayout science;
    private LinearLayout legalProfessional;
    private LinearLayout nursery;
    private LinearLayout manufacturing;
    private LinearLayout property;
    private LinearLayout sales;
    private LinearLayout transportation;
    private LinearLayout hospitality;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBaseView = inflater.inflate(R.layout.activity_internship, container, false);


        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        toolbarTop = (Toolbar) mBaseView.findViewById(R.id.my_toolbar);
        title = (TextView) toolbarTop.findViewById(R.id.toolbar_title);
        backButton = (ImageButton) toolbarTop.findViewById(R.id.back_button);

        // buttons
        healthNfitnes = (LinearLayout) mBaseView.findViewById(R.id.intern_health_fitness);
        insurance = (LinearLayout) mBaseView.findViewById(R.id.intern_insurance);
        it = (LinearLayout) mBaseView.findViewById(R.id.intern_it);
        media = (LinearLayout) mBaseView.findViewById(R.id.intern_media);
        science = (LinearLayout) mBaseView.findViewById(R.id.intern_science_search);
        legalProfessional = (LinearLayout) mBaseView.findViewById(R.id.intern_legal_professional);
        nursery = (LinearLayout) mBaseView.findViewById(R.id.intern_nursery);
        manufacturing = (LinearLayout) mBaseView.findViewById(R.id.intern_manufacturing);
        property = (LinearLayout) mBaseView.findViewById(R.id.intern_property);
        sales = (LinearLayout) mBaseView.findViewById(R.id.intern_sales);
        transportation = (LinearLayout) mBaseView.findViewById(R.id.intern_transportation);
        hospitality = (LinearLayout) mBaseView.findViewById(R.id.intern_hospitality);

        healthNfitnes.setOnClickListener(this);
        insurance.setOnClickListener(this);
        it.setOnClickListener(this);
        media.setOnClickListener(this);
        science.setOnClickListener(this);
        legalProfessional.setOnClickListener(this);
        nursery.setOnClickListener(this);
        manufacturing.setOnClickListener(this);
        property.setOnClickListener(this);
        sales.setOnClickListener(this);
        transportation.setOnClickListener(this);
        hospitality.setOnClickListener(this);

        backButton.setOnClickListener(this);
        title.setText(R.string.internship_title);
        activity.setSupportActionBar(toolbarTop);
        return mBaseView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button:
                FragmentManager manager = getFragmentManager();
                manager.popBackStack();
                break;
            // buttons
            case R.id.intern_health_fitness:
                System.out.println("OKOKOKOK");
                MainActivity.getInstance().loadThisFragment(new JobsList(), "Health/Fitness", "Internship");
                break;
            case R.id.intern_insurance:
                MainActivity.getInstance().loadThisFragment(new JobsList(), "Insurance", "Internship");
                break;
            case R.id.intern_it:
                MainActivity.getInstance().loadThisFragment(new JobsList(), "IT", "Internship");
                break;
            case R.id.intern_media:
                MainActivity.getInstance().loadThisFragment(new JobsList(), "Media", "Internship");
                break;
            case R.id.intern_science_search:
                MainActivity.getInstance().loadThisFragment(new JobsList(), "Science/Search", "Internship");
                break;
            case R.id.intern_legal_professional:
                MainActivity.getInstance().loadThisFragment(new JobsList(), "Legal/Professional", "Internship");
                break;
            case R.id.intern_nursery:
                MainActivity.getInstance().loadThisFragment(new JobsList(), "Nursery/Pharmacy", "Internship");
                break;
            case R.id.intern_manufacturing:
                MainActivity.getInstance().loadThisFragment(new JobsList(), "Manufacturing/Production", "Internship");
                break;
            case R.id.intern_property:
                MainActivity.getInstance().loadThisFragment(new JobsList(), "Property", "Internship");
                break;
            case R.id.intern_sales:
                MainActivity.getInstance().loadThisFragment(new JobsList(), "Sales", "Internship");
                break;
            case R.id.intern_transportation:
                MainActivity.getInstance().loadThisFragment(new JobsList(), "Transportation", "Internship");
                break;
            case R.id.intern_hospitality:
                MainActivity.getInstance().loadThisFragment(new JobsList(), "Hospitality/Tourism", "Internship");
                break;
        }
    }
}