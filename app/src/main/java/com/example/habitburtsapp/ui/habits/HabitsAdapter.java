package com.example.habitburtsapp.ui.habits;

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

    private final List<String> habitList;
    private final Context context;

    public HabitsAdapter(Context context, List<String> habitList) {
        this.context = context;
        this.habitList = habitList;
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.habit_item, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        String habitName = habitList.get(position);
        holder.habitName.setText(habitName);

        holder.viewDetailsButton.setOnClickListener(v -> {
            // Open a pop-up dialog
            HabitDetailsDialog dialog = new HabitDetailsDialog(context, habitName);
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    public static class HabitViewHolder extends RecyclerView.ViewHolder {
        TextView habitName;
        Button viewDetailsButton;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            habitName = itemView.findViewById(R.id.habit_name);
            viewDetailsButton = itemView.findViewById(R.id.view_details_button);
        }
    }
}
