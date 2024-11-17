package com.example.habitburtsapp.ui.habits;

import android.os.Bundle;
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

import java.util.Arrays;
import java.util.List;

public class HabitsFragment extends Fragment {

    private FragmentHabitsBinding binding;
    private RecyclerView habitsRecyclerView;

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

        // Dummy habit list
        List<String> habitList = Arrays.asList("Exercise", "Meditation", "Reading", "Writing");

        // Set the adapter
        HabitsAdapter adapter = new HabitsAdapter(getContext(), habitList);
        habitsRecyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}