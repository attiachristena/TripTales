
package com.unimib.triptales.ui.diary.fragment;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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
    Button inputImmagine;
    ShapeableImageView immagineTappaPreview;
    ActivityResultLauncher<Intent> imagePickerLauncher;
    Uri selectedImageUri;
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
    ArrayList<MaterialCardView> listTappeCards;
    ImageButton backModificaTappa;
    View overlay_modifica_tappa;
    Button salvaModificaTappa;
    EditText editModificaNome;
    String inputModificaNome;
    Button inputModificaImmagine;
    ShapeableImageView anteprimaImmagineModificata;
    ActivityResultLauncher<Intent> imagePickerLauncherModifica;
    TextView testoAnteprimaImmagineModificata;
    Uri selectedImageUriModifica;
    EditText editModificaData;
    GoogleMap googleMap;
    SearchView searchView;
    boolean isMapExpanded = false;
    RelativeLayout mappaContainer;
    View overlay_mappa;
    ImageButton closeMapButton;
    TappaDao tappaDao;
    List<Tappa> tappeSalvate;
    Set<Integer> selectedTappeIds = new HashSet<>();
    AppRoomDatabase database;
    List<String> tappeEsistenti = new ArrayList<>(); // Per controllare le tappe esistenti
    FrameLayout mapOverlay;
    MapView expandedMapView;
    GoogleMap expandedMap;




    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tappe, container, false);

        // Inizializzazione dei componenti
        mappaTappe = rootView.findViewById(R.id.mappaTappe);
        tappeCardContainer = rootView.findViewById(R.id.tappeCardContainer);
        mapOverlay = rootView.findViewById(R.id.mapOverlay);
        expandedMapView = mapOverlay.findViewById(R.id.expandedMap);
        closeMapButton = mapOverlay.findViewById(R.id.closeMapButton);

        // Inizializzazione del database e DAO
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

        // Inizializzazione della mappa principale
        mappaTappe.onCreate(savedInstanceState);
        mappaTappe.getMapAsync(googleMap -> {
            this.googleMap = googleMap;
            LatLng posizioneIniziale = new LatLng(46.414382, 10.013988);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posizioneIniziale, 10));

            // Imposta il listener per le mappe
            googleMap.setOnMapClickListener(latLng -> {
                // Verifica che il listener venga chiamato
                Log.d("TappeFragment", "Mappa cliccata: " + latLng);
                mostraOverlayMappe(latLng); // Chiama il metodo per mostrare l'overlay
            }); // Usa il metodo giusto

            // Carica le tappe salvate dalla base dati
            caricaTappeSalvate();
        });

        // Listener per il bottone di chiusura della mappa ingrandita
        closeMapButton.setOnClickListener(v -> {
            mapOverlay.setVisibility(View.GONE);  // Nascondi l'overlay
        });

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null && immagineTappaPreview != null) {
                            immagineTappaPreview.setImageURI(selectedImageUri);
                            anteprimaImmagine.setVisibility(View.GONE);
                        } else {
                            anteprimaImmagine.setVisibility(View.VISIBLE);
                        }
                    }
                }
        );

        imagePickerLauncherModifica = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUriModifica = result.getData().getData();
                        if (selectedImageUriModifica != null) {
                            anteprimaImmagineModificata.setImageURI(selectedImageUriModifica);
                            testoAnteprimaImmagineModificata.setVisibility(View.GONE);
                        } else {
                            testoAnteprimaImmagineModificata.setVisibility(View.VISIBLE);
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
        listTappeCards = new ArrayList<>();

        overlay_add_tappa = inflater.inflate(R.layout.overlay_add_tappa, layoutTappe, false);
        layoutTappe.addView(overlay_add_tappa);
        overlay_add_tappa.setVisibility(View.GONE);



        // Inizializza i componenti di overlay_add_tappa
        addTappaBtn = view.findViewById(R.id.addTappaButton);
        editNome = view.findViewById(R.id.editNomeTappa);
        editData = view.findViewById(R.id.editDataTappa);
        salvaTappa = view.findViewById(R.id.salvaTappaButton);
        immagineTappaPreview = overlay_add_tappa.findViewById(R.id.anteprimaImmagine); // Aggiunto il riferimento alla preview dell'immagine
        tappeCardContainer = view.findViewById(R.id.tappeCardContainer);
        anteprimaImmagine = overlay_add_tappa.findViewById(R.id.testoAnteprimaImmagine);
        indice = 0;

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
            editNome.setError(null);
            editData.setError(null);
        });

        // Listener per il pulsante "Inserisci un'immagine"
        inputImmagine = view.findViewById(R.id.inserisciImmagineTappaButton);
        inputImmagine.setOnClickListener(v -> {
            Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(pickImageIntent);
            immagineTappaPreview.setVisibility(View.VISIBLE);
        });

        editData.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view1, selectedYear, selectedMonth, selectedDay) -> {
                String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                editData.setText(formattedDate);  // Imposta il formato corretto nella EditText
            }, year, month, day);

            datePickerDialog.show();
        });



        overlay_modifica_tappa = inflater.inflate(R.layout.overlay_modifica_tappa, layoutTappe, false);
        layoutTappe.addView(overlay_modifica_tappa);
        overlay_modifica_tappa.setVisibility(View.GONE);

        eliminaTappaButton = view.findViewById(R.id.eliminaTappa);
        modificaTappaButton = view.findViewById(R.id.modificaTappa);
        salvaModificaTappa = view.findViewById(R.id.salvaModificaTappaButton);
        backModificaTappa = view.findViewById(R.id.backModificaTappaButton);
        editModificaNome = view.findViewById(R.id.editModificaNomeTappa);
        inputModificaImmagine = view.findViewById(R.id.inserisciImmagineModificaTappaButton);
        anteprimaImmagineModificata = overlay_modifica_tappa.findViewById(R.id.anteprimaImmagineModificata);
        testoAnteprimaImmagineModificata = view.findViewById(R.id.testoAnteprimaImmagineModificata);
        editModificaData = view.findViewById(R.id.editModificaDataTappa);


        inputModificaImmagine.setOnClickListener(v -> {
            Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncherModifica.launch(pickImageIntent);
        });


        modificaTappaButton.setOnClickListener(v -> {
            MaterialCardView card = findSelectedCard(listTappeCards);
            overlay_modifica_tappa.setVisibility(View.VISIBLE);
            addTappaBtn.setEnabled(false);
            modificaTappaButton.setVisibility(View.GONE);
            eliminaTappaButton.setVisibility(View.GONE);

            TextView nomeTappaCard = card.findViewById(R.id.nomeTappaCard);
            editModificaNome.setText(nomeTappaCard.getText().toString());

            // TextView dataTappaCard = card.findViewById(R.id.dataTappa);
            // editModificaData.setText(dataTappaCard.getText().toString());

            ImageView immagineTappaCard = card.findViewById(R.id.anteprimaImmagine);
            if (immagineTappaCard.getDrawable() != null) {
                Bitmap bitmap = ((BitmapDrawable) immagineTappaCard.getDrawable()).getBitmap();
                anteprimaImmagineModificata.setImageBitmap(bitmap); // Usa il Bitmap per l'anteprima
                testoAnteprimaImmagineModificata.setVisibility(View.GONE);
            } else {
                anteprimaImmagineModificata.setImageDrawable(null);
                testoAnteprimaImmagineModificata.setVisibility(View.VISIBLE);
            }

        });


        salvaModificaTappa.setOnClickListener(v -> {
            MaterialCardView card = findSelectedCard(listTappeCards);

            TextView nomeTappaCard = card.findViewById(R.id.nomeTappaCard);
            inputModificaNome = editModificaNome.getText().toString();
            nomeTappaCard.setText(inputModificaNome);

            // String inputModificaData = editModificaData.getText().toString();
            // TextView dataTappaCard = card.findViewById(R.id.dataTappa);
            // dataTappaCard.setText(inputModificaData);


            ImageView immagineTappaCard = card.findViewById(R.id.anteprimaImmagine);
            if (selectedImageUriModifica != null) {
                immagineTappaCard.setImageURI(selectedImageUriModifica);
            } else if (anteprimaImmagineModificata.getDrawable() != null) {
                Bitmap bitmap = ((BitmapDrawable) anteprimaImmagineModificata.getDrawable()).getBitmap();
                immagineTappaCard.setImageBitmap(bitmap);
            } else {
                immagineTappaCard.setImageResource(getRandomImage()); // Immagine di default
            }


            overlay_modifica_tappa.setVisibility(View.GONE);
            card.setCardBackgroundColor(getResources().getColor(R.color.primary));
            card.setStrokeColor(getResources().getColor(R.color.primary));
            card.setSelected(false);
            selectedImageUriModifica = null;
            modificaTappaButton.setVisibility(View.GONE);
            eliminaTappaButton.setVisibility(View.GONE);
            addTappaBtn.setEnabled(true);
        });


        backModificaTappa.setOnClickListener(v -> {
            MaterialCardView card = findSelectedCard(listTappeCards);
            overlay_modifica_tappa.setVisibility(View.GONE);
            modificaTappaButton.setVisibility(View.GONE);
            eliminaTappaButton.setVisibility(View.GONE);
            addTappaBtn.setEnabled(true);
            card.setCardBackgroundColor(getResources().getColor(R.color.primary));
            card.setStrokeColor(getResources().getColor(R.color.primary));
            card.setSelected(false);
        });


        eliminaTappaButton.setOnClickListener(v -> {

            List<MaterialCardView> toRemove = new ArrayList<>();
            for (MaterialCardView m : listTappeCards) {
                if (m.isSelected()) {
                    toRemove.add(m);
                }
            }

            for (MaterialCardView m : toRemove) {
                tappeCardContainer.removeView(m);
                listTappeCards.remove(m);
                indice--;
            }

            modificaTappaButton.setVisibility(View.GONE);
            eliminaTappaButton.setVisibility(View.GONE);
            addTappaBtn.setEnabled(true);
        });

        overlay_mappa = getLayoutInflater().inflate(R.layout.overlay_add_tappa_mappa, layoutTappe, false);
        layoutTappe.addView(overlay_mappa);
        overlay_mappa.setVisibility(View.GONE);

        backTappa = view.findViewById(R.id.backTappaButton);
        backTappa.setOnClickListener(v -> layoutTappe.removeView(overlay_add_tappa));

        mappaTappe = view.findViewById(R.id.mappaTappe);

    }

    private void caricaTappeSalvate() {
        // Carica le tappe esistenti dal database in background
        new Thread(() -> {
            List<Tappa> tappe = tappaDao.getAllTappe();
            getActivity().runOnUiThread(() -> {
                if (googleMap != null) {
                    for (Tappa tappa : tappe) {
                        LatLng latLng = new LatLng(tappa.getLatitude(), tappa.getLongitude());
                        aggiungiTappaAllaMappa(tappa.getNome(), latLng); // Aggiungi il marker
                        aggiungiTappaAllaLista(tappa.getNome());
                    }

                    // Sposta la mappa sull'ultima tappa aggiunta (se ce ne sono)
                    if (!tappe.isEmpty()) {
                        Tappa ultimaTappa = tappe.get(tappe.size() - 1);
                        LatLng ultimaPosizione = new LatLng(ultimaTappa.getLatitude(), ultimaTappa.getLongitude());
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ultimaPosizione, 15));
                    }
                }
            });
        }).start();
    }


    private void searchLocation(String location) {
        Geocoder geocoder = new Geocoder(getContext());
        try {
            List<Address> addresses = geocoder.getFromLocationName(location, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                // Sposta la mappa sulla posizione trovata
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                // Aggiungi un marker alla posizione trovata
                googleMap.clear(); // Rimuovi tutti i marker precedenti
                googleMap.addMarker(new MarkerOptions().position(latLng).title(address.getFeatureName())
                        .snippet("Clicca per aggiungere la tappa"));

                // Aggiungi un listener per il click sul marker per aggiungere la tappa
                googleMap.setOnMarkerClickListener(marker -> {
                    mostraOverlaySalvataggioTappa(latLng); // Mostra l'overlay per aggiungere la tappa
                    return true;
                });
            } else {
                Toast.makeText(getContext(), "Luogo non trovato", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Errore nella ricerca del luogo", Toast.LENGTH_SHORT).show();
        }
    }


    private void mostraOverlaySalvataggioTappa(LatLng latLng) {
        // Rimuovi overlay esistente se presente
        if (overlay_mappa != null && layoutTappe.indexOfChild(overlay_mappa) != -1) {
            layoutTappe.removeView(overlay_mappa);
        }

        // Gonfia il layout dell'overlay
        overlay_mappa = getLayoutInflater().inflate(R.layout.overlay_add_tappa_mappa, layoutTappe, false);
        layoutTappe.addView(overlay_mappa);

        // Configura gli elementi dell'overlay
        EditText nomeTappaMappaEditText = overlay_mappa.findViewById(R.id.editNomeTappaMappa);
        EditText dataTappaEditText = overlay_mappa.findViewById(R.id.editDataTappaMappa);
        Button salvaTappaButton = overlay_mappa.findViewById(R.id.salvaTappaMappaButton);
        ImageButton backTappaButton = overlay_mappa.findViewById(R.id.backTappaMappaButton);

        // Logica per il salvataggio della tappa
        salvaTappaButton.setOnClickListener(v -> {
            String nomeTappa = nomeTappaMappaEditText.getText().toString();
            String dataTappa = dataTappaEditText.getText().toString();

            if (!nomeTappa.isEmpty()) {
                addTappa(nomeTappa, dataTappa, latLng);
                layoutTappe.removeView(overlay_mappa);
            } else {
                Toast.makeText(getContext(), "Nome della tappa vuoto", Toast.LENGTH_SHORT).show();
            }
        });

        // Indietro
        backTappaButton.setOnClickListener(v -> layoutTappe.removeView(overlay_mappa));
    }


    private void mostraOverlayMappe(LatLng latLng) {
        // Verifica che l'overlay venga mostrato
        Log.d("TappeFragment", "Mostra overlay mappa");

        // Mostra l'overlay
        mapOverlay.setVisibility(View.VISIBLE);  // Assicurati che la visibilità sia corretta

        // Inizializza la MapView all'interno dell'overlay
        expandedMapView.onCreate(null);
        expandedMapView.getMapAsync(googleMap -> {
            expandedMap = googleMap;
            LatLng posizioneIniziale = latLng != null ? latLng : new LatLng(46.414382, 10.013988);
            expandedMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posizioneIniziale, 10));

            // Aggiungi i marker delle tappe salvate sulla mappa ingrandita
            caricaTappeSalvateOnExpandedMap();
        });
    }


    private void caricaTappeSalvateOnExpandedMap() {
        // Carica le tappe esistenti sulla mappa ingrandita
        new Thread(() -> {
            List<Tappa> tappe = tappaDao.getAllTappe();
            getActivity().runOnUiThread(() -> {
                if (expandedMap != null) {
                    for (Tappa tappa : tappe) {
                        LatLng latLng = new LatLng(tappa.getLatitude(), tappa.getLongitude());
                        expandedMap.addMarker(new MarkerOptions().position(latLng).title(tappa.getNome()));
                    }

                    // Sposta la mappa sull'ultima tappa aggiunta (se ce ne sono)
                    if (!tappe.isEmpty()) {
                        Tappa ultimaTappa = tappe.get(tappe.size() - 1);
                        LatLng ultimaPosizione = new LatLng(ultimaTappa.getLatitude(), ultimaTappa.getLongitude());
                        expandedMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ultimaPosizione, 15));
                    }
                }
            });
        }).start();
    }





    private void addTappa(String nome, String data, LatLng latLng) {
        if (tappeEsistenti.contains(nome)) {
            Toast.makeText(getContext(), "Tappa già esistente", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crea una nuova tappa e salvala nel database
        Tappa nuovaTappa = new Tappa(nome, data, null, latLng.latitude, latLng.longitude);
        new Thread(() -> {
            tappaDao.insertTappa(nuovaTappa);

            // Dopo aver salvato, aggiorna la UI
            getActivity().runOnUiThread(() -> {
                tappeEsistenti.add(nome);
                aggiungiTappaAllaMappa(nome, latLng);
                aggiungiTappaAllaLista(nome);
            });
        }).start();
    }

    private void aggiungiTappaAllaMappa(String nome, LatLng latLng) {
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(nome));
    }

    private void aggiungiTappaAllaLista(String nome) {
        MaterialCardView cardView = (MaterialCardView) getLayoutInflater().inflate(R.layout.item_card_tappa, tappeCardContainer, false);
        TextView nomeTappaCard = cardView.findViewById(R.id.nomeTappaCard);
        nomeTappaCard.setText(nome);
        tappeCardContainer.addView(cardView);
    }

    // Metodi per gestire la mappa
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (googleMap != null) {
            LatLng posizioneIniziale = new LatLng(46.414382, 10.013988);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posizioneIniziale, 10));

            // Aggiungi listener per click sulla mappa
            googleMap.setOnMapClickListener(this::mostraOverlaySalvataggioTappa);
        } else {
            Log.e("MapError", "GoogleMap non inizializzata correttamente.");
        }
    }

    private MaterialCardView findSelectedCard(ArrayList<MaterialCardView> listTappeCards) {
        MaterialCardView selectedCard = listTappeCards.get(0);
        for (MaterialCardView m : listTappeCards) {
            if (m.isSelected())
                selectedCard = m;
        }
        return selectedCard;
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

    private Uri saveImageToCache(Bitmap bitmap) {
        File cachePath = new File(getContext().getCacheDir(), "images");
        cachePath.mkdirs(); // Assicurati che la directory esista

        // Usa un nome univoco per ogni immagine
        String uniqueName = "image_" + System.currentTimeMillis() + ".png";
        File file = new File(cachePath, uniqueName);

        try (FileOutputStream stream = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return FileProvider.getUriForFile(getContext(), "com.unimib.triptales.fileprovider", file);
    }

}
