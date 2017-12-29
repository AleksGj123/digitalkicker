package service;

import model.Player;
import spark.ModelAndView;

import java.util.HashMap;
import java.util.List;

public class PlayerService {

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
