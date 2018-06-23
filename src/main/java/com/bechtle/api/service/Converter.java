package com.bechtle.api.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Converter
 */

public class Converter {

    /** convert MODEL to JSON*/
    public static String toJSON(Object obj){

        Gson gson =  new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("dd/MM/yyyy").create();
        return gson.toJson(obj);
    }

    /** convert JSON to MODEL*/
    public static <MODEL> MODEL toModel(String json, Class<MODEL> toClass) {
        Gson gson = new GsonBuilder().setDateFormat("dd/MM/yyyy").create();
        return gson.fromJson(json, toClass);
    }

}
