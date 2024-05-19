package com.looksmaxxing.mellow.Activities.Classroom.Models;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminChecker {
    private static final String ADMIN_NODE = "Check_Group_Admins";

    public interface AdminCheckCallback {
        void onAdminCheck(boolean isAdmin);
    }

    public static void checkIfUserIsAdmin(AdminCheckCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference(ADMIN_NODE);

            groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean isAdmin = false;
                    for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot childSnapshot : groupSnapshot.getChildren()) {
                            for (DataSnapshot docSnapshot : childSnapshot.getChildren()) {
                                if (docSnapshot.getKey().equals("userId") &&
                                        docSnapshot.getValue().toString().equals(currentUserId)) {
                                    isAdmin = true;
                                    break;
                                }
                            }
                        }
                    }

                    callback.onAdminCheck(isAdmin);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            });
        } else {
            // User is not signed in
            callback.onAdminCheck(false);
        }
    }
}
