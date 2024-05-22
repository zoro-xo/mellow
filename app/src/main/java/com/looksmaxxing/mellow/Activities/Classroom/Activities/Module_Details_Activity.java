package com.looksmaxxing.mellow.Activities.Classroom.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.looksmaxxing.mellow.databinding.ActivityModuleDetailsBinding;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Module_Details_Activity extends AppCompatActivity {
    ActivityModuleDetailsBinding binding;

    private String moduleId;
    private String moduleName;
    private String courseId;
    private String setId;

    private WebView webView;

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
        binding.title.setText(moduleName);

        fetchModuleDetails(moduleId);
//            Toast.makeText(this, "set:" + setId+"course:"+courseId , Toast.LENGTH_SHORT).show();

//        }


        binding.courseMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editIntent = new Intent(Module_Details_Activity.this, Edit_Module_Activity.class);
                editIntent.putExtra("moduleId", moduleId); // assuming moduleId is a variable holding the module ID
                editIntent.putExtra("moduleName", moduleName); // assuming moduleName is a variable holding the module name
                editIntent.putExtra("setId", setId); // assuming setId is a variable holding the module name
                editIntent.putExtra("courseId", courseId); // assuming courseId is a variable holding the module name
                startActivity(editIntent);
            }
        });

        getLifecycle().addObserver(binding.videoView);



//        if (videoId != null) {
//            System.out.println("Video ID: " + videoId);
//            Toast.makeText(this, "Video ID: " + videoId, Toast.LENGTH_SHORT).show();
//        } else {
//            System.out.println("Invalid video URL");
//            Toast.makeText(this, "Invalid video URL", Toast.LENGTH_SHORT).show();
//        }

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
        Toast.makeText(this, ""+videoId, Toast.LENGTH_SHORT).show();
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
                    String moduleContent = dataSnapshot.child("moduleContent").getValue(String.class);
                    String moduleName = dataSnapshot.child("moduleName").getValue(String.class);
                    String videoUrl = dataSnapshot.child("videoUrl").getValue(String.class);

                    if (moduleContent != null && moduleName != null && videoUrl != null) {
                        binding.descryption.setText(moduleContent);
                        binding.videoView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                            @Override
                            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                                String videoId = extractVideoIdFromUrl(videoUrl);
                                youTubePlayer.loadVideo(videoId, 0);
                            }


                            public void onVideoDuration(@NonNull YouTubePlayer youTubePlayer, float duration) {
                                // Now we have the duration of the video
                                int videoDuration = (int) duration;

                                Toast.makeText(Module_Details_Activity.this, ""+duration, Toast.LENGTH_SHORT).show();
                                youTubePlayer.addListener(new YouTubePlayerListener() {
                                    @Override
                                    public void onVideoLoadedFraction(@NonNull YouTubePlayer youTubePlayer, float v) {

                                    }

                                    @Override
                                    public void onVideoId(@NonNull YouTubePlayer youTubePlayer, @NonNull String s) {

                                    }

                                    @Override
                                    public void onVideoDuration(@NonNull YouTubePlayer youTubePlayer, float v) {

                                    }

                                    @Override
                                    public void onStateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerState playerState) {

                                    }

                                    @Override
                                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {

                                    }

                                    @Override
                                    public void onPlaybackRateChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlaybackRate playbackRate) {

                                    }

                                    @Override
                                    public void onPlaybackQualityChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlaybackQuality playbackQuality) {

                                    }

                                    @Override
                                    public void onApiChange(@NonNull YouTubePlayer youTubePlayer) {

                                    }

                                    @Override
                                    public void onCurrentSecond(@NonNull YouTubePlayer youTubePlayer, float second) {
                                        int currentSecond = (int) second;
//                                        updateProgressBar(currentSecond, videoDuration);
                                    }

                                    @Override
                                    public void onError(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerError error) {
                                        Toast.makeText(Module_Details_Activity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }

                                    // Other overridden methods can be left empty
                                });
                            }
                        });
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

}