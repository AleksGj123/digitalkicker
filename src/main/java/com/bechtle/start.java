package com.bechtle;

import com.bechtle.model.Player;
import com.bechtle.model.Season;
import com.bechtle.service.PlayerService;
import com.bechtle.service.SeasonService;
import com.bechtle.util.Constants;
import net.formio.FormData;
import net.formio.FormMapping;
import net.formio.Forms;
import net.formio.RequestParams;
import net.formio.servlet.ServletRequestParams;
import net.formio.validation.ValidationResult;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.staticFileLocation;

public class start {

    private static final FormMapping<Player> playerForm = Forms.automatic(Player.class, "player").build();

    public static void main(String[] args) {


        System.setProperty("hibernate.dialect.storage_engine", "myisam");
        final VelocityTemplateEngine velocityTemplateEngine = new VelocityTemplateEngine();

        port(4444);

        staticFileLocation("/static");

        // match
        path("/match", () -> {
            before("/*", (q, a) -> System.out.println("Match ..."));
            post("", (req, res) -> {
                return "create new match";
            });
            get("/:id",  (req, res) -> {
                return req.params();
            });
            get("",  (req, res) -> {
                return new ModelAndView(new HashMap<>(), "views/matches.vm");
            }, velocityTemplateEngine);
            put("/:status", (req, res) -> {
                final String command = req.params(":status");
                return command;
            });
            /*delete("/remove",  (req, res) -> {
                return "";
            });*/
        });


        // seasons
        path("/season", () -> {
            before("/*", (q, a) -> System.out.println("Seasons ..."));
            post("", (req, res) -> {

                String name = "TestSeason";
                Date start = new Date();
                Date end = new Date();

                Season s = new Season(name,start,end);
                SeasonService ss = new SeasonService();
                ss.createSeason(s);
                return "created new season";
            });
            get("/:id",  (req, res) -> {
                return new ModelAndView(new HashMap<>(), "views/season.vm");
            }, velocityTemplateEngine);
            get("",  (req, res) -> {
                return new ModelAndView(new HashMap<>(), "views/season.vm");
            }, velocityTemplateEngine);
            put("/:status", (req, res) -> {
                final String command = req.params(":status");
                return command;
            });
            /*delete("/remove",  (req, res) -> {
                return "";
            });*/
        });


        // players
        path("/player", () -> {
            before("/*", (q, a) -> System.out.println("Players ..."));
            post("", (req, res) -> {

                RequestParams params = new ServletRequestParams(req.raw());
                FormData<Player> bind = playerForm.bind(params);

                PlayerService playerService = new PlayerService();
                HashMap<String, Object> stringFormMappingHashMap = playerService.validatePlayer(playerForm.bind(params));
                FormMapping<Player> playerFormMapping = (FormMapping<Player>) stringFormMappingHashMap.get(Constants.PLAYERFORM);
                if(playerForm.bind(params).isValid() && (playerFormMapping.getValidationResult().isEmpty()) ){
                    Player player = bind.getData();
                    playerService.createPlayer(player);
                }

                ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages");
                stringFormMappingHashMap.put("messages", bundle);

                return new ModelAndView(stringFormMappingHashMap, "views/player/player.vm");

            }, velocityTemplateEngine);
            get("/list",  (req, res) -> {
                PlayerService playerService = new PlayerService();

                HashMap<String, List<Player>> playersMap = new HashMap<>();
                playersMap.put("players", playerService.getPlayers());
                return new ModelAndView(playersMap, "views/player/players.vm");
            }, velocityTemplateEngine);
            get("/new",  (req, res) -> {
                return new ModelAndView(new HashMap<>(), "views/player/player.vm");
            }, velocityTemplateEngine);
            get("/:id",  (req, res) -> {
                return new ModelAndView(new HashMap<>(), "views/player/player.vm");
            }, velocityTemplateEngine);
            put("/:status", (req, res) -> {
                final String command = req.params(":status");
                return command;
            });
            /*delete("/remove",  (req, res) -> {
                return "";
            });*/
        });

        /*get("/player", (req, res) -> {

            Player player = new Player();
            player.setForename("Aleks");
            player.setEmail("Aleksandar.Gjurcinov@bechtle.com");
            player.setSurname("Gj");
            player.setPassword("aleks123");
            player.setPasswordRepeat("aleks123");
            player.setBiography("bla");
            FormData<Player> formData = new FormData<>(player, ValidationResult.empty);

            FormMapping<Player> filledForm = playerForm.fill(formData);


            final HashMap<String, Object> stringPlayerHashMap = new HashMap<>();
            stringPlayerHashMap.put("playerForm", filledForm);

            return new ModelAndView(stringPlayerHashMap, "views/player/player.vm");
        }, velocityTemplateEngine);*/

        /*post("/player", (req, res) -> {

            PlayerService playerService = new PlayerService();

            RequestParams params = new ServletRequestParams(req.raw());


            ValidationResult validationResult = playerForm.bind(params).getValidationResult();
            System.out.println("ALeks");
            System.out.println(validationResult);

            return new ModelAndView(playerService.validatePlayer(playerForm.bind(params)), "views/player/player.vm");
        }, velocityTemplateEngine);*/

    }


}
