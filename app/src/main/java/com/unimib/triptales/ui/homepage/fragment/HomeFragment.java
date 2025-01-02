package com.unimib.triptales.ui.homepage.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.unimib.triptales.R;
import com.unimib.triptales.adapters.Diary;
import com.unimib.triptales.adapters.DiaryAdapter;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private DiaryAdapter diaryAdapter;
    private List<Diary> diaryList = new ArrayList<>();
    private TextView emptyMessage;
    private EditText inputDayStartDate, inputMonthStartDate, inputYearStartDate;
    private EditText inputDayEndDate, inputMonthEndDate, inputYearEndDate;
    private Button buttonChooseImage, buttonSave;
    private  TextView inputDiaryName;
    private ImageView imageViewCover;
    private Uri selectedImageUri;
    private RelativeLayout rootLayoutHome;
    private View overlayAddDiary;
    private final Calendar calendar = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflazione del layout principale del fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inizializzazione dei componenti del layout
        rootLayoutHome = view.findViewById(R.id.root_layout_home);
        overlayAddDiary = inflater.inflate(R.layout.fragment_add_diary, rootLayoutHome, false);
        rootLayoutHome.addView(overlayAddDiary);
        overlayAddDiary.setVisibility(View.GONE);



        recyclerView = view.findViewById(R.id.recycler_view_diaries);
        emptyMessage = view.findViewById(R.id.text_empty_message);
        FloatingActionButton buttonAddDiary = view.findViewById(R.id.fab_add_diary);

        inputDiaryName = overlayAddDiary.findViewById(R.id.inputDiaryName);
        inputDayStartDate = overlayAddDiary.findViewById(R.id.inputDayDeparture);
        inputMonthStartDate = overlayAddDiary.findViewById(R.id.inputMonthDeparture);
        inputYearStartDate = overlayAddDiary.findViewById(R.id.inputYearDeparture);


        inputDayEndDate = overlayAddDiary.findViewById(R.id.inputReturnDay);
        inputMonthEndDate = overlayAddDiary.findViewById(R.id.inputReturnMonth);
        inputYearEndDate = overlayAddDiary.findViewById(R.id.inputReturnYear);


        buttonSave = overlayAddDiary.findViewById(R.id.buttonSave);
        buttonChooseImage = overlayAddDiary.findViewById(R.id.buttonChooseImage);
        imageViewCover = overlayAddDiary.findViewById(R.id.imageViewSelected);
        MaterialButton closeOverlayButton = overlayAddDiary.findViewById(R.id.buttonCancel);
        closeOverlayButton.setOnClickListener(v -> overlayAddDiary.setVisibility(View.GONE));

        // Impostazione del RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        diaryAdapter = new DiaryAdapter(getContext(), diaryList);
        recyclerView.setAdapter(diaryAdapter);

        // Listener per l'aggiunta di un nuovo diario
        buttonAddDiary.setOnClickListener(v -> overlayAddDiary.setVisibility(View.VISIBLE));
        closeOverlayButton.setOnClickListener(v -> overlayAddDiary.setVisibility(View.GONE));

        // Listener per i bottoni di salvataggio e scelta immagine
        buttonSave.setOnClickListener(v -> saveDiary());
        buttonChooseImage.setOnClickListener(v -> openImagePicker());

        // Impostazione dei date picker
        setupDatePicker(inputDayStartDate, inputMonthStartDate, inputYearStartDate);
        setupDatePicker(inputDayEndDate, inputMonthEndDate, inputYearEndDate);

        updateEmptyMessage();

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
        String diaryName = inputDiaryName.getText().toString().trim();


        if (TextUtils.isEmpty(dayStartDate) || TextUtils.isEmpty(monthStartDate) || TextUtils.isEmpty(yearStartDate) ||
                TextUtils.isEmpty(dayEndDate) || TextUtils.isEmpty(monthEndDate) || TextUtils.isEmpty(yearEndDate)) {
            Toast.makeText(getContext(), "Le date non possono essere vuote!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidDay(dayStartDate) || !isValidDay(dayEndDate) ||
                !isValidMonth(monthStartDate) || !isValidMonth(monthEndDate) ||
                !isValidYear(yearStartDate) || !isValidYear(yearEndDate)) {
            Toast.makeText(getContext(), "Date non valide!", Toast.LENGTH_SHORT).show();
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


        diaryList.add(new Diary(diaryName,startDate, selectedImageUri));
        diaryAdapter.notifyDataSetChanged();
        overlayAddDiary.setVisibility(View.GONE);
        updateEmptyMessage();
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

    private void updateEmptyMessage() {
        if (diaryList.isEmpty()) {
            emptyMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
