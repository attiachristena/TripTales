package com.unimib.triptales.ui.homepage.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.unimib.triptales.R;
import com.unimib.triptales.ui.diario.fragment.TappeFragment;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        FloatingActionButton fab = view.findViewById(R.id.fab_add_diary);

        // Azioni quando si preme il pulsante "+"
        fab.setOnClickListener(v -> {
            // Visualizza l'alert dialog personalizzato
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.activity_dialog_add_diary, null);

            // Trova gli elementi del layout personalizzato
            EditText editDiaryName = dialogView.findViewById(R.id.diary_name);
            Button btnAdd = dialogView.findViewById(R.id.btn_add);
            Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

            // Crea il dialog
            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setView(dialogView)
                    .setCancelable(true)
                    .create();

            // Imposta il comportamento del pulsante "Aggiungi"
            btnAdd.setOnClickListener(v1 -> {
                String diaryName = editDiaryName.getText().toString().trim();
                if (!diaryName.isEmpty()) {

                    // Passa il nome del diario al TappeFragment con un Bundle
                    TappeFragment tappeFragment = new TappeFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("diary_name", diaryName);
                    tappeFragment.setArguments(bundle);

                    // Sostituisci il Fragment corrente con il TappeFragment
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, tappeFragment) // Assicurati che 'fragment_container' sia corretto
                            .addToBackStack(null)
                            .commit();

                    dialog.dismiss(); // Chiudi il dialog
                } else {
                    Toast.makeText(getContext(), "Inserisci un nome per il diario!", Toast.LENGTH_SHORT).show();
                }
            });

            // Imposta il comportamento del pulsante "Annulla"
            btnCancel.setOnClickListener(v1 -> dialog.dismiss());

            // Mostra il dialog
            dialog.show();
        });

        return view;
    }
}
