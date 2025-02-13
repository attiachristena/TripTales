package com.unimib.triptales.ui.homepage.overlay;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.unimib.triptales.R;
import com.unimib.triptales.model.Diary;
import com.unimib.triptales.ui.homepage.fragment.HomeFragment;
import com.unimib.triptales.ui.homepage.viewmodel.HomeViewModel;
import com.unimib.triptales.util.GeoJSONParser;


import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class OverlayAddEditDiary {
    private final Context context;
    private final ViewGroup rootLayout;
    private final View overlayView;
    private String selectedImageUri;
    private Calendar calendar;
    private final HomeFragment homeFragment;
    private final ImageView imageViewCover;

    public OverlayAddEditDiary(ViewGroup rootLayout, Context context, HomeFragment homeFragment) {
        this.rootLayout = rootLayout;
        this.context = context;
        this.homeFragment = homeFragment;
        LayoutInflater inflater = LayoutInflater.from(context);
        overlayView = inflater.inflate(R.layout.overlay_add_edit_diary, rootLayout, false);

        imageViewCover = overlayView.findViewById(R.id.imageViewSelected);

    }

    private void setupAutoCompleteTextView() {
        AutoCompleteTextView countryAutoComplete = overlayView.findViewById(R.id.VisitedCountry);
        if (countryAutoComplete != null) {
            GeoJSONParser.setupAutoCompleteTextView(context, countryAutoComplete);
        }
    }

    private void setupDatePicker(EditText dayField, EditText monthField, EditText yearField) {
        View.OnClickListener listener = v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    context,
                    (view, year, month, dayOfMonth) -> {
                        dayField.setText(String.format(Locale.getDefault(), "%02d", dayOfMonth));
                        monthField.setText(String.format(Locale.getDefault(), "%02d", month + 1));
                        yearField.setText(String.format(Locale.getDefault(), "%04d", year));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            Objects.requireNonNull(datePickerDialog.getWindow())
                    .setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            datePickerDialog.show();
        };

        dayField.setOnClickListener(listener);
        monthField.setOnClickListener(listener);
        yearField.setOnClickListener(listener);
    }


    public void showOverlay(HomeViewModel homeViewModel, boolean bAdd, boolean bEdit) {
        if (overlayView.getParent() == null) { // Evita di aggiungere più volte l'overlay
            rootLayout.addView(overlayView);
        }

        ImageButton closeButton = overlayView.findViewById(R.id.backaddDiaryButton);
        Button saveButton = overlayView.findViewById(R.id.buttonSaveDiary);
        EditText diaryName = overlayView.findViewById(R.id.inputDiaryName);
        EditText dayStartDate = overlayView.findViewById(R.id.inputDayDeparture);
        EditText monthStartDate = overlayView.findViewById(R.id.inputMonthDeparture);
        EditText yearStartDate = overlayView.findViewById(R.id.inputYearDeparture);
        EditText dayEndDate = overlayView.findViewById(R.id.inputReturnDay);
        EditText monthEndDate = overlayView.findViewById(R.id.inputReturnMonth);
        EditText yearEndDate = overlayView.findViewById(R.id.inputReturnYear);
        AutoCompleteTextView countryField = overlayView.findViewById(R.id.VisitedCountry);
        Button buttonChooseImage = overlayView.findViewById(R.id.buttonChooseImage);
        setupAutoCompleteTextView();

        calendar = Calendar.getInstance();
        setupDatePicker(dayStartDate, monthStartDate, yearStartDate);
        setupDatePicker(dayEndDate, monthEndDate, yearEndDate);

        setupDateFieldFocus();

        if (bAdd) {
            resetFields(diaryName, dayStartDate, monthStartDate, yearStartDate, dayEndDate, monthEndDate, yearEndDate, imageViewCover, countryField);
        } else if (bEdit) {
            populateFields(homeViewModel, diaryName, dayStartDate, monthStartDate, yearStartDate, dayEndDate, monthEndDate, yearEndDate, imageViewCover, countryField);
        }

        if (closeButton != null) {
            closeButton.setOnClickListener(v -> {
                homeViewModel.setDiaryOverlayVisibility(false);
                hideOverlay();
            });
        }

        if (saveButton != null) {
            saveButton.setOnClickListener(v -> {
                String name = diaryName != null ? diaryName.getText().toString() : "";
                String startDate = (dayStartDate != null ? dayStartDate.getText().toString() : "") + "/" +
                        (monthStartDate != null ? monthStartDate.getText().toString() : "") + "/" +
                        (yearStartDate != null ? yearStartDate.getText().toString() : "");

                String endDate = (dayEndDate != null ? dayEndDate.getText().toString() : "") + "/" +
                        (monthEndDate != null ? monthEndDate.getText().toString() : "") + "/" +
                        (yearEndDate != null ? yearEndDate.getText().toString() : "");

                String country = countryField != null ? countryField.getText().toString() : "";
                String imageUri = selectedImageUri != null ? selectedImageUri : "";

                boolean correct = homeViewModel.validateDiaryInput(name, startDate, endDate, imageUri, country, bAdd);
                if (correct) {
                    if (bAdd) {
                        homeViewModel.insertDiary(name, startDate, endDate, imageUri, null, country);
                    } else if (bEdit) {
                        List<Diary> selectedDiaries = homeViewModel.getSelectedDiariesLiveData().getValue();
                        if (selectedDiaries != null && !selectedDiaries.isEmpty()) {
                            Diary currentDiary = selectedDiaries.get(0);
                            homeViewModel.updateDiary(currentDiary, name, startDate, endDate, imageUri, country);
                            homeViewModel.deselectAllDiaries();
                        }
                    }
                    homeViewModel.setDiaryOverlayVisibility(false);
                    hideOverlay();
                }
            });

            buttonChooseImage.setOnClickListener(v -> openImagePicker());


        }
    }

    private void openImagePicker() {
        if (homeFragment != null) {
            homeFragment.openImagePicker(); // Usa il metodo di HomeFragment
        }
    }
    
    public void hideOverlay() {
        rootLayout.removeView(overlayView);
    }

    public String getCountry() {
        AutoCompleteTextView countryField = overlayView.findViewById(R.id.VisitedCountry);
        return countryField.getText().toString();
    }

    private void populateFields(HomeViewModel homeViewModel, EditText diaryName, EditText dayStartDate, EditText monthStartDate, EditText yearStartDate, EditText dayEndDate, EditText monthEndDate, EditText yearEndDate, ImageView imageViewCover, AutoCompleteTextView countryField) {
        List<Diary> selectedDiaries = homeViewModel.getSelectedDiariesLiveData().getValue();
        if (selectedDiaries != null && !selectedDiaries.isEmpty()) {
            Diary currentDiary = selectedDiaries.get(0);
            diaryName.setText(currentDiary.getName());
            String[] startDateParts = currentDiary.getStartDate().split("/");
            dayStartDate.setText(startDateParts[0]);
            monthStartDate.setText(startDateParts[1]);
            yearStartDate.setText(startDateParts[2]);
            String[] endDateParts = currentDiary.getEndDate().split("/");
            dayEndDate.setText(endDateParts[0]);
            monthEndDate.setText(endDateParts[1]);
            yearEndDate.setText(endDateParts[2]);
            if (currentDiary.getCoverImageUri() != null) {
                Glide.with(context).load(Uri.parse(currentDiary.getCoverImageUri())).into(imageViewCover);
            }
            countryField.setText(currentDiary.getCountry());
        }
    }

    private void resetFields(EditText diaryName, EditText dayStartDate, EditText monthStartDate, EditText yearStartDate, EditText dayEndDate, EditText monthEndDate, EditText yearEndDate, ImageView imageViewCover, AutoCompleteTextView countryField) {
        diaryName.setText("");
        dayStartDate.setText("");
        monthStartDate.setText("");
        yearStartDate.setText("");
        dayEndDate.setText("");
        monthEndDate.setText("");
        yearEndDate.setText("");
        imageViewCover.setImageURI(null);
        imageViewCover.setVisibility(View.GONE);
        countryField.setText("");
    }

    private void setupDateFieldFocus() {
        EditText inputDayStartDate = overlayView.findViewById(R.id.inputDayDeparture);
        EditText inputMonthStartDate = overlayView.findViewById(R.id.inputMonthDeparture);
        EditText inputYearStartDate = overlayView.findViewById(R.id.inputYearDeparture);
        EditText inputDayEndDate = overlayView.findViewById(R.id.inputReturnDay);
        EditText inputMonthEndDate = overlayView.findViewById(R.id.inputReturnMonth);
        EditText inputYearEndDate = overlayView.findViewById(R.id.inputReturnYear);

        if (inputDayStartDate != null && inputMonthStartDate != null && inputYearStartDate != null) {
            inputDayStartDate.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() == 2) {
                        inputMonthStartDate.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });

            inputMonthStartDate.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() == 2) {
                        inputYearStartDate.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });
        }

        if (inputDayEndDate != null && inputMonthEndDate != null && inputYearEndDate != null) {
            inputDayEndDate.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() == 2) {
                        inputMonthEndDate.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });

            inputMonthEndDate.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() == 2) {
                        inputYearEndDate.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });
        }
    }

    public void updateImageView(Uri imageUri) {
        if (imageViewCover != null) {
            imageViewCover.setImageURI(imageUri);
            imageViewCover.setVisibility(View.VISIBLE);
            selectedImageUri = imageUri.toString();
        } else {
            throw new NullPointerException(context.getString(R.string.errore_immagine));
        }
    }

}
