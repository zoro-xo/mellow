package com.looksmaxxing.mellow.Activities.Classroom.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.looksmaxxing.mellow.Activities.Classroom.Models.AdminChecker;
import com.looksmaxxing.mellow.Activities.Classroom.Models.Module;
import com.looksmaxxing.mellow.Activities.Classroom.SetClickListner;
import com.looksmaxxing.mellow.R;
import com.looksmaxxing.mellow.Utility.UserUtils;

import java.util.List;

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder> {

    private List<Module> moduleList;
    private String setId;
    private SetClickListner setClickListener;
    private Context context;
    private boolean isPreviousSetCompleted;
    private String courseId;

    public ModuleAdapter(List<Module> moduleList, SetClickListner setClickListener, Context context, String setId, boolean isPreviousSetCompleted, String courseId) {
        this.moduleList = moduleList;
        this.context = context;
        this.setClickListener = setClickListener;
        this.setId = setId;
        this.isPreviousSetCompleted = isPreviousSetCompleted;
        this.courseId = courseId;
    }

    @NonNull
    @Override
    public ModuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_module, parent, false);
        return new ModuleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModuleViewHolder holder, int position) {
        Module module = moduleList.get(position);
        boolean isAdmin = UserUtils.isAdmin();

        holder.bind(module, position, isAdmin);

        // Set the moduleMenu click listener for admins
        if (isAdmin) {
            holder.moduleMenu.setVisibility(View.VISIBLE);
            holder.moduleMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(view, position);
                }
            });
        } else {
            holder.moduleMenu.setVisibility(View.GONE);
        }

        // Set the moduleName click listener based on user's admin status and module lock status
        holder.moduleName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (setClickListener != null && (isAdmin || isPreviousSetCompleted)) {
                    setClickListener.onSetClicked(setId, module.getModuleName(), module.getModuleId());
                } else {
                    Toast.makeText(context, "Module locked or admin access required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return moduleList.size();
    }

    private void showPopupMenu(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.module_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.option1:
                        deleteModule(courseId, setId, moduleList.get(position).getModuleId());
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }

    private void deleteModule(String courseId, String setId, String moduleIdToDelete) {
        DatabaseReference moduleRef = FirebaseDatabase.getInstance().getReference()
                .child("courses")
                .child(courseId)
                .child("sets")
                .child(setId)
                .child("modules")
                .child(moduleIdToDelete);

        moduleRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Module deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to delete module: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public class ModuleViewHolder extends RecyclerView.ViewHolder {
        private final TextView moduleName;
        private final ImageView lockIcon;
        private final ImageView moduleMenu;

        public ModuleViewHolder(@NonNull View itemView) {
            super(itemView);
            moduleName = itemView.findViewById(R.id.module_name);
            lockIcon = itemView.findViewById(R.id.lock_icon);
            moduleMenu = itemView.findViewById(R.id.module_menu);
        }

        public void bind(Module module, int position, boolean isAdmin) {
            moduleName.setText(module.getModuleName());

            // Show or hide lock icon based on previous module's completion status
            lockIcon.setVisibility(position == 0 || moduleList.get(position - 1).isWatched() ? View.GONE : View.VISIBLE);

            // Show or hide menu icon based on admin status
            moduleMenu.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        }
    }
}
