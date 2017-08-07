package com.byteshaft.teranect.gettersetters;

/**
 * Created by husnain on 7/18/17.
 */

public class FilterCategories {

    public int getFilterCategoriesId() {
        return FilterCategoriesId;
    }

    public void setFilterCategoriesId(int filterCategoriesId) {
        FilterCategoriesId = filterCategoriesId;
    }

    public String getFilterCategoriesName() {
        return FilterCategoriesName;
    }

    public void setFilterCategoriesName(String filterCategoriesName) {
        FilterCategoriesName = filterCategoriesName;
    }

    private int FilterCategoriesId;
    private String FilterCategoriesName;

}
