package com.hikari.spacecardscollection.RoomsImplementation;

import com.hikari.spacecardscollection.RoomsImplementation.RoomsChat.SerializableTimestamp;

import java.io.Serializable;
import java.util.List;

public class Room implements Serializable {

    private String roomName;
    private String roomCreator;
    private String roomCardType;
    private int playerQuantity;
    private List<String> players;
    private String roomPassword;

    public Room(String roomName, String roomCreator, String roomCardType, int playerQuantity, List<String> players) {
        this.roomName = roomName;
        this.roomCreator = roomCreator;
        this.roomCardType = roomCardType;
        this.playerQuantity = playerQuantity;
        this.players = players;
    }

    public Room(String roomName, String roomCreator, String roomCardType, int playerQuantity) {
        this.roomName = roomName;
        this.roomCreator = roomCreator;
        this.roomCardType = roomCardType;
        this.playerQuantity = playerQuantity;
    }

    public Room() {

    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomCreator() {
        return roomCreator;
    }

    public void setRoomCreator(String roomCreator) {
        this.roomCreator = roomCreator;
    }

    public String getRoomCardType() {
        return roomCardType;
    }

    public void setRoomCardType(String roomCardType) {
        this.roomCardType = roomCardType;
    }

    public int getPlayerQuantity() {
        return playerQuantity;
    }

    public void setPlayerQuantity(int playerQuantity) {
        this.playerQuantity = playerQuantity;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> playersList) {
        this.players = playersList;
    }

    public String getRoomPassword() {
        return roomPassword;
    }

    public void setRoomPassword(String roomPassword) {
        this.roomPassword = roomPassword;
    }


}
