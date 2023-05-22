package com.example.pickspeak;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private LinearLayout choicesContainer;
    private EditText searchField;
    private List<String> array_s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        choicesContainer = findViewById(R.id.choicesContainer);
        searchField = findViewById(R.id.searchField);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            initialize();
        }
    }

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String FOLDER_NAME = "picspeak";

    private void createPicspeakFolder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                createFolder();
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        } else {
            createFolder();
        }
    }

    private void createFolder() {
        String rootPath;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())) {
            rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            rootPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
        }

        String folderPath = rootPath + File.separator + FOLDER_NAME;

        File folder = new File(folderPath);
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (created) {
                // Folder created successfully
            } else {
                // Failed to create folder
            }
        } else {
            // Folder already exists
        }
    }


    private void initialize() {
        array_s = new ArrayList<>();

        List<String> allFileNames = new ArrayList<>();

        // Read names of files in "pcs" folder (assets)
        try {
            String[] pcsFiles = getAssets().list("pcs");
            if (pcsFiles != null) {
                for (String fileName : pcsFiles) {
                    String imageUrl = "pcs/" + fileName; // Remove "assets/" prefix
                    allFileNames.add(imageUrl);
                    array_s.add(imageUrl);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Read names of files in "picspeak" folder (SD card)
        File picspeakFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/picspeak");
        if (picspeakFolder.exists() && picspeakFolder.isDirectory()) {
            String[] picspeakFiles = picspeakFolder.list();
            if (picspeakFiles != null) {
                for (String fileName : picspeakFiles) {
                    String fullPath = picspeakFolder.getAbsolutePath() + "/" + fileName;
                    allFileNames.add(fullPath);
                }
            }
        }

        // Write names from both folders into the scrollView and add to array_s
        for (String fileName : allFileNames) {
            TextView textView = new TextView(this);
            textView.setText(fileName);
            choicesContainer.addView(textView);

            array_s.add(fileName);
        }

        // Start MainActivity2 after 5 seconds
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                intent.putStringArrayListExtra("array_s", (ArrayList<String>) array_s);
                startActivity(intent);
                finish();
            }
        }, 5000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createPicspeakFolder();
                initialize();
            } else {
                // Handle permission denied
            }
        } else if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createFolder();
            } else {
                // Permission denied, handle it accordingly
            }
        }
    }
}
