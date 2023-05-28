package com.lanstructor.android.student;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lanstructor.android.R;
import com.lanstructor.android.model.Appointment;
import com.lanstructor.android.model.User;

import java.util.ArrayList;

public class ChatAppointmentAdapter extends RecyclerView.Adapter<ChatAppointmentAdapter.InstructorViewHolder> {

    Context context;
    ArrayList<Appointment> appointments;
    String userType;
    public ChatAppointmentAdapter(Context context, ArrayList<Appointment> appointments,String userType) {
        this.context = context;
        this.appointments = appointments;
        this.userType = userType;
    }

    @NonNull
    @Override
    public InstructorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_appointment,parent,false);

        return new InstructorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstructorViewHolder holder, int position) {
        holder.date.setText(appointments.get(position).date);
        holder.time.setText(appointments.get(position).time);

        FirebaseDatabase.getInstance().getReference().child("users").child(userType.equals("Student")? appointments.get(position).instructorId : appointments.get(position).studentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                holder.username.setText(user.username);
                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("users").child(userType.equals("Student")? user.id+"Profile": user.id);
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Got the download URL for 'users/me/profile.png'
                        Glide.with(context)
                                .load(uri)
                                .placeholder(R.drawable.ic_baseline_hide_image_24)
                                .into(holder.img);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    class InstructorViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView username,date,time;
        public InstructorViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            img = itemView.findViewById(R.id.img);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,ChatActivity.class);
                    intent.putExtra("appointment",appointments.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });


        }
    }
}
