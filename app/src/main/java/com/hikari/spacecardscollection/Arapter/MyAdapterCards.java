package com.hikari.spacecardscollection.Arapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.hikari.spacecardscollection.Objects.Card;
import com.hikari.spacecardscollection.R;

import java.util.List;

public class MyAdapterCards extends RecyclerView.Adapter<MyAdapterCards.MyViewHolder> {

    private final Context context;
    private final List<Card> cardList;

    public MyAdapterCards(Context context, List<Card> cardList) {
        this.context = context;
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public MyAdapterCards.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_item_row, parent, false);
        return new MyAdapterCards.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterCards.MyViewHolder holder, int position) {
        Card card = cardList.get(position);

        holder.name.setText(card.getCardName());
        holder.dmg.setText("Dmg: " + card.getAttackDmg());
        holder.hp.setText("Hp: " + card.getHp());
        holder.agil.setText("Agil: " + card.getAgility());
        holder.lvl.setText("Lvl: " + card.getCardLvl());

        Glide.with(context)
                .load(card.getCardImg())
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView dmg;
        private TextView hp;
        private TextView agil;
        private TextView lvl;
        private ShapeableImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.card_name_row);
            dmg = itemView.findViewById(R.id.card_dmg_row);
            hp = itemView.findViewById(R.id.card_hp_row);
            agil = itemView.findViewById(R.id.card_agil_row);
            lvl = itemView.findViewById(R.id.card_lvl_row);
            image = itemView.findViewById(R.id.card_image_row);
        }
    }
}
