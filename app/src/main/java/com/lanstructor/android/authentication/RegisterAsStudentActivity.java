package com.lanstructor.android.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lanstructor.android.R;
import com.lanstructor.android.model.User;

public class RegisterAsStudentActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_as_student);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        EditText username = findViewById(R.id.username);
        EditText phone = findViewById(R.id.phone);
        Spinner mainLang = findViewById(R.id.mainLang);
        Spinner secondLang = findViewById(R.id.secondLang);
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        Button register = findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().toString().isEmpty()){
                    username.setError("Enter username");
                }else if(phone.getText().toString().isEmpty()){
                    phone.setError("Enter phone");
                }else if(mainLang.getSelectedItem().toString().equals("Select your main language")){
                    Toast.makeText(RegisterAsStudentActivity.this, "Select your main language", Toast.LENGTH_SHORT).show();
                }else if(secondLang.getSelectedItem().toString().equals("Select your second language")){
                    Toast.makeText(RegisterAsStudentActivity.this, "Select your second language", Toast.LENGTH_SHORT).show();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()){
                    email.setError("Enter valid email address");
                }else if(password.getText().toString().length() < 6){
                    password.setError("Enter password length 6");
                }else{
                    ProgressDialog progressDialog = new ProgressDialog(RegisterAsStudentActivity.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();

                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(RegisterAsStudentActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    User user = new User(mAuth.getCurrentUser().getUid(),username.getText().toString(),email.getText().toString(),phone.getText().toString(),mainLang.getSelectedItem().toString(),secondLang.getSelectedItem().toString(),"Student");
                                    //create or get users folder |the object inside user folder is the user id | add the object attributes inside the id
                                    mDatabase.child("users").child(user.id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.dismiss();
                                           if(task.isSuccessful()){
                                               Toast.makeText(RegisterAsStudentActivity.this, "Register Successfully", Toast.LENGTH_SHORT).show();
                                               Intent intent =new Intent(RegisterAsStudentActivity.this,WelcomeActivity.class);
                                               startActivity(intent);
                                               finish();

                                           } else{
                                               Toast.makeText(RegisterAsStudentActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                           }
                                        }

                                    });
                                } else {
                                    progressDialog.dismiss();
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(RegisterAsStudentActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            }
        });


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