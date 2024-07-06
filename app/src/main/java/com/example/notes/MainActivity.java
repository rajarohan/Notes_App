package com.example.notes;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ListView notesListView;
    private Button addNoteButton;
    private ArrayAdapter<String> notesAdapter;
    private ArrayList<String> notesList;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "NotesApp";
    private int selectedNotePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notesListView = findViewById(R.id.notesListView);
        addNoteButton = findViewById(R.id.addNoteButton);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        notesList = new ArrayList<>();

        loadNotes();
        notesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notesList);
        notesListView.setAdapter(notesAdapter);
        registerForContextMenu(notesListView);

        addNoteButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
            startActivityForResult(intent, 1);
        });

        notesListView.setOnItemClickListener((parent, view, position, id) -> {
            String note = notesList.get(position);
            String[] parts = note.split(": ", 2);
            String title = parts[0];
            String content = parts.length > 1 ? parts[1] : "";

            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
            intent.putExtra("noteTitle", title);
            intent.putExtra("noteContent", content);
            intent.putExtra("notePosition", position);
            startActivityForResult(intent, 2);
        });

        notesListView.setOnItemLongClickListener((parent, view, position, id) -> {
            selectedNotePosition = position;
            return false;
        });
    }

    private void loadNotes() {
        // Load notes from Shared Preferences
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            notesList.add(entry.getValue().toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            String noteTitle = data.getStringExtra("noteTitle");
            String noteContent = data.getStringExtra("noteContent");
            int notePosition = data.getIntExtra("notePosition", -1);
            if (requestCode == 1) {
                // Add new note
                notesList.add(noteTitle + ": " + noteContent);
            } else if (requestCode == 2 && notePosition != -1) {
                // Edit existing note
                notesList.set(notePosition, noteTitle + ": " + noteContent);
            }
            notesAdapter.notifyDataSetChanged();
            saveNotes();
        }
    }

    private void saveNotes() {
        // Save notes to Shared Preferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        for (int i = 0; i < notesList.size(); i++) {
            editor.putString("note_" + i, notesList.get(i));
        }
        editor.apply();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.notesListView) {
            menu.add(0, v.getId(), 0, "Delete");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Delete") {
            notesList.remove(selectedNotePosition);
            notesAdapter.notifyDataSetChanged();
            saveNotes();
            return true;
        }
        return super.onContextItemSelected(item);
    }
}
