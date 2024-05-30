package com.looksmaxxing.mellow.Activities.Classroom.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.looksmaxxing.mellow.R;
import com.looksmaxxing.mellow.databinding.ActivityEditModuleBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.richeditor.RichEditor;


public class Edit_Module_Activity extends AppCompatActivity {

    private String moduleId;
    private String moduleName;
    private String courseId;
    private String setId;
    private Spinner spinner;
    private EditText mEditor;
    private TextView mPreview;
    ActivityEditModuleBinding binding;
    String moduleContent;
    String videoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditModuleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("moduleId") && intent.hasExtra("moduleName")) {
            moduleId = intent.getStringExtra("moduleId");
            moduleName = intent.getStringExtra("moduleName");
            courseId = intent.getStringExtra("courseId");
            setId = intent.getStringExtra("setId");
            moduleContent = intent.getStringExtra("moduleContent");
            videoUrl = intent.getStringExtra("videoUrl");

            binding.vidUrl.setText(videoUrl);
            binding.editor.setText(moduleContent);

        }

        binding.editor.setScroller(new Scroller(getApplicationContext()));
        binding.editor.setHorizontallyScrolling(true);
        binding.editor.setMovementMethod(new ScrollingMovementMethod());

        spinner = findViewById(R.id.spinner);

        // Dummy data for the spinner
        List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("Item 1");
        spinnerArray.add("Item 2");
        spinnerArray.add("Item 3");

        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Setting adapter to spinner
        spinner.setAdapter(spinnerAdapter);
        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        binding.description.setText(moduleName);
        binding.Add.setOnClickListener(v -> {
            String newModuleName = binding.description.getText().toString();
            String newCourseDesc = binding.editor.getText().toString();
            String newVideoUrl = binding.vidUrl.getText().toString();

            if (moduleName.isEmpty()  ) {
                Toast.makeText(Edit_Module_Activity.this, "Please enter Module Name", Toast.LENGTH_SHORT).show();
            }else if(newVideoUrl.isEmpty()){
                Toast.makeText(Edit_Module_Activity.this, "Please enter Video URL", Toast.LENGTH_SHORT).show();

            }else if(!isValidUrl(newVideoUrl)){
                Toast.makeText(Edit_Module_Activity.this, "Please enter a valid Video URL", Toast.LENGTH_SHORT).show();

            }else if(newCourseDesc.isEmpty()){
                Toast.makeText(Edit_Module_Activity.this, "Please enter Module Descryption", Toast.LENGTH_SHORT).show();

            }

            else {
                // Proceed with saving logic
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference moduleRef = rootRef.child("courses").child(courseId);

                String setPath = "/sets/" + setId;
                String path = (moduleId != null) ? setPath + "/modules/" + moduleId : setPath;

                Map<String, Object> updates = new HashMap<>();
                if (moduleId != null) {
                    updates.put("moduleName", newModuleName);
                    updates.put("moduleContent", newCourseDesc); // Add course description
                    updates.put("videoUrl", newVideoUrl);
                } else
                    updates.put("setName", newModuleName);

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(path, updates);

                moduleRef.updateChildren(childUpdates)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(Edit_Module_Activity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(Edit_Module_Activity.this, "Something went wrong" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
                  });


    }


    private boolean isValidUrl(String url) {
        // Use Patterns class to check if the URL is valid
        return Patterns.WEB_URL.matcher(url).matches();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
