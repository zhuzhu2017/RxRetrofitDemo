package com.zhuzhu.allen.rxretrofitlib.base;

import java.io.Serializable;

/**
 * 统一格式的数据基类--可以根据接口返回的固定数据格式进行调整
 * Created by allen on 2017/8/23.
 */

public class BaseResultEntity<T> implements Serializable {
    private int code;
    private String msg;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
