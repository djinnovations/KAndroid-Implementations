package com.goldadorn.main.bindings;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.View;

import java.util.Map;

/**
 * Created by bhavinpadhiyar on 3/9/16.
 */
public class BindHelper<T extends ViewDataBinding> {
    private final int variableID;
    private final T binding;
    public BindHelper(View itemView,int variableID)
    {
        this.variableID=variableID;

        T bindingTemp = DataBindingUtil.getBinding(itemView);
        if(bindingTemp==null)
            bindingTemp = DataBindingUtil.bind(itemView);
        binding =bindingTemp;
    }

    public BindHelper(View itemView)
    {
        this(itemView,-1);
    }
    public T getBinding()
    {
        return binding;
    }

    public void setVariable(Object data) {
        if(variableID!=-1)
            setVariable(variableID, data);
    }
    public void setVariable(int variableID,Object data) {
        binding.setVariable(variableID, data);
        binding.executePendingBindings();
    }
    public void setVariables(Map<Integer, Object> data) {
        if(data!=null && data.entrySet().size()!=0) {
            for (Map.Entry<Integer, Object> entry : data.entrySet()) {
                binding.setVariable(entry.getKey(), entry.getValue());
            }
            binding.executePendingBindings();
        }
    }

    public View getView(int resID)
    {
        return binding.getRoot().findViewById(resID);
    }
}
