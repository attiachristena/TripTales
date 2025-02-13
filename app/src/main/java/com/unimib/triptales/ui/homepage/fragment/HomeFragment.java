package com.unimib.triptales.ui.homepage.fragment;

import static com.unimib.triptales.util.Constants.ADDED;

import static com.unimib.triptales.util.Constants.DELETED;
import static com.unimib.triptales.util.Constants.INVALID_DELETE;
import static com.unimib.triptales.util.Constants.UPDATED;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import com.unimib.triptales.ui.homepage.overlay.OverlayAddEditDiary;



public class HomeFragment extends Fragment {

    private DiaryRecyclerAdapter diaryRecyclerAdapter;
    private TextView emptyMessage;
    private FloatingActionButton addDiaryButton, deleteDiaryButton, modifyDiaryButton;
    private OverlayAddEditDiary overlayAddEditDiary;
    private ImageView imageViewCover;
    private String selectedImageUri;
    private HomeViewModel homeViewModel;
    private boolean bEdit;
    private boolean bAdd;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        IDiaryRepository diaryRepository = ServiceLocator.getINSTANCE().getDiaryRepository(getContext());
        homeViewModel = new ViewModelProvider(requireActivity(),
                new ViewModelFactory(diaryRepository, requireActivity().getApplication())).get(HomeViewModel.class);
        ConstraintLayout rootLayoutHome = requireActivity().findViewById(R.id.rootLayoutHome);
        overlayAddEditDiary = new OverlayAddEditDiary(rootLayoutHome, getContext(), this);

        homeViewModel.loadDiaries();
        homeViewModel.deselectAllDiaries();
        initializeViews(view, inflater);
        List<Diary> diaries = homeViewModel.getDiariesLiveData().getValue();
        if(diaries != null) {
            for (Diary diary : diaries) {
                String imageUriString = diary.getCoverImageUri();
                if (imageUriString != null && !imageUriString.isEmpty()) {
                    view.post(() -> {
                        if (imageViewCover != null) {
                            Glide.with(requireContext())
                                    .load(imageUriString)
                                    .into(imageViewCover);
                        }
                    });

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

                            // Passa l'immagine all'overlay
                            if (overlayAddEditDiary != null) {
                                overlayAddEditDiary.updateImageView(newUri);
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
        List<Diary> diariesList = homeViewModel.getDiariesLiveData().getValue();
        diaryRecyclerAdapter.setDiaries(diariesList);
        final int maxObservations = 20;
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

        addDiaryButton.setOnClickListener(v -> {
            bAdd = true;
            overlayAddEditDiary.showOverlay(homeViewModel, true, false);
        });


// Gestione dell'overlay per aggiungere o modificare un diario
        homeViewModel.getDiaryOverlayVisibility().observe(getViewLifecycleOwner(), visible -> {
            if (visible) {
                addDiaryButton.setVisibility(View.GONE);
                if (bAdd) {
                    overlayAddEditDiary.showOverlay(homeViewModel, true, false);
                } else if (bEdit) {
                    overlayAddEditDiary.showOverlay(homeViewModel, false, true);
                    modifyDiaryButton.setVisibility(View.GONE);
                    deleteDiaryButton.setVisibility(View.GONE);
                }
            } else {
                Constants.hideKeyboard(view, requireActivity());
                addDiaryButton.setVisibility(View.VISIBLE);
                overlayAddEditDiary.hideOverlay();
            }
        });


        homeViewModel.getSelectedDiariesLiveData().observe(getViewLifecycleOwner(), selectedDiaries -> {
            if (selectedDiaries != null) {
                if (selectedDiaries.size() == 1) {
                    addDiaryButton.setEnabled(false);
                    modifyDiaryButton.setVisibility(View.VISIBLE);
                    deleteDiaryButton.setVisibility(View.VISIBLE);
                } else if (selectedDiaries.size() >= 2) {
                    addDiaryButton.setEnabled(false);
                    modifyDiaryButton.setVisibility(View.GONE);
                    deleteDiaryButton.setVisibility(View.VISIBLE);
                } else { // Nessun diario selezionato
                    modifyDiaryButton.setVisibility(View.GONE);
                    deleteDiaryButton.setVisibility(View.GONE);
                    addDiaryButton.setEnabled(true);
                }
            }
        });


        deleteDiaryButton.setOnClickListener(deleteDiaryButtonListener ->
                homeViewModel.deleteSelectedDiaries());

        modifyDiaryButton.setOnClickListener(v -> {
            bEdit = true;
            overlayAddEditDiary.showOverlay(homeViewModel, false, true);
        });

        SearchView searchView = view.findViewById(R.id.search_view_diaries);
        

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterDiariesByCountry(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterDiariesByCountry(newText);
                return false;
            }
        });
    }


    private void initializeViews(View view, LayoutInflater inflater) {
        initializeButtons(view);
    }

    private void initializeButtons(View view) {
        addDiaryButton = view.findViewById(R.id.fab_add_diary);
        deleteDiaryButton = view.findViewById(R.id.deleteDiaryButton);
        modifyDiaryButton = view.findViewById(R.id.modifyDiaryButton);
    }


    public void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permesso concesso! Caricamento immagini...", Toast.LENGTH_SHORT).show();
                homeViewModel.loadDiaries();
            } else {
                Toast.makeText(getContext(), "Permesso negato. Le immagini non saranno visibili.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 100);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
        }
    }

    private void filterDiariesByCountry(String countryQuery) {
        List<Diary> allDiaries = homeViewModel.getDiariesLiveData().getValue();
        if (allDiaries != null) {
            List<Diary> filteredDiaries = new ArrayList<>();

            for (Diary diary : allDiaries) {
                String diaryCountry = diary.getCountry();

                if (diaryCountry != null && countryQuery != null &&
                        diaryCountry.toLowerCase().startsWith(countryQuery.toLowerCase())) {
                    filteredDiaries.add(diary);
                }
            }

            diaryRecyclerAdapter.setDiaries(filteredDiaries);
        }
    }

    private Uri saveImageToPublicStorage(Uri sourceUri) {
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(),
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

        Uri imageUri = requireContext().getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            if (imageUri != null) {
                OutputStream out = requireContext().getContentResolver().openOutputStream(imageUri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.close();
                return imageUri; // Restituisce l'URI pubblico
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
