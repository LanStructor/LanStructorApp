package com.lanstructor.android.student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.lanstructor.android.R;
import com.lanstructor.android.instructor.CoursesFragment;

public class HomeStudentActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener {
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_student);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(this);setTitle("Home");
        bottomNavigationView.setSelectedItemId(R.id.home);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.home:
            setTitle("Home");
            getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,new InstructorsFragment()).commit();
            break;
            case R.id.courses:
                setTitle("Courses");
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,new CoursesFragment()).commit();
            break;
            case R.id.chat:
                setTitle("Chat");
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,new ChatsFragment()).commit();
            break;
            case R.id.group_chat:
                setTitle("Group Chat");
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,new GroupChatFragment()).commit();
            break;
            case R.id.profile:
                setTitle("Profile");
                getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,new StudentProfileFragment()).commit();
            break;
        }
        return true;
    }
}