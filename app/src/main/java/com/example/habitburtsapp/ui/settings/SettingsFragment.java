package com.example.habitburtsapp.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.habitburtsapp.MainActivity;
import com.example.habitburtsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class SettingsFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private TextView signInDateText;

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
        LinearLayout changeNameSection = root.findViewById(R.id.changeNameSection);
        EditText newFirstNameField = root.findViewById(R.id.newFirstNameField);
        EditText newLastNameField = root.findViewById(R.id.newLastNameField);
        Button saveNameButton = root.findViewById(R.id.saveNameButton);

        LinearLayout changePasswordSection = root.findViewById(R.id.changePasswordSection);
        EditText newPasswordField = root.findViewById(R.id.newPasswordField);
        EditText confirmNewPasswordField = root.findViewById(R.id.confirmNewPasswordField);
        Button savePasswordButton = root.findViewById(R.id.savePasswordButton);

        Button logoutButton = root.findViewById(R.id.logoutButton);

        // Initialize the TextView for the sign-in date
        signInDateText = root.findViewById(R.id.signInDateText);

        // Display the sign-in date
        displaySignInDate();

        // Handle change name logic
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

    // Method to display the sign-in date
    private void displaySignInDate() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Get the account creation timestamp from Firebase User metadata
            long signInTimestamp = user.getMetadata().getCreationTimestamp();

            // Convert the timestamp to a Date object
            Date signInDate = new Date(signInTimestamp);

            // Format the date to a readable string
            CharSequence formattedDate = DateFormat.format("MMMM dd, yyyy", signInDate);

            // Display the formatted date in the TextView
            signInDateText.setText("Account created on: " + formattedDate);
        }
    }
}
