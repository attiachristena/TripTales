package com.unimib.triptales.ui.settings;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;


import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.unimib.triptales.R;
import com.unimib.triptales.adapters.SettingsAdapter;
import com.unimib.triptales.ui.homepage.HomepageActivity;
import com.unimib.triptales.ui.login.LoginActivity;
import com.unimib.triptales.ui.settings.fragment.SettingsFragment;




public class SettingsActivity extends AppCompatActivity {


    ViewPager2 viewPager2;
    SettingsAdapter viewPagerAdapter;
    //LinearLayout rootLayoutDiary;
    Toolbar toolbar;
    //BottomNavigationView bottom_navigation;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();


        //bottom_navigation = findViewById(R.id.bottom_navigation);
        viewPager2 = findViewById(R.id.settingsSlider);


        viewPagerAdapter = new SettingsAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);
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


    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();


        View buttonAccount = toolbar.findViewById(R.id.action_account);


        if (id == R.id.action_account){
            PopupMenu popupMenu = new PopupMenu(SettingsActivity.this, buttonAccount);
            popupMenu.getMenuInflater().inflate(R.menu.menu_account, popupMenu.getMenu());


            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.action_logout) {
                        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
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


        if (id == android.R.id.home) {
            int posizione_vp2 = viewPager2.getCurrentItem();

            if(posizione_vp2==0){
                Intent intent = new Intent(SettingsActivity.this, HomepageActivity.class);
                startActivity(intent);
                return true;
            }else{
                viewPager2.setCurrentItem(0,false);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);

    }
}
