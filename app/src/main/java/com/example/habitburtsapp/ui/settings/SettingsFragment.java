package com.example.habitburtsapp.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.habitburtsapp.MainActivity;
import com.example.habitburtsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    public SettingsFragment() {
        super(R.layout.fragment_settings);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Connect UI components to Java variables
        Button changeNameButton = root.findViewById(R.id.changeNameButton);
        Button changePasswordButton = root.findViewById(R.id.changePasswordButton);
        Button logoutButton = root.findViewById(R.id.logoutButton);

        LinearLayout nameChangeLayout = root.findViewById(R.id.nameChangeLayout);  // Updated ID
        EditText newFirstNameField = root.findViewById(R.id.newFirstNameField);  // First name field
        EditText newLastNameField = root.findViewById(R.id.newLastNameField);    // Last name field
        Button saveNameButton = root.findViewById(R.id.saveNameButton);

        EditText newPasswordField = root.findViewById(R.id.newPasswordField);
        EditText confirmNewPasswordField = root.findViewById(R.id.confirmNewPasswordField);
        Button savePasswordButton = root.findViewById(R.id.savePasswordButton);

        // Show layout for name change on button click (Toggling visibility)
        changeNameButton.setOnClickListener(v -> {
            if (nameChangeLayout.getVisibility() == View.GONE) {
                nameChangeLayout.setVisibility(View.VISIBLE);
                newFirstNameField.setText("");  // Clear any previous text
                newLastNameField.setText("");   // Clear any previous text
            } else {
                nameChangeLayout.setVisibility(View.GONE);
            }
        });

        saveNameButton.setOnClickListener(v -> {
            String newFirstName = newFirstNameField.getText().toString().trim();
            String newLastName = newLastNameField.getText().toString().trim();

            if (newFirstName.isEmpty() || newLastName.isEmpty()) {
                Toast.makeText(getContext(), "Both first name and last name are required", Toast.LENGTH_SHORT).show();
            } else {
                updateNameInFirestore(newFirstName, newLastName); // Update both first and last name
            }
        });

        // Handle change password logic
        changePasswordButton.setOnClickListener(v -> {
            LinearLayout passwordChangeLayout = root.findViewById(R.id.passwordChangeLayout);
            passwordChangeLayout.setVisibility(View.VISIBLE);
        });

        savePasswordButton.setOnClickListener(v -> {
            String newPassword = newPasswordField.getText().toString().trim();
            String confirmNewPassword = confirmNewPasswordField.getText().toString().trim();

            if (newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in both password fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmNewPassword)) {
                Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            updatePasswordInFirestore(newPassword);
        });

        // Handle logout button
        logoutButton.setOnClickListener(v -> logoutUser());

        return root;
    }

    // Method to update user's name in Firestore
    private void updateNameInFirestore(String newFirstName, String newLastName) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid())
                    .update("firstName", newFirstName, "lastName", newLastName)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Name updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error updating name: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }


    // Method to update user's password
    private void updatePasswordInFirestore(String newPassword) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error updating password: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void logoutUser() {
        // Sign out from Firebase Authentication
        FirebaseAuth.getInstance().signOut();

        // Redirect to MainActivity (Login screen)
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
