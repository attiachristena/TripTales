package com.unimib.triptales.ui.settings.fragment;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unimib.triptales.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePasswordFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private EditText oldPasswordEditText, newPasswordEditText;
    private Button changePasswordButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        oldPasswordEditText = view.findViewById(R.id.oldPassword);
        newPasswordEditText = view.findViewById(R.id.newPassword);
        changePasswordButton = view.findViewById(R.id.changePassword);

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleChangePassword();
            }
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

        //if(oldPassword = firebaseUser.getPassword() )

        if (!isPasswordOk(newPasswordEditText.getText().toString())) {
            newPasswordEditText.setError(getString(R.string.error_password_login));
            return;
        }

        changePasswordButton.setEnabled(false);

        updatePassword(oldPassword, newPassword);
    }

    private void updatePassword(String oldPassword, final String newPassword) {
        AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), oldPassword);

        firebaseUser.reauthenticate(credential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        firebaseUser.updatePassword(newPassword)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(), getString(R.string.password_aggiornata), Toast.LENGTH_SHORT).show();
                                        resetFields();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), getString(R.string.errore_aggiornamento_password), Toast.LENGTH_LONG).show();
                                        changePasswordButton.setEnabled(true);
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), getString(R.string.autenticazione_fallita), Toast.LENGTH_LONG).show();
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
