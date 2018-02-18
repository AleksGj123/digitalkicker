package com.bechtle;

import com.bechtle.controller.*;
import com.bechtle.util.WebSocketUpdateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import spark.ModelAndView;

import spark.template.velocity.VelocityTemplateEngine;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;

import static spark.Spark.*;

public class Start {
    private final static VelocityTemplateEngine velocityTemplateEngine = new VelocityTemplateEngine();

    public static WebSocketUpdateHandler updateService = new WebSocketUpdateHandler();

    private static Logger logger = LoggerFactory.getLogger(Start.class);

    final static String PERSISTENCE_UNIT_NAME = "KickerPersistence";
    public final static EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);

    public static void main(String[] args) {

        System.setProperty("hibernate.dialect.storage_engine", "myisam");
        port(4444);
        staticFileLocation("/static");

        webSocket("/update", WebSocketUpdateHandler.class);

        before((request, response) -> {
            EntityManager session = factory.createEntityManager();
            request.attribute("em", session);
            //logger.info("pre -> open: " + session.hashCode());
        });

        after((request, response) -> {
            EntityManager session = (EntityManager)request.attribute("em");
            //logger.info("after -> close: " + session.hashCode());
            session.close();

        });

        internalServerError((request, response) -> {
            EntityManager session = (EntityManager)request.attribute("em");
            //logger.info("after -> close: " + session.hashCode());
            session.close();
            return "uuups";
        });

        //index
        // match
        path("/", () -> {
            get("", IndexController::index, velocityTemplateEngine);
        });

        // match
        path("/match", () -> {
            before("/*", (q, a) -> System.out.println("Match ..."));
            post("/new", MatchController::createNewMatch, velocityTemplateEngine);
            put("", MatchController::updateMatch);
            put("/finish", MatchController::finishMatch);
            put("/instant", MatchController::instantFinish);
            delete("/:id", MatchController::deleteMatch);
            get("/dashboard", MatchController::showDashboard, velocityTemplateEngine);
            get("/list", MatchController::listMatches, velocityTemplateEngine);
            get("/new", MatchController::showNewMatchFrom, velocityTemplateEngine);
            get("/:id", MatchController::showPlayer, velocityTemplateEngine);
            /*delete("/remove",  (req, res) -> {
                return "";
            });*/
        });

        // seasons
        path("/season", () -> {
            before("/*", (q, a) -> System.out.println("Season ..."));
            post("", SeasonController::createNewSeason);
            get("/list", SeasonController::listSeasons, velocityTemplateEngine);
            get("/new", SeasonController::showNewSeasonForm, velocityTemplateEngine);
            get("/:id", SeasonController::showSeason, velocityTemplateEngine);
            get("", (req, res) -> {
                return new ModelAndView(new HashMap<>(), "views/season/season.vm");
            }, velocityTemplateEngine);
            /*put("/:status", (req, res) -> {
                final String command = req.params(":status");
                return command;
            });*/
            /*delete("/remove",  (req, res) -> {
                return "";
            });*/
        });


        // players
        path("/player", () -> {
            before("/*", (q, a) -> System.out.println("Player ..."));

            post("/new", PlayerController::createNewPlayer, velocityTemplateEngine);
            get("/list", PlayerController::listPlayers, velocityTemplateEngine);
            get("/new", PlayerController::getNewPlayerForm, velocityTemplateEngine);
            get("/:id", PlayerController::showPlayer, velocityTemplateEngine);
            post("/:id", PlayerController::updatePlayer, velocityTemplateEngine);
            /*delete("/remove",  (req, res) -> {
                return "";
            });*/
        });

        // statistics
        path("/stats", () -> {
            get("", StatisticsController::getStats, velocityTemplateEngine);
        });

        Jedis jSubscriber = new Jedis();

        jSubscriber.psubscribe(new JedisPubSub() {
            @Override
            public void onPMessage(String pattern, String channel, String message) {
                super.onPMessage(pattern, channel, message);
                if(channel.equals("event.goal")){
                    WebSocketUpdateHandler.broadcastMessage("updateMatch", message);
                }
                else if(channel.equals("event.start")){

                }
            }
        }, "event.*");
    }
}
