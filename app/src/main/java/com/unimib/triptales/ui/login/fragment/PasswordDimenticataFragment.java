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
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.unimib.triptales.R;
import com.unimib.triptales.repository.user.IUserRepository;
import com.unimib.triptales.ui.login.viewmodel.UserViewModel;
import com.unimib.triptales.ui.login.viewmodel.UserViewModelFactory;
import com.unimib.triptales.util.ServiceLocator;

public class PasswordDimenticataFragment extends Fragment {

    private TextInputEditText editTextEmail;
    private UserViewModel userViewModel;

    public PasswordDimenticataFragment() {
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
        return inflater.inflate(R.layout.fragment_password_dimenticata, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView backButton = view.findViewById(R.id.backarrow);
        backButton.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        editTextEmail = view.findViewById(R.id.textInputEmail);

        Button resetPasswordButton = view.findViewById(R.id.resetPasswordButton);
        resetPasswordButton.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString();
            if (TextUtils.isEmpty(email)) {
                editTextEmail.setError(getString(R.string.error_email_required));
            } else if (!isEmailOk(email)) {
                editTextEmail.setError(getString(R.string.error_email_invalid));
            } else {
                resetPasswordButton.setEnabled(false);
                userViewModel.resetPassword(email).observe(getViewLifecycleOwner(), result -> {
                    resetPasswordButton.setEnabled(true);
                    if(result.isSuccess()){
                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                getString(R.string.password_reset_email_sent), Snackbar.LENGTH_SHORT).show();
                        Navigation.findNavController(v).navigateUp();
                    } else {
                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                getString(R.string.error_unexpected), Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private boolean isEmailOk(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}