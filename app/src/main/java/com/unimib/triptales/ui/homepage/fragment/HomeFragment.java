    package com.unimib.triptales.ui.homepage.fragment;

    import android.app.DatePickerDialog;
    import android.content.Intent;
    import android.net.Uri;
    import android.os.Bundle;
    import android.provider.MediaStore;
    import android.text.TextUtils;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageButton;
    import android.widget.ImageView;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.constraintlayout.widget.ConstraintLayout;
    import androidx.fragment.app.Fragment;
    import androidx.recyclerview.widget.GridLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import com.google.android.material.card.MaterialCardView;
    import com.google.android.material.floatingactionbutton.FloatingActionButton;
    import com.unimib.triptales.R;
    import com.unimib.triptales.adapters.Diary;
    import com.unimib.triptales.adapters.DiaryAdapter;

    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Calendar;
    import java.util.List;
    import java.util.Locale;

    public class HomeFragment extends Fragment implements DiaryAdapter.OnDiaryItemLongClickListener{

        private RecyclerView recyclerView;
        private DiaryAdapter diaryAdapter;
        private List<Diary> diaryList = new ArrayList<>();
        private TextView emptyMessage;
        private EditText inputDayStartDate, inputMonthStartDate, inputYearStartDate;
        private EditText inputDayEndDate, inputMonthEndDate, inputYearEndDate;
        private Button buttonChooseImage, buttonSave;
        private TextView inputDiaryName;
        private ImageView imageViewCover;
        private Uri selectedImageUri;
        private ConstraintLayout rootLayoutHome;
        private View overlayAddDiary;
        private final Calendar calendar = Calendar.getInstance();
        private FloatingActionButton deleteDiaryButton, modifyDiaryButton;
        private RecyclerView recyclerViewDiaries;
        private Diary selectedDiary;
        private Diary diary;
        private ArrayList<Diary> selectedDiaries = new ArrayList<>();
        private MaterialCardView cardView;


        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
            // Inflazione del layout principale del fragment
            View view = inflater.inflate(R.layout.fragment_home, container, false);

            // Inizializzazione dei componenti del layout
            rootLayoutHome = view.findViewById(R.id.root_layout_home);
            overlayAddDiary = inflater.inflate(R.layout.overlay_add_diary, rootLayoutHome, false);
            rootLayoutHome.addView(overlayAddDiary);
            overlayAddDiary.setVisibility(View.GONE);

            recyclerView = view.findViewById(R.id.recycler_view_diaries);
            emptyMessage = view.findViewById(R.id.text_empty_message);
            FloatingActionButton buttonAddDiary = view.findViewById(R.id.fab_add_diary);

            inputDiaryName = overlayAddDiary.findViewById(R.id.inputDiaryName);
            inputDayStartDate = overlayAddDiary.findViewById(R.id.inputDayDeparture);
            inputMonthStartDate = overlayAddDiary.findViewById(R.id.inputMonthDeparture);
            inputYearStartDate = overlayAddDiary.findViewById(R.id.inputYearDeparture);

            inputDayEndDate = overlayAddDiary.findViewById(R.id.inputReturnDay);
            inputMonthEndDate = overlayAddDiary.findViewById(R.id.inputReturnMonth);
            inputYearEndDate = overlayAddDiary.findViewById(R.id.inputReturnYear);

            buttonSave = overlayAddDiary.findViewById(R.id.buttonSaveDiary);
            buttonChooseImage = overlayAddDiary.findViewById(R.id.buttonChooseImage);
            imageViewCover = overlayAddDiary.findViewById(R.id.imageViewSelected);
            ImageButton closeOverlayButton = overlayAddDiary.findViewById(R.id.backaddDiaryButton);

            // Nascondere i bottoni di delete e modify quando si apre l'overlay
            deleteDiaryButton = view.findViewById(R.id.deleteDiaryButton);
            modifyDiaryButton = view.findViewById(R.id.modifyDiaryButton);
            deleteDiaryButton.setVisibility(View.GONE);  // Nascondi il bottone di eliminazione
            modifyDiaryButton.setVisibility(View.GONE);  // Nascondi il bottone di modifica


            // Impostazione del RecyclerView
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            diaryAdapter = new DiaryAdapter(getContext(), diaryList, this);  // Pass listener here
            recyclerView.setAdapter(diaryAdapter);

            // Listener per l'aggiunta di un nuovo diario
            buttonAddDiary.setOnClickListener(v -> {
                overlayAddDiary.setVisibility(View.VISIBLE);

                // Nascondi il bottone "Aggiungi diario" quando l'overlay è visibile
                buttonAddDiary.setVisibility(View.GONE);

                // Nascondi anche gli altri bottoni
                deleteDiaryButton.setVisibility(View.GONE);
                modifyDiaryButton.setVisibility(View.GONE);
            });

            closeOverlayButton.setOnClickListener(v -> {
                overlayAddDiary.setVisibility(View.GONE);

                // Deseleziona tutti i diari selezionati
                diaryAdapter.clearSelections();
                diaryAdapter.notifyDataSetChanged();

                // Svuota la lista dei diari selezionati
                selectedDiaries.clear();

                // Nascondi i bottoni "Modifica" e "Elimina"
                deleteDiaryButton.setVisibility(View.GONE);
                modifyDiaryButton.setVisibility(View.GONE);

                // Mostra il bottone "Aggiungi diario" quando l'overlay è chiuso
                buttonAddDiary.setVisibility(View.VISIBLE);
            });


            // Listener per i bottoni di salvataggio e scelta immagine
            buttonSave.setOnClickListener(v -> saveDiary());
            buttonChooseImage.setOnClickListener(v -> openImagePicker());

            // Impostazione dei date picker
            setupDatePicker(inputDayStartDate, inputMonthStartDate, inputYearStartDate);
            setupDatePicker(inputDayEndDate, inputMonthEndDate, inputYearEndDate);

            // Imposta il listener per il bottone di eliminazione
            deleteDiaryButton.setOnClickListener(v -> {
                deleteDiary();  // Chiama il metodo per eliminare il diario
                updateEmptyMessage();

            });

            modifyDiaryButton.setOnClickListener(v -> {
                if (!selectedDiaries.isEmpty() && selectedDiaries.size() == 1) {
                    selectedDiary = selectedDiaries.get(0); // Assegna il diario selezionato per la modifica

                    inputDiaryName.setText(selectedDiary.getName());

                    String[] startDateParts = selectedDiary.getStartDate().split("/");
                    inputDayStartDate.setText(startDateParts[0]);
                    inputMonthStartDate.setText(startDateParts[1]);
                    inputYearStartDate.setText(startDateParts[2]);

                    String[] endDateParts = selectedDiary.getEndDate().split("/");
                    inputDayEndDate.setText(endDateParts[0]);
                    inputMonthEndDate.setText(endDateParts[1]);
                    inputYearEndDate.setText(endDateParts[2]);

                    imageViewCover.setImageURI(selectedDiary.getCoverImageUri());
                    imageViewCover.setVisibility(View.VISIBLE);

                    overlayAddDiary.setVisibility(View.VISIBLE);

                    FloatingActionButton floatingActionButton = getView().findViewById(R.id.fab_add_diary);
                    buttonAddDiary.setVisibility(View.GONE);

                    deleteDiaryButton.setVisibility(View.GONE);
                    modifyDiaryButton.setVisibility(View.GONE);
                }
            });



            return view;
        }

        private void setupDatePicker(EditText dayField, EditText monthField, EditText yearField) {
            View.OnClickListener listener = v -> {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        requireContext(),
                        (view, year, month, dayOfMonth) -> {
                            dayField.setText(String.format(Locale.getDefault(), "%02d", dayOfMonth));
                            monthField.setText(String.format(Locale.getDefault(), "%02d", month + 1));
                            yearField.setText(String.format(Locale.getDefault(), "%04d", year));
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show();
            };
            dayField.setOnClickListener(listener);
            monthField.setOnClickListener(listener);
            yearField.setOnClickListener(listener);
        }

        private void openImagePicker() {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        }


        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1 && resultCode == getActivity().RESULT_OK && data != null) {
                selectedImageUri = data.getData();
                imageViewCover.setImageURI(selectedImageUri);
                imageViewCover.setVisibility(View.VISIBLE);  // Rendere visibile l'immagine selezionata
            }
        }


        private void saveDiary() {
            String dayStartDate = inputDayStartDate.getText().toString().trim();
            String monthStartDate = inputMonthStartDate.getText().toString().trim();
            String yearStartDate = inputYearStartDate.getText().toString().trim();
            String dayEndDate = inputDayEndDate.getText().toString().trim();
            String monthEndDate = inputMonthEndDate.getText().toString().trim();
            String yearEndDate = inputYearEndDate.getText().toString().trim();
            String diaryName = inputDiaryName.getText().toString().trim();

            if (TextUtils.isEmpty(dayStartDate) || TextUtils.isEmpty(monthStartDate) || TextUtils.isEmpty(yearStartDate) ||
                    TextUtils.isEmpty(dayEndDate) || TextUtils.isEmpty(monthEndDate) || TextUtils.isEmpty(yearEndDate)) {
                Toast.makeText(getContext(), "Le date non possono essere vuote!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidDay(dayStartDate) || !isValidDay(dayEndDate) ||
                    !isValidMonth(monthStartDate) || !isValidMonth(monthEndDate) ||
                    !isValidYear(yearStartDate) || !isValidYear(yearEndDate)) {
                Toast.makeText(getContext(), "Date non valide!", Toast.LENGTH_SHORT).show();
                return;
            }

            String startDate = dayStartDate + "/" + monthStartDate + "/" + yearStartDate;
            String endDate = dayEndDate + "/" + monthEndDate + "/" + yearEndDate;

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            try {
                if (sdf.parse(startDate).after(sdf.parse(endDate))) {
                    Toast.makeText(getContext(), "La data di partenza deve precedere quella di ritorno!", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Errore nel formato delle date!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedImageUri == null) {
                Toast.makeText(getContext(), "Seleziona un'immagine per la copertina!", Toast.LENGTH_SHORT).show();
                return;
            }

            // If we are editing an existing diary, update it; else create a new diary.
            if (selectedDiary != null) {
                // Modify the existing diary
                selectedDiary.setName(diaryName);
                selectedDiary.setStartDate(startDate);
                selectedDiary.setEndDate(endDate);
                selectedDiary.setCoverImageUri(selectedImageUri);
                Toast.makeText(getContext(), "Diario modificato con successo!", Toast.LENGTH_SHORT).show();
                selectedDiary = null; // Reset the selected diary after modification
            } else {
                // Create a new diary
                diaryList.add(new Diary(diaryName, startDate, endDate, selectedImageUri));
                Toast.makeText(getContext(), "Diario salvato con successo!", Toast.LENGTH_SHORT).show();
            }

            // Notify the adapter that the data has changed and update the view
            diaryAdapter.notifyDataSetChanged();
            diaryAdapter.clearSelections();

            // Reset the selection state and update buttons
            selectedDiaries.clear();
            deleteDiaryButton.setVisibility(View.GONE);
            modifyDiaryButton.setVisibility(View.GONE);

            // Hide the overlay and reset the form fields
            overlayAddDiary.setVisibility(View.GONE);
            resetDiaryFields();
            updateEmptyMessage();

            // Show the "Add Diary" button again
            FloatingActionButton buttonAddDiary = getView().findViewById(R.id.fab_add_diary);
            buttonAddDiary.setVisibility(View.VISIBLE);
        }




        private void resetDiaryFields() {
            inputDiaryName.setText("");
            inputDayStartDate.setText("");
            inputMonthStartDate.setText("");
            inputYearStartDate.setText("");
            inputDayEndDate.setText("");
            inputMonthEndDate.setText("");
            inputYearEndDate.setText("");
            imageViewCover.setVisibility(View.GONE);  // Nascondi l'immagine selezionata
        }


        private boolean isValidDay(String day) {
            try {
                int dayInt = Integer.parseInt(day);
                return dayInt >= 1 && dayInt <= 31;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        private boolean isValidMonth(String month) {
            try {
                int monthInt = Integer.parseInt(month);
                return monthInt >= 1 && monthInt <= 12;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        private boolean isValidYear(String year) {
            try {
                int yearInt = Integer.parseInt(year);
                return yearInt >= 1985;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        private void updateEmptyMessage() {
            if (diaryList.isEmpty()) {
                emptyMessage.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyMessage.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }

        private void deleteDiary() {
            // Ottieni la lista dei diari selezionati dal DiaryAdapter
            List<Diary> selectedDiaries = diaryAdapter.getSelectedDiaries();

            // Verifica se ci sono diari selezionati
            if (!selectedDiaries.isEmpty()) {
                // Rimuovi i diari selezionati dalla lista
                diaryList.removeAll(selectedDiaries);

                // Notifica l'adapter per aggiornare la visualizzazione
                diaryAdapter.notifyDataSetChanged();

                // Nascondi i bottoni di eliminazione e modifica
                deleteDiaryButton.setVisibility(View.GONE);
                modifyDiaryButton.setVisibility(View.GONE);

                // Mostra un messaggio di successo
                Toast.makeText(getContext(), "Diari eliminati con successo!", Toast.LENGTH_SHORT).show();

                // Aggiorna il messaggio per caso di lista vuota
                updateEmptyMessage();
            } else {
                Toast.makeText(getContext(), "Nessun diario selezionato!", Toast.LENGTH_SHORT).show();
            }
        }


        private void modifyDiary() {
            // Handle the modification of the diary (e.g., show an edit form)
            if (selectedDiary != null) {
                Toast.makeText(getContext(), "Modifica il diario", Toast.LENGTH_SHORT).show();
            }
        }
        public void onDiaryDeletedOrModified() {
            // Deseleziona tutti i diari
            diaryAdapter.clearSelections();

            // Notifica l'adapter per aggiornare la lista
            diaryAdapter.notifyDataSetChanged();

            // Nascondi il bottone "Modifica" e aggiorna la visibilità del bottone "Elimina"
            modifyDiaryButton.setVisibility(View.VISIBLE); // Rendi visibile "Modifica"
            deleteDiaryButton.setVisibility(View.GONE); // Nascondi "Elimina" se nessun diario è selezionato
        }


        public void onDiaryItemLongClicked(Diary diary) {

            if (selectedDiaries.contains(diary)) {
                selectedDiaries.remove(diary);
            } else {
                selectedDiaries.add(diary);
            }

            // Controlla il numero di diari selezionati
            if (selectedDiaries.size() > 1) {
                modifyDiaryButton.setVisibility(View.GONE); // Nascondi il bottone "Modifica"
            } else {
                modifyDiaryButton.setVisibility(View.VISIBLE); // Mostra il bottone "Modifica"
            }

            // Rende visibili o nasconde gli altri bottoni (elimina, ecc.)
            deleteDiaryButton.setVisibility(selectedDiaries.isEmpty() ? View.GONE : View.VISIBLE);
        }

    }
