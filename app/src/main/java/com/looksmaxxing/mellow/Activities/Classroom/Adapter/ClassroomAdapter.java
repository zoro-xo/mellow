package com.looksmaxxing.mellow.Activities.Classroom.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.looksmaxxing.mellow.Activities.Classroom.Activities.Course_List;
import com.looksmaxxing.mellow.Activities.Classroom.Activities.Create_Course_Activity;
import com.looksmaxxing.mellow.Activities.Classroom.Models.ClassroomModel;
import com.looksmaxxing.mellow.R;
import com.looksmaxxing.mellow.Utility.UserUtils;
import com.looksmaxxing.mellow.databinding.DeleteAlertdialogBinding;
import com.looksmaxxing.mellow.databinding.ItemClassroomCourseBinding;

import java.util.List;

import eightbitlab.com.blurview.BlurView;

public class ClassroomAdapter extends RecyclerView.Adapter<ClassroomAdapter.Viewholder> {

    Context context;
    List<ClassroomModel> classroomModels;
    DeleteAlertdialogBinding dialogBinding;
    private AlertDialog alertDialog;


    public ClassroomAdapter(List<ClassroomModel> classroomModels) {
        this.classroomModels = classroomModels;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new Viewholder(ItemClassroomCourseBinding.inflate(((Activity) parent.getContext()).getLayoutInflater()));
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        ClassroomModel model = classroomModels.get(position);
        boolean isPrevsCourseCompleted = position == 0 || classroomModels.get(position - 1).getProgress() == 100;
        holder.binding.tvCourseTitle.setText(model.getTitle());
        holder.binding.tvCourseDesc.setText(model.getDescription());
        holder.binding.tvProgressText.setText(model.getProgress() + "");
        holder.binding.progressBar.setProgress(model.getProgress());
        Glide.with(context)
                .load(model.getImageUrl())
                .into(holder.binding.ivCourseImage);

        boolean isAdmin = UserUtils.isAdmin();
        if (isAdmin) {
            holder.binding.menu.setVisibility(View.VISIBLE);
        } else {
            holder.binding.menu.setVisibility(View.GONE);
        }

//        AdminChecker.checkIfUserIsAdmin(new AdminChecker.AdminCheckCallback() {
//            @Override
//            public void onAdminCheck(boolean isAdmin) {
//                if (isAdmin) {
//                    holder.binding.menu.setVisibility(View.VISIBLE);
//                    // Perform additional actions for admin users
//                } else {
//                    holder.binding.menu.setVisibility(View.GONE);
//                }
//            }
//        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPrevsCourseCompleted) {
                    Intent intent = new Intent(context, Course_List.class);
                    intent.putExtra("courseId", model.getCourseId());
                    intent.putExtra("progress", model.getProgress());
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "This course is Locked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.binding.courseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPrevsCourseCompleted) {
                    Intent intent = new Intent(context, Course_List.class);
                    intent.putExtra("courseId", model.getCourseId());
                    intent.putExtra("progress", model.getProgress());
//                Toast.makeText(context, ""+model.getCourseId(), Toast.LENGTH_SHORT).show();
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "This course is Locked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (!isPrevsCourseCompleted) {
            holder.binding.lockIcon.setImageResource(R.drawable.ic_lock);
        }
        holder.binding.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.getMenuInflater().inflate(R.menu.course_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.option1:
                                Intent intent = new Intent(context, Create_Course_Activity.class);
                                intent.putExtra("EDIT_MODE", true);
                                intent.putExtra("CREATE_MODE", false);
                                intent.putExtra("CourseId", model.getCourseId());
                                intent.putExtra("title", model.getTitle());
                                intent.putExtra("desc", model.getDescription());
                                intent.putExtra("image", model.getImageUrl());
                                intent.putExtra("access", model.getSelectedAccess());
                                intent.putExtra("published", model.isPublished());
                                context.startActivity(intent);
                                return true;
                            case R.id.option2:
                                showCustomDialog(model.getCourseId());
                                return true;
                            case R.id.option3:
                                deleteCourse(model.getCourseId());
                                return true;
//                            case R.id.option4:
//                                // Handle option 4 click
//                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popupMenu.show();
            }
        });

        loadImageIntoBlurView(model.getImageUrl(), holder.binding.blurView, 50f);


    }

    @Override
    public int getItemCount() {
        return classroomModels.size();
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

    private void loadImageIntoBlurView(String imageUrl, BlurView blurView, float blurRadius) {
        // Load the image using Glide
        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // Blur the loaded image
                        Bitmap blurredBitmap = blurBitmap(context, resource, 25f);

                        blurView.setBackground(new BitmapDrawable(context.getResources(), blurredBitmap));
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Handle when the image is cleared
                    }
                });
    }

    // Method to blur a bitmap
    private Bitmap blurBitmap(Context context, Bitmap bitmap, float blurRadius) {
        // Create a new Bitmap
        Bitmap blurredBitmap = Bitmap.createBitmap(bitmap);
        RenderScript renderScript = RenderScript.create(context);
        Allocation input = Allocation.createFromBitmap(renderScript, bitmap, Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SCRIPT);
        Allocation output = Allocation.createTyped(renderScript, input.getType());

        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        script.setInput(input);

        script.setRadius(blurRadius);

        script.forEach(output);

        // Copy the output to the blurred bitmap
        output.copyTo(blurredBitmap);

        // Clean up
        renderScript.destroy();

        return blurredBitmap;
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        ItemClassroomCourseBinding binding;

        public Viewholder(@NonNull ItemClassroomCourseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
