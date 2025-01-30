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
            oldPasswordEditText.setError("Inserisci la vecchia password");
            return;
        }
        if (TextUtils.isEmpty(newPassword)) {
            newPasswordEditText.setError("Inserisci la nuova password");
            return;
        }
        if (newPassword.length() < 6) {
            newPasswordEditText.setError("La nuova password deve contenere almeno 6 caratteri");
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
                                        Toast.makeText(getContext(), "Password aggiornata con successo", Toast.LENGTH_SHORT).show();
                                        resetFields();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Errore durante l'aggiornamento della password: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        changePasswordButton.setEnabled(true);
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Autenticazione fallita: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        changePasswordButton.setEnabled(true);
                    }
                });
    }

    private void resetFields() {
        oldPasswordEditText.setText("");
        newPasswordEditText.setText("");
        changePasswordButton.setEnabled(true);
    }
}
