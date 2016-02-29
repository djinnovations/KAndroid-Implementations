package com.goldadorn.main.model;

/**
 * Created by bhavinpadhiyar on 11/9/15.
 */
public class ServerError {

    public boolean getSuccess()
    {
        if(status!=null)
            return true;
        else if(status!=null && status.equals("Failed"))
            return false;
        return true;
    }


    private String status;
    private String msg;

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
}
