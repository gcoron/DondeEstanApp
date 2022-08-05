package com.dondeestanapp.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ServerResponse<T> {

    private int code;
    private List<T> data;
    private Paginator paginator;
    private String status;

    public ServerResponse(int code, List<Object> data, Paginator paginator, String status) {
        this.code = code;
        this.data = setData(data);
        this.paginator = paginator;
        this.status = status;
    }

    public ServerResponse() {
        this.code = 0;
        this.data = new ArrayList<T>();
        this.paginator = null;
        this.status = null;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<T> getData() {
        return data;
    }

    public ArrayList<T> setData(List<Object> data) {
        List<Object> list = data;
        Gson g = new Gson();
        Type listType = new TypeToken<ArrayList<T>>(){}.getType();
        ArrayList<T> castList = g.fromJson(g.toJson(list), listType);
        return castList;
    }

    public Paginator getPaginator() {
        return paginator;
    }

    public void setPaginator(Paginator paginator) {
        this.paginator = paginator;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public T getObjectList(int i) {
        Gson g = new Gson();
        Type listType = new TypeToken<ArrayList<T>>(){}.getType();
        ArrayList<T> castList = g.fromJson(g.toJson(data), listType);
        T obj = castList.get(i);
        return obj;
    }
}
