package com.looksmaxxing.mellow.Activities.Classroom.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.looksmaxxing.mellow.R;
import com.looksmaxxing.mellow.databinding.ActivityEditModuleBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Edit_Module_Activity extends AppCompatActivity {

    private String moduleId;
    private String moduleName;
    private String courseId;
    private String setId;
    String newVideoUrl;
    private Spinner spinner;

    ActivityEditModuleBinding binding;

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
        }


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
                startActivity(new Intent(Edit_Module_Activity.this,Module_Details_Activity.class));
            }
        });



        binding.description.setText(moduleName); // Set the module name to EditText
        binding.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate the custom layout
                View dialogView = LayoutInflater.from(Edit_Module_Activity.this).inflate(R.layout.addurl_alertdialog, null);

                // Find views in the custom layout
                EditText editText = dialogView.findViewById(R.id.addUrl);
                AppCompatButton addButton = dialogView.findViewById(R.id.Add);
                AppCompatButton cancelButton = dialogView.findViewById(R.id.cancel);

                // Create the AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(Edit_Module_Activity.this);
                builder.setView(dialogView);
                builder.setCancelable(false);

                // Create and show the AlertDialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                // Set onClickListeners for buttons
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        newVideoUrl = editText.getText().toString();
                        Toast.makeText(Edit_Module_Activity.this, "Url Added", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }

        });

        binding.Add.setOnClickListener(v -> {
            String newModuleName = binding.description.getText().toString();
            String newCourseDesc = binding.CourseDescription.getText().toString();
            if (newModuleName.isEmpty()) {
                Toast.makeText(this, "Module name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
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
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Edit_Module_Activity.this, "Something went wrong" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });


    }
}

