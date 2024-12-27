package com.unimib.triptales.ui.diario.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.unimib.triptales.AddTappaFragment;
import com.unimib.triptales.R;

public class TappeFragment extends Fragment {

    private LinearLayout containerTappe;
    private ImageButton addTappaBtn;
    private ImageButton deleteButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tappe, container, false);

        // Recupera il contenitore delle card tappe
        containerTappe = view.findViewById(R.id.tappeCardContainer);

        // Imposta il click listener per il pulsante "Aggiungi Tappa"
        setupAddTappaButton(view);

        // Carica le tappe salvate
        loadTappe();


        return view;
    }

    private void setupAddTappaButton(View view) {
        addTappaBtn = view.findViewById(R.id.addTappaButton);
        addTappaBtn.setOnClickListener(v -> openAddTappaFragment());
    }

    public void loadTappe() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("TAPPE_PREFS", Context.MODE_PRIVATE);
        String tappeSalvate = sharedPreferences.getString("tappe_list", "");

        // Pulisci il contenitore prima di ricaricare le tappe
        if (containerTappe instanceof ViewGroup) {
            ((ViewGroup) containerTappe).removeAllViews();
        }


        if (!tappeSalvate.isEmpty()) {
            String[] tappeArray = tappeSalvate.split(";");
            for (String tappa : tappeArray) {
                addCardTappa(tappa);
            }
        } else {
            Toast.makeText(getContext(), "Nessuna tappa disponibile", Toast.LENGTH_SHORT).show();
        }
    }



    private void addCardTappa(String name) {
        // Infla la card dinamica
        View cardTappa = LayoutInflater.from(requireContext()).inflate(R.layout.item_card_tappa, containerTappe, false);

        // Imposta il nome della tappa
        TextView textViewNomeTappa = cardTappa.findViewById(R.id.nomeTappa);
        textViewNomeTappa.setText(name);

        // Configura il pulsante di eliminazione
        ImageButton deleteButton = cardTappa.findViewById(R.id.delete_tappa);
        deleteButton.setOnClickListener(v -> {
            // Rimuovi la tappa dalla lista visiva
            containerTappe.removeView(cardTappa);

            // Aggiorna i SharedPreferences
            removeTappaFromPreferences(name);

            // Notifica l'utente
            Toast.makeText(getContext(), "Tappa eliminata: " + name, Toast.LENGTH_SHORT).show();
        });

        // Aggiungi la card al contenitore
        containerTappe.addView(cardTappa);
    }

    private void removeTappaFromPreferences(String tappaToRemove) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("TAPPE_PREFS", Context.MODE_PRIVATE);
        String tappeSalvate = sharedPreferences.getString("tappe_list", "");

        if (!tappeSalvate.isEmpty()) {
            // Dividi le tappe in un array
            String[] tappeArray = tappeSalvate.split(";");

            // Rimuovi la tappa specificata
            StringBuilder newTappeList = new StringBuilder();
            for (String tappa : tappeArray) {
                if (!tappa.equals(tappaToRemove)) {
                    if (newTappeList.length() > 0) {
                        newTappeList.append(";");
                    }
                    newTappeList.append(tappa);
                }
            }

            // Salva la lista aggiornata nei SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("tappe_list", newTappeList.toString());
            editor.apply();
        }
    }



    private void openAddTappaFragment() {
        try {
            Fragment addTappaFragment = new AddTappaFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, addTappaFragment)
                    .addToBackStack(null)
                    .commit();
        } catch (Exception e) {
            Log.e("TappeFragment", "Errore nell'aprire AddTappaFragment", e);
        }
    }
}