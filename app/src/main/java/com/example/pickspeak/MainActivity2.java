package com.example.pickspeak;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    private LinearLayout choicesContainer;
    private LinearLayout greenContainer;
    private EditText searchField;
    private List<String> array_s;

    private static final String FILE_NAME = "choices.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        choicesContainer = findViewById(R.id.choicesContainer);
        greenContainer = findViewById(R.id.greenContainer);
        searchField = findViewById(R.id.searchField);


        // Retrieve the array_s from MainActivity
        array_s = getIntent().getStringArrayListExtra("array_s");

        // Display the slideshow using image paths from choice.txt
        displaySlideshow(readChoiceImagePaths());

        // Display the names from array_s in the HorizontalScrollView
        displayNames(array_s);

        // Listen for text changes in the search field
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Filter the names based on the search string
                filterNames(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not used
            }
        });

        // Create choices.txt file if it does not exist
        createFileIfNotExists();

        // Read choices.txt and populate greenContainer with the file names
        readChoicesFile();

        // Set click listeners for choicesContainer and greenContainer
        setContainerClickListeners();
    }


    private void createFileIfNotExists() {
        try {
            if (!getFileStreamPath(FILE_NAME).exists()) {
                FileOutputStream fos = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
                outputStreamWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void displaySlideshow(List<String> imagePaths) {
        HorizontalScrollView imageSlider = findViewById(R.id.imageSlider);
        LinearLayout linearLayout = findViewById(R.id.linearLayout); // Replace with the correct ID of your LinearLayout
        if (linearLayout != null) {
            linearLayout.removeAllViews();

            // Read the image paths from choice.txt
            List<String> choiceImagePaths = readChoiceImagePaths();

            // Use the image paths from choice.txt instead of imagePaths
            for (String imagePath : choiceImagePaths) {
                ImageView imageView = new ImageView(this);
                try {
                    InputStream is = getAssets().open(imagePath);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                linearLayout.addView(imageView);
            }
        }
    }


    private List<String> readChoiceImagePaths() {
        List<String> imagePaths = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(getFileStreamPath(FILE_NAME)));
            String line;
            while ((line = reader.readLine()) != null) {
                imagePaths.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imagePaths;
    }








    private void readChoicesFile() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(getFileStreamPath(FILE_NAME)));
            String line;
            while ((line = reader.readLine()) != null) {
                addFileNameToGreenContainer(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addFileNameToGreenContainer(String fileName) {
        TextView textView = new TextView(this);
        textView.setText(fileName);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFromChoicesFile(fileName);
                removeFromGreenContainer(view);
            }
        });
        greenContainer.addView(textView);
    }

    private void setContainerClickListeners() {
        int choicesChildCount = choicesContainer.getChildCount();
        for (int i = 0; i < choicesChildCount; i++) {
            View childView = choicesContainer.getChildAt(i);
            childView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView textView = (TextView) view;
                    String fileName = textView.getText().toString();
                    addToChoicesFile(fileName);
                    addToGreenContainer(fileName);
                }
            });
        }

        int greenChildCount = greenContainer.getChildCount();
        for (int i = 0; i < greenChildCount; i++) {
            View childView = greenContainer.getChildAt(i);
            childView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView textView = (TextView) view;
                    String fileName = textView.getText().toString();
                    removeFromChoicesFile(fileName);
                    removeFromGreenContainer(view);
                }
            });
        }
    }

    private void addToChoicesFile(String fileName) {
        try {
            FileOutputStream fos = openFileOutput(FILE_NAME, Context.MODE_APPEND);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            outputStreamWriter.write(fileName + "\n");
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeFromChoicesFile(String fileName) {
        try {
            List<String> lines = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(getFileStreamPath(FILE_NAME)));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.equals(fileName)) {
                    lines.add(line);
                }
            }
            reader.close();

            FileOutputStream fos = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            for (String updatedLine : lines) {
                outputStreamWriter.write(updatedLine + "\n");
            }
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addToGreenContainer(String fileName) {
        TextView textView = new TextView(this);
        textView.setText(fileName);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeFromChoicesFile(fileName);
                removeFromGreenContainer(view);
            }
        });
        greenContainer.addView(textView);
    }

    private void removeFromGreenContainer(View view) {
        greenContainer.removeView(view);
    }

    private void displayNames(List<String> names) {
        choicesContainer.removeAllViews();

        for (String name : names) {
            TextView textView = new TextView(this);
            textView.setText(name);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView clickedTextView = (TextView) view;
                    String fileName = clickedTextView.getText().toString();
                    addToChoicesFile(fileName);
                    addToGreenContainer(fileName);
                }
            });
            choicesContainer.addView(textView);
        }
    }

    private void filterNames(String searchString) {
        List<String> filteredNames = new ArrayList<>();

        for (String name : array_s) {
            if (name.contains(searchString)) {
                filteredNames.add(name);
            }
        }

        displayNames(filteredNames);
    }
}