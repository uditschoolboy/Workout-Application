package com.example.workouttime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class WorkoutList extends AppCompatActivity {
    private ArrayList<Workout> workoutList;
    private ListView listView;
    private DatabaseManager databaseManager;
    private TextView editTextRitualName;
    private WorkoutAdapter workoutAdapter;
    private String ritualName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_list);
        //ritual customization
        editTextRitualName = findViewById(R.id.editTextRitualName);
        ritualName = getIntent().getStringExtra("Ritual");
        editTextRitualName.setText(ritualName);
        //ritual customization
        listView = findViewById(R.id.listView);
        databaseManager = new DatabaseManager(this);
        workoutList = databaseManager.readFromDatabase(ritualName);
        workoutAdapter = new WorkoutAdapter(this, workoutList);
        listView.setAdapter(workoutAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Workout workout = workoutList.get(i);
                onListItemClick(workout);
            }
        });



    }
    public void onListItemClick(Workout workout) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("Name", workout.getName());
        intent.putExtra("Duration", workout.getDuration());
        intent.putExtra("CoolDown", workout.getCoolDown());
        intent.putExtra("Ritual", ritualName);
        startActivity(intent);
    }
    private class WorkoutAdapter extends ArrayAdapter<Workout> {

        public WorkoutAdapter(@NonNull Context context, @NonNull ArrayList<Workout> workoutList) {
            super(context, 0, workoutList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.workout_list_item, parent, false);
            }
            TextView textViewName = convertView.findViewById(R.id.textViewName);
            TextView textViewDuration = convertView.findViewById(R.id.textViewDuration);
            TextView textViewCoolDown = convertView.findViewById(R.id.textViewCoolDown);

            Workout currentWorkout = getItem(position);

            textViewCoolDown.setText(Integer.toString(currentWorkout.getCoolDown()) + "s");
            textViewDuration.setText(Integer.toString(currentWorkout.getDuration()) + "s");
            textViewName.setText(currentWorkout.getName());
            return convertView;
        }
    }
    public void createOrEdit(View view) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("Ritual", ritualName);
        startActivity(intent);
    }
    public void startTimer(View view) {
        if(workoutList.isEmpty()) {
            Toast.makeText(this, "Workout List Empty", Toast.LENGTH_LONG).show();
        }
        else {
            Intent intent = new Intent(this, TimerActivity.class);
            intent.putExtra("Ritual", ritualName);
            startActivity(intent);
        }
    }
    public void upButton(View view) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        workoutList = databaseManager.readFromDatabase(ritualName);
        workoutAdapter.clear();
        workoutAdapter.addAll(workoutList);
    }
    public void deleteRitual(View view) {
        try {
            databaseManager.deleteRitual(ritualName);
        }
        catch(Exception e) {

        }
        finish();
    }
}