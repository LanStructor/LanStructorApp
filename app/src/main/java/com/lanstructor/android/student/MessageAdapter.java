package com.lanstructor.android.student;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lanstructor.android.R;
import com.lanstructor.android.model.Message;
import com.lanstructor.android.model.User;

import java.util.ArrayList;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.InstructorViewHolder> {

    Context context;
    ArrayList<Message> messages;
     TextToSpeech textToSpeechSystem;

    public MessageAdapter(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public InstructorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message,parent,false);
        return new InstructorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstructorViewHolder holder, int position) {

        if(messages.get(position).senderId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            holder.sender.setVisibility(View.VISIBLE);
            holder.receiver.setVisibility(View.GONE);
            holder.receiver_name.setVisibility(View.GONE);

            holder.sender.setText(messages.get(position).message);
        }else{
            holder.receiver.setVisibility(View.VISIBLE);
            holder.receiver_name.setVisibility(View.VISIBLE);
            holder.sender.setVisibility(View.GONE);

            holder.receiver.setText(messages.get(position).message);
            FirebaseDatabase.getInstance().getReference().child("users").child(messages.get(position).senderId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    holder.receiver_name.setText(user.username);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        holder.receiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeechSystem = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            String textToSay = holder.receiver.getText().toString();
                            textToSpeechSystem.setLanguage(Locale.ENGLISH);

                            textToSpeechSystem.speak(textToSay, TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                });
            }
        });




    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class InstructorViewHolder extends RecyclerView.ViewHolder{

        TextView sender,receiver,receiver_name;
        public InstructorViewHolder(@NonNull View itemView) {
            super(itemView);
            sender = itemView.findViewById(R.id.sender);
            receiver = itemView.findViewById(R.id.receiver);
            receiver_name = itemView.findViewById(R.id.receiver_name);
        }
    }
}
