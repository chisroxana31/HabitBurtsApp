package com.example.habitburtsapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CurrentActivity extends AppCompatActivity {

    private static final String EXTRA_HABIT_NAME = "habit_name";
    private static final String EXTRA_HABIT_DESCRIPTION = "habit_description";
    private static final String EXTRA_HABIT_DURATION = "habit_duration";
    private static final String EXTRA_HABIT_REWARD = "habit_reward";
    private static final String EXTRA_HABIT_ID = "habit_id";
    private long totalDurationInMillis;
    private long remainingTimeInMillis;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current);

        // Retrieve the habit data from the Intent
        String habitName = getIntent().getStringExtra(EXTRA_HABIT_NAME);
        String habitDescription = getIntent().getStringExtra(EXTRA_HABIT_DESCRIPTION);
        int habitDuration = getIntent().getIntExtra(EXTRA_HABIT_DURATION, 0);
        String habitReward = getIntent().getStringExtra(EXTRA_HABIT_REWARD);
        String habitID = getIntent().getStringExtra(EXTRA_HABIT_ID);

        // Set the flag to indicate the user is in CurrentActivity
        markAsInCurrentActivity(true);

        // Display habit details
        displayHabitDetails(habitName, habitDescription, habitReward);

        // Start the countdown timer
        startCountdown(habitDuration);

        // Set up buttons
        setupFinishButton(habitID, habitReward.length());
        setupAbandonButton();
    }

    // Display details
    private void displayHabitDetails(String habitName, String habitDescription, String habitReward) {
        TextView habitNameTextView = findViewById(R.id.habitNameTextView);
        TextView habitDescriptionTextView = findViewById(R.id.habitDescriptionTextView);
        TextView habitRewardTextView = findViewById(R.id.habitRewardTextView);

        habitNameTextView.setText(habitName);
        habitDescriptionTextView.setText(habitDescription);
        habitRewardTextView.setText(habitReward);
    }

    // Display countdown
    private void startCountdown(int durationInSeconds) {
        TextView timerTextView = findViewById(R.id.timerTextView);

        // Initialize total and remaining time
        totalDurationInMillis = durationInSeconds * 1000L;
        remainingTimeInMillis = totalDurationInMillis;

        countDownTimer = new CountDownTimer(totalDurationInMillis, 1000) {

            @SuppressLint("DefaultLocale")
            public void onTick(long millisUntilFinished) {

                // Update remaining time
                remainingTimeInMillis = millisUntilFinished;

                // Convert milliseconds into minutes and seconds
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;

                // Display time in "MM:SS" format
                timerTextView.setText(String.format("Time left: %02d:%02d", minutes, seconds));
            }

            public void onFinish() {

                // Clear the flag to indicate the user is leaving CurrentActivity
                markAsInCurrentActivity(false);

                Toast.makeText(CurrentActivity.this, "Timer finished!", Toast.LENGTH_SHORT).show();


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
    private void setupFinishButton(String habitID, int starCount) {
        Button finishButton = findViewById(R.id.finishButton);

        finishButton.setOnClickListener(v -> {

            // Calculate elapsed time
            long elapsedTime = totalDurationInMillis - remainingTimeInMillis;

            // Check if the user is trying to abandon the challenge too early
            if (elapsedTime < totalDurationInMillis / 2) {
                showEarlyFinishDialog();
            } else {

                // Clear the flag to indicate the user is leaving CurrentActivity
                markAsInCurrentActivity(false);

                //Stop timer
                stopCountdownTimer();

                addHabitToCompleted(habitID, starCount);

                Toast.makeText(CurrentActivity.this, "Habit Challenge Finished!", Toast.LENGTH_SHORT).show();

                // Redirect to HomeActivity
                redirectToHomeActivity();
            }
        });
    }

    // Method to stop the countdown timer
    private void stopCountdownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    //Sets up the abandon button behavior.
    //When clicked, clears the "isInCurrentActivity" flag and redirects to HomeActivity.
    private void setupAbandonButton() {
        Button abandonButton = findViewById(R.id.abandonButton);

        abandonButton.setOnClickListener(v -> {

            // Clear the flag to indicate the user is leaving CurrentActivity
            markAsInCurrentActivity(false);

            //Stop timer
            stopCountdownTimer();

            Toast.makeText(CurrentActivity.this, "Habit Challenge Abandoned!", Toast.LENGTH_SHORT).show();


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


    // Method to show a dialog when finishing too early
    private void showEarlyFinishDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(CurrentActivity.this)
                .setTitle("Wait a moment!")
                .setMessage("That's a bit fast. Have you really completed the challenge?")
                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void addHabitToCompleted(String habitID, int starCount) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<String> completedHabitIDs = (List<String>) document.get("completedHabitIDs");
                    Long userStars = (Long) document.get("stars");

                    if (completedHabitIDs == null) {
                        completedHabitIDs = new ArrayList<>();
                    }

                    // Add the new habit ID to the list
                    completedHabitIDs.add(habitID);

                    // Update the user's completedHabitIDs list in Firebase
                    userRef.update("completedHabitIDs", completedHabitIDs)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("CurrentActivity", "Habit added to completed list.");
                            })
                            .addOnFailureListener(e -> {
                                Log.e("CurrentActivity", "Error adding habit to completed list.", e);
                            });

                    // Update stars
                    userRef.update("stars", userStars + starCount).addOnSuccessListener(aVoid -> {
                                Log.d("CurrentActivity", "Stars added.");
                            })
                            .addOnFailureListener(e -> {
                                Log.e("CurrentActivity", "Error adding stars.", e);
                            });;
                }
            } else {
                Log.e("CurrentActivity", "Error getting user document", task.getException());
            }
        });
    }
}

