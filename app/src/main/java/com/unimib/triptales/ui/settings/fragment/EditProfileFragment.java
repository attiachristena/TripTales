package com.unimib.triptales.ui.settings.fragment;


import static com.unimib.triptales.util.Constants.INVALID_UPDATE;
import static com.unimib.triptales.util.Constants.hideKeyboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.unimib.triptales.R;
import com.unimib.triptales.repository.user.IUserRepository;
import com.unimib.triptales.ui.login.viewmodel.UserViewModel;
import com.unimib.triptales.ui.login.viewmodel.UserViewModelFactory;
import com.unimib.triptales.util.ServiceLocator;

public class EditProfileFragment extends Fragment {

    private EditText newNameField, newSurnameField;
    private Button saveButton;
    private boolean isNameChanged = false;
    private boolean isSurnameChanged = false;
    private UserViewModel userViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        IUserRepository userRepository = ServiceLocator.getINSTANCE().getUserRepository();
        userViewModel = new ViewModelProvider(requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

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
            hideKeyboard(view,requireActivity());

            if (isNameChanged || isSurnameChanged) {
                saveButton.setEnabled(false);
                userViewModel.updateProfile(newName, newSurname);
            }
        });

        userViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if(!newNameField.getText().toString().isEmpty()) {
                if (error == null || error.isEmpty()) {
                    saveButton.setEnabled(true);
                    Toast.makeText(getContext(), getString(R.string.profilo_aggiornato), Toast.LENGTH_SHORT).show();
                } else if (error.equals(INVALID_UPDATE)) {
                    saveButton.setEnabled(true);
                    Toast.makeText(getContext(), getString(R.string.aggiornamento_fallito), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void enableSaveButton() {
        saveButton.setEnabled(isNameChanged || isSurnameChanged);
    }

}
