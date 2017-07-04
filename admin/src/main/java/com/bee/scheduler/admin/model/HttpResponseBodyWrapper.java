package com.bee.scheduler.admin.model;

import java.util.Date;

public class HttpResponseBodyWrapper {
    private Integer code;
    private Object data;
    private String msg;
    private Date time = new Date();


    public HttpResponseBodyWrapper(Integer code, Object data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public HttpResponseBodyWrapper(Object data) {
        this(1, data, "success");
    }

    public HttpResponseBodyWrapper(Object data, String msg) {
        this(1, data, msg);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
