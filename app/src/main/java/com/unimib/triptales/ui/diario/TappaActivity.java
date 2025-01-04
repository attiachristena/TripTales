package com.unimib.triptales.ui.diario;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unimib.triptales.R;
import com.unimib.triptales.adapters.CarouselAdapter;

import java.util.ArrayList;

public class TappaActivity extends AppCompatActivity {

    private TextView descrizioneTappa;
    private ArrayList<Uri> imageUris;
    private CarouselAdapter carouselAdapter;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tappa);

        // Ottieni i dati passati dall'intent
        String nomeTappaString = getIntent().getStringExtra("nomeTappa");
        Uri imageUri = Uri.parse(getIntent().getStringExtra("immagineTappaUri"));
        String dataTappaString = getIntent().getStringExtra("dataTappa");
        imageUris = new ArrayList<>();




        // Configura il launcher per selezionare immagini
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        if (selectedImage != null) {
                            imageUris.add(selectedImage);
                            carouselAdapter.notifyItemInserted(imageUris.size() - 1);
                        }
                    }
                }
        );

        // Configura i componenti della UI
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

        ImageButton backButton = findViewById(R.id.backToTappaFragment);
        backButton.setOnClickListener(v -> finish());

        // Listener sul layout principale
        View mainLayout = findViewById(R.id.main);
        mainLayout.setOnTouchListener((v, event) -> {
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


        // Configura il RecyclerView
        RecyclerView carouselRecyclerView = findViewById(R.id.carouselRecyclerView);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        carouselRecyclerView.setLayoutManager(layoutManager);

        carouselAdapter = new CarouselAdapter(this, imageUris);
        carouselRecyclerView.setAdapter(carouselAdapter);

        // Imposta la larghezza degli item nel RecyclerView (larghezza fissa per ogni immagine)
        int itemWidth = getResources().getDisplayMetrics().widthPixels / 2; // Ogni immagine occuperà metà della larghezza dello schermo
        carouselRecyclerView.setPadding(0, 0, 0, 0);
        carouselRecyclerView.setClipToPadding(false); // Non taglia gli elementi fuori dalla visibilità

        carouselRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                applyHeroEffect(recyclerView);
            }
        });

        // Bottone per aggiungere immagini
        Button aggiungiImmagineButton = findViewById(R.id.aggiungiImmagineCar);
        aggiungiImmagineButton.setOnClickListener(v -> apriGalleria());
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

    // Metodo per aprire la galleria
    private void apriGalleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        pickImageLauncher.launch(intent); // Usa il launcher invece di startActivityForResult
    }

    // Metodo per applicare l'effetto hero (scaling delle immagini)
    private void applyHeroEffect(RecyclerView recyclerView) {
        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();

        for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
            View view = layoutManager.findViewByPosition(i);
            if (view != null) {
                float center = recyclerView.getWidth() / 2f;
                float distance = Math.abs(view.getX() + view.getWidth() / 2 - center);
                float scale = Math.max(1 - distance / recyclerView.getWidth(), 0.85f); // Aumenta il valore minimo di scala
                view.setScaleX(scale);
                view.setScaleY(scale);
            }
        }
    }
}
