package com.goldadorn.main.model;

import com.kimeeo.library.model.IFragmentData;

/**
 * Created by bhavinpadhiyar on 1/11/16.
 */
public class NavigationDataObject implements IFragmentData {
    public  Object param;

    public void setView(Class view) {
        this.view = view;
    }

    public  Class view;
    public  String id;

    public int getIdInt() {
        return idInt;
    }

    public  int idInt;
    public  String name;
    public  Object actionValue;
    public  String actionType;

    public Class getView()
    {
        return view;
    };
    public String getID()
    {
        return id;
    }
    public String getName()
    {
        return name;
    }
    public String getTitle(){return getName();}
    public Object getParam()
    {
        return param;
    }
    public void setParam(Object value){param=value;}
    public Object getActionValue()
    {
        return actionValue;
    }
    public String getActionType()
    {
        return actionType;
    }

    public NavigationDataObject(int id, String actionType, Object actionValue)
    {
        this.idInt =id;
        this.id = id+"";
        this.name = id+"";
        this.actionType = actionType;
        this.actionValue = actionValue;
    }
    public NavigationDataObject(int id, String title, String actionType, Object actionValue, Class view)
    {
        this.idInt =id;
        this.id = id+"";
        this.name = title;
        this.actionType = actionType;
        this.actionValue = actionValue;
        this.view = view;
    }
    public NavigationDataObject(int id, String title, String actionType, Class view)
    {
        this.idInt =id;
        this.id = id+"";
        this.name = title;
        this.actionType = actionType;
        this.view = view;
    }
    public NavigationDataObject(int id, String actionType)
    {
        this.idInt =id;
        this.id = id+"";
        this.name = id+"";
        this.actionType = actionType;
    }
    public NavigationDataObject()
    {

    }

    public boolean isType(String actionType) {
        return this.actionType.equals(actionType);
    }

    public static class ACTION_TYPE
    {
        public static final String ACTION_TYPE_LOGOUT="logout";
        public static final String ACTION_TYPE_FRAGMENT_VIEW="fragmentView";
        public static final String ACTION_TYPE_FRAGMENT_WEB_VIEW="fragmentWebView";
        public static final String ACTION_TYPE_ACTIVITY="activity";
        public static final String ACTION_TYPE_WEB_EXTERNAL ="webExternal";
        public static final String ACTION_TYPE_WEB_ACTIVITY ="webActivity";
        public static final String ACTION_TYPE_TEXT_SHARE ="shareText";
    }

}