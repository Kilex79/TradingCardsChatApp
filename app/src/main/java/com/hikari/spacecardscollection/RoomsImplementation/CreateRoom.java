package com.hikari.spacecardscollection.RoomsImplementation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.hikari.spacecardscollection.R;
import com.hikari.spacecardscollection.Security.PasswordEncriptation.PasswordHelper;
import com.hikari.spacecardscollection.User.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateRoom extends AppCompatActivity {

    private Button room_exit_btn;
    private Button room_create_btn;
    private String[] cardsType = {"Monke", "Space"};
    private String[] publicPrivate = {"Public", "Private"};
    private AutoCompleteTextView autoCompletText;
    private AutoCompleteTextView autoCompletText2;
    private ArrayAdapter<String> adapterItems;
    private String cardsTypeString;
    private String roomPublicPrivate;

    private EditText roomName;
    private EditText roomPassword;

    private FirebaseFirestore db;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        Intent intent = getIntent();
        if (intent.hasExtra("user")) {
            user = (User) intent.getSerializableExtra("user");
        }

        // Inicializa Firebase Firestore
        db = FirebaseFirestore.getInstance();
        //

        // Ids
        ids();
        //

        //
        adapterItems = new ArrayAdapter<>(this, R.layout.list_items, cardsType);
        autoCompletText.setAdapter(adapterItems);

        autoCompletText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                cardsTypeString = item;
            }
        });


        adapterItems = new ArrayAdapter<>(this, R.layout.list_items, publicPrivate);
        autoCompletText2.setAdapter(adapterItems);

        autoCompletText2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                roomPublicPrivate = item;
            }
        });
        //

        // Exit
        room_exit_btn.setOnClickListener(v -> {
            finish();
        });
        //

        // Create Room
        room_create_btn.setOnClickListener(v -> {
            createRoom();
        });
        //

    }

    public void ids() {
        room_exit_btn = findViewById(R.id.room_exit_btn);
        room_create_btn = findViewById(R.id.room_create_btn);
        autoCompletText = findViewById(R.id.auto_complete_text);
        autoCompletText2 = findViewById(R.id.auto_complete_text2);
        roomName = findViewById(R.id.editText_room_name);
        roomPassword = findViewById(R.id.editText_room_password);
    }


    public void createRoom() {
        String roomNameAux = roomName.getText().toString().trim();
        String roomPasswordAux = roomPassword.getText().toString();

        // Verificamos que la longitud del nombre no sea mayor a 20 carácteres
        if (roomNameAux.length() > 20) {
            Toast.makeText(CreateRoom.this, "El nombre de la sala debe tener como máximo 20 caracteres", Toast.LENGTH_SHORT).show();
            return; // Salir del método si el nombre excede el límite
        }

        DocumentReference roomRef = db.collection("Rooms").document(roomNameAux);

        // Verificar la disponibilidad del nombre de la sala
        roomRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    // El nombre de la sala ya está en uso
                    Toast.makeText(CreateRoom.this, "Nombre de sala ya está en uso", Toast.LENGTH_SHORT).show();
                } else {
                    // El nombre de la sala está disponible, proceder con la creación
                    createRoomDocument(roomNameAux, roomPasswordAux);
                }
            } else {
                // Manejar errores en la consulta
                Toast.makeText(CreateRoom.this, "Error al verificar el nombre de la sala", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void createRoomDocument(String roomNameAux, String roomPasswordAux) {
        DocumentReference roomRef = db.collection("Rooms").document(roomNameAux);

        // Verificar la disponibilidad del nombre de la sala
        roomRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    // El nombre de la sala ya está en uso
                    Toast.makeText(CreateRoom.this, "Nombre de sala ya está en uso", Toast.LENGTH_SHORT).show();
                } else {
                    // El nombre de la sala está disponible, proceder con la creación
                    // Crear la sala y copiar la colección SpaceCards
                    roomRef.set(createRoomData(roomNameAux, roomPasswordAux))
                            .addOnSuccessListener(documentReference -> {
                                // Éxito al agregar la sala
                                addChatDocument(roomRef);
                                //copySpaceCardsCollectionToRoom(cardsTypeString,roomRef);
                                //createUserInfo(roomRef);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                // Error al agregar la sala
                                Toast.makeText(CreateRoom.this, "Error al crear la sala", Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                // Manejar errores en la consulta
                Toast.makeText(CreateRoom.this, "Error al verificar el nombre de la sala", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Map<String, Object> createRoomData(String roomNameAux, String roomPasswordAux) {
        // Crear los datos para el documento de la sala

        // Encriptamos la password de la room
        String encryptedPassword = PasswordHelper.encryptPassword(roomPasswordAux);

        Map<String, Object> roomData = new HashMap<>();
        ArrayList<String> players = new ArrayList<>();

        players.add(this.user.getUserEmail());
        roomData.put("players", players);
        roomData.put("roomCreator", user.getUserName());
        roomData.put("roomName", roomNameAux);
        roomData.put("roomPassword", encryptedPassword);
        roomData.put("roomState", roomPublicPrivate);
        roomData.put("roomCardType", cardsTypeString);
        roomData.put("playerQuantity", 1);
        //roomData.put("playersInfo", new ArrayList<>());

        return roomData;
    }

    private void copySpaceCardsCollectionToRoom(String cardsTypeString, DocumentReference roomRef) {
        // Obtener la referencia a la colección SpaceCards

        if (cardsTypeString.equals("Space")) {
            cardsTypeString = "SpaceCards";
        } else if (cardsTypeString.equals("Monke")) {
            cardsTypeString = "MonkeCards";
        }

        CollectionReference spaceCardsCollection = db.collection(cardsTypeString);

        // Obtener todos los documentos de la colección SpaceCards
        spaceCardsCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Iterar sobre los documentos de SpaceCards y copiarlos en la colección Cards del documento Room
                for (QueryDocumentSnapshot document : task.getResult()) {
                    roomRef.collection("Cards").document(document.getId()).set(document.getData());
                }
            } else {
                // Manejar errores al obtener los documentos de SpaceCards
            }
        });
    }


    private void addChatDocument(DocumentReference roomRef) {
        // Crear la colección "Chat" dentro del documento de la sala
        roomRef.collection("Chat").add(createChatDocumentData())
                .addOnSuccessListener(documentReference -> {
                    // Éxito al agregar el documento en la colección "Chat"
                    String documentId = documentReference.getId(); // Obtener el ID del documento creado
                    // Actualizar el documento con el ID
                    roomRef.collection("Chat").document(documentId).update("id", documentId)
                            .addOnSuccessListener(aVoid -> {
                                // Éxito al actualizar el ID del documento
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                // Error al actualizar el ID del documento
                            });
                })
                .addOnFailureListener(e -> {
                    // Error al agregar el documento en la colección "Chat"
                });
    }

    private Map<String, Object> createChatDocumentData() {
        // Crear los datos para el documento "Created" en la colección "Chat"
        Map<String, Object> chatData = new HashMap<>();
        chatData.put("text", "Bienvenido");
        chatData.put("user", user.getUserName());

        // Obtener la fecha actual formateada
        Date date = new Date();
        chatData.put("date", date);

        return chatData;
    }


    private void createUserInfo(DocumentReference roomRef) {

        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(roomRef);

            if (snapshot.exists()) {
                String playerEmail = user.getUserEmail(); // Obtener el nombre del usuario
                DocumentReference playerInfoRef = roomRef.collection("PlayersInfo").document(playerEmail); // Referencia al documento del jugador

                Map<String, Object> playerData = new HashMap<>();
                playerData.put("playerColdown", FieldValue.serverTimestamp());
                playerData.put("gachaQuantity", 5);

                transaction.set(playerInfoRef, playerData); // Guardar los datos del jugador en el documento de playersInfo

                return null;
            } else {
                throw new FirebaseFirestoreException("La sala no existe.", FirebaseFirestoreException.Code.NOT_FOUND);
            }
        }).addOnSuccessListener(aVoid -> {
            System.out.println("Se agregó la información del jugador correctamente.");
        }).addOnFailureListener(e -> {
            System.out.println("Error al agregar la información del jugador: " + e.getMessage());
        });
    }

}