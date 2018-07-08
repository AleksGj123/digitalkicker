package com.bechtle.api.service;
import spark.QueryParamsMap;
import spark.Request;
import java.util.List;
import java.util.Set;

/**
 * URLParser
 */

public class UrlParser {

    public static long getId(Request req){ return Long.parseLong(req.params(":id")); }

    public static QueryParamsMap getFilters(Request req){
        QueryParamsMap queryMap = req.queryMap("filter");
        return queryMap;
    }

    public static List<String> getIncludes(Request req) {return null;}

}
