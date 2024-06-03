package com.hikari.spacecardscollection.NavigationMenu;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hikari.spacecardscollection.Firebase.FirestoreDatabase;
import com.hikari.spacecardscollection.Objects.Card;
import com.hikari.spacecardscollection.R;
import com.hikari.spacecardscollection.RoomsImplementation.JoinRoom.DialogJoinRoom;
import com.hikari.spacecardscollection.RoomsImplementation.Room;
import com.hikari.spacecardscollection.RoomsImplementation.RoomsChat.SerializableTimestamp;
import com.hikari.spacecardscollection.ShopImplementation.DialogShop;
import com.hikari.spacecardscollection.User.User;
import com.hikari.spacecardscollection.databinding.FragmentRoomsBinding;
import com.hikari.spacecardscollection.databinding.FragmentShopBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class ShopFragment extends Fragment {

    private User user;
    private FragmentShopBinding binding;
    private MutableLiveData<String> playerColdownLiveData = new MutableLiveData<>();
    private MutableLiveData<Long> gachaQuantityLiveData = new MutableLiveData<>();

    private TextView playerColdownTextView;
    private TextView gachaQuatityTextView;
    private boolean isCountdownFinished = false;

    private Button monkeButton;
    private Button spaceButton;

    private FirestoreDatabase firestoreDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }
        binding = FragmentShopBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        getPlayerColdown();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        firestoreDatabase = new FirestoreDatabase(db);

        // Obtenemos el coldown y la cantidad de gachas
        playerColdownLiveData.observe(getViewLifecycleOwner(), playerColdown -> {
            // Actualizar la interfaz de usuario con playerColdown
            // playerColdown contiene el valor actualizado
            this.user.setPlayerColdown(playerColdown);
        });

        gachaQuantityLiveData.observe(getViewLifecycleOwner(), gachaQuantity -> {
            // Actualizar la interfaz de usuario con gachaQuantity
            // gachaQuantity contiene el valor actualizado
            this.user.setGachaQuantity(gachaQuantity);
        });
        //System.out.printf("hola");

        // ID's
        ids(root);
        //

        // Actualización de los TextView
        SerializableTimestamp timestamp = convertToSerializableTimestamp(user.getPlayerColdown());
        if (timestamp != null) {
            startCountdown(timestamp);
        } else {
            // Manejar el caso en que la conversión falle
            // Por ejemplo, puedes establecer un valor predeterminado o mostrar un mensaje de error
            playerColdownTextView.setText("Invalid timestamp");
        }

        startCountdown(timestamp);
        if (this.user.getGachaQuantity() <= 0) {
            gachaQuatityTextView.setText("Throws " + 0);
        } else if (this.user.getGachaQuantity() > 0) {
            gachaQuatityTextView.setText("Throws " + this.user.getGachaQuantity().toString());
        }
        //

        // Actions
        buttonActions();
        //

        return root;
    }

    private void ids(View root) {
        playerColdownTextView = root.findViewById(R.id.playerColdown_textView);
        gachaQuatityTextView = root.findViewById(R.id.gachaQuantity_textView);
        monkeButton = root.findViewById(R.id.monkeCards_button);
        spaceButton = root.findViewById(R.id.spaceCards_button);
    }

    private void buttonActions() {
        monkeButton.setOnClickListener(v -> {
            Long gachaAux = this.user.getGachaQuantity();
            if (gachaAux > 0) {
                this.user.setGachaQuantity(gachaAux - 1);
                gachaQuatityTextView.setText("Throws " + this.user.getGachaQuantity().toString());
                updateGachaQuantityInFirestore(this.user.getUserEmail(), this.user.getGachaQuantity());
                openDialog("MonkeCards", this.user);
            } else if (gachaAux == 0) {
                if (isCountdownFinished) {
                    this.user.setGachaQuantity(-1L);
                    resetColdown();
                }
            }
        });

        spaceButton.setOnClickListener(v -> {
            Long gachaAux = this.user.getGachaQuantity();
            if (gachaAux > 0) {
                this.user.setGachaQuantity(gachaAux - 1);
                gachaQuatityTextView.setText("Throws " + this.user.getGachaQuantity().toString());
                updateGachaQuantityInFirestore(this.user.getUserEmail(), this.user.getGachaQuantity());
                openDialog("SpaceCards", this.user);
            } else if (gachaAux == 0) {
                if (isCountdownFinished) {
                    this.user.setGachaQuantity(-1L);
                    resetColdown();
                }
            }
        });

    }

    private void resetColdown() {
        SimpleDateFormat playerColdownFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        String playerColdownStart = playerColdownFormat.format(currentDate);
        this.user.setPlayerColdown(playerColdownStart);

        SerializableTimestamp timestamp = convertToSerializableTimestamp(this.user.getPlayerColdown());
        if (timestamp != null) {
            startCountdown(timestamp);
        } else {
            // Manejar el caso en que la conversión falle
            playerColdownTextView.setText("Invalid timestamp");
        }
        updatePlayerColdownInFirestore(this.user.getUserEmail(), playerColdownStart);
    }


    private void getPlayerColdown() {
        new Thread(() -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("Users").document(user.getUserEmail());

            try {
                DocumentSnapshot documentSnapshot = Tasks.await(docRef.get());
                if (documentSnapshot.exists()) {
                    String playerColdwnFirebase = documentSnapshot.getString("playerColdown");
                    Long playerGachaQuantity = documentSnapshot.getLong("gachaQuantity");
                    if (playerColdwnFirebase != null) {
                        playerColdownLiveData.postValue(playerColdwnFirebase);
                    }
                    if (playerGachaQuantity != null) {
                        gachaQuantityLiveData.postValue(playerGachaQuantity);
                    }
                }
            } catch (ExecutionException | InterruptedException e) {
                // Handle any errors
            }
        }).start();
    }

    private void startCountdown(SerializableTimestamp timestamp) {
        long timestampMillis = timestamp.getSeconds() * 1000;
        long currentMillis = System.currentTimeMillis();
        long differenceMillis = (timestampMillis - currentMillis) * -1;

        long aux = 3600000 - differenceMillis;

        // Verifica si el timestamp del usuario está en el futuro
        if (aux > 0) {
            isCountdownFinished = false;
            new CountDownTimer(aux, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    // Convierte los milisegundos restantes en horas, minutos y segundos
                    long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60;
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;

                    // Actualiza la interfaz de usuario con el contador regresivo
                    String countdown = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
                    playerColdownTextView.setText(countdown);
                }

                @Override
                public void onFinish() {
                    // Cuando el contador llega a 00:00:00, establece la variable global en true
                    isCountdownFinished = true;
                    playerColdownTextView.setText("00:00:00");
                    user.setGachaQuantity(5L);
                    gachaQuatityTextView.setText("Throws " + user.getGachaQuantity().toString());
                    updateGachaQuantityInFirestore(user.getUserEmail(), 5L);
                }
            }.start();
        } else {
            // El timestamp está en el pasado, ya ha expirado
            isCountdownFinished = true;
            playerColdownTextView.setText("00:00:00");
            this.user.setGachaQuantity(5L);
            gachaQuatityTextView.setText("Throws " + this.user.getGachaQuantity().toString());
            updateGachaQuantityInFirestore(user.getUserEmail(), 5L);
        }
    }

    private SerializableTimestamp convertToSerializableTimestamp(String timestampString) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            Date date = format.parse(timestampString);
            long seconds = date.getTime() / 1000; // Convertir a segundos
            int nanoseconds = (int) ((date.getTime() % 1000) * 1000000); // Convertir los milisegundos restantes a nanosegundos
            return new SerializableTimestamp(seconds, nanoseconds);
        } catch (ParseException e) {
            e.printStackTrace();
            // Manejar la excepción según sea necesario
        }
        return null;
    }

    private void updateGachaQuantityInFirestore(String userEmail, Long gachaQuantity) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(userEmail);

        docRef
                .update("gachaQuantity", gachaQuantity)
                .addOnSuccessListener(aVoid -> {
                    // La actualización fue exitosa
                })
                .addOnFailureListener(e -> {
                    // La actualización falló
                    // Maneja el error según sea necesario
                });
    }


    private void updatePlayerColdownInFirestore(String userEmail, String playerColdown) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(userEmail);

        docRef
                .update("playerColdown", playerColdown)
                .addOnSuccessListener(aVoid -> {
                    // La actualización fue exitosa
                    isCountdownFinished = false;
                })
                .addOnFailureListener(e -> {
                    // La actualización falló
                    // Maneja el error según sea necesario
                });
    }

    public void openDialog(String cardType, User user) {
        getRandomCard(cardType, user);
    }

    public void getRandomCard(String cardType, User user) {
        firestoreDatabase.getOneCard(cardType, user.getUserEmail(), card -> {
            if (card != null) {
                DialogShop dialogShop = new DialogShop(this, card);
                dialogShop.show(requireActivity().getSupportFragmentManager(), "Show Card");
            } else {
                // Handle the case when no card is obtained
                System.out.println("No se pudo obtener una carta aleatoria.");
            }
        });
    }


}
