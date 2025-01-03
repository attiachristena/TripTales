package com.unimib.triptales.source.user;

import static com.unimib.triptales.util.Constants.*;

import androidx.annotation.NonNull;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.unimib.triptales.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserAuthenticationFirebaseDataSource extends BaseUserAuthenticationRemoteDataSource{

    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;

    public UserAuthenticationFirebaseDataSource(){
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public User getLoggedUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null)
            return null;
        else{
            DocumentReference docRef = firestore.collection("users").document(firebaseUser.getUid());
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User user = document.toObject(User.class);
                        userResponseCallback.onSuccessGetLoggedUser(user);
                    } else {
                        userResponseCallback.onFailureGetLoggedUser(UNEXPECTED_ERROR);
                    }
                } else {
                    userResponseCallback.onFailureGetLoggedUser(UNEXPECTED_ERROR);
                }
            });
            return null;
        }
    }

    @Override
    public void logout() {
        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    firebaseAuth.removeAuthStateListener(this);
                    userResponseCallback.onSuccessLogout();
                }
            }
        };
        firebaseAuth.addAuthStateListener(authStateListener);
        firebaseAuth.signOut();
    }

    @Override
    public void signUp(String name, String surname, String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null){
                    User user = new User(name, surname, email, firebaseUser.getUid());
                    DocumentReference docRef = firestore.collection("users").document(firebaseUser.getUid());
                    docRef.set(user).addOnCompleteListener(task1 -> {
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
    public void signIn(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null){
                    DocumentReference docRef = firestore.collection("users").document(firebaseUser.getUid());
                    docRef.get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            DocumentSnapshot document = task1.getResult();
                            if (document.exists()) {
                                User user = document.toObject(User.class);
                                userResponseCallback.onSuccessFromAuthentication(user);
                            } else {
                                userResponseCallback.onFailureFromAuthentication(UNEXPECTED_ERROR);
                            }
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


    @Override
    public void signInWithGoogle(String idToken) {
        if (idToken != null){
            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
            firebaseAuth.signInWithCredential(firebaseCredential).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if(firebaseUser != null){
                        DocumentReference docRef = firestore.collection("users").document(firebaseUser.getUid());
                        docRef.get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                DocumentSnapshot document = task1.getResult();
                                if (document.exists()) {
                                    User user = document.toObject(User.class);
                                    userResponseCallback.onSuccessFromAuthentication(user);
                                } else {
                                    userResponseCallback.onFailureFromAuthentication(UNEXPECTED_ERROR);
                                }
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
    }
}
