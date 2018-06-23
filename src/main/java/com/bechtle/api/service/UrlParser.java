package com.bechtle.api.service;
import spark.Request;
import java.util.List;

/**
 * URLParser
 */

public class UrlParser {

    public static long getId(Request req){ return Long.parseLong(req.params(":id")); }

    public static List<String> getIncludes(Request req) {return null;}

}
