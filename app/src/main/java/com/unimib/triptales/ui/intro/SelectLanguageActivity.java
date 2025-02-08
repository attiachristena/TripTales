package com.unimib.triptales.ui.intro;

import static android.app.PendingIntent.getActivity;

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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.os.LocaleListCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.unimib.triptales.R;


public class SelectLanguageActivity extends AppCompatActivity {

    String[] item = {"English", "Italiano"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;

    Button nextButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applySavedLanguage();
        setContentView(R.layout.activity_select_language);

        nextButton = findViewById(R.id.nextButton);
        //SharedPreferences preferences = getSharedPreferences("PREFERENCES", MODE_PRIVATE);
        //String FirstTime = preferences.getString("FirstTimeInstall","");

        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item_intro, item);
        autoCompleteTextView.setAdapter(adapterItems);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(SelectLanguageActivity.this, item, Toast.LENGTH_SHORT).show();

                if (item.equals(getString(R.string.italiano))) {
                    changeLanguage("it");
                } else if (item.equals(getString(R.string.inglese))) {
                    changeLanguage("en");
                }

            }
        });


        nextButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(SelectLanguageActivity.this, IntroActivity.class);
                startActivity(intent);
                finish();

                //SharedPreferences.Editor editor = preferences.edit();
                //editor.putString("FirstTimeInstall", "Yes");
                //editor.apply();
            }
        });
    }

    private void changeLanguage(String languageCode) {
        SharedPreferences preferences = this.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("language", languageCode);
        editor.apply();

        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode));
        recreate();

    }

    private void applySavedLanguage() {
        SharedPreferences preferences = this.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String languageCode = preferences.getString("language", "it");

        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode));
    }
}