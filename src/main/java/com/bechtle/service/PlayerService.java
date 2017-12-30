package com.bechtle.service;

import com.bechtle.model.Player;
import spark.ModelAndView;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.List;

public class PlayerService {

    private static EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("KickerPersistence");
    private static EntityManager entityManager = emFactory.createEntityManager();

    public static ModelAndView showPlayer(String playerId){
        return new ModelAndView(new HashMap<>(), "views/player.vm");
    }

    public List<Player> getPlayers(){
        return null;
    }

    public void createPlayer(Player player){

    }

    public void updatePlayer(Player player){

    }

    public void deletePlayer(Player player){

    }


}
