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

import javax.persistence.EntityManager;
import javax.servlet.MultipartConfigElement;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class PlayerController {

    private static final FormMapping<Player> playerForm = Forms.automatic(Player.class, "player").build();

    public static ModelAndView listPlayers(Request request, Response response) {
        final EntityManager em = request.attribute("em");
        final PlayerService playerService = new PlayerService(em);

        final HashMap<String, List<Player>> playersMap = new HashMap<>();
        List<Player> players = playerService.getPlayers();
        players.sort(Comparator.comparing(Player::getForename));

        playersMap.put("players", players);
        return new ModelAndView(playersMap, "views/player/players.vm");
    }

    public static ModelAndView loginPlayer(Request request, Response response) {
        String email = request.queryParams("email").trim();
        String password = request.queryParams("password");
        final EntityManager em = request.attribute("em");
        final PlayerService playerService = new PlayerService(em);

        boolean login = playerService.login(email, password);

        if (login == true) {
            request.session().attribute("loggedInEmail", email);
            // login successful
        } else {
            // login not successful
        }

        return new ModelAndView(new HashMap<>(), "views/player/edit_player.vm");
    }

    public static ModelAndView getNewPlayerForm(Request request, Response response) {
        final EntityManager em = request.attribute("em");
        final Player player = new Player();

        final HashMap<String, Object> map = new HashMap<>();

        final FormData<Player> formData = new FormData<>(player, ValidationResult.empty);
        final FormMapping<Player> filledForm = playerForm.fill(formData);
        map.put(Constants.PLAYER_FORM, filledForm);
        map.put(Constants.LOKSAFE, player.getLokSafe());
        map.put(Constants.PLAYER, player);

        return new ModelAndView(map, "views/player/edit_player.vm");
    }

    public static ModelAndView showPlayer(Request request, Response response) {
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

    public static ModelAndView createNewPlayer(Request request, Response response) {

        final EntityManager em = request.attribute("em");
        final PlayerService playerService = new PlayerService(em);

        final Player player = getPlayerFromParams(request);

        playerService.updatePlayer(player);
        response.redirect("/player/list");

        return new ModelAndView(new HashMap<>(), "views/player/edit_player.vm");
    }

    public static ModelAndView updatePlayer(Request request, Response response) {
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
    private static Player getPlayerFromParams(Request request) {
        return getPlayerFromParams(request, null);
    }

    private static Player getPlayerFromParams(Request request, Player player) {
        FormData<Player> formData = null;
        Player requestPlayer = null;

        // Form was submitted (parameter with the name of submit button is present)
        RequestParams params = new ServletRequestParams(request.raw());
        formData = playerForm.bind(params);
        if (formData.isValid()) {
            // Store edited person and redirect to some "other" page:
            requestPlayer = formData.getData(); // store this person...
        }

        if (player == null) {
            player = new Player();
        }

        if (requestPlayer != null) {
            player.setForename(requestPlayer.getForename());
            player.setSurname(requestPlayer.getSurname());
            player.setNickname(requestPlayer.getNickname());
            player.setEmail(requestPlayer.getEmail());
            player.setBiography(requestPlayer.getBiography());
            player.setLokSafe(requestPlayer.getLokSafe());
            player.setPassword(requestPlayer.getPassword());
            player.setPasswordRepeat(requestPlayer.getPasswordRepeat());
        }

        return player;
    }

    public static ModelAndView getUploadImage(Request request, Response response) {
        final EntityManager em = request.attribute("em");
        final PlayerService playerService = new PlayerService(em);

        final HashMap<String, List<Player>> playersMap = new HashMap<>();
        List<Player> players = playerService.getPlayers();
        players.sort(Comparator.comparing(Player::getForename));

        playersMap.put("players", players);
        return new ModelAndView(playersMap, "views/player/uploadImage.vm");
    }

    public static ModelAndView postUploadImage(Request request, Response response) {
        File uploadDir = new File("target/classes/static/uploads");
        uploadDir.mkdir(); // create the upload directory if it doesn't exist

        try {
            Path tempFile = Files.createTempFile(uploadDir.toPath(), "", "");
            request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
            InputStream input = request.raw().getPart("uploaded_file").getInputStream(); //) { // getPart needs to use same "name" as input field in form
            Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);

            if (Files.size(tempFile) == 0) {
                Files.delete(tempFile);
            } else {
                Path finalFile = Paths.get(uploadDir.toString()
                        , request.queryParams("player") + ".jpg");
                Files.copy(tempFile, finalFile, StandardCopyOption.REPLACE_EXISTING);
                Files.delete(tempFile);
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }

        final EntityManager em = request.attribute("em");
        final PlayerService playerService = new PlayerService(em);

        final HashMap<String, List<Player>> playersMap = new HashMap<>();
        List<Player> players = playerService.getPlayers();
        players.sort(Comparator.comparing(Player::getForename));

        playersMap.put("players", players);

        return new ModelAndView(playersMap, "views/player/uploadImage.vm");
    }
}
