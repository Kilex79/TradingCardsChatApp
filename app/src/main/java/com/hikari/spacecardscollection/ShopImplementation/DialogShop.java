package com.hikari.spacecardscollection.ShopImplementation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.hikari.spacecardscollection.NavigationMenu.ShopFragment;
import com.hikari.spacecardscollection.Objects.Card;
import com.hikari.spacecardscollection.R;
import com.hikari.spacecardscollection.User.User;

public class DialogShop extends AppCompatDialogFragment {

    private ShopFragment shopFragment;

    private TextView cardName;
    private ShapeableImageView cardImage;
    private Card card;
    private Button continueBnt;

    public DialogShop(ShopFragment shopFragment, Card card) {
        this.shopFragment = shopFragment;
        this.card = card;
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_activity_show_card, container, false);

        cardName = view.findViewById(R.id.card_name_dialog);
        cardImage = view.findViewById(R.id.card_image_dialog);
        continueBnt = view.findViewById(R.id.continue_dialog_btn);

        cardName.setText(card.getCardName());

        // Cargar la imagen utilizando Glide desde la URL proporcionada por Card
        Glide.with(requireContext())
                .load(card.getCardImg())
                .into(cardImage);


        continueBnt.setOnClickListener(v -> {
            dismiss();
        });

        return view;
    }
}
