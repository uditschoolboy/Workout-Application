package com.example.workouttime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CreateRitual extends AppCompatActivity {
    private EditText editText;
    private DatabaseManager databaseManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ritual);
        editText = findViewById(R.id.editTextTextPersonName);
        databaseManager = new DatabaseManager(this);
    }
    public void createRitual(View view) {
        String ritualName = editText.getText().toString();
        boolean created = databaseManager.createRitual(ritualName);
        if(created)
            finish();
        else
            Toast.makeText(this, "Invalid Name", Toast.LENGTH_SHORT).show();
    }
    public void upButtonCreateRitual(View view) {
        finish();
    }
}