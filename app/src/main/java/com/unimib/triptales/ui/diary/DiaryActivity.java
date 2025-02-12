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
import com.unimib.triptales.repository.checkpointDiary.ICheckpointDiaryRepository;
import com.unimib.triptales.repository.expense.IExpenseRepository;
import com.unimib.triptales.repository.goal.IGoalRepository;
import com.unimib.triptales.repository.imageCardItem.IImageCardItemRepository;
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

import java.util.Objects;

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

        ICheckpointDiaryRepository checkpointDiaryRepository = ServiceLocator.getINSTANCE().getCheckpointDiaryRepository(getApplicationContext());
        TaskViewModel checkpointDiaryViewModel = new ViewModelProvider(this,
                new ViewModelFactory(checkpointDiaryRepository)).get(TaskViewModel.class);
        checkpointDiaryViewModel.fetchAllTasks();



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

            if (position == 0) tabText.setText(R.string.tabTappe);
            else if (position == 1) tabText.setText(R.string.tabSpese);
            else if (position == 2) tabText.setText(R.string.tabObiettivi);
            else tabText.setText(R.string.tabCheckList);

            tab.setCustomView(customView);
        }).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Objects.requireNonNull(tabLayout.getTabAt(position)).select();
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
}
