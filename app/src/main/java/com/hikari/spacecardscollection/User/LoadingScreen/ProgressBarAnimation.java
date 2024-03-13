package com.hikari.spacecardscollection.User.LoadingScreen;

import android.content.Context;
import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hikari.spacecardscollection.MainActivity;
import com.hikari.spacecardscollection.RoomsImplementation.Room;
import com.hikari.spacecardscollection.User.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProgressBarAnimation extends Animation {

    private Context context;
    private ProgressBar progressBar;
    private TextView textView;
    private float from;
    private float to;
    private User user;

    private List<Room> publicRooms;
    private List<Room> yourRooms;
    private String userEmail;

    public ProgressBarAnimation(Context context, ProgressBar progressBar, TextView textView, float from, float to, User user, List<Room> publicRooms, List<Room> yourRooms, String email) {
        this.context = context;
        this.progressBar = progressBar;
        this.textView = textView;
        this.from = from;
        this.to = to;
        this.user = user;
        this.publicRooms = publicRooms;
        this.yourRooms = yourRooms;
        this.userEmail = email;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to + from) * interpolatedTime;
        progressBar.setProgress((int) value);
        textView.setText((int) value + " %");

        /*if (value == to) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("email", this.userEmail);
            intent.putExtra("yourRooms", (Serializable) this.yourRooms);
            intent.putExtra("publicRooms", (Serializable) this.publicRooms);
            intent.putExtra("user", this.user);
            // Agregar la bandera FLAG_ACTIVITY_CLEAR_TOP para limpiar todas las instancias anteriores de MainActivity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Agregar la bandera FLAG_ACTIVITY_CLEAR_TASK para limpiar todas las actividades y crear una nueva tarea de actividad
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }*/
    }
}
