package com.unimib.triptales.ui.settings.fragment;

import static com.unimib.triptales.util.Constants.INTERNET_ERROR;
import static com.unimib.triptales.util.Constants.INVALID_OLD_PASSWORD_ERROR;
import static com.unimib.triptales.util.Constants.PASSWORD_NOT_UPDATED_ERROR;
import static com.unimib.triptales.util.Constants.hideKeyboard;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.unimib.triptales.util.SharedPreferencesUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePasswordFragment extends Fragment {

    private EditText oldPasswordEditText, newPasswordEditText;
    private Button changePasswordButton;
    private UserViewModel userViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater
                .inflate(R.layout.fragment_change_password, container, false);

        IUserRepository userRepository = ServiceLocator.getINSTANCE().getUserRepository();
        userViewModel = new ViewModelProvider(requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        oldPasswordEditText = view.findViewById(R.id.oldPassword);
        newPasswordEditText = view.findViewById(R.id.newPassword);
        changePasswordButton = view.findViewById(R.id.changePassword);

        changePasswordButton.setOnClickListener(v -> {
            handleChangePassword();
            hideKeyboard(view,requireActivity());
        });

        return view;
    }

    private void handleChangePassword() {
        String oldPassword = oldPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(oldPassword)) {
            oldPasswordEditText.setError(getString(R.string.inserisci_vecchia_password));
            return;
        }
        if (TextUtils.isEmpty(newPassword)) {
            newPasswordEditText.setError(getString(R.string.inserisci_nuova_password));
            return;
        }

        if (!isPasswordOk(newPasswordEditText.getText().toString())) {
            newPasswordEditText.setError(getString(R.string.error_password_login));
            return;
        }

        changePasswordButton.setEnabled(false);

        String email = SharedPreferencesUtils.getLoggedUserEmail();
        userViewModel.updatePassword(email, oldPassword, newPassword);

        userViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if(error == null || error.isEmpty()){
                Toast.makeText(getContext(), getString(R.string.password_aggiornata), Toast.LENGTH_SHORT).show();
                resetFields();
            } else if(error.equals(PASSWORD_NOT_UPDATED_ERROR)){
                Toast.makeText(getContext(), getString(R.string.errore_aggiornamento_password), Toast.LENGTH_SHORT).show();
                changePasswordButton.setEnabled(true);
            } else if(error.equals(INVALID_OLD_PASSWORD_ERROR)){
                Toast.makeText(getContext(), getString(R.string.autenticazione_fallita), Toast.LENGTH_SHORT).show();
                changePasswordButton.setEnabled(true);
            } else if(error.equals(INTERNET_ERROR)){
                Toast.makeText(getContext(), getString(R.string.errore_internet), Toast.LENGTH_SHORT).show();
                changePasswordButton.setEnabled(true);
            }
        });

    }

    private boolean isPasswordOk(String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

    private void resetFields() {
        oldPasswordEditText.setText("");
        newPasswordEditText.setText("");
        changePasswordButton.setEnabled(true);
    }
}
