package com.example.habitburtsapp;

import android.content.Intent;
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

        // **CHECK IF USER IS ALREADY LOGGED IN**
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) { // If a user is logged in, skip login and go to HomeActivity
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Prevent going back to MainActivity
        }

        // Connect UI components to Java variables
        EditText emailField = findViewById(R.id.emailField);
        EditText passwordField = findViewById(R.id.passwordField);
        Button signUpButton = findViewById(R.id.signUpButton);
        Button loginButton = findViewById(R.id.loginButton);

        // Set click listeners for the buttons
        signUpButton.setOnClickListener(v -> {
            // Redirect to SignUpActivity
            Intent intent = new Intent(MainActivity.this, com.example.habitburtsapp.SignUpActivity.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(v -> loginUser(emailField.getText().toString(), passwordField.getText().toString()));

    }

    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login success
                        FirebaseUser user = auth.getCurrentUser();
                        assert user != null;
                        Toast.makeText(MainActivity.this, "Welcome: " + user.getEmail()+"!", Toast.LENGTH_SHORT).show();
                        // Redirect to HomeActivity after successful login
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);

                        // Call finish() to prevent going back to the login screen when the user presses the back button
                        finish();
                    } else {
                        // Login failure
                        Toast.makeText(MainActivity.this, "Login failed: " + Objects.requireNonNull(task.getException()).getMessage()+"!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}