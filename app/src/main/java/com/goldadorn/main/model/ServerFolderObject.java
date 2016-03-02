package com.goldadorn.main.model;

import com.goldadorn.main.activities.post.GaleryGrid;
import com.goldadorn.main.utils.URLHelper;
import com.kimeeo.library.listDataView.dataManagers.BaseDataParser;
import com.kimeeo.library.listDataView.dataManagers.IParseableObject;
import com.kimeeo.library.model.IFragmentData;

/**
 * Created by bhavinpadhiyar on 3/2/16.
 */
public class ServerFolderObject implements IFragmentData,IParseableObject
{
    private String name;
    private Object param;
    private String preview;

    public boolean isDir() {
        return isDir;
    }

    public void setIsDir(boolean isDir) {
        this.isDir = isDir;
    }

    private boolean isDir;

    public void dataLoaded(BaseDataParser entireData)
    {
        preview = URLHelper.parseImageURL(name);
    }
    @Override
    public Class getView() {
        return GaleryGrid.class;
    }
    @Override
    public String getID() {
        return name.toString();
    }
    @Override
    public String getName()
    {
        if(name!=null && isDir())
        {
            String name1 =name.substring(name.lastIndexOf("/")+1,name.length());
            name1 = name1.replaceAll("_"," ");
            return name1.toUpperCase();
        }
        return "";
    }
    public String getPath()
    {
        return name;
    }
    @Override
    public Object getParam() {
        return param;
    }
    @Override
    public void setParam(Object o){param = o;}
    @Override
    public Object getActionValue() {
        return null;
    }
    @Override
    public String getActionType() {
        return null;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }
}
