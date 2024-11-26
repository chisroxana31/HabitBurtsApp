package com.example.habitburtsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class CurrentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current);

        // Set the flag to indicate the user is in CurrentActivity
        markAsInCurrentActivity(true);

        setupFinishButton();

        setupAbandonButton();
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
