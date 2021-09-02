package com.example.workouttime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class TimerActivity extends AppCompatActivity {
    private TextView textViewName, textViewTime, textViewRitual;
    private ArrayList<Workout> workoutList;
    private ImageButton pauseButton;
    private int currentIdx, timeLeft;
    private TextToSpeech mTTS;
    private int suffixSum[][];
    private int len;
    private boolean paused, canSpeak;
    private boolean finished;
    private WorkoutTimer workoutTimer;
    private DatabaseManager databaseManager;
    private String ritualName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        textViewName = findViewById(R.id.textViewName);
        textViewTime = findViewById(R.id.textViewTime);
        textViewRitual = findViewById(R.id.textViewRitualTimer);
        databaseManager = new DatabaseManager(this);
        ritualName = getIntent().getStringExtra("Ritual");
        textViewRitual.setText(ritualName);
        pauseButton = findViewById(R.id.buttonPause);
        workoutList = databaseManager.readFromDatabase(ritualName);
        len = workoutList.size();
        suffixSum = new int[len + 3][2];
        for(int i = len - 1; i >= 0; i--) {
            suffixSum[i][1] = suffixSum[i + 1][0] + workoutList.get(i).getCoolDown();
            suffixSum[i][0] = suffixSum[i][1] + workoutList.get(i).getDuration();
        }
        canSpeak = true;
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.ENGLISH);
                    if(result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA) {
                        canSpeak = false;
                    }
                }
            }
        });

        paused = false;
        currentIdx = 0;
        finished = false;
        workoutTimer = new WorkoutTimer(suffixSum[0][0] * 1000);
    }
    public void onbuttonSkip(View v) {
        if(!finished) {
            currentIdx++;
            workoutTimer.cancel();
            if (currentIdx >= workoutList.size())
                onFinishWorkout();
            else
                workoutTimer = new WorkoutTimer(suffixSum[currentIdx][0] * 1000);
        }
    }
    public void onButtonPrevious(View v) {
        if(!finished) {
            currentIdx = Math.max(0, currentIdx - 1);
            workoutTimer.cancel();
            workoutTimer = new WorkoutTimer(suffixSum[currentIdx][0] * 1000);
        }
    }
    public void onbuttonPause(View v) {
        if(!finished) {
            if (paused) {
                paused = false;
                pauseButton.setImageResource(R.drawable.ic_pause_24px);
                workoutTimer = new WorkoutTimer(timeLeft * 1000);
            } else {
                paused = true;
                pauseButton.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                workoutTimer.cancel();
            }
        }
    }
    public void onbuttonStop(View v) {
        if(!finished) {
            onFinishWorkout();
        }
    }
    private void onFinishWorkout() {
        workoutTimer.cancel();
        String speech = ritualName + " Complete.";
        mTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null, null);
        textViewName.setText("Complete");
        textViewTime.setText("00 : 00");
        finished = true;
    }
    private class WorkoutTimer extends CountDownTimer {
        public WorkoutTimer(long timeInMillis) {
            super(timeInMillis, 1000);
            if(canSpeak) {
                String name = workoutList.get(currentIdx).getName();
                int duration = workoutList.get(currentIdx).getDuration();
                int minutes = duration / 60, second = duration % 60;
                String speech = name + ", for ";
                if (minutes > 0) {
                    speech = speech + minutes + " minutes, ";
                }
                speech = speech + second + " seconds.";
                mTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null, null);
            }
            this.start();
        }

        @Override
        public void onTick(long l) {
            int minutesRemaining = (int)l / 1000 / 60;
            int secondRemaining = (int)l / 1000 % 60;
            String minString = "" + minutesRemaining;
            String secString = "" + secondRemaining;
            if(minString.length() <= 1) minString = "0" + minString;
            if(secString.length() <= 1) secString = "0" + secString;
            textViewTime.setText(minString + " : " + secString);
            timeLeft = (int)l / 1000;
            if(suffixSum[currentIdx + 1][0] >= timeLeft) {
                currentIdx++;
                String name = workoutList.get(currentIdx).getName();
                int duration = workoutList.get(currentIdx).getDuration();
                int minutes = duration / 60, second = duration % 60;
                if(canSpeak) {
                    String speech = name + ", for ";
                    if (minutes > 0) {
                        speech = speech + minutes + " minutes,";
                    }
                    speech = speech + second + " seconds.";
                    mTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null, null);
                }
                textViewName.setText(name);

            }
            else if(suffixSum[currentIdx][1] >= l / 1000) {
                if(!(textViewName.getText().equals("CoolDown"))) {
                    textViewName.setText("CoolDown");
                    if(canSpeak) {
                        int coolDown = workoutList.get(currentIdx).getCoolDown();
                        int minute = coolDown / 60, second = coolDown % 60;
                        String speech = "CoolDown, for ";
                        if (minute > 0) {
                            speech = speech + minute + " minutes, ";
                        }
                        speech = speech + second + " seconds.";
                        mTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }
            }
            else {
                textViewName.setText(workoutList.get(currentIdx).getName());
            }
        }

        @Override
        public void onFinish() {
            onFinishWorkout();
        }
    }
    public void backPressed(View view) {
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!paused)
            onbuttonPause(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(paused)
            onbuttonPause(null);
    }
}