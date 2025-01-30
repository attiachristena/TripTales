package com.unimib.triptales.ui.settings.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.os.LocaleListCompat;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unimib.triptales.R;
import com.unimib.triptales.ui.login.LoginActivity;
import com.google.firebase.database.*;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    ImageButton PrivacyButton, LinguaButton, AccountButton, AboutUsButton;
    Button ModificaProfiloButton;
    ViewPager2 viewPager2;
    SwitchCompat switchNightMode;
    boolean nightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private FirebaseAuth firebaseAuth;

    public SettingsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        applySavedLanguage();
        firebaseAuth = FirebaseAuth.getInstance();
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
        ModificaProfiloButton = view.findViewById(R.id.ModificaProfiloButton);
        switchNightMode = view.findViewById(R.id.switchNightMode);
        TextView nomeCognomeTextView = view.findViewById(R.id.nome_cognome);

        sharedPreferences = getActivity().getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMode =sharedPreferences.getBoolean("nightMode", false);

        //NOME E COGNOME UTENTE
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("Users");

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String surname = dataSnapshot.child("surname").getValue(String.class);
                    nomeCognomeTextView.setText(name + " " + surname);
                } else {
                    nomeCognomeTextView.setText("Dati non disponibili");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                nomeCognomeTextView.setText("Errore nel recupero dei dati");
            }
        });



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
                            confirmAndDeleteAccount();
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
                viewPager2.setCurrentItem(1, false);
            }
        });

        AboutUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager2.setCurrentItem(4, false);
            }
        });

        ModificaProfiloButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager2.setCurrentItem(6, false);
            }
        });


        return view;
    }



    //CAMBIO LINGUA
    private void changeLanguage(String languageCode) {
        SharedPreferences preferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("language", languageCode);
        editor.apply();

        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode));

        getActivity().recreate();
    }

    private void applySavedLanguage() {
        SharedPreferences preferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String languageCode = preferences.getString("language", "it");

        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode));
    }


    //ELIMINARE ACCOUNT
    private void confirmAndDeleteAccount() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.elimina_account))
                    .setMessage(getString(R.string.dialog_elimina_account))
                    .setPositiveButton(getString(R.string.elimina), (dialog, which) -> {
                        currentUser.delete().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), getString(R.string.eliminazione_con_successo), Toast.LENGTH_SHORT).show();
                                firebaseAuth.signOut();
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getContext(), getString(R.string.errore_eliminazione), Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton(getString(R.string.annulla), null)
                    .show();
        } else {
            Toast.makeText(getContext(), getString(R.string.nessun_utente_trovato), Toast.LENGTH_SHORT).show();
        }
    }



}