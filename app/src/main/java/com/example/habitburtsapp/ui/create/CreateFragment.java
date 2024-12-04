package com.example.habitburtsapp.ui.create;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.habitburtsapp.databinding.FragmentCreateBinding;
import com.example.habitburtsapp.ui.habits.Habit;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateFragment extends Fragment {

    private FragmentCreateBinding binding;

    // Array of habit types for the dropdown options
    private static final String[] HABIT_TYPES = {
            "Exercise", "Meditation", "Writing", "Reading", "Music"
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CreateViewModel createViewModel =
                new ViewModelProvider(this).get(CreateViewModel.class);

        binding = FragmentCreateBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set up the Spinner with habit types
        Spinner spinnerType = binding.spinnerType;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, HABIT_TYPES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        // Set up the submit button click listener
        binding.buttonSubmit.setOnClickListener(v -> submitHabit());

        return root;
    }

    private void submitHabit() {
        String name = binding.editTextTitle.getText().toString();
        String description = binding.editTextDescription.getText().toString();
        String timeStr = binding.editTextTime.getText().toString();
        String type = binding.spinnerType.getSelectedItem().toString();  // Get the selected type

        // Validation
        if (name.isEmpty() || description.isEmpty() || timeStr.isEmpty() || type.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse time as integer
        int time;
        try {
            time = Integer.parseInt(timeStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter a valid number for time.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a habit object using the default constructor and setters
        Habit habit = new Habit();
        habit.setName(name);
        habit.setDescription(description);
        habit.setTime(time);
        habit.setType(type);

        // Submit to Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("submittedHabits")
                .add(habit)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Habit submitted successfully!", Toast.LENGTH_SHORT).show();
                    clearForm();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error submitting habit.", Toast.LENGTH_SHORT).show();
                });
    }

    // Method to clear the form after successful submission
    private void clearForm() {
        binding.editTextTitle.setText("");
        binding.editTextDescription.setText("");
        binding.editTextTime.setText("");
        binding.spinnerType.setSelection(0);  // Reset to the first item in the spinner
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
