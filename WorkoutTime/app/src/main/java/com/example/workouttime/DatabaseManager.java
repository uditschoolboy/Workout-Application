package com.example.workouttime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.Nullable;

class DatabaseManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "WorkoutTimeDatabaseTrial";
    private static final String RITUAL_TABLE_NAME = "ritualtable";
    public DatabaseManager(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + RITUAL_TABLE_NAME + " (Name TEXT PRIMARY KEY)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + RITUAL_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
    private String processString(String tableName) {
        return "tname" + tableName.replace(' ', '_');
    }
    public boolean createRitual(String ritualName) {
        try {
            if(ritualName.isEmpty()) return false;
            ritualName = processString(ritualName);
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("Name", ritualName);
            long res = db.insert(RITUAL_TABLE_NAME, null, cv);
            if (res == -1) return false;
            db.execSQL("create table " + ritualName + " (Name TEXT PRIMARY KEY, Duration INTEGER, CoolDown INTEGER)");
        }
        catch(Exception e) {
            return false;
        }
        return true;
    }
    public void deleteRitual(String ritualName) {
        try {
            ritualName = processString(ritualName);
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(RITUAL_TABLE_NAME, "Name = ?", new String[]{ritualName});
            db.execSQL("drop table if exists " + ritualName);
        }
        catch (Exception e) { }
    }
    public boolean insert(Workout workout, String ritualName) {
        try {
            ritualName = processString(ritualName);
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("Name", workout.getName());
            cv.put("Duration", workout.getDuration());
            cv.put("CoolDown", workout.getCoolDown());
            long res = db.insert(ritualName, null, cv);
            return res != -1;
        }
        catch(Exception e) {

        }
        return false;
    }
    public boolean update(Workout workout, Workout oldWorkout, String ritualName) {
        try {
            ritualName = processString(ritualName);
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("Name", workout.getName());
            cv.put("Duration", workout.getDuration());
            cv.put("CoolDown", workout.getCoolDown());
            db.update(ritualName, cv, " Name = ? ", new String[]{oldWorkout.getName()});
        }
        catch (Exception e) { }
        return true;
    }
    public void delete(Workout workout, String ritualName) {
        try {
            ritualName = processString(ritualName);
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(ritualName, " Name = ? ", new String[]{workout.getName()});
        }
        catch (Exception e) { }
    }
    public ArrayList<Workout> readFromDatabase(String ritualName) {
        ritualName = processString(ritualName);
        Cursor cursor = read(ritualName);
        ArrayList<Workout> workoutList = new ArrayList<Workout>();
        while(cursor.moveToNext()) {
            String name = cursor.getString(0);
            int duration = cursor.getInt(1);
            int coolDown = cursor.getInt(2);
            workoutList.add(new Workout(name, duration, coolDown));
        }
        return workoutList;
    }
    private Cursor read(String ritualName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + ritualName, null);
        return cursor;
    }
    public ArrayList<String> getAllRituals() {
        Cursor cursor = read(RITUAL_TABLE_NAME);
        ArrayList<String> ritualList = new ArrayList<>();
        while(cursor.moveToNext()) {
            String ritualName = cursor.getString(0);
            ritualName = ritualName.substring(5);
            ritualName = ritualName.replace('_', ' ');
            ritualList.add(ritualName);
        }
        return ritualList;
    }
}
