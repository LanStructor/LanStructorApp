package com.lanstructor.android.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lanstructor.android.R;
import com.lanstructor.android.admin.HomeAdminActivity;
import com.lanstructor.android.instructor.HomeInstructorActivity;
import com.lanstructor.android.student.HomeStudentActivity;

import java.util.Locale;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    public void onStart() {
        super.onStart();

        //Make the application language English so that the direction of the application is from left to right
        Configuration config  =  new Configuration();
        config.locale = new Locale("en");
        getResources().updateConfiguration(config,getResources().getDisplayMetrics());

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String userType = sharedPreferences.getString("userType","userType");

            if(userType.equals("Admin")){
                Intent intent = new Intent(WelcomeActivity.this, HomeAdminActivity.class);
                startActivity(intent);
            }else if(userType.equals("Instructor")){
                    Intent intent = new Intent(WelcomeActivity.this, HomeInstructorActivity.class);
                    startActivity(intent);
            }else if(userType.equals("Student")){
                Intent intent = new Intent(WelcomeActivity.this, HomeStudentActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //hide action bar on the top
        getSupportActionBar().hide();

        //Make the application language English so that the direction of the application is from left to right
        Configuration config  =  new Configuration();
        config.locale = new Locale("en");
        getResources().updateConfiguration(config,getResources().getDisplayMetrics());

        Button login = findViewById(R.id.login);
        Button register = findViewById(R.id.register);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //this code will run when button clicked

                Intent intent = new Intent(WelcomeActivity.this,LoginActivity.class);
                startActivity(intent);

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //this code will run when button clicked
                Intent intent = new Intent(WelcomeActivity.this,SelectUserTypeActivity.class);
                startActivity(intent);
            }
        });
    }
}