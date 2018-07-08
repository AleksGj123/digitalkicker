package com.bechtle;

import com.bechtle.model.Player;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class Test {

    public static void main(String[] args) {
        Player p = new Player();
        p.setForename("Aleks");
        p.setSurname("Gj");
        //p.setNickname("Gj");
        p.setEmail("Gj");
        p.setPassword("Gj");

            System.out.println(p.getNullAndEmptyFields());


        JSONObject jsonObject = new JSONObject();

        jsonObject.put("goalsTeam1", 0).put("goalsTeam2", 3);
        //jsonObject.put("goalsTeam2", 3);

        System.out.println(jsonObject);

        //Jedis jedis = new Jedis("localhost");
        Jedis jSubscriber = new Jedis();
        jSubscriber.subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {

                System.out.println(channel + "-" + message);
                // handle message
            }
        }, "test");
    }
}
