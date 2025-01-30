package com.unimib.triptales.ui.settings.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.unimib.triptales.R;

public class ChangeEmailFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private EditText oldEmailField, newEmailField;
    private Button changeEmailButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_email, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        oldEmailField = view.findViewById(R.id.oldEmail);
        newEmailField = view.findViewById(R.id.newEmail);
        changeEmailButton = view.findViewById(R.id.changeEmail);

        changeEmailButton.setEnabled(false);

        oldEmailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                checkEmailFields();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        newEmailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                checkEmailFields();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        if (firebaseUser != null) {
            String userEmail = firebaseUser.getEmail();
            if (!TextUtils.isEmpty(userEmail)) {
                Query query = databaseReference.orderByChild("email").equalTo(userEmail);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String email = "" + snapshot.child("email").getValue();
                            Toast.makeText(getContext(), "Email: " + email, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "User email is empty", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
        }

        changeEmailButton.setOnClickListener(v -> {
            String oldEmail = oldEmailField.getText().toString();
            String newEmail = newEmailField.getText().toString();

            if (!TextUtils.isEmpty(oldEmail) && !TextUtils.isEmpty(newEmail) && !oldEmail.equals(newEmail)) {
                Toast.makeText(getContext(), "Email changed successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Emails do not match or invalid", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void checkEmailFields() {
        String oldEmail = oldEmailField.getText().toString().trim();
        String newEmail = newEmailField.getText().toString().trim();

        if (!TextUtils.isEmpty(oldEmail) && !TextUtils.isEmpty(newEmail) && !oldEmail.equals(newEmail)) {
            changeEmailButton.setEnabled(true);
        } else {
            changeEmailButton.setEnabled(false);
        }
    }
}
