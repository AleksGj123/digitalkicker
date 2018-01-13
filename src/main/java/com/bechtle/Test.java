package com.bechtle;

import com.bechtle.model.Player;

public class Test {

    public static void main(String[] args) {
        Player p = new Player();
        p.setForename("Aleks");
        p.setSurname("Gj");
        //p.setNickname("Gj");
        p.setEmail("Gj");
        p.setPassword("Gj");
        p.setPasswordRepeat("Gj");

            System.out.println(p.getNullAndEmptyFields());

    }
}
