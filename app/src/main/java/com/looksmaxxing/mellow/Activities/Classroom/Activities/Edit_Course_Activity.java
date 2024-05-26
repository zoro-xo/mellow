package com.looksmaxxing.mellow.Activities.Classroom.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.looksmaxxing.mellow.databinding.ActivityEditCourseBinding;

public class Edit_Course_Activity extends AppCompatActivity {

    ActivityEditCourseBinding binding;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    private DatabaseReference databaseRef;
    private StorageReference storageRef;

    private ProgressDialog progressDialog;

    private String courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditCourseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize progressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading image...");
        progressDialog.setCancelable(false);

        // Initialize Firebase references
        databaseRef = FirebaseDatabase.getInstance().getReference().child("courses"); // Adjust path as per your Firebase database structure
        storageRef = FirebaseStorage.getInstance().getReference().child("course_images"); // Adjust path as per your Firebase Storage structure

        // Retrieve intent extras
        Intent intent = getIntent();
        courseId = intent.getStringExtra("CourseId");
        String title = intent.getStringExtra("title");
        String desc = intent.getStringExtra("desc");
        String image = intent.getStringExtra("image");

        // Populate UI with existing data
        binding.courseToolBarName.setText(title);
        binding.courseName.setText(title);
        binding.Coursedescription.setText(desc);
        Glide.with(this)
                .load(image)
                .into(binding.imageView);

        // Button click to update course
        binding.upload.setOnClickListener(v -> updateCourse(courseId));
    }

    private void updateCourse(String courseId) {
        String courseName = binding.courseName.getText().toString().trim();
        String description = binding.Coursedescription.getText().toString().trim();
        boolean isPublished = binding.switchButton.isChecked();

        // Validate input
        if (courseName.isEmpty()) {
            binding.courseName.setError("Course name is required");
            return;
        }

        if (description.isEmpty()) {
            binding.Coursedescription.setError("Course description is required");
            return;
        }

        // Check if databaseRef is initialized
        if (databaseRef == null) {
            Toast.makeText(this, "Database reference is null", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update course data
        DatabaseReference courseRef = databaseRef.child(courseId);
        courseRef.child("title").setValue(courseName);
        courseRef.child("description").setValue(description);
        courseRef.child("published").setValue(isPublished)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Edit_Course_Activity.this, "Course updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Edit_Course_Activity.this, "Failed to update course: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Check if imageUri is available and upload image
        if (imageUri != null) {
            progressDialog.show();

            String imagePath = "course_images/" + courseId + ".jpg";
            StorageReference imageRef = storageRef.child(imagePath);

            UploadTask uploadTask = imageRef.putFile(imageUri);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();

                            courseRef.child("imageUrl").setValue(imageUrl)
                                    .addOnSuccessListener(aVoid -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(Edit_Course_Activity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(Edit_Course_Activity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(Edit_Course_Activity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
