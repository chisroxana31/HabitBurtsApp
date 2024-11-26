package com.example.habitburtsapp.ui.habits;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitburtsapp.R;

import java.util.List;

public class HabitsAdapter extends RecyclerView.Adapter<HabitsAdapter.HabitViewHolder> {

    private final List<Habit> habitList;
    private final Context context;

    public HabitsAdapter(Context context, List<Habit> habitList) {
        this.context = context;
        this.habitList = habitList;
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.habit_item, parent, false);
        return new HabitViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        // Get the habit object
        Habit habit = habitList.get(position);

        // Set the name of the habit
        holder.habitName.setText(habit.getName());

        // Set the type of the habit
        holder.habitType.setText(habit.getType()+ " Habit");

        // Set the background based on the type
        String habitType = habit.getType(); // e.g., "exercise", "meditation"
        int backgroundResId ;

        // Map the type to the corresponding drawable resource
        switch (habitType.toLowerCase()) {
            case "exercise":
                backgroundResId = R.drawable.exercise;
                break;
            case "meditation":
                backgroundResId = R.drawable.meditation;
                break;
            case "reading":
                backgroundResId = R.drawable.reading;
                break;
            case "writing":
                backgroundResId = R.drawable.writing;
                break;
            case "music":
                backgroundResId = R.drawable.music;
                break;
            default:
                backgroundResId = R.drawable.box_background;
                // Fallback background
                break;
        }

        // Set the background resource
        holder.itemView.setBackgroundResource(backgroundResId);

        // Set up the button click listener
        holder.viewDetailsButton.setOnClickListener(v -> {
            HabitDetailsDialog dialog = new HabitDetailsDialog(context, habit);
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    public static class HabitViewHolder extends RecyclerView.ViewHolder {
        TextView habitName;
        TextView habitType;
        Button viewDetailsButton;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            habitName = itemView.findViewById(R.id.habit_name);
            habitType = itemView.findViewById(R.id.habit_type);
            viewDetailsButton = itemView.findViewById(R.id.view_details_button);
        }
    }
}
