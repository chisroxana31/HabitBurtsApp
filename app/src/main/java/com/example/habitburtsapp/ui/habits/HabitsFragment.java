package com.example.habitburtsapp.ui.habits;

import android.content.Intent;
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

import com.example.habitburtsapp.MainActivity;
import com.example.habitburtsapp.R;
import com.example.habitburtsapp.databinding.FragmentHabitsBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HabitsFragment extends Fragment {

    private FragmentHabitsBinding binding;
    private RecyclerView habitsRecyclerView;
    private List<Habit> habitList;
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

        // **Initialize habitList here as an empty ArrayList**
        habitList = new ArrayList<>();

        // **Set up adapter with the empty habitList**
        adapter = new HabitsAdapter(getContext(), habitList);
        habitsRecyclerView.setAdapter(adapter);

        // Fetch habits from Firestore
        fetchHabitsFromFirestore();

        return root;
    }
    // Fetch habits from Firestore and update the habit list
    private void fetchHabitsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("habits") // Replace with your Firestore collection name
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        habitList.clear(); // Clear existing data
                        for (DocumentSnapshot document : task.getResult()) {
                            // Create a Habit object from Firestore document
                            Habit habit = new Habit();
                            if(document.exists()) {
                                habit.setName(document.getString("name"));
                                habit.setDescription(document.getString("description"));
                                habit.setType(document.getString("type"));
                                habit.setTime(document.getLong("time").intValue());
                            }
                            // Add the habit to the list
                            habitList.add(habit);
                        }
                        // Notify adapter about data changes
                        adapter.notifyDataSetChanged();
                    } else {
                        // Handle errors
                         Log.d("HabitsFragment", "Error getting documents: ", task.getException());
                    }
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}