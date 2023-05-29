package com.lanstructor.android.instructor;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.google.firebase.storage.UploadTask;

import com.lanstructor.android.general.OCRActivity;
import com.lanstructor.android.R;
import com.lanstructor.android.authentication.WelcomeActivity;
import com.lanstructor.android.model.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class InstructorProfileFragment extends Fragment {


    ImageView selectCertificate,inst_image;
    Uri selectCertificateUri,inst_imageUri;
    User user;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_instructor_profile, container, false);

        setHasOptionsMenu(true);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");

        inst_image = view.findViewById(R.id.inst_image);
        EditText username = view.findViewById(R.id.username);
        EditText phone = view.findViewById(R.id.phone);
        EditText mainLang = view.findViewById(R.id.mainLang);
        selectCertificate = view.findViewById(R.id.selectCertificate);
        EditText email = view.findViewById(R.id.email);
        Button update = view.findViewById(R.id.update);

        FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);

                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("users").child(user.id);

                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        try {
                        // Got the download URL for 'users/me/profile.png'
                        Glide.with(getActivity())
                                .load(uri)
                                .placeholder(R.drawable.ic_baseline_hide_image_24)
                                .into(selectCertificate);
                    }catch (Exception e){}
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {}
                });

                StorageReference storageRefProfileImage = FirebaseStorage.getInstance().getReference().child("users").child(user.id+"Profile");
                storageRefProfileImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Got the download URL for 'users/me/profile.png'
                        try {
                            Glide.with(getActivity())
                                    .load(uri)
                                    .placeholder(R.drawable.ic_baseline_hide_image_24)
                                    .into(inst_image);
                        }catch (Exception e){}
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {}
                });

                username.setText(user.username);
                phone.setText(user.phone);
                mainLang.setText(user.mainLang);
                email.setText(user.email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        inst_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,43);
            }
        });

        selectCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,34);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().toString().isEmpty()){
                    username.setError("Enter username");
                }else if(phone.getText().toString().isEmpty()){
                    phone.setError("Enter phone");
                } else{
                    if(selectCertificateUri != null){
                        progressDialog.show();

                        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                        selectCertificate.setDrawingCacheEnabled(true);
                        selectCertificate.buildDrawingCache();
                        Bitmap bitmap = ((BitmapDrawable) selectCertificate.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        UploadTask uploadTask = storageRef.putBytes(data);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                progressDialog.dismiss();
                                // If sign in fails, display a message to the user.
                                Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                            }
                        });

                    }
                    if(inst_imageUri != null){

                        progressDialog.show();
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"Profile");

                        inst_image.setDrawingCacheEnabled(true);
                        inst_image.buildDrawingCache();

                        Bitmap bitmap = ((BitmapDrawable) inst_image.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        UploadTask uploadTask = storageRef.putBytes(data);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                progressDialog.dismiss();
                                // If sign in fails, display a message to the user.
                                Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                            }
                        });
                    }
                    progressDialog.show();
                    user.username = username.getText().toString();
                    user.phone = phone.getText().toString();
                    FirebaseDatabase.getInstance().getReference().child("users").child(user.id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if(task.isSuccessful()){
                                Toast.makeText(getActivity(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                            } else{
                                Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.logout_menu_user,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.logout){
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setMessage("Are you sure?")
                    .setPositiveButton("Logout", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().clear().commit();
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(getActivity(), WelcomeActivity.class));
                            getActivity().finish();
                        }
                    }).setNegativeButton("Cancel", null);
            alert.show();
        }else if(item.getItemId() == R.id.ocr){
            //intent to pick image from gallery
            Intent intent = new Intent(Intent.ACTION_PICK);
            //set intent type to image
            intent.setType("image/*");
            startActivityForResult(intent, 2);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 34 && resultCode == RESULT_OK){
            selectCertificateUri = data.getData();
            selectCertificate.setImageURI(selectCertificateUri);
        } else if(requestCode == 43 && resultCode == RESULT_OK){
            inst_imageUri = data.getData();
            inst_image.setImageURI(inst_imageUri);
        }else if(requestCode == 2 && resultCode == RESULT_OK){
            // Access the raw data if needed.
            try {
            File savedPhoto = new File(getActivity().getExternalFilesDir(null), "photo.jpg");
            // Access the raw data if needed.
            InputStream iStream =   getActivity().getContentResolver().openInputStream(data.getData());

                byte[] inputData = getBytes(iStream);

                FileOutputStream outputStream = new FileOutputStream(savedPhoto.getPath());
                outputStream.write(inputData);
                outputStream.close();
                if (savedPhoto.exists()) {

                    Intent intent= new Intent(getActivity(), OCRActivity.class);
                    intent.putExtra("path", savedPhoto.getAbsolutePath());
                    startActivity(intent);

                }
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }




        }
    }
    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}