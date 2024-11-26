package com.example.habitburtsapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CurrentActivity extends AppCompatActivity {

    private static final String EXTRA_HABIT_NAME = "habit_name";
    private static final String EXTRA_HABIT_DESCRIPTION = "habit_description";
    private static final String EXTRA_HABIT_DURATION = "habit_duration";

    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current);

        // Retrieve the habit data from the Intent
        String habitName = getIntent().getStringExtra(EXTRA_HABIT_NAME);
        String habitDescription = getIntent().getStringExtra(EXTRA_HABIT_DESCRIPTION);
        int habitDuration = getIntent().getIntExtra(EXTRA_HABIT_DURATION, 0);

        // Set the flag to indicate the user is in CurrentActivity
        markAsInCurrentActivity(true);

        // Display habit details
        displayHabitDetails(habitName, habitDescription);

        // Start the countdown timer
        startCountdown(habitDuration);

        // Set up buttons
        setupFinishButton();
        setupAbandonButton();
    }

    // Display details
    private void displayHabitDetails(String habitName, String habitDescription) {
        TextView habitNameTextView = findViewById(R.id.habitNameTextView);
        TextView habitDescriptionTextView = findViewById(R.id.habitDescriptionTextView);

        habitNameTextView.setText(habitName);
        habitDescriptionTextView.setText(habitDescription);
    }

    // Display countdown
    private void startCountdown(int durationInSeconds) {
        TextView timerTextView = findViewById(R.id.timerTextView);

        countDownTimer = new CountDownTimer(durationInSeconds * 1000L, 1000) {

            @SuppressLint("DefaultLocale")
            public void onTick(long millisUntilFinished) {
                // Convert milliseconds into minutes and seconds
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;

                // Display time in "MM:SS" format
                timerTextView.setText(String.format("Time left: %02d:%02d", minutes, seconds));
            }

            public void onFinish() {
                // Clear the flag to indicate the user is leaving CurrentActivity
                markAsInCurrentActivity(false);

                Toast.makeText(CurrentActivity.this, "Timer finished", Toast.LENGTH_SHORT).show();


                // Redirect to HomeActivity when the timer ends
                redirectToHomeActivity();
            }
        }.start();
    }

    //Marks the user's presence in CurrentActivity by updating SharedPreferences.
    private void markAsInCurrentActivity(boolean isInCurrentActivity) {
        SharedPreferences preferences = getSharedPreferences("HabitBurstsPrefs", MODE_PRIVATE);
        preferences.edit().putBoolean("isInCurrentActivity", isInCurrentActivity).apply();
    }


    //Sets up the finish button behavior.
    //When clicked, clears the "isInCurrentActivity" flag and redirects to HomeActivity.
    private void setupFinishButton() {
        Button finishButton = findViewById(R.id.finishButton);

        finishButton.setOnClickListener(v -> {
            // Clear the flag to indicate the user is leaving CurrentActivity
            markAsInCurrentActivity(false);

            // Stop the timer
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }

            Toast.makeText(CurrentActivity.this, "Habit Challenge Finished", Toast.LENGTH_SHORT).show();

            // Redirect to HomeActivity
            redirectToHomeActivity();
        });
    }

    //Sets up the abandon button behavior.
    //When clicked, clears the "isInCurrentActivity" flag and redirects to HomeActivity.
    private void setupAbandonButton() {
        Button abandonButton = findViewById(R.id.abandonButton);

        abandonButton.setOnClickListener(v -> {
            // Clear the flag to indicate the user is leaving CurrentActivity
            markAsInCurrentActivity(false);

            // Stop the timer
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }

            Toast.makeText(CurrentActivity.this, "Habit Challenge Abandoned", Toast.LENGTH_SHORT).show();


            // Redirect to HomeActivity
            redirectToHomeActivity();
        });
    }


    // Redirects the user to HomeActivity and finishes CurrentActivity.
    private void redirectToHomeActivity() {
        Intent intent = new Intent(CurrentActivity.this, HomeActivity.class);
        startActivity(intent);
        finish(); // Close CurrentActivity
    }

}
