package com.unimib.triptales.ui.settings;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.unimib.triptales.R;
import com.unimib.triptales.adapters.SettingsAdapter;


public class SettingsActivity extends AppCompatActivity {

    ViewPager2 viewPager2;
    SettingsAdapter viewPagerAdapter;
    //LinearLayout rootLayoutDiary;
    Toolbar toolbar;
    BottomNavigationView bottom_navigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.toolbar);
        bottom_navigation = findViewById(R.id.bottom_navigation);
        viewPager2 = findViewById(R.id.settingsSlider);

        viewPagerAdapter = new SettingsAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);
    }


}