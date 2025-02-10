package com.unimib.triptales.ui.diary.overlay;

import static com.unimib.triptales.util.Constants.CATEGORIES;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;

import com.unimib.triptales.R;
import com.unimib.triptales.ui.diary.viewmodel.ExpenseViewModel;

public class OverlayFilter {
    private final Context context;
    private final ViewGroup rootLayout;
    private final ExpenseViewModel expenseViewModel;
    private final View overlayView;

    public OverlayFilter(Context context, ViewGroup rootLayout, ExpenseViewModel expenseViewModel) {
        this.context = context;
        this.rootLayout = rootLayout;
        this.expenseViewModel = expenseViewModel;
        LayoutInflater inflater = LayoutInflater.from(context);
        overlayView = inflater.inflate(R.layout.overlay_filter, rootLayout, false);
    }

    public void showOverlay(){
        ImageButton filterBackButton = overlayView.findViewById(R.id.backButtonFilter);
        Button saveCategoryButton = overlayView.findViewById(R.id.saveCategory);

        AutoCompleteTextView filterCategoryEditText = overlayView.findViewById(R.id.inputCategoryFilter);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_dropdown_item_1line, CATEGORIES);
        filterCategoryEditText.setAdapter(categoryAdapter);

        filterCategoryEditText.setText("", false);

        filterBackButton.setOnClickListener(filterBackButtonListener -> {
            expenseViewModel.setFilterOverlayVisibility(false);
            hideOverlay();
            expenseViewModel.setBFilter(false);
        });

        saveCategoryButton.setOnClickListener(saveCategoryButtonListener -> {
            String inputFilterCategory = filterCategoryEditText.getText().toString().trim();
            boolean correct = expenseViewModel.validateInputFilter(inputFilterCategory);
            if(correct) {
                expenseViewModel.filterExpenses(inputFilterCategory);
                expenseViewModel.setFilterOverlayVisibility(false);
                hideOverlay();
                expenseViewModel.setBFilter(true);
            }
        });
        rootLayout.addView(overlayView);
    }

    public void hideOverlay(){
        rootLayout.removeView(overlayView);
    }
}
