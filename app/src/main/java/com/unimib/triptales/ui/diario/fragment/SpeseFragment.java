package com.unimib.triptales.ui.diario.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.unimib.triptales.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;


public class SpeseFragment extends Fragment {

    int budget;
    double spent;
    Button saveButton;
    TextView progressText;
    ImageButton editBudget;
    ImageButton backButton;
    int progressPercentage;
    LinearProgressIndicator progressIndicator;
    TextView budgetText;
    EditText numberEditText;
    String formattedText;
    ImageButton addSpesa;
    LinearLayout spesaCardsContainer;
    ImageButton backButtonSpesa;
    Button saveSpesa;
    String inputQuantitaSpesa;
    String inputCategoria;
    String inputDescrizione;
    Integer inputDay;
    Integer inputMonth;
    Integer inputYear;
    EditText editTextQuantitaSpesa;
    AutoCompleteTextView editTextCategoria;
    EditText editTextDescrizione;
    EditText editTextDay;
    EditText editTextMonth;
    EditText editTextYear;
    ConstraintLayout rootLayout;
    FloatingActionButton modificaSpesa;
    FloatingActionButton eliminaSpesa;
    ArrayList<MaterialCardView> listSpesaCards;
    int indice;
    View overlay_add_spesa;
    LayoutInflater inflater;
    View overlay_add_budget;
    Boolean nuovaSpesa;
    ImageButton backButtonModificaSpesa;
    Button saveModificaSpesa;
    String inputModificaQuantitaSpesa;
    String inputModificaCategoria;
    String inputModificaDescrizione;
    Integer inputModifyDay;
    Integer inputModifyMonth;
    Integer inputModifyYear;
    EditText editTextModificaQuantitaSpesa;
    AutoCompleteTextView editTextModificaCategoria;
    EditText editTextModificaDescrizione;
    EditText editTextModifyDay;
    EditText editTextModifyMonth;
    EditText editTextModifyYear;
    View overlay_modifica_spesa;
    Button filterButton;
    ImageButton closeFilter;
    TextView totSpesa;


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

        inflater = LayoutInflater.from(view.getContext());
        listSpesaCards = new ArrayList<>();
        rootLayout = view.findViewById(R.id.rootLayoutSpese);

        // Card per modificare il budget

        overlay_add_budget = inflater.inflate(R.layout.overlay_add_budget, rootLayout, false);
        rootLayout.addView(overlay_add_budget);
        overlay_add_budget.setVisibility(View.GONE);

        saveButton = view.findViewById(R.id.salvaBudget);
        editBudget = view.findViewById(R.id.editBudget);
        backButton = view.findViewById(R.id.backBudgetButton);

        editBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_add_budget.setVisibility(View.VISIBLE);
                addSpesa.setVisibility(View.GONE);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_add_budget.setVisibility(View.GONE);
                hideKeyboard(view);
                addSpesa.setVisibility(View.VISIBLE);
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
                } else if (inputText.length() > 8) {
                    numberEditText.setError("Inserisci un numero più basso");
                } else {
                    // Testo valido
                    numberEditText.setError(null);
                    budget = Integer.parseInt(inputText);
                    budgetText.setText(getString(R.string.stringaCosto, budget));
                    updateProgressIndicator(spent, budget, 0);
                    hideKeyboard(view);
                    overlay_add_budget.setVisibility(View.GONE);
                    addSpesa.setVisibility(View.VISIBLE);
                }
            }
        });

        //Card per aggiungere una nuova spesa

        addSpesa = view.findViewById(R.id.addButtonSpese);
        overlay_add_spesa = inflater.inflate(R.layout.overlay_add_spesa, rootLayout, false);
        rootLayout.addView(overlay_add_spesa);
        overlay_add_spesa.setVisibility(View.GONE);

        addSpesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(budget == 0){
                    Snackbar snackbar = Snackbar.make(rootLayout, R.string.snackbarErroreBudget, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }else {
                    overlay_add_spesa.setVisibility(View.VISIBLE);
                    addSpesa.setVisibility(View.GONE);
                    editTextQuantitaSpesa.setText("");
                    editTextCategoria.setText("");
                    editTextDescrizione.setText("");
                    editTextDay.setText("");
                    editTextMonth.setText("");
                    editTextYear.setText("");
                    nuovaSpesa = true;
                }
            }
        });

        backButtonSpesa = view.findViewById(R.id.backSpesaButton);

        backButtonSpesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_add_spesa.setVisibility(View.GONE);
                hideKeyboard(view);
                addSpesa.setVisibility(View.VISIBLE);
            }
        });

        spesaCardsContainer = view.findViewById(R.id.spesaCardsContainer);
        saveSpesa = view.findViewById(R.id.salvaSpesa);
        editTextQuantitaSpesa = view.findViewById(R.id.inputQuantitaSpesa);
        editTextCategoria = view.findViewById(R.id.inputCategory);
        editTextDescrizione = view.findViewById(R.id.inputDescription);
        editTextDay = view.findViewById(R.id.inputDay);
        editTextMonth = view.findViewById(R.id.inputMonth);
        editTextYear = view.findViewById(R.id.inputYear);
        indice = 0;
        modificaSpesa = view.findViewById(R.id.modificaSpesa);
        eliminaSpesa = view.findViewById(R.id.eliminaSpesa);

        String[] items = {"Shopping", "Cibo", "Mezzi di trasporto", "Alloggio", "Cultura", "Svago"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, items);
        editTextCategoria.setAdapter(adapter);


        saveSpesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Usa LayoutInflater per creare una nuova CardView
                View card = inflater.inflate(R.layout.item_card_spesa, spesaCardsContainer, false);


                // Modifica il contenuto della card
                TextView category = card.findViewById(R.id.categoryTextView);
                inputCategoria = editTextCategoria.getText().toString().trim();

                TextView description = card.findViewById(R.id.descriptionTextView);
                inputDescrizione = editTextDescrizione.getText().toString().trim();

                TextView dataSpesa = card.findViewById(R.id.dateTextView);
                inputDay = Integer.parseInt(editTextDay.getText().toString());
                inputMonth = Integer.parseInt(editTextMonth.getText().toString());
                inputYear = Integer.parseInt(editTextYear.getText().toString());

                TextView amount = card.findViewById(R.id.amountTextView);
                inputQuantitaSpesa = editTextQuantitaSpesa.getText().toString().trim();

                if (inputQuantitaSpesa.isEmpty()) {
                    editTextQuantitaSpesa.setError("Inserisci una quantità");
                } else if (inputCategoria.isEmpty()) {
                    editTextCategoria.setError("Scegli una categoria");
                } else if (inputDescrizione.isEmpty()) {
                    editTextDescrizione.setError("Inserisci una descrizione");
                }else if (inputDescrizione.length() > 18){
                    editTextDescrizione.setError("La descrizione supera il massimo di caratteri");
                } else if (inputDay == null) {
                    editTextDay.setError("Inserisci un giorno");
                } else if (inputDay < 1 || inputDay > 31) {
                    editTextDay.setError("Inserisci un giorno valido");
                } else if (inputMonth == null) {
                    editTextMonth.setError("Inserisci un mese");
                } else if (inputMonth < 1 || inputMonth > 12) {
                    editTextMonth.setError("Inserisci un mese valido");
                } else if (inputYear == null) {
                    editTextYear.setError("Inserisci un anno");
                } else if (inputYear < 2000 || inputYear > 2100) {
                    editTextYear.setError("Inserisci un anno valido");
                } else {
                    editTextCategoria.setError(null);
                    category.setText(inputCategoria);
                    editTextDescrizione.setError(null);
                    description.setText(inputDescrizione);
                    editTextDay.setError(null);
                    editTextMonth.setError(null);
                    editTextYear.setError(null);
                    String dataCompleta = getString(R.string.dataCompleta, inputDay, inputMonth, inputYear);
                    dataSpesa.setText(dataCompleta);
                    editTextQuantitaSpesa.setError(null);
                    double spesa = Double.parseDouble(inputQuantitaSpesa);
                    updateProgressIndicator(spesa, budget, 1);
                    String s = spesa + "€";
                    amount.setText(s);

                    ImageView icon = view.findViewById(R.id.cardSpesaIcon);

                    if(inputCategoria.equalsIgnoreCase("Shopping")){
                        icon.setImageResource(R.drawable.baseline_shopping_cart_24);
                    }else if(inputCategoria.equalsIgnoreCase("Cibo")) {
                        icon.setImageResource(R.drawable.baseline_fastfood_24);
                    }else if(inputCategoria.equalsIgnoreCase("Mezzi di trasporto")) {
                        icon.setImageResource(R.drawable.baseline_directions_bus_24);
                    }else if(inputCategoria.equalsIgnoreCase("Alloggio")) {
                        icon.setImageResource(R.drawable.baseline_hotel_24);
                    }else if(inputCategoria.equalsIgnoreCase("Cultura")) {
                        icon.setImageResource(R.drawable.baseline_museum_24);
                    }else if(inputCategoria.equalsIgnoreCase("Svago")) {
                        icon.setImageResource(R.drawable.baseline_attractions_24);
                    }

                    // Aggiungi la nuova CardView al layout genitore
                    spesaCardsContainer.addView(card);
                    hideKeyboard(view);
                    overlay_add_spesa.setVisibility(View.GONE);
                    addSpesa.setVisibility(View.VISIBLE);
                    MaterialCardView cardSpesaCorrente = (MaterialCardView) spesaCardsContainer.getChildAt(indice);
                    listSpesaCards.add(cardSpesaCorrente);
                    indice++;

                    cardSpesaCorrente.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            addSpesa.setEnabled(false);
                            if (cardSpesaCorrente.isSelected()){
                                cardSpesaCorrente.setCardBackgroundColor(getResources().getColor(R.color.primary));
                                cardSpesaCorrente.setStrokeColor(getResources().getColor(R.color.primary));
                                cardSpesaCorrente.setSelected(false);
                                boolean notSelectedAll = true;
                                for (MaterialCardView m : listSpesaCards){
                                    if(m.isSelected())
                                        notSelectedAll = false;
                                }
                                if(notSelectedAll) {
                                    modificaSpesa.setVisibility(View.GONE);
                                    eliminaSpesa.setVisibility(View.GONE);
                                    addSpesa.setEnabled(true);
                                }
                            } else {
                                cardSpesaCorrente.setCardBackgroundColor(getResources().getColor(R.color.primary_light));
                                cardSpesaCorrente.setStrokeColor(getResources().getColor(R.color.background_dark));
                                cardSpesaCorrente.setSelected(true);
                            }
                            if (countSelectedCards(listSpesaCards) == 1) {
                                modificaSpesa.setVisibility(View.VISIBLE);
                                eliminaSpesa.setVisibility(View.VISIBLE);
                            } else if (countSelectedCards(listSpesaCards) == 2){
                                modificaSpesa.setVisibility(View.GONE);
                            }
                            return true;
                        }
                    });
                }
            }
        });

        // per eliminare una spesa

        eliminaSpesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Iterator<MaterialCardView> iterator = listSpesaCards.iterator();
                while (iterator.hasNext()) {
                    MaterialCardView item = iterator.next();
                    if (item.isSelected()) {
                        TextView amountTextView = item.findViewById(R.id.amountTextView);
                        String amountText = amountTextView.getText().toString();
                        String amountSubstring = amountText.substring(0, amountText.length()-1);
                        double amount = Double.parseDouble(amountSubstring);
                        iterator.remove();
                        spesaCardsContainer.removeView(item);
                        spent = spent - amount;
                        updateProgressIndicator(spent, budget, 0);
                        indice--;
                    }
                }
                modificaSpesa.setVisibility(View.GONE);
                eliminaSpesa.setVisibility(View.GONE);
                addSpesa.setEnabled(true);
            }
        });

        // per modificare una spesa

        overlay_modifica_spesa = inflater.inflate(R.layout.overlay_modifica_spesa, rootLayout, false);
        rootLayout.addView(overlay_modifica_spesa);
        overlay_modifica_spesa.setVisibility(View.GONE);

        modificaSpesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_modifica_spesa.setVisibility(View.VISIBLE);

                MaterialCardView card = findSelectedCard(listSpesaCards);

                TextView cardSpesa = card.findViewById(R.id.amountTextView);
                String amountCardSpesa = cardSpesa.getText().toString();
                editTextModificaQuantitaSpesa.setText(amountCardSpesa.substring(0, amountCardSpesa.length()-1));
                TextView cardCategoria = card.findViewById(R.id.categoryTextView);
                editTextModificaCategoria.setText(cardCategoria.getText().toString().trim());
                TextView cardDescrizione = card.findViewById(R.id.descriptionTextView);
                editTextModificaDescrizione.setText(cardDescrizione.getText().toString().trim());
                editTextModifyDay.setText(inputDay);
                editTextModifyMonth.setText(inputMonth);
                editTextModifyYear.setText(inputYear);

                addSpesa.setVisibility(View.GONE);
                modificaSpesa.setVisibility(View.GONE);
                eliminaSpesa.setVisibility(View.GONE);
            }
        });

        backButtonModificaSpesa = view.findViewById(R.id.backModificaSpesaButton);

        backButtonModificaSpesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_modifica_spesa.setVisibility(View.GONE);
                hideKeyboard(view);
                addSpesa.setVisibility(View.VISIBLE);
                modificaSpesa.setVisibility(View.VISIBLE);
                eliminaSpesa.setVisibility(View.VISIBLE);
            }
        });

        saveModificaSpesa = view.findViewById(R.id.salvaModificaSpesa);
        editTextModificaQuantitaSpesa = view.findViewById(R.id.inputModificaQuantitaSpesa);
        editTextModificaCategoria = view.findViewById(R.id.inputModificaCategory);
        editTextModificaDescrizione = view.findViewById(R.id.inputModificaDescription);
        editTextModifyDay = view.findViewById(R.id.inputModifyDay);
        editTextModifyMonth = view.findViewById(R.id.inputModifyMonth);
        editTextModifyYear = view.findViewById(R.id.inputModifyYear);
        editTextModificaCategoria.setAdapter(adapter);

        saveModificaSpesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MaterialCardView card = findSelectedCard(listSpesaCards);

                TextView amountTextView = card.findViewById(R.id.amountTextView);
                String amountText = amountTextView.getText().toString();
                String amountSubstring = amountText.substring(0, amountText.length()-1);
                double amount = Double.parseDouble(amountSubstring);
                spent = spent - amount;

                // Modifica il contenuto della card
                TextView category = card.findViewById(R.id.categoryTextView);
                inputModificaCategoria = editTextModificaCategoria.getText().toString().trim();

                TextView description = card.findViewById(R.id.descriptionTextView);
                inputModificaDescrizione = editTextModificaDescrizione.getText().toString().trim();

                TextView dataSpesa = card.findViewById(R.id.dateTextView);
                inputModifyDay = Integer.parseInt(editTextModifyDay.getText().toString());
                inputModifyMonth = Integer.parseInt(editTextModifyMonth.getText().toString());
                inputModifyYear = Integer.parseInt(editTextModifyYear.getText().toString());

                inputModificaQuantitaSpesa = editTextModificaQuantitaSpesa.getText().toString().trim();

                if (inputModificaQuantitaSpesa.isEmpty()) {
                    editTextModificaQuantitaSpesa.setError("Inserisci una data");
                } else if (inputModificaCategoria.isEmpty()) {
                    editTextModificaCategoria.setError("Scegli una categoria");
                } else if (inputModificaDescrizione.isEmpty()) {
                    editTextModificaDescrizione.setError("Inserisci una descrizione");
                }else if (inputModificaDescrizione.length() > 18){
                    editTextModificaDescrizione.setError("La descrizione supera il massimo di caratteri");
                } else if (inputModifyDay == null) {
                    editTextModifyDay.setError("Inserisci un giorno");
                } else if (inputModifyDay < 1 || inputModifyDay > 31) {
                    editTextModifyDay.setError("Inserisci un giorno valido");
                } else if (inputModifyMonth == null) {
                    editTextModifyMonth.setError("Inserisci un mese");
                } else if (inputModifyMonth < 1 || inputModifyMonth > 12) {
                    editTextModifyMonth.setError("Inserisci un mese valido");
                } else if (inputModifyYear == null) {
                    editTextModifyYear.setError("Inserisci un anno");
                } else if (inputYear < 2000 || inputYear > 2100) {
                    editTextModifyYear.setError("Inserisci un anno valido");
                } else {
                    editTextModificaCategoria.setError(null);
                    category.setText(inputModificaCategoria);
                    editTextModificaDescrizione.setError(null);
                    description.setText(inputModificaDescrizione);
                    editTextModifyDay.setError(null);
                    editTextModifyMonth.setError(null);
                    editTextModifyYear.setError(null);
                    String dataCompleta = getString(R.string.dataCompleta, inputModifyDay, inputModifyMonth, inputModifyYear);
                    dataSpesa.setText(dataCompleta);
                    editTextModificaQuantitaSpesa.setError(null);

                    inputDay = inputModifyDay;
                    inputMonth = inputModifyMonth;
                    inputYear = inputModifyYear;

                    ImageView icon = card.findViewById(R.id.cardSpesaIcon);

                    if(inputModificaCategoria.equalsIgnoreCase("Shopping")){
                        icon.setImageResource(R.drawable.baseline_shopping_cart_24);
                    }else if(inputModificaCategoria.equalsIgnoreCase("Cibo")) {
                        icon.setImageResource(R.drawable.baseline_fastfood_24);
                    }else if(inputModificaCategoria.equalsIgnoreCase("Mezzi di trasporto")) {
                        icon.setImageResource(R.drawable.baseline_directions_bus_24);
                    }else if(inputModificaCategoria.equalsIgnoreCase("Alloggio")) {
                        icon.setImageResource(R.drawable.baseline_hotel_24);
                    }else if(inputModificaCategoria.equalsIgnoreCase("Cultura")) {
                        icon.setImageResource(R.drawable.baseline_museum_24);
                    }else if(inputModificaCategoria.equalsIgnoreCase("Svago")) {
                        icon.setImageResource(R.drawable.baseline_attractions_24);
                    }

                    double spesa = Double.parseDouble(inputModificaQuantitaSpesa);
                    updateProgressIndicator(spesa, budget, 1);
                    String s = spesa + "€";
                    amountTextView.setText(s);

                    hideKeyboard(view);
                    overlay_modifica_spesa.setVisibility(View.GONE);
                    addSpesa.setVisibility(View.VISIBLE);
                    addSpesa.setEnabled(true);
                    card.setCardBackgroundColor(getResources().getColor(R.color.primary));
                    card.setStrokeColor(getResources().getColor(R.color.primary));
                    card.setSelected(false);

                }
            }
        });

        // Filter button

        filterButton = view.findViewById(R.id.buttonFilter);
        closeFilter = view.findViewById(R.id.closeFilter);
        totSpesa = view.findViewById(R.id.totSpesa);

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFilter.setVisibility(View.VISIBLE);
                totSpesa.setVisibility(View.VISIBLE);
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

    public void updateProgressIndicator(double spesa, int budget, int add){
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

        formattedText = spent + " / " + budget + " spent " + " (" + progressPercentage + "%)";
        progressText.setText(formattedText);

    }

    public int countSelectedCards(ArrayList<MaterialCardView> cardList) {
        int selectedCount = 0;
        for (MaterialCardView card : cardList) {
            if (card.isSelected())
                selectedCount++;
        }
        return selectedCount;  // Restituisce il numero di card selezionate
    }

    public MaterialCardView findSelectedCard(ArrayList<MaterialCardView> cardList){
        MaterialCardView selectedCard = cardList.get(0);
        for (MaterialCardView card : cardList) {
            if (card.isSelected())
                selectedCard = card;
        }
        return selectedCard;
    }

}
