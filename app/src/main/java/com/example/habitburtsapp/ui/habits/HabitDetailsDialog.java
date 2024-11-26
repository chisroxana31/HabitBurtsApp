package com.example.habitburtsapp.ui.habits;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.habitburtsapp.R;

public class HabitDetailsDialog extends Dialog {

    private final Habit habit;

    public HabitDetailsDialog(@NonNull Context context, Habit habit) {
        super(context);
        this.habit = habit;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_habit_details);

        // Get references to the views
        TextView habitTimeText = findViewById(R.id.habit_details_time);
        TextView habitNameText = findViewById(R.id.habit_details_name);
        TextView habitDescriptionText = findViewById(R.id.habit_details_description);
        Button startChallengeButton = findViewById(R.id.start_challenge_button);
        Button closeButton = findViewById(R.id.close_button);

        habitNameText.setText(habit.getName());
        habitDescriptionText.setText(habit.getDescription());
        habitTimeText.setText(habit.getTime()+":00");

        closeButton.setOnClickListener(v -> {
            // Close
            dismiss();
        });

        startChallengeButton.setOnClickListener(v -> {
            // Start habit challenge logic here
            dismiss();
        });

    }
}
