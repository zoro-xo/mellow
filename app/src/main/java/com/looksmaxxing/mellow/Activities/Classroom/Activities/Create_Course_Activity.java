package com.looksmaxxing.mellow.Activities.Classroom.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.looksmaxxing.mellow.Activities.Classroom.Models.ClassroomModel;
import com.looksmaxxing.mellow.databinding.ActivityCreateCourseBinding;
import com.looksmaxxing.mellow.databinding.AddsetAlertdialogBinding;

public class Create_Course_Activity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private String selectedAccess;
    AlertDialog alertDialog;
    AddsetAlertdialogBinding addsetAlertdialogBinding;

    private ActivityCreateCourseBinding binding;
    private DatabaseReference databaseRef;
    private StorageReference storageRef;

    private ProgressDialog progressDialog;

    private String CourseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateCourseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading image...");
        progressDialog.setCancelable(false);

        String[] items = {"All Members Have Access", "Only Some Members Have Access", "Member of Certain Level"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(adapter);

        Intent intent = getIntent();
        boolean Edit = intent.getBooleanExtra("EDIT_MODE", false);
        boolean Create = intent.getBooleanExtra("CREATE_MODE", false);


        if (Edit) {
            binding.upload.setText("Edit Course");
            if (intent != null) {
                CourseId = intent.getStringExtra("CourseId");

                String title = intent.getStringExtra("title");
                String desc = intent.getStringExtra("desc");
                String image = intent.getStringExtra("image");
                String access = intent.getStringExtra("access");

                Glide.with(this)
                        .load(image)
                        .into(binding.courseImage);
                binding.courseToolBarName.setText(title);


                boolean published = intent.getBooleanExtra("published", true);
                binding.courseName.setText(title);
                binding.Coursedescription.setText(desc);
                Glide.with(this)
                        .load(image)
                        .into(binding.imageView);
                if (!published) {
                    binding.switchButton.setText("Draft");
                } else {
                    binding.switchButton.setText("Published");
                }
            }

        } else if (Create) {
            binding.upload.setText("Create Course");

        } else {

        }

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAccess = items[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });


        binding.uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        storageRef = FirebaseStorage.getInstance().getReference();
        databaseRef = FirebaseDatabase.getInstance().getReference("courses");

        binding.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Edit) {
                    updateCourse(CourseId);
                } else {
                    uploadCourse();

                }
            }
        });
    }

    private void uploadCourse() {
        String courseName = binding.courseName.getText().toString();
        String description = binding.Coursedescription.getText().toString();
        boolean isPublished = binding.switchButton.isChecked();

        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show();

        String courseId = databaseRef.push().getKey();
        String imagePath = "course_images/" + courseId + ".jpg";
        StorageReference imageRef = storageRef.child(imagePath);

        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();

                        ClassroomModel classroomModel = new ClassroomModel(courseName, description, imageUrl, courseId, selectedAccess,"", 0, isPublished);

                        databaseRef.child(courseId).setValue(classroomModel)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(Create_Course_Activity.this, "Course created successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(Create_Course_Activity.this, "Failed to create course: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();

                    Toast.makeText(Create_Course_Activity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                Glide.with(this).load(imageUri).into(binding.imageView);
                binding.uploadButton.setVisibility(View.GONE);
            }
        }


    }

    private void updateCourse(String courseId) {
        String courseName = binding.courseName.getText().toString();
        String description = binding.Coursedescription.getText().toString();
        boolean isPublished = binding.switchButton.isChecked();

        if (imageUri == null) {
            DatabaseReference courseRef = databaseRef.child(courseId);
            courseRef.child("title").setValue(courseName);
            courseRef.child("description").setValue(description);
            courseRef.child("published").setValue(isPublished)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Create_Course_Activity.this, "Course updated successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Create_Course_Activity.this, "Failed to update course: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            return;
        }

        progressDialog.show();

        String imagePath = "course_images/" + courseId + ".jpg";
        StorageReference imageRef = storageRef.child(imagePath);

        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();

                        DatabaseReference courseRef = databaseRef.child(courseId);
                        courseRef.child("title").setValue(courseName);
                        courseRef.child("description").setValue(description);
                        courseRef.child("published").setValue(isPublished);
                        courseRef.child("imageUrl").setValue(imageUrl)

                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        Toast.makeText(Create_Course_Activity.this, "Course updated successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(Create_Course_Activity.this, "Failed to update course: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(Create_Course_Activity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


}
