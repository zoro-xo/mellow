package com.looksmaxxing.mellow.Activities.Classroom.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.looksmaxxing.mellow.Activities.Classroom.Adapter.ClassroomAdapter;
import com.looksmaxxing.mellow.Activities.Classroom.Models.ClassroomModel;
import com.looksmaxxing.mellow.Activities.Notification_Activity;
import com.looksmaxxing.mellow.Activities.Profile.Profile_Activity;
import com.looksmaxxing.mellow.Activities.RightPanel.Discover_Activity;
import com.looksmaxxing.mellow.Activities.Server.Server_Activity;
import com.looksmaxxing.mellow.R;
import com.looksmaxxing.mellow.databinding.ActivityClassroomBinding;

import java.util.ArrayList;
import java.util.List;

public class Classroom_Activity extends AppCompatActivity {
    private final List<ClassroomModel> courseList = new ArrayList<>();
    ActivityClassroomBinding binding;
    ClassroomAdapter adapter;
    String userID;
    private DatabaseReference databaseRef;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClassroomBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        userID = FirebaseAuth.getInstance().getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference("courses");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

//        AdminChecker.checkIfUserIsAdmin(new AdminChecker.AdminCheckCallback() {
//            @Override
//            public void onAdminCheck(boolean isAdmin) {
//                if (isAdmin) {
//                    binding.fabAddCourse.setVisibility(View.VISIBLE);
//                    // Perform additional actions for admin users
//                } else {
//                    binding.fabAddCourse.setVisibility(View.GONE);
//                }
//            }
//        });

        binding.fabAddCourse.setOnClickListener(view -> {
            Intent intent=new Intent(Classroom_Activity.this, Create_Course_Activity.class);
            intent.putExtra("CREATE_MODE",true);
            intent.putExtra("EDIT_MODE", false);
            startActivity(intent);

        });

        fetchCourseData();
        progressDialog.show();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_class);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;

                switch (item.getItemId()) {
                    case R.id.bottom_nav_home:
//                        fragment = new HomeFragment();
//                        tag = "home";
                        Intent intent = new Intent(Classroom_Activity.this, Server_Activity.class);
                        intent.putExtra("panelState", "close");
                        startActivity(intent);

                        break;
                    case R.id.bottom_nav_discover:
//                          fragment = new FeedFragment();
//                        fragment = new JoinGroupFragment();
//                        tag = "joingroup";
                        startActivity(new Intent(Classroom_Activity.this, Discover_Activity.class));
                        Classroom_Activity.this.overridePendingTransition(0, 0);

                        break;
                    case R.id.bottom_nav_class:
//                          fragment = new FeedFragment();
//                        fragment = new JoinGroupFragment();
//                        tag = "joingroup";
//                        startActivity(new Intent(Classroom_Activity.this, Discover_Activity.class));
                        Classroom_Activity.this.overridePendingTransition(0, 0);

                        break;
                    case R.id.bottom_nav_notification:
//                        fragment = new User_Notification_Frag();
//                        tag = "notify";
                        startActivity(new Intent(Classroom_Activity.this, Notification_Activity.class));
                        Classroom_Activity.this.overridePendingTransition(0, 0);

                        break;
                    case R.id.bottom_nav_profile:
//                        fragment = new ProfileFragment();
//                        tag = "profile";
                        startActivity(new Intent(Classroom_Activity.this, Profile_Activity.class));
                        Classroom_Activity.this.overridePendingTransition(0, 0);

                        break;
//                    case R.id.action_Share:
//                        Toast.makeText(landing_Page.this, "Refer and Earn", Toast.LENGTH_LONG).show();
//                        generateLink();
//                        break;
                }
//                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.fragment_container,fragment,tag);
//                transaction.addToBackStack(tag);
//                transaction.commit();


                return true;
            }
        });


}




    private void fetchCourseData() {
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("History");

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                courseList.clear();
                // Show progress dialog before fetching data

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    int totalSets = 0;
                    int setsCompleted = 0;
                    String courseId = dataSnapshot.getKey();
                    String description = dataSnapshot.child("description").getValue(String.class);
                    String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                    int progress = dataSnapshot.child("progress").getValue(Integer.class);
                    boolean published = dataSnapshot.child("published").getValue(Boolean.class);
                    String selectedAccess = dataSnapshot.child("selectedAccess").getValue(String.class);
                    String title = dataSnapshot.child("title").getValue(String.class);

                    ClassroomModel classroomModel = new ClassroomModel(title, description, imageUrl, courseId, selectedAccess, "", progress, published);
                    int courseProgress = calculateProgress(dataSnapshot, totalSets, setsCompleted);
                    classroomModel.setProgress(courseProgress);
                    courseList.add(classroomModel);
                }

                adapter = new ClassroomAdapter(courseList);

                binding.rvClassroomCourse.setAdapter(adapter);

                adapter.notifyDataSetChanged();
                progressDialog.dismiss(); // Dismiss progress dialog when data fetching is complete

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss(); // Dismiss progress dialog if data fetching is cancelled or failed

                Toast.makeText(Classroom_Activity.this, "Failed to fetch data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int calculateProgress(DataSnapshot dataSnapshot, int totalSets, int setsCompleted) {
        for (DataSnapshot setSnapshot : dataSnapshot.child("sets").getChildren()) {
            int totalModulesInset = 0;
            totalSets++;
            int completedModules = 0;
            for (DataSnapshot moduleSnapshot : setSnapshot.child("modules").getChildren()) {
                totalModulesInset++;
                Boolean watched = moduleSnapshot.child("watched_users")
                        .child(userID).child("watched").getValue(Boolean.class);
                if (watched != null && watched) {
                    completedModules++;
                }
            }
            if (totalModulesInset > 0 && totalModulesInset == completedModules) {
                setsCompleted++;
            }
        }
        return (totalSets > 0) ? (setsCompleted * 100 / totalSets) : 0;
    }

}