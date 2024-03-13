package com.hikari.spacecardscollection.User.LoadingScreen;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hikari.spacecardscollection.Firebase.FirestoreDatabase;
import com.hikari.spacecardscollection.MainActivity;
import com.hikari.spacecardscollection.R;
import com.hikari.spacecardscollection.RoomsImplementation.InterfaceListener.RoomFetchListener;
import com.hikari.spacecardscollection.RoomsImplementation.Room;
import com.hikari.spacecardscollection.User.InterfaceListeners.UserInfoListener;
import com.hikari.spacecardscollection.User.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LoadingScreen extends AppCompatActivity implements RoomFetchListener {

    // ======< APP VERSION >=====
    private final String appVersion = "0.0.1 Alpha";
    private TextView appVersionTextView;
    // ==========================

    private ProgressBar progressBar;
    private TextView progressTextView;
    private String userEmail;
    private List<Room> publicRooms;
    private List<Room> yourRooms;
    private FirestoreDatabase firestoreDatabase;
    private FirebaseFirestore db;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        progressBar = findViewById(R.id.progress_bar_loading);
        progressTextView = findViewById(R.id.progress_TextView);
        appVersionTextView = findViewById(R.id.app_version_textView);

        appVersionTextView.setText(appVersion);

        db = FirebaseFirestore.getInstance();
        firestoreDatabase = new FirestoreDatabase(db);
        publicRooms = new ArrayList<>();
        yourRooms = new ArrayList<>();

        progressBar.setMax(100);
        progressBar.setScaleY(3f);

        Intent intent2 = getIntent();
        if (intent2.hasExtra("email")) {
            this.userEmail = ((String) intent2.getSerializableExtra("email"));
            this.user = ((User) intent2.getSerializableExtra("user"));
        }

        publicRoomsSynchronously();
        yourRoomsSynchronously();
        getUserInfoSynchronously(userEmail);

        getUserData(userEmail);
        //testUser(userEmail);

        // Crear un intento para MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("email", this.userEmail);
        intent.putExtra("yourRooms", (Serializable) this.yourRooms);
        intent.putExtra("publicRooms", (Serializable) this.publicRooms);
        intent.putExtra("user", this.user);



        animation();
    }


    public void animation() {

        getUserData(userEmail);
        ProgressBarAnimation anim = new ProgressBarAnimation(this, progressBar, progressTextView, 0f, 100f, user, publicRooms, yourRooms, userEmail);
        anim.setDuration(2000);
        progressBar.setAnimation(anim);
    }

    private void yourRoomsSynchronously() {
        firestoreDatabase.showYourRooms(userEmail, new RoomFetchListener() {
            @Override
            public void onYourRoomsFetched(List<Room> rooms) {
                yourRooms = rooms;

                getUserData(userEmail);
                // Llamar al método para continuar con la animación o cualquier otra operación necesaria
                //continueWithAnimation();
            }

            @Override
            public void onPublicRoomsFetched(List<Room> rooms) {
                // No necesitamos implementar este método aquí
            }

            @Override
            public void onError(String errorMessage) {
                // Manejar el error, si es necesario
            }
        });
    }

    private void publicRoomsSynchronously() {
        firestoreDatabase.showPublicRooms(userEmail, new RoomFetchListener() {
            @Override
            public void onPublicRoomsFetched(List<Room> rooms) {
                publicRooms = rooms;

                getUserData(userEmail);
                // Llamar al método para continuar con la animación o cualquier otra operación necesaria
                continueWithAnimation();
            }

            @Override
            public void onYourRoomsFetched(List<Room> rooms) {
                // No necesitamos implementar este método aquí
            }

            @Override
            public void onError(String errorMessage) {
                // Manejar el error, si es necesario
            }
        });
    }


    @Override
    public void onPublicRoomsFetched(List<Room> rooms) {
        this.publicRooms = rooms;
    }


    @Override
    public void onYourRoomsFetched(List<Room> rooms) {
        this.yourRooms = rooms;
    }

    @Override
    public void onError(String errorMessage) {

    }

    public List<Room> getPublicRooms() {
        return publicRooms;
    }

    public List<Room> getYourRooms() {
        return yourRooms;
    }

    private void continueWithAnimation() {
        // Verificar si se han cargado ambos conjuntos de datos antes de continuar con la animación
        if (publicRooms != null && yourRooms != null) {
            // Crear un nuevo Handler
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Crear un intento para MainActivity
                    Intent intent = new Intent(LoadingScreen.this, MainActivity.class);
                    intent.putExtra("email", userEmail);
                    intent.putExtra("yourRooms", (Serializable) yourRooms);
                    intent.putExtra("publicRooms", (Serializable) publicRooms);
                    getUserData(userEmail);
                    intent.putExtra("user", user);
                    // Agregar la bandera FLAG_ACTIVITY_CLEAR_TOP para limpiar todas las instancias anteriores de MainActivity
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    // Agregar la bandera FLAG_ACTIVITY_CLEAR_TASK para limpiar todas las actividades y crear una nueva tarea de actividad
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    // Iniciar el MainActivity
                    startActivity(intent);
                }
            }, 2000); // 2000 milisegundos de retraso, ajusta este valor según sea necesario
        }
    }



    private void getUserInfoSynchronously(String email) {
        firestoreDatabase.getInfoUserSynchronously(email, new UserInfoListener() {
            @Override
            public void onUserInfoLoaded(User user) {
                LoadingScreen.this.user = user;
            }

            @Override
            public void onUserInfoError(String errorMessage) {
                // Manejar el error si ocurre al obtener la información del usuario
            }
        });
    }

    void getUserData(String userEmail) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users")
                .document(userEmail)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // El documento existe, puedes obtener los datos del usuario
                            User user = documentSnapshot.toObject(User.class);
                            // Aquí puedes hacer lo que necesites con los datos del usuario
                            LoadingScreen.this.user = user;
                            // Por ejemplo, imprimir los datos en la consola
                            Log.d(TAG, "Datos del usuario: " + user.getUserName() + ", " + user.getUserEmail());
                        } else {
                            // El documento no existe
                            Log.d(TAG, "No se encontró el usuario con el email especificado.");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al obtener el usuario: ", e);
                    }
                });
    }
}
