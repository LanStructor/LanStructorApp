package com.lanstructor.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lanstructor.android.admin.AddGroupActivity;
import com.lanstructor.android.admin.HomeAdminActivity;
import com.lanstructor.android.instructor.HomeInstructorActivity;
import com.lanstructor.android.model.User;
import com.lanstructor.android.student.HomeStudentActivity;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        Button login = findViewById(R.id.login);
        TextView forgetPassword = findViewById(R.id.forgetPassword);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches()) {
                    email.setError("Enter valid email address");
                } else if (password.getText().toString().length() < 6) {
                    password.setError("Enter password length 6");
                }else{
                ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.show();

                mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            progressDialog.dismiss();
                                            User user = snapshot.getValue(User.class);

                                            if (user.userType.equals("Admin")) {
                                                editor.putString("userType", user.userType).commit();
                                                Intent intent = new Intent(LoginActivity.this, HomeAdminActivity.class);
                                                startActivity(intent);
                                            } else if (user.userType.equals("Instructor")) {
                                                if (user.status.equals("New")) {
                                                    Toast.makeText(LoginActivity.this, "Your Account Under Review", Toast.LENGTH_LONG).show();
                                                } else if (user.status.equals("Rejected")) {
                                                    Toast.makeText(LoginActivity.this, "Your Account Has Been Rejected", Toast.LENGTH_LONG).show();
                                                } else if (user.status.equals("Accepted")) {
                                                    editor.putString("userType", user.userType).commit();
                                                    Intent intent = new Intent(LoginActivity.this, HomeInstructorActivity.class);
                                                    startActivity(intent);
                                                }
                                            } else if (user.userType.equals("Student")) {
                                                editor.putString("userType", user.userType).commit();
                                                editor.putString("lang", user.secondLang).commit();
                                                Intent intent = new Intent(LoginActivity.this, HomeStudentActivity.class);
                                                startActivity(intent);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {}
                                    });
                                } else {
                                    progressDialog.dismiss();
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
              }
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });
    }
    //??????????????????????????????????????????????????????????????????????
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);

    }
//??????????????????????????????????????????????????????????????????????
 }