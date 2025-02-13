package com.unimib.triptales.ui.settings.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import com.unimib.triptales.R;
import com.unimib.triptales.ui.login.LoginActivity;
import com.unimib.triptales.util.SharedPreferencesUtils;

public class SettingsFragment extends Fragment {

    ImageButton LinguaButton, AccountButton, AboutUsButton, LogoutButton;
    Button ModificaProfiloButton;
    SwitchCompat switchNightMode;
    boolean nightMode;
    boolean oldNightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private FirebaseAuth firebaseAuth;
    private NavController navController;
    private android.app.AlertDialog loadingDialog;

    public SettingsFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        SharedPreferencesUtils.applyNightMode(requireContext());
        SharedPreferencesUtils.applyLanguage(requireContext());
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);


        // Inizializza il NavController
        navController = NavHostFragment.findNavController(this);


        LinguaButton = view.findViewById(R.id.LinguaButton);
        AccountButton = view.findViewById(R.id.AccountButton);
        AboutUsButton = view.findViewById(R.id.AboutUsButton);
        LogoutButton = view.findViewById(R.id.LogoutButton);
        ModificaProfiloButton = view.findViewById(R.id.ModificaProfiloButton);
        switchNightMode = view.findViewById(R.id.switchNightMode);
        TextView nomeCognomeTextView = view.findViewById(R.id.nome_cognome);

        sharedPreferences = getActivity().getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMode = SharedPreferencesUtils.isNightModeEnabled(requireContext());

        // Recupera nome e cognome utente da Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String surname = dataSnapshot.child("surname").getValue(String.class);
                    nomeCognomeTextView.setText(name + " " + surname);
                } else {
                    nomeCognomeTextView.setText(getString(R.string.dati_non_disponibili));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                nomeCognomeTextView.setText(getString(R.string.errore_nel_recupero_dati));
            }
        });


        // Attiva o disattiva la modalità notturna
        nightMode = SharedPreferencesUtils.isNightModeEnabled(requireContext());
        switchNightMode.setChecked(nightMode);

        switchNightMode.setOnClickListener(view1 -> {
            nightMode = !nightMode;
            SharedPreferencesUtils.saveNightMode(requireContext(), nightMode);
            SharedPreferencesUtils.applyNightMode(requireContext());
            requireActivity().recreate();
        });

        // Cambiare lingua con popup menu
        LinguaButton.setOnClickListener(v -> {

            PopupMenu popupMenu = new PopupMenu(getActivity(), LinguaButton);
            popupMenu.getMenuInflater().inflate(R.menu.lingua_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();

                showLoadingDialog();

                if (id == R.id.action_italiano) {
                    changeLanguage("it");
                    return true;
                } else if (id == R.id.action_inglese) {
                    changeLanguage("en");
                    return true;
                }
                return false;
            });
            hideLoadingDialog();
            popupMenu.show();
        });

        // Navigazione con Navigation Component
        AboutUsButton.setOnClickListener(v -> navController.navigate(R.id.action_settings_to_aboutUs));
        ModificaProfiloButton.setOnClickListener(v -> navController.navigate(R.id.action_settings_to_edit_profile));

        // Menu Account con navigazione
        AccountButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getActivity(), AccountButton);
            popupMenu.getMenuInflater().inflate(R.menu.account_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_modifica_password) {
                    navController.navigate(R.id.action_settings_to_change_password);
                    return true;
                } else if (id == R.id.action_elimina_profilo) {
                    confirmAndDeleteAccount();
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });

        LogoutButton.setOnClickListener(v -> showLogoutDialog());

        return view;
    }

    // Cambio lingua
    // Cambio lingua
    private void changeLanguage(String languageCode) {
        SharedPreferencesUtils.saveLanguage(requireContext(), languageCode);
        SharedPreferencesUtils.applyLanguage(requireContext());
        requireActivity().recreate();
    }


    // Eliminazione account con conferma
    private void confirmAndDeleteAccount() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.elimina_account))
                    .setMessage(getString(R.string.dialog_elimina_account))
                    .setPositiveButton(getString(R.string.elimina), (dialog, which) -> performDelete())
                    .setNegativeButton(getString(R.string.annulla), null)
                    .show();
        } else {
            Toast.makeText(getContext(), getString(R.string.nessun_utente_trovato), Toast.LENGTH_SHORT).show();
        }
    }

    private void performDelete(){
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String userId = currentUser.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        userRef.removeValue().addOnCompleteListener(task -> {
            currentUser.delete().addOnCompleteListener(task2 -> {
                if (task2.isSuccessful()) {
                    Toast.makeText(getContext(), getString(R.string.eliminazione_con_successo), Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), getString(R.string.errore_eliminazione), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    //Dialog per uscire dall'account
    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.conferma_logout))
                .setMessage(R.string.conferma_uscire_da_account)
                .setPositiveButton(getString(R.string.esci), (dialog, which) -> performLogout())
                .setNegativeButton(getString(R.string.annulla), (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void performLogout() {
        SharedPreferencesUtils.setLoggedIn(requireContext(), false);

        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        requireActivity().finish();
    }

    private void showLoadingDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setView(R.layout.dialog_loading);
        builder.setCancelable(false);
        loadingDialog = builder.create();
        loadingDialog.show();
    }

    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }
}
