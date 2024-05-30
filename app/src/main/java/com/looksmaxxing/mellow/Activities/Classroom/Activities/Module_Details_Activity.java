package com.looksmaxxing.mellow.Activities.Classroom.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.looksmaxxing.mellow.R;
import com.looksmaxxing.mellow.Utility.UserUtils;
import com.looksmaxxing.mellow.databinding.ActivityModuleDetailsBinding;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Module_Details_Activity extends AppCompatActivity {
    ActivityModuleDetailsBinding binding;
    FirebaseDatabase database;
    String userID;
    String moduleContent;
    String videoUrl;
    private String moduleId;
    private String moduleName;
    private String courseId;
    private String setId;
    private boolean isModuleWatched;
    private View customView;
    private WebChromeClient.CustomViewCallback customViewCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModuleDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
//        if (intent != null && intent.hasExtra("id")) {
        moduleId = intent.getStringExtra("id");
        moduleName = intent.getStringExtra("name");
        courseId = intent.getStringExtra("courseId");
        setId = intent.getStringExtra("setId");
        isModuleWatched = intent.getBooleanExtra("isModuleWatched", false);
        setCompletionBtn();
        binding.title.setText(moduleName);
        database = FirebaseDatabase.getInstance();
        userID = FirebaseAuth.getInstance().getUid();
        fetchModuleDetails(moduleId);
//            Toast.makeText(this, "set:" + setId+"course:"+courseId , Toast.LENGTH_SHORT).show();

//        }


        if (UserUtils.isAdmin()) {
            binding.courseMenu.setVisibility(View.VISIBLE);
        } else {
            binding.courseMenu.setVisibility(View.GONE);

        }
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.incomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                binding.incomplete.setVisibility(View.GONE);
//                binding.complete.setVisibility(View.VISIBLE);
                String tag = (String) view.getTag();
                if (Objects.equals(tag, "incomplete")) {
                    updateCompletionBtn(R.drawable.green_check, "completed", true);
                } else {
                    updateCompletionBtn(R.drawable.btn_complete, "incomplete", false);
                }
            }
        });


        binding.courseToolBarName.setText(moduleName);

        binding.courseMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editIntent = new Intent(Module_Details_Activity.this, Edit_Module_Activity.class);
                editIntent.putExtra("moduleId", moduleId); // assuming moduleId is a variable holding the module ID
                editIntent.putExtra("moduleName", moduleName); // assuming moduleName is a variable holding the module name
                editIntent.putExtra("setId", setId); // assuming setId is a variable holding the module name
                editIntent.putExtra("courseId", courseId); // assuming courseId is a variable holding the module name
                editIntent.putExtra("videoUrl", videoUrl); // assuming courseId is a variable holding the module name
                editIntent.putExtra("moduleContent", moduleContent); // assuming courseId is a variable holding the module name
                startActivity(editIntent);
            }
        });

//        getLifecycle().addObserver((LifecycleObserver) binding.videoView);


//        if (videoId != null) {
//            System.out.println("Video ID: " + videoId);
//            Toast.makeText(this, "Video ID: " + videoId, Toast.LENGTH_SHORT).show();
//        } else {
//            System.out.println("Invalid video URL");
//            Toast.makeText(this, "Invalid video URL", Toast.LENGTH_SHORT).show();
//        }

    }

    private void setCompletionBtn() {
        int drawable = isModuleWatched ? R.drawable.green_check : R.drawable.btn_complete;
        String tag = isModuleWatched ? "completed" : "incomplete";
        updateCompletionBtn(drawable, tag, null);
    }

    private void updateCompletionBtn(int btn_complete, String incomplete, Boolean b) {
        binding.incomplete.setImageResource(btn_complete);
        binding.incomplete.setTag(incomplete);
        if (b != null)
            updateModuleCompletion(b);
    }

    private void updateModuleCompletion(boolean b) {

        DatabaseReference historyRef = database.getReference("History").child(userID);
        DatabaseReference courseRef = database.getReference("courses").child(courseId);
        // Update module completion status
        historyRef.child("course").child(courseId)
                .child("sets").child(setId)
                .child("modules").child(moduleId)
                .child("watched").setValue(b);
        // Update module completion status inside module
        courseRef.child("sets").child(setId)
                .child("modules").child(moduleId)
                .child("watched_users")
                .child(userID).child("watched").setValue(b);

//        Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show();
    }

    private String extractVideoIdFromUrl(String videoUrl) {
        String videoId = null;
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(videoUrl);
        if (matcher.find()) {
            videoId = matcher.group();
        }
        System.out.println(videoId);
        Toast.makeText(this, videoId, Toast.LENGTH_SHORT).show();
        return videoId;

    }


    private void fetchModuleDetails(String moduleId) {
        DatabaseReference moduleRef = FirebaseDatabase.getInstance().getReference()
                .child("courses")
                .child(courseId)
                .child("sets")
                .child(setId)
                .child("modules")
                .child(moduleId);

        moduleRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    moduleContent = dataSnapshot.child("moduleContent").getValue(String.class);
                    moduleName = dataSnapshot.child("moduleName").getValue(String.class);
                    videoUrl = dataSnapshot.child("videoUrl").getValue(String.class);

                    if (moduleContent != null && moduleName != null && videoUrl != null && !videoUrl.isEmpty()) {
                        displayTextWithClickableUrls(moduleContent);

                        WebView webView = binding.videoView;
                        WebSettings webSettings = webView.getSettings();
                        webSettings.setJavaScriptEnabled(true);

                        webView.setWebViewClient(new WebViewClient());
                        webView.setWebChromeClient(new WebChromeClient() {
                            @Override
                            public void onShowCustomView(View view, CustomViewCallback callback) {
                                // Enter full-screen mode
                                customView = view;
                                customViewCallback = callback;
                                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                                webView.setVisibility(View.GONE);
                                setContentView(view);
                                super.onShowCustomView(view, callback);
                            }

                            @Override
                            public void onHideCustomView() {
                                onBackPressed();
                            }
                        });

                        webView.loadUrl(videoUrl);

                    } else {
                        showAddModuleMessage();
                    }

                    Log.d("ModuleContent", "Module Content: " + moduleContent);
                    Log.d("ModuleName", "Module Name: " + moduleName);
                } else {
                    showAddModuleMessage();
                    Log.d("ModuleDetails", "Module with ID " + moduleId + " does not exist.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Error fetching module details: " + databaseError.getMessage());
            }
        });
    }

    private void showAddModuleMessage() {
        binding.descryption.setText("Add Module");
        // Optionally hide or show other UI elements as needed
    }

    private void displayTextWithClickableUrls(String textWithUrls) {
        if (textWithUrls != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.descryption.setText(Html.fromHtml(textWithUrls, Html.FROM_HTML_MODE_LEGACY));
            } else {
                binding.descryption.setText(HtmlCompat.fromHtml(textWithUrls, HtmlCompat.FROM_HTML_MODE_COMPACT));
            }
            binding.descryption.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}