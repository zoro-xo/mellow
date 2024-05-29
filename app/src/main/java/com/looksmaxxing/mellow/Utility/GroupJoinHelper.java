package com.looksmaxxing.mellow.Utility;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.looksmaxxing.mellow.Activities.Server.Server_Activity;
import com.looksmaxxing.mellow.ModelClasses.Class_Group;
import com.looksmaxxing.mellow.ModelClasses.Class_Student_Details;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GroupJoinHelper {

    public static void sentGroupJoinInvitation(Activity activity, String userID, String userName, String userEmail, String dateTimeCC, String adminGroupID, String adminUserName, String groupName, String groupPushId, String subGroupName, String classPushId, String JoinStatus) {

        Log.d("JOINTT", "adminGroupID: " + adminGroupID + "\nsubGroupName: " + subGroupName +
                "\nadminUserName: " + adminUserName + "\ngroupName: " + groupName + "\ngroupPushId: " + groupPushId);

        DatabaseReference userNoti = FirebaseDatabase.getInstance().getReference().child("Notification").child("User_Notifications").child(userID).child(groupPushId).child(classPushId);
        DatabaseReference refjoiningReq = FirebaseDatabase.getInstance().getReference().child("Notification").child("Received_Req").child(groupPushId).child("groupTeacherJoiningReqs");
        DatabaseReference refacceptingReq = FirebaseDatabase.getInstance().getReference().child("Notification").child("Submit_Req").child(userID);
        DatabaseReference grpJoiningReqs = FirebaseDatabase.getInstance().getReference().child("Groups").child("All_GRPs").child(groupPushId).child(classPushId).child("groupJoiningReqs");

        DatabaseReference userNotiCheck = FirebaseDatabase.getInstance().getReference().child("Notification").child("User_Notifications").child(userID).child(groupPushId).child(classPushId);
        GroupJoinHelper.acceptAndNotify(Server_Activity.defaultGrpPushId, userID, Server_Activity.defaultClassPshId, "", userName, Server_Activity.defaultGrpName, Server_Activity.defaultAdminGrpId, Server_Activity.defaultNotiPushId, Server_Activity.defaultClassPshId);
        userNotiCheck.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    if (snapshot.child("joiningStatus").exists()) {
                        String status = snapshot.child("joiningStatus").getValue(String.class);
                        if ("req_sent".equals(status)) {
                            Toast.makeText(activity, "Your joining request has been sent please wait...", Toast.LENGTH_SHORT).show();
                        }
                        if ("Approve".equals(status)) {
                            Toast.makeText(activity, "Your joining request has been approved", Toast.LENGTH_SHORT).show();
                        }
                        if ("Reject".equals(status) || "admission_req_sent".equals(status)) {

                            refjoiningReq.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    long noofQuesinCategory = snapshot.getChildrenCount() + 1;
                                    String pushLong = "Joining B Reqno_" + noofQuesinCategory;

                                    if ("TeacherJoin".equals(JoinStatus)) {
                                        Class_Group userAddComment = new Class_Group(dateTimeCC, userName, "req_sent", userID, adminGroupID, userEmail, pushLong, groupName, groupPushId, subGroupName, "Group_JoiningReq_Teacher", classPushId);
                                        refjoiningReq.child(pushLong).setValue(userAddComment);
                                        refacceptingReq.child(pushLong).setValue(userAddComment);
                                        userNoti.child("notificationPushId").setValue(pushLong);
                                        userNoti.child("joiningStatus").setValue("req_sent");
                                        showToast(activity, "Teacher");
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });

                            grpJoiningReqs.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    long noofQuesinCategory = snapshot.getChildrenCount() + 1;
                                    String pushLong = "Joining B Reqno_" + noofQuesinCategory;

                                    if ("StudentJoin".equals(JoinStatus)) {
                                        Class_Group userAddComment = new Class_Group(dateTimeCC, userName, "req_sent", userID, adminGroupID, userEmail, pushLong, groupName, groupPushId, subGroupName, "Group_JoiningReq", classPushId);
                                        grpJoiningReqs.child(pushLong).setValue(userAddComment);
                                        refacceptingReq.child(pushLong).setValue(userAddComment);
                                        userNoti.child("notificationPushId").setValue(pushLong);
                                        userNoti.child("joiningStatus").setValue("req_sent");
                                        showToast(activity, "Student");
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });

                        }
                    }

                } else {

                    refjoiningReq.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            long noofQuesinCategory = snapshot.getChildrenCount() + 1;
                            String pushLong = "Joining B Reqno_" + noofQuesinCategory;

                            if ("TeacherJoin".equals(JoinStatus)) {
                                Class_Group userAddComment = new Class_Group(dateTimeCC, userName, "req_sent", userID, adminGroupID, userEmail, pushLong, groupName, groupPushId, subGroupName, "Group_JoiningReq_Teacher", classPushId);
                                refjoiningReq.child(pushLong).setValue(userAddComment);
                                refacceptingReq.child(pushLong).setValue(userAddComment);
                                userNoti.child("notificationPushId").setValue(pushLong);
                                userNoti.child("joiningStatus").setValue("req_sent");
                                userNoti.child("notificationPushId").setValue(pushLong);
                                userNoti.child("joiningStatus").setValue("req_sent");
                                showToast(activity, "Teacher");
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    grpJoiningReqs.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            long noofQuesinCategory = snapshot.getChildrenCount() + 1;
                            String pushLong = "Joining B Reqno_" + noofQuesinCategory;

                            if ("StudentJoin".equals(JoinStatus)) {
                                Class_Group userAddComment = new Class_Group(dateTimeCC, userName, "req_sent", userID, adminGroupID, userEmail, pushLong, groupName, groupPushId, subGroupName, "Group_JoiningReq", classPushId);
                                grpJoiningReqs.child(pushLong).setValue(userAddComment);
                                refacceptingReq.child(pushLong).setValue(userAddComment);
                                userNoti.child("notificationPushId").setValue(pushLong);
                                userNoti.child("joiningStatus").setValue("req_sent");
                                showToast(activity, "Student");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void acceptAndNotify(String groupPushId, String reqUserID, String classUniPushId, String dateTimeCC, String userName, String groupName, String currUserId, String notPushId, String subGroupPushId) {

        DatabaseReference refAllGRPs = FirebaseDatabase.getInstance().getReference().child("Groups").child("All_GRPs").child(groupPushId);
        DatabaseReference saveUserGroupClassRef = FirebaseDatabase.getInstance().getReference().child("Groups")
                .child("All_User_Group_Class").child(groupPushId).child(reqUserID).child("classUniPushId");
        saveUserGroupClassRef.setValue(classUniPushId);

        DatabaseReference addedOrJoinedGroups = FirebaseDatabase.getInstance().getReference().child("Groups").child("UserAddedOrJoinedGrp").child(reqUserID).child(groupPushId);
        addedOrJoinedGroups.child("dateTime").setValue(dateTimeCC);
        addedOrJoinedGroups.child("addedOrJoined").setValue("StudentJoin");

        if (reqUserID != null && groupPushId != null && classUniPushId != null) {

            DatabaseReference userNoti = FirebaseDatabase.getInstance().getReference().child("Notification").child("User_Notifications").child(reqUserID).child(groupPushId).child(classUniPushId);
            userNoti.child("grpJoiningStatus").setValue("Approve");
            userNoti.child("joiningStatus").setValue("Approve");
        }


        DatabaseReference refSubs_J_Group = FirebaseDatabase.getInstance().getReference().child("Groups").child("All_Sub_Group").child(groupPushId).child(classUniPushId).child("SubGroup_SubsList");
        DatabaseReference refAll_J_Group = FirebaseDatabase.getInstance().getReference().child("Groups").child("All_Universal_Group").child(groupPushId).child("User_Subscribed_Groups");


        refAllGRPs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                refAllGRPs.child(classUniPushId).child("classStudentList").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Class_Student_Details class_student_details = new Class_Student_Details(false, reqUserID, userName, "unpaid", "unpaid");
                        refAllGRPs.child(classUniPushId).child("classStudentList").child(reqUserID).setValue(class_student_details);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        refSubs_J_Group.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long noofGroupinCategory = snapshot.getChildrenCount() + 1;
                String push = "joining_No" + noofGroupinCategory;

                Calendar calenderCC = Calendar.getInstance();
                SimpleDateFormat simpleDateFormatCC = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
                String dateTimeCC = simpleDateFormatCC.format(calenderCC.getTime());
                Class_Group userSubsGroupClass = new Class_Group(dateTimeCC, userName, reqUserID, currUserId, noofGroupinCategory, groupName, classUniPushId, "Class Member", "On");
                refSubs_J_Group.child(reqUserID).setValue(userSubsGroupClass);
                refAll_J_Group.child(reqUserID).setValue(userSubsGroupClass);

                DatabaseReference refAccUserNotify = FirebaseDatabase.getInstance().getReference().
                        child("Groups").child("All_GRPs").child(groupPushId).child(subGroupPushId).child("groupJoiningReqs").child(notPushId);
                DatabaseReference refAccAdminNotify = FirebaseDatabase.getInstance().getReference().child("Notification").child("Submit_Req").child(reqUserID).child(notPushId);

                refAccUserNotify.child("grpJoiningStatus").setValue("Approve");
                refAccAdminNotify.child("grpJoiningStatus").setValue("Approve");

//                bottomSheetDialog.dismiss();
//                            Toast.makeText(getActivity(), "Group request from " + userName + "has been Approved", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private static void showToast(Activity activity, String type) {
        if ("Teacher".equals(type)) {
            Toast.makeText(activity, "Teacher join request sent", Toast.LENGTH_SHORT).show();
        } else if ("Student".equals(type)) {
            Toast.makeText(activity, "Student join request sent", Toast.LENGTH_SHORT).show();
        }
    }
}

