package com.looksmaxxing.mellow.Activities.Classroom.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.looksmaxxing.mellow.Activities.Classroom.Models.Module;
import com.looksmaxxing.mellow.Activities.Classroom.SetClickListner;
import com.looksmaxxing.mellow.R;
import com.looksmaxxing.mellow.Utility.UserUtils;

import java.util.List;

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder> {
    public static List<Module> moduleList;
    private static String setId;
    private static SetClickListner setClickListner;
    private final Context context;
    boolean isPrevsSetCompltd;

    String courseId;

    public ModuleAdapter(List<Module> moduleList, SetClickListner setClickListner, Context context, String setId, boolean isPrvsSetCompltd, String courseId) {
        ModuleAdapter.moduleList = moduleList;
        this.context = context;
        ModuleAdapter.setClickListner = setClickListner;
        ModuleAdapter.setId = setId;
        this.isPrevsSetCompltd = isPrvsSetCompltd;
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
        boolean isPreviousVideoWatched = position == 0 ? isPrevsSetCompltd : moduleList.get(position - 1).isWatched();
        holder.bind(module, isPreviousVideoWatched, holder);

        holder.moduleMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.getMenuInflater().inflate(R.menu.module_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.option1:
                                deleteModule(courseId, setId, module.getModuleId());
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
        return moduleList.size();
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


    public static class ModuleViewHolder extends RecyclerView.ViewHolder {
        private final TextView moduleName;
        ImageView lockIcon, moduleMenu;
        TextView unlockText;
        RelativeLayout lock_bg;

        public ModuleViewHolder(@NonNull View itemView) {
            super(itemView);
            moduleName = itemView.findViewById(R.id.module_name);
            moduleMenu = itemView.findViewById(R.id.module_menu);
            lockIcon = itemView.findViewById(R.id.lock_icon);
            unlockText = itemView.findViewById(R.id.unlockText);
            lock_bg = itemView.findViewById(R.id.overlay);
        }

        public void bind(Module module, boolean isPreviousVideoWatched, ModuleViewHolder holder) {
            moduleName.setText(module.getModuleName());

//            lock_bg.setBackground(null);

            /*
            lockIcon.setVisibility(isPreviousVideoWatched ? View.GONE : View.VISIBLE);
            unlockText.setVisibility(isPreviousVideoWatched ? View.GONE : View.VISIBLE);

            if (isPreviousVideoWatched) {
                lock_bg.setBackground(null);
            }

            if (module.isWatched()) {
                lockIcon.setVisibility(View.VISIBLE);
                lockIcon.setImageResource(R.drawable.green_check);

            }
            */
            boolean isAdmin = UserUtils.isAdmin();

            // Set moduleMenu visibility and enabled state based on user's admin status
            if (isAdmin) {
                holder.moduleMenu.setVisibility(View.VISIBLE);
                holder.moduleMenu.setEnabled(true);
            } else {
                holder.moduleMenu.setVisibility(View.GONE);
                holder.moduleMenu.setEnabled(isPreviousVideoWatched);
            }

            moduleName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Course locked removed
                    setClickListner.onSetClicked(setId, module.getModuleName(), module.getModuleId(), module.isWatched());

                    /*
                    if (isAdmin) {
                        // Admins can click on modules regardless of lock status
                        setClickListner.onSetClicked(setId, module.getModuleName(), module.getModuleId(), module.isWatched());
                    } else {
                        // For non-admins, check if module is locked
                        if (isPreviousVideoWatched) {
                            setClickListner.onSetClicked(setId, module.getModuleName(), module.getModuleId(), module.isWatched());
                        }
                    }
                    */
                }
            });

        }
    }
}