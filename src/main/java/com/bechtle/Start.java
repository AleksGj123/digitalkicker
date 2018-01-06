package com.bechtle;

import com.bechtle.model.*;
import com.bechtle.service.MatchService;
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
import spark.Request;
import spark.Response;
import spark.template.velocity.VelocityTemplateEngine;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static spark.Spark.*;

public class Start {

    private static final FormMapping<Player> playerForm = Forms.automatic(Player.class, "player").build();
    private static final FormMapping<Season> seasonForm = Forms.automatic(Season.class, "season").build();

    public static void main(String[] args) {


        System.setProperty("hibernate.dialect.storage_engine", "myisam");
        final VelocityTemplateEngine velocityTemplateEngine = new VelocityTemplateEngine();

        port(4444);

        staticFileLocation("/static");

        MatchService matchService = new MatchService();

        // match
        path("/match", () -> {
            before("/*", (q, a) -> System.out.println("Match ..."));
            post("/new", (Request req, Response res) -> {

                SeasonService seasonService = new SeasonService();
                PlayerService playerService = new PlayerService();

                String matchType = req.queryParams("matchType");
                // -- you receive only ids --
                String season = req.queryParams("season");
                String keeperTeam1 = req.queryParams("keeperTeam1");
                String keeperTeam2 = req.queryParams("keeperTeam2");
                String strikerTeam1 = req.queryParams("strikerTeam1");
                String strikerTeam2 = req.queryParams("strikerTeam2");

                Season s = seasonService.getSeason(Long.parseLong(season));
                Player kT1 = playerService.getPlayer(Long.parseLong(keeperTeam1));
                Player kT2 = playerService.getPlayer(Long.parseLong(keeperTeam2));

                if (Matchtype.REGULAR.toString().equals(matchType)){

                    Player sT1 = playerService.getPlayer(Long.parseLong(strikerTeam1));
                    Player sT2 = playerService.getPlayer(Long.parseLong(strikerTeam2));

                    matchService.createMatch(kT1,sT1,kT2,sT2,s);
                }
                else {

                    if (Matchtype.DEATH_MATCH.toString().equals(matchType)){
                        matchService.createMatch(kT1, kT2, Matchtype.DEATH_MATCH, s);
                    }
                    else if(Matchtype.DEATH_MATCH_BO3.toString().equals(matchType))
                    {
                        matchService.createMatch(kT1, kT2, Matchtype.DEATH_MATCH_BO3, s);
                    }

                }

                res.redirect("/match/list");
                return "";
            });
            put("", (req, res) ->{

                // -- required attributes
                Long matchId = Long.parseLong(req.queryParams("matchId"));
                int goalsTeam1 = Integer.parseInt(req.queryParams("goalsTeam1"));
                int goalsTeam2 = Integer.parseInt(req.queryParams("goalsTeam2"));

                Long newMatchId = matchService.updateMatch(matchId, goalsTeam1, goalsTeam2);

                if (newMatchId == null){
                    res.redirect("/match/new");
                }
                else{
                    res.redirect("/match/"+matchId.toString());
                }

                return "Ok";
            });
            get("/list",  (req, res) -> {

                List<Match> allMatches = matchService.getAllMatches();

                HashMap<String, List<Match>> matchesMap = new HashMap<>();
                matchesMap.put("matches", allMatches);

                return new ModelAndView(matchesMap, "views/match/matches.vm");
            }, velocityTemplateEngine);
            get("/new",  (req, res) -> {
                HashMap<String, Object> map = new HashMap<>();


                // now get all players
                PlayerService playerService = new PlayerService();
                List<Player> players = playerService.getPlayers();

                // ... get all seasons
                SeasonService seasonService = new SeasonService();
                List<Season> allSeasons = seasonService.getAllSeasons();

                map.put(Constants.PLAYERS, players);
                map.put(Constants.SEASONS, allSeasons);
                map.put(Constants.MATCH_TYPES, Matchtype.values());

                return new ModelAndView(map, "views/match/new_match.vm");
            }, velocityTemplateEngine);
            get("/:id", (req, res) -> {
                final String matchId = req.params(":id");
                return matchId;
            });
            /*delete("/remove",  (req, res) -> {
                return "";
            });*/
        });


        // seasons
        path("/season", () -> {
            before("/*", (q, a) -> System.out.println("Seasons ..."));
            post("", (req, res) -> {

                List<String> collect = req.queryParams().stream()
                        .filter(p -> p.equals("season-startDate") || p.equals("season-endDate"))
                        .collect(Collectors.toList());

                String startDateString = req.queryParams("season-startDate");
                String endDateString = req.queryParams("season-endDate");

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                Instant startDateInstant = LocalDate.parse(startDateString, formatter).atStartOfDay(ZoneId.systemDefault()).toInstant();
                Instant endDateInstant = LocalDate.parse(endDateString, formatter).atStartOfDay(ZoneId.systemDefault()).toInstant();

                Date startDate = Date.from(startDateInstant);
                Date endDate = Date.from(endDateInstant);

                RequestParams params = new ServletRequestParams(req.raw());
                FormData<Season> bind = seasonForm.bind(params);


                Season season = bind.getData();

                season.setStartDate(startDate);
                season.setEndDate(endDate);
                SeasonService seasonService = new SeasonService();
                seasonService.createSeason(season);

                //if(seasonForm.bind(params).isValid()){}

                res.redirect("/season/list");
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
            post("/new", (req, res) -> {

                RequestParams params = new ServletRequestParams(req.raw());
                FormData<Player> bind = playerForm.bind(params);

                PlayerService playerService = new PlayerService();
                HashMap<String, Object> stringFormMappingHashMap = playerService.validatePlayer(playerForm.bind(params));
                FormMapping<Player> playerFormMapping = (FormMapping<Player>) stringFormMappingHashMap.get(Constants.PLAYER_FORM);
                if(playerForm.bind(params).isValid() && (playerFormMapping.getValidationResult().isEmpty()) ){
                    Player player = bind.getData();
                    String loksafe = req.queryParams("player-loksafe");
                    if(Boolean.TRUE.equals(Boolean.parseBoolean(loksafe))){
                        player.setLokSafe(true);
                    }
                    else {
                        player.setLokSafe(false);
                    }
                    playerService.createPlayer(player);
                    res.redirect("/player/" + player.getId());
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

                String id = req.params(":id");

                PlayerService playerService = new PlayerService();
                Player player = playerService.getPlayer(Long.parseLong(id));

                HashMap<String, Object> map = new HashMap<>();

                FormData<Player> formData = new FormData<>(player, ValidationResult.empty);
                FormMapping<Player> filledForm = playerForm.fill(formData);
                map.put(Constants.PLAYER_FORM, filledForm);
                map.put(Constants.LOKSAFE, player.getLokSafe());

                return new ModelAndView(map, "views/player/player.vm");
            }, velocityTemplateEngine);
            put("/:status", (req, res) -> {
                final String command = req.params(":status");
                return command;
            });
            /*delete("/remove",  (req, res) -> {
                return "";
            });*/
        });
    }


}
