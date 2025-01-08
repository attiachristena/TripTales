
package com.unimib.triptales.ui.homepage;

import static com.unimib.triptales.util.Constants.ACTIVE_FRAGMENT_TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationBarView;
import com.unimib.triptales.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.unimib.triptales.ui.homepage.fragment.CalendarFragment;
import com.unimib.triptales.ui.homepage.fragment.HomeFragment;
import com.unimib.triptales.ui.homepage.fragment.MapFragment;
import com.unimib.triptales.ui.login.LoginActivity;
import com.unimib.triptales.ui.settings.SettingsActivity;

import java.util.Map;
import java.util.Objects;


public class HomepageActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Fragment homeFragment;
    private Fragment mapFragment;
    private Fragment calendarFragment;
    private Fragment activeFragment;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        homeFragment = new HomeFragment();
        mapFragment = new MapFragment();
        calendarFragment = new CalendarFragment();

        if (savedInstanceState != null) {
            // Ripristina il fragment attivo dal salvataggio dello stato
            String activeTag = savedInstanceState.getString(ACTIVE_FRAGMENT_TAG);
            activeFragment = getSupportFragmentManager().findFragmentByTag(activeTag);
            if (activeFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .show(activeFragment)
                        .commit();
            }
        } else {
            Intent intent = getIntent();
            boolean fromSettings = intent.getBooleanExtra("fromSettings", false);

            if (fromSettings) {
                // Ripristina il fragment attivo dal tag passato nell'Intent
                String lastFragmentTag = intent.getStringExtra(ACTIVE_FRAGMENT_TAG);
                if (lastFragmentTag != null) {
                    activeFragment = getSupportFragmentManager().findFragmentByTag(lastFragmentTag);
                }
                if (activeFragment == null) {
                    // Se il fragment non esiste, creane uno nuovo
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
            } else {
                // Caso iniziale: carica il frammento Home
                activeFragment = homeFragment;
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, homeFragment, "HOME")
                        .commit();
            }
        }
        
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.page_1) {
                switchFragment(homeFragment, "HOME");
            } else if (itemId == R.id.page_2) {
                switchFragment(mapFragment, "MAP");
            } else if (itemId == R.id.page_3) {
                switchFragment(calendarFragment, "CALENDAR");
            }
            return true;
        });

    }

    private void switchFragment(Fragment fragment, String tag){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(fragment instanceof MapFragment){
            Fragment existingMapFragment = getSupportFragmentManager().findFragmentByTag(tag);
            if(existingMapFragment != null){
                transaction.remove(existingMapFragment);
            }
            mapFragment = new MapFragment();
            transaction.hide(activeFragment).add(R.id.fragment_container, mapFragment, tag);
            activeFragment = mapFragment;
            transaction.commit();
        }else{
            //per non creare nuova la mappa basta togliere l'if sopra e lasciare solo contenuto else!
            if(!fragment.isAdded()){
                transaction.hide(activeFragment)
                        .add(R.id.fragment_container, fragment, tag);
            }else{
                transaction.hide(activeFragment).show(fragment);
            }
            activeFragment = fragment;
            transaction.commit();
        }
    }

    private void restoreFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(fragment != null){
            transaction.show(fragment);
            activeFragment = fragment;
        }else{
            transaction.add(R.id.fragment_container, homeFragment, "HOME");
            activeFragment = homeFragment;
        }
        transaction.commit();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.baseline_account_circle_24));
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem hideItem = menu.findItem(R.id.action_home);
        hideItem.setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        View buttonAccount = toolbar.findViewById(R.id.action_account);

        if (id == R.id.action_account){
            PopupMenu popupMenu = new PopupMenu(HomepageActivity.this, buttonAccount);
            popupMenu.getMenuInflater().inflate(R.menu.menu_account, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.action_logout) {
                        Intent intent = new Intent(HomepageActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        return true;
                    }
                    return false;
                }
            });

            popupMenu.show();
        }

        if (id == android.R.id.home){
            //inserire intent per andare alla SetingsActivity
            Intent intent = new Intent(HomepageActivity.this, SettingsActivity.class);
            intent.putExtra(ACTIVE_FRAGMENT_TAG, activeFragment.getTag());
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
