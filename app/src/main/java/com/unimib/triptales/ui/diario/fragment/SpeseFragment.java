package com.unimib.triptales.ui.diario.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

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
import com.google.android.material.textfield.TextInputLayout;
import com.unimib.triptales.R;

import java.util.ArrayList;
import java.util.Iterator;


public class SpeseFragment extends Fragment {

    int budget;
    double spent;
    Button saveBudget;
    TextView progressText;
    String inputBudget;
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
    TextView filterText;
    View overlay_filter;
    AutoCompleteTextView editTextCategoryFilter;
    String inputCategoryFilter;
    ImageButton backButtonFilter;
    ArrayList<MaterialCardView> filteredCards;
    Button saveCategory;
    String inputCurrency;
    String inputOldCurrency;
    AutoCompleteTextView editTextCurrency;
    TextInputLayout textFieldCurrency;
    TextView noSpeseString;


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

        saveBudget = view.findViewById(R.id.salvaBudget);
        editBudget = view.findViewById(R.id.editBudget);
        backButton = view.findViewById(R.id.backButtonBudget);
        noSpeseString = view.findViewById(R.id.noSpeseString);

        editBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_add_budget.setVisibility(View.VISIBLE);
                addSpesa.setVisibility(View.GONE);
                if(listSpesaCards.isEmpty()){
                    textFieldCurrency.setVisibility(View.VISIBLE);
                } else {
                    textFieldCurrency.setVisibility(View.GONE);
                }
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
        editTextCurrency = view.findViewById(R.id.inputCurrency);
        budgetText = view.findViewById(R.id.totBudget);
        progressIndicator = view.findViewById(R.id.budgetProgressIndicator);
        inputOldCurrency = editTextCurrency.getText().toString().trim();
        textFieldCurrency = view.findViewById(R.id.textFieldCurrency);
        totSpesa = view.findViewById(R.id.totSpesa);

        String[] itemsCurrency = {"€", "$", "£", "¥"};
        ArrayAdapter<String> adapterBudget = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, itemsCurrency);
        editTextCurrency.setAdapter(adapterBudget);

        saveBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputBudget = numberEditText.getText().toString().trim();
                inputCurrency = editTextCurrency.getText().toString().trim();
                if (inputBudget.isEmpty()) {
                    numberEditText.setError("Inserisci una quantità");
                } else if (inputCurrency.isEmpty()) {
                    editTextCurrency.setError("Scegli una valuta");
                } else if (inputBudget.length() > 9) {
                    numberEditText.setError("Inserisci un numero più basso");
                } else {
                    numberEditText.setError(null);
                    budget = Integer.parseInt(inputBudget);
                    String tmp;
                    if(inputCurrency.equalsIgnoreCase("€"))
                        tmp = budget+inputCurrency;
                    else
                        tmp = inputCurrency+budget;
                    budgetText.setText(tmp);
                    updateProgressIndicator(spent, budget, 0);
                    hideKeyboard(view);
                    overlay_add_budget.setVisibility(View.GONE);
                    addSpesa.setVisibility(View.VISIBLE);
                    if(totSpesa.getVisibility() == View.VISIBLE) {
                        double countSpese = countAmountByCategory();
                        String tmp2;
                        if (inputCurrency.equalsIgnoreCase("€"))
                            tmp2 = countSpese + inputCurrency;
                        else
                            tmp2 = inputCurrency + countSpese;
                        totSpesa.setText(tmp2);
                    }
                    updateCurrencyIcon();
                    inputOldCurrency = inputCurrency;
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

        String[] itemsCategory = {"Shopping", "Cibo", "Trasporto", "Alloggio", "Cultura", "Svago"};
        ArrayAdapter<String> adapterCategory = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, itemsCategory);
        editTextCategoria.setAdapter(adapterCategory);

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
                if (editTextDay.getText().toString().isEmpty()) {
                    editTextDay.setError("Inserisci un giorno");
                } else {
                    inputDay = Integer.parseInt(editTextDay.getText().toString());
                    if (inputDay < 1 || inputDay > 31) {
                        editTextDay.setError("Inserisci un giorno valido");
                    } else {
                        editTextDay.setError(null);
                    }
                }

                if (editTextMonth.getText().toString().isEmpty()) {
                    editTextMonth.setError("Inserisci un mese");
                } else {
                    inputMonth = Integer.parseInt(editTextMonth.getText().toString());
                    if (inputMonth < 1 || inputMonth > 12) {
                        editTextMonth.setError("Inserisci un mese valido");
                    } else {
                        editTextMonth.setError(null);
                    }
                }

                if (editTextYear.getText().toString().isEmpty()) {
                    editTextYear.setError("Inserisci un anno");
                } else {
                    inputYear = Integer.parseInt(editTextYear.getText().toString());
                    if (inputYear < 2000 || inputYear > 2100) {
                        editTextYear.setError("Inserisci un anno valido");
                    } else {
                        editTextYear.setError(null);
                    }
                }


                TextView amount = card.findViewById(R.id.amountTextView);
                inputQuantitaSpesa = editTextQuantitaSpesa.getText().toString().trim();

                if (inputQuantitaSpesa.isEmpty()) {
                    editTextQuantitaSpesa.setError("Inserisci una quantità");
                } else if (inputCategoria.isEmpty()) {
                    editTextCategoria.setError("Scegli una categoria");
                } else if (inputDescrizione.isEmpty()) {
                    editTextDescrizione.setError("Inserisci una descrizione");
                } else if (editTextDay.getError() != null){
                    editTextDay.setError("Inserisci un giorno");
                } else if (editTextMonth.getError() != null){
                    editTextMonth.setError("Inserisci un mese");
                } else if (editTextYear.getError() != null){
                    editTextYear.setError("Inserisci un anno");
                } else if (inputMonth < 1 || inputMonth > 12) {
                    editTextMonth.setError("Inserisci un mese valido");
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
                    String tmp;
                    if(inputCurrency.equalsIgnoreCase("€"))
                        tmp = spesa+inputCurrency;
                    else
                        tmp = inputCurrency+spesa;
                    amount.setText(tmp);

                    ImageView icon = card.findViewById(R.id.cardSpesaIcon);

                    if(inputCategoria.equalsIgnoreCase("Shopping")){
                        icon.setImageResource(R.drawable.baseline_shopping_cart_24);
                    }else if(inputCategoria.equalsIgnoreCase("Cibo")) {
                        icon.setImageResource(R.drawable.baseline_fastfood_24);
                    }else if(inputCategoria.equalsIgnoreCase("Trasporto")) {
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
                    noSpeseString.setVisibility(View.GONE);

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
                            if (countSelectedCards() == 1) {
                                modificaSpesa.setVisibility(View.VISIBLE);
                                eliminaSpesa.setVisibility(View.VISIBLE);
                            } else if (countSelectedCards() == 2){
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
                if(listSpesaCards.isEmpty()){
                    noSpeseString.setVisibility(View.VISIBLE);
                }
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

                MaterialCardView card = findSelectedCard();

                TextView cardSpesa = card.findViewById(R.id.amountTextView);
                String amountCardSpesa = cardSpesa.getText().toString();
                String tmp;
                if(inputCurrency.equalsIgnoreCase("€"))
                    tmp = amountCardSpesa.substring(0, amountCardSpesa.length()-1);
                else
                    tmp = amountCardSpesa.substring(1);
                editTextModificaQuantitaSpesa.setText(tmp);
                TextView cardCategoria = card.findViewById(R.id.categoryTextView);
                editTextModificaCategoria.setText(cardCategoria.getText().toString().trim(), false);

                TextView cardDescrizione = card.findViewById(R.id.descriptionTextView);
                editTextModificaDescrizione.setText(cardDescrizione.getText().toString().trim());

                TextView cardData = card.findViewById(R.id.dateTextView);
                String data = cardData.getText().toString().trim();

                if(data.charAt(1) == '/'){
                    inputModifyDay = (int)(data.charAt(0)) - '0';
                    data = data.substring(2);
                }else{
                    inputModifyDay = Integer.parseInt(data.substring(0, 2));
                    data = data.substring(3);
                }
                editTextModifyDay.setText(String.valueOf(inputModifyDay));

                if(data.charAt(1) == '/'){
                    inputModifyMonth =(int)(data.charAt(0)) - '0';
                    data = data.substring(2);
                }else{
                    inputModifyMonth = Integer.parseInt(data.substring(0, 2));
                    data = data.substring(3);
                }
                editTextModifyMonth.setText(String.valueOf(inputModifyMonth));

                inputModifyYear = Integer.parseInt(data);
                editTextModifyYear.setText(String.valueOf(inputModifyYear));

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
        editTextModificaCategoria.setAdapter(adapterCategory);

        saveModificaSpesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialCardView card = findSelectedCard();

                TextView amountTextView = card.findViewById(R.id.amountTextView);
                String amountText = amountTextView.getText().toString();
                String amountSubstring;
                if(inputCurrency.equalsIgnoreCase("€"))
                    amountSubstring = amountText.substring(0, amountText.length()-1);
                else
                    amountSubstring = amountText.substring(1);
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
                    }else if(inputModificaCategoria.equalsIgnoreCase("Trasporto")) {
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
                    String tmp;
                    if(inputCurrency.equalsIgnoreCase("€"))
                        tmp = spesa+inputCurrency;
                    else
                        tmp = inputCurrency+spesa;
                    amountTextView.setText(tmp);

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
        overlay_filter = inflater.inflate(R.layout.overlay_filter, rootLayout, false);
        rootLayout.addView(overlay_filter);
        overlay_filter.setVisibility(View.GONE);

        filterButton = view.findViewById(R.id.buttonFilter);
        backButtonFilter = view.findViewById(R.id.backButtonFilter);
        saveCategory = view.findViewById(R.id.saveCategory);
        closeFilter = view.findViewById(R.id.closeFilter);
        filterText = view.findViewById(R.id.testoFiltro);
        editTextCategoryFilter = view.findViewById(R.id.inputCategoryFilter);
        editTextCategoryFilter.setAdapter(adapterCategory);

        backButtonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_filter.setVisibility(View.GONE);
                addSpesa.setVisibility(View.VISIBLE);
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_filter.setVisibility(View.VISIBLE);
                editTextCategoryFilter.setText("", false);
                addSpesa.setVisibility(View.GONE);
            }
        });

        saveCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputCategoryFilter = editTextCategoryFilter.getText().toString().trim();
                if (inputCategoryFilter.isEmpty()) {
                    editTextCategoryFilter.setError("Scegli una categoria");
                } else {
                    filteredCards = filterByCategory(inputCategoryFilter);
                    spesaCardsContainer.removeAllViews();
                    for (View card : filteredCards) {
                        spesaCardsContainer.addView(card);
                    }
                    double countSpese = countAmountByCategory();
                    String tmp;
                    if(inputCurrency.equalsIgnoreCase("€"))
                        tmp = countSpese+inputCurrency;
                    else
                        tmp = inputCurrency+countSpese;
                    totSpesa.setText(tmp);
                    overlay_filter.setVisibility(View.GONE);
                    closeFilter.setVisibility(View.VISIBLE);
                    filterText.setVisibility(View.VISIBLE);
                    totSpesa.setVisibility(View.VISIBLE);
                    addSpesa.setVisibility(View.VISIBLE);
                    addSpesa.setEnabled(false);
                }
            }
        });

        closeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spesaCardsContainer.removeAllViews();
                for (View card : listSpesaCards) {
                    spesaCardsContainer.addView(card);
                }
                closeFilter.setVisibility(View.GONE);
                filterText.setVisibility(View.GONE);
                totSpesa.setVisibility(View.GONE);
                addSpesa.setEnabled(true);
            }
        });


        ScrollView scrollView = view.findViewById(R.id.scrollView);
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);

    }

    //nasconde la tastiera
    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //aggiorna il progress indicator delle spese
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

        formattedText = spent + " / " + budget + " spesi" + " (" + progressPercentage + "%)";
        progressText.setText(formattedText);
    }

    //conta le spese selezionate
    public int countSelectedCards() {
        int selectedCount = 0;
        for (MaterialCardView card : listSpesaCards) {
            if (card.isSelected())
                selectedCount++;
        }
        return selectedCount;  // Restituisce il numero di card selezionate
    }

    //trova la spesa selezionata nella lista di spese inserite
    public MaterialCardView findSelectedCard(){
        MaterialCardView selectedCard = listSpesaCards.get(0);
        for (MaterialCardView card : listSpesaCards) {
            if (card.isSelected())
                selectedCard = card;
        }
        return selectedCard;
    }

    //filtra le carte per una determinata categoria
    public ArrayList<MaterialCardView> filterByCategory(String category){
        ArrayList<MaterialCardView> filteredCards = new ArrayList<>();
        for (MaterialCardView card : listSpesaCards) {
            TextView categoryTextView = card.findViewById(R.id.categoryTextView);
            if (categoryTextView != null && categoryTextView.getText().toString().equalsIgnoreCase(category)) {
                filteredCards.add(card);
            }
        }
        return filteredCards;
    }

    //calcola la spesa totale per una determinata categoria
    public double countAmountByCategory(){
        double spesaTot = 0;
        for (MaterialCardView card : filteredCards) {
            TextView amountTextView = card.findViewById(R.id.amountTextView);
            String amount = amountTextView.getText().toString().trim();
            String realAmount = amount.substring(0, amount.length()-1);
            double spesa = Double.parseDouble(realAmount);
            spesaTot += spesa;
        }

        return spesaTot;
    }

    //modifica la valuta nell'overlay_add_spesa e nell'overlay_modifica_spesa
    public void updateCurrencyIcon(){
        TextInputLayout textQuantity = overlay_add_spesa.findViewById(R.id.textFieldQuantita);
        TextInputLayout textModifyQuantity = overlay_modifica_spesa.findViewById(R.id.textFieldModificaQuantita);
        if(inputCurrency.equalsIgnoreCase("€")){
            textQuantity.setStartIconDrawable(R.drawable.baseline_euro_24);
            textModifyQuantity.setStartIconDrawable(R.drawable.baseline_euro_24);
        }else if(inputCurrency.equalsIgnoreCase("$")){
            textQuantity.setStartIconDrawable(R.drawable.baseline_attach_money_24);
            textModifyQuantity.setStartIconDrawable(R.drawable.baseline_attach_money_24);
        }else if(inputCurrency.equalsIgnoreCase("£")){
            textQuantity.setStartIconDrawable(R.drawable.baseline_currency_pound_24);
            textModifyQuantity.setStartIconDrawable(R.drawable.baseline_currency_pound_24);
        }else if(inputCurrency.equalsIgnoreCase("¥")){
            textQuantity.setStartIconDrawable(R.drawable.baseline_currency_yen_24);
            textModifyQuantity.setStartIconDrawable(R.drawable.baseline_currency_yen_24);
        }
    }


}
