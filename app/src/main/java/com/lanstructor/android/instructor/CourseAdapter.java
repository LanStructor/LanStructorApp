package com.lanstructor.android.instructor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lanstructor.android.R;
import com.lanstructor.android.model.Course;

import java.util.ArrayList;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    Context context;
    ArrayList<Course> courses;
    String userType;
    public CourseAdapter(Context context, ArrayList<Course> courses , String userType) {
        this.context = context;
        this.courses = courses;
        this.userType = userType;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_course,parent,false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        holder.name.setText(courses.get(position).name);
        holder.lang.setText(courses.get(position).lang);
        holder.price.setText(courses.get(position).price);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("courses").child(courses.get(position).id);
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
        if(userType.equals("Student")){
            holder.delete.setVisibility(View.GONE);
        }

        int i = position;
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setMessage("Do you want to delete "+ courses.get(i).name+"?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                                FirebaseDatabase.getInstance().getReference().child("videos").child(courses.get(i).id).removeValue();
                                FirebaseDatabase.getInstance().getReference().child("courses").child(courses.get(i).id).removeValue();
                            }
                        }).setNegativeButton("No", null);

                AlertDialog alert1 = alert.create();
                alert1.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    class CourseViewHolder extends RecyclerView.ViewHolder{
        ImageView img,delete;
        TextView name,lang,price;
        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            lang = itemView.findViewById(R.id.lang);
            price = itemView.findViewById(R.id.price);
            img = itemView.findViewById(R.id.img);
            delete = itemView.findViewById(R.id.delete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(),VideosActivity.class);
                    intent.putExtra("course",courses.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });


        }
    }
}