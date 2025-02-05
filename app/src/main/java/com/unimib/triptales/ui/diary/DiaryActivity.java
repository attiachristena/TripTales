package com.unimib.triptales.ui.diary;

import static com.unimib.triptales.util.Constants.ACTIVE_FRAGMENT_TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.unimib.triptales.R;
import com.unimib.triptales.adapters.ViewPagerAdapter;
import com.unimib.triptales.repository.expense.IExpenseRepository;
import com.unimib.triptales.repository.goal.IGoalRepository;
import com.unimib.triptales.repository.task.ITaskRepository;
import com.unimib.triptales.ui.diary.viewmodel.ExpenseViewModel;
import com.unimib.triptales.ui.diary.viewmodel.GoalViewModel;
import com.unimib.triptales.ui.diary.viewmodel.TaskViewModel;
import com.unimib.triptales.ui.diary.viewmodel.ViewModelFactory;
import com.unimib.triptales.ui.homepage.HomepageActivity;
import com.unimib.triptales.ui.login.LoginActivity;
import com.unimib.triptales.ui.settings.SettingsActivity;
import com.unimib.triptales.util.ServiceLocator;
import com.unimib.triptales.util.SharedPreferencesUtils;

import org.w3c.dom.Text;

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

        IExpenseRepository expenseRepository = ServiceLocator.getINSTANCE().getExpenseRepository(getApplicationContext());
        ExpenseViewModel expenseViewModel = new ViewModelProvider(this,
                new ViewModelFactory(expenseRepository)).get(ExpenseViewModel.class);
        expenseViewModel.fetchAllExpenses();

        IGoalRepository goalRepository = ServiceLocator.getINSTANCE().getGoalRepository(getApplicationContext());
        GoalViewModel goalViewModel = new ViewModelProvider(this,
                new ViewModelFactory(goalRepository)).get(GoalViewModel.class);
        goalViewModel.fetchAllGoals();

        ITaskRepository taskRepository = ServiceLocator.getINSTANCE().getTaskRepository(getApplicationContext());
        TaskViewModel taskViewModel = new ViewModelProvider(this,
                new ViewModelFactory(taskRepository)).get(TaskViewModel.class);
        taskViewModel.fetchAllTasks();

        // Recupera i dati dall'Intent
        Intent intent = getIntent();

        if (intent != null) {
            diaryName = intent.getStringExtra("diaryName");
            startDate = intent.getStringExtra("startDate");
            endDate = intent.getStringExtra("endDate");

            // Se coverImageUri è una String, convertila in Uri
            String coverImageUriString = intent.getStringExtra("coverImageUri");
            if (coverImageUriString != null) {
                coverImageUri = Uri.parse(coverImageUriString);
            }
        }

        // Set up the ViewPager2 and TabLayout (after the fragment setup)
        tabLayout = findViewById(R.id.tablayout);
        viewPager2 = findViewById(R.id.viewpager);

        // Passa l'URI come Uri, non come String
        viewPagerAdapter = new ViewPagerAdapter(this, diaryName, startDate, endDate, coverImageUri);
        viewPager2.setAdapter(viewPagerAdapter);
        rootLayoutDiary = findViewById(R.id.rootLayoutDiary);

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            View customView = LayoutInflater.from(this).inflate(R.layout.tab_custom, null);
            TextView tabText = customView.findViewById(R.id.tabText);

            if (position == 0) tabText.setText("Tappe");
            else if (position == 1) tabText.setText("Spese");
            else if (position == 2) tabText.setText("Obiettivi");
            else tabText.setText("Attività");

            tab.setCustomView(customView);
        }).attach();

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

        TextView diaryNameTextView = findViewById(R.id.diaryName);
        diaryNameTextView.setText(diaryName);
        ImageButton diaryBackButton = findViewById(R.id.backButtonDiary);
        diaryBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
            SharedPreferencesUtils.clearDiaryId(getApplicationContext());
            Intent intent = new Intent(DiaryActivity.this, HomepageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
            return true;
        }

        View buttonAccount = toolbar.findViewById(R.id.action_account);

        if (id == R.id.action_account) {
            PopupMenu popupMenu = new PopupMenu(DiaryActivity.this, buttonAccount);
            popupMenu.getMenuInflater().inflate(R.menu.menu_account, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.action_logout) {
                        Intent intent = new Intent(DiaryActivity.this, LoginActivity.class);
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
            Intent intent = new Intent(DiaryActivity.this, SettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
