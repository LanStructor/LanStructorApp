package com.lanstructor.android.general;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.lanstructor.android.R;

import java.io.IOException;
import java.util.Locale;

public class OCRActivity extends AppCompatActivity {
    TextView content;
    ImageView img;
    Bitmap myBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocractivity);
        getSupportActionBar().setTitle("OCR Result");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        content = findViewById(R.id.content);
        img = findViewById(R.id.img);

        Intent intent = getIntent();
        String path = intent.getStringExtra("path");

        Bitmap myBitmap = BitmapFactory.decodeFile(path);


        ExifInterface ei = null;
        try {
            ei = new ExifInterface(path);

            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap = null;
            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(myBitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(myBitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(myBitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = myBitmap;
            }

            img.setImageBitmap(rotatedBitmap);

            runTextRecognition(rotatedBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

        private void runTextRecognition(Bitmap myBitmap) {

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(myBitmap);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        detector.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText texts) {
                processTextRecognitionResult(texts);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void processTextRecognitionResult(FirebaseVisionText firebaseVisionText) {
        content.setText(null);
        if (firebaseVisionText.getTextBlocks().size() == 0) {
            content.setText("No Text Found");
            return;
        }
        for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
            content.append(block.getText());

        }
    }
    TextToSpeech textToSpeechSystem;
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }else if(item.getItemId() == R.id.copy){
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", content.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Text has been copied", Toast.LENGTH_SHORT).show();
        }else if(item.getItemId() == R.id.TTS){
            textToSpeechSystem = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status == TextToSpeech.SUCCESS) {
                        String textToSay = content.getText().toString();
                        textToSpeechSystem.setLanguage(Locale.ENGLISH);
                        textToSpeechSystem.speak(textToSay, TextToSpeech.QUEUE_ADD, null);
                    }
                }
            });
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ocr_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(textToSpeechSystem != null){
            if(textToSpeechSystem.isSpeaking()){
                textToSpeechSystem.stop();
            }
        }
    }
}