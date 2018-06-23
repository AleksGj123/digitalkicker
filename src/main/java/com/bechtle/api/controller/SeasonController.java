package com.bechtle.api.controller;

import com.bechtle.api.dao.GenericDAO;
import com.bechtle.api.service.Converter;
import com.bechtle.api.service.ErrorHandler;
import com.bechtle.api.service.UrlParser;
import com.bechtle.model.Player;
import com.bechtle.model.Season;
import spark.Request;
import spark.Response;

import java.util.List;

public class SeasonController {
    private static final GenericDAO<Season> dao = new GenericDAO<>();

    public static String getAll(Request req, Response res){
        final List<Season> result = dao.findBy("select s from Season as s");
        return Converter.toJSON(result);
    }

    public static String get(Request req, Response res){
        Long id = UrlParser.getId(req);
        final Season result = dao.findById(id, Season.class);
        if(result != null){
            return Converter.toJSON(result);
        }
        res.status(404);
        return new ErrorHandler("No Season found with id " + id).getMessage();
    }

    public static String create(Request req, Response res){
        Season season2Create = Converter.toModel(req.body(), Season.class);
        Season season = dao.saveOrUpdate(0L, season2Create);
        return Converter.toJSON(season);
    }

    public static String update(Request req, Response res){
        Player player2Create = Converter.toModel(req.body(), Player.class);

        return Converter.toJSON("");
    }


    public static String delete(Request req, Response res){
       return "";
    }
}
