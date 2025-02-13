package com.unimib.triptales.ui.settings;

import static com.unimib.triptales.util.Constants.ACTIVE_FRAGMENT_TAG;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.unimib.triptales.R;
import com.unimib.triptales.ui.homepage.HomepageActivity;
import com.unimib.triptales.ui.login.LoginActivity;
import com.unimib.triptales.util.SharedPreferencesUtils;

public class SettingsActivity extends AppCompatActivity {

    private NavController navController;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_settings);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        toolbar = findViewById(R.id.settingsToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController != null && navController.navigateUp() || super.onSupportNavigateUp();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        View buttonAccount = toolbar.findViewById(R.id.action_account);


        if (id == android.R.id.home) {
            if (navController.getCurrentDestination().getId() == R.id.settingsFragment) {
                Intent resultIntent = getIntent();
                String fragmentTag = resultIntent.getStringExtra(ACTIVE_FRAGMENT_TAG);

                Intent intent = new Intent(SettingsActivity.this, HomepageActivity.class);
                intent.putExtra("fromSettings", true);
                intent.putExtra(ACTIVE_FRAGMENT_TAG, fragmentTag);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else {
                navController.navigateUp();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
