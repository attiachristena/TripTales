package com.unimib.triptales;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.unimib.triptales.ui.diario.fragment.TappeFragment;

public class AddTappaFragment extends Fragment {

    private EditText editTextNomeTappa;
    private ImageButton salvaButton;
    private View backBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_tappa, container, false);

        // Riferimenti ai componenti
        editTextNomeTappa = view.findViewById(R.id.editTextTappa); // Nome EditText corretto
        salvaButton = view.findViewById(R.id.save_tappa_btn); // Assicurati di avere questo ID nel layout
        backBtn = view.findViewById(R.id.backTappaBtn);

        backBtn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack(); // Torna indietro al TappeFragment
        });


        // Listener per il pulsante Salva
        salvaButton.setOnClickListener(v -> {
            String nuovaTappa = editTextNomeTappa.getText().toString().trim();
            if (!nuovaTappa.isEmpty()) {
                salvaTappa(nuovaTappa);
                getParentFragmentManager().popBackStack(); // Torna indietro al TappeFragment
            }
        });

        return view;
    }

    // Metodo per salvare la tappa nei SharedPreferences
    private void salvaTappa(String tappa) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("TAPPE_PREFS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Recupera le tappe esistenti
        String tappeSalvate = sharedPreferences.getString("tappe_list", "");

        // Aggiungi la nuova tappa
        if (!tappeSalvate.isEmpty()) {
            tappeSalvate += ";" + tappa; // Aggiunge con separatore
        } else {
            tappeSalvate = tappa; // Prima tappa salvata
        }

        editor.putString("tappe_list", tappeSalvate);
        editor.apply();

        // Notifica il fragment padre per ricaricare le tappe
        TappeFragment tappeFragment = (TappeFragment) getParentFragment();
        if (tappeFragment != null) {
            tappeFragment.loadTappe(); // Ricarica le tappe nel parent fragment
        }
    }

}