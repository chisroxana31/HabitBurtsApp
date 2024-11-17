package com.example.habitburtsapp.ui.habits;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.habitburtsapp.R;

public class HabitDetailsDialog extends Dialog {

    private final String habitName;

    public HabitDetailsDialog(@NonNull Context context, String habitName) {
        super(context);
        this.habitName = habitName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_habit_details);

        TextView habitNameText = findViewById(R.id.habit_details_name);
        Button startChallengeButton = findViewById(R.id.start_challenge_button);

        habitNameText.setText(habitName);

        startChallengeButton.setOnClickListener(v -> {
            // Start habit challenge logic here
            dismiss();
        });
    }
}
