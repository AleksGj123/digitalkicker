package com.bechtle.api.controller;
import com.bechtle.api.dao.GenericDAO;
import com.bechtle.api.service.JSONConverter;
import com.bechtle.api.service.ErrorHandler;
import com.bechtle.api.service.UrlParser;
import com.bechtle.api.dto.ResultWithPagination;
import com.bechtle.api.util.SearchCTX;
import com.bechtle.api.model.Player;
import spark.Request;
import spark.Response;

public class PlayerController{

    private static final GenericDAO<Player> dao = new GenericDAO<>(Player.class);

    public static String findBy(Request req, Response res){
        SearchCTX ctx = UrlParser.getSearchCTX(req);
        final ResultWithPagination result = dao.findBy(ctx);
        return JSONConverter.toJSON(result);
    }

    public static String get(Request req, Response res){
        Long id = UrlParser.getId(req);
        final Player result = dao.findById(id, Player.class);
        if(result != null){
            return JSONConverter.toJSON(result);
        }
        res.status(404);
        return new ErrorHandler("No Player found with id " + id).getMessage();
    }

    public static String create(Request req, Response res){
        Player player2Create = JSONConverter.toModel(req.body(), Player.class);
        final Player result = dao.saveOrUpdate(0L, player2Create);
        return JSONConverter.toJSON(result);
    }

    public static String update(Request req, Response res){
        Player player2Create = JSONConverter.toModel(req.body(), Player.class);
        final Player result = dao.saveOrUpdate(player2Create.getId(), player2Create);
        return JSONConverter.toJSON(result);
    }

    public static String delete(Request req, Response res){
        Long playerId = UrlParser.getId(req);
        Player playerToDeactivate = dao.findById(playerId, Player.class);
        playerToDeactivate.setInactive(true);
        Player deactivatedPlayer = dao.saveOrUpdate(playerId, playerToDeactivate);
        return JSONConverter.toJSON(deactivatedPlayer);
    }
}