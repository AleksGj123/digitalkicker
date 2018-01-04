package com.bechtle.service;

import com.bechtle.model.Player;
import com.bechtle.util.Constants;
import com.bechtle.util.JPAUtil;
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

public class PlayerService {

    private static final FormMapping<Player> playerForm = Forms.automatic(Player.class, "player").build();

    public List<Player> getPlayers(){
        EntityManager entityManager = JPAUtil.getEntityManager();

        entityManager.getTransaction().begin();
        List<Player> allPlayers = entityManager.createQuery("select p from Player as p").getResultList();

        entityManager.close();
        JPAUtil.shutdown();
        return allPlayers;
    }

    public Player getPlayer(Long id){
        EntityManager entityManager = JPAUtil.getEntityManager();
        entityManager.getTransaction().begin();

        Player playerForId = entityManager.find(Player.class, id);

        JPAUtil.shutdown();
        return playerForId;
    }

    public long createPlayer(Player newPlayer){
        EntityManager entityManager = JPAUtil.getEntityManager();

        entityManager.getTransaction().begin();

        String pw = newPlayer.getPassword();
        String hashpw = BCrypt.hashpw(pw, BCrypt.gensalt());

        newPlayer.setPasswordHash(hashpw);

        entityManager.persist(newPlayer);
        entityManager.getTransaction().commit();

        entityManager.close();
        JPAUtil.shutdown();

        return newPlayer.getId();
    }

    public HashMap<String, Object> validatePlayer(final FormData<Player> formData){

        FormMapping<Player> filledForm = playerForm.fill(formData);

        Player player = formData.getData();

        String password = player.getPassword();
        String passwordRepeat = player.getPasswordRepeat();

        if( (password!= null && passwordRepeat!= null) && !(password.equals(passwordRepeat)) ){

            if (filledForm.getValidationResult().getFieldMessages().isEmpty()){
                FormData<Player> playerFormData =
                        new FormData<>(formData.getData(), createValidationResultForSamePassword(formData));
                filledForm = playerForm.fill(playerFormData);
            }
            else{

                Map<String, List<ConstraintViolationMessage>> fieldMessages = filledForm.getValidationResult().getFieldMessages();
                HashMap newFieldMessages = new HashMap(fieldMessages);
                newFieldMessages.put("player-passwordRepeat", getConstraintViolationMessage());
                newFieldMessages.put("player-password", getConstraintViolationMessage());
                ValidationResult validationResult = new ValidationResult(newFieldMessages, filledForm.getValidationResult().getGlobalMessages());
                FormData<Player> newPlayerFormData = new FormData<>(formData.getData(), validationResult);
                filledForm = playerForm.fill(newPlayerFormData);
            }

        }
        else{
            filledForm = playerForm.fill(formData);
        }

        final HashMap<String, Object> stringPlayerHashMap = new HashMap<>();
        stringPlayerHashMap.put(Constants.PLAYER_FORM, filledForm);

        //ValidationResult validationResult = formData.getValidationResult();

        return stringPlayerHashMap;
    }

    private ValidationResult createValidationResultForSamePassword(FormData formData){
        Map<String, List<ConstraintViolationMessage>> fieldMessages = new HashMap<>();

        fieldMessages.put("player-passwordRepeat", getConstraintViolationMessage());
        fieldMessages.put("player-password", getConstraintViolationMessage());
        return new ValidationResult(fieldMessages, Collections.emptyList());

    }

    private ArrayList<ConstraintViolationMessage> getConstraintViolationMessage(){
        LinkedHashMap<String, Serializable> msgArgs = new LinkedHashMap<>();

        msgArgs.put("groups", "");
        msgArgs.put("value", "{constraints.PasswordNotEqual.message}");
        msgArgs.put("hash", "");

        ConstraintViolationMessage  violationMessage =  new ConstraintViolationMessage(Severity.ERROR,
                "constraints.PasswordNotEqual.message", "constraints.PasswordNotEqual.message", msgArgs);;

        ArrayList<ConstraintViolationMessage> constraintViolationMessages = new ArrayList<>();
        constraintViolationMessages.add(violationMessage);
        return constraintViolationMessages;
    }

    public void updatePlayer(Player player){

        EntityManager entityManager = JPAUtil.getEntityManager();

        entityManager.getTransaction().begin();
        Player playerToUpdate = entityManager.find(Player.class, player.getId());

        entityManager.merge(playerToUpdate);
        entityManager.getTransaction().commit();

        JPAUtil.shutdown();
    }

    public void deletePlayer(Player player){

    }


}
