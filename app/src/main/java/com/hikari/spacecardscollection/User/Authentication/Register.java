package com.hikari.spacecardscollection.User.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hikari.spacecardscollection.Firebase.FirestoreDatabase;
import com.hikari.spacecardscollection.Firebase.LoginRegisterResult;
import com.hikari.spacecardscollection.R;
import com.hikari.spacecardscollection.User.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Register extends AppCompatActivity {

    private EditText name_editText, email_editText, password_editText;
    private Button register_btn;
    private LinearLayout goTo_Login;
    private User user;
    private ProgressBar progressBarRegister;
    private FirestoreDatabase firestoreClassRegister;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private LinearLayout linearLayoutRegister;

    private boolean nameExists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // IDs
        ids();
        //

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        LoginRegisterResult loginRegisterResult = new LoginRegisterResult();
        firestoreClassRegister = new FirestoreDatabase(mAuth, db, this);
        //


        // Registrarse
        register_btn.setOnClickListener(v -> {
            String name = name_editText.getText().toString().trim();
            String email = email_editText.getText().toString().trim();
            String password = password_editText.getText().toString();

            linearLayoutRegister.setVisibility(View.GONE);
            progressBarRegister.setVisibility(View.VISIBLE);
            goTo_Login.setVisibility(View.GONE);

            if (datosNotNull(name, email, password)) {
                checkNameAvailabilityAndRegister(name, email, password);
            }

        });
        //

        // LogIn
        goTo_Login.setOnClickListener(v -> {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
        });
        //


    }

    private void ids() {
        name_editText = findViewById(R.id.user_name_register);
        email_editText = findViewById(R.id.user_email_register);
        password_editText = findViewById(R.id.user_password_register);
        register_btn = findViewById(R.id.register_btn);
        goTo_Login = findViewById(R.id.goto_login_linearLayout);
        progressBarRegister = findViewById(R.id.progressBarRegister);
        linearLayoutRegister = findViewById(R.id.linearLayoutRegister);
    }

    public boolean datosNotNull(String name, String email, String password) {

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show();
            linearLayoutRegister.setVisibility(View.VISIBLE);
            goTo_Login.setVisibility(View.VISIBLE);
            progressBarRegister.setVisibility(View.GONE);
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
            linearLayoutRegister.setVisibility(View.VISIBLE);
            goTo_Login.setVisibility(View.VISIBLE);
            progressBarRegister.setVisibility(View.GONE);
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
            linearLayoutRegister.setVisibility(View.VISIBLE);
            goTo_Login.setVisibility(View.VISIBLE);
            progressBarRegister.setVisibility(View.GONE);
            return false;
        }

        return true;
    }

    public void register(String name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "User Created Successfully", Toast.LENGTH_SHORT).show();

                        // Obtén el UID del usuario actual
                        String userId = mAuth.getCurrentUser().getUid();

                        String documentName = email;

                        Map<String, Object> userData = new HashMap<>();
                        userData.put("userName", name);
                        userData.put("userEmail", email);
                        userData.put("userId", userId);
                        userData.put("level", "1");
                        userData.put("virtualMoney", "0");
                        userData.put("virtualPremiumMoney", "0");
                        userData.put("gachaQuantity", 5);
                        //userData.put("playerColdown", Timestamp.now());
                        SimpleDateFormat playerColdownFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.HOUR_OF_DAY, -1); // Restar una hora
                        Date currentDate = calendar.getTime();
                        String playerColdownStart = playerColdownFormat.format(currentDate);
                        userData.put("playerColdown", playerColdownStart);


                        // Obtén la fecha actual
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        Date currentDateAux = new Date();
                        String registerDate = sdf.format(currentDateAux);
                        userData.put("registerDate", registerDate);

                        List<String> stringArray = new ArrayList<>();
                        userData.put("friends", stringArray);
                        userData.put("friendsRequest", stringArray);

                        // Código para asignar imagen predeterminada
                        Uri predefImageUri = Uri.parse("android.resource://com.hikari.spacecardscollection/drawable/default_user_icon.png");
                        userData.put("userIcon", predefImageUri.toString());

                        FirebaseFirestore.getInstance().collection("Users")
                                .document(documentName)
                                .set(userData)
                                .addOnSuccessListener(aVoid -> {

                                    uploadDefaultImageToStorage(userId, email, name);
                                    Intent intent = new Intent(this, Login.class);
                                    intent.putExtra("userEmail", email);
                                    startActivity(intent);
                                    finish();
                                });

                    } else {
                        Toast.makeText(this, "User name already exists.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Método para subir la imagen predeterminada al Storage
    private void uploadDefaultImageToStorage(String userId, String email, String name) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("Users/" + name + "/" + userId + "Icon.jpg");
        int defaultImageResourceId = R.drawable.default_user_icon;

        // Crea un Uri desde el recurso drawable
        Uri predefImageUri = Uri.parse("android.resource://com.hikari.spacecardscollection/" + defaultImageResourceId);


        storageRef.putFile(predefImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // La imagen se ha subido exitosamente al Storage


                    // Obtén la URL de descarga de la imagen
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();

                        // Actualiza el campo userIcon en Firestore
                        FirebaseFirestore.getInstance().collection("Users")
                                .document(email)
                                .update("userIcon", imageUrl)
                                .addOnSuccessListener(aVoid -> {
                                    // Éxito al actualizar el campo en Firestore
                                })
                                .addOnFailureListener(e -> {
                                    // Manejar errores en caso de fallo al actualizar en Firestore
                                    Toast.makeText(this, "Error updating userIcon: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    });

                })
                .addOnFailureListener(e -> {
                    // Manejar errores en caso de fallo al subir la imagen al Storage
                    Toast.makeText(this, "Error uploading image to storage: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void checkNameAvailabilityAndRegister(String name, String email, String password) {
        // Verificar si el nombre contiene caracteres no permitidos
        if (!name.matches("[a-zA-Z0-9]+")) {
            // Mostrar alerta si el nombre contiene caracteres no permitidos
            Toast.makeText(Register.this, "El nombre de usuario solo puede contener letras y números", Toast.LENGTH_SHORT).show();
            return;
        }

        // Continuar con la verificación y registro si el nombre es válido
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("Users");

        usersRef.whereEqualTo("userName", name).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Verificar si el nombre está en otro documento
                            if (!document.getId().equals(email)) {
                                // El nombre ya está en uso
                                Toast.makeText(Register.this, "Nombre de usuario ya está en uso", Toast.LENGTH_SHORT).show();
                                linearLayoutRegister.setVisibility(View.VISIBLE);
                                goTo_Login.setVisibility(View.VISIBLE);
                                progressBarRegister.setVisibility(View.GONE);
                                return;
                            }
                        }

                        // El nombre está disponible, proceder con el registro
                        register(name, email, password);
                    } else {
                        // Manejar errores en la consulta
                        Toast.makeText(Register.this, "Error al verificar el nombre de usuario", Toast.LENGTH_SHORT).show();
                        linearLayoutRegister.setVisibility(View.VISIBLE);
                        goTo_Login.setVisibility(View.VISIBLE);
                        progressBarRegister.setVisibility(View.GONE);
                    }
                });
    }



}