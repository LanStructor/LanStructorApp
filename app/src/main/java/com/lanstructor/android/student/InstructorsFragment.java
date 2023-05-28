package com.lanstructor.android.student;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lanstructor.android.R;
import com.lanstructor.android.model.User;

import java.util.ArrayList;


public class InstructorsFragment extends Fragment {


    ArrayList<User> instructors = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_instructors, container, false);


        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        InstructorForStudentAdapter adapter = new InstructorForStudentAdapter(getActivity(),instructors);
        recyclerView.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference().child("users").orderByChild("userType").equalTo("Instructor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                instructors.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User instructor = dataSnapshot.getValue(User.class);
                    if(instructor.status.equals("Accepted")) {
                        instructors.add(instructor);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}