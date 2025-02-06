package com.unimib.triptales.ui.diary;


import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.unimib.triptales.R;
import com.unimib.triptales.adapters.ImageCardItemAdapter;
import com.unimib.triptales.model.ImageCardItem;
import com.unimib.triptales.repository.imageCardItem.IImageCardItemRepository;
import com.unimib.triptales.repository.imageCardItem.ImageCardItemRepository;
import com.unimib.triptales.ui.diary.viewmodel.ImageCardItemViewModel;
import com.unimib.triptales.ui.diary.viewmodel.ViewModelFactory;
import com.unimib.triptales.util.ServiceLocator;
import com.unimib.triptales.util.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;


public class CheckpointDiaryActivity extends AppCompatActivity {

    private TextView descrizioneTappa;
    private ActivityResultLauncher<Intent> pickImageLauncher;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkpoint_diary);

        ViewGroup rootLayout = findViewById(android.R.id.content);
        LayoutInflater inflater = getLayoutInflater();
        overlayView = inflater.inflate(R.layout.overlay_add_checkpoint_image, rootLayout, false);
        overlayView.setVisibility(View.GONE); // Nasconde inizialmente l'overlay
        rootLayout.addView(overlayView);

        IImageCardItemRepository imageCardItemRepository = ServiceLocator.getINSTANCE().getImageCardItemRepository(this);

        imageCardItemViewModel = new ViewModelProvider(this,
                new ViewModelFactory(imageCardItemRepository))
                .get(ImageCardItemViewModel.class);

        imageCardItemViewModel.loadImageCardItems(this);

        // Imposta il RecyclerView
        carouselRecyclerView = findViewById(R.id.carouselRecyclerViewCheckpointDiary);
        carouselRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ImageCardItemAdapter(new ArrayList<>());
        carouselRecyclerView.setAdapter(adapter);

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            ImageView imageView = findViewById(R.id.previewImageCheckpointDiary);
                            imageView.setImageURI(selectedImageUri);
                        }
                    }
                }
        );

        imageCardItemViewModel.getAllImageCardItems().observe(this, imageCardItems -> {
            adapter.setImageCardItems(imageCardItems);
            adapter.notifyDataSetChanged();
        });


        // Gestione dei pulsanti e del comportamento dell'overlay
        addCheckpointDiaryImage = findViewById(R.id.addCheckpointDiaryImage);
        addCheckpointDiaryImage.setOnClickListener(v -> overlayView.setVisibility(View.VISIBLE));

        closeOverlayButton = overlayView.findViewById(R.id.goBackArrowButtonCheckpointDiary);
        closeOverlayButton.setOnClickListener(v -> overlayView.setVisibility(View.GONE));
        goBackArrow = findViewById(R.id.backToTappaFragment);
        goBackArrow.setOnClickListener(v -> finish());

        // Altri setup (come il titolo e la data della tappa)
        nomeTappaString = getIntent().getStringExtra("nomeTappa");
        imageUri = Uri.parse(getIntent().getStringExtra("immagineTappaUri"));
        dataTappaString = getIntent().getStringExtra("dataTappa");
        int checkpointDiaryId = getIntent().getIntExtra("checkpointDiaryId", -1);
        if (checkpointDiaryId != -1) {
            SharedPreferencesUtils.saveCheckpointDiaryId(this, checkpointDiaryId);
            Log.d("CheckpointDiaryActivity", "Saved checkpointDiaryId: " + checkpointDiaryId);
        }

        TextView nomeTappa = findViewById(R.id.nomeTappa);
        nomeTappa.setText(nomeTappaString);
        TextView dataTappa = findViewById(R.id.dataTappa);
        dataTappa.setText(dataTappaString);
        ImageView immagineTappa = findViewById(R.id.immagineTappaItem);
        if (imageUri != null) {
            immagineTappa.setImageURI(imageUri);
        } else {
            immagineTappa.setImageResource(R.drawable.ic_launcher_background); // Immagine di default
        }

        imageTitle = overlayView.findViewById(R.id.imageTitle);
        imageDescrpition = overlayView.findViewById(R.id.imageDescription);
        dateImage = overlayView.findViewById(R.id.addDateCheckpointDiary);
        previewImage = overlayView.findViewById(R.id.previewImageCheckpointDiary);
        openGallery = overlayView.findViewById(R.id.addImageCheckpointDiaryButton);

        openGallery.setOnClickListener(v -> apriGalleria());

        saveImage = overlayView.findViewById(R.id.saveCheckpointDiaryButton);
        saveImage.setOnClickListener(v -> {
            String title = imageTitle.getText().toString();
            String description = imageDescrpition.getText().toString();
            String date = dateImage.getText().toString();

            Log.d("CheckpointDiaryActivity", "Saving image with title: " + title);

            if (!title.isEmpty() && !description.isEmpty() && !date.isEmpty()) {
                Log.d("CheckpointDiaryActivity", "All fields are filled, proceeding with insert");
                imageCardItemViewModel.insertImageCardItem(title, description, date, selectedImageUri, this);
                overlayView.setVisibility(View.GONE);
            } else {
                Log.d("CheckpointDiaryActivity", "Some fields are empty");
            }
        });


        /*
        rootLayout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hideKeyboard();

                descrizioneTappa = findViewById(R.id.descrizioneTappa);
                String inputDescrizione = descrizioneTappa.getText().toString().trim();
                descrizioneTappa.setText(inputDescrizione);
                v.performClick();
                return true;
            }
            return false;
        });

         */

    }

    // Metodo per nascondere la tastiera
    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }
    }

    private void apriGalleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }
}


