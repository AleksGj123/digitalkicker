package com.bechtle.api.service;
import com.bechtle.api.util.SearchCTX;
import spark.QueryParamsMap;
import spark.Request;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * URLParser
 */

public class UrlParser {

    public static long getId(Request req){ return Long.parseLong(req.params(":id")); }

    public static SearchCTX getSearchCTX(Request req){
        Map<String, String[]> filterQueryMap = req.queryMap("filter").toMap();
        Map<String, String[]> sortingQueryMap = req.queryMap("column").toMap();
        String page = req.queryParams("page");
        String size = req.queryParams("size");
        return new SearchCTX(page, size, filterQueryMap, sortingQueryMap);
    }
}
