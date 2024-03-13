package com.hikari.spacecardscollection.NavigationMenu;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.hikari.spacecardscollection.Arapter.MyAdapterRooms;
import com.hikari.spacecardscollection.MainActivity;
import com.hikari.spacecardscollection.R;
import com.hikari.spacecardscollection.RoomsImplementation.CreateRoom;
import com.hikari.spacecardscollection.RoomsImplementation.InterfaceListener.RoomUpdateListener;
import com.hikari.spacecardscollection.RoomsImplementation.Room;
import com.hikari.spacecardscollection.RoomsImplementation.RoomsChat.RoomChat;
import com.hikari.spacecardscollection.RoomsImplementation.RoomsChat.SerializableTimestamp;
import com.hikari.spacecardscollection.User.User;
import com.hikari.spacecardscollection.databinding.FragmentRoomsBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class RoomsFragment extends Fragment {

    private Button create_room_btn;
    private Button your_rooms_btn;
    private Button public_rooms_btn;
    private FloatingActionButton refreshRoomsFab;
    private FragmentRoomsBinding binding;
    private List<Room> publicRooms;
    private List<Room> yourRooms;
    private String email;
    private User user;

    private RecyclerView publicRoomsRecyclerView;
    private MyAdapterRooms myAdapterRoomsPublic;
    private RecyclerView yourRoomsRecyclerView;
    private MyAdapterRooms myAdapterRoomsYour;

    private CollectionReference collectionReference;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }
        //
        binding = FragmentRoomsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        publicRooms = new ArrayList<>();
        yourRooms = new ArrayList<>();
        publicRooms = ((MainActivity) getActivity()).getPublicRooms();
        yourRooms = ((MainActivity) getActivity()).getYourRooms();
        email = ((MainActivity) getActivity()).getUserEmail();

        //

        // ========< AUTOMATICAMENTE ACTUALIZA LAS SALAS >========
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection("Rooms");

        collectionReference.addSnapshotListener((value, error) -> {
            if (value != null) {
                for (DocumentChange dc : value.getDocumentChanges()) {
                    QueryDocumentSnapshot document = dc.getDocument();
                    Room room = document.toObject(Room.class);

                    switch (dc.getType()) {
                        case ADDED:
                            // Agregar la sala solo si no existe en la lista
                            if (!listContainsRoom(room)) {

                                addRoomToList(room);

                            }
                            break;
                        case MODIFIED:
                            // Actualizar la sala si ya existe en la lista
                            updateRoomInList(room);
                            break;
                        case REMOVED:
                            // Eliminar la sala de la lista
                            removeRoomFromList(room);
                            break;
                    }
                }
                myAdapterRoomsYour.notifyDataSetChanged();
            }
        });
        // ==================================================

        // RecyclerView
        // Public Rooms
        publicRoomsRecyclerView = binding.publicRomesRecyclerView;
        publicRoomsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        myAdapterRoomsPublic = new MyAdapterRooms(getContext(), publicRooms, false, email, this, user);
        publicRoomsRecyclerView.setAdapter(myAdapterRoomsPublic);
        //

        // Your Rooms
        yourRoomsRecyclerView = binding.yourRomesRecyclerView;
        yourRoomsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        myAdapterRoomsYour = new MyAdapterRooms(requireContext(), yourRooms, true, email, this, user);
        yourRoomsRecyclerView.setAdapter(myAdapterRoomsYour);
        //

        // ID's
        create_room_btn = root.findViewById(R.id.create_room_btn);
        your_rooms_btn = root.findViewById(R.id.your_rooms_btn);
        public_rooms_btn = root.findViewById(R.id.public_rooms_btn);
        refreshRoomsFab = root.findViewById(R.id.refresh_rooms_fab);
        //

        // Create Room
        create_room_btn.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                User user = ((MainActivity) getActivity()).getUser();
                Intent intent = new Intent(getContext(), CreateRoom.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
        //


        // Show diferents RecyclerViews
        // Your Rooms RecView
        your_rooms_btn.setOnClickListener(v -> {
            your_rooms_btn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.selected));
            public_rooms_btn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.light));
            yourRoomsRecyclerView.setVisibility(View.VISIBLE);
            publicRoomsRecyclerView.setVisibility(View.GONE);
            refreshRoomsFab.setVisibility(View.GONE);
        });

        // Public Rooms RecView
        public_rooms_btn.setOnClickListener(v -> {
            public_rooms_btn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.selected));
            your_rooms_btn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.light));
            publicRoomsRecyclerView.setVisibility(View.VISIBLE);
            yourRoomsRecyclerView.setVisibility(View.GONE);
            refreshRoomsFab.setVisibility(View.VISIBLE);
        });
        //


        // FAB Action

        //

        return root;
    }


    // ========< AUTOMATICAMENTE ACTUALIZA LAS SALAS >========


    // Métodos auxiliares
    private boolean listContainsRoom(Room room) {
        for (Room item : yourRooms) {
            if (item.getRoomName().equals(room.getRoomName())) {
                return true;
            }
        }
        return false;
    }

    private void addRoomToList(Room room) {
        if (room.getPlayers() != null && room.getPlayers().contains(email)) {
            yourRooms.add(room);
        }
    }

    private void updateRoomInList(Room room) {
        for (int i = 0; i < yourRooms.size(); i++) {
            if (yourRooms.get(i).getRoomName().equals(room.getRoomName())) {
                yourRooms.set(i, room);
                return;
            }
        }
    }

    private void removeRoomFromList(Room room) {
        yourRooms.removeIf(item -> item.getRoomName().equals(room.getRoomName()));
    }
    // ======================================================

    public void addRoomToYourRooms(Room room) {
        yourRooms.add(room);
        myAdapterRoomsYour.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
    }

    @Override
    public void onResume() {
        super.onResume();
        // Actualizar los datos del RecyclerView
        if (myAdapterRoomsYour != null) {
            myAdapterRoomsYour.notifyDataSetChanged();
        }
    }

    public void joinedRoom() {
        your_rooms_btn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.selected));
        public_rooms_btn.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.light));
        yourRoomsRecyclerView.setVisibility(View.VISIBLE);
        publicRoomsRecyclerView.setVisibility(View.GONE);
        refreshRoomsFab.setVisibility(View.GONE);
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

    }
}