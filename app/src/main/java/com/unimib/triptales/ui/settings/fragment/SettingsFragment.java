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
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        applySavedLanguage();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflazione del layout
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Trova il riferimento al ViewPager2 nel tuo layout
        viewPager2 = getActivity().findViewById(R.id.settingsSlider); // Assicurati che l'ID corrisponda

        // Trova il bottone Privacy
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

        // Associa lo stato iniziale dello switch
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
                // Crea il PopupMenu
                PopupMenu popupMenu = new PopupMenu(getActivity(), LinguaButton);

                // Inflates il menu dal file XML
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.lingua_menu, popupMenu.getMenu());

                // Gestisci l'evento di selezione di un item dal menu
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.action_italiano) {
                            changeLanguage("it"); // Cambia la lingua a italiano
                            return true;
                        } else if (id == R.id.action_inglese) {
                            changeLanguage("en"); // Cambia la lingua a inglese
                            return true;
                        }
                        return false;
                    }
                });

                // Mostra il menu
                popupMenu.show();
            }
        });



        //ACCOUNT
        AccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crea il PopupMenu
                PopupMenu popupMenu = new PopupMenu(getActivity(), AccountButton);

                // Inflates il menu dal file XML
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.account_menu, popupMenu.getMenu());

                // Gestisci l'evento di selezione di un item dal menu
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.action_modifica_email) {
                            viewPager2.setCurrentItem(2, true);
                            return true;
                        } else if (id == R.id.action_modifica_password) {
                            viewPager2.setCurrentItem(3, true);
                            return true;
                        }else if (id == R.id.action_elimina_profilo) {

                            return true;
                        }
                        return false;
                    }
                });

                // Mostra il menu
                popupMenu.show();
            }
        });




        //BOTTONE PER ANDARE AL PRIVACYFRAGMENT
        // Aggiungi il listener per il bottone
        PrivacyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Imposta la pagina corrente a quella desiderata
                viewPager2.setCurrentItem(1, true); // Supponendo che il PrivacyFragment sia alla posizione 1
            }
        });

        AboutUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Imposta la pagina corrente a quella desiderata
                viewPager2.setCurrentItem(4, true); // Supponendo che il PrivacyFragment sia alla posizione 1
            }
        });

        return view;
    }

    // Funzione per cambiare la lingua
    private void changeLanguage(String languageCode) {
        // Cambia la lingua dell'app qui
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = getActivity().getResources().getConfiguration();
        config.setLocale(locale);
        getActivity().getResources().updateConfiguration(config, getActivity().getResources().getDisplayMetrics());

        // Salva la lingua preferita in SharedPreferences
        SharedPreferences preferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("language", languageCode);
        editor.apply();

        // Aggiorna l'interfaccia utente o ricarica l'attività
        getActivity().recreate(); // Ricarica l'attività per applicare la nuova lingua
    }

    private void applySavedLanguage() {
        SharedPreferences preferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String languageCode = preferences.getString("language", null); // Controlla se una lingua è già salvata

        if (languageCode == null) {
            // Nessuna lingua salvata, imposta l'italiano come default
            languageCode = "it";
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("language", languageCode);
            editor.apply();
        }

        // Applica la lingua salvata o quella di default
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