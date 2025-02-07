package com.unimib.triptales.ui.homepage.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unimib.triptales.R;
import com.unimib.triptales.adapters.DiaryAdapter;
import com.unimib.triptales.database.AppRoomDatabase;
import com.unimib.triptales.database.CheckpointDao;
import com.unimib.triptales.database.DiaryDao;
import com.unimib.triptales.database.UserDao;
import com.unimib.triptales.model.Checkpoint;
import com.unimib.triptales.model.Diary;
import com.unimib.triptales.model.User;
import com.unimib.triptales.ui.diary.fragment.CheckpointsFragment;
import com.unimib.triptales.ui.homepage.viewmodel.SharedViewModel;
import com.unimib.triptales.ui.login.viewmodel.UserViewModel;
import com.unimib.triptales.util.SharedPreferencesUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class HomeFragment extends Fragment implements DiaryAdapter.OnDiaryItemLongClickListener {

    private static final String TAG = HomeFragment.class.getSimpleName();

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
    private String selectedImageUri;
    private Diary selectedDiary;
    private final Calendar calendar = Calendar.getInstance();

    private ArrayList<Diary> selectedDiaries = new ArrayList<>();
    private View imageViewSelectedChanges;
    private String country;
    private SharedViewModel sharedViewModel;
    private int id;
    private String idUser;
    private String budget;
    AppRoomDatabase database;
    private DiaryDao diaryDao;
    private UserDao userDao;
    private CheckpointDao checkpointDao;

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
        initializeOverlays(view, inflater);
        initializeRecyclerView(view);
        initializeButtons(view);
        initializeFormFields();
        setupAutoCompleteTextView(overlayAddDiary, overlayModifyDiary);
        initializeDatabase();
    }

    private void initializeOverlays(View view, LayoutInflater inflater) {
        rootLayoutHome = view.findViewById(R.id.root_layout_home);
        overlayAddDiary = inflater.inflate(R.layout.overlay_add_diary, rootLayoutHome, false);
        overlayModifyDiary = inflater.inflate(R.layout.overlay_modify_diary, rootLayoutHome, false);
        rootLayoutHome.addView(overlayAddDiary);
        rootLayoutHome.addView(overlayModifyDiary);
        overlayAddDiary.setVisibility(View.GONE);
        overlayModifyDiary.setVisibility(View.GONE);
    }

    private void initializeRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_diaries);
        emptyMessage = view.findViewById(R.id.text_empty_message);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        diaryAdapter = new DiaryAdapter(getContext(), diaryList, this);
        recyclerView.setAdapter(diaryAdapter);
        updateEmptyMessage();
    }

    private void initializeButtons(View view) {
        addDiaryButton = view.findViewById(R.id.fab_add_diary);
        deleteDiaryButton = view.findViewById(R.id.deleteDiaryButton);
        modifyDiaryButton = view.findViewById(R.id.modifyDiaryButton);
        buttonSave = overlayAddDiary.findViewById(R.id.buttonSaveDiary);
        buttonChooseImage = overlayAddDiary.findViewById(R.id.buttonChooseImage);
        buttonChooseImageChanges = overlayModifyDiary.findViewById(R.id.buttonChooseImageChanges);
        closeAddOverlayButton = overlayAddDiary.findViewById(R.id.backaddDiaryButton);
        closeModifyOverlayButton = overlayModifyDiary.findViewById(R.id.backModifyDiaryButton);
        buttonSaveModify = overlayModifyDiary.findViewById(R.id.buttonSaveDiaryChanges);
    }

    private void initializeFormFields() {
        inputDiaryName = overlayAddDiary.findViewById(R.id.inputDiaryName);
        modifyDiaryName = overlayModifyDiary.findViewById(R.id.inputDiaryNameChanges);
        imageViewCover = overlayAddDiary.findViewById(R.id.imageViewSelected);
        modifyCoverImage = overlayModifyDiary.findViewById(R.id.imageViewSelectedChanges);
        setupDateFields();
    }

    private void setupDateFields() {
        inputDayStartDate = overlayAddDiary.findViewById(R.id.inputDayDeparture);
        inputMonthStartDate = overlayAddDiary.findViewById(R.id.inputMonthDeparture);
        inputYearStartDate = overlayAddDiary.findViewById(R.id.inputYearDeparture);
        inputDayEndDate = overlayAddDiary.findViewById(R.id.inputReturnDay);
        inputMonthEndDate = overlayAddDiary.findViewById(R.id.inputReturnMonth);
        inputYearEndDate = overlayAddDiary.findViewById(R.id.inputReturnYear);

        modifyDayStartDate = overlayModifyDiary.findViewById(R.id.inputDayDepartureChanges);
        modifyMonthStartDate = overlayModifyDiary.findViewById(R.id.inputMonthDepartureChanges);
        modifyYearStartDate = overlayModifyDiary.findViewById(R.id.inputYearDepartureChanges);
        modifyDayEndDate = overlayModifyDiary.findViewById(R.id.inputReturnDayChanges);
        modifyMonthEndDate = overlayModifyDiary.findViewById(R.id.inputReturnMonthChanges);
        modifyYearEndDate = overlayModifyDiary.findViewById(R.id.inputReturnYearChanges);

        setupDatePicker(inputDayStartDate, inputMonthStartDate, inputYearStartDate);
        setupDatePicker(inputDayEndDate, inputMonthEndDate, inputYearEndDate);
        setupDatePicker(modifyDayStartDate, modifyMonthStartDate, modifyYearStartDate);
        setupDatePicker(modifyDayEndDate, modifyMonthEndDate, modifyYearEndDate);
    }

    private void initializeDatabase() {
        database = AppRoomDatabase.getDatabase(getContext());
        diaryDao = database.diaryDao();
        checkpointDao = database.checkpointDao();
        String idUser = getLoggedUserId();



        // Recupera solo i diari dell'utente corrente
        diaryList = diaryDao.getAllDiariesByUserId(idUser);

        diaryAdapter.notifyDataSetChanged();
        Log.d(TAG, "Diari recuperati: " + diaryList.size());

        //load diarys. appdat erecycle view
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

    private Uri saveImageToInternalStorage(Uri sourceUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), sourceUri);
            File storageDir = getContext().getFilesDir();
            String fileName = "diary_" + System.currentTimeMillis() + ".jpg";
            File imageFile = new File(storageDir, fileName);

            try (FileOutputStream out = new FileOutputStream(imageFile)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            }

            return Uri.fromFile(imageFile); // Restituisci un URI accessibile dalla tua app
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK && data != null) {
            Uri selectedUri = data.getData();
            Uri newUri = saveImageToInternalStorage(selectedUri); // Copia l'immagine
            if (newUri != null) {
                selectedImageUri = newUri.toString(); // Aggiorna l'URI usato dalla tua app
                imageViewCover.setImageURI(newUri);
                imageViewCover.setVisibility(View.VISIBLE);
            }

        }
    }


    public String getLoggedUserId() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            return null;
        }
        Log.d(TAG, "Logged user ID: " + firebaseUser.getUid());
        return firebaseUser.getUid();
    }

    private void saveDiary() {
        String diaryName = inputDiaryName.getText().toString();
        String startDate = inputDayStartDate.getText().toString() + "/" + inputMonthStartDate.getText().toString() + "/" + inputYearStartDate.getText().toString();
        String endDate = inputDayEndDate.getText().toString() + "/" + inputMonthEndDate.getText().toString() + "/" + inputYearEndDate.getText().toString();
        String country = ((AutoCompleteTextView) overlayAddDiary.findViewById(R.id.VisitedCountry)).getText().toString();

        if (diaryName.isEmpty() || selectedImageUri == null || startDate.isEmpty() || endDate.isEmpty() || country.isEmpty()) {
            Toast.makeText(getContext(), "Compila tutti i campi e scegli un'immagine!", Toast.LENGTH_SHORT).show();
            saveImageToInternalStorage(Uri.parse(selectedImageUri));

        }


        // Ottieni l'ID dell'utente corrente (ad esempio, da un sistema di autenticazione)
        String currentUserId = getLoggedUserId(); // Implementa questo metodo per ottenere l'ID dell'utente

        Diary newDiary = new Diary(id, currentUserId, diaryName, startDate, endDate, selectedImageUri, budget, country);
        long diaryId = diaryDao.insert(newDiary); // Inserisci il diario nel database
        newDiary.setId((int) diaryId); // Imposta l'ID generato dal database
        Log.d(TAG,"Diary saved with ID: " + diaryId);

        if (diaryId > 0) {
            newDiary.setId((int) diaryId);
            Log.d(TAG, "Diary saved with ID: " + diaryId);

            Checkpoint newCheckpoint = new Checkpoint((int) diaryId, diaryName, startDate, endDate, selectedImageUri);
            checkpointDao.insertCheckpoint(newCheckpoint);
        }

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

        if (diaryName.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || country.isEmpty()) {
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

        // Aggiorna il diario nel database
        diaryDao.update(selectedDiary);

        // Notifica all'adapter che la lista è cambiata
        diaryAdapter.notifyDataSetChanged();

        // Nascondi l'overlay di modifica
        hideOverlay(overlayModifyDiary);

        Toast.makeText(getContext(), "Diario modificato con successo!", Toast.LENGTH_SHORT).show();
    }



    private void deleteSelectedDiaries() {
        for (Diary diary : selectedDiaries) {
            diaryDao.delete(diary); // Elimina il diario dal database
        }
        diaryList.removeAll(selectedDiaries); // Rimuovi i diari dalla lista
        diaryAdapter.notifyDataSetChanged(); // Notifica l'adapter
        Toast.makeText(getContext(), "Diari eliminati con successo!", Toast.LENGTH_SHORT).show();
        selectedDiaries.clear(); // Pulisci la lista dei diari selezionati
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
        String coverImageUri = diary.getCoverImageUri();
        if (coverImageUri != null) {
            modifyCoverImage.setImageURI(Uri.parse(coverImageUri));
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
