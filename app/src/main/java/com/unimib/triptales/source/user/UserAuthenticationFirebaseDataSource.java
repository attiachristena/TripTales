package com.unimib.triptales.source.user;

import static com.unimib.triptales.util.Constants.*;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

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
import com.unimib.triptales.model.Result;
import com.unimib.triptales.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserAuthenticationFirebaseDataSource extends BaseUserAuthenticationRemoteDataSource{

    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;
    private final FirebaseUser firebaseUser;

    public UserAuthenticationFirebaseDataSource(){
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    @Override
    public User getLoggedUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            return null;
        } else {
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
        if(firebaseUser != null) {
            String uid = firebaseUser.getUid();
            firestore.collection("users").document(uid).delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    firebaseUser.delete().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            userResponseCallback.onSuccessLogout();
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

                                DocumentReference docRef = firestore.collection("users").document(firebaseUser.getUid());
                                docRef.get().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        DocumentSnapshot document = task1.getResult();
                                        if (document.exists()) {
                                            userResponseCallback.onFailureFromAuthentication(USER_ALREADY_EXISTS);
                                        } else {
                                            User user = new User(name, surname, email, firebaseUser.getUid());
                                            docRef.set(user).addOnCompleteListener(task2 -> {
                                                if (task2.isSuccessful()) {
                                                    userResponseCallback.onSuccessFromAuthentication(user);
                                                } else {
                                                    userResponseCallback.onFailureFromAuthentication(UNEXPECTED_ERROR);
                                                }
                                            });
                                        }
                                    } else {
                                        userResponseCallback.onFailureFromAuthentication(getErrorMessage(task1.getException()));
                                    }
                                }).addOnFailureListener(e -> {
                                    userResponseCallback.onFailureFromAuthentication(getErrorMessage(e));
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
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        resultLiveData.postValue(new Result.GenericSuccess());
                    } else {
                        resultLiveData.postValue(new Result.Error(task.getException().getMessage()));
                    }
                });
        return resultLiveData;
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
                                    User user = new User(firebaseUser.getDisplayName(), "", firebaseUser.getEmail(), firebaseUser.getUid());
                                    docRef.set(user).addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            userResponseCallback.onSuccessFromAuthentication(user);
                                        } else {
                                            userResponseCallback.onFailureFromAuthentication(UNEXPECTED_ERROR);
                                        }
                                    });
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
