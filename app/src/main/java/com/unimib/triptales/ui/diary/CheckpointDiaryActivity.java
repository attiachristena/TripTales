package com.unimib.triptales.ui.diary;

import static android.app.PendingIntent.getActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.unimib.triptales.R;
import com.unimib.triptales.adapters.ImageCardItemAdapter;
import com.unimib.triptales.model.ImageCardItem;
import com.unimib.triptales.repository.imageCardItem.IImageCardItemRepository;
import com.unimib.triptales.ui.diary.viewmodel.ImageCardItemViewModel;
import com.unimib.triptales.ui.diary.viewmodel.ViewModelFactory;
import com.unimib.triptales.util.ServiceLocator;
import com.unimib.triptales.util.SharedPreferencesUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;


public class CheckpointDiaryActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncherEdit;
    private String nomeTappaString;
    private String dataTappaString;
    private Uri imageUri;
    private Uri selectedImageUri;
    private FloatingActionButton addCheckpointDiaryImage;
    private Button openGallery;
    private ImageButton closeOverlayButton;
    private View overlayView;
    private View overlayEditView;
    private ImageButton goBackArrow;
    private TextInputEditText imageTitle;
    private TextInputEditText imageDescrpition;
    private TextInputEditText dateImage;
    private Button saveImage;
    private ImageCardItemViewModel imageCardItemViewModel;
    private int checkpointDiaryId;
    private TextView nomeTappa;
    private TextView dataTappa;
    private ImageView immagineTappa;
    private ImageCardItemAdapter imageCardItemAdapter;
    private Uri selectedImageUriDiary;
    private ImageView previewImageCheckpointDiary;
    private TextView textPreviewImageCheckpointDiary;
    private FloatingActionButton editCheckpointDiaryImage;
    private FloatingActionButton deleteCheckpointDiaryImage;
    private Button saveEditButton;
    private TextInputEditText editTitle;
    private TextInputEditText editDescription;
    private TextInputEditText editDate;
    private ImageView editImage;
    private TextView textPrevieEdited;
    private Uri selectedEditImageUri;
    private Uri selectedImageUriDiaryEdited;
    private ImageButton goBackArrowEdit;
    private ImageCardItem selectedCardItem;
    private Button openGalleryEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkpoint_diary);

        ViewGroup rootLayout = findViewById(android.R.id.content);
        LayoutInflater inflater = getLayoutInflater();
        overlayView = inflater.inflate(R.layout.overlay_add_checkpoint_image, rootLayout, false);
        overlayEditView = inflater.inflate(R.layout.overlay_edit_checkpoint_diary_image, rootLayout, false);
        overlayView.setVisibility(View.GONE);
        overlayEditView.setVisibility(View.GONE);
        rootLayout.addView(overlayView);
        rootLayout.addView(overlayEditView);


        nomeTappaString = getIntent().getStringExtra("nomeTappa");
        imageUri = Uri.parse(getIntent().getStringExtra("immagineTappaUri"));
        dataTappaString = getIntent().getStringExtra("dataTappa");
        checkpointDiaryId = getIntent().getIntExtra("checkpointDiaryId", -1);

        if (checkpointDiaryId == -1) {
            throw new IllegalStateException("Invalid CheckpointDiary ID");
        }

        SharedPreferencesUtils.setCheckpointDiaryId(this, checkpointDiaryId);


        IImageCardItemRepository imageCardItemRepository = ServiceLocator.getINSTANCE().getImageCardItemRepository(this);
        imageCardItemViewModel = new ViewModelProvider(this,
                new ViewModelFactory(imageCardItemRepository))
                .get(ImageCardItemViewModel.class);



        RecyclerView recyclerViewCards = findViewById(R.id.imageCardItemRecyclerView);
        imageCardItemAdapter = new ImageCardItemAdapter(this);
        recyclerViewCards.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCards.setAdapter(imageCardItemAdapter);

        imageCardItemViewModel.fetchAllImageCardItems(this);
        imageCardItemViewModel.loadAllImageCardItems();

        imageCardItemViewModel.getImageCardItemsLiveData().observe(this, imageCardItems -> {
            imageCardItemAdapter.setImageCardItems(imageCardItems);
            imageCardItemAdapter.notifyDataSetChanged();
        });

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            selectedImageUriDiary = selectedImageUri;
                            previewImageCheckpointDiary.setImageURI(selectedImageUriDiary);
                            textPreviewImageCheckpointDiary.setVisibility(View.GONE);
                        } else {
                            textPreviewImageCheckpointDiary.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );

        pickImageLauncherEdit = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedEditImageUri = result.getData().getData();
                        if (selectedEditImageUri != null) {
                            selectedImageUriDiaryEdited = selectedEditImageUri;
                            editImage.setImageURI(selectedImageUriDiaryEdited);
                            textPrevieEdited.setVisibility(View.GONE);
                        } else {
                            textPrevieEdited.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );

        addCheckpointDiaryImage = findViewById(R.id.addCheckpointDiaryImage);
        closeOverlayButton = overlayView.findViewById(R.id.goBackArrowButtonCheckpointDiary);
        goBackArrow = findViewById(R.id.backToTappaFragment);

        goBackArrow.setOnClickListener(v -> finish());


        nomeTappa = findViewById(R.id.nomeTappa);
        dataTappa = findViewById(R.id.dataTappa);
        immagineTappa = findViewById(R.id.immagineTappaItem);

        nomeTappa.setText(nomeTappaString);
        dataTappa.setText(dataTappaString);

        if (imageUri != null) {
            immagineTappa.setImageURI(imageUri);
        } else {
            immagineTappa.setImageResource(R.drawable.ic_launcher_background); // Immagine di default
        }

        imageTitle = overlayView.findViewById(R.id.imageTitle);
        imageDescrpition = overlayView.findViewById(R.id.imageDescription);
        dateImage = overlayView.findViewById(R.id.addDateCheckpointDiary);
        openGallery = overlayView.findViewById(R.id.addImageCheckpointDiaryButton);
        saveImage = overlayView.findViewById(R.id.saveCheckpointDiaryButton);
        previewImageCheckpointDiary = overlayView.findViewById(R.id.previewImageCheckpointDiary);
        textPreviewImageCheckpointDiary = overlayView.findViewById(R.id.textPreviewImageCheckpointDiary);
        editCheckpointDiaryImage = findViewById(R.id.editCheckpointDiaryImageButton);
        deleteCheckpointDiaryImage = findViewById(R.id.deleteCheckpointDiaryImageButton);


        addCheckpointDiaryImage.setOnClickListener(v -> {
            addCheckpointDiaryImage.setVisibility(View.GONE);
            goBackArrow.setVisibility(View.GONE);
            overlayView.setVisibility(View.VISIBLE);
            dateImage.setOnClickListener(v1 -> showDatePickerDialog(dateImage));

        });

        editCheckpointDiaryImage.setOnClickListener(v -> {
            List<ImageCardItem> selectedItems = imageCardItemAdapter.getSelectedItems();
            if (selectedItems.size() == 1) {
                ImageCardItem selectedItem = selectedItems.get(0);
                openEditOverlay(selectedItem);
            }
        });

        deleteCheckpointDiaryImage.setOnClickListener(v -> {
            List<ImageCardItem> selectedItems = imageCardItemAdapter.getSelectedItems();
            if (!selectedItems.isEmpty()) {
                imageCardItemViewModel.deleteSelectedImageCardItems(selectedItems, this);
                Snackbar.make(this, rootLayout, "Card cancellate con successo", Snackbar.LENGTH_SHORT).show();
            }
        });


        closeOverlayButton.setOnClickListener(v -> {
            imageTitle.setText("");
            imageDescrpition.setText("");
            dateImage.setText("");
            previewImageCheckpointDiary.setImageURI(null);
            textPreviewImageCheckpointDiary.setVisibility(View.VISIBLE);
            hideKeyboard();
            overlayView.setVisibility(View.GONE);
            addCheckpointDiaryImage.setVisibility(View.VISIBLE);
            goBackArrow.setVisibility(View.VISIBLE);
        });


        openGallery.setOnClickListener(v -> openGallery());

        saveImage.setOnClickListener(v -> {
            String title = imageTitle.getText().toString();
            String description = imageDescrpition.getText().toString();
            String date = dateImage.getText().toString();
            Uri selectedImageUri = selectedImageUriDiary;

            if (!title.isEmpty() && !description.isEmpty() && !date.isEmpty() && selectedImageUri != null) {
                Uri imageUri = saveImageToPublicStorage(selectedImageUri);

                if (imageUri == null) {
                    Snackbar.make(rootLayout, "Errore durante il salvataggio dell'immagine",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                imageCardItemViewModel.insertImageCardItem(title, description, date, imageUri,
                        this, checkpointDiaryId);

                imageTitle.setText("");
                imageDescrpition.setText("");
                dateImage.setText("");
                hideKeyboard();

                previewImageCheckpointDiary.setImageURI(null);
                textPreviewImageCheckpointDiary.setVisibility(View.VISIBLE);
                addCheckpointDiaryImage.setVisibility(View.VISIBLE);
                goBackArrow.setVisibility(View.VISIBLE);
                overlayView.setVisibility(View.GONE);

                Snackbar.make(rootLayout, "La tua immagine è stata aggiunta con successo",
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(rootLayout, "Compila tutti i campi (inclusa l'immagine)",
                        Snackbar.LENGTH_SHORT).show();
            }

        });

        imageCardItemAdapter.setOnImageCardItemClickListener(new ImageCardItemAdapter.OnImageCardItemClickListener() {

            @Override
            public void onImageCardItemLongClick(ImageCardItem imageCardItem) {
                int position = imageCardItemAdapter.getImageCardItems().indexOf(imageCardItem);
                if (position != -1) {
                    imageCardItemAdapter.toggleSelection(position);
                    selectedCardItem = imageCardItem; // Imposta la card selezionata
                }
                editCheckpointDiaryImage.setVisibility(View.VISIBLE);
                deleteCheckpointDiaryImage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSelectionChanged(List<ImageCardItem> selectedItems) {
                if (selectedItems.size() == 1) {
                    selectedCardItem = selectedItems.get(0);
                    editCheckpointDiaryImage.setVisibility(View.VISIBLE);
                    deleteCheckpointDiaryImage.setVisibility(View.VISIBLE);
                } else if (selectedItems.size() > 1) {
                    selectedCardItem = null;
                    editCheckpointDiaryImage.setVisibility(View.GONE);
                    deleteCheckpointDiaryImage.setVisibility(View.VISIBLE);
                } else {
                    selectedCardItem = null;
                    editCheckpointDiaryImage.setVisibility(View.GONE);
                    deleteCheckpointDiaryImage.setVisibility(View.GONE);
                }
            }
        });

        saveEditButton = overlayEditView.findViewById(R.id.saveEditCheckpointDiaryButton);
        editTitle = overlayEditView.findViewById(R.id.editedTitleCheckpointDiary);
        editDescription = overlayEditView.findViewById(R.id.imageDescriptionEdited);
        editDate = overlayEditView.findViewById(R.id.addDateCheckpointDiaryEdited);
        editImage = overlayEditView.findViewById(R.id.previewImageCheckpointDiaryEdited);
        textPrevieEdited = overlayEditView.findViewById(R.id.textPreviewImageCheckpointDiaryEdited);
        goBackArrowEdit = overlayEditView.findViewById(R.id.goBackArrowButtonEditedCheckpointDiary);
        openGalleryEdit = overlayEditView.findViewById(R.id.editImageCheckpointDiaryButton);


        editCheckpointDiaryImage.setOnClickListener(v -> {
            overlayEditView.setVisibility(View.VISIBLE);

            List<ImageCardItem> selectedItems = imageCardItemAdapter.getSelectedItems();
            if (!selectedItems.isEmpty()) {
                ImageCardItem imageCardItem = selectedItems.get(0);
                editTitle.setText(imageCardItem.getTitle());
                editDescription.setText(imageCardItem.getDescription());
                editDate.setText(imageCardItem.getDate());
                addCheckpointDiaryImage.setVisibility(View.GONE);
                editCheckpointDiaryImage.setVisibility(View.GONE);
                deleteCheckpointDiaryImage.setVisibility(View.GONE);

                if (imageCardItem.getImageUri() != null && !imageCardItem.getImageUri().isEmpty()) {
                    Glide.with(this)
                            .load(imageCardItem.getImageUri())
                            .into(editImage);
                    textPrevieEdited.setVisibility(View.GONE);
                } else {
                    editImage.setImageResource(R.drawable.ic_launcher_background);
                    textPrevieEdited.setVisibility(View.VISIBLE);
                }
            }
        });

        openGalleryEdit.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            pickImageLauncherEdit.launch(intent);
        });

        // Listener per il pulsante di cancellazione
        deleteCheckpointDiaryImage.setOnClickListener(v -> {
            List<ImageCardItem> selectedItems = imageCardItemAdapter.getSelectedItems();
            if (!selectedItems.isEmpty()) {
                imageCardItemViewModel.deleteSelectedImageCardItems(selectedItems, this);
                Snackbar.make(this, rootLayout, "Card cancellate con successo",
                        Snackbar.LENGTH_SHORT).show();
                editCheckpointDiaryImage.setVisibility(View.GONE);
                deleteCheckpointDiaryImage.setVisibility(View.GONE);
            }
        });

        saveEditButton.setOnClickListener(v -> {
            String newTitle = editTitle.getText().toString();
            String newDescription = editDescription.getText().toString();
            String newDate = editDate.getText().toString();

            editDate.setOnClickListener(v1 -> showDatePickerDialog(editDate));

            Uri newImageUri = selectedImageUriDiaryEdited;

            if (selectedCardItem != null) {
                if (!newTitle.isEmpty()) {
                    Uri finalImageUri = (newImageUri != null) ?
                            saveImageToPublicStorage(newImageUri) :
                            Uri.parse(selectedCardItem.getImageUri());

                    imageCardItemViewModel.updateImageCardItem(
                            selectedCardItem.getId(),
                            newTitle,
                            newDescription,
                            newDate,
                            finalImageUri,
                            CheckpointDiaryActivity.this
                    );

                    hideKeyboard();
                    overlayEditView.setVisibility(View.GONE);
                    selectedImageUriDiaryEdited = null;
                    editTitle.setText("");
                    editDescription.setText("");
                    editDate.setText("");
                    editImage.setImageURI(null);
                    textPrevieEdited.setVisibility(View.VISIBLE);
                    addCheckpointDiaryImage.setVisibility(View.VISIBLE);

                    Snackbar.make(CheckpointDiaryActivity.this, rootLayout,
                            "Modifiche salvate con successo", Snackbar.LENGTH_SHORT).show();

                    imageCardItemAdapter.clearSelections();
                    editCheckpointDiaryImage.setVisibility(View.GONE);
                    deleteCheckpointDiaryImage.setVisibility(View.GONE);

                } else {
                    Snackbar.make(CheckpointDiaryActivity.this, rootLayout,
                            "Il titolo è obbligatorio", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(CheckpointDiaryActivity.this, rootLayout,
                        "Nessuna card selezionata", Snackbar.LENGTH_SHORT).show();
            }
        });


        goBackArrowEdit.setOnClickListener(v -> {
            hideKeyboard();
            overlayEditView.setVisibility(View.GONE);
            imageCardItemAdapter.clearSelections();
            editCheckpointDiaryImage.setVisibility(View.GONE);
            deleteCheckpointDiaryImage.setVisibility(View.GONE);
            editTitle.setText("");
            editDescription.setText("");
            editDate.setText("");
            previewImageCheckpointDiary.setImageURI(null);
            textPrevieEdited.setVisibility(View.VISIBLE);
            selectedCardItem = null;
            addCheckpointDiaryImage.setVisibility(View.VISIBLE);
        });


    }


    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private Uri saveImageToPublicStorage(Uri sourceUri) {
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), sourceUri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "diary_" + System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/TripTales");

        Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            if (imageUri != null) {
                OutputStream out = getContentResolver().openOutputStream(imageUri);
                if (out != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.close();
                }
                return imageUri;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void openEditOverlay(ImageCardItem item) {
        selectedCardItem = item;
        overlayEditView.setVisibility(View.VISIBLE);
        editTitle.setText(item.getTitle());
        editDescription.setText(item.getDescription());
        editDate.setText(item.getDate());

        if (item.getImageUri() != null && !item.getImageUri().isEmpty()) {
            Glide.with(this)
                    .load(item.getImageUri())
                    .into(editImage);
            textPrevieEdited.setVisibility(View.GONE);
        } else {
            editImage.setImageResource(R.drawable.ic_launcher_background);
            textPrevieEdited.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        imageCardItemViewModel.fetchAllImageCardItems(this);
    }

    private void showDatePickerDialog(TextInputEditText dateEditText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    dateEditText.setText(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                // Nascondi la tastiera
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

}



