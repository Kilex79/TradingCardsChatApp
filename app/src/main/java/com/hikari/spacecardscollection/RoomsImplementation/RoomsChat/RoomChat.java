package com.hikari.spacecardscollection.RoomsImplementation.RoomsChat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.hikari.spacecardscollection.Arapter.MyAdapterRoomChat2;
import com.hikari.spacecardscollection.R;
import com.hikari.spacecardscollection.RoomsImplementation.Room;
import com.hikari.spacecardscollection.User.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RoomChat extends AppCompatActivity {


    private User user;
    private Room room;
    private List<Message> roomMessages;
    private RecyclerView recyclerViewMessages;
    private MyAdapterRoomChat2 myAdapterRoomChat2;
    private ImageButton sendMessageBtn;
    private EditText messageText;
    private TextView gachaColdown;
    private boolean isCountdownFinished = false;

    private CollectionReference collectionReference;

    private TextView roomNameTitle;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_chat);

        roomMessages = new ArrayList<>();
        room = new Room();
        ids();


        Intent intent2 = getIntent();
        if (intent2.hasExtra("user")) {
            this.user = new User();
            this.user = (User) intent2.getSerializableExtra("user");
            this.room = (Room) intent2.getSerializableExtra("room");
        }

        db = FirebaseFirestore.getInstance();

        obtenerUserName(this.user.getUserEmail());
        roomNameTitle.setText(this.room.getRoomName());

        collectionReference = db.collection("Rooms").document(this.room.getRoomName()).collection("Chat");

        // Actualización del chat instantaneamente
        collectionReference.addSnapshotListener((value, error) -> {
            if (value != null) {
                for (DocumentChange dc : value.getDocumentChanges()) {
                    QueryDocumentSnapshot roomSnapshot = dc.getDocument();
                    Message room = roomSnapshot.toObject(Message.class);

                    switch (dc.getType()) {
                        case ADDED:
                            // Agregar el mensaje solo si no existe en la lista
                            if (!listContainsMessage(room)) {
                                addMessageToList(room);
                            }
                            break;
                        case MODIFIED:
                            // Actualizar el mensaje si ya existe en la lista
                            updateMessageInList(room);
                            break;
                        case REMOVED:
                            // Eliminar el mensaje de la lista
                            removeMessageFromList(room);
                            break;
                    }
                }
                myAdapterRoomChat2.notifyDataSetChanged();
            }
        });

        setUpChatRecyclerView();

        System.out.println("hola");
        // Send Message
        sendMessageBtn.setOnClickListener(v -> {
            String messageString = messageText.getText().toString().trim();
            if (!messageString.isEmpty()) {
                sendMessage(messageString);
                messageText.setText("");
            }
        });
        //


        messageText.setOnTouchListener((v, event) -> {
            myAdapterRoomChat2.hideOptionsMenu();
            return false;
        });

    }


    public void ids() {
        recyclerViewMessages = findViewById(R.id.message_chat_recyclerView);
        roomNameTitle = findViewById(R.id.room_name_textView);
        sendMessageBtn = findViewById(R.id.send_message_button);
        messageText = findViewById(R.id.message_editText);
        gachaColdown = findViewById(R.id.gacha_coldown_textView);
    }



    private void obtenerUserName(String email) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Users")
                .document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        this.user = documentSnapshot.toObject(User.class);
                    } else {
                        // Manejar el caso en que el documento no existe
                    }
                })
                .addOnFailureListener(e -> {
                    // Manejar cualquier error de consulta
                });
    }


    // ========< AUTOMATICAMENTE ACTUALIZA LAS SALAS >========


    // Métodos auxiliares para manejar la lista de mensajes
    private boolean listContainsMessage(Message message) {
        if (message != null && message.getId() != null) {
            for (Message item : roomMessages) {
                if (item.getId() != null && item.getId().equals(message.getId())) {
                    return true;
                }
            }
        }
        return false;
    }


    private void addMessageToList(Message message) {
        roomMessages.add(message);
    }

    private void updateMessageInList(Message message) {
        if (message != null && message.getId() != null) {
            for (int i = 0; i < roomMessages.size(); i++) {
                if (roomMessages.get(i).getId() != null && roomMessages.get(i).getId().equals(message.getId())) {
                    roomMessages.set(i, message);
                    return;
                }
            }
        }
    }


    private void removeMessageFromList(Message message) {
        if (message != null && message.getId() != null) {
            roomMessages.removeIf(item -> item.getId().equals(message.getId()));
        }
    }

    // ======================================================

    public void sendMessage(String text) {
        // Obtener la fecha y hora actual
        /*Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);*/

        // Crear un nuevo objeto para el mensaje
        Message message = new Message();
        message.setText(text);
        message.setUser(user.getUserName()); // Suponiendo que el nombre del usuario está disponible en el objeto User
        //Date currentDate = new Date(); // Marca de tiempo actual
        message.setDate(Timestamp.now());

        // Guardar el nuevo mensaje en la subcolección "Chat"
        db.collection("Rooms").document(this.room.getRoomName()).collection("Chat")
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    String messageId = documentReference.getId();

                    // Actualizar el mensaje con el ID del documento
                    message.setId(messageId);
                    documentReference.set(message);
                })
                .addOnFailureListener(e -> {
                    // Manejar cualquier error al guardar el mensaje
                });
    }


    void setUpChatRecyclerView() {

        Query query = db.collection("Rooms").document(this.room.getRoomName()).collection("Chat")
                .orderBy("date", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class).build();

        myAdapterRoomChat2 = new MyAdapterRoomChat2(options, getApplicationContext(), this.user, this.user.getUserName(), this.room.getRoomName());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(myAdapterRoomChat2);

        myAdapterRoomChat2.startListening();

        smoothScroll();



    }


    void smoothScroll() {
        myAdapterRoomChat2.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerViewMessages.smoothScrollToPosition(positionStart);
            }
        });
    }

    private void startCountdown(SerializableTimestamp timestamp) {
        long timestampMillis = timestamp.getSeconds() * 1000;
        long currentMillis = System.currentTimeMillis();
        long differenceMillis = (timestampMillis - currentMillis) * -1;

        long aux = 3600000 - differenceMillis;

        // Verifica si el timestamp del usuario está en el futuro
        if (aux > 0) {
            new CountDownTimer(aux, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    // Convierte los milisegundos restantes en horas, minutos y segundos
                    long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60;
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;

                    // Actualiza la interfaz de usuario con el contador regresivo
                    String countdown = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
                    gachaColdown.setText(countdown);
                }

                @Override
                public void onFinish() {
                    // Cuando el contador llega a 00:00:00, establece la variable global en true
                    isCountdownFinished = true;
                    gachaColdown.setText("00:00:00");
                }
            }.start();
        } else {
            // El timestamp está en el pasado, ya ha expirado
            isCountdownFinished = true;
            gachaColdown.setText("00:00:00");
        }
    }


    @Override
    public void onBackPressed() {
        finish(); // Finish the current activity without calling the parent class's onBackPressed
    }
}