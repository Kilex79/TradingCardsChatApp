package com.hikari.spacecardscollection.RoomsImplementation.InterfaceListener;

import com.hikari.spacecardscollection.RoomsImplementation.Room;

import java.util.List;

public interface RoomFetchListener {
    void onPublicRoomsFetched(List<Room> rooms);
    void onYourRoomsFetched(List<Room> rooms);
    void onError(String errorMessage);
}
