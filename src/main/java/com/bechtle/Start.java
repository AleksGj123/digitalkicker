package com.bechtle;
import com.bechtle.api.controller.MatchController;
import com.bechtle.api.controller.PlayerController;
import com.bechtle.api.controller.SeasonController;
import com.bechtle.api.service.UrlParser;
import com.bechtle.config.Pac4JConfig;
import org.pac4j.core.config.Config;
import org.pac4j.sparkjava.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static spark.Spark.*;

public class Start {
//    private final static VelocityTemplateEngine velocityTemplateEngine = new VelocityTemplateEngine();
//
//    public static WebSocketUpdateHandler updateService = new WebSocketUpdateHandler();

    private static Logger logger = LoggerFactory.getLogger(Start.class);

    final static String PERSISTENCE_UNIT_NAME = "KickerPersistence";
    public final static EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);

    public static void main(String[] args) {

        System.setProperty("hibernate.dialect.storage_engine", "myisam");
        port(4444);
        staticFileLocation("/static");
//        webSocket("/update", WebSocketUpdateHandler.class);

        /**
         * Routes for API Calls
         */
        path("/api", () -> {
            before("/*", (request, response) -> {
                System.out.println("API Call ...");
                UrlParser.getSearchCTX(request);
            });

            get("/players", PlayerController::getAll);
            get("/players/:id", PlayerController::get);
            post("/players", PlayerController::create);
            put("/players", PlayerController::update);
            delete("/players/:id", PlayerController::delete);

            get("/matches", MatchController::getAll);
            get("/matches/:id", MatchController::get);
            post("/matches", MatchController::create);
            put("/matches", MatchController::update);
            delete("/matches/:id", MatchController::delete);

            get("/seasons", SeasonController::getAll);
            get("/seasons/:id", SeasonController::get);
            post("/seasons", SeasonController::create);
            put("/matches", MatchController::update);
            delete("/matches/:id", MatchController::delete);

        });

        internalServerError((request, response) -> {
            EntityManager session = (EntityManager)request.attribute("em");
            //logger.info("after -> close: " + session.hashCode());
            session.close();
            return "Something went wrong Internal Server Error";
        });



        Jedis jSubscriber = new Jedis();

        jSubscriber.psubscribe(new JedisPubSub() {
            @Override
            public void onPMessage(String pattern, String channel, String message) {
                super.onPMessage(pattern, channel, message);
                if(channel.equals("event.goal")){
//                    WebSocketUpdateHandler.broadcastMessage("updateMatch", message);
                }
                else if(channel.equals("event.start")){

                }
            }
        }, "event.*");
    }
}
