package com.example.smartdiettrack;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CaptureActivity extends AppCompatActivity {
    String TAG = "CAPTURE";
    ImageView imageView;
    Button btOpen;
    Button btConfirm;
    File imgFile;
    String photoName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        imageView = findViewById(R.id.image_view);
        btOpen = findViewById(R.id.bt_open);
        btConfirm = findViewById(R.id.bt_confirmImg);
        btConfirm.setVisibility(View.INVISIBLE);
        checkPermission();
        initCamera();
    }
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(CaptureActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CaptureActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    100);
        }
    }

    private void initCamera() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK) {
                            Bitmap takenImage = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                            imageView.setImageBitmap(takenImage);
                            btConfirm.setVisibility(View.VISIBLE);
                        }
                    }
                });
        btOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });
        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onConfirmImage();
            }
        });
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imgFile = getPhotoFile(photoName);
        Uri fileProvider = FileProvider.getUriForFile(this, "SmartDietTrack", imgFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        activityResultLauncher.launch(intent);
    }

    private File getPhotoFile(String photoName) {
        File photoDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            return File.createTempFile(photoName, ".jpg", photoDirectory);
        }
        catch(IOException ie){
            ie.printStackTrace();
            return null;
        }
    }

    public void onConfirmImage(){
        new MLAPI(this, imgFile.getAbsolutePath()).identifyFoodRequest();
    }
}