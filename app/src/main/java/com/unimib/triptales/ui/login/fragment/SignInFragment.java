package com.unimib.triptales.ui.login.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.unimib.triptales.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInFragment extends Fragment {

    private TextInputEditText editTextNome, editTextCognome, editTextEmail, editTextPassword;

    public SignInFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
            if (!TextUtils.isEmpty(editTextNome.getText())) {
                if (!TextUtils.isEmpty(editTextCognome.getText())) {
                    if (!TextUtils.isEmpty(editTextEmail.getText()) && isEmailOk(editTextEmail.getText().toString())) {
                        if (!TextUtils.isEmpty(editTextPassword.getText()) && isPasswordOk(editTextPassword.getText().toString())) {
                            Navigation.findNavController(v).navigate(R.id.action_signInFragment_to_homepageActivity);
                        } else {
                            editTextPassword.setError(getString(R.string.error_password_login));
                        }
                    } else {
                        editTextEmail.setError(getString(R.string.error_email_login));
                    }
                } else {
                    editTextCognome.setError("Compila con il tuo cognome");
                }
            } else {
                editTextNome.setError("Compila con il tuo nome");
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