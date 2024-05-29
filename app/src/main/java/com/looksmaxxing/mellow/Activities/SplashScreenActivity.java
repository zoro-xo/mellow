package com.looksmaxxing.mellow.Activities;

import static com.looksmaxxing.mellow.Activities.Profile.AccountSetting_Activity.getDefaults;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.looksmaxxing.mellow.Activities.Server.Server_Activity;
import com.looksmaxxing.mellow.ModelClasses.User;
import com.looksmaxxing.mellow.R;
import com.looksmaxxing.mellow.Utility.UserUtils;

//@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private String userID;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);



        checkDarkLightDefault();
        checkDarkLightDefaultStatusBar();
        configurations();
        getFirebaseInstance();
        if (userID != null) {
            checkIsPaidAndNavigate();
            checkAdminStatus();
        } else {
            navigateToGetStartedActivity();
        }
    }

    private void checkDarkLightDefault() {

        String darkLightDefaultVal = getDefaults("DefaultDarkLight", SplashScreenActivity.this);
        Log.d("USERIDSA", "onCreate: " + getDefaults("DefaultDarkLight", SplashScreenActivity.this));
        if (darkLightDefaultVal != null) {
            if (darkLightDefaultVal.equals("Dark")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            if (darkLightDefaultVal.equals("Light")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            if (darkLightDefaultVal.equals("Default")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    public void checkDarkLightDefaultStatusBar() {
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {

            case Configuration.UI_MODE_NIGHT_YES:
                getWindow().setStatusBarColor(Color.parseColor("#17181c"));
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                // edited here
                getWindow().setStatusBarColor(Color.parseColor("#ffffff"));

                break;


        }
    }

    private void configurations() {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
        }
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
    private void getFirebaseInstance() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            firebaseAuth = FirebaseAuth.getInstance();
            currentUser = firebaseAuth.getCurrentUser();
            assert currentUser != null;
            userID = currentUser.getUid();
        }
    }
    private void checkAdminStatus() {
        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference().child("AppAdmin");
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        Toast.makeText(this, ""+currentUserId, Toast.LENGTH_SHORT).show();
        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    if (userId != null && userId.equals(currentUserId)) {
                        Boolean isAdmin = userSnapshot.child("Admin").getValue(Boolean.class);
                        if (isAdmin != null && isAdmin) {
//                            Toast.makeText(SplashScreenActivity.this, "User is Admin", Toast.LENGTH_SHORT).show();

                            UserUtils.setAdmin(true);
                            // Save admin status to a static variable if needed
                            // Example: AdminUtils.setAdmin(true);
                            return; // Exit loop if admin found
                        }else {
//                            Toast.makeText(SplashScreenActivity.this, "User is No Admin", Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("SplashScreenActivity", "Error fetching admin status: " + databaseError.getMessage());
            }
        });
    }


    private void checkIsPaidAndNavigate() {

        DatabaseReference refUserName = FirebaseDatabase.getInstance().getReference().child("Users").child("Registration").child(userID);

        refUserName.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
                navigateToGetStartedActivity();
                return;
            }
            User user = task.getResult().getValue(User.class);
            if (user != null && user.getIsPaid()) {
                showToast();
                moveToServerActivity();
            } else {
                navigateToGetStartedActivity();
            }
        });
    }

    private void navigateToGetStartedActivity() {
        Intent intent = new Intent(SplashScreenActivity.this, GetStartedActivity.class);
        startActivity(intent);
        finish();
    }

    private void moveToServerActivity() {
        Intent intent = new Intent(SplashScreenActivity.this, Server_Activity.class);
        startActivity(intent);
        finish();
    }

    private void showToast() {
        Toast.makeText(this, "Welcome back", Toast.LENGTH_SHORT).show();
    }

    private void showPaymentSheet() {
        // Show payment sheet logic
    }


}
