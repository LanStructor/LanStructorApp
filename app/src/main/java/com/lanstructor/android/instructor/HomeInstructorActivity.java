package com.lanstructor.android.instructor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lanstructor.android.R;
import com.lanstructor.android.student.ChatsFragment;
import com.lanstructor.android.instructor.CoursesFragment;
import com.lanstructor.android.student.GroupChatFragment;
import com.lanstructor.android.student.InstructorsFragment;
import com.lanstructor.android.student.StudentProfileFragment;

public class HomeInstructorActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_instructor);


        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(this);

        bottomNavigationView.setSelectedItemId(R.id.home);
        //setTitle("Home");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.home:
                setTitle("Home");
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,new AppointmentsFragment()).commit();
                break;
            case R.id.courses:
                setTitle("Courses");
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,new CoursesFragment()).commit();
                break;
            case R.id.chat:
                setTitle("Chat");
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,new ChatsFragment()).commit();
                break;

            case R.id.profile:
                setTitle("Profile");
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,new InstructorProfileFragment()).commit();
                break;
        }
        return true;
    }
}