package com.lanstructor.android.general;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lanstructor.android.R;
import com.lanstructor.android.model.Appointment;
import com.lanstructor.android.model.Group;
import com.lanstructor.android.model.User;
import com.zegocloud.uikit.prebuilt.videoconference.ZegoUIKitPrebuiltVideoConferenceConfig;
import com.zegocloud.uikit.prebuilt.videoconference.ZegoUIKitPrebuiltVideoConferenceFragment;

public class VideoCallActivity extends AppCompatActivity {
    final static long AppID = 836240429;
    final static String AppSign = "d8503d186646f34b8b68343340464a12fa9980b7349ae58ac8367e8be1e63909";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        getSupportActionBar().hide();


        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
             User user = snapshot.getValue(User.class);
                if( getIntent().hasExtra("appointment")) {
                    Appointment appointment = (Appointment) getIntent().getSerializableExtra("appointment");
                    addFragment(appointment.id, FirebaseAuth.getInstance().getCurrentUser().getUid(), user.username);
                }else if( getIntent().hasExtra("group")){
                    Group group  = (Group) getIntent().getSerializableExtra("group");
                    addFragment(group.id, FirebaseAuth.getInstance().getCurrentUser().getUid(), user.username);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });


    }
    public void addFragment(String conferenceID, String userID,String userName) {
        long appID = AppID;
        String appSign = AppSign;
        ZegoUIKitPrebuiltVideoConferenceConfig config = new ZegoUIKitPrebuiltVideoConferenceConfig();
        ZegoUIKitPrebuiltVideoConferenceFragment fragment = ZegoUIKitPrebuiltVideoConferenceFragment.newInstance(appID, appSign, userID, userName,conferenceID,config);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commitNow();
    }



}