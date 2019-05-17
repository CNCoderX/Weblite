package com.topevery.um.entity;

import com.google.gson.annotations.SerializedName;

/**
 * @author wujie
 */
public class Head<T> {
    @SerializedName("IsSuccess")
    private boolean isSuccess;
    @SerializedName("Msg")
    private String message;
    @SerializedName("TotalCount")
    private int count;
    @SerializedName("Data")
    private T data;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
