package com.unimib.triptales.ui.settings.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;    //aggiunto dopo come ultima riga in build.gradle.kts (:app)
import com.google.firebase.storage.UploadTask;

import com.unimib.triptales.R;

public class EditProfileFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private EditText newNameField, newSurnameField;
    private Button saveButton;
    private boolean isNameChanged = false;
    private boolean isSurnameChanged = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        newNameField = view.findViewById(R.id.editName);
        newSurnameField = view.findViewById(R.id.editSurname);
        saveButton = view.findViewById(R.id.editProfileButton);

        saveButton.setEnabled(false);


        newNameField.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                isNameChanged = true;
                enableSaveButton();
            }
            @Override
            public void afterTextChanged(android.text.Editable editable) {}
        });


        newSurnameField.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                isSurnameChanged = true;
                enableSaveButton();
            }
            @Override
            public void afterTextChanged(android.text.Editable editable) {}
        });


        saveButton.setOnClickListener(v -> {
            String newName = newNameField.getText().toString().trim();
            String newSurname = newSurnameField.getText().toString().trim();

            if (isNameChanged || isSurnameChanged) {
                saveButton.setEnabled(false);
                updateProfile(newName, newSurname);
            }
        });


        return view;
    }

    private void enableSaveButton() {
        saveButton.setEnabled(isNameChanged || isSurnameChanged);
    }

    private void updateProfile(String newName, String newSurname) {
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();
            DatabaseReference userRef = databaseReference.child(userId);

            if (isNameChanged) {
                userRef.child("name").setValue(newName);
            }

            if (isSurnameChanged) {
                userRef.child("surname").setValue(newSurname);
            }


            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    saveButton.setEnabled(true);
                    Toast.makeText(getContext(), getString(R.string.profilo_aggiornato), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    saveButton.setEnabled(true);
                    Toast.makeText(getContext(), getString(R.string.aggiornamento_fallito), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            saveButton.setEnabled(true);
            Toast.makeText(getContext(), getString(R.string.utente_non_autenticated), Toast.LENGTH_SHORT).show();
        }
    }




}
