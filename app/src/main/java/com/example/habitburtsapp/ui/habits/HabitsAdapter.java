package com.example.habitburtsapp.ui.habits;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitburtsapp.CurrentActivity;
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
        Habit habit = habitList.get(position);

        // Bind data to the view holder
        bindHabitData(holder, habit);

        // Set background based on the habit type
        setHabitBackground(holder, habit.getType());

        // Set up button listeners
        setupViewDetailsButton(holder.viewDetailsButton, habit);
        setupStartHabitButton(holder.startHabitButton, habit);
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    //Binds the habit data to the view holder.
    private void bindHabitData(@NonNull HabitViewHolder holder, @NonNull Habit habit) {
        holder.habitName.setText(habit.getName());
        holder.habitType.setText(habit.getType() + " Habit");
    }

    //Sets the background of the habit item based on its type.
    private void setHabitBackground(@NonNull HabitViewHolder holder, @NonNull String habitType) {
        int backgroundResId;

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
                backgroundResId = R.drawable.box_background; // Fallback background
                break;
        }

        holder.itemView.setBackgroundResource(backgroundResId);
    }

    //Sets up the View Details button click listener.
    private void setupViewDetailsButton(@NonNull Button button, @NonNull Habit habit) {
        button.setOnClickListener(v -> {
            HabitDetailsDialog dialog = new HabitDetailsDialog(context, habit);
            dialog.show();
        });
    }

    //Sets up the Start Habit button click listener
    private void setupStartHabitButton(@NonNull Button button, @NonNull Habit habit) {
        button.setOnClickListener(v -> {
            Toast.makeText(context, "Habit Challenge Started", Toast.LENGTH_SHORT).show();

            // Redirect to CurrentActivity with habit details
            Intent intent = new Intent(context, CurrentActivity.class);
            intent.putExtra("habit_name", habit.getName());
            intent.putExtra("habit_description", habit.getDescription());
            intent.putExtra("habit_duration", habit.getTime() * 60); // Duration in seconds
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        });
    }

    public static class HabitViewHolder extends RecyclerView.ViewHolder {
        TextView habitName;
        TextView habitType;
        Button viewDetailsButton;
        Button startHabitButton;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            habitName = itemView.findViewById(R.id.habit_name);
            habitType = itemView.findViewById(R.id.habit_type);
            viewDetailsButton = itemView.findViewById(R.id.view_details_button);
            startHabitButton = itemView.findViewById(R.id.start_button);
        }
    }
}
