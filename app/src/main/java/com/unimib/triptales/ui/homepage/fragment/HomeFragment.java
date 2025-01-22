package com.unimib.triptales.ui.homepage.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.unimib.triptales.R;
import com.unimib.triptales.adapters.DiaryAdapter;
import com.unimib.triptales.database.AppRoomDatabase;
import com.unimib.triptales.database.DiaryDao;
import com.unimib.triptales.model.Diary;
import com.unimib.triptales.ui.homepage.viewmodel.SharedViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements DiaryAdapter.OnDiaryItemLongClickListener {

    private RecyclerView recyclerView;
    private DiaryAdapter diaryAdapter;
    private List<Diary> diaryList = new ArrayList<>();
    private TextView emptyMessage;
    private FloatingActionButton addDiaryButton, deleteDiaryButton, modifyDiaryButton;

    private ConstraintLayout rootLayoutHome;
    private View overlayAddDiary, overlayModifyDiary;
    private EditText inputDayStartDate, inputMonthStartDate, inputYearStartDate;
    private EditText inputDayEndDate, inputMonthEndDate, inputYearEndDate;
    private EditText modifyDayStartDate, modifyMonthStartDate, modifyYearStartDate;
    private EditText modifyDayEndDate, modifyMonthEndDate, modifyYearEndDate;
    private View Country;
    private View CountryChanges;

    private EditText inputDiaryName, modifyDiaryName;
    private ImageView imageViewCover, modifyCoverImage;
    private Button buttonChooseImage, buttonSave, buttonSaveModify, buttonChooseImageChanges;
    private ImageButton closeAddOverlayButton, closeModifyOverlayButton;
    private Uri selectedImageUri;
    private Diary selectedDiary;
    private final Calendar calendar = Calendar.getInstance();

    private ArrayList<Diary> selectedDiaries = new ArrayList<>();
    private View imageViewSelectedChanges;
    private String country;
    private SharedViewModel sharedViewModel;
    private int id;
    private int idUser;
    private String budget;
    AppRoomDatabase database;
    private DiaryDao diaryDao;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initializeViews(view, inflater);
        setupRecyclerView();
        setupButtonListeners();
        setupDatePicker(inputDayStartDate, inputMonthStartDate, inputYearStartDate);
        setupDatePicker(inputDayEndDate, inputMonthEndDate, inputYearEndDate);
        setupDatePicker(modifyDayStartDate, modifyMonthStartDate, modifyYearStartDate);
        setupDatePicker(modifyDayEndDate, modifyMonthEndDate, modifyYearEndDate);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        return view;
    }

    private void initializeViews(View view, LayoutInflater inflater) {
        rootLayoutHome = view.findViewById(R.id.root_layout_home);
        overlayAddDiary = inflater.inflate(R.layout.overlay_add_diary, rootLayoutHome, false);
        overlayModifyDiary = inflater.inflate(R.layout.overlay_modify_diary, rootLayoutHome, false);

        rootLayoutHome.addView(overlayAddDiary);
        rootLayoutHome.addView(overlayModifyDiary);

        overlayAddDiary.setVisibility(View.GONE);
        overlayModifyDiary.setVisibility(View.GONE);

        recyclerView = view.findViewById(R.id.recycler_view_diaries);
        emptyMessage = view.findViewById(R.id.text_empty_message);

        addDiaryButton = view.findViewById(R.id.fab_add_diary);
        deleteDiaryButton = view.findViewById(R.id.deleteDiaryButton);
        modifyDiaryButton = view.findViewById(R.id.modifyDiaryButton);

        inputDiaryName = overlayAddDiary.findViewById(R.id.inputDiaryName);
        Country = overlayAddDiary.findViewById(R.id.VisitedCountry);
        CountryChanges = overlayModifyDiary.findViewById(R.id.VisitedCountryChanges);
        inputDayStartDate = overlayAddDiary.findViewById(R.id.inputDayDeparture);
        inputMonthStartDate = overlayAddDiary.findViewById(R.id.inputMonthDeparture);
        inputYearStartDate = overlayAddDiary.findViewById(R.id.inputYearDeparture);
        inputDayEndDate = overlayAddDiary.findViewById(R.id.inputReturnDay);
        inputMonthEndDate = overlayAddDiary.findViewById(R.id.inputReturnMonth);
        inputYearEndDate = overlayAddDiary.findViewById(R.id.inputReturnYear);
        imageViewCover = overlayAddDiary.findViewById(R.id.imageViewSelected);
        buttonSave = overlayAddDiary.findViewById(R.id.buttonSaveDiary);
        buttonChooseImage = overlayAddDiary.findViewById(R.id.buttonChooseImage);
        buttonChooseImageChanges = overlayModifyDiary.findViewById(R.id.buttonChooseImageChanges);
        closeAddOverlayButton = overlayAddDiary.findViewById(R.id.backaddDiaryButton);
        imageViewSelectedChanges = overlayModifyDiary.findViewById(R.id.imageViewSelectedChanges);

        modifyDiaryName = overlayModifyDiary.findViewById(R.id.inputDiaryNameChanges);
        modifyDayStartDate = overlayModifyDiary.findViewById(R.id.inputDayDepartureChanges);
        modifyMonthStartDate = overlayModifyDiary.findViewById(R.id.inputMonthDepartureChanges);
        modifyYearStartDate = overlayModifyDiary.findViewById(R.id.inputYearDepartureChanges);
        modifyDayEndDate = overlayModifyDiary.findViewById(R.id.inputReturnDayChanges);
        modifyMonthEndDate = overlayModifyDiary.findViewById(R.id.inputReturnMonthChanges);
        modifyYearEndDate = overlayModifyDiary.findViewById(R.id.inputReturnYearChanges);
        modifyCoverImage = overlayModifyDiary.findViewById(R.id.imageViewSelectedChanges);
        buttonSaveModify = overlayModifyDiary.findViewById(R.id.buttonSaveDiaryChanges);
        closeModifyOverlayButton = overlayModifyDiary.findViewById(R.id.backModifyDiaryButton);


        // Configura l'AutoCompleteTextView per i paesi
        setupAutoCompleteTextView(overlayAddDiary, overlayModifyDiary);
        // Inizializzazione del database e DAO
        database = AppRoomDatabase.getDatabase(getContext());
        diaryDao = database.diaryDao();
        diaryList = diaryDao.getAllDiaries(); // Recupera le diary salvati
    }



    private List<String> extractCountryNamesFromJson(Context context) {
        List<String> countryNames = new ArrayList<>();
        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.world_countries);

            StringBuilder builder = new StringBuilder();
            int byteData;
            while ((byteData = inputStream.read()) != -1) {
                builder.append((char) byteData);
            }

            JSONObject jsonObject = new JSONObject(builder.toString());
            JSONArray features = jsonObject.getJSONArray("features");

            for (int i = 0; i < features.length(); i++) {
                JSONObject feature = features.getJSONObject(i);
                JSONObject properties = feature.getJSONObject("properties");
                String countryName = properties.getString("NAME_IT");
                countryNames.add(countryName);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return countryNames;
    }

    private void setupAutoCompleteTextView(View overlayAddDiary, View overlayModifyDiary) {
        AutoCompleteTextView countryAutoComplete = overlayAddDiary.findViewById(R.id.VisitedCountry);
        AutoCompleteTextView countryChangesAutoComplete = overlayModifyDiary.findViewById(R.id.VisitedCountryChanges);

        List<String> countryNames = extractCountryNamesFromJson(requireContext());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, countryNames);

        countryAutoComplete.setAdapter(adapter);
        countryChangesAutoComplete.setAdapter(adapter);
    }




    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        diaryAdapter = new DiaryAdapter(getContext(), diaryList, this);
        recyclerView.setAdapter(diaryAdapter);
        updateEmptyMessage();
    }

    private void setupButtonListeners() {
        addDiaryButton.setOnClickListener(v -> showOverlay(overlayAddDiary));
        closeAddOverlayButton.setOnClickListener(v -> hideOverlay(overlayAddDiary));

        closeModifyOverlayButton.setOnClickListener(v -> {

            // Deseleziona il diario dopo averlo modificato
            selectedDiaries.remove(selectedDiary);

            // Notifica all'adapter che la lista è cambiata
            diaryAdapter.notifyDataSetChanged();

            // Aggiungi questa riga per deselezionare tutti i diari
            diaryAdapter.clearSelections(); // Deseleziona tutti i diari

            // Nascondi l'overlay di modifica e aggiorna i bottoni
            hideOverlay(overlayModifyDiary);
        });





        buttonSave.setOnClickListener(v -> saveDiary());
        buttonSaveModify.setOnClickListener(v -> modifyDiary());

        buttonChooseImage.setOnClickListener(v -> openImagePicker());
        buttonChooseImageChanges.setOnClickListener(v -> openImagePicker());

        deleteDiaryButton.setOnClickListener(v -> {
            deleteSelectedDiaries();
            updateEmptyMessage();
            // Deseleziona il diario dopo averlo modificato
            selectedDiaries.remove(selectedDiary);

            // Notifica all'adapter che la lista è cambiata
            diaryAdapter.notifyDataSetChanged();

            // Aggiungi questa riga per deselezionare tutti i diari
            diaryAdapter.clearSelections(); //
            deleteDiaryButton.setVisibility(View.GONE);
            modifyDiaryButton.setVisibility(View.GONE);
        });

        modifyDiaryButton.setOnClickListener(v -> {
            if (selectedDiaries.size() == 1) {
                selectedDiary = selectedDiaries.get(0);
                populateModifyOverlay(selectedDiary);
                showOverlay(overlayModifyDiary);
            } else {
                Toast.makeText(getContext(), "Seleziona un solo diario per modificarlo.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showOverlay(View overlay) {
        overlay.setVisibility(View.VISIBLE);
        addDiaryButton.setVisibility(View.GONE);
        deleteDiaryButton.setVisibility(View.GONE);
        modifyDiaryButton.setVisibility(View.GONE);
    }

    private void hideOverlay(View overlay) {
        overlay.setVisibility(View.GONE);
        addDiaryButton.setVisibility(View.VISIBLE);
        resetDiaryFields();
        selectedDiary = null;
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
            imageViewCover.setVisibility(View.VISIBLE);
        }
    }

    private void saveDiary() {
        String diaryName = inputDiaryName.getText().toString();
        sharedViewModel.setDiaryName(diaryName);
        String startDate = inputDayStartDate.getText().toString() + "/" + inputMonthStartDate.getText().toString() + "/" + inputYearStartDate.getText().toString();
        sharedViewModel.setStartDate(startDate);
        String endDate = inputDayEndDate.getText().toString() + "/" + inputMonthEndDate.getText().toString() + "/" + inputYearEndDate.getText().toString();
        sharedViewModel.setEndDate(endDate);
        String country = ((AutoCompleteTextView) overlayAddDiary.findViewById(R.id.VisitedCountry)).getText().toString();
        sharedViewModel.setDiaryCountry(country);
        if (diaryName.isEmpty() || selectedImageUri == null || startDate.isEmpty() || endDate.isEmpty()|| country.isEmpty()) {
            Toast.makeText(getContext(), "Compila tutti i campi e scegli un'immagine!", Toast.LENGTH_SHORT).show();
            return;
        }

        Diary newDiary = new Diary(id, idUser, diaryName, startDate, endDate, selectedImageUri, budget, country);
        diaryList.add(newDiary);
        diaryAdapter.notifyDataSetChanged();
        hideOverlay(overlayAddDiary);
        updateEmptyMessage();
        Toast.makeText(getContext(), "Diario salvato con successo!", Toast.LENGTH_SHORT).show();
    }

    private void modifyDiary() {
        if (selectedDiary == null) return;

        String diaryName = modifyDiaryName.getText().toString();
        String startDate = modifyDayStartDate.getText().toString() + "/" + modifyMonthStartDate.getText().toString() + "/" + modifyYearStartDate.getText().toString();
        String endDate = modifyDayEndDate.getText().toString() + "/" + modifyMonthEndDate.getText().toString() + "/" + modifyYearEndDate.getText().toString();
        String country = ((AutoCompleteTextView) overlayModifyDiary.findViewById(R.id.VisitedCountryChanges)).getText().toString();

        if (diaryName.isEmpty() || startDate.isEmpty() || endDate.isEmpty()|| country.isEmpty()) {
            Toast.makeText(getContext(), "Compila tutti i campi!", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedDiary.setName(diaryName);
        selectedDiary.setStartDate(startDate);
        selectedDiary.setEndDate(endDate);
        selectedDiary.setCountry(country);

        if (selectedImageUri != null) {
            selectedDiary.setCoverImageUri(selectedImageUri);
        }

        // Deseleziona il diario dopo averlo modificato
        selectedDiaries.remove(selectedDiary);

        // Notifica all'adapter che la lista è cambiata
        diaryAdapter.notifyDataSetChanged();

        // Aggiungi questa riga per deselezionare tutti i diari
        diaryAdapter.clearSelections(); // Deseleziona tutti i diari

        // Nascondi l'overlay di modifica e aggiorna i bottoni
        hideOverlay(overlayModifyDiary);

        // Aggiorna la visibilità dei pulsanti
        modifyDiaryButton.setVisibility(View.GONE); // Rendi visibile il bottone "Modifica"
        deleteDiaryButton.setVisibility(selectedDiaries.isEmpty() ? View.GONE : View.VISIBLE); // Nascondi "Elimina" se nessun diario è selezionato

        Toast.makeText(getContext(), "Diario modificato con successo!", Toast.LENGTH_SHORT).show();
    }



    private void deleteSelectedDiaries() {
        diaryList.removeAll(selectedDiaries);
        diaryAdapter.notifyDataSetChanged();
        Toast.makeText(getContext(), "Diari eliminati con successo!", Toast.LENGTH_SHORT).show();
        selectedDiaries.clear();
    }

    private void populateModifyOverlay(Diary diary) {
        modifyDiaryName.setText(diary.getName());

        // Ripristina la data di inizio
        String[] startDate = diary.getStartDate().split("/");
        modifyDayStartDate.setText(startDate[0]);
        modifyMonthStartDate.setText(startDate[1]);
        modifyYearStartDate.setText(startDate[2]);


        // Ripristina la data di fine
        String[] endDate = diary.getEndDate().split("/");
        modifyDayEndDate.setText(endDate[0]);
        modifyMonthEndDate.setText(endDate[1]);
        modifyYearEndDate.setText(endDate[2]);

        // Ripristina l'immagine
        Uri coverImageUri = diary.getCoverImageUri();
        if (coverImageUri != null) {
            modifyCoverImage.setImageURI(coverImageUri);
            modifyCoverImage.setVisibility(View.VISIBLE);  // Assicurati che l'immagine sia visibile
        } else {
            modifyCoverImage.setVisibility(View.GONE);  // Se non c'è un'immagine, nascondila
        }

        // Ripristina il paese
        AutoCompleteTextView countryChangesAutoComplete = overlayModifyDiary.findViewById(R.id.VisitedCountryChanges);
        countryChangesAutoComplete.setText(diary.getCountry());
    }


    private void resetDiaryFields() {
        inputDiaryName.setText("");
        inputDayStartDate.setText("");
        inputMonthStartDate.setText("");
        inputYearStartDate.setText("");
        inputDayEndDate.setText("");
        inputMonthEndDate.setText("");
        inputYearEndDate.setText("");
        imageViewCover.setImageURI(null);
        imageViewCover.setVisibility(View.GONE);

        modifyDiaryName.setText("");
        modifyDayStartDate.setText("");
        modifyMonthStartDate.setText("");
        modifyYearStartDate.setText("");
        modifyDayEndDate.setText("");
        modifyMonthEndDate.setText("");
        modifyYearEndDate.setText("");
        modifyCoverImage.setImageURI(null);
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



    public void onDiaryItemLongClicked(Diary diary) {

        if (selectedDiaries.contains(diary)) {
            selectedDiaries.remove(diary);
        } else {
            selectedDiaries.add(diary);
        }

        // Controlla il numero di diari selezionati
        if (selectedDiaries.size() > 1) {
            modifyDiaryButton.setVisibility(View.GONE); // Nascondi il bottone "Modifica"
        } else {
            modifyDiaryButton.setVisibility(View.VISIBLE); // Mostra il bottone "Modifica"
        }

        // Rende visibili o nasconde gli altri bottoni (elimina, ecc.)
        deleteDiaryButton.setVisibility(selectedDiaries.isEmpty() ? View.GONE : View.VISIBLE);
    }

}
