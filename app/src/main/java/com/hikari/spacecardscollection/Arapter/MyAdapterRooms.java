package com.hikari.spacecardscollection.Arapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hikari.spacecardscollection.NavigationMenu.RoomsFragment;
import com.hikari.spacecardscollection.R;
import com.hikari.spacecardscollection.RoomsImplementation.JoinRoom.DialogJoinRoom;
import com.hikari.spacecardscollection.RoomsImplementation.Room;
import com.hikari.spacecardscollection.RoomsImplementation.RoomsChat.RoomChat;
import com.hikari.spacecardscollection.User.User;

import java.util.List;

public class MyAdapterRooms extends RecyclerView.Adapter<MyAdapterRooms.MyViewHolder> {

    private final Context context;
    private final List<Room> roomList;
    private final boolean yourRoom;
    private final String email;
    private final RoomsFragment roomsFragment;
    private final User user;

    public MyAdapterRooms(Context context, List<Room> roomList, boolean yourRoom, String email, RoomsFragment roomsFragment, User user) {
        this.context = context;
        this.roomList = roomList;
        this.yourRoom = yourRoom;
        this.email = email;
        this.roomsFragment = roomsFragment;
        this.user = user;
    }

    @NonNull
    @Override
    public MyAdapterRooms.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.room_item_row, parent, false);
        return new MyAdapterRooms.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterRooms.MyViewHolder holder, int position) {
        Room room = roomList.get(position);

        holder.roomName.setText(room.getRoomName());
        holder.roomCreator.setText(room.getRoomCreator());
        String playersQuantity = room.getPlayerQuantity() + "/10";
        holder.players_in_room.setText(playersQuantity);
        holder.roomType.setText(room.getRoomCardType());

        holder.cardViewRoom.setOnClickListener(v -> {
            if (yourRoom) {
                Intent intent = new Intent(context, RoomChat.class);
                intent.putExtra("room", room);
                intent.putExtra("user", user);
                //((AppCompatActivity) context).startActivityForResult(intent, YOUR_REQUEST_CODE);
                context.startActivity(intent);
            } else {
                openDialog(room, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView roomName;
        private TextView roomCreator;
        private TextView roomType;
        private TextView players_in_room;
        private CardView cardViewRoom;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            roomName = itemView.findViewById(R.id.roomName_row);
            roomCreator = itemView.findViewById(R.id.roomCreator_row);
            players_in_room = itemView.findViewById(R.id.players_in_room_row);
            cardViewRoom = itemView.findViewById(R.id.CardViewRoom);
            roomType = itemView.findViewById(R.id.roomType_row);
        }
    }


    public void openDialog(Room room, int position) {
        DialogJoinRoom dialogJoinRoom = new DialogJoinRoom(this, position, roomsFragment);
        dialogJoinRoom.setRoom(room);
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        // Mostrar el diálogo
        dialogJoinRoom.show(fragmentManager, "Join Room");
    }

    /*public void addRoom(Room room) {
        roomList.add(room);
        notifyItemInserted(roomList.size() - 1);
    }

    public List<Room> getRoomList() {
        return roomList;
    }*/

    public void removeRoom(int position) {
        if (position >= 0 && position < roomList.size()) {
            roomList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Context getContext() {
        return context;
    }



}
