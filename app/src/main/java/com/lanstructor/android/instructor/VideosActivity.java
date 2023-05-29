package com.lanstructor.android.instructor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lanstructor.android.R;
import com.lanstructor.android.model.Course;
import com.lanstructor.android.model.Video;
import com.lanstructor.android.student.ShowHomeworkActivity;

import java.util.ArrayList;

public class VideosActivity extends AppCompatActivity {

    Course course;
    String userType;
    ArrayList<Video> videos = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        getSupportActionBar().setTitle("Videos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton addVideo = findViewById(R.id.floatingActionButton);

         course = (Course) getIntent().getSerializableExtra("course");

        addVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),AddVideoActivity.class);
                intent.putExtra("course",course);
                startActivity(intent);
            }
        });


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
         userType = sharedPreferences.getString("userType","");

        if(userType.equals("Student")){
            addVideo.setVisibility(View.GONE);
        }


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        VideoAdapter adapter = new VideoAdapter(this,videos,userType);
        recyclerView.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference().child("videos").child(course.id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                videos.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Video video = dataSnapshot.getValue(Video.class);
                    videos.add(video);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(userType.equals("Student")){
            getMenuInflater().inflate(R.menu.homework,menu);
        }else {
            getMenuInflater().inflate(R.menu.add_homework,menu);
        }
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        if(userType.equals("Student")) {
         if (item.getItemId() == R.id.homework) {
                Intent intent = new Intent(this, ShowHomeworkActivity.class);
                intent.putExtra("courseId", course.id);
                startActivity(intent);
            }
        }else {
          if(item.getItemId() == R.id.addHomeWork){
                Intent intent = new Intent(this, AddHomeWorkActivity.class);
                intent.putExtra("courseId",course.id);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);

    }
}