package com.bechtle.api.service;
import com.bechtle.api.util.Parser;
import com.bechtle.model.Player;
import org.mindrot.jbcrypt.BCrypt;
import spark.Request;

import javax.persistence.EntityManager;
import java.util.List;

public class PlayerService implements Service {

    @Override
    public String getAll(Request req) {
        EntityManager db = Parser.getDbSession(req);
        final List<Object> players = db.createQuery("select p from Player as p").getResultList();
        return Service.toJson(players);
    }

    @Override
    public Object get(Request req) {
        long id = Parser.getPathId(req);
        EntityManager db = Parser.getDbSession(req);
        Player player = db.find(Player.class, id);
        return Service.toJson(player);
    }

    @Override
    public Object create(Request req) {
        EntityManager db = Parser.getDbSession(req);
        Player player2Create = Service.fromJson(req.body(), Player.class);
        db.getTransaction().begin();
        final String pw = player2Create.getPassword();
        final String hashpw = BCrypt.hashpw(pw, BCrypt.gensalt());
        player2Create.setPasswordHash(hashpw);
        db.persist(player2Create);
        db.getTransaction().commit();
        return Service.toJson(player2Create);
    }

    @Override
    public Object update(Request req) {
        EntityManager db = Parser.getDbSession(req);
        Player player2Update = Service.fromJson(req.body(), Player.class);
        db.getTransaction().begin();
        db.merge(player2Update);
        db.getTransaction().commit();
        return Service.toJson(player2Update);
    }

    @Override
    public Object delete(Request req) {
        //TODO MG delete with dependencies? status inactive?
        return null;
    }
}
