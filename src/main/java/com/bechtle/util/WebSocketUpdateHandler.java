//package com.bechtle.util;
//
//import com.bechtle.Start;
//import com.bechtle.model.Match;
//import com.bechtle.service.MatchService;
//import org.eclipse.jetty.websocket.api.*;
//import org.eclipse.jetty.websocket.api.annotations.*;
//import org.json.JSONObject;
//
//import javax.persistence.EntityManager;
//import java.io.*;
//import java.util.*;
//import java.util.concurrent.*;
//
//@WebSocket
//public class WebSocketUpdateHandler {
//
//    private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();
//
//    static Map<Session, String> listeners = new ConcurrentHashMap<>();
//
//    @OnWebSocketConnect
//    public void connected(Session session) {
//
//        //System.out.println("someone conneced");
//        sessions.add(session);
//    }
//
//    @OnWebSocketClose
//    public void closed(Session session, int statusCode, String reason) {
//        sessions.remove(session);
//    }
//
//    @OnWebSocketMessage
//    public void message(Session session, String message) throws IOException {
//        //System.out.println("Got: " + message);   // Print message
//        session.getRemote().sendString(message); // and send it back
//    }
//
//    public static void broadcastMessage(String type, String message) {
//        EntityManager em = null;
//        try {
//            em = Start.factory.createEntityManager();
//            final MatchService matchService = new MatchService(em);
//            final Match currentMatch = matchService.getCurrentMatch();
//
//
//
//            sessions.stream().filter(Session::isOpen).forEach(session -> {
//                try {
//                    if(type.equals("updateMatch")){
//                        session.getRemote().sendString(
//                                String.valueOf(new JSONObject()
//                                        .put("goalsTeam1", currentMatch.getGoalsTeam1())
//                                        .put("goalsTeam2", currentMatch.getGoalsTeam2()))
//                        );
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });
//        } finally {
//            if (em != null) {
//                em.close();
//            }
//        }
//    }
//}
