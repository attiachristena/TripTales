package com.unimib.triptales.ui.diary.overlay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.unimib.triptales.R;
import com.unimib.triptales.ui.diary.viewmodel.ExpenseViewModel;

import java.util.Arrays;
import java.util.List;

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

        List<String> categories = Arrays.asList(context.getString(R.string.shopping_category),
                context.getString(R.string.food_category),
                context.getString(R.string.transport_category),
                context.getString(R.string.accommodation_category),
                context.getString(R.string.culture_category),
                context.getString(R.string.fun_category));

        AutoCompleteTextView filterCategoryEditText = overlayView.findViewById(R.id.inputCategoryFilter);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_dropdown_item_1line, categories){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(ContextCompat.getColor(context, R.color.black));
                return textView;
            }
        };
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
