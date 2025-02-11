package com.unimib.triptales.ui.login.fragment;

import static com.unimib.triptales.util.Constants.INVALID_CREDENTIALS_ERROR;
import static com.unimib.triptales.util.Constants.INVALID_USER_ERROR;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.unimib.triptales.R;
import com.unimib.triptales.model.Result;
import com.unimib.triptales.repository.user.IUserRepository;
import com.unimib.triptales.ui.homepage.HomepageActivity;
import com.unimib.triptales.ui.login.viewmodel.UserViewModel;
import com.unimib.triptales.ui.login.viewmodel.UserViewModelFactory;
import com.unimib.triptales.util.ServiceLocator;
import com.unimib.triptales.util.SharedPreferencesUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginFragment extends Fragment {

    private final static String TAG = LoginFragment.class.getSimpleName();

    private TextInputEditText editTextEmail, editTextPassword;

    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;
    private ActivityResultContracts.StartIntentSenderForResult startIntentSenderForResult;
    private UserViewModel userViewModel;
    private LoadingDialog loadingDialog;

    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IUserRepository userRepository = ServiceLocator.getINSTANCE().getUserRepository();
        userViewModel = new ViewModelProvider(requireActivity(), new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        loadingDialog = new LoadingDialog(requireActivity());

        oneTapClient = Identity.getSignInClient(requireActivity());
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.default_web_client_id))
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .setAutoSelectEnabled(true)
                .build();

        startIntentSenderForResult = new ActivityResultContracts.StartIntentSenderForResult();

        activityResultLauncher = registerForActivityResult(startIntentSenderForResult, activityResult -> {
            if (activityResult.getResultCode() == Activity.RESULT_OK) {
                Log.d(TAG, "result.getResultCode() == Activity.RESULT_OK");
                loadingDialog.startLoadingDialog();
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(activityResult.getData());
                    String idToken = credential.getGoogleIdToken();
                    if (idToken !=  null) {
                        userViewModel.getGoogleUserMutableLiveData(idToken).observe(getViewLifecycleOwner(), authenticationResult -> {
                            loadingDialog.dismissDialog();
                            if (authenticationResult.isSuccess()) {
                                SharedPreferencesUtils.setLoggedIn(getContext(), true);
                                startActivity(new Intent(getContext(), HomepageActivity.class));
                            } else {
                                userViewModel.setAuthenticationError(true);
                                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                        getErrorMessage(((Result.Error) authenticationResult).getMessage()),
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (ApiException e) {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                            requireActivity().getString(R.string.error_unexpected),
                            Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }


    private String getErrorMessage(String errorType) {
        switch (errorType) {
            case INVALID_CREDENTIALS_ERROR:
                return requireActivity().getString(R.string.error_password_login);
            case INVALID_USER_ERROR:
                return getString(R.string.user_not_logged_in_error);
            default:
                return getString(R.string.unexpected_error);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton backButton = view.findViewById(R.id.backarrow);
        backButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_selezione));

        Button signUpButton = view.findViewById(R.id.signInButton);
        signUpButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_signInFragment));

        editTextEmail = view.findViewById(R.id.textInputEmail);
        editTextPassword = view.findViewById(R.id.textInputPassword);

        ImageButton togglePassword = view.findViewById(R.id.showPassword);
        togglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                    editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    togglePassword.setImageResource(R.drawable.eye_key_look_password_security_see_svgrepo_com);
                } else {
                    editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    togglePassword.setImageResource(R.drawable.eye_password_see_view_svgrepo_com);
                }
                editTextPassword.setSelection(editTextPassword.getText().length());
            }
        });

        Button loginButton = view.findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            boolean isValid = true;

            if (TextUtils.isEmpty(editTextEmail.getText())) {
                editTextEmail.setError(getString(R.string.error_email_login));
                isValid = false;
            } else if (!isEmailOk(editTextEmail.getText().toString())) {
                editTextEmail.setError(getString(R.string.error_email_login));
                isValid = false;
            }

            if (TextUtils.isEmpty(editTextPassword.getText())) {
                editTextPassword.setError(getString(R.string.error_password_login));
                isValid = false;
            } else if (!isPasswordOk(editTextPassword.getText().toString())) {
                editTextPassword.setError(getString(R.string.error_password_login));
                isValid = false;
            }

            if (isValid) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                loginButton.setEnabled(false);

                loadingDialog.startLoadingDialog();

                userViewModel.getUserMutableLiveData(email, password, true).observe(getViewLifecycleOwner(), result -> {
                    loginButton.setEnabled(true);
                    loadingDialog.dismissDialog();
                    if (result.isSuccess()) {
                        SharedPreferencesUtils.setLoggedIn(getContext(), true);
                        startActivity(new Intent(getContext(), HomepageActivity.class));
                    } else {
                        userViewModel.setAuthenticationError(true);
                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                getErrorMessage(((Result.Error) result).getMessage()),
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Button passwordDimenticata = view.findViewById(R.id.passwordDimenticata);

        passwordDimenticata.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_passwordDimenticataFragment));

        Button googleLoginButton = view.findViewById(R.id.googleLoginButton);
        googleLoginButton.setOnClickListener(v -> { oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(requireActivity(), result -> {
                    try {
                        activityResultLauncher.launch(new IntentSenderRequest.Builder(result.getPendingIntent()).build());
                    } catch (Exception e) {
                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                getString(R.string.error_unexpected),
                                Snackbar.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(requireActivity(), e -> {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                            getString(R.string.error_google_login),
                            Snackbar.LENGTH_SHORT).show();
                });
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