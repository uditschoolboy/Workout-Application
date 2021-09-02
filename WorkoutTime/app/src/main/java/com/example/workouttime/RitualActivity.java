package com.example.workouttime;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class RitualActivity extends AppCompatActivity {
    private DatabaseManager databaseManager;
    private ArrayList<String> ritualList;
    private ListView listView;
    private ArrayAdapter<String> ritualAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ritual);
        databaseManager = new DatabaseManager(this);
        ritualList = databaseManager.getAllRituals();
        ritualAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ritualList);
        listView = findViewById(R.id.listViewRitual);
        listView.setAdapter(ritualAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String ritualName = ritualList.get(i);
                listItemClicked(ritualName);
            }
        });

//        Intent myIntent = new Intent(getApplicationContext() , MyService.class);
//        AlarmManager alarmManager = (AlarmManager) getSystemService( ALARM_SERVICE ) ;
//        PendingIntent pendingIntent = PendingIntent. getService ( this, 0 , myIntent , 0 ) ;
//        Calendar calendar = Calendar. getInstance () ;
//        calendar.set(Calendar. SECOND, 0) ;
//        calendar.set(Calendar. MINUTE, 57) ;
//        calendar.set(Calendar. HOUR, 12) ;
//        calendar.set(Calendar. AM_PM, Calendar.PM) ;
//        alarmManager.setRepeating(AlarmManager. RTC_WAKEUP , calendar.getTimeInMillis() , 1000 * 60 * 60 * 24 , pendingIntent) ;
    }
    private void listItemClicked(String ritualName) {
        Intent intent = new Intent(this, WorkoutList.class);
        intent.putExtra("Ritual", ritualName);
        startActivity(intent);
    }
    public void fabClickRitual(View view) {
        Intent intent = new Intent(this, CreateRitual.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ritualList = databaseManager.getAllRituals();
        ritualAdapter.clear();
        ritualAdapter.addAll(ritualList);
    }
}