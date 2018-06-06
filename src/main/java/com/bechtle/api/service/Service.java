package com.bechtle.api.service;

import com.google.gson.Gson;
import spark.Request;

interface Service {
    public String getAll(Request req);

    public Object get(Request req);

    public Object create(Request req);

    public Object update(Request req);

    public Object delete(Request req);

    public static String toJson(Object obj){
        return new Gson().toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> toClass){
        return new Gson().fromJson(json, toClass);
    }
}