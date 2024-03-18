package com.hikari.spacecardscollection.NavigationMenu;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hikari.spacecardscollection.R;
import com.hikari.spacecardscollection.User.User;
import com.hikari.spacecardscollection.databinding.FragmentRoomsBinding;
import com.hikari.spacecardscollection.databinding.FragmentShopBinding;

import java.util.concurrent.ExecutionException;


public class ShopFragment extends Fragment {

    private User user;
    private FragmentShopBinding binding;
    private String time;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }
        binding = FragmentShopBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        getPlayerColdown();

        System.out.printf("hola");

        return root;
    }


    private void getPlayerColdown() {
        new Thread(() -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("Users").document(user.getUserEmail());

            try {
                DocumentSnapshot documentSnapshot = Tasks.await(docRef.get());
                if (documentSnapshot.exists()) {
                    String playerColdwn = documentSnapshot.getString("playerColdown");
                    if (playerColdwn != null) {
                        // Actualizar la interfaz de usuario con los datos obtenidos
                        requireActivity().runOnUiThread(() -> {
                            //user.setPlayerColdown(playerColdwn);
                            time = playerColdwn;
                            // Actualizar la interfaz de usuario aquí según sea necesario
                        });
                    } else {
                        // Handle the case where playerColdwn is null
                    }
                } else {
                    // Handle the case where the document does not exist
                }
            } catch (ExecutionException | InterruptedException e) {
                // Handle any errors
            }
        }).start();
    }



}