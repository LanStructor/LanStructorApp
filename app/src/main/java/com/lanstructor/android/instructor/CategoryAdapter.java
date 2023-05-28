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

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CourseViewHolder> {

    Context context;
    ArrayList<String> categories;
    int selectedPosition = 0;
    String userType;
    ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CategoryAdapter(Context context, ArrayList<String> categories) {
        this.context = context;
        this.categories = categories;

    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category,parent,false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        if (selectedPosition == position){
            holder.name.setBackgroundColor(context.getResources().getColor(R.color.purple_500));
            holder.name.setTextColor(context.getResources().getColor(R.color.white));

        }else{
            holder.name.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.name.setTextColor(context.getResources().getColor(R.color.black));

        }
        holder.name.setText(categories.get(position));

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class CourseViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  itemClickListener.onItemClick(categories.get(getAdapterPosition()),getAdapterPosition());
                }
            });


        }
    }

    public interface ItemClickListener{
       void  onItemClick(String category,int position);
    }
}
