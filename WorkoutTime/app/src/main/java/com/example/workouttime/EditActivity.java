package com.example.workouttime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class EditActivity extends AppCompatActivity {

    private EditText editTextName, editTextDuration, editTextCoolDown;
    private DatabaseManager databaseManager;
    private ImageButton deleteButton;
    private String ritualName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        editTextCoolDown = findViewById(R.id.editTextCoolDown);
        editTextDuration = findViewById(R.id.editTextDuration);
        editTextName = findViewById(R.id.editTextName);
        ritualName = getIntent().getStringExtra("Ritual");
        databaseManager = new DatabaseManager(this);
        deleteButton = findViewById(R.id.imageButtonDelete);
        Intent intent = getIntent();
        if(!intent.hasExtra("Name"))
            deleteButton.setVisibility(View.GONE);
        else {
            String name = intent.getStringExtra("Name");
            int duration = intent.getIntExtra("Duration", 1);
            int coolDown = intent.getIntExtra("CoolDown", 1);
            editTextName.setText(name);
            editTextDuration.setText(Integer.toString(duration));
            editTextCoolDown.setText(Integer.toString(coolDown));
        }
    }
    public void delete(View view) {
        Intent intent = getIntent();
        if(intent.hasExtra("Name")) {
            String name = intent.getStringExtra("Name");
            int duration = intent.getIntExtra("Duration", 1);
            int coolDown = intent.getIntExtra("CoolDown", 1);
            databaseManager.delete(new Workout(name, duration, coolDown), ritualName);
        }
        finish();
    }
    public void create(View view) {
        try {
            String name = editTextName.getText().toString();
            int coolDown = Integer.parseInt(editTextCoolDown.getText().toString());
            int duration = Integer.parseInt(editTextDuration.getText().toString());
            if(coolDown < 0 || duration <= 0 || name.isEmpty()) throw new Exception("Some Problem");
            if(getIntent().hasExtra("Name")) {
                Intent intent = getIntent();
                String oldName = intent.getStringExtra("Name");
                int oldDuration = intent.getIntExtra("Duration", 1);
                int oldCoolDown = intent.getIntExtra("CoolDown", 1);
                databaseManager.update(new Workout(name, duration, coolDown), new Workout(oldName, oldDuration, oldCoolDown), ritualName);
            }
            else if(!databaseManager.insert(new Workout(name, duration, coolDown), ritualName)) throw new Exception("Could not Insert");
        }
        catch(Exception e) {
            Toast.makeText(this, "Invalid Parameters", Toast.LENGTH_LONG).show();
            return;
        }
        finish();
    }
    public void upButtonEditAct(View view) {
        finish();
    }
}