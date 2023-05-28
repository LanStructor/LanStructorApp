package com.lanstructor.android.instructor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lanstructor.android.R;
import com.lanstructor.android.model.Course;

import java.util.ArrayList;


public class CoursesFragment extends Fragment {

    // 1- add course
    // 2- show courses
    // : delete or Show homeworks  - home work details students answer - answers
    // 4- add video
    // 5- show videos
    // 6 delete video

    ArrayList<Course> courses = new ArrayList<>();
    ArrayList<Course> coursesAfterFillter = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_courses, container, false);

        FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingActionButton);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView recyclerViewCategory = view.findViewById(R.id.recyclerViewCategory);

        LinearLayoutManager linearLayoutManagerHor = new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);

        recyclerViewCategory.setLayoutManager(linearLayoutManagerHor);
        recyclerView.setLayoutManager(linearLayoutManager);


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),AddCourseActivity.class);
                startActivity(intent);
            }
        });


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String userType = sharedPreferences.getString("userType","");
//--------------------------------------------------------------------------------------------------
        ArrayList<String> categories = new ArrayList<>();
        categories.add("All");
        categories.add("Speaking");
        categories.add("Listening");
        categories.add("Writing");
        categories.add("Grammar");
        categories.add("Vocabulary");

        CategoryAdapter categoryAdapter = new CategoryAdapter(getActivity(),categories);
        recyclerViewCategory.setAdapter(categoryAdapter);
//--------------------------------------------------------------------------------------------------
        CourseAdapter adapter = new CourseAdapter(getActivity(),courses,userType);
        recyclerView.setAdapter(adapter);

        if(userType.equals("Student")){
            floatingActionButton.setVisibility(View.GONE);
            String lang = sharedPreferences.getString("lang","");


            FirebaseDatabase.getInstance().getReference().child("courses").orderByChild("lang").equalTo(lang).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    courses.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Course course = dataSnapshot.getValue(Course.class);
                        courses.add(course);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else{
            FirebaseDatabase.getInstance().getReference().child("courses").orderByChild("instId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    courses.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Course course = dataSnapshot.getValue(Course.class);
                        courses.add(course);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
// this method from the interface in catagoryAdapter V
        categoryAdapter.setItemClickListener(new CategoryAdapter.ItemClickListener() {
            @Override
            public void onItemClick(String category, int position) {
                categoryAdapter.selectedPosition = position;

                if(category.equals("All")){
                    CourseAdapter adapter = new CourseAdapter(getActivity(), courses, userType);
                    recyclerView.setAdapter(adapter);
                }else {
                    coursesAfterFillter.clear();
                    for (Course course : courses) {
                        if (course.category.equals(category)) {
                            coursesAfterFillter.add(course);
                        }
                    }

                    CourseAdapter adapter = new CourseAdapter(getActivity(), coursesAfterFillter, userType);
                    recyclerView.setAdapter(adapter);
                }

                categoryAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }


}