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

import com.looksmaxxing.mellow.Activities.Classroom.Models.AdminChecker;
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


    public ModuleAdapter(List<Module> moduleList, SetClickListner setClickListner, Context context, String setId, boolean isPrvsSetCompltd) {
        ModuleAdapter.moduleList = moduleList;
        this.context = context;
        ModuleAdapter.setClickListner = setClickListner;
        ModuleAdapter.setId = setId;
        this.isPrevsSetCompltd = isPrvsSetCompltd;
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

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(context, Module_Details_Activity.class);
//                intent.putExtra("id",module.getModuleId());
//                intent.putExtra("name",module.getModuleName());
//                context.startActivity(intent);
//            }
//        });
        if (!isPreviousVideoWatched) holder.moduleMenu.setEnabled(false);
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
        private final TextView moduleName;
        ImageView lockIcon, moduleMenu;

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
                            setClickListner.onSetClicked(setId, module.getModuleName(), module.getModuleId());
                        }
                    }
                }
            });
        }

        public void bind(Module module, boolean isPreviousVideoWatched, ModuleViewHolder holder) {
            moduleName.setText(module.getModuleName());
//            moduleContent.setText(module.getModuleContent());

            moduleName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (UserUtils.isAdmin()) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            Module module = moduleList.get(position);
                            if (setClickListner != null) {
                                setClickListner.onSetClicked(setId, module.getModuleName(), module.getModuleId());
                            }
                        }
                    } else {
                        if (isPreviousVideoWatched) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                Module module = moduleList.get(position);
                                if (setClickListner != null) {
                                    setClickListner.onSetClicked(setId, module.getModuleName(), module.getModuleId());
                                }
                            }
                        } else {
                            holder.itemView.setEnabled(false);
                            Toast.makeText(v.getContext(), "This video is locked", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

//            moduleContent.setText(module.getModuleContent());
//            AdminChecker.checkIfUserIsAdmin(isAdmin -> {
//                if (isAdmin) {
//                    moduleMenu.setVisibility(View.VISIBLE);
//                    // Perform additional actions for admin users
//                } else {
//                    moduleMenu.setVisibility(View.GONE);
//                }
//            });

            boolean isAdmin = UserUtils.isAdmin();
            if (isAdmin) {
                    moduleMenu.setVisibility(View.VISIBLE);
            } else {
                    moduleMenu.setVisibility(View.GONE);
            }

            lockIcon.setVisibility(isPreviousVideoWatched ? View.GONE : View.VISIBLE);
        }
    }
}
