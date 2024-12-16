package com.unimib.triptales.ui.login.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.util.Patterns;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.unimib.triptales.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginFragment extends Fragment {

    private TextInputEditText editTextEmail, editTextPassword;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //DA FINIRE
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);

        toolbar.setNavigationOnClickListener(v -> {
            // Navigate back to the SelectionFragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_login, new Selezione())
                    .addToBackStack(null)
                    .commit();
        });

        toolbar.inflateMenu(R.menu.menu_register);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_register) {
                navigateToSignInFragment();
                return true;
            }
            return false;
        });

        editTextEmail = view.findViewById(R.id.textInputEmail);
        editTextPassword = view.findViewById(R.id.textInputPassword);

        Button loginButton = view.findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            if (editTextEmail.getText() != null && isEmailOk(editTextEmail.getText().toString())) {
                if (editTextPassword.getText() != null && isPasswordOk(editTextPassword.getText().toString())) {
                    Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_homepageActivity);
                } else {
                    editTextPassword.setError(getString(R.string.error_password_login));
                }
            } else {
                editTextEmail.setError(getString(R.string.error_email_login));
            }
        });

        Button passwordDimenticata = view.findViewById(R.id.passwordDimenticata);

        passwordDimenticata.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_passwordDimenticataFragment));

    }

    private void navigateToSignInFragment() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.loginFragment, new SignInFragment())
                .addToBackStack(null)
                .commit();
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
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
}