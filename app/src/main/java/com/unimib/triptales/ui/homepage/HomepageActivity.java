package com.unimib.triptales.ui.homepage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.unimib.triptales.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.unimib.triptales.ui.homepage.fragment.CalendarFragment;
import com.unimib.triptales.ui.homepage.fragment.HomeFragment;
import com.unimib.triptales.ui.homepage.fragment.MapFragment;


public class HomepageActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Mostra il primo fragment (HomeFragment)
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();

            if (itemId == R.id.page_1) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.page_2) {
                selectedFragment = new MapFragment();
            } else if (itemId == R.id.page_3) {
                selectedFragment = new CalendarFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

    }
}
