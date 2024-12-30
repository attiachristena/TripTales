package com.unimib.triptales.ui.homepage.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.unimib.triptales.R;
import com.unimib.triptales.adapters.Diary;
import com.unimib.triptales.adapters.DiaryAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private DiaryAdapter diaryAdapter;
    private List<Diary> diaryList = new ArrayList<>();
    private TextView emptyMessage;
    private FloatingActionButton fabAddDiary;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inizializza i riferimenti agli elementi della vista
        recyclerView = view.findViewById(R.id.recycler_view_diaries);
        emptyMessage = view.findViewById(R.id.text_empty_message);
        fabAddDiary = view.findViewById(R.id.fab_add_diary);

        // Configura il RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        diaryAdapter = new DiaryAdapter(getContext(), diaryList);
        recyclerView.setAdapter(diaryAdapter);

        // Aggiorna il messaggio vuoto in base al contenuto della lista
        updateEmptyMessage();

        // Naviga al fragment per aggiungere un diario quando si preme il pulsante FAB
        fabAddDiary.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new AddDiaryFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }

    // Metodo per aggiornare il messaggio vuoto
    private void updateEmptyMessage() {
        if (diaryList.isEmpty()) {
            emptyMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    // Metodo per aggiungere un diario alla lista
    public void addDiary(Diary diary) {
        diaryList.add(diary);
        diaryAdapter.notifyDataSetChanged();
        updateEmptyMessage();
    }
}
