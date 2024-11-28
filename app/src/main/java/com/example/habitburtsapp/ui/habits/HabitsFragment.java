package com.example.habitburtsapp.ui.habits;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitburtsapp.R;
import com.example.habitburtsapp.databinding.FragmentHabitsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HabitsFragment extends Fragment {

    private FragmentHabitsBinding binding;
    private RecyclerView habitsRecyclerView;
    private List<Habit> habitList;
    private List<String> completedHabitIDs;
    private HabitsAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HabitsViewModel habitsViewModel =
                new ViewModelProvider(this).get(HabitsViewModel.class);

        binding = FragmentHabitsBinding.inflate(inflater, container, false);
        View root = inflater.inflate(R.layout.fragment_habits, container, false);

        final TextView textView = binding.textGallery;
        habitsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Initialize RecyclerView
        habitsRecyclerView = root.findViewById(R.id.habits_recycler_view);
        habitsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize habitList here as an empty ArrayList
        habitList = new ArrayList<>();
        completedHabitIDs = new ArrayList<>();

        // Get database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Fetch completed habits from Firestore
        // Inside this function we then fetch the habits and setup the adapter
        fetchCompletedHabitsFromFirestore(db);

        return root;
    }

    private void fetchCompletedHabitsFromFirestore(FirebaseFirestore db) {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<String> fetchedCompletedHabitIDs = (List<String>) document.get("completedHabitIDs");

                    // If "completedHabitIDs" is null or not set, initialize it as an empty list
                if (fetchedCompletedHabitIDs == null) {
                    completedHabitIDs = new ArrayList<>();
                } else {
                    // Use a Set to remove duplicates and convert the List to ArrayList
                    completedHabitIDs = fetchedCompletedHabitIDs;
                }

            }
                else {
                // If the document doesn't exist, initialize completedHabitIDs as an empty list
                completedHabitIDs = new ArrayList<>();
            }
                // Fetch habits
                fetchHabitsFromFirestore(db);

            }

        });

    }

    private void setupAdapter() {
        adapter = new HabitsAdapter(getContext(), habitList, completedHabitIDs);
        habitsRecyclerView.setAdapter(adapter);
    }

    // Fetch habits from Firestore and update the habit list
    private void fetchHabitsFromFirestore(FirebaseFirestore db) {

        db.collection("habits")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        habitList.clear(); // Clear existing data
                        for (DocumentSnapshot document : task.getResult()) {
                            // Create a Habit object from Firestore document
                            Habit habit = new Habit();
                            if(document.exists()) {
                                habit.setHabitID(document.getId());
                                habit.setName(document.getString("name"));
                                habit.setDescription(document.getString("description"));
                                habit.setType(document.getString("type"));
                                habit.setTime(document.getLong("time").intValue());
                            }
                            // Add the habit to the list
                            habitList.add(habit);
                        }
                        // Sort the habit list so that completed habits are at the end
                        sortHabitList();
                        // Notify adapter about data changes
                        adapter.notifyDataSetChanged();
                    } else {
                        // Handle errors
                         Log.d("HabitsFragment", "Error getting documents: ", task.getException());
                    }
                });

        // Setup adapter
        setupAdapter();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void sortHabitList() {
        // Comparator to prioritize habits not in completedHabitIDs
        Comparator<Habit> habitComparator = (habit1, habit2) -> {
            boolean isHabit1Completed = completedHabitIDs.contains(habit1.getHabitID());
            boolean isHabit2Completed = completedHabitIDs.contains(habit2.getHabitID());

            // If habit1 is completed and habit2 is not, habit2 should come first
            if (!isHabit1Completed && isHabit2Completed) {
                return -1; // habit1 comes first
            }
            // If habit1 is not completed and habit2 is, habit1 should come first
            else if (isHabit1Completed && !isHabit2Completed) {
                return 1; // habit2 comes first
            }

            return 0; // If both are completed or both are not, maintain original order
        };

        // Sort the habit list using the comparator
        Collections.sort(habitList, habitComparator);
    }
}