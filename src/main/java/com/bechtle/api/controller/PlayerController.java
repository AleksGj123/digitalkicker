package com.bechtle.api.controller;
import com.bechtle.api.dao.GenericDAO;
import com.bechtle.api.service.Converter;
import com.bechtle.api.service.ErrorHandler;
import com.bechtle.api.service.UrlParser;
import com.bechtle.model.Player;
import spark.Request;
import spark.Response;
import java.util.List;

public class PlayerController{

    private static final GenericDAO<Player> dao = new GenericDAO<>();

    public static String getAll(Request req, Response res){
        final List<Player> result = dao.findBy("select p from Player as p");
        return Converter.toJSON(result);
    }

    public static String get(Request req, Response res){
        Long id = UrlParser.getId(req);
        final Player result = dao.findById(id, Player.class);
        if(result != null){
            return Converter.toJSON(result);
        }
        res.status(404);
        return new ErrorHandler("No Player found with id " + id).getMessage();
    }

    public static String create(Request req, Response res){
        Player player2Create = Converter.toModel(req.body(), Player.class);
        final Player result = dao.saveOrUpdate(0L, player2Create);
        return Converter.toJSON(result);
    }

    public static String update(Request req, Response res){
        Player player2Create = Converter.toModel(req.body(), Player.class);
        final Player result = dao.saveOrUpdate(0L, player2Create);
        return Converter.toJSON(result);
    }


    public static String delete(Request req, Response res){
        Long playerId = UrlParser.getId(req);
        Player playerToDeactivate = dao.findById(playerId, Player.class);
        playerToDeactivate.setInactive(true);
        Player deactivatedPlayer = dao.saveOrUpdate(playerId, playerToDeactivate);
        return Converter.toJSON(deactivatedPlayer);
    }
}
