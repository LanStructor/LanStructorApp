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
import com.lanstructor.android.LoginActivity;
import com.lanstructor.android.PlayVideoFormYoutubeActivity;
import com.lanstructor.android.R;
import com.lanstructor.android.admin.HomeAdminActivity;
import com.lanstructor.android.model.Course;
import com.lanstructor.android.model.Video;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.CourseViewHolder> {

    Context context;
    ArrayList<Video> videos;
    String userType;
    public VideoAdapter(Context context, ArrayList<Video> videos, String userType) {
        this.context = context;
        this.videos = videos;
        this.userType = userType;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video,parent,false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        holder.name.setText(videos.get(position).title);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("videos").child(videos.get(position).courseId).child(videos.get(position).id);
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


        if(userType.equals("Student")){
            holder.delete.setVisibility(View.GONE);
        }

        int i = position;
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setMessage("Do you want to delete "+ videos.get(i).title+"?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase.getInstance().getReference().child("videos").child(videos.get(i).courseId).child(videos.get(i).id).removeValue();
                            }
                        }).setNegativeButton("No", null);

                alert.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    class CourseViewHolder extends RecyclerView.ViewHolder{
        ImageView img,delete;
        TextView name;
        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);

            img = itemView.findViewById(R.id.img);
            delete = itemView.findViewById(R.id.delete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), PlayVideoFormYoutubeActivity.class);
                    intent.putExtra("video",videos.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });


        }
    }
}
