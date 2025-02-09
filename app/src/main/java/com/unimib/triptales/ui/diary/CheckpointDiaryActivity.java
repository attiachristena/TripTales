package com.unimib.triptales.ui.diary;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.unimib.triptales.R;
import com.unimib.triptales.adapters.ImageCardItemAdapter;
import com.unimib.triptales.model.ImageCardItem;
import com.unimib.triptales.repository.imageCardItem.IImageCardItemRepository;
import com.unimib.triptales.ui.diary.viewmodel.checkpoint.ImageCardItemViewModel;
import com.unimib.triptales.ui.diary.viewmodel.ViewModelFactory;
import com.unimib.triptales.util.ServiceLocator;
import com.unimib.triptales.util.SharedPreferencesUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CheckpointDiaryActivity extends AppCompatActivity {

    private TextView descrizioneTappa;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncherEdit;
    private String nomeTappaString;
    private String dataTappaString;
    private Uri imageUri;
    private MaterialCardView overlayAddImage;
    private ShapeableImageView previewImage;
    private Uri selectedImageUri;
    private FloatingActionButton addCheckpointDiaryImage;
    private Button openGallery;
    private ImageButton closeOverlayButton;
    View rootLayout;
    private View overlayView;
    private View overlayEditView;
    private ImageButton goBackArrow;
    private TextInputEditText imageTitle;
    private TextInputEditText imageDescrpition;
    private ArrayList<Uri> imageUris = new ArrayList<>();
    private TextInputEditText dateImage;
    private RecyclerView carouselRecyclerView;
    private ImageCardItemAdapter adapter;
    private List<ImageCardItem> imageCardItems = new ArrayList<>();
    private Button saveImage;
    private ImageCardItemViewModel imageCardItemViewModel;
    int checkpointDiaryId;
    TextView nomeTappa;
    TextView dataTappa;
    ImageView immagineTappa;
    List<ImageCardItem> imageCardItemList = new ArrayList<>();
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

        IImageCardItemRepository imageCardItemRepository = ServiceLocator.getINSTANCE().getImageCardItemRepository(this);
        imageCardItemViewModel = new ViewModelProvider(this,
                new ViewModelFactory(imageCardItemRepository))
                .get(ImageCardItemViewModel.class);


        RecyclerView recyclerViewCards = findViewById(R.id.imageCardItemRecyclerView);
        imageCardItemAdapter = new ImageCardItemAdapter(this);
        recyclerViewCards.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCards.setAdapter(imageCardItemAdapter);

        nomeTappaString = getIntent().getStringExtra("nomeTappa");
        imageUri = Uri.parse(getIntent().getStringExtra("immagineTappaUri"));
        dataTappaString = getIntent().getStringExtra("dataTappa");

        checkpointDiaryId = getIntent().getIntExtra("checkpointDiaryId", -1);

        if (checkpointDiaryId == -1) {
            checkpointDiaryId = SharedPreferencesUtils.getCheckpointDiaryId(this);
            Log.d("CheckpointDiaryActivity", "Retrieved checkpointDiaryId from SharedPreferences: " + checkpointDiaryId);
        }

        if (checkpointDiaryId > 0) {
            SharedPreferencesUtils.saveCheckpointDiaryId(this, checkpointDiaryId);
            imageCardItemViewModel.setCheckpointDiaryId(checkpointDiaryId);
            Log.d("CheckpointDiaryActivity", "Set checkpointDiaryId in ViewModel: " + checkpointDiaryId);
        }


        imageCardItemViewModel.getImageCardItemsLiveData().observe(this, imageCardItems -> {
            imageCardItemAdapter.setImageCardItems(imageCardItems);
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
        });

        editCheckpointDiaryImage.setOnClickListener(v -> {
            List<ImageCardItem> selectedItems = imageCardItemAdapter.getSelectedItems();
            if (!selectedItems.isEmpty() && selectedItems.size() == 1) {
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

            if (!title.isEmpty() && !description.isEmpty() && !date.isEmpty()) {
                Uri ImageUri = (selectedImageUri != null) ? saveImageToInternalStorage(selectedImageUri) :
                        Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.ic_launcher_background);

                if (ImageUri == null) {
                    Snackbar.make(this, rootLayout, "Errore durante il salvataggio dell'immagine",
                            Snackbar.LENGTH_SHORT).show();
                    return;
                }

                imageCardItemViewModel.insertImageCardItem(title, description, date, ImageUri, this);

                imageCardItemViewModel.resetParam(title, description, date);
                previewImageCheckpointDiary.setImageURI(null);
                textPreviewImageCheckpointDiary.setVisibility(View.VISIBLE);
                addCheckpointDiaryImage.setVisibility(View.VISIBLE);
                goBackArrow.setVisibility(View.VISIBLE);
                overlayView.setVisibility(View.GONE);

                Snackbar.make(this, rootLayout, "La tua immagine è stata aggiunta con successo",
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(this, rootLayout, "Compila tutti i campi",
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

            Uri newImageUri = selectedImageUriDiaryEdited;

            if (selectedCardItem != null) {
                if (!newTitle.isEmpty()) {
                    Uri finalImageUri = (newImageUri != null) ?
                            saveImageToInternalStorage(newImageUri) :
                            Uri.parse(selectedCardItem.getImageUri());

                    // Chiama il ViewModel per aggiornare la card
                    imageCardItemViewModel.updateImageCardItem(
                            selectedCardItem.getId(),
                            newTitle,
                            newDescription,
                            newDate,
                            finalImageUri,
                            CheckpointDiaryActivity.this
                    );

                    // Resetta l'UI
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


    @Override
    protected void onResume() {
        super.onResume();
        // Ensure we still have a valid ID when resuming
        checkpointDiaryId = SharedPreferencesUtils.getCheckpointDiaryId(this);
        if (checkpointDiaryId > 0) {
            imageCardItemViewModel.setCheckpointDiaryId(checkpointDiaryId);
            imageCardItemViewModel.fetchAllImageCardItems();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private Uri saveImageToInternalStorage(Uri sourceUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), sourceUri);

            File storageDir = new File(getFilesDir(), "checkpoint_images");
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }

            String fileName = "tappa_" + System.currentTimeMillis() + ".jpg";
            File imageFile = new File(storageDir, fileName);

            try (FileOutputStream out = new FileOutputStream(imageFile)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            }

            return Uri.fromFile(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("CheckpointDiaryActivity", "Errore durante il salvataggio dell'immagine: " + e.getMessage());
            return null;
        }
    }

    private void openEditOverlay(ImageCardItem item) {
        selectedCardItem = item; // Imposta la card selezionata
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

}



