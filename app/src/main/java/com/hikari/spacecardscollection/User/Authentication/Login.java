package com.hikari.spacecardscollection.User.Authentication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hikari.spacecardscollection.Firebase.FirestoreDatabase;
import com.hikari.spacecardscollection.Firebase.LoginRegisterResult;
import com.hikari.spacecardscollection.MainActivity;
import com.hikari.spacecardscollection.R;
import com.hikari.spacecardscollection.User.LoadingScreen.LoadingScreen;
import com.hikari.spacecardscollection.User.User;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {

    private EditText email_editText, password_editText;
    private Button login_btn;
    private ProgressBar progressBar;
    private CheckBox checkBoxRemember;
    private static final String PREFS_NAME = "SaveUser";
    private User user;
    private LinearLayout goTo_Register;
    private FirestoreDatabase firestoreClassLogin;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private LoginRegisterResult loginRegisterResult;
    private LinearLayout linearLayoutLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // IDs
        ids();
        //

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        loginRegisterResult = new LoginRegisterResult();
        firestoreClassLogin = new FirestoreDatabase(mAuth, db, this);
        //

        // Al registrarse se pasara el email al editText
        Intent intent2 = getIntent();
        if (intent2.hasExtra("email")) {
            this.email_editText.setText((String) intent2.getSerializableExtra("email"));
        }
        //

        // CheckBox for remember user
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean rememberUser = prefs.getBoolean("rememberUser", false);
        checkBoxRemember.setChecked(rememberUser);

        if (rememberUser) {
            linearLayoutLogin.setVisibility(View.GONE);
            goTo_Register.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            String savedEmail = prefs.getString("savedEmail", "");
            String savedPassword = prefs.getString("savedPassword", "");

            if (!TextUtils.isEmpty(savedEmail) && !TextUtils.isEmpty(savedPassword)) {
                // Autocompletar campos de correo electrónico y contraseña
                email_editText.setText(savedEmail);
                password_editText.setText(savedPassword);
                login(savedEmail, savedPassword);
            }
        }
        //

        login_btn.setOnClickListener(v -> {
            //login_btn.setVisibility(View.GONE);
            linearLayoutLogin.setVisibility(View.GONE);
            goTo_Register.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            String email = this.email_editText.getText().toString().trim();
            String password = this.password_editText.getText().toString();

            getUserData(email);

            if (datosNotNull(email, password)) {
                if (checkBoxRemember.isChecked()) {
                    // Si el checkbox está marcado, guarda la información del usuario
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("savedEmail", email);
                    editor.putString("savedPassword", password);
                    editor.apply();
                } else {
                    // Si el checkbox no está marcado, elimina la información guardada
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.remove("savedEmail");
                    editor.remove("savedPassword");
                    editor.apply();
                }

                /*if (firestoreClassLogin.login(email, password)) {
                    *//*Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();*//*
                } else {
                    login_btn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }*/
                login(email, password);
            }
        });

        // Configurar el Listener del CheckBox
        checkBoxRemember.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
            editor.putBoolean("rememberUser", isChecked);

            if (!isChecked) {
                // Si el checkbox no está marcado, elimina la información guardada
                editor.remove("savedEmail");
                editor.remove("savedPassword");
            } else {
                // Si el checkbox está marcado, guarda la información del usuario
                editor.putString("savedEmail", email_editText.getText().toString());
                editor.putString("savedPassword", password_editText.getText().toString());
            }

            editor.apply();
        });


        // Register
        goTo_Register.setOnClickListener(v -> {
            Intent intent = new Intent(this, Register.class);
            startActivity(intent);
            finish();
        });
        //

    }

    private void ids() {
        email_editText = findViewById(R.id.user_email_login);
        password_editText = findViewById(R.id.user_password_login);
        login_btn = findViewById(R.id.login_btn);
        goTo_Register = findViewById(R.id.goto_register_linearLayout);
        progressBar = findViewById(R.id.progressBarLogin);
        checkBoxRemember = findViewById(R.id.checkBoxRememberUser);
        linearLayoutLogin = findViewById(R.id.linearLayoutLogin);
    }

    public boolean datosNotNull(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
            //login_btn.setVisibility(View.VISIBLE);
            linearLayoutLogin.setVisibility(View.VISIBLE);
            goTo_Register.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
            //login_btn.setVisibility(View.VISIBLE);
            linearLayoutLogin.setVisibility(View.VISIBLE);
            goTo_Register.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            return false;
        }

        return true;
    }


    public void login(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {


                    if (task.isSuccessful()) {

                        //Toast.makeText(this, "Login Succesful", Toast.LENGTH_SHORT).show();
                        CollectionReference usersRef = db.collection("Users");  // Nombre de la colección
                        DocumentReference userDocRef = usersRef.document(email);  // Documento con el email del usuario

                        userDocRef.get().addOnCompleteListener(userTask -> {
                            if (userTask.isSuccessful()) {
                                DocumentSnapshot document = userTask.getResult();
                                if (document.exists()) {
                                    // Ponemos loginResult en true para así devolver que se ha podido iniciar sesión correctamente
                                    Intent intent = new Intent(this, LoadingScreen.class);
                                    intent.putExtra("email", email);
                                    intent.putExtra("user", user);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // Manejo de error
                        Toast.makeText(this, "User doesen't exist", Toast.LENGTH_SHORT).show();
                        linearLayoutLogin.setVisibility(View.VISIBLE);
                        goTo_Register.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
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
