package com.hikari.spacecardscollection.Firebase;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.hikari.spacecardscollection.RoomsImplementation.InterfaceListener.RoomFetchListener;
import com.hikari.spacecardscollection.RoomsImplementation.Room;
import com.hikari.spacecardscollection.RoomsImplementation.RoomsChat.SerializableTimestamp;
import com.hikari.spacecardscollection.User.User;
import com.hikari.spacecardscollection.User.InterfaceListeners.UserInfoListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FirestoreDatabase extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private User user;
    private LoginRegisterResult loginRegisterResult;
    private Context context;
    private List<Room> yourRoomList;
    private List<Room> publicRoomList;
    private long quantity = 0;
    private Timestamp coldown;


    public FirestoreDatabase(FirebaseAuth mAuth, FirebaseFirestore db, Context context) {
        this.mAuth = mAuth;
        this.db = db;
        this.context = context;
        this.yourRoomList = new ArrayList<>();
        this.publicRoomList = new ArrayList<>();
    }

    public FirestoreDatabase(FirebaseFirestore db) {
        this.db = db;
        this.yourRoomList = new ArrayList<>();
        this.publicRoomList = new ArrayList<>();
    }


    public User infoUser(String email) {
        CollectionReference userRef = db.collection("Users");
        DocumentReference userDocRef = userRef.document(email);

        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    User user = new User();
                    user.setLevel(document.getString("level"));
                    user.setRegisterDate(document.getString("registerDate"));
                    user.setUserEmail(document.getString("userEmail"));
                    user.setUserName(document.getString("userName"));
                    user.setUserIcon(document.getString("userIcon"));
                    user.setRegisterDate(document.getString("registerDate"));
                    user.setUserId(document.getString("userId"));
                    user.setVirtualMoney(document.getString("virtualMoney"));
                    user.setVirtualPremiumMoney(document.getString("virtualPremiumMoney"));

                    loginRegisterResult.setUser(user);
                } else {
                    //Toast.makeText(this, "Usuario no encontrado en Firestore", Toast.LENGTH_SHORT).show();
                }
            } else {
                //Toast.makeText(this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
            }
        });
        return loginRegisterResult.getUser();
    }

    public void getInfoUserSynchronously(String email, UserInfoListener listener) {
        CollectionReference userRef = db.collection("Users");
        DocumentReference userDocRef = userRef.document(email);

        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    // Crea un nuevo objeto User y configúralo con los datos obtenidos
                    User user = new User();
                    user.setLevel(document.getString("level"));
                    user.setUserEmail(document.getString("userEmail"));
                    user.setUserName(document.getString("userName"));
                    user.setUserIcon(document.getString("userIcon"));
                    user.setRegisterDate(document.getString("registerDate"));
                    user.setUserId(document.getString("userId"));
                    user.setVirtualMoney(document.getString("virtualMoney"));
                    user.setVirtualPremiumMoney(document.getString("virtualPremiumMoney"));
                    user.setGachaQuantity(document.getLong("gachaQuantity"));
                    //user.setPlayerColdown(document.getTimestamp("playerColdown"));

                    // Notifica al listener que la información del usuario ha sido cargada
                    listener.onUserInfoLoaded(user);
                } else {
                    // Notifica al listener que el usuario no fue encontrado
                    listener.onUserInfoError("Usuario no encontrado en Firestore");
                }
            } else {
                // Notifica al listener sobre un error al obtener datos del usuario
                listener.onUserInfoError("Error al obtener datos del usuario");
            }
        });
    }

    public void showYourRooms(String userEmail, RoomFetchListener listener) {
        CollectionReference roomsRef = db.collection("Rooms");

        roomsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    List<String> players = (List<String>) document.get("players");
                    if (players != null && players.contains(userEmail)) {
                        String roomName = document.getString("roomName");


                        Room room = new Room();
                        room.setRoomName(roomName);
                        room.setRoomCreator(document.getString("roomCreator"));
                        room.setRoomCardType(document.getString("roomCardType"));
                        room.setPlayerQuantity(document.getLong("playerQuantity").intValue());
                        room.setRoomPassword(document.getString("roomPassword"));

                        yourRoomList.add(room);

                    }
                }
                listener.onYourRoomsFetched(yourRoomList);
            } else {
                // Manejar el caso de error al obtener las salas
                listener.onError("ERROR");
            }
        });
    }


    public void showPublicRooms2(String userEmail, RoomFetchListener listener) {
        CollectionReference roomsRef = db.collection("Rooms");

        roomsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    List<String> players = (List<String>) document.get("players");

                    if (!players.contains(userEmail)) {

                        String roomState = document.getString("roomState");
                        if (roomState != null && roomState.equals("Public")) {
                            Room room = new Room();
                            room.setRoomName(document.getString("roomName"));
                            room.setRoomCreator(document.getString("roomCreator"));
                            room.setRoomCardType(document.getString("roomCardType"));
                            room.setPlayerQuantity(document.getLong("playerQuantity").intValue());
                            room.setRoomPassword(document.getString("roomPassword"));

                            this.publicRoomList.add(room);
                        }
                    }
                }
                listener.onPublicRoomsFetched(this.publicRoomList);
            } else {
                // Manejar el caso de error al obtener las salas
                listener.onError("ERROR");
            }
        });
    }

    public void showPublicRooms(String userEmail, RoomFetchListener listener) {
        CollectionReference roomsRef = db.collection("Rooms");

        roomsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Room> allRooms = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult()) {
                    List<String> players = (List<String>) document.get("players");
                    if (!players.contains(userEmail) && players.size() < 10) {
                        String roomState = document.getString("roomState");
                        if (roomState != null && roomState.equals("Public")) {
                            Room room = new Room();
                            room.setRoomName(document.getString("roomName"));
                            room.setRoomCreator(document.getString("roomCreator"));
                            room.setRoomCardType(document.getString("roomCardType"));
                            room.setPlayerQuantity(document.getLong("playerQuantity").intValue());
                            room.setRoomPassword(document.getString("roomPassword"));
                            allRooms.add(room);
                        }
                    }
                }

                // Obtener 100 índices aleatorios sin repetición
                List<Integer> randomIndices = new ArrayList<>();
                Random random = new Random();
                int maxIndex = allRooms.size();
                while (randomIndices.size() < 100 && randomIndices.size() < maxIndex) {
                    int randomIndex = random.nextInt(maxIndex);
                    if (!randomIndices.contains(randomIndex)) {
                        randomIndices.add(randomIndex);
                    }
                }

                // Almacenar las 100 salas aleatorias
                List<Room> randomRooms = new ArrayList<>();
                for (int index : randomIndices) {
                    randomRooms.add(allRooms.get(index));
                }

                listener.onPublicRoomsFetched(randomRooms);
            } else {
                // Manejar el caso de error al obtener las salas
                listener.onError("ERROR");
            }
        });
    }


    public void joinRoom(String email, String roomName, String userName) {
        CollectionReference roomsRef = db.collection("Rooms");
        DocumentReference roomDocRef = roomsRef.document(roomName);

        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(roomDocRef);

            if (snapshot.exists()) {
                Long currentPlayers = snapshot.getLong("playerQuantity");
                if (currentPlayers == null) {
                    currentPlayers = 0L;
                }

                // Agregar el correo electrónico a la lista de jugadores
                transaction.update(roomDocRef, "players", FieldValue.arrayUnion(email));

                // Incrementar el número de jugadores en 1
                transaction.update(roomDocRef, "playerQuantity", currentPlayers + 1);


                DocumentReference playerInfoRef = roomDocRef.collection("PlayersInfo").document(user.getUserEmail()); // Referencia al documento del jugador

                Map<String, Object> playerData = new HashMap<>();
                playerData.put("playerColdown", FieldValue.serverTimestamp());
                playerData.put("gachaQuantity", 5);

                transaction.set(playerInfoRef, playerData);


                return null;
            } else {
                throw new FirebaseFirestoreException("La sala " + roomName + " no existe.", FirebaseFirestoreException.Code.NOT_FOUND);
            }
        }).addOnSuccessListener(aVoid -> {
            System.out.println("Se agregó el email correctamente a la sala " + roomName);
        }).addOnFailureListener(e -> {
            System.out.println("Error al agregar el email a la sala " + roomName + ": " + e.getMessage());
        });
    }


}
