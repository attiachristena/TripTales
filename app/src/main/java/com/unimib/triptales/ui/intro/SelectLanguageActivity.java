package com.unimib.triptales.ui.intro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import com.unimib.triptales.R;

public class SelectLanguageActivity extends AppCompatActivity {

    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;
    Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        applySavedLanguage();

        setContentView(R.layout.activity_select_language);

        nextButton = findViewById(R.id.nextButton);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);

        setupLanguageDropdown();

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(SelectLanguageActivity.this, selectedItem, Toast.LENGTH_SHORT).show();

                if (selectedItem.equals(getString(R.string.italiano))) {
                    changeLanguage("it");
                } else if (selectedItem.equals(getString(R.string.inglese))) {
                    changeLanguage("en");
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectLanguageActivity.this, IntroActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupLanguageDropdown() {
        String[] items = {getString(R.string.inglese), getString(R.string.italiano)};
        adapterItems = new ArrayAdapter<>(this, R.layout.list_item_intro, items);
        autoCompleteTextView.setAdapter(adapterItems);
    }

    private void changeLanguage(String languageCode) {
        SharedPreferences preferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("language", languageCode);
        editor.apply();

        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode));

        recreate();
    }

    private void applySavedLanguage() {
        SharedPreferences preferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String languageCode = preferences.getString("language", "it"); // "it" è la lingua predefinita

        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupLanguageDropdown();
    }
}
