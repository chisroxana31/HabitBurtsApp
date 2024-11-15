package com.example.habitburtsapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        // Connect UI components to Java variables
        EditText emailField = findViewById(R.id.emailField);
        EditText passwordField = findViewById(R.id.passwordField);
        Button signUpButton = findViewById(R.id.signUpButton);
        Button loginButton = findViewById(R.id.loginButton);

        // Set click listeners for the buttons
        signUpButton.setOnClickListener(v -> createUser(emailField.getText().toString(), passwordField.getText().toString()));
        loginButton.setOnClickListener(v -> loginUser(emailField.getText().toString(), passwordField.getText().toString()));

    }

    private void createUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign-up success
                        FirebaseUser user = auth.getCurrentUser();
                        assert user != null;
                        Toast.makeText(MainActivity.this, "User created: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                    } else {
                        // Sign-up failure
                        Toast.makeText(MainActivity.this, "Authentication failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login success
                        FirebaseUser user = auth.getCurrentUser();
                        assert user != null;
                        Toast.makeText(MainActivity.this, "Welcome: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                    } else {
                        // Login failure
                        Toast.makeText(MainActivity.this, "Login failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void logoutUser() {
        auth.signOut();
        Toast.makeText(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

}