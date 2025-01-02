package com.unimib.triptales.ui.homepage.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.unimib.triptales.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddDiaryFragment extends Fragment {

    private EditText inputDayStartDate, inputMonthStartDate, inputYearStartDate;
    private EditText inputDayEndDate, inputMonthEndDate, inputYearEndDate;
    private Button buttonChooseImage, buttonSave;
    private ImageView imageViewCover;
    private Uri selectedImageUri;

    private final Calendar calendar = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_diary, container, false);

        inputDayStartDate = view.findViewById(R.id.inputDayDeparture);
        inputMonthStartDate = view.findViewById(R.id.inputMonthDeparture);
        inputYearStartDate = view.findViewById(R.id.inputYearDeparture);
        inputDayEndDate = view.findViewById(R.id.inputDayArrival);
        inputMonthEndDate = view.findViewById(R.id.inputMonthArrival);
        inputYearEndDate = view.findViewById(R.id.inputYearArrival);
        buttonChooseImage = view.findViewById(R.id.buttonChooseImage);
        buttonSave = view.findViewById(R.id.buttonSave);
        imageViewCover = view.findViewById(R.id.imageViewSelected);

        // Date picker setup for start date
        setupDatePicker(inputDayStartDate, inputMonthStartDate, inputYearStartDate);

        // Image picker setup
        buttonChooseImage.setOnClickListener(v -> openImagePicker());

        // Save button logic
        buttonSave.setOnClickListener(v -> saveDiary());

        return view;
    }

    private void setupDatePicker(EditText dayField, EditText monthField, EditText yearField) {
        View.OnClickListener listener = v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        dayField.setText(String.format(Locale.getDefault(), "%02d", dayOfMonth));
                        monthField.setText(String.format(Locale.getDefault(), "%02d", month + 1));
                        yearField.setText(String.format(Locale.getDefault(), "%04d", year));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        };

        dayField.setOnClickListener(listener);
        monthField.setOnClickListener(listener);
        yearField.setOnClickListener(listener);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imageViewCover.setImageURI(selectedImageUri);
            imageViewCover.setVisibility(View.VISIBLE);  // Rendere visibile l'immagine selezionata
        }
    }

    private void saveDiary() {
        String dayStartDate = inputDayStartDate.getText().toString().trim();
        String monthStartDate = inputMonthStartDate.getText().toString().trim();
        String yearStartDate = inputYearStartDate.getText().toString().trim();
        String dayEndDate = inputDayEndDate.getText().toString().trim();
        String monthEndDate = inputMonthEndDate.getText().toString().trim();
        String yearEndDate = inputYearEndDate.getText().toString().trim();

        // Controllo che i campi non siano vuoti
        if (TextUtils.isEmpty(dayStartDate) || TextUtils.isEmpty(monthStartDate) || TextUtils.isEmpty(yearStartDate) ||
                TextUtils.isEmpty(dayEndDate) || TextUtils.isEmpty(monthEndDate) || TextUtils.isEmpty(yearEndDate)) {
            Toast.makeText(getContext(), "Le date non possono essere vuote!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Controllo che il giorno sia compreso tra 1 e 31
        if (!isValidDay(dayStartDate) || !isValidDay(dayEndDate)) {
            Toast.makeText(getContext(), "Giorno non valido!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Controllo che il mese sia compreso tra 1 e 12
        if (!isValidMonth(monthStartDate) || !isValidMonth(monthEndDate)) {
            Toast.makeText(getContext(), "Mese non valido!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Controllo che l'anno sia maggiore o uguale a 1985
        if (!isValidYear(yearStartDate) || !isValidYear(yearEndDate)) {
            Toast.makeText(getContext(), "L'anno deve essere 1985 o successivo!", Toast.LENGTH_SHORT).show();
            return;
        }

        String startDate = dayStartDate + "/" + monthStartDate + "/" + yearStartDate;
        String endDate = dayEndDate + "/" + monthEndDate + "/" + yearEndDate;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            if (sdf.parse(startDate).after(sdf.parse(endDate))) {
                Toast.makeText(getContext(), "La data di partenza deve precedere quella di ritorno!", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Errore nel formato delle date!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri == null) {
            Toast.makeText(getContext(), "Seleziona un'immagine per la copertina!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Salvataggio logico del diario (può essere esteso per integrare un database o altra logica)
        Toast.makeText(getContext(), "Diario salvato con successo!", Toast.LENGTH_SHORT).show();
    }

    private boolean isValidDay(String day) {
        try {
            int dayInt = Integer.parseInt(day);
            return dayInt >= 1 && dayInt <= 31;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidMonth(String month) {
        try {
            int monthInt = Integer.parseInt(month);
            return monthInt >= 1 && monthInt <= 12;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidYear(String year) {
        try {
            int yearInt = Integer.parseInt(year);
            return yearInt >= 1985;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
