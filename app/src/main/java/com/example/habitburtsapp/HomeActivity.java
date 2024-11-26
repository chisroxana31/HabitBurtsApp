package com.example.habitburtsapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.habitburtsapp.databinding.ActivityHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkIfInCurrentActivity()) {
            return; // Early return if redirected to CurrentActivity
        }

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setUpToolbarAndNavigation();

        fetchAndDisplayUserInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


     // Checks if the user is currently in CurrentActivity and redirects them if so.
     //@return true if redirected to CurrentActivity, false otherwise.
    private boolean checkIfInCurrentActivity() {
        SharedPreferences preferences = getSharedPreferences("HabitBurstsPrefs", MODE_PRIVATE);
        boolean isInCurrentActivity = preferences.getBoolean("isInCurrentActivity", false);

        if (isInCurrentActivity) {
            Intent intent = new Intent(this, CurrentActivity.class);
            startActivity(intent);
            finish(); // Close HomeActivity
            return true;
        }

        return false;
    }

    // Sets up the toolbar, navigation drawer, and app bar configuration.
    private void setUpToolbarAndNavigation() {
        setSupportActionBar(binding.appBarHome.toolbar);

        binding.appBarHome.fab.setOnClickListener(view ->
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show()
        );

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_settings, R.id.nav_current)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    //Fetches and displays the user information in the navigation header.
    @SuppressLint("SetTextI18n")
    private void fetchAndDisplayUserInfo() {
        NavigationView navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);

        TextView nameTextView = headerView.findViewById(R.id.textViewName);
        TextView emailTextView = headerView.findViewById(R.id.textViewEmail);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            String email = user.getEmail();

            if (email != null) {
                emailTextView.setText(email);
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String firstName = document.getString("firstName");
                        String lastName = document.getString("lastName");

                        if (firstName != null && lastName != null) {
                            nameTextView.setText(firstName + " " + lastName);
                        } else {
                            nameTextView.setText("Name not available");
                        }
                    } else {
                        nameTextView.setText("Document not found");
                    }
                } else {
                    nameTextView.setText("Error loading name");
                }
            });
        }
    }
}
