package com.unimib.triptales.ui.diary.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
    ImageButton goBackEditCheckpoint;
    View overlay_edit_checkpoint;
    View overlay_dialog;
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
    List<String> tappeEsistenti = new ArrayList<>();
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
    Button yesAnswer;
    Button noAnswer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_checkpoints, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String diaryName = bundle.getString("diaryName");
            String startDate = bundle.getString("startDate");
            String endDate = bundle.getString("endDate");


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
                hideKeyboard();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        checkpointDiaryViewModel.getSearchedLocationWithName().observe(getViewLifecycleOwner(), pair -> {
            if (pair != null) {
                LatLng latLng = pair.first;
                String featureName = pair.second;

                moveMap(latLng);
                addMarkerOnMap(latLng, featureName);
                showOverlayDialogAdd();
            }
        });

        checkpointDiaryViewModel.getSearchError().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

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

        overlay_dialog = inflater.inflate(R.layout.overlay_dialog_add_checkpoint_diary, checkpointsLayout, false);
        checkpointsLayout.addView(overlay_dialog);
        overlay_dialog.setVisibility(View.GONE);

        deleteCheckpoint = view.findViewById(R.id.deleteCheckpointButton);
        editCheckpoint = view.findViewById(R.id.editCheckpointButton);
        saveEditCheckpoint = overlay_edit_checkpoint.findViewById(R.id.saveEditCheckpointButton);
        goBackEditCheckpoint = overlay_edit_checkpoint.findViewById(R.id.goBackArrowButtonEditCheckpoint);
        editNameCheckpoint = overlay_edit_checkpoint.findViewById(R.id.editNameCheckpoint);
        editDateCheckpoint = overlay_edit_checkpoint.findViewById(R.id.editDateCheckpoint);
        changeImageCheckpoint = overlay_edit_checkpoint.findViewById(R.id.changeImageButton);
        previewChangedImage = overlay_edit_checkpoint.findViewById(R.id.previewChangedImage);
        textPreviewChangeImage = overlay_edit_checkpoint.findViewById(R.id.textPreviewChangeImage);
        yesAnswer = overlay_dialog.findViewById(R.id.yesAnswer);
        noAnswer = overlay_dialog.findViewById(R.id.noAnswer);

        noAnswer.setOnClickListener(v -> {
            hideKeyboard();
            overlay_dialog.setVisibility(View.GONE);
            hideOverlayDialogAdd();
        });

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
        googleMap.addMarker(new MarkerOptions().position(latLng).title(title).snippet(getString(R.string.click_per_tappa)));
        yesAnswer.setOnClickListener(v -> {
            showSavingCheckpointOverlay(latLng);
            hideOverlayDialogAdd();
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

        checkpointDate.setOnClickListener(v -> showDatePickerDialog());


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


            if (!checkpointNameSave.isEmpty() && !checkpointDateSave.isEmpty()) {
                Uri imageUri = (selectedImageUri != null) ? saveImageToPublicStorage(selectedImageUri) :
                        Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.drawable.aereo);

                if (tappeEsistenti.contains(checkpointNameSave)) {
                    Toast.makeText(getContext(), getString(R.string.tappa_gia_esistente), Toast.LENGTH_SHORT).show();
                    return;
                }

                String diaryIdStr = SharedPreferencesUtils.getDiaryId(getContext());

                if (diaryIdStr != null) {
                    if (imageUri != null) {
                        checkpointDiaryViewModel.insertCheckpoint(checkpointNameSave, checkpointDateSave, imageUri, latLng, getContext());
                        checkpointsLayout.removeView(add_checkpoint);
                        checkpointsLayout.removeView(add_checkpoint);

                        hideKeyboard();

                        previewImage.setImageURI(null);
                        textPreviewImage.setVisibility(View.VISIBLE);
                        searchView.setVisibility(View.VISIBLE);
                        checkpointsMap.setVisibility(View.VISIBLE);
                        checkpointName.setText("");
                        checkpointDate.setText("");
                        immagineTappaPreview.setImageURI(null);

                        Snackbar.make(getContext(), checkpointsLayout, getString(R.string.successo_tappa),
                                Snackbar.LENGTH_SHORT).show();
                    }

                } else {
                    Snackbar.make(getContext(), checkpointsLayout, getString(R.string.errore_salvataggio_immagine),
                            Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(getContext(), checkpointsLayout, getString(R.string.compila_campi),
                        Snackbar.LENGTH_SHORT).show();
            }
        });

        // Pulsante per tornare indietro
        goBackArrow.setOnClickListener(v -> {
            hideKeyboard();
            checkpointsLayout.removeView(add_checkpoint);

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

        int checkpointId = checkpoint.getId();

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
            CheckpointDiary checkpointFromTag = (CheckpointDiary) cardView.getTag();
            Intent intent = new Intent(getContext(), CheckpointDiaryActivity.class);
            intent.putExtra("nomeTappa", nome);
            intent.putExtra("dataTappa", data);
            intent.putExtra("immagineTappaUri", immagine);
            intent.putExtra("checkpointDiaryId", checkpointId);
            if (checkpointId == -1) {
                Log.e("CheckpointDiaryActivity", "checkpointDiaryId non valido. ID: " + checkpointId);
            }
            startActivity(intent);
        });

        // Pulsante per tornare indietro
        goBackEditCheckpoint.setOnClickListener(v -> {
            hideKeyboard();
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

                editDateCheckpoint.setOnClickListener(v1 -> {
                    showDatePickerDialogEdit(editDateCheckpoint);
                });

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
                    imageUri = saveImageToPublicStorage(selectedImageUriModifica);
                } else {
                    // Altrimenti, mantiene l'immagine corrente
                    imageUri = Uri.parse(currentCheckpoint.getImmagineUri());
                }

                checkpointDiaryViewModel.updateCheckpointDiary(selectedCheckpoints.get(0).getId(), editName,
                        editDate, imageUri, getContext());

                checkpointDiaryViewModel.clearSelectedCheckpoints();
                hideKeyboard();
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

                for (MaterialCardView card : tappeSelezionate) {
                    checkpointContainer.removeView(card);
                }

                tappeSelezionate.clear();
                checkpointDiaryViewModel.clearSelectedCheckpoints();

                // Nasconde i pulsanti
                editCheckpoint.setVisibility(View.GONE);
                deleteCheckpoint.setVisibility(View.GONE);

                Snackbar.make(getContext(), checkpointsLayout, getString(R.string.tappe_selezionate_eliminate),
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


    // Metodo per caricare le tappe salvate nella mappa
    private void loadCheckpointsOnExpandedMap() {
        checkpointDiaryViewModel.getMapCheckpoints().observe(getViewLifecycleOwner(), tappe -> {
            if (tappe != null && !tappe.isEmpty()) {

                CheckpointDiary lastCheckpoint = tappe.get(tappe.size() - 1);
                LatLng lastPosition = new LatLng(lastCheckpoint.getLatitude(), lastCheckpoint.getLongitude());

                for (CheckpointDiary checkpoint : tappe) {
                    LatLng latLng = new LatLng(checkpoint.getLatitude(), checkpoint.getLongitude());
                    expandedMap.addMarker(new MarkerOptions().position(latLng).title(checkpoint.getNome()));
                }

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastPosition, 15));
            }
        });
    }


    // Metodo per caricare le tappe salvate nel dispositivo
    private void loadSavedCheckpoints(List<CheckpointDiary> checkpoints) {
        checkpointContainer.removeAllViews();

        String currentDiaryIdStr = SharedPreferencesUtils.getDiaryId(getContext());
        if (currentDiaryIdStr == null) {
            return;
        }

        List<CheckpointDiary> currentDiaryCheckpoints = checkpoints.stream()
                .filter(checkpoint -> checkpoint.getDiaryId().equals(currentDiaryIdStr))
                .collect(Collectors.toList());

        for (CheckpointDiary checkpoint : currentDiaryCheckpoints) {
            addCheckpointToList(checkpoint.getNome(), checkpoint.getData(), checkpoint.getImmagineUri(), checkpoint);
        }

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


    private Uri saveImageToPublicStorage(Uri sourceUri) {
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), sourceUri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "diary_" + System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/TripTales");

        Uri imageUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            if (imageUri != null) {
                OutputStream out = getContext().getContentResolver().openOutputStream(imageUri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.close();
                return imageUri;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkpointDiaryViewModel.loadCheckpoints(getContext());
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    checkpointDate.setText(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void showDatePickerDialogEdit(EditText dateEditText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    dateEditText.setText(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void showOverlayDialogAdd() {
        overlay_dialog.setVisibility(View.VISIBLE);

        checkpointsLayout.setClickable(false);
        checkpointsLayout.setFocusable(false);

        for (int i = 0; i < checkpointsLayout.getChildCount(); i++) {
            View child = checkpointsLayout.getChildAt(i);
            if (child != overlay_dialog) {
                child.setAlpha(0.5f);
                child.setClickable(false);
                child.setFocusable(false);
            }
        }
    }

    private void hideOverlayDialogAdd() {
        overlay_dialog.setVisibility(View.GONE);

        checkpointsLayout.setClickable(true);
        checkpointsLayout.setFocusable(true);

        for (int i = 0; i < checkpointsLayout.getChildCount(); i++) {
            View child = checkpointsLayout.getChildAt(i);
            if (child != overlay_dialog) {
                child.setAlpha(1f);
                child.setClickable(true);
                child.setFocusable(true);
            }
        }
    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                // Nascondi la tastiera
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}
