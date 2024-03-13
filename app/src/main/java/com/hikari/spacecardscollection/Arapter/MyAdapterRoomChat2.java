package com.hikari.spacecardscollection.Arapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hikari.spacecardscollection.R;
import com.hikari.spacecardscollection.RoomsImplementation.RoomsChat.Message;
import com.hikari.spacecardscollection.User.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAdapterRoomChat2 extends FirestoreRecyclerAdapter<Message, MyAdapterRoomChat2.MyViewHolder> {

    private Context context;
    private User user;
    private String userName;
    private MyViewHolder lastLongClickedViewHolder;
    private String roomName;

    private Map<Integer, Boolean> buttonVisibilityMap = new HashMap<>();

    public void updateButtonVisibility(int position, boolean isVisible) {
        buttonVisibilityMap.put(position, isVisible);
        notifyDataSetChanged(); // Notifica al adaptador para que actualice la vista
    }

    public MyAdapterRoomChat2(@NonNull FirestoreRecyclerOptions<Message> options, Context applicationContext, User user, String userName, String roomName) {
        super(options);
        this.context = applicationContext;
        this.user = user;
        this.userName = userName;
        this.roomName = roomName;
    }

    @NonNull
    @Override
    public MyAdapterRoomChat2.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.messages_item_row, parent, false);
        return new MyAdapterRoomChat2.MyViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Message model) {

        // Convertir Timestamp a formato de fecha legible
        //DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String dateString = dateFormat.format(model.getDate().toDate()); // Convertir Timestamp a Date y luego formatear


        boolean isButtonVisible = buttonVisibilityMap.getOrDefault(position, false);
        holder.myLinearLayout_options.setVisibility(isButtonVisible ? View.VISIBLE : View.GONE);

        if (user != null) {
            if (Objects.equals(model.getUser(), userName)) {
                holder.myLinearLayout.setVisibility(View.VISIBLE);
                holder.linearLayoutSender.setVisibility(View.GONE);
                holder.myMessageText.setText(model.getText());
                holder.myDate.setText(dateString);


                // Codigo experimental para borrar mensajes
                // ERROR:
                // Al eliminar un mensaje recien enviado la app crashea
                /*holder.myLinearLayout.setOnLongClickListener(v -> {
                    if (lastLongClickedViewHolder != null && lastLongClickedViewHolder != holder) {
                        // Si hay un elemento anterior con onLongClickListener activo y no es el mismo que el actual, ocúltalo
                        lastLongClickedViewHolder.myLinearLayout_options.setVisibility(View.GONE);
                    }
                    holder.myLinearLayout_options.setVisibility(View.VISIBLE); // Mostrar las opciones para el elemento actual
                    lastLongClickedViewHolder = holder; // Actualizar la referencia al último elemento activado

                    holder.delete_messageBtn.setOnClickListener(v1 -> {
                        // Agrega un retraso de 500 milisegundos (medio segundo) antes de llamar a deleteMessage
                        new Handler().postDelayed(() -> {
                            String idMessage = model.getId();
                            deleteMessage(idMessage);
                        }, 500); // 500 milisegundos = 0.5 segundos
                    });
                    return false;
                });*/

            } else {
                holder.messageSender.setText(model.getUser());
                holder.messageTextSender.setText(model.getText());
                holder.dateSender.setText(dateString);
                holder.linearLayoutSender.setVisibility(View.VISIBLE);
                holder.myLinearLayout.setVisibility(View.GONE);
            }
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView messageSender;
        private TextView messageTextSender;
        private TextView dateSender;
        private LinearLayout linearLayoutSender;
        private CircleImageView userIcon;

        private TextView myMessageText;
        private TextView myDate;
        private LinearLayout myLinearLayout;
        private LinearLayout myLinearLayout_options;
        private ImageButton delete_messageBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            messageSender = itemView.findViewById(R.id.sender_name);
            messageTextSender = itemView.findViewById(R.id.sender_message_text);
            dateSender = itemView.findViewById(R.id.sender_message_time);
            linearLayoutSender = itemView.findViewById(R.id.sender_message_layout);
            userIcon = itemView.findViewById(R.id.sender_rofile_image);


            myMessageText = itemView.findViewById(R.id.my_message);
            myDate = itemView.findViewById(R.id.my_message_time);
            myLinearLayout = itemView.findViewById(R.id.my_message_layout);
            myLinearLayout_options = itemView.findViewById(R.id.options_message_linearLayout);
            delete_messageBtn = itemView.findViewById(R.id.delete_message_imageButton);
        }
    }


    public void hideOptionsMenu() {
        if (lastLongClickedViewHolder != null && lastLongClickedViewHolder.myLinearLayout_options.getVisibility() == View.VISIBLE) {
            lastLongClickedViewHolder.myLinearLayout_options.setVisibility(View.GONE);
        }
    }

    public void deleteMessage(String messageId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Rooms")
                .document(roomName)
                .collection("Chat")
                .document(messageId)  // Utiliza el ID del mensaje para identificar el documento a eliminar
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Éxito al eliminar el documento
                    // Aquí puedes realizar cualquier acción adicional después de eliminar el mensaje
                })
                .addOnFailureListener(e -> {
                    // Error al eliminar el documento
                    // Aquí puedes manejar el error, si es necesario
                });
    }

}
