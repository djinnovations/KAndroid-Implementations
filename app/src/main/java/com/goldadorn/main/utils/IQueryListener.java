package com.goldadorn.main.utils;

/**
 * Created by Kiran BH on 01/03/16.
 */
public interface IQueryListener {

    void onQueryChange(String newQuery);
    void onQuerySubmit(String query);
}
