package com.looksmaxxing.mellow.Activities.Classroom.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.looksmaxxing.mellow.Activities.Classroom.Activities.Classroom_Activity;
import com.looksmaxxing.mellow.Activities.Classroom.Activities.Edit_Course_Activity;
import com.looksmaxxing.mellow.Activities.Classroom.Models.Course;
import com.looksmaxxing.mellow.Activities.Classroom.Models.Module;
import com.looksmaxxing.mellow.Activities.Classroom.Models.Set;
import com.looksmaxxing.mellow.Activities.Classroom.SetClickListner;
import com.looksmaxxing.mellow.R;
import com.looksmaxxing.mellow.Utility.UserUtils;
import com.looksmaxxing.mellow.databinding.AddsetAlertdialogBinding;
import com.looksmaxxing.mellow.databinding.DeleteAlertdialogBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    static int progress;
    public Context context;
    AlertDialog alertDialog;
    AddsetAlertdialogBinding addsetAlertdialogBinding;
    DeleteAlertdialogBinding dialogBinding;
    SetClickListner setClickListener;
    private List<Course> courseList;
    private List<Course> filteredList; // Filtered list for search

    private   ActivityFinishListener activityFinishListener;

    private Course course;
    public interface ActivityFinishListener {
        void onBackButtonClicked();
    }
    public CourseAdapter(List<Course> courseList, Context context, SetClickListner setClickListener, int progress) {
        this.courseList = courseList;
        this.context = context;
        this.setClickListener = setClickListener;
        this.filteredList = new ArrayList<>(courseList); // Initialize filtered list with all data
        CourseAdapter.progress = progress;
    }
    public void setActivityFinishListener(ActivityFinishListener listener) {
        this.activityFinishListener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        course = courseList.get(position);
        holder.bind(course, setClickListener);



//        String setid=course.getSets().get(position).getSetId();

        holder.courseMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.getMenuInflater().inflate(R.menu.course_inside_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.option1:
                                Intent intent = new Intent(context, Edit_Course_Activity.class);
                                intent.putExtra("CourseId", course.getCourseId());
                                intent.putExtra("title", course.getCourseName());
                                intent.putExtra("desc", course.getCourseDescription());
                                intent.putExtra("image", course.getImageUrl());
                                context.startActivity(intent);
                                return true;

                            case R.id.option2:
                                showAddSet(course.getCourseId());
                                return true;
//                            case R.id.option3:
//
////                                if (setid!=null){
////                                    setClickListener.onSetItemClicked(setid,"");
////                                    addmoduletoset(setid,"module");
////                                }
//                                return true;
                            case R.id.option3:
                                showCustomDialog(course.getCourseId());
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public void filterSets(String query) {
        filteredList.clear();
        if (TextUtils.isEmpty(query)) {
            filteredList.addAll(courseList);
        } else {
            for (Course course : courseList) {
                List<Set> filteredSets = new ArrayList<>();
                for (Set set : course.getSets()) {
                    if (set.getSetName().toLowerCase().contains(query.toLowerCase())) {
                        filteredSets.add(set);
                    }
                }
                if (!filteredSets.isEmpty()) {
                    Course filteredCourse = new Course(course.getCourseId(), course.getCourseName(), course.getCourseDescription(), course.getImageUrl(), filteredSets);
                    filteredList.add(filteredCourse);
                }
            }
        }
        notifyDataSetChanged();
    }

    // Method to filter modules based on query
    public void filterModules(String query) {
        filteredList.clear();
        if (TextUtils.isEmpty(query)) {
            filteredList.addAll(courseList);
        } else {
            for (Course course : courseList) {
                List<Set> filteredSets = new ArrayList<>();
                for (Set set : course.getSets()) {
                    List<Module> filteredModules = new ArrayList<>();
                    for (Module module : set.getModules()) {
                        if (module.getModuleName().toLowerCase().contains(query.toLowerCase())) {
                            filteredModules.add(module);
                        }
                    }
                    if (!filteredModules.isEmpty()) {
                        Set filteredSet = new Set(set.getSetId(), set.getSetName(), filteredModules);
                        filteredSets.add(filteredSet);
                    }
                }
                if (!filteredSets.isEmpty()) {
                    Course filteredCourse = new Course(course.getCourseId(), course.getCourseName(), course.getCourseDescription(), course.getImageUrl(), filteredSets);
                    filteredList.add(filteredCourse);
                }
            }
        }
        notifyDataSetChanged();
    }

    // Method to update dataset with filtered list


    private void showAddSet(String courseId) {
        addsetAlertdialogBinding = AddsetAlertdialogBinding.inflate(LayoutInflater.from(context));
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(addsetAlertdialogBinding.getRoot());

        addsetAlertdialogBinding.Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String addText = addsetAlertdialogBinding.descryption.getText().toString();
                pushSetToFirebase(courseId, addText);
                Toast.makeText(context, "Set Added", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });

        addsetAlertdialogBinding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();

    }

    private void pushSetToFirebase(String courseId, String setName) {
        DatabaseReference setsRef = FirebaseDatabase.getInstance().getReference().child("courses").child(courseId).child("sets");

        String newSetKey = setsRef.push().getKey();
        Map<String, Object> setMap = new HashMap<>();
        setMap.put("setId", newSetKey);
        setMap.put("setName", setName);
        setsRef.child(newSetKey).setValue(setMap);
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
        notifyDataSetChanged();
    }

    private void addmoduletoset(String setId, String ModuleName) {


        DatabaseReference setsRef = FirebaseDatabase.getInstance().getReference()
                .child("courses").child(course.getCourseId()).child("sets").child(setId).child("modules");
        String newModuleKey = setsRef.push().getKey();
        Map<String, Object> moduleMap = new HashMap<>();
        moduleMap.put("moduleId", newModuleKey);
        moduleMap.put("moduleName", "ModuleName");
        setsRef.child(newModuleKey).setValue(moduleMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Module added successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to add module: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showCustomDialog(String courseId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        dialogBinding = DeleteAlertdialogBinding.inflate(LayoutInflater.from(context));
        builder.setView(dialogBinding.getRoot());

        dialogBinding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCourse(courseId);
                Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });

        dialogBinding.BtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteCourse(String courseId) {
        FirebaseDatabase.getInstance().getReference().child("courses").child(courseId).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Course deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to delete course: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void updateDataSet(List<Course> updatedSetList) {
        this.courseList = updatedSetList;
        notifyDataSetChanged(); // Notify RecyclerView of dataset changes
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        private final ProgressBar progressBar;
        private final TextView tvProgressBar;
//        private final TextView courseName;
//        private final TextView courseToolBarName;
        private final RecyclerView setsRecyclerView;
        ImageView courseMenu, courseImage,back;
        SearchView search;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
//            courseName = itemView.findViewById(R.id.course_name);
//            courseToolBarName = itemView.findViewById(R.id.courseToolBarName);
            courseMenu = itemView.findViewById(R.id.course_menu);
//            courseImage = itemView.findViewById(R.id.course_image);
            progressBar = itemView.findViewById(R.id.progressBar);
            tvProgressBar = itemView.findViewById(R.id.tvProgressText);
            back = itemView.findViewById(R.id.back);
//             search=itemView.findViewById(R.id.search);
            setsRecyclerView = itemView.findViewById(R.id.sets_recycler_view);
        }

        public void bind(Course course, SetClickListner setClickListener) {
//            courseName.setText(course.getCourseName());
//            courseToolBarName.setText(course.getCourseName());
            Context context = itemView.getContext();
            progressBar.setProgress(progress);
            tvProgressBar.setText(progress+"");

//            Glide.with(context).load(course.getImageUrl())
//                    .into(courseImage);
//              search.setOnClickListener(new View.OnClickListener() {
//                  @Override
//                  public void onClick(View view) {
//                      Toast.makeText(context, "click", Toast.LENGTH_SHORT).show();
//                  }
//              });

            boolean isAdmin = UserUtils.isAdmin();
            if (isAdmin) {
                courseMenu.setVisibility(View.VISIBLE);
            } else {
                courseMenu.setVisibility(View.GONE);
            }
//            AdminChecker.checkIfUserIsAdmin(new AdminChecker.AdminCheckCallback() {
//                @Override
//                public void onAdminCheck(boolean isAdmin) {
//                    if (isAdmin) {
//                        courseMenu.setVisibility(View.VISIBLE);
//                        // Perform additional actions for admin users
//                    } else {
//                        courseMenu.setVisibility(View.GONE);
//                    }
//                }
//            });
            SetAdapter setAdapter = new SetAdapter(course.getSets(), itemView.getContext(), setClickListener, course.getCourseId());
            setsRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            setsRecyclerView.setAdapter(setAdapter);
        }
    }

}
