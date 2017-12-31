package com.bechtle.service;

import com.bechtle.model.Player;
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
        return null;
    }

    public long createPlayer(Player newPlayer){
        EntityManager entityManager = JPAUtil.getEntityManager();

        String pw = newPlayer.getPassword();
        String hashpw = BCrypt.hashpw(pw, BCrypt.gensalt());

        newPlayer.setPasswordHash(hashpw);

        entityManager.persist(newPlayer);
        entityManager.getTransaction().commit();

        entityManager.close();
        JPAUtil.shutdown();

        return newPlayer.getId();
    }

    public HashMap validatePlayer(final FormData<Player> formData){

        FormMapping<Player> filledForm = null;

        if(formData.isValid()){
            Player player = formData.getData();
            if( !(player.getPassword().equals(player.getPasswordRepeat())) ){

                FormData<Player> playerFormData =
                        new FormData<>(formData.getData(), createValidationResultForSamePassword(formData));
                filledForm = playerForm.fill(playerFormData);
            }
            else {
                filledForm = playerForm.fill(formData);
            }
        }
        else{
            filledForm = playerForm.fill(formData);
        }

        final HashMap<String, FormMapping<Player>> stringPlayerHashMap = new HashMap<>();
        stringPlayerHashMap.put("playerForm", filledForm);

        ValidationResult validationResult = formData.getValidationResult();
        System.out.println("AAAA");
        System.out.println(validationResult);

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

    }

    public void deletePlayer(Player player){

    }


}
