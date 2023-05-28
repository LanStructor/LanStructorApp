package com.lanstructor.android.student;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lanstructor.android.R;
import com.lanstructor.android.VideoCallActivity;
import com.lanstructor.android.model.Appointment;
import com.lanstructor.android.model.Group;
import com.lanstructor.android.model.Message;

import java.util.ArrayList;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    ArrayList<Message> messages = new ArrayList<>();
    private final int REQ_CODE_SPEECH_INPUT = 100;
    EditText messageBox;
    Appointment appointment;
    String userType;
    boolean isMeetingStarted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
         userType = sharedPreferences.getString("userType","");


        appointment = (Appointment) getIntent().getSerializableExtra("appointment");
        getSupportActionBar().setTitle("Chat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        MessageAdapter adapter = new MessageAdapter(this,messages);
        recyclerView.setAdapter(adapter);
        FirebaseDatabase.getInstance().getReference().child("appointmentMessages").child(appointment.id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Message message = dataSnapshot.getValue(Message.class);
                    messages.add(message);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ImageButton send = findViewById(R.id.send);

         messageBox = findViewById(R.id.messageBox);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!messageBox.getText().toString().isEmpty()){
                    String id = FirebaseDatabase.getInstance().getReference().child("appointmentMessages").child(appointment.id).push().getKey();
                    Message message = new Message(id,messageBox.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getUid());
                    FirebaseDatabase.getInstance().getReference().child("appointmentMessages").child(appointment.id).child(message.id).setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                messageBox.setText("");
                            }
                        }
                    });
                }
            }
        });
        ImageButton stt = findViewById(R.id.stt);
        stt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                        getString(R.string.speech_prompt));
                try {
                    startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.speech_not_supported),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_CODE_SPEECH_INPUT && resultCode == RESULT_OK){
            ArrayList<String> result = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String t = result.get(0);
            messageBox.setText(t);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.call,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }else if(item.getItemId() == R.id.call){
            if(userType.equals("Student")){
                if(messages.get(messages.size()-1).message.equals("Meeting has bes started join to meeting by press call button")) {
                    Intent intent = new Intent(ChatActivity.this, VideoCallActivity.class);
                    intent.putExtra("appointment", appointment);
                    startActivity(intent);
                    isMeetingStarted = true;
                }else{
                    Toast.makeText(this, "meeting not started yet", Toast.LENGTH_SHORT).show();
                }
            }else{
                String id = FirebaseDatabase.getInstance().getReference().child("appointmentMessages").child(appointment.id).push().getKey();
                Message message = new Message(id,"Meeting has been started, join the meeting by pressing call button", FirebaseAuth.getInstance().getCurrentUser().getUid());
                FirebaseDatabase.getInstance().getReference().child("appointmentMessages").child(appointment.id).child(message.id).setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            messageBox.setText("");
                            Intent intent = new Intent(ChatActivity.this, VideoCallActivity.class);
                            intent.putExtra("appointment",appointment);
                            startActivity(intent);
                            isMeetingStarted = true;
                        }
                    }
                });
            }


        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!userType.equals("Student")){
            if(isMeetingStarted) {
                String id = FirebaseDatabase.getInstance().getReference().child("appointmentMessages").child(appointment.id).push().getKey();
                Message message = new Message(id, "Meeting ended", FirebaseAuth.getInstance().getCurrentUser().getUid());
                FirebaseDatabase.getInstance().getReference().child("appointmentMessages").child(appointment.id).child(message.id).setValue(message);
                isMeetingStarted = false;
            }
        }
    }
}