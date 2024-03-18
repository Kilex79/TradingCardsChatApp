package com.hikari.spacecardscollection.NavigationMenu;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

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
    private MutableLiveData<String> playerColdownLiveData = new MutableLiveData<>();
    private MutableLiveData<Long> gachaQuantityLiveData = new MutableLiveData<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }
        binding = FragmentShopBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        getPlayerColdown();



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

}
