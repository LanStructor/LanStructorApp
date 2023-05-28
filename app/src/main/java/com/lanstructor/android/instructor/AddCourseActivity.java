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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lanstructor.android.R;
import com.lanstructor.android.admin.AddGroupActivity;
import com.lanstructor.android.model.Course;
import com.lanstructor.android.model.Group;

import java.io.ByteArrayOutputStream;

public class AddCourseActivity extends AppCompatActivity {
    Uri uri;
    ImageView selectImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        getSupportActionBar().setTitle("Add Course");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        selectImage = findViewById(R.id.selectImage);
        EditText name = findViewById(R.id.name);
        EditText price = findViewById(R.id.price);
        EditText details = findViewById(R.id.details);
        Spinner mainLang = findViewById(R.id.mainLang);
        Spinner category = findViewById(R.id.category);
        Button add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uri == null){
                    Toast.makeText(AddCourseActivity.this, "Select Course Image", Toast.LENGTH_SHORT).show();
                }else if(name.getText().toString().isEmpty()){
                    name.setError("Enter name");
                }else if(price.getText().toString().isEmpty()){
                    price.setError("Enter price");
                }else if(details.getText().toString().isEmpty()){
                    details.setError("Enter details");
                }else if(mainLang.getSelectedItem().toString().equals("Select main language")){
                    Toast.makeText(AddCourseActivity.this, "Select main language", Toast.LENGTH_SHORT).show();
                }else {
                    ProgressDialog progressDialog = new ProgressDialog(AddCourseActivity.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                    String id = FirebaseDatabase.getInstance().getReference().child("courses").push().getKey();
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("courses").child(id);
                    selectImage.setDrawingCacheEnabled(true);
                    selectImage.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) selectImage.getDrawable()).getBitmap();
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
                            Toast.makeText(AddCourseActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Course course = new Course(id, FirebaseAuth.getInstance().getCurrentUser().getUid(),name.getText().toString(),details.getText().toString(),price.getText().toString(),mainLang.getSelectedItem().toString(),category.getSelectedItem().toString());
                            FirebaseDatabase.getInstance().getReference().child("courses").child(course.id).setValue(course).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()){
                                        finish();
                                    }else{
                                        Toast.makeText(AddCourseActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    });

                }
            }
        });

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,34);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 34 && resultCode == RESULT_OK){
            uri = data.getData();
            selectImage.setImageURI(uri);
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