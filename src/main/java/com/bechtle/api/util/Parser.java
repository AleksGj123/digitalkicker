package com.bechtle.api.util;
import spark.Request;

import javax.persistence.EntityManager;

public class Parser {

    public static EntityManager getDbSession(Request req) {return (EntityManager)req.attribute("db");}

    public static long getPathId(Request req){
        return Long.parseLong(req.params(":id"));
    }

}
