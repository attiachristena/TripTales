
package com.unimib.triptales.ui.homepage;

import static com.unimib.triptales.util.Constants.ACTIVE_FRAGMENT_TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.unimib.triptales.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.unimib.triptales.repository.diary.IDiaryRepository;
import com.unimib.triptales.ui.diary.viewmodel.ViewModelFactory;
import com.unimib.triptales.ui.homepage.fragment.CalendarFragment;
import com.unimib.triptales.ui.homepage.fragment.HomeFragment;
import com.unimib.triptales.ui.homepage.fragment.MapFragment;
import com.unimib.triptales.ui.homepage.viewmodel.HomeViewModel;
import com.unimib.triptales.ui.login.LoginActivity;
import com.unimib.triptales.ui.settings.SettingsActivity;
import com.unimib.triptales.util.ServiceLocator;
import com.unimib.triptales.util.SharedPreferencesUtils;

import java.util.Objects;


public class HomepageActivity extends AppCompatActivity {

    private Fragment homeFragment;
    private Fragment mapFragment;
    private Fragment calendarFragment;
    private Fragment activeFragment;
    private BottomNavigationView bottomNavigationView;
    private HomeViewModel homeViewModel;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        IDiaryRepository diaryRepository = ServiceLocator.getINSTANCE().getDiaryRepository(getApplicationContext());
        homeViewModel = new ViewModelProvider(this,
                new ViewModelFactory(diaryRepository)).get(HomeViewModel.class);

        homeViewModel.loadRemoteDiaries();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        homeFragment = new HomeFragment();
        mapFragment = new MapFragment();
        calendarFragment = new CalendarFragment();

        if (savedInstanceState != null) {
            String activeTag = savedInstanceState.getString(ACTIVE_FRAGMENT_TAG);
            activeFragment = getSupportFragmentManager().findFragmentByTag(activeTag);
            if (activeFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .show(activeFragment)
                        .commit();
            }
            updateBottomNavigation();
        } else {
            Intent intent = getIntent();
            boolean fromSettings = intent.getBooleanExtra("fromSettings", false);

            if (fromSettings) {
                String lastFragmentTag = intent.getStringExtra(ACTIVE_FRAGMENT_TAG);
                if (lastFragmentTag != null) {
                    activeFragment = getSupportFragmentManager().findFragmentByTag(lastFragmentTag);
                }
                if (activeFragment == null) {
                    switch (Objects.requireNonNull(lastFragmentTag)) {
                        case "CALENDAR":
                            activeFragment = new CalendarFragment();
                            break;
                        case "MAP":
                            activeFragment = new MapFragment();
                            break;
                        default:
                            activeFragment = homeFragment;
                            break;
                    }
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, activeFragment, lastFragmentTag)
                        .commit();
                updateBottomNavigation();
            } else {
                activeFragment = homeFragment;
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, homeFragment, "HOME")
                        .commit();
            }
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.homeFragment) {
                switchFragment(homeFragment, "HOME");
            } else if (itemId == R.id.mapFragment) {
                switchFragment(mapFragment, "MAP");
            } else if (itemId == R.id.calendarFragment) {
                switchFragment(calendarFragment, "CALENDAR");
            }
            return true;
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        homeViewModel.loadRemoteDiaries();
    }

    private void switchFragment(Fragment fragment, String tag){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(!fragment.isAdded()){
            transaction.hide(activeFragment)
                    .add(R.id.fragment_container, fragment, tag);
        }else{
            transaction.hide(activeFragment).show(fragment);
        }
        activeFragment = fragment;
        transaction.commit();
    }

    private void updateBottomNavigation(){
        if (activeFragment instanceof HomeFragment) {
            bottomNavigationView.setSelectedItemId(R.id.homeFragment);
        }else if (activeFragment instanceof MapFragment) {
            bottomNavigationView.setSelectedItemId(R.id.mapFragment);
        } else if (activeFragment instanceof CalendarFragment) {
            bottomNavigationView.setSelectedItemId(R.id.calendarFragment);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(activeFragment != null){
            String tag = activeFragment.getTag();
            outState.putString(ACTIVE_FRAGMENT_TAG, tag);
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            Intent intent = new Intent(HomepageActivity.this, SettingsActivity.class);
            if (activeFragment != null && activeFragment.getTag() != null) {
                intent.putExtra(ACTIVE_FRAGMENT_TAG, activeFragment.getTag());
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
