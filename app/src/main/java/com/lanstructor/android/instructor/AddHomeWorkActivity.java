package com.lanstructor.android.instructor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lanstructor.android.R;
import com.lanstructor.android.model.Video;

import java.io.ByteArrayOutputStream;

public class AddHomeWorkActivity extends AppCompatActivity {
    ImageView homeworkAnswerImage,homeworkImage;
    String courseId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_home_work);
        getSupportActionBar().setTitle("Add Homework");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

         courseId = getIntent().getStringExtra("courseId");

         homeworkImage = findViewById(R.id.homeworkImage);
         homeworkAnswerImage = findViewById(R.id.homeworkAnswerImage);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("homeworks").child(courseId+"homeworkImage");
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Glide.with(AddHomeWorkActivity.this)
                        .load(uri)
                        .placeholder(R.drawable.ic_baseline_add_photo_alternate_24)
                        .into(homeworkImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {}
        });

        StorageReference storageRefAnswer = FirebaseStorage.getInstance().getReference().child("homeworks").child(courseId+"homeworkAnswerImage");
        storageRefAnswer.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Glide.with(AddHomeWorkActivity.this)
                        .load(uri)
                        .placeholder(R.drawable.ic_baseline_add_photo_alternate_24)
                        .into(homeworkAnswerImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {}
        });


        homeworkImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,2);
            }
        });

        homeworkAnswerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2 && resultCode == RESULT_OK){
            Uri uri = data.getData();
            homeworkImage.setImageURI(uri);

            ProgressDialog progressDialog = new ProgressDialog(AddHomeWorkActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("homeworks").child(courseId+"homeworkImage");
            homeworkImage.setDrawingCacheEnabled(true);
            homeworkImage.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) homeworkImage.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] dataByteArray = baos.toByteArray();

            UploadTask uploadTask = storageRef.putBytes(dataByteArray);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    progressDialog.dismiss();
                    // If sign in fails, display a message to the user.
                    Toast.makeText(AddHomeWorkActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(AddHomeWorkActivity.this, "image uploaded successfully", Toast.LENGTH_SHORT).show();

                }
            });

        }else if(requestCode == 1 && resultCode == RESULT_OK){
            Uri uri = data.getData();
            homeworkAnswerImage.setImageURI(uri);

            ProgressDialog progressDialog = new ProgressDialog(AddHomeWorkActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("homeworks").child(courseId+"homeworkAnswerImage");
            homeworkAnswerImage.setDrawingCacheEnabled(true);
            homeworkAnswerImage.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) homeworkAnswerImage.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] dataByteArray = baos.toByteArray();

            UploadTask uploadTask = storageRef.putBytes(dataByteArray);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    progressDialog.dismiss();
                    // If sign in fails, display a message to the user.
                    Toast.makeText(AddHomeWorkActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(AddHomeWorkActivity.this, "image uploaded successfully", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);

    }
}