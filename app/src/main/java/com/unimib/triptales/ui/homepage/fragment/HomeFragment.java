package com.unimib.triptales.ui.homepage.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.unimib.triptales.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        FloatingActionButton fab = view.findViewById(R.id.fab_add_diary);
        fab.setOnClickListener(v -> {
            // Mostra un Toast di base (aggiungeremo un dialog in seguito)
            Toast.makeText(getContext(), "Aggiungi un nuovo diario!", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}