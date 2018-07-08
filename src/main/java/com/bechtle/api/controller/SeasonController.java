package com.bechtle.api.controller;
import com.bechtle.api.dao.GenericDAO;
import com.bechtle.api.service.JSONConverter;
import com.bechtle.api.service.ErrorHandler;
import com.bechtle.api.service.UrlParser;
import com.bechtle.api.dto.ResultWithPagination;
import com.bechtle.api.util.SearchCTX;
import com.bechtle.api.model.Season;
import spark.Request;
import spark.Response;

public class SeasonController {
    private static final GenericDAO<Season> dao = new GenericDAO<>(Season.class);

    public static String findBy(Request req, Response res){
        SearchCTX ctx = UrlParser.getSearchCTX(req);
        final ResultWithPagination result = dao.findBy(ctx);
        return JSONConverter.toJSON(result);
    }

    public static String get(Request req, Response res){
        Long id = UrlParser.getId(req);
        final Season result = dao.findById(id, Season.class);
        if(result != null){
            return JSONConverter.toJSON(result);
        }
        res.status(404);
        return new ErrorHandler("No Season found with id " + id).getMessage();
    }

    public static String create(Request req, Response res){
        Season season2Create = JSONConverter.toModel(req.body(), Season.class);
        Season season = dao.saveOrUpdate(0L, season2Create);
        return JSONConverter.toJSON(season);
    }

    public static String update(Request req, Response res){
        Season season2Update = JSONConverter.toModel(req.body(), Season.class);
        Season season = dao.saveOrUpdate(season2Update.getId(), season2Update);
        return JSONConverter.toJSON(season);
    }


    public static String delete(Request req, Response res){
       return "";
    }
}
