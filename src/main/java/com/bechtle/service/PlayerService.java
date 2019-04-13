package com.bechtle.service;

import com.bechtle.model.Match;
import com.bechtle.model.Matchtype;
import com.bechtle.model.Player;
import com.bechtle.util.Constants;
import net.formio.FormData;
import net.formio.FormMapping;
import net.formio.Forms;
import net.formio.validation.ConstraintViolationMessage;
import net.formio.validation.Severity;
import net.formio.validation.ValidationResult;
import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class PlayerService extends Service {

    private static final FormMapping<Player> playerForm = Forms.automatic(Player.class, "player").build();

    public PlayerService(EntityManager em) {
        super(em);
    }

    public boolean login(String email, String password) {
        List<Player> foundPlayers = getPlayers().stream()
                .filter(player -> player.getEmail().equals(email))
                .collect(Collectors.toList());

        if (foundPlayers.size() > 0) {
            String salt = BCrypt.gensalt(); // need to safe salt before and get it ...
            Player player = foundPlayers.get(0);
            String submittedPWHashed = BCrypt.hashpw(password, salt);

            if (player.getPasswordHash().equals(submittedPWHashed)) return true;
            else return false;
        } else return false;
    }

    public List<Player> getPlayers() {
        final List<Player> allPlayers = em.createQuery("select p from Player as p").getResultList();
        return allPlayers;
    }


    public List<Player> getSelectablePlayers(Match match) {
        final Set<Long> idSet = new HashSet<>();
        if (match.getKeeperTeam1() != null) {
            idSet.add(match.getKeeperTeam1().getId());
        }
        if (match.getStrikerTeam1() != null) {
            idSet.add(match.getStrikerTeam1().getId());
        }
        if (match.getKeeperTeam2() != null) {
            idSet.add(match.getKeeperTeam2().getId());
        }
        if (match.getStrikerTeam2() != null) {
            idSet.add(match.getStrikerTeam2().getId());
        }

        return em.createQuery("select p from Player as p where p.id not in :idList")
                .setParameter("idList", idSet)
                .getResultList();
    }


    public Player getPlayer(Long id) {
        final Player playerForId = em.find(Player.class, id);
        return playerForId;
    }

    public long createPlayer(Player newPlayer) {
        em.getTransaction().begin();

        final String pw = newPlayer.getPassword();
        final String hashpw = BCrypt.hashpw(pw, BCrypt.gensalt());

        newPlayer.setPasswordHash(hashpw);

        em.persist(newPlayer);
        em.getTransaction().commit();
        return newPlayer.getId();
    }

    public HashMap<String, Object> validatePlayer(final FormData<Player> formData) {

        FormMapping<Player> filledForm = playerForm.fill(formData);

        final Player player = formData.getData();

        final String password = player.getPassword();
        final String passwordRepeat = player.getPasswordRepeat();

        if ((password != null && passwordRepeat != null) && !(password.equals(passwordRepeat))) {

            if (filledForm.getValidationResult().getFieldMessages().isEmpty()) {
                final FormData<Player> playerFormData =
                        new FormData<>(formData.getData(), createValidationResultForSamePassword(formData));
                filledForm = playerForm.fill(playerFormData);
            } else {

                final Map<String, List<ConstraintViolationMessage>> fieldMessages = filledForm.getValidationResult().getFieldMessages();
                final HashMap newFieldMessages = new HashMap(fieldMessages);
                newFieldMessages.put("player-passwordRepeat", getConstraintViolationMessage());
                newFieldMessages.put("player-password", getConstraintViolationMessage());
                final ValidationResult validationResult = new ValidationResult(newFieldMessages, filledForm.getValidationResult().getGlobalMessages());
                final FormData<Player> newPlayerFormData = new FormData<>(formData.getData(), validationResult);
                filledForm = playerForm.fill(newPlayerFormData);
            }

        } else {
            filledForm = playerForm.fill(formData);
        }

        final HashMap<String, Object> stringPlayerHashMap = new HashMap<>();
        stringPlayerHashMap.put(Constants.PLAYER_FORM, filledForm);

        //ValidationResult validationResult = formData.getValidationResult();

        return stringPlayerHashMap;
    }

    private ValidationResult createValidationResultForSamePassword(FormData formData) {
        final Map<String, List<ConstraintViolationMessage>> fieldMessages = new HashMap<>();

        fieldMessages.put("player-passwordRepeat", getConstraintViolationMessage());
        fieldMessages.put("player-password", getConstraintViolationMessage());
        return new ValidationResult(fieldMessages, Collections.emptyList());

    }

    private ArrayList<ConstraintViolationMessage> getConstraintViolationMessage() {
        final LinkedHashMap<String, Serializable> msgArgs = new LinkedHashMap<>();

        msgArgs.put("groups", "");
        msgArgs.put("value", "{constraints.PasswordNotEqual.message}");
        msgArgs.put("hash", "");

        final ConstraintViolationMessage violationMessage = new ConstraintViolationMessage(Severity.ERROR,
                "constraints.PasswordNotEqual.message", "constraints.PasswordNotEqual.message", msgArgs);
        ;

        final ArrayList<ConstraintViolationMessage> constraintViolationMessages = new ArrayList<>();
        constraintViolationMessages.add(violationMessage);
        return constraintViolationMessages;
    }


    public List<Match> getLostDeathmachtesForPlayer(Player player) {

        final Set<Match> matches = player.getMatches();
        final List<Match> lostDeatmatches = matches.stream()
                .filter(match -> match.getMatchtype().equals(Matchtype.DEATH_MATCH))
                .filter(match -> playerHasLost(match, player.getId()))
                .collect(Collectors.toList());

        final List<Match> lostDeatmatchesBo3 = matches.stream()
                .filter(match -> match.getMatchtype().equals(Matchtype.DEATH_MATCH_BO3))
                .filter(match -> playerHasLost(match, player.getId()))
                .collect(Collectors.toList());

        lostDeatmatches.addAll(lostDeatmatchesBo3);

        return lostDeatmatches;
    }

    private boolean playerHasLost(Match match, long playerId) {
        final long idPlayer1 = match.getKeeperTeam1().getId();

        final int goalsTeam1 = match.getGoalsTeam1();
        final int goalsTeam2 = match.getGoalsTeam2();

        // player was in team 1
        if (playerId == idPlayer1) {
            return goalsTeam1 < goalsTeam2;
        } else {
            return goalsTeam1 > goalsTeam2;
        }
    }

    public int getNumberOfPlayedGamesForPlayer(Player player) {
        return player.getMatches().stream()
                .filter(match -> match.getMatchtype() == Matchtype.REGULAR)
                .collect(Collectors.toList()).size();
    }

    public void updatePlayer(Player player) {
        em.getTransaction().begin();
        //Player playerToUpdate = entityManager.find(Player.class, player.getId());
        em.merge(player);
        em.getTransaction().commit();
    }

    public void deletePlayer(Player player) {
    }

}
