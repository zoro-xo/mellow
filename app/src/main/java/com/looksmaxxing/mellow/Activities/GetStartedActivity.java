package com.looksmaxxing.mellow.Activities;

import static com.looksmaxxing.mellow.Activities.Profile.AccountSetting_Activity.getDefaults;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.looksmaxxing.mellow.Activities.Classroom.fragments.PaymentBottomSheet;
import com.looksmaxxing.mellow.Activities.Server.Server_Activity;
import com.looksmaxxing.mellow.ModelClasses.User;
import com.looksmaxxing.mellow.R;
import com.looksmaxxing.mellow.Utility.NetworkBroadcast;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GetStartedActivity extends AppCompatActivity {

    private BroadcastReceiver broadcastReceiver;


    GoogleSignInClient googleSignInClient;
    Button btn_SignIn;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    String userID;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ProgressBar progressBar;
    VideoView videoView;

    private void checkDarkLightDefault() {

        String darkLightDefaultVal = getDefaults("DefaultDarkLight", GetStartedActivity.this);
        Log.d("USERIDSA", "onCreate: " + getDefaults("DefaultDarkLight", GetStartedActivity.this));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        finish();
    }


    @Override
    protected void onPostResume() {
        videoView.resume();
        super.onPostResume();
    }

    @Override
    protected void onRestart() {
        videoView.start();
        super.onRestart();
    }

    @Override
    protected void onPause() {
        videoView.suspend();
        super.onPause();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);
//        setTheme(R.style.Theme_Cllasify);
        videoView = findViewById(R.id.video_view);
        loadVideo();
        checkDarkLightDefault();
        checkDarkLightDefaultStatusBar();
        broadcastReceiver = new NetworkBroadcast();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        progressBar = findViewById(R.id.SignInProgress);
        adMob();
        getFirebaseInstance();
        //Toast.makeText(this, "User Not ", Toast.LENGTH_SHORT).show();
        btn_SignIn = findViewById(R.id.btn_SignIn);
        btn_SignIn.setOnClickListener(view -> {
            // Configure Google Sign In
            signInGoogle();
        });

    }

    private void getFirebaseInstance() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            firebaseAuth = FirebaseAuth.getInstance();
            currentUser = firebaseAuth.getCurrentUser();
            assert currentUser != null;
            userID = currentUser.getUid();
        }
    }

    private void adMob() {
        //initialize() AdMob
        MobileAds.initialize(this, initializationStatus -> {
        });
    }


    private void loadVideo() {
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.cllasify_intro);
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.setOnPreparedListener(mediaPlayer -> mediaPlayer.setLooping(true));
    }

    private void signInGoogle() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("351525579518-d25phcu7mupuacvnq625fn8p9pk3firu.apps.googleusercontent.com")
                .requestEmail()
                .build();

        progressBar.setVisibility(View.VISIBLE);
        googleSignInClient = GoogleSignIn.getClient(GetStartedActivity.this, googleSignInOptions);
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, 100);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Toast.makeText(getStarted.this, "on activity result called", Toast.LENGTH_SHORT).show();
        if (requestCode == 100) {
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn
                    .getSignedInAccountFromIntent(data);
            if (signInAccountTask.isSuccessful()) {
                try {
                    GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);
                    if (googleSignInAccount != null) {
                        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(GetStartedActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    submitLoginData();
                                } else {
                                    Log.i("shitt22", task.getException().toString());
                                    Toast.makeText(GetStartedActivity.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(GetStartedActivity.this, "Google account null", Toast.LENGTH_SHORT).show();

                    }


                } catch (ApiException e) {
                    e.printStackTrace();
                    Log.i("shitt1", e.toString());
                }
            } else {
                Log.i("shitt2", signInAccountTask.getException().toString());
            }

        } else {
//            Toast.makeText(getStarted.this, "Wrong Request Code", Toast.LENGTH_SHORT).show();

        }
    }

    private void showPaymentSheet() {
        if (!isFinishing() && !isDestroyed()) {
            PaymentBottomSheet bottomSheetDialogFragment = new PaymentBottomSheet(userID);
            bottomSheetDialogFragment.setCancelable(false);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(bottomSheetDialogFragment, bottomSheetDialogFragment.getTag());
            transaction.commitAllowingStateLoss();
        }
    }

    private void submitLoginData() {
        Calendar calenderCC = Calendar.getInstance();
        SimpleDateFormat simpleDateFormatCC = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
        final String udateTimeCC = simpleDateFormatCC.format(calenderCC.getTime());

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("FCM", "Token retrieval failed", task.getException());
                        return;
                    }
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser == null) {
                        Log.e("Auth", "Current user is null");
                        return;
                    }
                    this.userID = currentUser.getUid();
                    String name = currentUser.getDisplayName();
                    String userEmailID = currentUser.getEmail();
                    Uri userPhoto = currentUser.getPhotoUrl();
                    String token = task.getResult();

                    DatabaseReference refUserRegister = FirebaseDatabase.getInstance().getReference()
                            .child("Users")
                            .child("Registration")
                            .child(userID);

                    refUserRegister.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                refUserRegister.child("Name").setValue(name);
                                refUserRegister.child("userEmailId").setValue(userEmailID);
                                refUserRegister.child("userId").setValue(userID);
                                refUserRegister.child("dateTime").setValue(udateTimeCC);
                                refUserRegister.child("userStatus").setValue("");
                                refUserRegister.child("token").setValue(token);
                                refUserRegister.child("isPaid").setValue(false);
                                if (userPhoto != null) {
                                    refUserRegister.child("profilePic").setValue(userPhoto.toString());
                                } else {
                                    refUserRegister.child("profilePic").setValue(""); // or handle accordingly
                                }
                            }
                            checkIsPaidAndNavigate();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Log the error for debugging purposes
                            Log.e("Database", "Error retrieving user data", error.toException());
                        }
                    });
                });

    }

    public void showToast() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_signin_successful, (ViewGroup) findViewById(R.id.toast));
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (userID != null)
            checkIsPaidAndNavigate();
    }

    private void moveToServerActivity() {
        Intent intent = new Intent(GetStartedActivity.this, Server_Activity.class);
        intent.putExtra("splash", "stop");
        intent.putExtra("Check", "appRestart");
        startActivity(intent);
        GetStartedActivity.this.overridePendingTransition(0, 0);
    }

    private void checkIsPaidAndNavigate() {
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
        DatabaseReference refUserName = FirebaseDatabase.getInstance().getReference().child("Users").child("Registration").child(userID);

        refUserName.get().addOnCompleteListener(task -> {
            if (progressBar != null)
                progressBar.setVisibility(View.GONE);
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
                return;
            }
            User user = task.getResult().getValue(User.class);
//            assert user != null;
            if (user != null && user.getIsPaid()) {
                showToast();
                moveToServerActivity();
            } else {
                showPaymentSheet();
            }
        });
    }

    @Override
    protected void onDestroy() {
        videoView.stopPlayback();
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

}