    package com.unimib.triptales.ui.diario.fragment;

    import static android.app.Activity.RESULT_OK;

    import android.content.Intent;
    import android.graphics.Bitmap;
    import android.graphics.drawable.Drawable;
    import android.net.Uri;
    import android.os.Bundle;
    import android.provider.MediaStore;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageButton;
    import android.widget.ImageView;
    import android.widget.LinearLayout;
    import android.widget.TextView;

    import androidx.activity.result.ActivityResultCallback;
    import androidx.activity.result.ActivityResultLauncher;
    import androidx.activity.result.contract.ActivityResultContracts;
    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.constraintlayout.widget.ConstraintLayout;
    import androidx.fragment.app.Fragment;

    import com.google.android.material.floatingactionbutton.FloatingActionButton;
    import com.google.android.material.imageview.ShapeableImageView;
    import com.unimib.triptales.R;
    import androidx.activity.result.ActivityResultLauncher;
    import androidx.activity.result.contract.ActivityResultContracts;
    import android.widget.Toast;

    import java.io.IOException;
    import java.util.Date;


    public class TappeFragment extends Fragment {

        private FloatingActionButton addTappaBtn;
        private ConstraintLayout layoutTappe;
        private ImageButton backTappa;
        private LayoutInflater inflater;
        private EditText editNome;
        private EditText editData;
        private View overlay_add_tappa;
        private Button salvaTappa;
        private LinearLayout tappeCardContainer;
        private Button inputImmagine;
        private ShapeableImageView immagineTappaPreview;

        private ActivityResultLauncher<Intent> imagePickerLauncher;
        private Uri selectedImageUri; // Variabile per salvare l'immagine selezionata
        private String inputNome;
        private String inputData;
        private TextView anteprimaImmagine;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_tappe, container, false);
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Inizializza il launcher per la selezione delle immagini
            imagePickerLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            selectedImageUri = result.getData().getData();
                            if (selectedImageUri != null && immagineTappaPreview != null) {
                                immagineTappaPreview.setImageURI(selectedImageUri);
                                anteprimaImmagine.setVisibility(View.GONE);
                            }
                        }
                    }
            );
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            // Inizializza i riferimenti
            layoutTappe = view.findViewById(R.id.layoutTappe);
            inflater = LayoutInflater.from(view.getContext());

            overlay_add_tappa = inflater.inflate(R.layout.overlay_add_tappa, layoutTappe, false);
            layoutTappe.addView(overlay_add_tappa);
            overlay_add_tappa.setVisibility(View.GONE);

            // Inizializza i componenti di overlay_add_tappa
            addTappaBtn = view.findViewById(R.id.addTappaButton);
            editNome = view.findViewById(R.id.editTextTappa);
            editData = view.findViewById(R.id.editDataTappa);
            salvaTappa = view.findViewById(R.id.salvaTappaButton);
            immagineTappaPreview = overlay_add_tappa.findViewById(R.id.immagineTappa); // Aggiunto il riferimento alla preview dell'immagine
            tappeCardContainer = view.findViewById(R.id.tappeCardContainer);
            anteprimaImmagine = overlay_add_tappa.findViewById(R.id.anteprimaImmagine);

            // Listener per il pulsante "Aggiungi Tappa"
            addTappaBtn.setOnClickListener(v -> {
                overlay_add_tappa.setVisibility(View.VISIBLE);
                addTappaBtn.setVisibility(View.GONE);
                editNome.setText("");
                editData.setText("");
                selectedImageUri = null; // Resetta l'immagine selezionata
                anteprimaImmagine.setVisibility(View.VISIBLE);
                immagineTappaPreview.setImageDrawable(null);
            });

            // Listener per il pulsante "Torna Indietro"
            backTappa = view.findViewById(R.id.backTappaButton);
            backTappa.setOnClickListener(v -> {
                overlay_add_tappa.setVisibility(View.GONE);
                addTappaBtn.setVisibility(View.VISIBLE);
            });

            // Listener per il pulsante "Inserisci un'immagine"
            inputImmagine = view.findViewById(R.id.inserisciImmagineTappaButton);
            inputImmagine.setOnClickListener(v -> {
                Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imagePickerLauncher.launch(pickImageIntent);
                immagineTappaPreview.setVisibility(View.VISIBLE);
                anteprimaImmagine.setVisibility(View.GONE);
            });

            // Listener per il pulsante "Salva Tappa"
            salvaTappa.setOnClickListener(v -> {
                inputNome = editNome.getText().toString();
                inputData = editData.getText().toString();

                if (inputNome.isEmpty() || inputData.isEmpty()) {
                    if (inputNome.isEmpty()) editNome.setError("Inserisci il nome della tappa");
                    if (inputData.isEmpty()) editData.setError("Inserisci la data della tappa");

                } else {
                    View cardView = inflater.inflate(R.layout.item_card_tappa, tappeCardContainer, false);
                    TextView nomeTappaCard = cardView.findViewById(R.id.nomeTappaCard);
                    ImageView immagineTappaCard = cardView.findViewById(R.id.immagineTappa);


                    nomeTappaCard.setText(inputNome);

                    if (selectedImageUri != null) {
                        anteprimaImmagine.setVisibility(View.GONE);
                        immagineTappaCard.setImageURI(selectedImageUri);
                    } else {
                        immagineTappaCard.setImageResource(R.drawable.roma); // Immagine di default
                    }

                    tappeCardContainer.addView(cardView);
                    overlay_add_tappa.setVisibility(View.GONE);
                    addTappaBtn.setVisibility(View.VISIBLE);
                    editNome.setText("");
                    editData.setText("");
                    selectedImageUri = null; // Resetta l'immagine selezionata
                }
            });
        }
    }
