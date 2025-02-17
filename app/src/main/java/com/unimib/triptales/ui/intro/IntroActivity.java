package com.unimib.triptales.ui.intro;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.unimib.triptales.R;
import com.unimib.triptales.adapters.OnBoardingAdapter;
import com.unimib.triptales.ui.login.LoginActivity;

public class IntroActivity extends AppCompatActivity {

    ViewPager slideViewPager;
    LinearLayout dotIndicator;
    OnBoardingAdapter viewPagerAdapter;
    Button backButton, nextButton, startButton, backButtonToActivity;
    TextView[] dots;

    ViewPager.OnPageChangeListener viewPagerListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {
            setDotIndicator(position);

            if(position == 0){
                backButtonToActivity.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.VISIBLE);
                startButton.setVisibility(View.INVISIBLE);
                backButton.setVisibility(View.INVISIBLE);
            } else if (position == 1) {
                backButtonToActivity.setVisibility(View.INVISIBLE);
                backButton.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.VISIBLE);
                startButton.setVisibility(View.INVISIBLE);
            } else if (position == 2) {
                backButtonToActivity.setVisibility(View.INVISIBLE);
                backButton.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.INVISIBLE);
                startButton.setVisibility(View.VISIBLE);
            }




            if(position == 0){
                nextButton.setBackgroundColor(getResources().getColor(R.color.light_brown3,getApplicationContext().getTheme()));
                backButton.setBackgroundColor(getResources().getColor(R.color.light_brown3,getApplicationContext().getTheme()));

            } else if (position == 1) {
                nextButton.setBackgroundColor(getResources().getColor(R.color.light_brown2,getApplicationContext().getTheme()));
                backButton.setBackgroundColor(getResources().getColor(R.color.light_brown2,getApplicationContext().getTheme()));

            } else if (position == 2) {
                nextButton.setBackgroundColor(getResources().getColor(R.color.orange2,getApplicationContext().getTheme()));
                backButton.setBackgroundColor(getResources().getColor(R.color.orange2,getApplicationContext().getTheme()));
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        backButton = findViewById(R.id.backButton);
        nextButton = findViewById(R.id.nextButton);
        startButton = findViewById(R.id.startButton);
        backButtonToActivity = findViewById(R.id.backButtonToActivity);

        slideViewPager = findViewById(R.id.slideViewPager);
        dotIndicator = findViewById(R.id.dotIndicator);
        viewPagerAdapter = new OnBoardingAdapter(this);
        slideViewPager.setAdapter(viewPagerAdapter);
        setDotIndicator(0);
        slideViewPager.addOnPageChangeListener(viewPagerListener);

        backButtonToActivity.setVisibility(View.VISIBLE);

        SharedPreferences preferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
        String FirstTime = preferences.getString("FirstTimeInstall","");

        backButton.setOnClickListener(view -> {
            if (getItem(0) > 0) {
                slideViewPager.setCurrentItem(getItem(-1), true);
            }
        });

        nextButton.setOnClickListener(view -> {
            if(getItem(0)<2){
                slideViewPager.setCurrentItem(getItem(1), true);
            }
        });

        backButtonToActivity.setOnClickListener(view -> {
            if(getItem(0) == 0) {
                Intent intent = new Intent(IntroActivity.this, SelectLanguageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        startButton.setOnClickListener(view -> {
            if(getItem(0) == 2){
                Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("FirstTimeInstall", "Yes");
                editor.putString("FirstTimeLanguage", "Yes");
                editor.apply();
            }
        });

        if(FirstTime.equals("Yes")){
            Intent intent = new Intent (IntroActivity.this, LoginActivity.class);
            startActivity(intent);
        }

    }

    public void setDotIndicator(int position){
        dots = new TextView[3];
        dotIndicator.removeAllViews();

        for(int i=0; i < dots.length; i++ ){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226", Html.FROM_HTML_MODE_LEGACY));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.light_brown3, getApplicationContext().getTheme()));
            dotIndicator.addView(dots[i]);
        }
        dots[position].setTextColor(getResources().getColor(R.color.light_brown2,getApplicationContext().getTheme()));


        if (position==2) {
            for(int i=0; i < dots.length; i++ ){
                dots[i].setTextColor(getResources().getColor(R.color.light_orange3, getApplicationContext().getTheme()));
            }
            dots[2].setTextColor(getResources().getColor(R.color.orange,getApplicationContext().getTheme()));
        }

    }

    private int getItem(int i){
        return slideViewPager.getCurrentItem() + i;
    }


}