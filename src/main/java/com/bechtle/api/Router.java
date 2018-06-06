package com.bechtle.api;

import static spark.Spark.*;
import static com.bechtle.api.util.Parser.*;

import com.bechtle.api.service.PlayerService;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Router {
    final static String PERSISTENCE_UNIT_NAME = "KickerPersistence";
    public final static EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);


    public Router(
            final PlayerService playerService
            ) {

        /**
         * Open Hibernate session with request
         * Close Hibternate session with response
         */

        before((request, response) -> {
            EntityManager session = factory.createEntityManager();
            request.attribute("db", session);
        });

        after((request, response) -> {
            EntityManager session = (EntityManager)request.attribute("db");
            session.close();

        });

        /**
         * Routes for API Calls
         */


        path("/api", () -> {
            get("/players", (req, res) -> playerService.getAll(req));
            get("/players/:id", (req, res) -> playerService.get(req));
            post("/players", (req, res) -> playerService.create(req));
            put("/players", (req, res) -> playerService.update(req));
            delete("/players/:id", (req, res) -> playerService.delete(req));

        });
    }
}
