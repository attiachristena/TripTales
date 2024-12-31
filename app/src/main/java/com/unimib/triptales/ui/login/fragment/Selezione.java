package com.unimib.triptales.ui.login.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.unimib.triptales.R;

public class Selezione extends Fragment {

    public Selezione() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_selezione, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button signInButton = view.findViewById(R.id.registrati);
        Button loginButton = view.findViewById(R.id.accedi);

        loginButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_selezione_to_loginFragment));
        signInButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_selezione_to_signInFragment));

    }
}