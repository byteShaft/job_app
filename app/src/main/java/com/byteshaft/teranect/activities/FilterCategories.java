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


public class FilterCategories extends Activity implements HttpRequest.OnReadyStateChangeListener,
        HttpRequest.OnErrorListener {

    private ListView mFilterCategoriesListView;
    private ArrayList<com.byteshaft.teranect.gettersetters.FilterCategories> mCategoriesArrayList;
    private CategoriesAdapter categoriesAdapter;

    private HttpRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_categories);
        mFilterCategoriesListView = (ListView) findViewById(R.id.categories_list_view);
        mCategoriesArrayList = new ArrayList<>();
        getCategoriesFromServer();

        mFilterCategoriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String categoryName = mCategoriesArrayList.get(position).getFilterCategoriesName();
                AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_JOB_CATEGORY_NAME, categoryName);
                System.out.println(categoryName);
                FilterCategories.this.finish();
                Filter.setTextForCategory(categoryName);
                for (int i = 0; i < parent.getCount(); i++) {
                    view = parent.getChildAt(i);
                    if (view != null) {
                        if (i == position) {
                            view.setBackgroundColor(Color.LTGRAY);
                        } else {
                            view.setBackgroundColor(Color.TRANSPARENT);

                        }
                    }
                }
            }
        });
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

    @Override
    public void onReadyStateChange(HttpRequest request, int readyState) {
        switch (readyState) {
            case HttpRequest.STATE_DONE:
                Helpers.dismissProgressDialog();
                switch (request.getStatus()) {
                    case HttpURLConnection.HTTP_OK:
                        System.out.println(request.getResponseText() + "working ");
                        categoriesAdapter = new CategoriesAdapter(mCategoriesArrayList);
                        mFilterCategoriesListView.setAdapter(categoriesAdapter);

                        try {
                            JSONArray categoriesArray = new JSONArray(request.getResponseText());
                            for (int i = 0; i < categoriesArray.length(); i++) {
                                JSONObject jsonObject = categoriesArray.getJSONObject(i);
                                com.byteshaft.teranect.gettersetters.FilterCategories categories = new com.byteshaft.teranect.gettersetters.FilterCategories();
                                categories.setFilterCategoriesName(jsonObject.getString("name"));
                                mCategoriesArrayList.add(categories);
                                categoriesAdapter.notifyDataSetChanged();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                }
        }

    }

    private void getCategoriesFromServer() {
        request = new HttpRequest(this);
        request.setOnReadyStateChangeListener(this);
        request.setOnErrorListener(this);
        request.open("GET", String.format("%sjobs/categories/", AppGlobals.BASE_URL));
        request.send();
        Helpers.showProgressDialog(FilterCategories.this, "Getting Categories...");
    }

    private class CategoriesAdapter extends BaseAdapter {
        private ViewHolder viewHolder;
        private ArrayList<com.byteshaft.teranect.gettersetters.FilterCategories> categoriesArrayList;

        private CategoriesAdapter(ArrayList<com.byteshaft.teranect.gettersetters.FilterCategories> categoriesArrayList) {
            this.categoriesArrayList = categoriesArrayList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.delegate_filter_categories, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.mCategoriesName = (TextView) convertView.findViewById(R.id.categories_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            com.byteshaft.teranect.gettersetters.FilterCategories categoriesName = categoriesArrayList.get(position);
            viewHolder.mCategoriesName.setText(categoriesName.getFilterCategoriesName());
            return convertView;
        }

        @Override
        public int getCount() {
            return categoriesArrayList.size();
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
        TextView mCategoriesName;
    }
}
