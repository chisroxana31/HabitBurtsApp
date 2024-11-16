package com.example.habitburtsapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Connect UI components to Java variables
        EditText firstNameField = findViewById(R.id.firstNameField);
        EditText lastNameField = findViewById(R.id.lastNameField);
        EditText emailField = findViewById(R.id.emailField);
        EditText passwordField = findViewById(R.id.passwordField);
        EditText verifyPasswordField = findViewById(R.id.verifyPasswordField);
        Button registerButton = findViewById(R.id.registerButton);
        Button goBackButton = findViewById(R.id.goBackButton);

        // Set click listener for register button
        registerButton.setOnClickListener(v -> {
            String firstName = firstNameField.getText().toString().trim();
            String lastName = lastNameField.getText().toString().trim();
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String verifyPassword = verifyPasswordField.getText().toString().trim();

            // Validate input
            if (firstName.isEmpty() || lastName.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "First and Last Name are required.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(verifyPassword)) {
                Toast.makeText(SignUpActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a new user
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Save additional user info in Firestore
                            FirebaseUser user = auth.getCurrentUser();
                            assert user != null;

                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("firstName", firstName);
                            userInfo.put("lastName", lastName);
                            userInfo.put("email", email);

                            db.collection("users").document(user.getUid())
                                    .set(userInfo)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(SignUpActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                                        // Redirect to login or home page
                                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Print the error in log and show a toast with error message
                                        Log.e("Firestore Error", Objects.requireNonNull(e.getMessage()));
                                        Toast.makeText(SignUpActivity.this, "Error saving user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });} else {
                            Toast.makeText(SignUpActivity.this, "Registration failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Set click listener for go back button
        goBackButton.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
