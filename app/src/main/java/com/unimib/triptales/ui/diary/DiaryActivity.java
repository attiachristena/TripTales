package com.unimib.triptales.ui.diary;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.unimib.triptales.R;
import com.unimib.triptales.adapters.ViewPagerAdapter;
import com.unimib.triptales.ui.homepage.HomepageActivity;
import com.unimib.triptales.ui.login.LoginActivity;

    public class DiaryActivity extends AppCompatActivity {

        TabLayout tabLayout;
        ViewPager2 viewPager2;
        ViewPagerAdapter viewPagerAdapter;
        ConstraintLayout rootLayoutDiary;
        Toolbar toolbar;

        // Variabili per i dati
        private String diaryName;
        private String startDate;
        private String endDate;
        private Uri coverImageUri;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_diary);

            // Recupera i dati dall'Intent
            Intent intent = getIntent();
            if (intent != null) {
                diaryName = intent.getStringExtra("diaryName");
                startDate = intent.getStringExtra("startDate");
                endDate = intent.getStringExtra("endDate");
                coverImageUri = intent.getParcelableExtra("coverImageUri"); // Ricevi come Uri direttamente
            }

            // Set up the ViewPager2 and TabLayout (after the fragment setup)
            tabLayout = findViewById(R.id.tablayout);
            viewPager2 = findViewById(R.id.viewpager);

            // Passa l'URI come Uri, non come String
            viewPagerAdapter = new ViewPagerAdapter(this, diaryName, startDate, endDate, coverImageUri);
            viewPager2.setAdapter(viewPagerAdapter);
            rootLayoutDiary = findViewById(R.id.rootLayoutDiary);

            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            ActionBar actionBar = getSupportActionBar();

            // TabLayout listener for ViewPager2 synchronization
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager2.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    // No need to implement
                }
              
              @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    // No need to implement
                }
            });

            // Sync ViewPager2 with TabLayout
            viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    tabLayout.getTabAt(position).select();
                }
            });
        }

    public void setViewPagerSwipeEnabled(boolean enabled) {
        viewPager2.setUserInputEnabled(enabled);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.baseline_account_circle_24));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_home) {
            Intent intent = new Intent(DiaryActivity.this, HomepageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        View buttonAccount = toolbar.findViewById(R.id.action_account);

        if (id == R.id.action_account) {
            // Create a PopupMenu for account actions
            PopupMenu popupMenu = new PopupMenu(DiaryActivity.this, buttonAccount);
            popupMenu.getMenuInflater().inflate(R.menu.menu_account, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.action_logout) {
                        Intent intent = new Intent(DiaryActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish(); // Finish DiaryActivity
                        return true;
                    }
                    return false;
                }
            });

            popupMenu.show();
        }
      
            if (id == android.R.id.home){
            //inserire intent per andare alla SetingsActivity
            }

            return super.onOptionsItemSelected(item);
        }
    }
