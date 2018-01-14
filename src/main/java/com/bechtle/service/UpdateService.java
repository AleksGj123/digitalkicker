package com.bechtle.service;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.json.JSONObject;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

@WebSocket
public class UpdateService {

    private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();

    static Map<Session, String> listeners = new ConcurrentHashMap<>();

    @OnWebSocketConnect
    public void connected(Session session) {

        System.out.println("someone conneced");
        sessions.add(session);
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        sessions.remove(session);
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        System.out.println("Got: " + message);   // Print message
        session.getRemote().sendString(message); // and send it back
    }

    public static void broadcastMessage(String chanel, String message) {
        sessions.stream().filter(Session::isOpen).forEach(session -> {
            try {

                if(chanel.equals("goalsTeam1")){
                    session.getRemote().sendString(
                            String.valueOf(new JSONObject().put("goalsTeam1", message))
                    );
                }
                else if(chanel.equals("goalsTeam2")){
                    session.getRemote().sendString(
                            String.valueOf(new JSONObject().put("goalsTeam2", message))
                    );
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
