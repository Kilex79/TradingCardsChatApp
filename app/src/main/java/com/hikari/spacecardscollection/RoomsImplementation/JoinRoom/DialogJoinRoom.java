package com.hikari.spacecardscollection.RoomsImplementation.JoinRoom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.hikari.spacecardscollection.Arapter.MyAdapterRooms;
import com.hikari.spacecardscollection.Firebase.FirestoreDatabase;
import com.hikari.spacecardscollection.MainActivity;
import com.hikari.spacecardscollection.NavigationMenu.RoomsFragment;
import com.hikari.spacecardscollection.R;
import com.hikari.spacecardscollection.RoomsImplementation.Room;
import com.hikari.spacecardscollection.RoomsImplementation.RoomsChat.RoomChat;
import com.hikari.spacecardscollection.Security.PasswordEncriptation.AuthenticationHelper;

public class DialogJoinRoom extends AppCompatDialogFragment {

    private Room room;
    private String email;
    private String userName;
    private TextView roomNameDialog;
    private EditText roomPasswordEditText;
    private Button joinBtn;

    private FirebaseFirestore db;
    private FirestoreDatabase firestoreDatabaseClass;

    private MyAdapterRooms adapter;
    private int position;
    private RoomsFragment roomsFragment;

    public DialogJoinRoom(MyAdapterRooms adapter, int position, RoomsFragment roomsFragment) {
        this.adapter = adapter;
        this.position = position;
        this.roomsFragment = roomsFragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_activity_join_room, null);

        builder.setView(view);

        db = FirebaseFirestore.getInstance();
        firestoreDatabaseClass = new FirestoreDatabase(db);

        roomNameDialog = view.findViewById(R.id.room_name_dialog);
        roomPasswordEditText = view.findViewById(R.id.editText_room_password);
        joinBtn = view.findViewById(R.id.join_room_btn);


        roomNameDialog.setText(room.getRoomName().toString());

        this.email = ((MainActivity) getActivity()).getUserEmail();
        this.userName = ((MainActivity) getActivity()).getUserName();


        joinBtn.setOnClickListener(v -> {
            String inputPassword = roomPasswordEditText.getText().toString();
            String storedPassword = room.getRoomPassword();

            boolean passwordsMatch = AuthenticationHelper.checkPassword(inputPassword, storedPassword);

            if (passwordsMatch) {
                firestoreDatabaseClass.joinRoom(email, room.getRoomName(), userName);
                if (adapter != null && position != RecyclerView.NO_POSITION) {
                    //Room room = adapter.getRoomList().get(position);
                    int aux = room.getPlayerQuantity();
                    aux++;
                    room.setPlayerQuantity(aux);
                    adapter.removeRoom(position);


                    ((RoomsFragment) roomsFragment).addRoomToYourRooms(room);
                    ((RoomsFragment) roomsFragment).joinedRoom();
                    adapter.notifyDataSetChanged();

                    /*Intent intent = new Intent(getContext(), RoomChat.class);
                    intent.putExtra("room", room);
                    startActivity(intent);*/
                }
                dismiss();
            } else {
                roomPasswordEditText.setText("");
                roomPasswordEditText.setBackgroundResource(R.drawable.rounded_corner_error);
                roomPasswordEditText.setHint("Incorrect Password");
                roomPasswordEditText.setHintTextColor(getResources().getColor(R.color.error));
            }

        });


        return builder.create();
    }


    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}