package com.unimib.triptales.source.user;

import static com.unimib.triptales.util.Constants.*;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unimib.triptales.R;
import com.unimib.triptales.model.Result;
import com.unimib.triptales.model.User;

import java.util.List;

public class UserAuthenticationFirebaseDataSource extends BaseUserAuthenticationRemoteDataSource{

    private final FirebaseAuth firebaseAuth;
    private final FirebaseDatabase firebaseDatabase;
    private final FirebaseUser firebaseUser;

    public UserAuthenticationFirebaseDataSource(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    @Override
    public User getLoggedUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            return null;
        } else {
            DatabaseReference userRef = firebaseDatabase.getReference("users").child(firebaseUser.getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        userResponseCallback.onSuccessGetLoggedUser(user);
                    } else {
                        userResponseCallback.onFailureGetLoggedUser(UNEXPECTED_ERROR);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    userResponseCallback.onFailureGetLoggedUser(UNEXPECTED_ERROR);
                }
            });
            return null;
        }
    }

    @Override
    public void logout() {
        if(firebaseUser != null) {
            String uid = firebaseUser.getUid();
            DatabaseReference userRef = firebaseDatabase.getReference("users").child(uid);
            userRef.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    firebaseUser.delete().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            userResponseCallback.onSuccessLogout();
                            firebaseAuth.signOut();
                        } else {
                            userResponseCallback.onFailureFromAuthentication("Error deleting user account");
                        }
                    });
                } else {
                    userResponseCallback.onFailureFromAuthentication("Error deleting user data from Firestore");
                }
            });
        } else {
            userResponseCallback.onFailureFromAuthentication("No user is currently signed in");
        }
    }

    @Override
    public void signUp(String name, String surname, String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null){
                    User user = new User(name, surname, email, firebaseUser.getUid());
                    DatabaseReference userRef = firebaseDatabase.getReference("users").child(firebaseUser.getUid());
                    userRef.setValue(user).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            userResponseCallback.onSuccessFromAuthentication(user);
                        } else {
                            userResponseCallback.onFailureFromAuthentication(UNEXPECTED_ERROR);
                        }
                    });
                } else {
                    userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                }
            } else {
                userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
            }
        });
    }

    private String getErrorMessage(Exception exception) {
        if (exception instanceof FirebaseAuthWeakPasswordException)
            return WEAK_PASSWORD_ERROR;
        else if (exception instanceof FirebaseAuthInvalidCredentialsException)
            return INVALID_CREDENTIALS_ERROR;
        else if (exception instanceof FirebaseAuthInvalidUserException)
            return INVALID_USER_ERROR;
        else if (exception instanceof FirebaseAuthUserCollisionException)
            return USER_COLLISION_ERROR;
        return UNEXPECTED_ERROR;
    }

    @Override
    public void signUpWithGoogle(String idToken) {
        if (idToken != null) {
            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
            firebaseAuth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                String name = firebaseUser.getDisplayName();
                                String email = firebaseUser.getEmail();
                                String surname = "";

                                DatabaseReference userRef = firebaseDatabase.getReference("users").child(firebaseUser.getUid());
                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            userResponseCallback.onFailureFromAuthentication(USER_ALREADY_EXISTS);
                                        } else {
                                            User user = new User(name, surname, email, firebaseUser.getUid());
                                            userRef.setValue(user).addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    userResponseCallback.onSuccessFromAuthentication(user);
                                                } else {
                                                    userResponseCallback.onFailureFromAuthentication(UNEXPECTED_ERROR);
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        userResponseCallback.onFailureFromAuthentication(getErrorMessage(databaseError.toException()));
                                    }

                                });
                            } else {
                                userResponseCallback.onFailureFromAuthentication(UNEXPECTED_ERROR);
                            }
                        } else {
                            userResponseCallback.onFailureFromAuthentication(UNEXPECTED_ERROR);
                        }
                    });
        }
    }

    @Override
    public MutableLiveData<Result> resetPassword(String email) {
        MutableLiveData<Result> resultLiveData = new MutableLiveData<>();

        firebaseAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        List<String> signInMethods = task.getResult().getSignInMethods();
                        if(signInMethods == null || signInMethods.isEmpty()) {
                            resultLiveData.postValue(new Result.Error("Nessun account trovato per questa email"));
                        } else {
                            firebaseAuth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener(resetTask -> {
                                        if (resetTask.isSuccessful()){
                                            resultLiveData.postValue(new Result.GenericSuccess());
                                        } else {
                                            resultLiveData.postValue(new Result.Error(resetTask.getException().getMessage()));
                                        }
                                    });
                        }
                    }
                });
        return resultLiveData;
    }

    @Override
    public void updatePassword(String email, String oldPassword, String newPassword) {
        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);

        firebaseUser.reauthenticate(credential)
                .addOnSuccessListener(aVoid -> firebaseUser.updatePassword(newPassword)
                        .addOnSuccessListener(aVoid1 ->
                                userResponseCallback.onSuccessFromUpdateData())
                        .addOnFailureListener(e ->
                                userResponseCallback.onFailureFromUpdateData(PASSWORD_NOT_UPDATED_ERROR)))
                .addOnFailureListener(e -> {
                            if(e instanceof FirebaseNetworkException){
                                userResponseCallback.onFailureFromUpdateData(INTERNET_ERROR);
                            } else {
                                userResponseCallback.onFailureFromUpdateData(INVALID_OLD_PASSWORD_ERROR);
                            }
                        });
    }

    @Override
    public void updateProfile(String newName, String newSurname) {

        DatabaseReference userRef = firebaseDatabase
                .getReference("users").child(firebaseUser.getUid());

        userRef.child("name").setValue(newName);
        userRef.child("surname").setValue(newSurname);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userResponseCallback.onSuccessFromUpdateData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                userResponseCallback.onFailureFromUpdateData(INVALID_UPDATE);
            }
        });

    }

    @Override
    public void signIn(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null){
                    DatabaseReference userRef = firebaseDatabase.getReference("users").child(firebaseUser.getUid());
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                User user = dataSnapshot.getValue(User.class);
                                userResponseCallback.onSuccessFromAuthentication(user);
                            } else {
                                userResponseCallback.onFailureFromAuthentication(UNEXPECTED_ERROR);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            userResponseCallback.onFailureFromAuthentication(UNEXPECTED_ERROR);
                        }
                    });
                } else {
                    userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                }
            } else {
                userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
            }
        });
    }


    @Override
    public void signInWithGoogle(String idToken) {
        if (idToken != null) {
            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
            firebaseAuth.signInWithCredential(firebaseCredential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        DatabaseReference userRef = firebaseDatabase.getReference("users").child(firebaseUser.getUid());
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    User user = dataSnapshot.getValue(User.class);
                                    userResponseCallback.onSuccessFromAuthentication(user);
                                } else {
                                    User user = new User(firebaseUser.getDisplayName(), "", firebaseUser.getEmail(), firebaseUser.getUid());
                                    userRef.setValue(user).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            userResponseCallback.onSuccessFromAuthentication(user);
                                        } else {
                                            userResponseCallback.onFailureFromAuthentication(UNEXPECTED_ERROR);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                userResponseCallback.onFailureFromAuthentication(UNEXPECTED_ERROR);
                            }
                        });
                    } else {
                        userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                    }
                } else {
                    userResponseCallback.onFailureFromAuthentication(getErrorMessage(task.getException()));
                }
            });
        }
    }
}
