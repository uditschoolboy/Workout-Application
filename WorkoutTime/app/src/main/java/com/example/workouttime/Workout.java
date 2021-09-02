package com.example.workouttime;

import android.database.Cursor;

import java.util.ArrayList;

class Workout {
    private int duration, coolDown;
    private String name;
    public Workout(String name, int duration, int coolDown) {
        this.duration = duration;
        this.coolDown = coolDown;
        this.name = name;
    }
    public int getCoolDown() {
        return coolDown;
    }

    public int getDuration() {
        return duration;
    }

    public String getName() {
        return name;
    }

}
