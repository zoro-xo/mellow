package com.looksmaxxing.mellow.Activities.Classroom.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.looksmaxxing.mellow.Activities.Classroom.Adapter.CourseAdapter;
import com.looksmaxxing.mellow.Activities.Classroom.Adapter.ModuleAdapter;
import com.looksmaxxing.mellow.Activities.Classroom.Models.Course;
import com.looksmaxxing.mellow.Activities.Classroom.Models.Module;
import com.looksmaxxing.mellow.Activities.Classroom.Models.Set;
import com.looksmaxxing.mellow.Activities.Classroom.SetClickListner;
import com.looksmaxxing.mellow.R;
import com.looksmaxxing.mellow.databinding.ActivityCourseListBinding;

import java.util.ArrayList;
import java.util.List;

public class Course_List extends AppCompatActivity implements SetClickListner, CourseAdapter.ActivityFinishListener {
    private RecyclerView courseRecyclerView;

    ActivityCourseListBinding binding;
    private CourseAdapter courseAdapter;
    private String courseId;
    public List<Course> courseList;
    ModuleAdapter moduleAdapter;
    String clickedSetId;
    private String courseName;
    private String courseImage;
    int progress;
    String userID;
    List<Module> moduleList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCourseListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        courseRecyclerView = findViewById(R.id.course_recycler_view);
        courseList = new ArrayList<>();
        userID = FirebaseAuth.getInstance().getUid();
        Intent intent = getIntent();


        moduleAdapter = new ModuleAdapter(moduleList, this, this, clickedSetId, false, courseId);

//        binding.search.setVisibility(View.GONE);
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

//        binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                // Handle search when query is submitted (e.g., filter adapter data)
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                // Handle search text change (e.g., filter adapter data)
//                filterSetsAndModules(newText);
//                return true;
//            }
//        });
        if (intent.hasExtra("courseId")) {
            courseId = intent.getStringExtra("courseId");
            progress = intent.getIntExtra("progress", 0);


        } else {
            Toast.makeText(this, "Course ID is missing", Toast.LENGTH_SHORT).show();
            return; // Exit the method
        }
        courseName = intent.getStringExtra("courseName");
        courseImage = intent.getStringExtra("courseImage");


//        Glide.with(this).load(courseImage).into(binding.courseImage);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference courseRef = rootRef.child("courses").child(courseId);

// Add a ValueEventListener to fetch data for the specific course
        courseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the existing courseList
                courseList.clear();

                // Retrieve course data
                String description = dataSnapshot.child("description").getValue(String.class);
                String title = dataSnapshot.child("title").getValue(String.class);
                binding.courseToolBarName.setText(title);
//                long progress = dataSnapshot.child("progress").getValue(Long.class);
                String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);

                boolean published = dataSnapshot.child("published").getValue(Boolean.class);
                String selectedAccess = dataSnapshot.child("selectedAccess").getValue(String.class);

                // Retrieve sets data
                List<Set> sets = new ArrayList<>();
                for (DataSnapshot setSnapshot : dataSnapshot.child("sets").getChildren()) {
                    String setId = setSnapshot.getKey();
                    String setName = setSnapshot.child("setName").getValue(String.class);

                    // Retrieve modules data
                    List<Module> modules = new ArrayList<>();
                    for (DataSnapshot moduleSnapshot : setSnapshot.child("modules").getChildren()) {
                        String moduleId = moduleSnapshot.getKey();
                        String moduleName = moduleSnapshot.child("moduleName").getValue(String.class);
                        boolean isWatched = Boolean.TRUE.equals(moduleSnapshot.child("watched_users")
                                .child(userID).child("watched").getValue(Boolean.class));
                        modules.add(new Module(moduleId, moduleName, false, isWatched));
                    }

                    sets.add(new Set(setId, setName, modules));
                }

                // Add the retrieved data to courseList
                courseList.add(new Course(courseId, title, description, imageUrl, sets));

                // Create a new CourseAdapter with the updated courseList
                courseAdapter = new CourseAdapter(courseList, Course_List.this, Course_List.this, progress);

                courseRecyclerView.setLayoutManager(new LinearLayoutManager(Course_List.this));
                courseRecyclerView.setAdapter(courseAdapter);

                // Notify the adapter of data change
                courseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle cancellation error
                Toast.makeText(Course_List.this, "Failed to retrieve course data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSetClicked(String setId, String name, String moduleId) {
        clickedSetId = setId;
        Intent sendIntent = new Intent(this, Module_Details_Activity.class);
        sendIntent.putExtra("courseId", courseId);
        sendIntent.putExtra("setId", clickedSetId);
        sendIntent.putExtra("name", name);
        if (moduleId != null)
            sendIntent.putExtra("id", moduleId);
        startActivity(sendIntent);
    }

    @Override
    public void onSetItemClicked(String setId, String itemId) {
        // Handle set item click
    }

    private void updateCourseData(List<Course> updatedCourseList) {
        courseAdapter.updateDataSet(updatedCourseList);
    }


    @Override
    public void onBackButtonClicked() {
        Toast.makeText(Course_List.this, "Back button clicked in Adapter!", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(Course_List.this, Classroom_Activity.class));
        finish();
    }

    private void filterSetsAndModules(String query) {
        if (courseAdapter != null) {
            // Depending on your filtering logic in CourseAdapter, call the appropriate filter method
            courseAdapter.filterSets(query); // Example: filtering sets
            // Or
            courseAdapter.filterModules(query); // Example: filtering modules
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}


