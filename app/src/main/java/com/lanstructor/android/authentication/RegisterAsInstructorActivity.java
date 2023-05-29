package com.lanstructor.android.authentication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lanstructor.android.R;
import com.lanstructor.android.model.User;

import java.io.ByteArrayOutputStream;

public class RegisterAsInstructorActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    Uri uri;
    ImageView selectCertificate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_as_instructor);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        EditText username = findViewById(R.id.username);
        EditText phone = findViewById(R.id.phone);
        Spinner mainLang = findViewById(R.id.mainLang);
        selectCertificate = findViewById(R.id.selectCertificate);
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        Button register = findViewById(R.id.register);

        selectCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,34);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().toString().isEmpty()){
                    username.setError("Enter username");
                }else if(phone.getText().toString().isEmpty()){
                    phone.setError("Enter phone");
                }else if(mainLang.getSelectedItem().toString().equals("Select your main language")){
                    Toast.makeText(RegisterAsInstructorActivity.this, "Select your main language", Toast.LENGTH_SHORT).show();
                } else if(uri == null){
                    Toast.makeText(RegisterAsInstructorActivity.this, "Select your certification", Toast.LENGTH_SHORT).show();
                } else if(!Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()){
                    email.setError("Enter valid email address");
                }else if(password.getText().toString().length() < 6){
                    password.setError("Enter password length 6");
                }else{
                    ProgressDialog progressDialog = new ProgressDialog(RegisterAsInstructorActivity.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();

                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(RegisterAsInstructorActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        //                                                             create users folder in db storage  | add the pic and name it with the user id so we can latter retrieve it with user id
                                        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
                                        selectCertificate.setDrawingCacheEnabled(true);
                                        selectCertificate.buildDrawingCache();

                                        Bitmap bitmap = ((BitmapDrawable) selectCertificate.getDrawable()).getBitmap();
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                        byte[] data = baos.toByteArray();

                                        UploadTask uploadTask = storageRef.putBytes(data);

                                        uploadTask.addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                progressDialog.dismiss();
                                                // If sign in fails, display a message to the user.
                                                Toast.makeText(RegisterAsInstructorActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                                // ...
                                                User user = new User(mAuth.getCurrentUser().getUid(),username.getText().toString(),email.getText().toString(),phone.getText().toString(),mainLang.getSelectedItem().toString(),"","Instructor","New");

                                                mDatabase.child("users").child(user.id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        progressDialog.dismiss();
                                                        if(task.isSuccessful()){
                                                            Toast.makeText(RegisterAsInstructorActivity.this, "Register Successfully", Toast.LENGTH_SHORT).show();
                                                            Intent intent =new Intent(RegisterAsInstructorActivity.this,WelcomeActivity.class);
                                                            startActivity(intent);
                                                            finish();

                                                        } else{
                                                            Toast.makeText(RegisterAsInstructorActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            }
                                        });

                                         } else {
                                        progressDialog.dismiss();
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(RegisterAsInstructorActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
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
            selectCertificate.setImageURI(uri);
        }
    }
//??????????????????????????????????????????????????????????????????????
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);

    }
//??????????????????????????????????????????????????????????????????????
}