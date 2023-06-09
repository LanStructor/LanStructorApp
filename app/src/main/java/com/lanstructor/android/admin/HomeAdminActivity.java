package com.lanstructor.android.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lanstructor.android.R;
import com.lanstructor.android.authentication.WelcomeActivity;
import com.lanstructor.android.model.User;

import java.util.ArrayList;

public class HomeAdminActivity extends AppCompatActivity {

    ArrayList<User> instructors = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);
        getSupportActionBar().setTitle("Home");

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        InstructorAdapter adapter = new InstructorAdapter(HomeAdminActivity.this,instructors);
        recyclerView.setAdapter(adapter);
       //                                                                      to get only instructors
        FirebaseDatabase.getInstance().getReference().child("users").orderByChild("userType").equalTo("Instructor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                instructors.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User instructor = dataSnapshot.getValue(User.class);
                    if(instructor.status.equals("New")) {
                        instructors.add(instructor);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //to display add group and logout icons
        getMenuInflater().inflate(R.menu.logout_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.logout){
            AlertDialog.Builder alert = new AlertDialog.Builder(HomeAdminActivity.this);
            alert.setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            PreferenceManager.getDefaultSharedPreferences(HomeAdminActivity.this).edit().clear().commit();
                            //sign out
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(HomeAdminActivity.this, WelcomeActivity.class));
                            finish();
                        }
                    }).setNegativeButton("Cancel", null);

            alert.show();

        }else if(item.getItemId() ==  R.id.add_group){
            startActivity(new Intent(HomeAdminActivity.this, AddGroupActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
}