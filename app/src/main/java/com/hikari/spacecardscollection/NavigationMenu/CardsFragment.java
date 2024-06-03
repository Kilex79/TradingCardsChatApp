package com.hikari.spacecardscollection.NavigationMenu;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.FirebaseFirestore;
import com.hikari.spacecardscollection.Arapter.MyAdapterCards;
import com.hikari.spacecardscollection.Firebase.FirestoreDatabase;
import com.hikari.spacecardscollection.Objects.Card;
import com.hikari.spacecardscollection.User.User;
import com.hikari.spacecardscollection.databinding.FragmentCardsBinding;

import java.util.ArrayList;
import java.util.List;

public class CardsFragment extends Fragment {

    private List<Card> userCards;
    private User user;
    private FragmentCardsBinding binding;
    private FirestoreDatabase firestoreDatabase;
    private RecyclerView cardsRecyclerView;
    private MyAdapterCards myAdapterCards;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }
        binding = FragmentCardsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        firestoreDatabase = new FirestoreDatabase(db);

        // Inicializar userCards
        userCards = new ArrayList<>();

        cardsRecyclerView = binding.cardsRecyclerView;
        cardsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        myAdapterCards = new MyAdapterCards(requireContext(), userCards);
        cardsRecyclerView.setAdapter(myAdapterCards);

        // Llamar al método para recuperar las cartas del usuario
        firestoreDatabase.fetchUserCards(user.getUserEmail(), new FirestoreDatabase.FirestoreCardsListener() {
            @Override
            public void onSuccess(List<Card> cards) {
                //userCards.clear();
                userCards.addAll(cards);
                myAdapterCards.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                // Manejar error
                e.printStackTrace();
            }
        });
        return root;
    }
}
