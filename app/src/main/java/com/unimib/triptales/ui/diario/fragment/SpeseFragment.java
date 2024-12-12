package com.unimib.triptales.ui.diario.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.unimib.triptales.R;


public class SpeseFragment extends Fragment {

    int budget;
    int spent;
    Button saveButton;
    TextView progressText;
    ImageButton editBudget;
    CardView cardAddBudget;
    ImageButton backButton;
    int progressPercentage;
    LinearProgressIndicator progressIndicator;
    TextView budgetText;
    EditText numberEditText;
    String formattedText;
    ImageButton addSpesa;
    LinearLayout spesaCardLayout;
    ImageButton backButtonSpesa;
    CardView cardAddSpesa;
    Button saveSpesa;
    String inputQuantitaSpesa;
    String inputCategoria;
    String inputDescrizione;
    String inputData;
    EditText editTextQuantitaSpesa;
    EditText editTextCategoria;
    EditText editTextDescrizione;
    EditText editTextData;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_spese, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Card per modificare il budget

        saveButton = view.findViewById(R.id.salvaBudget);
        editBudget = view.findViewById(R.id.editBudget);
        cardAddBudget = view.findViewById(R.id.cardAddBudget);
        backButton = view.findViewById(R.id.backBudgetButton);

        editBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardAddBudget.setVisibility(View.VISIBLE);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardAddBudget.setVisibility(View.GONE);
            }
        });

        progressText = view.findViewById(R.id.progressText);
        numberEditText = view.findViewById(R.id.inputBudget);
        budgetText = view.findViewById(R.id.totBudget);
        progressIndicator = view.findViewById(R.id.budgetProgressIndicator);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputText = numberEditText.getText().toString().trim();
                if (inputText.isEmpty()) {
                    numberEditText.setError("Il campo non può essere vuoto");
                } else if (!inputText.matches("\\d+")) {
                    numberEditText.setError("Inserisci solo numeri");
                } else {
                    // Testo valido
                    numberEditText.setError(null);
                    budget = Integer.parseInt(inputText);
                    budgetText.setText(getString(R.string.stringaCosto, budget));
                    updateProgressIndicator(spent, budget, 0);
                    hideKeyboard(numberEditText);
                    cardAddBudget.setVisibility(View.GONE);
                }
            }
        });

        //Card per aggiungere una nuova spesa

        addSpesa = view.findViewById(R.id.addButtonSpese);
        cardAddSpesa = view.findViewById(R.id.cardAddSpesa);

        addSpesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardAddSpesa.setVisibility(View.VISIBLE);
                editTextQuantitaSpesa.setText("");
                editTextCategoria.setText("");
                editTextDescrizione.setText("");
                editTextData.setText("");
            }
        });

        backButtonSpesa = view.findViewById(R.id.backSpesaButton);

        backButtonSpesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardAddSpesa.setVisibility(View.GONE);
            }
        });

        spesaCardLayout = view.findViewById(R.id.spesaCardsContainer);
        saveSpesa = view.findViewById(R.id.salvaSpesa);
        editTextQuantitaSpesa = view.findViewById(R.id.inputQuantitaSpesa);
        editTextCategoria = view.findViewById(R.id.inputCategory);
        editTextDescrizione = view.findViewById(R.id.inputDescription);
        editTextData = view.findViewById(R.id.inputDate);


        saveSpesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Usa LayoutInflater per creare una nuova CardView
                LayoutInflater inflater = LayoutInflater.from(view.getContext());
                View newCard = inflater.inflate(R.layout.item_card_spesa, spesaCardLayout, false);

                // Modifica il contenuto della card
                TextView category = newCard.findViewById(R.id.categoryTextView);
                inputCategoria = editTextCategoria.getText().toString().trim();
                if (inputCategoria.isEmpty()) {
                    editTextCategoria.setError("Inserisci una categoria");
                } else {
                    editTextCategoria.setError(null);
                    category.setText(inputCategoria);
                }

                TextView description = newCard.findViewById(R.id.descriptionTextView);
                inputDescrizione = editTextDescrizione.getText().toString().trim();
                if (inputDescrizione.isEmpty()) {
                    editTextDescrizione.setError("Inserisci una descrizione");
                } else {
                    editTextDescrizione.setError(null);
                    description.setText(inputDescrizione);
                }

                TextView dataSpesa = newCard.findViewById(R.id.dateTextView);
                inputData = editTextData.getText().toString().trim();
                if (inputData.isEmpty()) {
                    editTextData.setError("Inserisci una data");
                } else {
                    editTextData.setError(null);
                    dataSpesa.setText(inputData);
                }

                TextView amount = newCard.findViewById(R.id.amountTextView);
                inputQuantitaSpesa = editTextQuantitaSpesa.getText().toString().trim();
                if (inputQuantitaSpesa.isEmpty()) {
                    editTextQuantitaSpesa.setError("Inserisci una data");
                } else {
                    editTextQuantitaSpesa.setError(null);
                    int spesa = Integer.parseInt(inputQuantitaSpesa);
                    updateProgressIndicator(spesa, budget, 1);
                    amount.setText(getString(R.string.stringaCosto, spesa));
                }
                //manca non aggiungere la card quando i campi sono vuoti!!
                // Aggiungi la nuova CardView al layout genitore
                spesaCardLayout.addView(newCard);
                //hideKeyboard(numberEditText);
                cardAddSpesa.setVisibility(View.GONE);
            }
        });

        ScrollView scrollView = view.findViewById(R.id.scrollView);
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);

    }

    // Metodo per nascondere la tastiera
    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void updateProgressIndicator(int spesa, int budget, int add){
        if(add == 1)
            spent = spent+spesa;
        // calcola la percentuale spesa
        progressPercentage = (int) ((spent / (float) budget) * 100);
        if(progressPercentage > 100)
            progressIndicator.setIndicatorColor(getResources().getColor(R.color.error));
        else
            progressIndicator.setIndicatorColor(getResources().getColor(R.color.secondary));
        // imposta il progress indicator
        progressIndicator.setProgress(progressPercentage);
        // aggiorna la descrizione del progress indicator
        formattedText = getString(R.string.progress_text, spent, budget, progressPercentage);
        progressText.setText(formattedText);
    }


}




