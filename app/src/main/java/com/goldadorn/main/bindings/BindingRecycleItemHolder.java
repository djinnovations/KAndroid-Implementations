package com.goldadorn.main.bindings;

import android.databinding.ViewDataBinding;
import android.view.View;

import com.kimeeo.library.listDataView.recyclerView.BaseItemHolder;

import java.util.Map;

/**
 * Created by bhavinpadhiyar on 3/9/16.
 */
public class BindingRecycleItemHolder<T extends ViewDataBinding> extends BaseItemHolder {

    private final BindHelper<T> bindHelper;
    public BindingRecycleItemHolder(View itemView, int variableID) {
        super(itemView);
        bindHelper = new BindHelper<T>(itemView,variableID);
        setVariables(getDefaultMap());
    }

    protected Map<Integer, Object> getDefaultMap() {
        return null;
    }

    public BindingRecycleItemHolder(View itemView)
    {
        this(itemView, -1);
    }
    public BindHelper getBindHelper() {
        return bindHelper;
    }
    public T getBinding()
    {
        return bindHelper.getBinding();
    }
    public void updateItemView(Object item, int position){
        setVariable(item);
        super.updateItemView(item, position);
    }
    public void updateItemView(Object data, View view, int position){

    }
    public void setVariables(Map<Integer,Object> data)
    {
        bindHelper.setVariables(data);
    };
    public void setVariable(Object data)
    {
        bindHelper.setVariable(data);
    }
    public void setVariable(int variableID,Object data) {
        bindHelper.setVariable(variableID, data);
    }
    public View getView(int resID)
    {
        return bindHelper.getView(resID);
    }
}
