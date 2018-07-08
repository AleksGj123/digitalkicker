package com.bechtle;
import com.bechtle.api.controller.MatchController;
import com.bechtle.api.controller.PlayerController;
import com.bechtle.api.controller.SeasonController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static spark.Spark.*;

public class Start {

    private static Logger logger = LoggerFactory.getLogger(Start.class);

    final static String PERSISTENCE_UNIT_NAME = "KickerPersistence";
    public final static EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);

    public static void main(String[] args) {

        System.setProperty("hibernate.dialect.storage_engine", "myisam");
        port(4444);
        staticFileLocation("/static");

        /**
         * Routes for API Calls
         */
        path("/api", () -> {
            before("/*", (request, response) -> {
                System.out.println("API Call ...");
            });

            get("/players", PlayerController::findBy);
            get("/players/:id", PlayerController::get);
            post("/players", PlayerController::create);
            put("/players", PlayerController::update);
            delete("/players/:id", PlayerController::delete);

            get("/matches", MatchController::findBy);
            get("/matches/:id", MatchController::get);
            post("/matches", MatchController::create);
            put("/matches", MatchController::update);
            delete("/matches/:id", MatchController::delete);

            get("/seasons", SeasonController::findBy);
            get("/seasons/:id", SeasonController::get);
            post("/seasons", SeasonController::create);
            put("/seasons", SeasonController::update);
            delete("/seasons/:id", SeasonController::delete);

            get("/statistics/lokcount", SeasonController::findBy);
            get("/statistics/lokcount/:id", SeasonController::findBy);
            get("/statistics/playedgamescount", SeasonController::findBy);
            get("/statistics/playedgamescount/:id", SeasonController::findBy);
            get("/statistics/conductorcount", SeasonController::findBy);
            get("/statistics/conductorcount/:id", SeasonController::findBy);

        });

        internalServerError((request, response) -> {
            EntityManager session = (EntityManager)request.attribute("em");
            //logger.info("after -> close: " + session.hashCode());
            session.close();
            return "Something went wrong Internal Server Error";
        });
    }
}
