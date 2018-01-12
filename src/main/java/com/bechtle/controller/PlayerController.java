package com.bechtle.controller;

import com.bechtle.model.Player;
import com.bechtle.service.PlayerService;
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

import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class PlayerController {

    private static final FormMapping<Player> playerForm = Forms.automatic(Player.class, "player").build();

    private static final PlayerService playerService = new PlayerService();

    public static ModelAndView getNewPlayerForm(Request request, Response response){
        HashMap<String, Object> map = new HashMap<>();

        FormData<Player> formData = new FormData<>(new Player(), ValidationResult.empty);
        FormMapping<Player> filledForm = playerForm.fill(formData);

        ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages");
        map.put("messages", bundle);
        map.put(Constants.PLAYER_FORM, filledForm);
        return new ModelAndView(map, "views/player/player.vm");
    }

    public static ModelAndView showPlayer(Request request, Response response){
        String id = request.params(":id");

        Player player = playerService.getPlayer(Long.parseLong(id));

        HashMap<String, Object> map = new HashMap<>();

        FormData<Player> formData = new FormData<>(player, ValidationResult.empty);
        FormMapping<Player> filledForm = playerForm.fill(formData);
        map.put(Constants.PLAYER_FORM, filledForm);
        map.put(Constants.LOKSAFE, player.getLokSafe());
        map.put(Constants.PLAYER, player);

        return new ModelAndView(map, "views/player/edit_player.vm");
    }

    public static ModelAndView createNewPlayer(Request request, Response response){
        RequestParams params = new ServletRequestParams(request.raw());
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
            playerService.createPlayer(player);
            response.redirect("/player/" + player.getId());
        }

        ResourceBundle bundle = ResourceBundle.getBundle("i18n.messages");
        stringFormMappingHashMap.put("messages", bundle);

        return new ModelAndView(stringFormMappingHashMap, "views/player/player.vm");
    }

    public static ModelAndView updatePlayer(Request request, Response response){
        final long playerId = Long.parseLong(request.params(":id"));

        final String forename = request.queryParams("forename");
        final String surname = request.queryParams("surname");
        final String nickname = request.queryParams("nickname");
        final String email = request.queryParams("email");
        final String password = request.queryParams("password");
        final String passwordRepeat = request.queryParams("passwordRepeat");
        final String biography = request.queryParams("biography");
        final boolean loksafe = Boolean.parseBoolean(request.queryParams("loksafe"));

        final Player playerToUpdate = playerService.getPlayer(playerId);
        playerToUpdate.setForename(forename);
        playerToUpdate.setSurname(surname);
        playerToUpdate.setNickname(nickname);
        playerToUpdate.setEmail(email);
        playerToUpdate.setBiography(biography);
        playerToUpdate.setLokSafe(loksafe);

        playerService.updatePlayer(playerToUpdate);

        return new ModelAndView(new HashMap<>(), "views/player/edit_player.vm");
    }

    public static ModelAndView listPlayers(){
        HashMap<String, List<Player>> playersMap = new HashMap<>();
        playersMap.put("players", playerService.getPlayers());
        return new ModelAndView(playersMap, "views/player/players.vm");
    }
}
