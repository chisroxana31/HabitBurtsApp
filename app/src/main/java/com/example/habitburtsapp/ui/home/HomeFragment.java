package com.example.habitburtsapp.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.habitburtsapp.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.habitburtsapp.R;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Fetch and display user data
        fetchAndDisplayUserInfo(root);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("SetTextI18n")
    private void fetchAndDisplayUserInfo(View root) {
        // Get references to the TextViews
        TextView greetingTextView = root.findViewById(R.id.textViewGreeting);
        TextView starsTextView = root.findViewById(R.id.textViewStars);

        // Get current Firebase user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            // Fetch user data from Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String firstName = document.getString("firstName");
                        greetingTextView.setText("Hi, " + firstName + "!");
                        // Get the integer value for the "starCount" field
                        Long userStars = (Long) document.get("stars");
                        starsTextView.setText(userStars + " ⭐");

                    } else {
                        greetingTextView.setText("Hi, User!");
                    }
                } else {
                    greetingTextView.setText("Hi, User!");
                }
            });
        }
    }
}
