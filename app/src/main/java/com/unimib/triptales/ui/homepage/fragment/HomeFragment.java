package com.unimib.triptales.ui.homepage.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.unimib.triptales.adapters.DiaryRecyclerAdapter;
import com.unimib.triptales.database.AppRoomDatabase;
import com.unimib.triptales.database.DiaryDao;
import com.unimib.triptales.database.UserDao;
import com.unimib.triptales.model.Diary;
import com.unimib.triptales.repository.diary.IDiaryRepository;
import com.unimib.triptales.ui.diary.viewmodel.ViewModelFactory;
import com.unimib.triptales.ui.homepage.viewmodel.HomeViewModel;
import com.unimib.triptales.ui.homepage.viewmodel.SharedViewModel;
import com.unimib.triptales.util.GeoJSONParser;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class HomeFragment extends Fragment implements DiaryRecyclerAdapter.OnDiaryItemLongClickListener {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private RecyclerView recyclerView;
    private DiaryRecyclerAdapter diaryRecyclerAdapter;
    private List<Diary> diaryList = new ArrayList<>();
    private TextView emptyMessage;
    private FloatingActionButton addDiaryButton, deleteDiaryButton, modifyDiaryButton;

    private ConstraintLayout rootLayoutHome;
    private View overlayAddDiary, overlayModifyDiary;
    private EditText inputDayStartDate, inputMonthStartDate, inputYearStartDate;
    private EditText inputDayEndDate, inputMonthEndDate, inputYearEndDate;
    private EditText modifyDayStartDate, modifyMonthStartDate, modifyYearStartDate;
    private EditText modifyDayEndDate, modifyMonthEndDate, modifyYearEndDate;

    private EditText inputDiaryName, modifyDiaryName;
    private ImageView imageViewCover, modifyCoverImage;
    private Button buttonChooseImage, buttonSave, buttonSaveModify, buttonChooseImageChanges;
    private ImageButton closeAddOverlayButton, closeModifyOverlayButton;
    private String selectedImageUri;
    private Diary selectedDiary;
    private final Calendar calendar = Calendar.getInstance();

    private ArrayList<Diary> selectedDiaries = new ArrayList<>();
    private String budget;
    private com.unimib.triptales.util.ServiceLocator ServiceLocator;
    private HomeViewModel homeViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        IDiaryRepository diaryRepository = ServiceLocator.getINSTANCE().getDiaryRepository(getContext());
        homeViewModel = new ViewModelProvider(requireActivity(),
                new ViewModelFactory(diaryRepository)).get(HomeViewModel.class);


        initializeViews(view, inflater);
        setupRecyclerView();
        setupButtonListeners();
        initializeDatabase();

        homeViewModel.getValidationErrorsLiveData().observe(getViewLifecycleOwner(), errors -> {
            if (errors == null || errors.isEmpty()) return;

            if (errors.containsKey("diaryName")) {
                inputDiaryName.setError(errors.get("diaryName"));
            } else {
                inputDiaryName.setError(null);
            }

            if (errors.containsKey("startDate")) {
                inputDayStartDate.setError(errors.get("startDate"));
            } else {
                inputDayStartDate.setError(null);
            }

            if (errors.containsKey("endDate")) {
                inputDayEndDate.setError(errors.get("endDate"));
            } else {
                inputDayEndDate.setError(null);
            }

            if (errors.containsKey("dateOrder")) {
                Toast.makeText(getContext(), errors.get("dateOrder"), Toast.LENGTH_LONG).show();
            }

            if (errors.containsKey("image")) {
                Toast.makeText(getContext(), errors.get("image"), Toast.LENGTH_LONG).show();
            }

            if (errors.containsKey("country")) {
                ((AutoCompleteTextView) overlayAddDiary.findViewById(R.id.VisitedCountry))
                        .setError(errors.get("country"));
            }
        });

        homeViewModel.getDiaryOverlayVisibility().observe(getViewLifecycleOwner(), isVisible -> {
            if (isVisible != null) {
                if (isVisible) {
                    showOverlay(overlayAddDiary);
                } else {
                    hideOverlay(overlayAddDiary);
                }
            }
        });



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
        diaryRecyclerAdapter = new DiaryRecyclerAdapter(getContext(), diaryList, this);
        recyclerView.setAdapter(diaryRecyclerAdapter);
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
        homeViewModel.getDiariesLiveData().observe(getViewLifecycleOwner(), diaries -> {
            diaryList.clear();
            diaryList.addAll(diaries);
            diaryRecyclerAdapter.notifyDataSetChanged();
            updateEmptyMessage();
        });
    }

    private void setupAutoCompleteTextView(View overlayAddDiary, View overlayModifyDiary) {
        AutoCompleteTextView countryAutoComplete = overlayAddDiary.findViewById(R.id.VisitedCountry);
        AutoCompleteTextView countryChangesAutoComplete = overlayModifyDiary.findViewById(R.id.VisitedCountryChanges);

        GeoJSONParser.setupAutoCompleteTextView(requireContext(), countryAutoComplete);
        GeoJSONParser.setupAutoCompleteTextView(requireContext(), countryChangesAutoComplete);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        diaryRecyclerAdapter = new DiaryRecyclerAdapter(getContext(), diaryList, this);
        recyclerView.setAdapter(diaryRecyclerAdapter);
        updateEmptyMessage();
    }

    private void setupButtonListeners() {
        addDiaryButton.setOnClickListener(v -> showOverlay(overlayAddDiary));
        closeAddOverlayButton.setOnClickListener(v -> hideOverlay(overlayAddDiary));

        closeModifyOverlayButton.setOnClickListener(v -> {

            // Deseleziona il diario dopo averlo modificato
            selectedDiaries.remove(selectedDiary);

            // Notifica all'adapter che la lista è cambiata
            diaryRecyclerAdapter.notifyDataSetChanged();

            // Aggiungi questa riga per deselezionare tutti i diari
            diaryRecyclerAdapter.clearSelections(); // Deseleziona tutti i diari

            // Nascondi l'overlay di modifica e aggiorna i bottoni
            hideOverlay(overlayModifyDiary);
        });



        buttonSave.setOnClickListener(v -> {
            homeViewModel.insertDiary(
                    inputDiaryName.getText().toString(),
                    inputDayStartDate.getText().toString() + "/" +
                            inputMonthStartDate.getText().toString() + "/" +
                            inputYearStartDate.getText().toString(),
                    inputDayEndDate.getText().toString() + "/" +
                            inputMonthEndDate.getText().toString() + "/" +
                            inputYearEndDate.getText().toString(),
                    selectedImageUri,
                    budget,
                    ((AutoCompleteTextView) overlayAddDiary.findViewById(R.id.VisitedCountry)).getText().toString()
            );
        });


        buttonSaveModify.setOnClickListener(v -> {
            if (selectedDiary != null) {
                homeViewModel.updateDiary(
                        selectedDiary,
                        modifyDiaryName.getText().toString(),
                        modifyDayStartDate.getText().toString() + "/" +
                                modifyMonthStartDate.getText().toString() + "/" +
                                modifyYearStartDate.getText().toString(),
                        modifyDayEndDate.getText().toString() + "/" +
                                modifyMonthEndDate.getText().toString() + "/" +
                                modifyYearEndDate.getText().toString(),
                        selectedImageUri,
                        ((AutoCompleteTextView) overlayModifyDiary.findViewById(R.id.VisitedCountryChanges)).getText().toString()
                );

                selectedDiaries.remove(selectedDiary);
                diaryRecyclerAdapter.clearSelections();
                diaryRecyclerAdapter.notifyDataSetChanged();
                selectedDiary = null;
                hideOverlay(overlayModifyDiary);
            }
        });


        buttonChooseImage.setOnClickListener(v -> openImagePicker());
        buttonChooseImageChanges.setOnClickListener(v -> openImagePicker());

        deleteDiaryButton.setOnClickListener(v -> {
            deleteSelectedDiaries();
            updateEmptyMessage();
            // Deseleziona il diario dopo averlo modificato
            selectedDiaries.remove(selectedDiary);

            // Notifica all'adapter che la lista è cambiata
            diaryRecyclerAdapter.notifyDataSetChanged();

            // Aggiungi questa riga per deselezionare tutti i diari
            diaryRecyclerAdapter.clearSelections(); //
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

        if (overlay == overlayAddDiary) {
            resetDiaryFields(); // Only reset fields when closing the Add Diary overlay
        }

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
            Uri newUri = saveImageToInternalStorage(selectedUri); // Copy the image for app use

            if (newUri != null) {
                selectedImageUri = newUri.toString(); // Store the new URI

                // Check which overlay is open and update the correct ImageView
                if (overlayAddDiary.getVisibility() == View.VISIBLE) {
                    imageViewCover.setImageURI(newUri);
                    imageViewCover.setVisibility(View.VISIBLE);
                } else if (overlayModifyDiary.getVisibility() == View.VISIBLE) {
                    modifyCoverImage.setImageURI(newUri);
                    modifyCoverImage.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    private void deleteSelectedDiaries() {
        for (Diary diary : selectedDiaries) {
            homeViewModel.deleteDiaries(selectedDiaries);
        }
        selectedDiaries.clear();
        Toast.makeText(getContext(), "Diari eliminati con successo!", Toast.LENGTH_SHORT).show();
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
        // Clear country field
        AutoCompleteTextView countryAutoComplete = overlayAddDiary.findViewById(R.id.VisitedCountry);
        if (countryAutoComplete != null) {
            countryAutoComplete.setText(""); // Reset country input
        }

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
            selectedDiaries.remove(diary); // 🔹 Deselect if already selected
        } else {
            selectedDiaries.add(diary); // 🔹 Select if not selected
        }

        // 🔹 Show delete button if at least one diary is selected
        deleteDiaryButton.setVisibility(selectedDiaries.isEmpty() ? View.GONE : View.VISIBLE);

        // 🔹 Show modify button ONLY if exactly one diary is selected
        modifyDiaryButton.setVisibility(selectedDiaries.size() == 1 ? View.VISIBLE : View.GONE);
    }



}
