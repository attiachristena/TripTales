package com.unimib.triptales.ui.diario.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.MapView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.unimib.triptales.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TappeFragment extends Fragment {

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




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tappe, container, false);
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
        public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState){
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
            mappaTappe = view.findViewById(R.id.mappaTappe);
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
                    editData.setText(formattedDate);
                }, year, month, day);

                datePickerDialog.show();
            });


            // Listener per il pulsante "Salva Tappa"
            salvaTappa.setOnClickListener(v -> {
                inputNome = editNome.getText().toString();
                inputData = editData.getText().toString();

                cardView = inflater.inflate(R.layout.item_card_tappa, tappeCardContainer, false);
                nomeTappaCard = cardView.findViewById(R.id.nomeTappaCard);
                immagineTappaCard = cardView.findViewById(R.id.anteprimaImmagine);

                nomeTappaCard.setText(inputNome);

                if (selectedImageUri != null) {
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

                MaterialCardView tappaCorrente = (MaterialCardView) tappeCardContainer.getChildAt(indice);
                listTappeCards.add(tappaCorrente);
                indice++;

                tappaCorrente.setOnLongClickListener(v1 -> {
                    if (!tappaCorrente.isSelected()) {
                        // Seleziona la card
                        tappaCorrente.setCardBackgroundColor(getResources().getColor(R.color.primary_light));
                        tappaCorrente.setStrokeColor(getResources().getColor(R.color.background_dark));
                        tappaCorrente.setSelected(true);
                    }

                    int selectedCount = 0;
                    for (MaterialCardView card : listTappeCards) {
                        if (card.isSelected()) {
                            selectedCount++;
                        }
                    }

                    if (selectedCount == 0) {
                        modificaTappaButton.setVisibility(View.GONE);
                        eliminaTappaButton.setVisibility(View.GONE);
                        addTappaBtn.setEnabled(true);
                    } else if (selectedCount == 1) {
                        modificaTappaButton.setVisibility(View.VISIBLE);
                        eliminaTappaButton.setVisibility(View.VISIBLE);
                        addTappaBtn.setEnabled(false);
                    } else {
                        modificaTappaButton.setVisibility(View.GONE);
                        eliminaTappaButton.setVisibility(View.VISIBLE);
                        addTappaBtn.setEnabled(false);
                    }

                    return true;
                });

                tappaCorrente.setOnClickListener(v1 -> {

                    int selectedCount = 0;
                    for (MaterialCardView card : listTappeCards) {
                        if (card.isSelected()) {
                            selectedCount++;
                        }
                    }

                    if (selectedCount == 0) {
                        return; // Esci senza fare nulla
                    }

                    if (tappaCorrente.isSelected()) {
                        // Deseleziona la card
                        tappaCorrente.setCardBackgroundColor(getResources().getColor(R.color.primary));
                        tappaCorrente.setStrokeColor(getResources().getColor(R.color.primary));
                        tappaCorrente.setSelected(false);
                    } else {
                        // Seleziona la card
                        tappaCorrente.setCardBackgroundColor(getResources().getColor(R.color.primary_light));
                        tappaCorrente.setStrokeColor(getResources().getColor(R.color.background_dark));
                        tappaCorrente.setSelected(true);
                    }

                    // Riconto le card selezionate per aggiornare lo stato dei bottoni
                    selectedCount = 0;
                    for (MaterialCardView card : listTappeCards) {
                        if (card.isSelected()) {
                            selectedCount++;
                        }
                    }

                    if (selectedCount == 0) {
                        modificaTappaButton.setVisibility(View.GONE);
                        eliminaTappaButton.setVisibility(View.GONE);
                        addTappaBtn.setEnabled(true);
                    } else if (selectedCount == 1) {
                        modificaTappaButton.setVisibility(View.VISIBLE);
                        eliminaTappaButton.setVisibility(View.VISIBLE);
                        addTappaBtn.setEnabled(false);
                    } else {
                        modificaTappaButton.setVisibility(View.GONE);
                        eliminaTappaButton.setVisibility(View.VISIBLE);
                        addTappaBtn.setEnabled(false);
                    }
                });


            });


            overlay_modifica_tappa = inflater.inflate(R.layout.overlay_modifica_tappa, layoutTappe, false);
            layoutTappe.addView(overlay_modifica_tappa);
            overlay_modifica_tappa.setVisibility(View.GONE);

            eliminaTappaButton = view.findViewById(R.id.eliminaTappa);
            modificaTappaButton = view.findViewById(R.id.modificaTappa);
            salvaModificaTappa = view.findViewById(R.id.salvaModificaTappaButton);
            backModificaTappa = view.findViewById(R.id.backModificaTappaButton);
            editModificaNome = view.findViewById(R.id.editModificaNomeTappa);
            editData = view.findViewById(R.id.editModificaDataTappa);
            inputModificaImmagine = view.findViewById(R.id.inserisciImmagineModificaTappaButton);
            anteprimaImmagineModificata = overlay_modifica_tappa.findViewById(R.id.anteprimaImmagineModificata);
            testoAnteprimaImmagineModificata = view.findViewById(R.id.testoAnteprimaImmagineModificata);

            inputModificaImmagine.setOnClickListener(v -> {
                Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                imagePickerLauncherModifica.launch(pickImageIntent);
            });


            modificaTappaButton.setOnClickListener(v -> {
                MaterialCardView card = findSelectedCard(listTappeCards);
                overlay_modifica_tappa.setVisibility(View.VISIBLE);
                addTappaBtn.setEnabled(false);

                TextView nomeTappaCard = card.findViewById(R.id.nomeTappaCard);
                editModificaNome.setText(nomeTappaCard.getText().toString());

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

                ImageView immagineTappaCard = card.findViewById(R.id.anteprimaImmagine);
                if (selectedImageUriModifica != null) {
                    immagineTappaCard.setImageURI(selectedImageUriModifica);
                } else if (anteprimaImmagineModificata.getDrawable() != null) {
                    Bitmap bitmap = ((BitmapDrawable) anteprimaImmagineModificata.getDrawable()).getBitmap();
                    immagineTappaCard.setImageBitmap(bitmap);
                } else {
                    immagineTappaCard.setImageResource(R.drawable.roma); // Immagine di default
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


            mappaTappe.setOnClickListener(v -> {
                Uri gmmIntentUri = Uri.parse("google.streetview:cbll=46.414382,10.013988");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            });
        }

    private MaterialCardView findSelectedCard(ArrayList<MaterialCardView> listTappeCards) {
        MaterialCardView selectedCard = listTappeCards.get(0);
        for (MaterialCardView m : listTappeCards) {
            if (m.isSelected())
                selectedCard = m;
        }
        return selectedCard;
    }

}
