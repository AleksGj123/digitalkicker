package com.bechtle;

import com.bechtle.model.Match;
import com.bechtle.model.Player;
import com.bechtle.model.Season;
import com.bechtle.service.MatchService;
import com.bechtle.service.MatchTypeService;
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

import java.util.*;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.staticFileLocation;

public class start {

    private static final FormMapping<Player> playerForm = Forms.automatic(Player.class, "player").build();
    private static final FormMapping<Season> seasonForm = Forms.automatic(Season.class, "season").build();
    private static final FormMapping<Match> matchForm = Forms.automatic(Match.class, "match").build();

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
            get("/list",  (req, res) -> {
                MatchService matchService = new MatchService();
                List<Match> allMatches = matchService.getAllMatches();

                HashMap<String, List<Match>> matchesMap = new HashMap<>();
                matchesMap.put("matches", allMatches);

                return new ModelAndView(matchesMap, "views/match/matches.vm");
            }, velocityTemplateEngine);
            get("/new",  (req, res) -> {
                HashMap<String, Object> map = new HashMap<>();

                // init empty form ...
                FormData<Match> formData = new FormData<>(new Match(), ValidationResult.empty);
                FormMapping<Match> filledForm = matchForm.fill(formData);

                // now get all players
                PlayerService playerService = new PlayerService();
                List<Player> players = playerService.getPlayers();

                // ... get all seasons
                SeasonService seasonService = new SeasonService();
                List<Season> allSeasons = seasonService.getAllSeasons();

                // ... get all matchTypes
                MatchTypeService matchTypeService = new MatchTypeService();


                map.put(Constants.MATCH_FORM, filledForm);
                map.put(Constants.PLAYERS, players);
                map.put(Constants.SEASONS, allSeasons);
                //map.put(Constants.MATCH_TYPES, allMatchTypes);

                return new ModelAndView(map, "views/match/match.vm");
            }, velocityTemplateEngine);
            get("/:id", (req, res) -> {
                final String command = req.params(":id");
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

                RequestParams params = new ServletRequestParams(req.raw());
                FormData<Season> bind = seasonForm.bind(params);

                if(seasonForm.bind(params).isValid()){
                    Season season = bind.getData();
                    SeasonService seasonService = new SeasonService();
                    seasonService.createSeason(season);
                }
                return "created new season";
            });
            get("/list",  (req, res) -> {
                SeasonService seasonService = new SeasonService();

                HashMap<String, List<Season>> seasonsMap = new HashMap<>();
                seasonsMap.put("seasons", seasonService.getAllSeasons());
                //seasonsMap.put("date", "date", new DateTool());
                return new ModelAndView(seasonsMap, "views/season/seasons.vm");
            }, velocityTemplateEngine);
            get("/new",  (req, res) -> {
                HashMap<String, Object> map = new HashMap<>();

                FormData<Season> formData = new FormData<>(new Season(), ValidationResult.empty);
                FormMapping<Season> filledForm = seasonForm.fill(formData);

                map.put(Constants.SEASON_FORM, filledForm);
                return new ModelAndView(map, "views/season/season.vm");
            }, velocityTemplateEngine);
            get("/:id",  (req, res) -> {

                String id = req.params(":id");

                SeasonService seasonService = new SeasonService();
                Season season = seasonService.getSeason(Long.parseLong(id));

                FormData<Season> formData = new FormData<>(season, ValidationResult.empty);
                FormMapping<Season> filledForm = seasonForm.fill(formData);

                HashMap<String, Object> seasonsMap = new HashMap<>();
                seasonsMap.put(Constants.SEASON_FORM, filledForm );

                return new ModelAndView(seasonsMap, "views/season/season.vm");
            }, velocityTemplateEngine);
            get("",  (req, res) -> {
                return new ModelAndView(new HashMap<>(), "views/season/season.vm");
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
                FormMapping<Player> playerFormMapping = (FormMapping<Player>) stringFormMappingHashMap.get(Constants.PLAYER_FORM);
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
                HashMap<String, Object> map = new HashMap<>();

                FormData<Player> formData = new FormData<>(new Player(), ValidationResult.empty);
                FormMapping<Player> filledForm = playerForm.fill(formData);

                ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages");
                map.put("messages", bundle);
                map.put(Constants.PLAYER_FORM, filledForm);
                return new ModelAndView(map, "views/player/player.vm");
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
