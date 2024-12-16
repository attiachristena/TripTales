package com.unimib.triptales.ui.diario.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.unimib.triptales.AddTappaFragment;
import com.unimib.triptales.R;

public class TappeFragment extends Fragment {

    private View cardTappa;
    private View addTappa;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tappe, container, false);

        // Recupera il nome del diario passato con il Bundle
        Bundle args = getArguments();
        if (args != null) {
            String diaryName = args.getString("diary_name");

            // Trova la TextView e imposta il nome del diario
            TextView diaryTitle = view.findViewById(R.id.diary_title);
            if (diaryName != null) {
                diaryTitle.setText(diaryName);
            }
        }
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Card per aggiungere una nuova tappa
        FloatingActionButton addTappa = view.findViewById(R.id.addTappaButton);
        cardTappa = view.findViewById(R.id.cardAddTappa);

        addTappa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // Sostituisci il Fragment corrente con AddTappaFragment
                    Fragment addTappaFragment = new AddTappaFragment();
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, addTappaFragment)
                            .addToBackStack(null)
                            .commit();
                } catch (Exception e) {
                    Log.e("CreateTappaActivity", "Error opening AddTappaFragment", e);
                    // Mostra un messaggio di errore all'utente o esegui altre azioni appropriate
                }
            }
        });
    }
}