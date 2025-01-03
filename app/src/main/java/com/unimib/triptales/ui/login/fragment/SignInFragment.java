package com.unimib.triptales.ui.login.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Firebase;
import com.unimib.triptales.R;
import com.unimib.triptales.repository.user.IUserRepository;
import com.unimib.triptales.ui.login.viewmodel.UserViewModel;
import com.unimib.triptales.ui.login.viewmodel.UserViewModelFactory;
import com.unimib.triptales.util.ServiceLocator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInFragment extends Fragment {

    private UserViewModel userViewModel;
    private TextInputEditText editTextNome, editTextCognome, editTextEmail, editTextPassword;

    public SignInFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IUserRepository userRepository = ServiceLocator.getINSTANCE().getUserRepository();
        userViewModel = new ViewModelProvider(requireActivity(), new UserViewModelFactory(userRepository)).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextNome = view.findViewById(R.id.textInputNome);
        editTextCognome = view.findViewById(R.id.textInputCognome);
        editTextEmail = view.findViewById(R.id.textInputEmail);
        editTextPassword = view.findViewById(R.id.textInputPassword);

        Button signInButton = view.findViewById(R.id.signInButton);

        signInButton.setOnClickListener(v -> {
            boolean isValid = true;

            // Check if each field is filled and valid
            if (TextUtils.isEmpty(editTextNome.getText())) {
                editTextNome.setError("Compila con il tuo nome");
                isValid = false;
            }

            if (TextUtils.isEmpty(editTextCognome.getText())) {
                editTextCognome.setError("Compila con il tuo cognome");
                isValid = false;
            }

            if (TextUtils.isEmpty(editTextEmail.getText())) {
                editTextEmail.setError("Compila con la tua email");
                isValid = false;
            } else if (!isEmailOk(editTextEmail.getText().toString())) {
                editTextEmail.setError(getString(R.string.error_email_login));
                isValid = false;
            }

            if (TextUtils.isEmpty(editTextPassword.getText())) {
                editTextPassword.setError("Compila con la tua password");
                isValid = false;
            } else if (!isPasswordOk(editTextPassword.getText().toString())) {
                editTextPassword.setError(getString(R.string.error_password_login));
                isValid = false;
            }

            // Proceed only if all fields are valid
            if (isValid) {
                Navigation.findNavController(v).navigate(R.id.action_signInFragment_to_homepageActivity);
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

    private boolean isEmailOk(String email) {
        return (Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
}