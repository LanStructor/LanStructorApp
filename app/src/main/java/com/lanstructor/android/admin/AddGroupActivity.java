package com.lanstructor.android.admin;

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
import com.lanstructor.android.model.Group;

import java.io.ByteArrayOutputStream;

public class AddGroupActivity extends AppCompatActivity {
    Uri uri;
    ImageView add_group_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        getSupportActionBar().setTitle("Add Group");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        add_group_image = findViewById(R.id.add_group_image);
        EditText name = findViewById(R.id.name);
        EditText bio = findViewById(R.id.bio);
        Spinner mainLang = findViewById(R.id.mainLang);
        Button add = findViewById(R.id.add);

        add_group_image.setOnClickListener(new View.OnClickListener() {
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
                    Toast.makeText(AddGroupActivity.this, "Select group image", Toast.LENGTH_SHORT).show();
                }else if(name.getText().toString().isEmpty()){
                    name.setError("Enter name");
                }else if(bio.getText().toString().isEmpty()){
                    bio.setError("Enter bio");
                }else if(mainLang.getSelectedItem().toString().equals("Select your main language")){
                    Toast.makeText(AddGroupActivity.this, "Select your main language", Toast.LENGTH_SHORT).show();
                } else {
                    ProgressDialog progressDialog = new ProgressDialog(AddGroupActivity.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();

                    String id = FirebaseDatabase.getInstance().getReference().child("groups").push().getKey();
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("groups").child(id);

                    add_group_image.setDrawingCacheEnabled(true);
                    add_group_image.buildDrawingCache();

                    Bitmap bitmap = ((BitmapDrawable) add_group_image.getDrawable()).getBitmap();
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
                            Toast.makeText(AddGroupActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Group group = new Group(id,name.getText().toString(),bio.getText().toString(),mainLang.getSelectedItem().toString());
                            FirebaseDatabase.getInstance().getReference().child("groups").child(group.id).setValue(group).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()){
                                        finish();
                                    }else{
                                        Toast.makeText(AddGroupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
            add_group_image.setImageURI(uri);
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