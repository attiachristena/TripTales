package com.unimib.triptales.ui.diario.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unimib.triptales.R;

public class ObiettiviFragment extends Fragment {

    public ObiettiviFragment() {
        // Required empty public constructor
    }

    public static ObiettiviFragment newInstance(String param1, String param2) {
        ObiettiviFragment fragment = new ObiettiviFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_obiettivi, container, false);
    }
}