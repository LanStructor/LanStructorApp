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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lanstructor.android.R;
import com.lanstructor.android.admin.AddGroupActivity;
import com.lanstructor.android.model.Course;
import com.lanstructor.android.model.Group;
import com.lanstructor.android.model.Video;

import java.io.ByteArrayOutputStream;

public class AddVideoActivity extends AppCompatActivity {

    ImageView video_image;
    Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video);
        getSupportActionBar().setTitle("Add Video");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Course course = (Course) getIntent().getSerializableExtra("course");

        video_image = findViewById(R.id.video_image);
        EditText title = findViewById(R.id.title);
        EditText urlPath = findViewById(R.id.urlPath);

        Button add = findViewById(R.id.add);

        video_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,34);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uri == null){
                    Toast.makeText(AddVideoActivity.this, "Select your certification", Toast.LENGTH_SHORT).show();
                }else if(title.getText().toString().isEmpty()){
                    title.setError("Enter title");
                }else if(urlPath.getText().toString().isEmpty()){
                    urlPath.setError("Enter urlPath");
                } else {
                    ProgressDialog progressDialog = new ProgressDialog(AddVideoActivity.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                    String id = FirebaseDatabase.getInstance().getReference().child("videos").child(course.id).push().getKey();
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("videos").child(course.id).child(id);
                    video_image.setDrawingCacheEnabled(true);
                    video_image.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) video_image.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = storageRef.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            progressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(AddVideoActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Video video = new Video(id,title.getText().toString(),urlPath.getText().toString(),course.id);
                            FirebaseDatabase.getInstance().getReference().child("videos").child(course.id).child(video.id).setValue(video).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()){
                                        finish();
                                    }else{
                                        Toast.makeText(AddVideoActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    });

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 34 && resultCode == RESULT_OK){
            uri = data.getData();
            video_image.setImageURI(uri);
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