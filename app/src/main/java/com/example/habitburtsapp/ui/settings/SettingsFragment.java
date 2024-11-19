package com.example.habitburtsapp.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.habitburtsapp.MainActivity;
import com.example.habitburtsapp.databinding.FragmentSettingsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        // Bind the logout button
        Button logoutButton = binding.logoutButton;
        logoutButton.setOnClickListener(v -> logoutUser());

        // Bind UI elements
        Button changePasswordButton = binding.changePasswordButton;
        LinearLayout passwordChangeLayout = binding.passwordChangeLayout;
        Button savePasswordButton = binding.savePasswordButton;

        // Toggle password change layout visibility
        changePasswordButton.setOnClickListener(v -> {
            if (passwordChangeLayout.getVisibility() == View.GONE) {
                passwordChangeLayout.setVisibility(View.VISIBLE);
            } else {
                passwordChangeLayout.setVisibility(View.GONE);
            }
        });

        // Save new password
        savePasswordButton.setOnClickListener(v -> {
            String newPassword = binding.newPasswordField.getText().toString().trim();
            String confirmPassword = binding.confirmNewPasswordField.getText().toString().trim();

            if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(getContext(), "Please fill out both fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(getContext(), "Passwords do not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update password in Firebase
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                user.updatePassword(newPassword)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Password updated successfully!", Toast.LENGTH_SHORT).show();
                                passwordChangeLayout.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(getContext(), "User not logged in.", Toast.LENGTH_SHORT).show();
            }
        });


        return root;
    }

    private void logoutUser() {
        // Sign out from Firebase Authentication
        Toast.makeText(getContext(), "Logged out" , Toast.LENGTH_SHORT).show();
        FirebaseAuth.getInstance().signOut();


        // Redirect to MainActivity (Login screen)
        Intent intent = new Intent(getActivity(), MainActivity.class);
        // Clear the activity stack to prevent the user from returning to the previous activity
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}