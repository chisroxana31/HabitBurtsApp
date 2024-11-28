package com.example.habitburtsapp.ui.habits;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HabitsAdapter extends RecyclerView.Adapter<HabitsAdapter.HabitViewHolder> {

    private final List<Habit> habitList;
    private final Context context;
    private final Set<String> completedHabitIDs;

    public HabitsAdapter(Context context, List<Habit> habitList, List<String> completedHabitIDs) {
        this.context = context;
        this.habitList = habitList;
        this.completedHabitIDs = new HashSet<>(completedHabitIDs);

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
        setHabitBackground(holder, habit.getType(), habit.getHabitID());

        // Setup buttons for non-completed habits
        if (!completedHabitIDs.contains(habit.getHabitID())) {
            // Setup buttons
            setupViewDetailsButton(holder.viewDetailsButton, habit);
            setupStartHabitButton(holder.startHabitButton, habit);

            // Hide the "Done" label
            holder.doneLabel.setVisibility(View.INVISIBLE);
        } else {
            // Hide buttons
            holder.viewDetailsButton.setVisibility(View.INVISIBLE);
            holder.startHabitButton.setVisibility(View.INVISIBLE);

            // Show the "Done" label
            holder.doneLabel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    //Binds the habit data to the view holder.
    @SuppressLint("SetTextI18n")
    private void bindHabitData(@NonNull HabitViewHolder holder, @NonNull Habit habit) {
        holder.habitName.setText(habit.getName());
        holder.habitType.setText(habit.getType() + " Habit");
    }

    //Sets the background of the habit item based on its type.
    private void setHabitBackground(@NonNull HabitViewHolder holder, @NonNull String habitType, String habitID) {
        int backgroundResId;

        // Modify background for completed habits
        if(completedHabitIDs.contains(habitID)){
            switch (habitType.toLowerCase()) {
                case "exercise":
                    backgroundResId = R.drawable.exercise_bw;
                    break;
                case "meditation":
                    backgroundResId = R.drawable.meditation_bw;
                    break;
                case "reading":
                    backgroundResId = R.drawable.reading_bw;
                    break;
                case "writing":
                    backgroundResId = R.drawable.writing_bw;
                    break;
                case "music":
                    backgroundResId = R.drawable.music;
                    break;
                default:
                    backgroundResId = R.drawable.box_background; // Fallback background
                    break;
            }
        } else {
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
}
        holder.itemView.setBackgroundResource(backgroundResId);
    }

    //Sets up the View Details button click listener.
    private void setupViewDetailsButton(@NonNull Button button, @NonNull Habit habit) {
        button.setOnClickListener(v -> {

            // Calculate the number of stars based on time
            String formattedMessage = habit.getDescription() + "<br>" +
                    "<div style='text-align: center; '>Time: " + habit.getTime() + ":00</div>" +
                    "<div style='text-align: center'>" + "Reward: "+ getRewardString(habit)+ "</div>";


            // Build the dialog with a formatted message
            new androidx.appcompat.app.AlertDialog.Builder(context)
                    .setTitle(habit.getName() + "\n\n" )
                    .setMessage(android.text.Html.fromHtml(formattedMessage, android.text.Html.FROM_HTML_MODE_LEGACY))
                    .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    @NonNull
    private static String getRewardString(@NonNull Habit habit) {
        int starCount = habit.getTime() / 3; // 1 star for every 3 minutes
        return "★".repeat(Math.max(0, starCount)); // Repeat the star symbol starCount times
    }


    //Sets up the Start Habit button click listener
    private void setupStartHabitButton(@NonNull Button button, @NonNull Habit habit) {
        button.setOnClickListener(v -> {
            Toast.makeText(context, "Habit Challenge Started!", Toast.LENGTH_SHORT).show();

            // Redirect to CurrentActivity with habit details
            Intent intent = new Intent(context, CurrentActivity.class);
            intent.putExtra("habit_id", habit.getHabitID());
            intent.putExtra("habit_name", habit.getName());
            intent.putExtra("habit_description", habit.getDescription());
            intent.putExtra("habit_duration", habit.getTime() * 60); // Duration in seconds
            intent.putExtra("habit_reward", getRewardString(habit));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        });
    }

    public static class HabitViewHolder extends RecyclerView.ViewHolder {
        TextView habitName;
        TextView habitType;
        Button viewDetailsButton;
        Button startHabitButton;
        TextView doneLabel;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            habitName = itemView.findViewById(R.id.habit_name);
            habitType = itemView.findViewById(R.id.habit_type);
            viewDetailsButton = itemView.findViewById(R.id.view_details_button);
            startHabitButton = itemView.findViewById(R.id.start_button);
            doneLabel = itemView.findViewById(R.id.done_label);
        }
    }


}
