package com.lanstructor.android.student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lanstructor.android.R;
import com.lanstructor.android.instructor.AddHomeWorkActivity;

public class ShowHomeworkActivity extends AppCompatActivity {
    ImageView homeworkImage;
    String courseId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_homework);

        getSupportActionBar().setTitle("Homework");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        courseId = getIntent().getStringExtra("courseId");

        homeworkImage = findViewById(R.id.homeworkImage);
        Button showAnswer = findViewById(R.id.showAnswer);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("homeworks").child(courseId+"homeworkImage");
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Glide.with(ShowHomeworkActivity.this)
                        .load(uri)
                        .placeholder(R.drawable.ic_baseline_add_photo_alternate_24)
                        .into(homeworkImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {}
        });

        showAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StorageReference storageRefAnswer = FirebaseStorage.getInstance().getReference().child("homeworks").child(courseId+"homeworkAnswerImage");
                storageRefAnswer.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Got the download URL for 'users/me/profile.png'
                        Glide.with(ShowHomeworkActivity.this)
                                .load(uri)
                                .placeholder(R.drawable.ic_baseline_add_photo_alternate_24)
                                .into(homeworkImage);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {}
                });
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);

    }
}