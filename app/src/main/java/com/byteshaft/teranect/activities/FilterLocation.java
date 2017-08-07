package com.byteshaft.teranect.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.byteshaft.requests.HttpRequest;
import com.byteshaft.teranect.R;
import com.byteshaft.teranect.fragments.Filter;
import com.byteshaft.teranect.utils.AppGlobals;
import com.byteshaft.teranect.utils.Helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;


public class FilterLocation extends Activity implements HttpRequest.OnErrorListener,
        HttpRequest.OnReadyStateChangeListener {

    private ListView mFilterLocationsListView;
    private ArrayList<com.byteshaft.teranect.gettersetters.FilterLocation> mLocationArrayList;
    private LocationAdapter locationAdapter;

    private HttpRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_location);
        mFilterLocationsListView = (ListView) findViewById(R.id.location_list_view);
        mLocationArrayList = new ArrayList<>();
        getLocationFromServer();
        mFilterLocationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String locationName = mLocationArrayList.get(position).getFilterLocationName();
                AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_JOB_LOCATION_NAME, locationName);
                System.out.println(locationName);
                FilterLocation.this.finish();
                Filter.setTextForLocation(locationName);
                for (int i = 0; i < parent.getCount(); i++) {
                    view = parent.getChildAt(i);
                    if (i == position) {
                        view.setBackgroundColor(Color.LTGRAY);
                    } else {
                        view.setBackgroundColor(Color.TRANSPARENT);

                    }
                }
            }
        });
    }

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                Helpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_OK:
                        System.out.println(request.getResponseText() + "working ");
                        locationAdapter = new LocationAdapter(mLocationArrayList);
                        mFilterLocationsListView.setAdapter(locationAdapter);

                        try {
                            JSONArray locationArry = new JSONArray(request.getResponseText());
                            for (int i = 0; i < locationArry.length(); i++) {
                                JSONObject jsonObject = locationArry.getJSONObject(i);
                                com.byteshaft.teranect.gettersetters.FilterLocation location = new com.byteshaft.teranect.gettersetters.FilterLocation();
                                location.setFilterLocationName(jsonObject.getString("name"));
                                mLocationArrayList.add(location);
                                locationAdapter.notifyDataSetChanged();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                }
        }
    }

    @Override
    public void onError(HttpRequest request, int readyState, short error, Exception exception) {
        if (exception.getLocalizedMessage().equals("Network is unreachable")) {
            Helpers.showSnackBar(findViewById(android.R.id.content), exception.getLocalizedMessage());
        }
        switch (readyState) {
            case HttpRequest.ERROR_CONNECTION_TIMED_OUT:
                Helpers.showSnackBar(findViewById(android.R.id.content), "connection time out");
                break;
        }
        Helpers.dismissProgressDialog();
    }

    private void getLocationFromServer() {
        request = new HttpRequest(this);
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("GET", String.format("%sjobs/locations/", AppGlobals.BASE_URL));
        request.send();
        Helpers.showProgressDialog(FilterLocation.this, "Getting Locations...");
    }

    private class LocationAdapter extends BaseAdapter {
        private ViewHolder viewHolder;
        private ArrayList<com.byteshaft.teranect.gettersetters.FilterLocation> locationArrayList;

        private LocationAdapter(ArrayList<com.byteshaft.teranect.gettersetters.FilterLocation> locationArrayList) {
            this.locationArrayList = locationArrayList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.delegate_filter_location, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.mLocationName = (TextView) convertView.findViewById(R.id.location_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            com.byteshaft.teranect.gettersetters.FilterLocation locationName = locationArrayList.get(position);
            viewHolder.mLocationName.setText(locationName.getFilterLocationName());
            return convertView;
        }

        @Override
        public int getCount() {
            return locationArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

    class ViewHolder {
        TextView mLocationName;
    }
}
