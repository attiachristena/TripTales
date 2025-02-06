package com.unimib.triptales.ui.diary.fragment;

import static android.content.Intent.getIntent;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.unimib.triptales.R;
import com.unimib.triptales.model.CheckpointDiary;
import com.unimib.triptales.repository.checkpointDiary.ICheckpointDiaryRepository;
import com.unimib.triptales.ui.diary.CheckpointDiaryActivity;
import com.unimib.triptales.ui.diary.viewmodel.CheckpointDiaryViewModel;
import com.unimib.triptales.ui.diary.viewmodel.ViewModelFactory;
import com.unimib.triptales.util.ServiceLocator;
import com.unimib.triptales.util.SharedPreferencesUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class CheckpointsFragment extends Fragment implements OnMapReadyCallback {

    ConstraintLayout checkpointsLayout;
    LayoutInflater inflater;
    View add_checkpoint;
    LinearLayout checkpointContainer;
    ShapeableImageView immagineTappaPreview;
    ActivityResultLauncher<Intent> imagePickerLauncher;
    MapView checkpointsMap;
    FloatingActionButton deleteCheckpoint;
    FloatingActionButton editCheckpoint;
    View cardView;
    ImageView checkpointCardImage;
    int indice;
    ImageButton goBackEditCheckpoint;
    View overlay_edit_checkpoint;
    Button saveEditCheckpoint;
    EditText editNameCheckpoint;
    Button changeImageCheckpoint;
    ShapeableImageView previewChangedImage;
    ShapeableImageView previewImage;
    ActivityResultLauncher<Intent> imagePickerLauncherModifica;
    TextView textPreviewChangeImage;
    TextView textPreviewImage;
    EditText editDateCheckpoint;
    GoogleMap googleMap;
    ImageButton closeMapButton;
    List<String> tappeEsistenti = new ArrayList<>(); // Per controllare le tappe esistenti
    FrameLayout mapOverlay;
    MapView expandedMapView;
    GoogleMap expandedMap;
    Uri selectedImageUriMappa;
    List<MaterialCardView> tappeSelezionate = new ArrayList<>();
    EditText checkpointName;
    EditText checkpointDate;
    Button saveCheckpoint;
    ImageButton goBackArrow;
    Button addImage;
    Uri selectedImageUri;
    SearchView searchView;
    private CheckpointDiaryViewModel checkpointDiaryViewModel;
    Uri selectedImageUriMappaModifica;
    Uri selectedImageUriModifica;
    int checkpointId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tappe, container, false);

        // Recupera i dati passati dal Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            String diaryName = bundle.getString("diaryName");
            String startDate = bundle.getString("startDate");
            String endDate = bundle.getString("endDate");


            // Usa i dati per aggiornare la UI
            TextView diaryNameTextView = rootView.findViewById(R.id.textViewDiaryName);
            TextView datesTextView = rootView.findViewById(R.id.textViewDates);
            ImageView coverImageView = rootView.findViewById(R.id.imageView);

            diaryNameTextView.setText(diaryName);
            datesTextView.setText(startDate + " - " + endDate);
            coverImageView.setVisibility(View.VISIBLE);

            String coverImageUriString = String.valueOf(bundle.get("coverImageUri"));

            if (coverImageUriString != null) {
                Uri coverImageUri = Uri.parse(coverImageUriString);
                Log.d("CheckpointsFragment", "Cover Image URI: " + coverImageUri.toString());

                // Usa Glide o Picasso per caricare l'immagine
                Glide.with(requireContext())
                        .load(coverImageUri)
                        .into(coverImageView);
            } else {
                Log.d("CheckpointsFragment", "Cover Image URI is null");
            }


        }
        // Inizializza il ViewModel
        ICheckpointDiaryRepository checkpointDiaryRepository = ServiceLocator.getINSTANCE().getCheckpointDiaryRepository(getContext());
        checkpointDiaryViewModel = new ViewModelProvider(requireActivity(),
                new ViewModelFactory(checkpointDiaryRepository)).get(CheckpointDiaryViewModel.class);


        checkpointDiaryViewModel.getCheckpointDiaries().observe(getViewLifecycleOwner(), this::loadSavedCheckpoints);
        checkpointDiaryViewModel.loadCheckpoints(getContext());


        // Inizializza le viste
        checkpointsMap = rootView.findViewById(R.id.mappaTappe);
        checkpointContainer = rootView.findViewById(R.id.checkpointsCardContainer);
        mapOverlay = rootView.findViewById(R.id.mapOverlay);
        searchView = rootView.findViewById(R.id.searchView);
        expandedMapView = mapOverlay.findViewById(R.id.expandedMap);
        closeMapButton = mapOverlay.findViewById(R.id.closeMapButton);


        // Gestione della ricerca
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                checkpointDiaryViewModel.searchLocation(query, requireContext());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Dopo aver cercato una località, aggiorna la mappa e aggiunge il marker
        checkpointDiaryViewModel.getSearchedLocationWithName().observe(getViewLifecycleOwner(), pair -> {
            if (pair != null) {
                LatLng latLng = pair.first;
                String featureName = pair.second;

                moveMap(latLng);
                addMarkerOnMap(latLng, featureName);
            }
        });

        // Gestione errori nella ricerca
        checkpointDiaryViewModel.getSearchError().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Inizializzazione della mappa e gestione dei click su di essa
        checkpointsMap.onCreate(savedInstanceState);
        checkpointsMap.getMapAsync(googleMap -> {
            this.googleMap = googleMap;

            LatLng posizioneIniziale = new LatLng(46.414382, 10.013988);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posizioneIniziale, 8));

            checkpointDiaryViewModel.loadMapCheckpoints();

            googleMap.setOnMapClickListener(this::showMapOverlay);
        });

        closeMapButton.setOnClickListener(v -> {
            mapOverlay.setVisibility(View.GONE);
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


        checkpointsLayout = view.findViewById(R.id.checkpointsLayout);
        inflater = LayoutInflater.from(view.getContext());
        checkpointContainer = view.findViewById(R.id.checkpointsCardContainer);

        overlay_edit_checkpoint = inflater.inflate(R.layout.overlay_edit_checkpoint, checkpointsLayout, false);
        checkpointsLayout.addView(overlay_edit_checkpoint);
        overlay_edit_checkpoint.setVisibility(View.GONE);

        deleteCheckpoint = view.findViewById(R.id.deleteCheckpointButton);
        editCheckpoint = view.findViewById(R.id.editCheckpointButton);
        saveEditCheckpoint = overlay_edit_checkpoint.findViewById(R.id.saveEditCheckpointButton);
        goBackEditCheckpoint = overlay_edit_checkpoint.findViewById(R.id.goBackArrowButtonEditCheckpoint);
        editNameCheckpoint = overlay_edit_checkpoint.findViewById(R.id.editNameCheckpoint);
        editDateCheckpoint = overlay_edit_checkpoint.findViewById(R.id.editDateCheckpoint);
        changeImageCheckpoint = overlay_edit_checkpoint.findViewById(R.id.changeImageButton);
        previewChangedImage = overlay_edit_checkpoint.findViewById(R.id.previewChangedImage);
        textPreviewChangeImage = overlay_edit_checkpoint.findViewById(R.id.textPreviewChangeImage);

        add_checkpoint = getLayoutInflater().inflate(R.layout.overlay_add_checkpoint, checkpointsLayout, false);
        previewImage = add_checkpoint.findViewById(R.id.previewImage);
        textPreviewImage = add_checkpoint.findViewById(R.id.textPreviewImage);
        cardView = inflater.inflate(R.layout.item_card_checkpoint, checkpointContainer, false);
        checkpointCardImage = cardView.findViewById(R.id.checkpointCardImage);

        // Launcher per la selezione delle immagini
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();

                        if (selectedImageUri != null) {
                            selectedImageUriMappa = selectedImageUri;
                            immagineTappaPreview.setImageURI(selectedImageUriMappa);
                            textPreviewImage.setVisibility(View.GONE);
                        } else {
                            textPreviewImage.setVisibility(View.VISIBLE);
                        }

                    }
                }
        );

        // Launcher per la modifica delle immagini
        imagePickerLauncherModifica = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        selectedImageUriModifica = result.getData().getData();

                        if (selectedImageUriModifica != null) {
                            selectedImageUriMappaModifica = selectedImageUriModifica;
                            previewChangedImage.setImageURI(selectedImageUriMappaModifica);
                            textPreviewChangeImage.setVisibility(View.GONE);
                        } else {
                            textPreviewChangeImage.setVisibility(View.VISIBLE);
                        }

                    }
                }
        );
    }


    private void moveMap(LatLng latLng) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    private void addMarkerOnMap(LatLng latLng, String title) {
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(latLng).title(title).snippet("Clicca per aggiungere la tappa"));
        googleMap.setOnMarkerClickListener(marker -> {
            searchView.setVisibility(View.GONE);
            checkpointsMap.setVisibility(View.GONE);
            showSavingCheckpointOverlay(latLng);
            return true;
        });
    }

    private void showSavingCheckpointOverlay(LatLng latLng) {
        if (add_checkpoint != null && checkpointsLayout.indexOfChild(add_checkpoint) != -1) {
            checkpointsLayout.removeView(add_checkpoint);
        }

        checkpointsLayout.addView(add_checkpoint);

        checkpointName = add_checkpoint.findViewById(R.id.addCheckpointName);
        checkpointDate = add_checkpoint.findViewById(R.id.addCheckpointDate);
        saveCheckpoint = add_checkpoint.findViewById(R.id.saveCheckpointButton);
        goBackArrow = add_checkpoint.findViewById(R.id.goBackArrowButton);
        addImage = add_checkpoint.findViewById(R.id.addImageButton);
        checkpointCardImage = cardView.findViewById(R.id.checkpointCardImage);
        immagineTappaPreview = add_checkpoint.findViewById(R.id.previewImage);


        // Pulsante per aggiungere l'immagine
        addImage.setOnClickListener(v -> {
            Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            imagePickerLauncher.launch(pickImageIntent);
        });

        // Pulsante per salvare la tappa
        saveCheckpoint.setOnClickListener(v -> {
            String checkpointNameSave = checkpointName.getText().toString();
            String checkpointDateSave = checkpointDate.getText().toString();
            Uri selectedImageUri = selectedImageUriMappa;

            if (!checkpointNameSave.isEmpty()) {
                Uri imageUri = (selectedImageUri != null) ? saveImageToInternalStorage(selectedImageUri) :
                        Uri.parse("android.resource://" + getContext().getPackageName() + "/" + getRandomImage());

                if (tappeEsistenti.contains(checkpointNameSave)) {
                    Toast.makeText(getContext(), "Tappa già esistente", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(checkpointDiaryViewModel.isPosizioneGiàSalvata(latLng)){
                    Snackbar.make(getContext(), checkpointsLayout, "Questa posizione è già associata a una tappa",
                            Snackbar.LENGTH_SHORT).show();
                }

                if (!checkpointDiaryViewModel.isPosizioneGiàSalvata(latLng)) {

                    String diaryIdStr = SharedPreferencesUtils.getDiaryId(getContext());
                    if (diaryIdStr != null) {
                        int diaryId = Integer.parseInt(diaryIdStr);

                        if (imageUri != null) {
                            checkpointDiaryViewModel.insertCheckpoint(checkpointNameSave, checkpointDateSave, imageUri, latLng, getContext());
                            checkpointsLayout.removeView(add_checkpoint);
                            checkpointsLayout.removeView(add_checkpoint);

                            // Resetta i parametri
                            checkpointName.setText("");
                            checkpointDate.setText("");
                            selectedImageUriMappa = null;
                            previewImage.setImageURI(null);
                            textPreviewImage.setVisibility(View.VISIBLE);
                            searchView.setVisibility(View.VISIBLE);
                            checkpointsMap.setVisibility(View.VISIBLE);

                            Snackbar.make(getContext(), checkpointsLayout, "La tappa è stata aggiunta con successo",
                                    Snackbar.LENGTH_SHORT).show();
                        }

                    } else {
                        Snackbar.make(getContext(), checkpointsLayout, "Errore nel salvataggio dell'immagine",
                                Snackbar.LENGTH_SHORT).show();
                    }
                }
            } else {
                Snackbar.make(getContext(), checkpointsLayout, "Nome della tappa vuoto",
                        Snackbar.LENGTH_SHORT).show();
            }
        });

        // Pulsante per tornare indietro
        goBackArrow.setOnClickListener(v -> {
            checkpointsLayout.removeView(add_checkpoint);

            // Reset dei parametri
            checkpointName.setText("");
            checkpointDate.setText("");
            immagineTappaPreview.setImageURI(null);
            searchView.setVisibility(View.VISIBLE);
            checkpointsMap.setVisibility(View.VISIBLE);
        });
    }


    // Aggiunge una posizione alla mappa
    private void addCheckpointToMap(String nome, LatLng latLng) {
        googleMap.addMarker(new MarkerOptions().position(latLng).title(nome));
    }


    private void addCheckpointToList(String nome, String data, String immagine, CheckpointDiary checkpoint) {
        MaterialCardView cardView = (MaterialCardView) getLayoutInflater().inflate(R.layout.item_card_checkpoint, checkpointContainer, false);
        TextView checkpointNameCard = cardView.findViewById(R.id.checkpointNameCard);
        TextView checkpointDateCard = cardView.findViewById(R.id.checkpointDateCard);
        ImageView checkpointImageCard = cardView.findViewById(R.id.checkpointCardImage);

        cardView.setTag(checkpoint);
        checkpointNameCard.setText(nome);
        checkpointDateCard.setText(data);
        checkpointImageCard.setImageURI(Uri.parse(immagine));
        checkpointContainer.addView(cardView);

        cardView.setOnLongClickListener(v -> {
            CheckpointDiary checkpointFromTag = (CheckpointDiary) cardView.getTag();

            // Richiama il ViewModel per la selezione/deselezione la tappa
            checkpointDiaryViewModel.toggleCheckpointSelection(checkpointFromTag);

            // Verifica se la tappa è attualmente selezionata
            boolean isCurrentlySelected = checkpointDiaryViewModel.isCheckpointSelected(checkpointFromTag);

            if (isCurrentlySelected) {
                cardView.setCardBackgroundColor(getResources().getColor(R.color.background_dark));
                cardView.setStrokeColor(getResources().getColor(R.color.background_dark));
                tappeSelezionate.add(cardView);
            } else {
                cardView.setCardBackgroundColor(getResources().getColor(R.color.primary));
                cardView.setStrokeColor(getResources().getColor(R.color.primary));
                tappeSelezionate.remove(cardView);
            }

            // Aggiorna lo stato dei pulsanti
            checkpointDiaryViewModel.getSelectedCheckpoints().observe(this, selectedCheckpoints -> {
                updateButtonVisibility(selectedCheckpoints);
            });

            return true;
        });


        cardView.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CheckpointDiaryActivity.class);
            intent.putExtra("checkpointDiaryId", checkpointId);  // Usa l'ID che hai già
            intent.putExtra("nomeTappa", nome);
            intent.putExtra("dataTappa", data);
            intent.putExtra("immagineTappaUri", immagine);
            startActivity(intent);
        });

        // Pulsante per tornare indietro
        goBackEditCheckpoint.setOnClickListener(v -> {
            overlay_edit_checkpoint.setVisibility(View.GONE);
            editCheckpoint.setVisibility(View.GONE);
            deleteCheckpoint.setVisibility(View.GONE);

            for (MaterialCardView card : tappeSelezionate) {
                card.setCardBackgroundColor(getResources().getColor(R.color.primary));
                card.setStrokeColor(getResources().getColor(R.color.primary));
            }

            // Rimuove le tappe selezionate dalla lista
            tappeSelezionate.clear();
            checkpointDiaryViewModel.clearSelectedCheckpoints();

            // Reset dei parametri
            editNameCheckpoint.setText("");
            editDateCheckpoint.setText("");
            previewChangedImage.setImageURI(null);
            selectedImageUriModifica = null;
            searchView.setVisibility(View.VISIBLE);
            checkpointsMap.setVisibility(View.VISIBLE);
        });

        // Pulsante per modificare la tappa
        editCheckpoint.setOnClickListener(v -> {
            overlay_edit_checkpoint.setVisibility(View.VISIBLE);
            editCheckpoint.setVisibility(View.GONE);
            deleteCheckpoint.setVisibility(View.GONE);
            searchView.setVisibility(View.GONE);
            checkpointsMap.setVisibility(View.GONE);

            // Ottiene le tappe selezionate
            List<CheckpointDiary> selectedCheckpoints = checkpointDiaryViewModel.getSelectedCheckpoints().getValue();

            if (selectedCheckpoints != null && selectedCheckpoints.size() == 1) {
                CheckpointDiary selectedCheckpoint = selectedCheckpoints.get(0);

                editNameCheckpoint.setText(selectedCheckpoint.getNome());
                editDateCheckpoint.setText(selectedCheckpoint.getData());

                // Immagine associata alla tappa
                if (selectedCheckpoint.getImmagineUri() != null) {
                    previewChangedImage.setImageURI(Uri.parse(selectedCheckpoint.getImmagineUri()));
                    previewChangedImage.setVisibility(View.VISIBLE);
                    textPreviewChangeImage.setVisibility(View.GONE);
                }
            }
        });

        // Pulsante per cambiare l'immagine della tappa
        changeImageCheckpoint.setOnClickListener(v -> {
            Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            imagePickerLauncherModifica.launch(pickImageIntent);
        });

        // Pulsante per salvare le modifiche
        saveEditCheckpoint.setOnClickListener(v1 -> {
            String editName = editNameCheckpoint.getText().toString();
            String editDate = editDateCheckpoint.getText().toString();  // Campo per modificare la data

            // Raccoglie le tappe selezionate
            List<CheckpointDiary> selectedCheckpoints = checkpointDiaryViewModel.getSelectedCheckpoints().getValue();

            if (selectedCheckpoints != null && selectedCheckpoints.size() == 1) {
                CheckpointDiary currentCheckpoint = selectedCheckpoints.get(0);
                Uri imageUri;

                if (selectedImageUriModifica != null) {
                    // Se l'utente ha selezionato una nuova immagine, la salva
                    imageUri = saveImageToInternalStorage(selectedImageUriModifica);
                } else {
                    // Altrimenti, mantiene l'immagine corrente
                    imageUri = Uri.parse(currentCheckpoint.getImmagineUri());
                }

                checkpointDiaryViewModel.updateCheckpointDiary(selectedCheckpoints.get(0).getId(), editName,
                      editDate, imageUri, getContext());

                checkpointDiaryViewModel.clearSelectedCheckpoints();

                overlay_edit_checkpoint.setVisibility(View.GONE);
                editCheckpoint.setVisibility(View.GONE);
                deleteCheckpoint.setVisibility(View.GONE);
                searchView.setVisibility(View.VISIBLE);
                checkpointsMap.setVisibility(View.VISIBLE);

                // Resetta i parametri
                editNameCheckpoint.setText("");
                editDateCheckpoint.setText("");
                previewChangedImage.setImageURI(null);
                selectedImageUriModifica = null;
            }
        });

        // Pulsante per l'eliminazione delle tappe selezionate
        deleteCheckpoint.setOnClickListener(v -> {
            List<CheckpointDiary> selectedCheckpoints = checkpointDiaryViewModel.getSelectedCheckpoints().getValue();

            if (selectedCheckpoints != null && !selectedCheckpoints.isEmpty()) {
                checkpointDiaryViewModel.deleteSelectedCheckpoints(selectedCheckpoints, getContext());

                // Rimuove le card dal container
                for (MaterialCardView card : tappeSelezionate) {
                    checkpointContainer.removeView(card);
                }

                // Svuota la lista delle card selezionate
                tappeSelezionate.clear();
                checkpointDiaryViewModel.clearSelectedCheckpoints();

                // Nasconde i pulsanti
                editCheckpoint.setVisibility(View.GONE);
                deleteCheckpoint.setVisibility(View.GONE);

                Snackbar.make(getContext(), checkpointsLayout, "Tappe selezionate eliminate",
                        Snackbar.LENGTH_SHORT).show();
            }
        });
    }


    // Metodo per aggiornare lo stato dei pulsanti in base alle tappe selezionate
    private void updateButtonVisibility(List<CheckpointDiary> selectedCheckpoints) {
        if (selectedCheckpoints.isEmpty()) {
            editCheckpoint.setVisibility(View.GONE);
            deleteCheckpoint.setVisibility(View.GONE);
        } else if (selectedCheckpoints.size() == 1) {
            editCheckpoint.setVisibility(View.VISIBLE);
            deleteCheckpoint.setVisibility(View.VISIBLE);
        } else {
            editCheckpoint.setVisibility(View.GONE);
            deleteCheckpoint.setVisibility(View.VISIBLE);
        }
    }


    // Metodo per mostrare l'overlay della mappa
    private void showMapOverlay(LatLng latLng) {
        mapOverlay.setVisibility(View.VISIBLE);
        expandedMapView.onCreate(null);

        expandedMapView.getMapAsync(googleMap -> {
            expandedMap = googleMap;
            LatLng posizioneIniziale = latLng != null ? latLng : new LatLng(46.414382, 10.013988);
            expandedMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posizioneIniziale, 15));
            loadCheckpointsOnExpandedMap();
        });
    }

    private void loadCheckpointsOnExpandedMap() {
        checkpointDiaryViewModel.getMapCheckpoints().observe(getViewLifecycleOwner(), tappe -> {
            if (tappe != null && !tappe.isEmpty()) {

                CheckpointDiary lastCheckpoint = tappe.get(tappe.size() - 1);
                LatLng lastPosition = new LatLng(lastCheckpoint.getLatitude(), lastCheckpoint.getLongitude());

                // Aggiunge i marker alla mappa
                for (CheckpointDiary checkpoint : tappe) {
                    LatLng latLng = new LatLng(checkpoint.getLatitude(), checkpoint.getLongitude());
                    expandedMap.addMarker(new MarkerOptions().position(latLng).title(checkpoint.getNome()));
                }

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastPosition, 15));
            }
        });
    }


    private void loadSavedCheckpoints(List<CheckpointDiary> checkpoints) {
        checkpointContainer.removeAllViews();

        String currentDiaryIdStr = SharedPreferencesUtils.getDiaryId(getContext());
        if (currentDiaryIdStr == null) {
            return;
        }
        int currentDiaryId = Integer.parseInt(currentDiaryIdStr);

        List<CheckpointDiary> currentDiaryCheckpoints = checkpoints.stream()
                .filter(checkpoint -> checkpoint.getDiaryId() == currentDiaryId)
                .collect(Collectors.toList());

        for (CheckpointDiary checkpoint : currentDiaryCheckpoints) {
            addCheckpointToList(checkpoint.getNome(), checkpoint.getData(), checkpoint.getImmagineUri(), checkpoint);
        }

        // Aggiorna la mappa solo se è inizializzata
        if (googleMap != null) {
            googleMap.clear();
            for (CheckpointDiary checkpoint : currentDiaryCheckpoints) {
                LatLng location = new LatLng(checkpoint.getLatitude(), checkpoint.getLongitude());
                addCheckpointToMap(checkpoint.getNome(), location);
                googleMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(checkpoint.getNome()));
            }

            if (!currentDiaryCheckpoints.isEmpty()) {
                LatLng lastPosition = new LatLng(
                        currentDiaryCheckpoints.get(currentDiaryCheckpoints.size() - 1).getLatitude(),
                        currentDiaryCheckpoints.get(currentDiaryCheckpoints.size() - 1).getLongitude()
                );
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastPosition, 15));
            }
        }
    }


    // Metodo per inizializzare la mappa e mostrare l'overlay di salvataggio delle tappe
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        LatLng posizioneIniziale = new LatLng(46.414382, 10.013988);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posizioneIniziale, 10));
        googleMap.setOnMapClickListener(this::showSavingCheckpointOverlay);
    }


    // Metodo per salvare l'immagine nel dispositivo
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

    // Metodo per selezionare una immagine casuale
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