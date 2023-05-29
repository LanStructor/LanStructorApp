package com.lanstructor.android.student;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lanstructor.android.model.Appointment;
import com.lanstructor.android.R;
import com.lanstructor.android.model.User;

import java.util.ArrayList;
import java.util.Calendar;

public class InstructorForStudentAdapter extends RecyclerView.Adapter<InstructorForStudentAdapter.InstructorViewHolder> {

    Context context;
    ArrayList<User> instructors;

    public InstructorForStudentAdapter(Context context, ArrayList<User> instructors) {
        this.context = context;
        this.instructors = instructors;
    }

    @NonNull
    @Override
    public InstructorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_instructor_for_studnet,parent,false);
        return new InstructorViewHolder(view);
    }
    private int mYear, mMonth, mDay, mHour, mMinute;
    @Override
    public void onBindViewHolder(@NonNull InstructorViewHolder holder, int position) {
        holder.username.setText(instructors.get(position).username);
        holder.lang.setText(instructors.get(position).mainLang);
        holder.phone.setText(instructors.get(position).phone);
        int index = position;

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("users").child(instructors.get(position).id+"Profile");
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Glide.with(context)
                        .load(uri)
                        .placeholder(R.drawable.ic_baseline_hide_image_24)
                        .into(holder.img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        holder.bookAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);



                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                final Calendar c = Calendar.getInstance();
                                mHour = c.get(Calendar.HOUR_OF_DAY);
                                mMinute = c.get(Calendar.MINUTE);
                                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                                        new TimePickerDialog.OnTimeSetListener() {

                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                                  int minute) {

                                               String date =  dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                               String time = hourOfDay + ":" + minute;

                                               String id = FirebaseDatabase.getInstance().getReference().child("appointments").push().getKey();
                                                Appointment appointment = new Appointment(id,time,date, FirebaseAuth.getInstance().getCurrentUser().getUid(),instructors.get(index).id,"New");
                                                FirebaseDatabase.getInstance().getReference().child("appointments").child(appointment.id).setValue(appointment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            Toast.makeText(context, "Appointment Booked Successfully", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }, mHour, mMinute, false);
                                timePickerDialog.show();

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return instructors.size();
    }

    class InstructorViewHolder extends RecyclerView.ViewHolder{
        ImageView img,bookAppointment;
        TextView lang,username,phone;
        public InstructorViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            lang = itemView.findViewById(R.id.lang);
            phone = itemView.findViewById(R.id.phone);
            img = itemView.findViewById(R.id.img);
            bookAppointment = itemView.findViewById(R.id.bookAppointment);
        }
    }
}
