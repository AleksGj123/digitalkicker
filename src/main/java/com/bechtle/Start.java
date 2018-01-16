package com.bechtle;

import com.bechtle.controller.MatchController;
import com.bechtle.controller.PlayerController;
import com.bechtle.controller.SeasonController;
import com.bechtle.controller.StatisticsController;
import com.bechtle.service.UpdateService;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import spark.ModelAndView;

import spark.template.velocity.VelocityTemplateEngine;

import java.util.HashMap;

import static spark.Spark.*;

public class Start {
    private final static VelocityTemplateEngine velocityTemplateEngine = new VelocityTemplateEngine();

    public static UpdateService updateService = new UpdateService();

    public static void main(String[] args) {

        System.setProperty("hibernate.dialect.storage_engine", "myisam");
        port(4444);
        staticFileLocation("/static");

        webSocket("/update", UpdateService.class);


        //index
        // match
        path("/", () -> {
            get("", (req, res) -> {
                return new ModelAndView(new HashMap<>(), "views/carousel.vm");
            }, velocityTemplateEngine);
        });

        // match
        path("/match", () -> {
            before("/*", (q, a) -> System.out.println("Match ..."));
            post("/new", MatchController::createNewMatch, velocityTemplateEngine);
            put("", MatchController::updateMatch);
            put("/finish", MatchController::finishMatch);
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
            get("/list", (req, res) -> PlayerController.listPlayers(), velocityTemplateEngine);
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
                    UpdateService.broadcastMessage("updateMatch", message);
                }
                else if(channel.equals("event.start")){

                }
            }
        }, "event.*");
    }
}
