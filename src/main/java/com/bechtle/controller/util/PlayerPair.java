package com.bechtle.controller.util;

import com.bechtle.model.Player;

import java.util.Objects;

public class PlayerPair {
    private Player firstPlayer;
    private Player secondPlayer;

    public PlayerPair(Player firstPlayer, Player secondPlayer) {
        if (firstPlayer.getId() >= secondPlayer.getId()) {
            this.firstPlayer = firstPlayer;
            this.secondPlayer = secondPlayer;
        } else {
            this.firstPlayer = secondPlayer;
            this.secondPlayer = firstPlayer;
        }
    }

    public Player getFirstPlayer() {
        return firstPlayer;
    }

    public void setFirstPlayer(Player firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    public Player getSecondPlayer() {
        return secondPlayer;
    }

    public void setSecondPlayer(Player secondPlayer) {
        this.secondPlayer = secondPlayer;
    }

    public String getHashable() {
        final long id1 = firstPlayer.getId();
        final long id2 = secondPlayer.getId();
        return id1 >= id2 ? id1 + "-" + id2 : id2 + "-" + id1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerPair that = (PlayerPair) o;
        return Objects.equals(firstPlayer, that.firstPlayer) &&
                Objects.equals(secondPlayer, that.secondPlayer) ||
                Objects.equals(firstPlayer, that.secondPlayer) &&
                        Objects.equals(secondPlayer, that.firstPlayer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHashable());
    }
}
