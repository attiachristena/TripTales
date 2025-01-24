

package com.unimib.triptales.ui.diary.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.unimib.triptales.R;
import com.unimib.triptales.database.AppRoomDatabase;
import com.unimib.triptales.database.TappaDao;
import com.unimib.triptales.model.Tappa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class TappeFragment extends Fragment implements OnMapReadyCallback {

    FloatingActionButton addTappaBtn;
    ConstraintLayout layoutTappe;
    ImageButton backTappa;
    LayoutInflater inflater;
    EditText editNome;
    EditText editData;
    View overlay_add_tappa;
    Button salvaTappa;
    LinearLayout tappeCardContainer;
    ShapeableImageView immagineTappaPreview;
    ActivityResultLauncher<Intent> imagePickerLauncher;
    String inputNome;
    String inputData;
    TextView anteprimaImmagine;
    MapView mappaTappe;
    FloatingActionButton eliminaTappaButton;
    FloatingActionButton modificaTappaButton;
    View cardView;
    TextView nomeTappaCard;
    ImageView immagineTappaCard;
    int indice;
    int indicePos;
    ImageButton backModificaTappa;
    View overlay_modifica_tappa;
    Button salvaModificaTappa;
    EditText editModificaNome;
    String inputModificaNome;
    Button inputModificaImmagine;
    ShapeableImageView anteprimaImmagineModificata;
    ShapeableImageView anteprimaImmagineTappaMappa;
    ActivityResultLauncher<Intent> imagePickerLauncherModifica;
    TextView testoAnteprimaImmagineModificata;
    TextView testoAnteprimaImmagineMappa;
    Uri selectedImageUriModifica;
    EditText editModificaData;
    GoogleMap googleMap;
    RelativeLayout mappaContainer;
    ImageButton closeMapButton;
    TappaDao tappaDao;
    List<Tappa> tappeSalvate;
    Set<Integer> selectedTappeIds = new HashSet<>();
    AppRoomDatabase database;
    List<String> tappeEsistenti = new ArrayList<>(); // Per controllare le tappe esistenti
    FrameLayout mapOverlay;
    MapView expandedMapView;
    GoogleMap expandedMap;
    Uri selectedImageUriMappa;
    List<MaterialCardView> tappeSelezionate = new ArrayList<>();
    EditText nomeTappaMappaEditText;
    EditText dataTappaEditText;
    Button salvaTappaButton;
    ImageButton backTappaButton;
    Button inputImmagine;
    String nomeTappa;
    String dataTappa;
    Uri selectedImageUri;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tappe, container, false);

        // Inizializza le viste
        mappaTappe = rootView.findViewById(R.id.mappaTappe);
        tappeCardContainer = rootView.findViewById(R.id.tappeCardContainer);
        mapOverlay = rootView.findViewById(R.id.mapOverlay);
        expandedMapView = mapOverlay.findViewById(R.id.expandedMap);
        closeMapButton = mapOverlay.findViewById(R.id.closeMapButton);



        database = AppRoomDatabase.getDatabase(getContext());
        tappaDao = database.tappaDao();
        tappeSalvate = tappaDao.getAllTappe(); // Recupera le tappe salvate

        SearchView searchView = rootView.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchLocation(query);  // Cerca il luogo
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mappaTappe.onCreate(savedInstanceState);
        mappaTappe.getMapAsync(googleMap -> {
            this.googleMap = googleMap;

            LatLng posizioneIniziale = new LatLng(46.414382, 10.013988);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posizioneIniziale, 10));

            googleMap.setOnMapClickListener(this::mostraOverlayMappe);

            // Chiama caricaTappeSalvate solo quando googleMap è pronto
            if (googleMap != null) {
                caricaTappeSalvate();
            }
        });


        closeMapButton.setOnClickListener(v -> {
            mapOverlay.setVisibility(View.GONE);  // Nascondi l'overlay
        });

        return rootView;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        layoutTappe = view.findViewById(R.id.layoutTappe);
        inflater = LayoutInflater.from(view.getContext());
        tappeCardContainer = view.findViewById(R.id.tappeCardContainer);
        indice = 0;

        overlay_modifica_tappa = inflater.inflate(R.layout.overlay_modifica_tappa, layoutTappe, false);
        overlay_modifica_tappa.setVisibility(View.GONE);

        eliminaTappaButton = view.findViewById(R.id.eliminaTappa);
        modificaTappaButton = view.findViewById(R.id.modificaTappa);
        salvaModificaTappa = overlay_modifica_tappa.findViewById(R.id.salvaModificaTappaButton);
        backModificaTappa = overlay_modifica_tappa.findViewById(R.id.backModificaTappaButton);
        editModificaNome = overlay_modifica_tappa.findViewById(R.id.editModificaNomeTappa);
        inputModificaImmagine = overlay_modifica_tappa.findViewById(R.id.inserisciImmagineModificaTappaButton);
        anteprimaImmagineModificata = overlay_modifica_tappa.findViewById(R.id.anteprimaImmagineModificata);
        testoAnteprimaImmagineModificata = overlay_modifica_tappa.findViewById(R.id.testoAnteprimaImmagineModificata);
        editModificaData = overlay_modifica_tappa.findViewById(R.id.editModificaDataTappa);

        overlay_add_tappa = getLayoutInflater().inflate(R.layout.overlay_add_tappa_mappa, layoutTappe, false);
        anteprimaImmagineTappaMappa = overlay_add_tappa.findViewById(R.id.anteprimaImmagineTappaMappa);
        testoAnteprimaImmagineMappa = overlay_add_tappa.findViewById(R.id.testoAnteprimaImmagineMappa);
        cardView = inflater.inflate(R.layout.item_card_tappa, tappeCardContainer, false);
        immagineTappaCard = cardView.findViewById(R.id.anteprimaImmagineCard);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();

                        if (selectedImageUri != null) {
                            selectedImageUriMappa = selectedImageUri;
                            immagineTappaPreview.setImageURI(selectedImageUri);
                            testoAnteprimaImmagineMappa.setVisibility(View.GONE);

                        } else {
                            testoAnteprimaImmagineMappa.setVisibility(View.VISIBLE);
                        }

                    }
                }
        );
    }




    private void searchLocation(String location) {
        Geocoder geocoder = new Geocoder(getContext());
        try {
            List<Address> addresses = geocoder.getFromLocationName(location, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                spostaMappa(latLng);
                aggiungiMarkerTappa(latLng, address.getFeatureName());
            } else {
                Toast.makeText(getContext(), "Luogo non trovato", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Errore nella ricerca del luogo", Toast.LENGTH_SHORT).show();
        }
    }

    private void spostaMappa(LatLng latLng) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    private void aggiungiMarkerTappa(LatLng latLng, String title) {
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(latLng).title(title).snippet("Clicca per aggiungere la tappa"));
        googleMap.setOnMarkerClickListener(marker -> {
            mostraOverlaySalvataggioTappa(latLng);
            return true;
        });
    }

    private void mostraOverlaySalvataggioTappa(LatLng latLng) {
        if (overlay_add_tappa != null && layoutTappe.indexOfChild(overlay_add_tappa) != -1) {
            layoutTappe.removeView(overlay_add_tappa);
        }

        layoutTappe.addView(overlay_add_tappa);

        nomeTappaMappaEditText = overlay_add_tappa.findViewById(R.id.editNomeTappaMappa);
        dataTappaEditText = overlay_add_tappa.findViewById(R.id.editDataTappaMappa);
        salvaTappaButton = overlay_add_tappa.findViewById(R.id.salvaTappaMappaButton);
        backTappaButton = overlay_add_tappa.findViewById(R.id.backTappaMappaButton);
        inputImmagine = overlay_add_tappa.findViewById(R.id.inserisciImmagineTappaMappaButton);
        immagineTappaCard = cardView.findViewById(R.id.anteprimaImmagineCard);
        immagineTappaPreview = overlay_add_tappa.findViewById(R.id.anteprimaImmagineTappaMappa);
        nomeTappa = nomeTappaMappaEditText.getText().toString();
        dataTappa = dataTappaEditText.getText().toString();
        selectedImageUri = selectedImageUriMappa;

        inputImmagine.setOnClickListener(v -> {
            Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            imagePickerLauncher.launch(pickImageIntent);
        });

        salvaTappaButton.setOnClickListener(v -> {
            String nomeTappa = nomeTappaMappaEditText.getText().toString();
            String dataTappa = dataTappaEditText.getText().toString();
            Uri selectedImageUri = selectedImageUriMappa;

            if (!nomeTappa.isEmpty()) {

                Uri imageUri;
                if (selectedImageUri != null) {
                    imageUri = saveImageToInternalStorage(selectedImageUri);
                } else {
                    imageUri = Uri.parse("android.resource://" + getContext().getPackageName() + "/" + getRandomImage());
                }

                if (imageUri != null) {
                    addTappa(nomeTappa, dataTappa, imageUri.toString(), latLng);
                    layoutTappe.removeView(overlay_add_tappa);
                    nomeTappaMappaEditText.setText("");
                    dataTappaEditText.setText("");
                    immagineTappaPreview.setImageURI(null);
                } else {
                    Toast.makeText(getContext(), "Errore nel salvataggio dell'immagine", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Nome della tappa vuoto", Toast.LENGTH_SHORT).show();
            }
        });

        backTappaButton.setOnClickListener(v -> {
            layoutTappe.removeView(overlay_add_tappa);
            nomeTappaMappaEditText.setText("");
            dataTappaEditText.setText("");
            immagineTappaPreview.setImageURI(null);
        });
    }


    private void addTappa(String nome, String data, String immagine, LatLng latLng) {
        if (tappeEsistenti.contains(nome)) {
            Toast.makeText(getContext(), "Tappa già esistente", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isPosizioneGiàSalvata(latLng)) {
            Toast.makeText(getContext(), "Questa posizione è già associata a una tappa", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri internalImageUri = saveImageToInternalStorage(Uri.parse(immagine));

        Tappa nuovaTappa = new Tappa(nome, data, internalImageUri.toString(), latLng.latitude, latLng.longitude);
        long tappaId = tappaDao.insertTappa(nuovaTappa);

        if (tappaId > 0) {
                tappeEsistenti.add(nome);
                aggiungiTappaAllaMappa(nome, latLng);
                aggiungiTappaAllaLista(nome, data, immagine, (int) tappaId);
        } else {
                Toast.makeText(getContext(), "Errore nell'aggiunta della tappa", Toast.LENGTH_SHORT).show();
        }
    }



    private boolean isPosizioneGiàSalvata(LatLng latLng) {
        List<Tappa> tappeSalvate = tappaDao.getAllTappe();

        for (Tappa tappa : tappeSalvate) {
            if (tappa.getLatitude() == latLng.latitude && tappa.getLongitude() == latLng.longitude) {
                return true;
            }
        }
        return false;
    }


    private void aggiungiTappaAllaMappa(String nome, LatLng latLng) {
        googleMap.addMarker(new MarkerOptions().position(latLng).title(nome));
    }


    private void aggiungiTappaAllaLista(String nome, String data, String immagine, int tappaId) {
        MaterialCardView cardView = (MaterialCardView) getLayoutInflater().inflate(R.layout.item_card_tappa, tappeCardContainer, false);
        TextView nomeTappaCard = cardView.findViewById(R.id.nomeTappaCard);
        TextView dataTappaCard = cardView.findViewById(R.id.dataTappaCard);
        ImageView immagineTappaCard = cardView.findViewById(R.id.anteprimaImmagineCard);

        nomeTappaCard.setText(nome);
        dataTappaCard.setText(data);

        if (immagine != null) {
            try {
                Uri immagineUri = Uri.parse(immagine);
                InputStream inputStream = getContext().getContentResolver().openInputStream(immagineUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                immagineTappaCard.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
                immagineTappaCard.setImageResource(getRandomImage()); // Immagine di default
            }
        } else {
            immagineTappaCard.setImageResource(getRandomImage()); // Immagine di default se immagine è null
        }




        tappeCardContainer.addView(cardView);
        cardView.setTag(tappaId);

        cardView.setOnLongClickListener(v -> {
            if (cardView.isSelected()) {
                // Deseleziona la tappa
                cardView.setSelected(false);
                cardView.setStrokeColor(getResources().getColor(R.color.primary));
                tappeSelezionate.remove(cardView);  // Rimuovi dalla lista delle selezionate
            } else {
                // Seleziona la tappa
                cardView.setSelected(true);
                cardView.setStrokeColor(getResources().getColor(R.color.background_dark));
                tappeSelezionate.add(cardView);  // Aggiungi alla lista delle selezionate
            }

            // Aggiorna lo stato del pulsante di modifica e eliminazione
            if (tappeSelezionate.isEmpty()) {
                modificaTappaButton.setVisibility(View.GONE);
                eliminaTappaButton.setVisibility(View.GONE);
            } else if (tappeSelezionate.size() == 1) {
                modificaTappaButton.setVisibility(View.VISIBLE);
                eliminaTappaButton.setVisibility(View.VISIBLE);
            } else {
                modificaTappaButton.setVisibility(View.GONE);
                eliminaTappaButton.setVisibility(View.VISIBLE);
            }
            return true;
        });

        cardView.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), TappaActivity.class);
            intent.putExtra("nomeTappa", nomeTappaCard.getText().toString());
            intent.putExtra("dataTappa", dataTappaCard.getText().toString());  // Passa la data
            startActivity(intent);
        });

        salvaModificaTappa.setOnClickListener(v1 -> {
            String inputModificaNome = editModificaNome.getText().toString();
            String inputModificaData = editModificaData.getText().toString();  // Campo per modificare la data
            nomeTappaCard.setText(inputModificaNome);
            dataTappaCard.setText(inputModificaData);  // Modifica della data

            TappaDao tappaDao = AppRoomDatabase.getDatabase(getContext()).tappaDao();
            Tappa tappa = tappaDao.getTappaById(tappaId);
            if (tappa != null) {
                tappa.setNome(inputModificaNome);
                tappa.setData(inputModificaData);  // Modifica della data
                tappaDao.updateTappa(tappa);
            }

            overlay_modifica_tappa.setVisibility(View.GONE);
            cardView.setCardBackgroundColor(getResources().getColor(R.color.primary));
            cardView.setStrokeColor(getResources().getColor(R.color.primary));
            cardView.setSelected(false);
            modificaTappaButton.setVisibility(View.GONE);
            eliminaTappaButton.setVisibility(View.GONE);
        });

        // Gestione eliminazione delle tappe selezionate
        eliminaTappaButton.setOnClickListener(v -> {
            if (!tappeSelezionate.isEmpty()) {
                List<Integer> idsToDelete = new ArrayList<>();
                List<MaterialCardView> toRemove = new ArrayList<>();

                // Raccogli gli ID delle tappe selezionate e le card da rimuovere dalla UI
                for (MaterialCardView selectedCard : tappeSelezionate) {
                    int idTappa = (int) selectedCard.getTag();
                    idsToDelete.add(idTappa);
                    toRemove.add(selectedCard);
                }

                // Elimina tutte le tappe selezionate dal database
                TappaDao tappaDao = AppRoomDatabase.getDatabase(getContext()).tappaDao();
                tappaDao.deleteTappeById(idsToDelete);

                // Rimuovi le card dalla UI
                for (MaterialCardView selectedCard : toRemove) {
                    tappeCardContainer.removeView(selectedCard);
                }

                // Ripristina la lista delle tappe selezionate
                tappeSelezionate.clear();

                // Nascondi i pulsanti di modifica e eliminazione
                modificaTappaButton.setVisibility(View.GONE);
                eliminaTappaButton.setVisibility(View.GONE);

                Toast.makeText(getContext(), "Tappe selezionate eliminate", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Seleziona almeno una tappa da eliminare", Toast.LENGTH_SHORT).show();
            }
        });

        modificaTappaButton.setOnClickListener(v -> {
            overlay_modifica_tappa.setVisibility(View.VISIBLE);
            editModificaNome.setText(nomeTappaCard.getText().toString());
            editModificaData.setText(dataTappaCard.getText().toString());  // Imposta la data nel campo di modifica
            cardView.setCardBackgroundColor(getResources().getColor(R.color.primary));
            cardView.setStrokeColor(getResources().getColor(R.color.primary));
            cardView.setSelected(false);
        });

        backModificaTappa.setOnClickListener(v -> {
            overlay_modifica_tappa.setVisibility(View.GONE);
            modificaTappaButton.setVisibility(View.GONE);
            eliminaTappaButton.setVisibility(View.GONE);
            cardView.setCardBackgroundColor(getResources().getColor(R.color.primary));
            cardView.setStrokeColor(getResources().getColor(R.color.primary));
            cardView.setSelected(false);
        });
    }


    private void mostraOverlayMappe(LatLng latLng) {
        mapOverlay.setVisibility(View.VISIBLE);
        expandedMapView.onCreate(null);

        expandedMapView.getMapAsync(googleMap -> {
            expandedMap = googleMap;
            LatLng posizioneIniziale = latLng != null ? latLng : new LatLng(46.414382, 10.013988);
            expandedMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posizioneIniziale, 10));
            caricaTappeSalvateOnExpandedMap();
        });
    }

    private void caricaTappeSalvateOnExpandedMap() {
        List<Tappa> tappe = tappaDao.getAllTappe();
        for (Tappa tappa : tappe) {
            LatLng latLng = new LatLng(tappa.getLatitude(), tappa.getLongitude());
            expandedMap.addMarker(new MarkerOptions().position(latLng).title(tappa.getNome()));
        }

        if (!tappe.isEmpty()) {
            Tappa ultimaTappa = tappe.get(tappe.size() - 1);
            LatLng ultimaPosizione = new LatLng(ultimaTappa.getLatitude(), ultimaTappa.getLongitude());
            expandedMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ultimaPosizione, 15));
        }
    }

    private void caricaTappeSalvate() {
        List<Tappa> tappe = tappaDao.getAllTappe();
        for (Tappa tappa : tappe) {
            LatLng latLng = new LatLng(tappa.getLatitude(), tappa.getLongitude());
            aggiungiTappaAllaMappa(tappa.getNome(), latLng);
            aggiungiTappaAllaLista(tappa.getNome(), tappa.getData(), tappa.getImmagineUri(), tappa.getId());
        }

        if (!tappe.isEmpty()) {
            Tappa ultimaTappa = tappe.get(tappe.size() - 1);
            LatLng ultimaPosizione = new LatLng(ultimaTappa.getLatitude(), ultimaTappa.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ultimaPosizione, 15));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (googleMap != null) {
            LatLng posizioneIniziale = new LatLng(46.414382, 10.013988);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posizioneIniziale, 10));
            googleMap.setOnMapClickListener(this::mostraOverlaySalvataggioTappa);
        } else {
            Log.e("MapError", "GoogleMap non inizializzata correttamente.");
        }
    }

    private Uri saveImageToInternalStorage(Uri sourceUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), sourceUri);
            File storageDir = getContext().getFilesDir();
            String fileName = "tappa_" + System.currentTimeMillis() + ".jpg";
            File imageFile = new File(storageDir, fileName);

            try (FileOutputStream out = new FileOutputStream(imageFile)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            }

            return Uri.fromFile(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    private int getRandomImage() {
        int[] defaultImages = {
                R.drawable.a1,
                R.drawable.a2,
                R.drawable.a3,
                R.drawable.a4,
                R.drawable.a5,
                R.drawable.a6,
                R.drawable.a7,
                R.drawable.a8,
                R.drawable.a9,
                R.drawable.a10,
                R.drawable.a11,
                R.drawable.a12,
                R.drawable.a13,
                R.drawable.a14,
                R.drawable.a15,
                R.drawable.a16
        };

        Random random = new Random();
        int randomIndex = random.nextInt(defaultImages.length);

        return defaultImages[randomIndex];
    }





}