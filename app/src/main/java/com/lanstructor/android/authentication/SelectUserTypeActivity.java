package com.lanstructor.android.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.lanstructor.android.R;

public class SelectUserTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user_type);
        getSupportActionBar().setTitle("Select User Type");
        //display back icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button btn_studnet = findViewById(R.id.btn_studnet);
        Button btn_instructor = findViewById(R.id.btn_instructor);

        btn_studnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectUserTypeActivity.this,RegisterAsStudentActivity.class);
                startActivity(intent);
            }
        });


        btn_instructor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectUserTypeActivity.this,RegisterAsInstructorActivity.class);
                startActivity(intent);
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