package com.unimib.triptales.ui.settings.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.unimib.triptales.R;

import java.util.Locale;


public class SettingsFragment extends Fragment {

    ImageButton PrivacyButton, LinguaButton, AccountButton, AboutUsButton;
    ViewPager2 viewPager2;
    SwitchCompat switchNightMode;
    boolean nightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public SettingsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        applySavedLanguage();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        viewPager2 = getActivity().findViewById(R.id.settingsSlider);

        PrivacyButton = view.findViewById(R.id.PrivacyButton);
        LinguaButton = view.findViewById(R.id.LinguaButton);
        AccountButton = view.findViewById(R.id.AccountButton);
        AboutUsButton = view.findViewById(R.id.AboutUsButton);
        switchNightMode = view.findViewById(R.id.switchNightMode);

        sharedPreferences = getActivity().getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMode =sharedPreferences.getBoolean("nightMode", false);

        //SWITCH PER MODALITA' NOTTURNA
        if(nightMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        switchNightMode.setChecked(nightMode);

        switchNightMode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(nightMode){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("nightMode", false);
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("nightMode", true);
                }
                editor.apply();
            }
        });

        //CAMBIARE LINGUA + POP UP MENU
        LinguaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), LinguaButton);

                MenuInflater inflater = popupMenu.getMenuInflater();
                popupMenu.getMenuInflater().inflate(R.menu.lingua_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.action_italiano) {
                            changeLanguage("it");
                            return true;
                        } else if (id == R.id.action_inglese) {
                            changeLanguage("en");
                            return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });




        //ACCOUNT
        AccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), AccountButton);

                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.account_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.action_modifica_email) {
                            viewPager2.setCurrentItem(2, false);
                            return true;
                        } else if (id == R.id.action_modifica_password) {
                            viewPager2.setCurrentItem(3, false);
                            return true;
                        }else if (id == R.id.action_elimina_profilo) {

                            return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });




        //BOTTONE PER ANDARE AL PRIVACYFRAGMENT

        PrivacyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Imposta la pagina corrente a quella desiderata
                viewPager2.setCurrentItem(1, false); // Supponendo che il PrivacyFragment sia alla posizione 1
            }
        });

        AboutUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager2.setCurrentItem(4, false); // Supponendo che il PrivacyFragment sia alla posizione 1
            }
        });

        return view;
    }

    //CAMBIO LINGUA
    private void changeLanguage(String languageCode) {

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = getActivity().getResources().getConfiguration();
        config.setLocale(locale);
        getActivity().getResources().updateConfiguration(config, getActivity().getResources().getDisplayMetrics());

        SharedPreferences preferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("language", languageCode);
        editor.apply();

        getActivity().recreate();
    }

    private void applySavedLanguage() {
        SharedPreferences preferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String languageCode = preferences.getString("language", null); // Controlla se una lingua è già salvata

        if (languageCode == null) {

            languageCode = "it";
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("language", languageCode);
            editor.apply();
        }

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = getActivity().getResources().getConfiguration();
        config.setLocale(locale);
        getActivity().getResources().updateConfiguration(config, getActivity().getResources().getDisplayMetrics());
    }

    /*private int getItem(int i){
        return settingsSlider.getCurrentItem() + i;
    }*/
}