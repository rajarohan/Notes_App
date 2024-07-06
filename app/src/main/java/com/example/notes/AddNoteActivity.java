package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class AddNoteActivity extends AppCompatActivity {

    private EditText noteTitle, noteContent;
    private Button saveNoteButton;
    private int notePosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        noteTitle = findViewById(R.id.noteTitle);
        noteContent = findViewById(R.id.noteContent);
        saveNoteButton = findViewById(R.id.saveNoteButton);

        Intent intent = getIntent();
        String title = intent.getStringExtra("noteTitle");
        String content = intent.getStringExtra("noteContent");
        notePosition = intent.getIntExtra("notePosition", -1);

        if (title != null) {
            noteTitle.setText(title);
        }
        if (content != null) {
            noteContent.setText(content);
        }

        saveNoteButton.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("noteTitle", noteTitle.getText().toString());
            resultIntent.putExtra("noteContent", noteContent.getText().toString());
            resultIntent.putExtra("notePosition", notePosition);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
