package com.hikari.spacecardscollection;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hikari.spacecardscollection.Firebase.FirestoreDatabase;
import com.hikari.spacecardscollection.NavigationMenu.CardsFragment;
import com.hikari.spacecardscollection.NavigationMenu.RoomsFragment;
import com.hikari.spacecardscollection.NavigationMenu.ShopFragment;
import com.hikari.spacecardscollection.RoomsImplementation.InterfaceListener.RoomFetchListener;
import com.hikari.spacecardscollection.RoomsImplementation.Room;
import com.hikari.spacecardscollection.RoomsImplementation.RoomsChat.RoomChat;
import com.hikari.spacecardscollection.User.LoadingScreen.LoadingScreen;
import com.hikari.spacecardscollection.User.User;
import com.hikari.spacecardscollection.User.InterfaceListeners.UserInfoListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements UserInfoListener {

    private String userEmail;
    private FirestoreDatabase firestoreDatabase;
    private User user;
    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    //

    // navigation
    private ImageButton shopFragmentBtn;
    private ImageButton roomsFragmentBtn;
    private ImageButton cardsFragmentBtn;
    private CardView shopCardView;
    private CardView roomsCardView;
    private CardView cardsCardView;
    //

    // listas
    private List<Room> publicRooms;
    private List<Room> yourRooms;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // ========< Recuperamos el email del usuario >========
        Intent intent2 = getIntent();
        if (intent2.hasExtra("email")) {
            this.userEmail = ((String) intent2.getSerializableExtra("email"));
            this.publicRooms = (List<Room>) intent2.getSerializableExtra("publicRooms");
            this.yourRooms = (List<Room>) intent2.getSerializableExtra("yourRooms");
            this.user = ((User) intent2.getSerializableExtra("user"));
        }
        // ==============================
        getUserData(userEmail);

        RoomsFragment roomsFragment = new RoomsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        roomsFragment.setArguments(bundle);

        replaceFragment(roomsFragment);

        // ========< IDs >========
        ids();
        // ==============================

        // ========< Firebase >========
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firestoreDatabase = new FirestoreDatabase(mAuth, db, this);
        // ==============================


        // ========< Accedemos a los datos del usuario mediante la interfaz >========
        firestoreDatabase.getInfoUserSynchronously(userEmail, this);
        infoUserSynchronously(userEmail);
        // ==============================

        // ========< NAVIGATION >========
        roomsFragmentBtn.setBackgroundColor(getResources().getColor(R.color.primary_color));
        cardsFragmentBtn.setBackgroundColor(getResources().getColor(R.color.background));
        shopFragmentBtn.setBackgroundColor(getResources().getColor(R.color.background));
        navigation();
        // ==============================

        firestoreDatabase.getInfoUserSynchronously(userEmail, this);

        getFCMToken();
    }

    public void ids() {
        roomsFragmentBtn = findViewById(R.id.rooms_img_btn);
        shopFragmentBtn = findViewById(R.id.shop_img_btn);
        cardsFragmentBtn = findViewById(R.id.cards_img_btn);
    }


    // Get user info
    private void infoUserSynchronously(String email) {
        UserInfoListener listener = this;
        firestoreDatabase.getInfoUserSynchronously(email, listener);
    }

    @Override
    public void onUserInfoLoaded(User user) {
        // Maneja la información del usuario cargada de forma síncrona
        this.user = user;

    }

    @Override
    public void onUserInfoError(String errorMessage) {
        // Maneja el error en la obtención de información del usuario
        Toast.makeText(this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_layout, fragment);
        fragmentTransaction.addToBackStack(null); // Agregar al back stack
        fragmentTransaction.commit();
    }


    public void navigation() {

        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);

        cardsFragmentBtn.setOnClickListener(v -> {
            CardsFragment cardsFragment = new CardsFragment();
            cardsFragment.setArguments(bundle);
            replaceFragment(cardsFragment);
            cardsFragmentBtn.setBackgroundColor(getResources().getColor(R.color.primary_color));
            shopFragmentBtn.setBackgroundColor(getResources().getColor(R.color.background));
            roomsFragmentBtn.setBackgroundColor(getResources().getColor(R.color.background));
        });

        shopFragmentBtn.setOnClickListener(v -> {
            ShopFragment shopFragment = new ShopFragment();
            shopFragment.setArguments(bundle);
            replaceFragment(shopFragment);
            cardsFragmentBtn.setBackgroundColor(getResources().getColor(R.color.background));
            shopFragmentBtn.setBackgroundColor(getResources().getColor(R.color.primary_color));
            roomsFragmentBtn.setBackgroundColor(getResources().getColor(R.color.background));
        });

        roomsFragmentBtn.setOnClickListener(v -> {
            RoomsFragment roomsFragment = new RoomsFragment();
            roomsFragment.setArguments(bundle);
            replaceFragment(roomsFragment);
            cardsFragmentBtn.setBackgroundColor(getResources().getColor(R.color.background));
            shopFragmentBtn.setBackgroundColor(getResources().getColor(R.color.background));
            roomsFragmentBtn.setBackgroundColor(getResources().getColor(R.color.primary_color));
        });

    }



    public User getUser() {
        return user;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserName() {
        return user.getUserName();
    }



    public List<Room> getPublicRooms() {
        return publicRooms;
    }

    public List<Room> getYourRooms() {
        return yourRooms;
    }



    @Override
    public void onBackPressed() {
        // Construir el diálogo de confirmación
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¿Salir de la aplicación?");
        builder.setMessage("¿Estás seguro de que quieres salir de la aplicación?");
        builder.setPositiveButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Cerrar la aplicación
                finish();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Cerrar el diálogo y permanecer en la aplicación
                dialog.dismiss();
            }
        });

        // Mostrar el diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
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


    void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                Log.i("My Token ", token);
            }
        });
    }
}