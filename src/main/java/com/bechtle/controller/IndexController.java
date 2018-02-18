package com.bechtle.controller;

import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.HashMap;

public class IndexController {

    public static ModelAndView index(Request request, Response response){
        final HashMap<String, Object> map = new HashMap<>();
        return new ModelAndView(map, "views/index/index.vm");
    }

}
