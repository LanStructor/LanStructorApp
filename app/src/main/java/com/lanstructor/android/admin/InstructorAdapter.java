package com.lanstructor.android.admin;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lanstructor.android.R;
import com.lanstructor.android.model.User;

import java.util.ArrayList;

public class InstructorAdapter extends RecyclerView.Adapter<InstructorAdapter.InstructorViewHolder> {

    Context context;
    ArrayList<User> instructors;

    public InstructorAdapter(Context context, ArrayList<User> instructors) {
        this.context = context;
        this.instructors = instructors;
    }

    @NonNull
    @Override
    public InstructorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_instructor,parent,false);

        return new InstructorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstructorViewHolder holder, int position) {
        holder.username.setText(instructors.get(position).username);
        holder.lang.setText(instructors.get(position).mainLang);
        holder.phone.setText(instructors.get(position).phone);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("users").child(instructors.get(position).id);
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
    public int getItemCount() {
        return instructors.size();
    }

    class InstructorViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView lang,username,phone;
        public InstructorViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            lang = itemView.findViewById(R.id.lang);
            phone = itemView.findViewById(R.id.phone);
            img = itemView.findViewById(R.id.img);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,InstructorDetailsActivity.class);
                    intent.putExtra("instructor",instructors.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });
        }
    }
}
