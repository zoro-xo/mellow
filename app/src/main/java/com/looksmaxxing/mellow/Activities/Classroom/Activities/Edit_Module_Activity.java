package com.looksmaxxing.mellow.Activities.Classroom.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.looksmaxxing.mellow.R;
import com.looksmaxxing.mellow.databinding.ActivityEditModuleBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.richeditor.RichEditor;


public class Edit_Module_Activity extends AppCompatActivity {

    private String moduleId;
    private String moduleName;
    private String courseId;
    private String setId;
    private Spinner spinner;
    private RichEditor mEditor;
    private TextView mPreview;
    ActivityEditModuleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditModuleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("moduleId") && intent.hasExtra("moduleName")) {
            moduleId = intent.getStringExtra("moduleId");
            moduleName = intent.getStringExtra("moduleName");
            courseId = intent.getStringExtra("courseId");
            setId = intent.getStringExtra("setId");
        }

        editTV();

        spinner = findViewById(R.id.spinner);

        // Dummy data for the spinner
        List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("Item 1");
        spinnerArray.add("Item 2");
        spinnerArray.add("Item 3");

        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Setting adapter to spinner
        spinner.setAdapter(spinnerAdapter);
        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Edit_Module_Activity.this, Module_Details_Activity.class));
            }
        });


        binding.description.setText(moduleName); // Set the module name to EditText
        /*
        binding.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate the custom layout
                View dialogView = LayoutInflater.from(Edit_Module_Activity.this).inflate(R.layout.addurl_alertdialog, null);

                // Find views in the custom layout
                EditText editText = dialogView.findViewById(R.id.addUrl);
                AppCompatButton addButton = dialogView.findViewById(R.id.Add);
                AppCompatButton cancelButton = dialogView.findViewById(R.id.cancel);

                // Create the AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(Edit_Module_Activity.this);
                builder.setView(dialogView);
                builder.setCancelable(false);

                // Create and show the AlertDialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                // Set onClickListeners for buttons
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        newVideoUrl = editText.getText().toString();
                        Toast.makeText(Edit_Module_Activity.this, "Url Added", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }

        });
*/
        binding.Add.setOnClickListener(v -> {
            String newModuleName = binding.description.getText().toString();
            String newCourseDesc = binding.convertedText.getText().toString();
            String newVideoUrl = binding.vidUrl.getText().toString();

            if (moduleName.isEmpty()  ) {
                Toast.makeText(Edit_Module_Activity.this, "Please enter Module Name", Toast.LENGTH_SHORT).show();
            }else if(newVideoUrl.isEmpty()){
                Toast.makeText(Edit_Module_Activity.this, "Please enter Video URL", Toast.LENGTH_SHORT).show();

            }else if(!isValidUrl(newVideoUrl)){
                Toast.makeText(Edit_Module_Activity.this, "Please enter a valid Video URL", Toast.LENGTH_SHORT).show();

            }else if(newCourseDesc.isEmpty()){
                Toast.makeText(Edit_Module_Activity.this, "Please enter Module Descryption", Toast.LENGTH_SHORT).show();

            }

            else {
                // Proceed with saving logic
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference moduleRef = rootRef.child("courses").child(courseId);

                String setPath = "/sets/" + setId;
                String path = (moduleId != null) ? setPath + "/modules/" + moduleId : setPath;

                Map<String, Object> updates = new HashMap<>();
                if (moduleId != null) {
                    updates.put("moduleName", newModuleName);
                    updates.put("moduleContent", newCourseDesc); // Add course description
                    updates.put("videoUrl", newVideoUrl);
                } else
                    updates.put("setName", newModuleName);

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(path, updates);

                moduleRef.updateChildren(childUpdates)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(Edit_Module_Activity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(Edit_Module_Activity.this, "Something went wrong" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
                  });


    }


    private boolean isValidUrl(String url) {
        // Use Patterns class to check if the URL is valid
        return Patterns.WEB_URL.matcher(url).matches();
    }
    public void editTV() {

        mEditor = (RichEditor) findViewById(R.id.editor);
        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(22);
        mEditor.setEditorFontColor(Color.RED);
        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        mEditor.setPadding(10, 10, 10, 10);
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        mEditor.setPlaceholder("Insert text here...");
        //mEditor.setInputEnabled(false);

        mPreview = (TextView) findViewById(R.id.converted_text);
        mEditor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                mPreview.setText(text);
            }
        });

        findViewById(R.id.action_undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.undo();
            }
        });

        findViewById(R.id.action_redo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.redo();
            }
        });

        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBold();
            }
        });

        findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setItalic();
            }
        });

        findViewById(R.id.action_subscript).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setSubscript();
            }
        });

        findViewById(R.id.action_superscript).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setSuperscript();
            }
        });

        findViewById(R.id.action_strikethrough).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setStrikeThrough();
            }
        });

        findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setUnderline();
            }
        });

        findViewById(R.id.action_heading1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(1);
            }
        });

        findViewById(R.id.action_heading2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(2);
            }
        });

        findViewById(R.id.action_heading3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(3);
            }
        });

        findViewById(R.id.action_heading4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(4);
            }
        });

        findViewById(R.id.action_heading5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(5);
            }
        });

        findViewById(R.id.action_heading6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setHeading(6);
            }
        });

        findViewById(R.id.action_txt_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override
            public void onClick(View v) {
                mEditor.setTextColor(isChanged ? Color.BLACK : Color.RED);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_bg_color).setOnClickListener(new View.OnClickListener() {
            private boolean isChanged;

            @Override
            public void onClick(View v) {
                mEditor.setTextBackgroundColor(isChanged ? Color.TRANSPARENT : Color.YELLOW);
                isChanged = !isChanged;
            }
        });

        findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setIndent();
            }
        });

        findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setOutdent();
            }
        });

        findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignLeft();
            }
        });

        findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignCenter();
            }
        });

        findViewById(R.id.action_align_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignRight();
            }
        });

        findViewById(R.id.action_blockquote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBlockquote();
            }
        });

        findViewById(R.id.action_insert_bullets).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBullets();
            }
        });

        findViewById(R.id.action_insert_numbers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setNumbers();
            }
        });

        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.insertImage("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg",
                        "dachshund", 320);
            }
        });

        findViewById(R.id.action_insert_youtube).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.insertYoutubeVideo("https://www.youtube.com/embed/pS5peqApgUA");
            }
        });

        findViewById(R.id.action_insert_audio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.insertAudio("https://file-examples-com.github.io/uploads/2017/11/file_example_MP3_5MG.mp3");
            }
        });

        findViewById(R.id.action_insert_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.insertVideo("https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/1080/Big_Buck_Bunny_1080_10s_10MB.mp4", 360);
            }
        });

        findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.insertLink("https://github.com/wasabeef", "wasabeef");
            }
        });
        findViewById(R.id.action_insert_checkbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.insertTodo();
            }
        });
    }

}
