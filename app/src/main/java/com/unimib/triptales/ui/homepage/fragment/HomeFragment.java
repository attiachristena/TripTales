package com.unimib.triptales.ui.homepage.fragment;

import static com.unimib.triptales.util.Constants.ADDED;
import static com.unimib.triptales.util.Constants.ADD_DIARY;
import static com.unimib.triptales.util.Constants.DELETED;
import static com.unimib.triptales.util.Constants.EDIT_DIARY;
import static com.unimib.triptales.util.Constants.INVALID_DELETE;
import static com.unimib.triptales.util.Constants.UPDATED;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.unimib.triptales.util.Constants;
import com.unimib.triptales.util.ServiceLocator;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.unimib.triptales.R;
import com.unimib.triptales.adapters.DiaryRecyclerAdapter;
import com.unimib.triptales.model.Diary;

import com.unimib.triptales.repository.diary.IDiaryRepository;
import com.unimib.triptales.ui.diary.viewmodel.ViewModelFactory;
import com.unimib.triptales.ui.homepage.viewmodel.HomeViewModel;
import com.unimib.triptales.util.GeoJSONParser;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;


public class HomeFragment extends Fragment {

    private DiaryRecyclerAdapter diaryRecyclerAdapter;
    private TextView emptyMessage;
    private FloatingActionButton addDiaryButton, deleteDiaryButton, modifyDiaryButton;
    private View overlayAddModifyDiary;
    private EditText inputDayStartDate, inputMonthStartDate, inputYearStartDate;
    private EditText inputDayEndDate, inputMonthEndDate, inputYearEndDate;
    private EditText inputDiaryName;
    private ImageView imageViewCover;
    private Button buttonChooseImage, buttonSave;
    private ImageButton closeAddOverlayButton;
    private String selectedImageUri;
    private final Calendar calendar = Calendar.getInstance();
    private HomeViewModel homeViewModel;
    private boolean bEdit;
    private boolean bAdd;
    private AutoCompleteTextView countryAutoComplete;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        IDiaryRepository diaryRepository = ServiceLocator.getINSTANCE().getDiaryRepository(getContext());
        homeViewModel = new ViewModelProvider(requireActivity(),
                new ViewModelFactory(diaryRepository)).get(HomeViewModel.class);

        homeViewModel.loadDiaries();
        homeViewModel.deselectAllDiaries();
        initializeViews(view, inflater);
        List<Diary> diaries = homeViewModel.getDiariesLiveData().getValue();
        if(diaries != null) {
            for (Diary diary : diaries) {
                String imageUriString = diary.getCoverImageUri();
                if (imageUriString != null && !imageUriString.isEmpty()) {
                    Glide.with(requireContext())
                            .load(imageUriString)  // Carica l'immagine dall'URI locale
                            .into(imageViewCover);
                    homeViewModel.updateDiaryCoverImage(diary.getId(), imageUriString);

                }
            }
        }

        RecyclerView recyclerViewDiaries = view.findViewById(R.id.recycler_view_diaries);
        diaryRecyclerAdapter = new DiaryRecyclerAdapter(getContext());
        recyclerViewDiaries.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerViewDiaries.setAdapter(diaryRecyclerAdapter);

        diaryRecyclerAdapter.setOnDiaryLongClicked((diary) -> {
            homeViewModel.toggleDiarySelection(diary);
        });

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedUri = result.getData().getData();

                        if(selectedUri != null){
                            Uri newUri = saveImageToPublicStorage(selectedUri);

                            if (newUri != null) {
                                selectedImageUri = newUri.toString();
                        }

                            // Controlla quale overlay è aperto e aggiorna l'immagine corretta
                            if (overlayAddModifyDiary.getVisibility() == View.VISIBLE) {
                                imageViewCover.setImageURI(newUri);
                                imageViewCover.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
        );

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestPermissions();

        emptyMessage = view.findViewById(R.id.text_empty_message);
        bAdd = false;
        bEdit = false;
        homeViewModel.loadDiaries();
        final int maxObservations = 10;
        final AtomicInteger observationCount = new AtomicInteger(0);

        homeViewModel.getLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(!aBoolean) {
                    int currentCount = observationCount.incrementAndGet();
                    List<Diary> diaries = homeViewModel.getDiariesLiveData().getValue();
                    if (diaries != null) {
                        homeViewModel.loadDiaries();
                    }
                    if (currentCount >= maxObservations) {
                        homeViewModel.getLoading().removeObserver(this);
                    }
                }
            }
        });

        //aggiorna l'adapter e gestisce l'emptyMessage
        homeViewModel.getDiariesLiveData().observe(getViewLifecycleOwner(), diaries -> {
            if(diaries != null){
                diaryRecyclerAdapter.setDiaries(diaries);
                if (diaries.isEmpty()) {
                    emptyMessage.setVisibility(View.VISIBLE);
                } else {
                    emptyMessage.setVisibility(View.GONE);
                }
            }
        });

        // mostra eventuali errori
        homeViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if(errorMessage != null){
                Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // mostra feedback all'utente su aggiunta, modifica e rimozione di un diario
        homeViewModel.getDiaryEvent().observe(getViewLifecycleOwner(), message -> {
            if(message != null){
                switch (message) {
                    case ADDED:
                        Toast.makeText(requireActivity(), R.string.snackbarDiaryAdded,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case UPDATED:
                        Toast.makeText(requireActivity(), R.string.snackbarDiaryUpdated,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case DELETED:
                        Toast.makeText(requireActivity(), R.string.snackbarDiaryDeleted,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case INVALID_DELETE:
                        Toast.makeText(requireActivity(), R.string.snackbarDiaryNotDeleted,
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        addDiaryButton.setOnClickListener(addDiaryButtonListener -> {
            bAdd = true;
            homeViewModel.setDiaryOverlayVisibility(true);
        });

        closeAddOverlayButton.setOnClickListener(closeAddOverlayButton -> {
            if(bEdit){
                modifyDiaryButton.setVisibility(View.VISIBLE);
                deleteDiaryButton.setVisibility(View.VISIBLE);
            }
            homeViewModel.setDiaryOverlayVisibility(false);
        });

        // gestione dell'overlay per aggiungere o modificare un diario
        homeViewModel.getDiaryOverlayVisibility().observe(getViewLifecycleOwner(), visible -> {
            if(visible){
                addDiaryButton.setVisibility(View.GONE);
                if(bAdd){
                    showOverlay(ADD_DIARY);
                } else if(bEdit){
                    showOverlay(EDIT_DIARY);
                    modifyDiaryButton.setVisibility(View.GONE);
                    deleteDiaryButton.setVisibility(View.GONE);
                }
            } else {
                Constants.hideKeyboard(view, requireActivity());
                addDiaryButton.setVisibility(View.VISIBLE);
                if(bAdd){
                    hideOverlay(ADD_DIARY);
                }else if(bEdit){
                    hideOverlay(EDIT_DIARY);
                }
            }
        });

        // gestione del focus nei campi delle date dell'overlay
        inputDayStartDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 2){
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
                if(charSequence.length() == 2){
                    inputYearStartDate.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        inputDayEndDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 2){
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
                if(charSequence.length() == 2){
                    inputYearEndDate.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        buttonSave.setOnClickListener(buttonSaveListener -> {
            String name = inputDiaryName.getText().toString();
            String dayStartDate = inputDayStartDate.getText().toString();
            String monthStartDate =  inputMonthStartDate.getText().toString();
            String yearStartDate = inputYearStartDate.getText().toString();
            String dayEndDate = inputDayEndDate.getText().toString();
            String monthEndDate = inputMonthEndDate.getText().toString();
            String yearEndDate = inputYearEndDate.getText().toString();
            String country = ((AutoCompleteTextView)
                    overlayAddModifyDiary.findViewById(R.id.VisitedCountry)).getText().toString();

            String startDate = dayStartDate + "/" + monthStartDate + "/" + yearStartDate;
            String endDate = dayEndDate + "/" + monthEndDate + "/" + yearEndDate;

            String imageUri;
            if(selectedImageUri != null && !selectedImageUri.isEmpty())
                imageUri = selectedImageUri;
            else
                imageUri = null;

            boolean correct = homeViewModel.validateDiaryInput(name, startDate, endDate, imageUri, country);
            if(correct) {
                if (bAdd) {
                    homeViewModel.insertDiary(name, startDate, endDate, imageUri, null, country);
                } else if (bEdit) {
                    List<Diary> selectedDiary = homeViewModel.getSelectedDiariesLiveData().getValue();
                    if (selectedDiary != null && !selectedDiary.isEmpty()) {
                        Diary currentDiary = selectedDiary.get(0);
                        if (imageUri == null) {
                            imageUri = currentDiary.getCoverImageUri();
                        }
                        homeViewModel.updateDiary(currentDiary, name, startDate, endDate, imageUri, country);
                        homeViewModel.deselectAllDiaries();
                    }
                }
                selectedImageUri = null;
                homeViewModel.setDiaryOverlayVisibility(false);
            }
        });

        // gestione delle spese selezionate
        homeViewModel.getSelectedDiariesLiveData().observe(getViewLifecycleOwner(), selectedDiaries -> {
            if(selectedDiaries != null){
                if(selectedDiaries.size() == 1){
                    if(overlayAddModifyDiary.getVisibility() == View.VISIBLE){
                        modifyDiaryButton.setVisibility(View.GONE);
                        deleteDiaryButton.setVisibility(View.GONE);
                    } else {
                        addDiaryButton.setEnabled(false);
                        modifyDiaryButton.setVisibility(View.VISIBLE);
                        deleteDiaryButton.setVisibility(View.VISIBLE);
                    }
                } else if(selectedDiaries.size() == 2){
                    addDiaryButton.setEnabled(false);
                    modifyDiaryButton.setVisibility(View.GONE);
                } else if(selectedDiaries.isEmpty()){
                    modifyDiaryButton.setVisibility(View.GONE);
                    deleteDiaryButton.setVisibility(View.GONE);
                    addDiaryButton.setEnabled(true);
                }
            }
        });

        deleteDiaryButton.setOnClickListener(deleteDiaryButtonListener ->
                homeViewModel.deleteSelectedDiaries());

        modifyDiaryButton.setOnClickListener(modifyDiaryButtonListener -> {
            bEdit = true;
            homeViewModel.setDiaryOverlayVisibility(true);
        });

        buttonChooseImage.setOnClickListener(v -> openImagePicker());

    }

    private void initializeViews(View view, LayoutInflater inflater) {
        initializeOverlays(view, inflater);
        initializeButtons(view);
        initializeFormFields();
        setupAutoCompleteTextView(overlayAddModifyDiary);
    }

    private void initializeOverlays(View view, LayoutInflater inflater) {
        ConstraintLayout rootLayoutHome = view.findViewById(R.id.root_layout_home);
        overlayAddModifyDiary = inflater.inflate(R.layout.overlay_add_edit_diary, rootLayoutHome,
                false);
        rootLayoutHome.addView(overlayAddModifyDiary);
        overlayAddModifyDiary.setVisibility(View.GONE);
    }

    private void initializeButtons(View view) {
        addDiaryButton = view.findViewById(R.id.fab_add_diary);
        deleteDiaryButton = view.findViewById(R.id.deleteDiaryButton);
        modifyDiaryButton = view.findViewById(R.id.modifyDiaryButton);
        buttonSave = overlayAddModifyDiary.findViewById(R.id.buttonSaveDiary);
        buttonChooseImage = overlayAddModifyDiary.findViewById(R.id.buttonChooseImage);
        closeAddOverlayButton = overlayAddModifyDiary.findViewById(R.id.backaddDiaryButton);
    }

    private void initializeFormFields() {
        inputDiaryName = overlayAddModifyDiary.findViewById(R.id.inputDiaryName);
        imageViewCover = overlayAddModifyDiary.findViewById(R.id.imageViewSelected);
        setupDateFields();
    }

    private void setupDateFields() {
        inputDayStartDate = overlayAddModifyDiary.findViewById(R.id.inputDayDeparture);
        inputMonthStartDate = overlayAddModifyDiary.findViewById(R.id.inputMonthDeparture);
        inputYearStartDate = overlayAddModifyDiary.findViewById(R.id.inputYearDeparture);
        inputDayEndDate = overlayAddModifyDiary.findViewById(R.id.inputReturnDay);
        inputMonthEndDate = overlayAddModifyDiary.findViewById(R.id.inputReturnMonth);
        inputYearEndDate = overlayAddModifyDiary.findViewById(R.id.inputReturnYear);

        setupDatePicker(inputDayStartDate, inputMonthStartDate, inputYearStartDate);
        setupDatePicker(inputDayEndDate, inputMonthEndDate, inputYearEndDate);
    }

    private void setupAutoCompleteTextView(View overlayAddDiary) {
        countryAutoComplete = overlayAddDiary.findViewById(R.id.VisitedCountry);
        GeoJSONParser.setupAutoCompleteTextView(requireContext(), countryAutoComplete);
    }

    private void showOverlay(String overlayType) {
        addDiaryButton.setVisibility(View.GONE);
        switch (overlayType) {
            case ADD_DIARY:
                overlayAddModifyDiary.setVisibility(View.VISIBLE);
                resetDiaryFields();
                break;
            case EDIT_DIARY:
                overlayAddModifyDiary.setVisibility(View.VISIBLE);
                populateModifyOverlay();
                break;
        }
    }

    private void hideOverlay(String overlayType) {
        switch (overlayType){
            case ADD_DIARY:
                overlayAddModifyDiary.setVisibility(View.GONE);
                bAdd = false;
                break;
            case EDIT_DIARY:
                overlayAddModifyDiary.setVisibility(View.GONE);
                bEdit = false;
                break;
        }
    }

    private void setupDatePicker(EditText dayField, EditText monthField, EditText yearField) {
        View.OnClickListener listener = v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        dayField.setText(String.format(Locale.getDefault(), "%02d",
                                dayOfMonth));
                        monthField.setText(String.format(Locale.getDefault(), "%02d",
                                month + 1));
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
        imagePickerLauncher.launch(intent);
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    100);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10-12
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    100);
        } else { // Android 9 o inferiore
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    100);
        }
    }


    private Uri saveImageToPublicStorage(Uri sourceUri) {
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),
                    sourceUri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "diary_" + System.currentTimeMillis()
                + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES +
                "/TripTales");

        Uri imageUri = getContext().getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            if (imageUri != null) {
                OutputStream out = getContext().getContentResolver().openOutputStream(imageUri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.close();
                return imageUri; // Restituisce l'URI pubblico
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void populateModifyOverlay() {
        List<Diary> selectedDiary = homeViewModel.getSelectedDiariesLiveData().getValue();
        if(selectedDiary != null && !selectedDiary.isEmpty()){
            Diary currentDiary = selectedDiary.get(0);
            inputDiaryName.setText(currentDiary.getName());

            String[] startDate = currentDiary.getStartDate().split("/");
            inputDayStartDate.setText(startDate[0]);
            inputMonthStartDate.setText(startDate[1]);
            inputYearStartDate.setText(startDate[2]);

            String[] endDate = currentDiary.getEndDate().split("/");
            inputDayEndDate.setText(endDate[0]);
            inputMonthEndDate.setText(endDate[1]);
            inputYearEndDate.setText(endDate[2]);

            String coverImageUri = currentDiary.getCoverImageUri();
            if (coverImageUri != null) {
                imageViewCover.setImageURI(Uri.parse(coverImageUri));
                imageViewCover.setVisibility(View.VISIBLE);  // Assicurati che l'immagine sia visibile
            } else {
                imageViewCover.setVisibility(View.GONE);  // Se non c'è un'immagine, nascondila
            }

            countryAutoComplete.setText(currentDiary.getCountry());
        }
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
        AutoCompleteTextView countryAutoComplete =
                overlayAddModifyDiary.findViewById(R.id.VisitedCountry);
        if (countryAutoComplete != null) {
            countryAutoComplete.setText(""); // Reset country input
        }
    }
}
