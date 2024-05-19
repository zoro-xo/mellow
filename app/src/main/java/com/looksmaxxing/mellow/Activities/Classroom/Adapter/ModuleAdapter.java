package com.looksmaxxing.mellow.Activities.Classroom.Adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.looksmaxxing.mellow.Activities.Classroom.Models.AdminChecker;

import java.util.List;

import com.looksmaxxing.mellow.Activities.Classroom.Models.Module;
import com.looksmaxxing.mellow.Activities.Classroom.SetClickListner;
import com.looksmaxxing.mellow.R;

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder> {
    public static List<Module> moduleList;
    private Context context;
    private static String setId;
    private static SetClickListner setClickListner;

    public ModuleAdapter(List<Module> moduleList, SetClickListner setClickListner, Context context, String setId) {
        this.moduleList = moduleList;
        this.context = context;
        this.setClickListner=setClickListner;
        this.setId=setId;
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
        holder.bind(module);

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(context, Module_Details_Activity.class);
//                intent.putExtra("id",module.getModuleId());
//                intent.putExtra("name",module.getModuleName());
//                context.startActivity(intent);
//            }
//        });

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
                                //edit
                                return true;
                            case R.id.option2:
                                //edit

                                return true;
                            case R.id.option3:
                                // Handle option 3 click
                                return true;
                            case R.id.option4:
                                // Handle option 4 click
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

    public static class ModuleViewHolder extends RecyclerView.ViewHolder {
        private TextView moduleName;

         ImageView lockIcon,moduleMenu;

        public ModuleViewHolder(@NonNull View itemView) {
            super(itemView);
            moduleName = itemView.findViewById(R.id.module_name);
            moduleMenu = itemView.findViewById(R.id.module_menu);
            lockIcon = itemView.findViewById(R.id.lock_icon);
            moduleName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Module module = moduleList.get(position);
                        if (setClickListner != null) {
                            setClickListner.onSetClicked(setId,module.getModuleName(),module.getModuleId());
                        }
                    }
                }
            });
        }

        public void bind(Module module) {
            moduleName.setText(module.getModuleName());
//            moduleContent.setText(module.getModuleContent());
            AdminChecker.checkIfUserIsAdmin(new AdminChecker.AdminCheckCallback() {
                @Override
                public void onAdminCheck(boolean isAdmin) {
                    if (isAdmin) {
                        moduleMenu.setVisibility(View.VISIBLE);
                        // Perform additional actions for admin users
                    } else {
                        moduleMenu.setVisibility(View.GONE);
                    }
                }
            });
            lockIcon.setVisibility(module.isLocked() ? View.VISIBLE : View.GONE);
        }
    }
}
