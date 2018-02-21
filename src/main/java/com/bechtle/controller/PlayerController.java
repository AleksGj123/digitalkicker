package com.bechtle.controller;

import com.bechtle.model.Player;
import com.bechtle.service.PlayerService;
import com.bechtle.util.Constants;
import net.formio.FormData;
import net.formio.FormMapping;
import net.formio.Forms;
import net.formio.validation.ValidationResult;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;

public class PlayerController {

    private static final FormMapping<Player> playerForm = Forms.automatic(Player.class, "player").build();

    public static ModelAndView listPlayers(Request request, Response response){
        final EntityManager em = request.attribute("em");
        final PlayerService playerService = new PlayerService(em);

        final HashMap<String, List<Player>> playersMap = new HashMap<>();
        playersMap.put("players", playerService.getPlayers());
        return new ModelAndView(playersMap, "views/player/players.vm");
    }

    public static ModelAndView loginPlayer(Request request, Response response){
        String email = request.queryParams("email").trim();
        String password = request.queryParams("password");
        final EntityManager em = request.attribute("em");
        final PlayerService playerService = new PlayerService(em);

        boolean login = playerService.login(email, password);

        if (login == true){
            request.session().attribute("loggedInEmail", email);
            // login successful
        }
        else{
            // login not successful
        }

        return new ModelAndView(new HashMap<>(), "views/player/edit_player.vm");
    }

    public static ModelAndView getNewPlayerForm(Request request, Response response){
        final HashMap<String, Object> map = new HashMap<>();

        /*FormData<Player> formData = new FormData<>(new Player(), ValidationResult.empty);
        FormMapping<Player> filledForm = playerForm.fill(formData);*/

        /*ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages");
        map.put("messages", bundle);
        map.put(Constants.PLAYER_FORM, filledForm);*/
        return new ModelAndView(map, "views/player/new_player.vm");
    }

    public static ModelAndView showPlayer(Request request, Response response){
        final EntityManager em = request.attribute("em");
        final PlayerService playerService = new PlayerService(em);

        final String id = request.params(":id");

        final Player player = playerService.getPlayer(Long.parseLong(id));

        final HashMap<String, Object> map = new HashMap<>();

        final FormData<Player> formData = new FormData<>(player, ValidationResult.empty);
        final FormMapping<Player> filledForm = playerForm.fill(formData);
        map.put(Constants.PLAYER_FORM, filledForm);
        map.put(Constants.LOKSAFE, player.getLokSafe());
        map.put(Constants.PLAYER, player);

        return new ModelAndView(map, "views/player/edit_player.vm");
    }

    public static ModelAndView createNewPlayer(Request request, Response response){

        final EntityManager em = request.attribute("em");
        final PlayerService playerService = new PlayerService(em);

        final HashMap<String, Object> map  = new HashMap<>();

        /*RequestParams params = new ServletRequestParams(request.raw());
        FormData<Player> bind = playerForm.bind(params);

        HashMap<String, Object> stringFormMappingHashMap = playerService.validatePlayer(playerForm.bind(params));
        FormMapping<Player> playerFormMapping = (FormMapping<Player>) stringFormMappingHashMap.get(Constants.PLAYER_FORM);
        if(playerForm.bind(params).isValid() && (playerFormMapping.getValidationResult().isEmpty()) ){
            Player player = bind.getData();
            String loksafe = request.queryParams("player-loksafe");
            if(Boolean.TRUE.equals(Boolean.parseBoolean(loksafe))){
                player.setLokSafe(true);
            }
            else {
                player.setLokSafe(false);
            }

            response.redirect("/player/" + player.getId());
        }

        ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages");
        stringFormMappingHashMap.put("messages", bundle);*/

        final Player player = getPlayerFromParams(request, null);
        final List<String> nullFields = player.getNullAndEmptyFields();
        if (!nullFields.isEmpty()){
            map.put(Constants.VALIDATION_EMPTY, nullFields);
        }
        if( !player.getPassword().equals(player.getPasswordRepeat()) ){
            map.put(Constants.VALIDATION_NOT_EQUAL, true);
        }
        else{
            playerService.createPlayer(player);
            //response.redirect("/player/" + player.getId());
            response.redirect("/player/list");
        }
        map.put(Constants.PLAYER, player);

        return new ModelAndView(map, "views/player/new_player.vm");
    }

    public static ModelAndView updatePlayer(Request request, Response response){
        final EntityManager em = request.attribute("em");
        final PlayerService playerService = new PlayerService(em);

        final long playerId = Long.parseLong(request.params(":id"));
        final Player playerFromDB = playerService.getPlayer(playerId);

        final Player player = getPlayerFromParams(request, playerFromDB);

        playerService.updatePlayer(player);
        response.redirect("/player/list");

        return new ModelAndView(new HashMap<>(), "views/player/edit_player.vm");
    }

    // helper
    private static Player getPlayerFromParams(Request request, Player player){

        if(player == null){
            player = new Player();
        }

        final String forename = request.queryParams("forename");
        final String surname = request.queryParams("surname");
        final String nickname = request.queryParams("nickname");
        final String email = request.queryParams("email");
        final String password = request.queryParams("password");
        final String passwordRepeat = request.queryParams("passwordRepeat");
        final String biography = request.queryParams("biography");
        final boolean loksafe = Boolean.parseBoolean(request.queryParams("loksafe"));

        player.setForename(forename);
        player.setSurname(surname);
        player.setNickname(nickname);
        player.setEmail(email);
        player.setBiography(biography);
        player.setLokSafe(loksafe);
        player.setPassword(password);
        player.setPasswordRepeat(passwordRepeat);

        return player;
    }

}
