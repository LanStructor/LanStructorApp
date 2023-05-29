package com.lanstructor.android.student;

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
import com.lanstructor.android.model.Group;

import java.util.ArrayList;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.InstructorViewHolder> {

    Context context;
    ArrayList<Group> groups;

    public GroupAdapter(Context context, ArrayList<Group> groups) {
        this.context = context;
        this.groups = groups;
    }

    @NonNull
    @Override
    public InstructorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group,parent,false);
        return new InstructorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstructorViewHolder holder, int position) {
        holder.name.setText(groups.get(position).name);
        holder.lang.setText(groups.get(position).mainLang);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("groups").child(groups.get(position).id);
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
            public void onFailure(@NonNull Exception exception) {}
        });


    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    class InstructorViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView name,lang,time,status;
        public InstructorViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            lang = itemView.findViewById(R.id.lang);
            img = itemView.findViewById(R.id.img);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,ChatGroupActivity.class);
                    intent.putExtra("group",groups.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });


        }
    }
}
