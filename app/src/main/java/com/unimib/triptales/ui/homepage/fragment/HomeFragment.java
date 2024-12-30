package com.unimib.triptales.ui.homepage.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.unimib.triptales.R;
import com.unimib.triptales.ui.homepage.fragment.AddDiaryFragment;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        FloatingActionButton fab = view.findViewById(R.id.fab_add_diary);

        // Azioni quando si preme il pulsante "+"
        fab.setOnClickListener(v -> {
            // Sostituisci il fragment corrente con AddDiaryFragment
            AddDiaryFragment addDiaryFragment = new AddDiaryFragment();

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, addDiaryFragment) // Assicurati che 'fragment_container' sia corretto
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
