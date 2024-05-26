package com.looksmaxxing.mellow.Activities.Classroom.Adapter;

import android.app.AlertDialog;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.looksmaxxing.mellow.Activities.Classroom.Models.Set;
import com.looksmaxxing.mellow.Activities.Classroom.SetClickListner;
import com.looksmaxxing.mellow.R;
import com.looksmaxxing.mellow.databinding.AddsetAlertdialogBinding;
import com.looksmaxxing.mellow.databinding.DeleteAlertdialogBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetAdapter extends RecyclerView.Adapter<SetAdapter.SetViewHolder> {
    private final List<Set> setList;
    private final Context context;
    private final SetClickListner setClickListner;
    public String courseid;
    AlertDialog alertDialog;
    DeleteAlertdialogBinding deleteAlertdialogBinding;

    public SetAdapter(List<Set> setList, Context context, SetClickListner setClickListner, String courseid) {
        this.setList = setList;
        this.context = context;
        this.setClickListner = setClickListner;
        this.courseid = courseid;
    }

    @NonNull
    @Override
    public SetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_set, parent, false);
        return new SetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SetViewHolder holder, int position) {
        Set set = setList.get(position);
        int index = setList.get(position == 0 ? 0 : position - 1).getModules().size() - 1;
        boolean isPrvsSetCompltd = position == 0 || setList.get(position == 0 ? 0 : position - 1).getModules().get(index).isWatched();
        holder.bind(set, isPrvsSetCompltd);
        if (!isPrvsSetCompltd) holder.moduleMenu.setVisibility(View.GONE);
        holder.moduleMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.getMenuInflater().inflate(R.menu.set_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (setClickListner != null) {
                            switch (menuItem.getItemId()) {
                                case R.id.option1:
                                    EditSet(set.getSetId(), set.getSetName());
                                    break;
                                case R.id.option2:
                                    showCustomDialog(set.getSetId(), courseid);
                                    break;
                                case R.id.option3:
                                    addmoduletoset(set.getSetId(), "modular");
                                    break;
                                // Handle other options if needed
                            }
                        }
                        return false;
                    }
                });

                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return setList.size();
    }

    private void addmoduletoset(String setId, String ModuleName) {
        DatabaseReference setsRef = FirebaseDatabase.getInstance().getReference()
                .child("courses").child(courseid).child("sets").child(setId).child("modules");
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

    private void EditSet(String setId, String currentSetText) {
        AddsetAlertdialogBinding addsetAlertdialogBinding = AddsetAlertdialogBinding.inflate(LayoutInflater.from(context));
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(addsetAlertdialogBinding.getRoot());

        // Set the current set text in the EditText
        addsetAlertdialogBinding.descryption.setText(currentSetText);
        AlertDialog alertDialog = builder.create();

        addsetAlertdialogBinding.Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String updatedText = addsetAlertdialogBinding.descryption.getText().toString();
                updateSetText(setId, updatedText);
                alertDialog.dismiss();
            }
        });

        addsetAlertdialogBinding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void updateSetText(String setId, String updatedText) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String courseKey = courseid;
        String setKey = setId;
        DatabaseReference setRef = databaseReference.child("courses").child(courseKey).child("sets").child(setKey);
        setRef.child("setName").setValue(updatedText)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Set Updated Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showCustomDialog(String setId, String courseId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        deleteAlertdialogBinding = DeleteAlertdialogBinding.inflate(LayoutInflater.from(context));
        builder.setView(deleteAlertdialogBinding.getRoot());

        deleteAlertdialogBinding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSet(setId, courseId);
                Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });

        deleteAlertdialogBinding.BtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog = builder.create(); // Assign the created AlertDialog to the class-level variable
        alertDialog.show();
    }

    private void deleteSet(String setId, String courseId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference setRef = databaseReference.child("courses").child(courseId).child("sets").child(setId);

        setRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Set deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to delete set: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public class SetViewHolder extends RecyclerView.ViewHolder {
        private final TextView setName;
        private final RecyclerView modulesRecyclerView;
        ImageView arrowIcon, moduleMenu;
        private boolean isExpanded;

        public SetViewHolder(@NonNull View itemView) {
            super(itemView);
            setName = itemView.findViewById(R.id.set_name);
            modulesRecyclerView = itemView.findViewById(R.id.modules_recycler_view);
            arrowIcon = itemView.findViewById(R.id.arrow_Icon);
            moduleMenu = itemView.findViewById(R.id.menu_set);
            isExpanded = false;

            setName.setOnClickListener(v -> {
                isExpanded = !isExpanded;
                updateArrowIcon();
                notifyDataSetChanged();
            });

            arrowIcon.setOnClickListener(v -> {
                isExpanded = !isExpanded;
                updateArrowIcon();
                notifyDataSetChanged();
            });
        }

        public void bind(Set set, boolean isPrvsSetCompltd) {
            setName.setText(set.getSetName());
            if (isExpanded) {
                modulesRecyclerView.setVisibility(View.VISIBLE);
                ModuleAdapter moduleAdapter = new ModuleAdapter(set.getModules(), setClickListner, itemView.getContext(), set.getSetId(), isPrvsSetCompltd);
                modulesRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
                modulesRecyclerView.setAdapter(moduleAdapter);
            } else {
                modulesRecyclerView.setVisibility(View.GONE);
            }

            updateArrowIcon();
        }

        private void updateArrowIcon() {
            if (isExpanded) {
                arrowIcon.setRotation(0);
            } else {
                arrowIcon.setRotation(180);
            }
        }
    }
}
