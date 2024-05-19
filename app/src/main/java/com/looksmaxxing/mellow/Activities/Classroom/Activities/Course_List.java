package com.looksmaxxing.mellow.Activities.Classroom.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.looksmaxxing.mellow.Activities.Classroom.Adapter.CourseAdapter;
import com.looksmaxxing.mellow.Activities.Classroom.Adapter.ModuleAdapter;
import com.looksmaxxing.mellow.Activities.Classroom.Models.Course;
import com.looksmaxxing.mellow.Activities.Classroom.Models.Module;
import com.looksmaxxing.mellow.Activities.Classroom.Models.Set;
import com.looksmaxxing.mellow.Activities.Classroom.SetClickListner;
import com.looksmaxxing.mellow.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Course_List extends AppCompatActivity implements SetClickListner {
    private RecyclerView courseRecyclerView;
    private CourseAdapter courseAdapter;
    private String courseId;
    public List<Course> courseList;
    ModuleAdapter moduleAdapter;
    String clickedSetId;
    String setID;
    List<Module> moduleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);


        courseRecyclerView = findViewById(R.id.course_recycler_view);
        courseList = new ArrayList<>();

        Intent intent = getIntent();
        if (intent.hasExtra("courseId")) {
            courseId = intent.getStringExtra("courseId");
            Toast.makeText(this, "" + courseId, Toast.LENGTH_SHORT).show();
        } else {
            // Handle case where courseId is missing
            Toast.makeText(this, "Course ID is missing", Toast.LENGTH_SHORT).show();
            return; // Exit the method
        }

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
                long progress = dataSnapshot.child("progress").getValue(Long.class);
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
                        modules.add(new Module(moduleId, moduleName, false));
                    }

                    sets.add(new Set(setId, setName, modules));
                }

                // Add the retrieved data to courseList
                courseList.add(new Course(courseId, title, description,imageUrl, sets));

                // Create a new CourseAdapter with the updated courseList
                courseAdapter = new CourseAdapter(courseList, Course_List.this, Course_List.this);

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
        public void onSetClicked (String setId, String name, String moduleId){
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
        public void onSetItemClicked (String setId, String itemId){
            // Handle set item click
        }

        private void updateCourseData (List < Course > updatedCourseList) {
            courseAdapter.updateDataSet(updatedCourseList);
        }
    }




//       courseRecyclerView = findViewById(R.id.course_recycler_view);
//               courseList = new ArrayList<>();
//
//
//
//        Intent intent = getIntent();
//        if (intent.hasExtra("courseId")) {
//        courseId = intent.getStringExtra("courseId");
//        Toast.makeText(this, "" + courseId, Toast.LENGTH_SHORT).show();
//        }
//
//        if (intent.hasExtra("setID")) {
//        setID = intent.getStringExtra("setID");
//
//        }
//
////        Intent sendIntent = new Intent(this, Module_Details_Activity.class);
////        sendIntent.putExtra("courseId", courseId);
////        sendIntent.putExtra("setId", setID);
////        startActivity(sendIntent);
//
//
//        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference coursesRef = rootRef.child("courses");
//
//        coursesRef.addValueEventListener(new ValueEventListener() {
//@Override
//public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//        List<Course> courseList = new ArrayList<>();
//
//        for (DataSnapshot courseSnapshot : dataSnapshot.getChildren()) {
//        String courseId = courseSnapshot.getKey();
//        String description = courseSnapshot.child("description").getValue(String.class);
//        String title = courseSnapshot.child("title").getValue(String.class);
//        long progress = courseSnapshot.child("progress").getValue(Long.class);
//        boolean published = courseSnapshot.child("published").getValue(Boolean.class);
//        String selectedAccess = courseSnapshot.child("selectedAccess").getValue(String.class);
//
//        List<Set> sets = new ArrayList<>();
//        for (DataSnapshot setSnapshot : courseSnapshot.child("sets").getChildren()) {
//        String setId = setSnapshot.getKey();
//        String setName = setSnapshot.child("setName").getValue(String.class);
//
//        List<Module> modules = new ArrayList<>();
//        for (DataSnapshot moduleSnapshot : setSnapshot.child("modules").getChildren()) {
//        String moduleId = moduleSnapshot.getKey();
//        String moduleName = moduleSnapshot.child("moduleName").getValue(String.class);
//        modules.add(new Module(moduleId, moduleName, false));
//        }
//
//        sets.add(new Set(setId, setName, modules));
//
//
//        }
//
//        courseList.add(new Course(courseId, title, description, sets));
//        }
//        courseAdapter = new CourseAdapter(courseList, Course_List.this, Course_List.this);
//
//        // Set up the RecyclerView
//        courseRecyclerView.setLayoutManager(new LinearLayoutManager(Course_List.this));
//        courseRecyclerView.setAdapter(courseAdapter);
//        updateCourseData(courseList);
//        courseAdapter.notifyDataSetChanged();
//        }
//
//@Override
//public void onCancelled(@NonNull DatabaseError databaseError) {
//        Toast.makeText(Course_List.this, "flai", Toast.LENGTH_SHORT).show();
//        }
//        });
//        }
//
//@Override
//public void onSetClicked(String setId, String name, String moduleId) {
//        clickedSetId = setId;
//        Intent sendIntent = new Intent(this, Module_Details_Activity.class);
//        sendIntent.putExtra("courseId", courseId);
//        sendIntent.putExtra("setId", clickedSetId);
//        sendIntent.putExtra("name", name);
//        if (moduleId!=null)
//        sendIntent.putExtra("id", moduleId);
//        startActivity(sendIntent);
//        Toast.makeText(this, "" + setId, Toast.LENGTH_SHORT).show();
//        }
//
//@Override
//public void onSetItemClicked(String setId, String itemId) {
//
//        }
//
//private void updateCourseData(List<Course> updatedCourseList) {
//        courseAdapter.updateDataSet(updatedCourseList);
//        }