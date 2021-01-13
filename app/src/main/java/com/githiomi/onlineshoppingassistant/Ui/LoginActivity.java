package com.githiomi.onlineshoppingassistant.Ui;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.githiomi.onlineshoppingassistant.Models.Constants;
import com.githiomi.onlineshoppingassistant.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

// Facebook libraries

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    //    TAG
    private static final String TAG = LoginActivity.class.getSimpleName();

    //    Binding widgets using butter knife
    @BindView(R.id.tvProceedAsGuest) TextView wProceedAsGuest;
    @BindView(R.id.edEmail) TextInputEditText wUserEmail;
    @BindView(R.id.edPassword) TextInputEditText wUserPassword;
    @BindView(R.id.btnLogin) Button wLoginButton;
    @BindView(R.id.cvLoginBtn) CardView wCvLoginButton;
    @BindView(R.id.cvSignInWithGoogle) CardView wCvSignInWithGoogle;
    @BindView(R.id.googleProgressBar) ProgressBar wGoogleProgressBar;
    @BindView(R.id.cvSignInWithFacebook) CardView wCvSignInWithFacebook;
    @BindView(R.id.ButtonFacebookLogin) LoginButton wFacebookLoginButton;
    @BindView(R.id.facebookProgressBar) ProgressBar wFacebookProgressBar;
    @BindView(R.id.tvToSignUp) TextView wToSignUp;
    @BindView(R.id.tvForgotPassword) TextView wForgotPassword;
    @BindView(R.id.loginProgressBar) ProgressBar wLoginProgressBar;
    @BindView(R.id.adContainer) FrameLayout wAdContainer;

    //    Local variables
    // Date entry
    private String userEmail;
    private String userPassword;
    // Firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    // Ad view
    private AdView adView;
    // Sign in with google
    private GoogleSignInClient googleSignInClient;
    // Sign in with facebook
    // Callbacks
    private CallbackManager mCallbackManager;
    // Token tracker
    private AccessTokenTracker accessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme
        // Get the theme applied
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPreferences.getString(Constants.APP_THEME, "Light Mode");

        if (theme.equals("Dark Mode")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Check and apply theme
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Binding widgets using butter knife
        ButterKnife.bind(this);

        // Requesting email & profile
        wFacebookLoginButton.setReadPermissions("email", "public_profile");

        // Configure Google sign in options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Adding the google options to google client
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Sign in with facebook
        // Init callback manager
        mCallbackManager = CallbackManager.Factory.create();
        // The button to change to login if the token is null
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

                if (oldAccessToken == null || oldAccessToken.isDataAccessExpired() || currentAccessToken == null) {
                    mFirebaseAuth.signOut();
                }
            }
        };

        // Adding callback to facebook button
        wFacebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                // Update UI
                wCvSignInWithFacebook.setVisibility(View.GONE);
                wFacebookProgressBar.setVisibility(View.VISIBLE);

                // If the login is a success
                String success = "Login successful";
                Log.d(TAG, "onSuccess: Obtained loginResult ----- " + success);

                // method that will make use of the access token
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

                // Return card view
                wFacebookProgressBar.setVisibility(View.GONE);
                wCvSignInWithFacebook.setVisibility(View.VISIBLE);

                // If cancelled
                String cancelled = "Login cancelled";
                Log.d(TAG, "onCancel: " + cancelled);

            }

            @Override
            public void onError(FacebookException error) {

                // Return card view
                wFacebookProgressBar.setVisibility(View.GONE);
                wCvSignInWithFacebook.setVisibility(View.VISIBLE);

                // If an error occurs
                String errorToPrint = error.getMessage().toString();
                Log.d(TAG, "onError: " + errorToPrint);

            }
        });

        // Init auth listener
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };

        // All listeners
        wProceedAsGuest.setOnClickListener(this);
        wLoginButton.setOnClickListener(this);
        wCvSignInWithGoogle.setOnClickListener(this);
        wCvSignInWithFacebook.setOnClickListener(this);
        wForgotPassword.setOnClickListener(this);
        wToSignUp.setOnClickListener(this);

        // Init ads
        MobileAds.initialize(this);

        // Init the ad view
        adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        wAdContainer.addView(adView);
        loadBanner();

    }

    // For the adaptive banner
    private void loadBanner() {
        AdRequest adRequest =
                new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .build();

        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);

        adView.loadAd(adRequest);
    }

    private AdSize getAdSize() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    //    Implementation of the on click listener
    @Override
    public void onClick(View v) {

        if (v == wProceedAsGuest) {

            hideKeyboard(v);
            Intent toSearchActivity = new Intent(this, SearchActivity.class);
            toSearchActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(toSearchActivity);
            finish();

        }

        if (v == wLoginButton) {
            // Update UI
            hideKeyboard(v);
            loginUser(v);
        }

        if (v == wCvSignInWithGoogle) {
            // Update UI
            wCvSignInWithGoogle.setVisibility(View.GONE);
            wGoogleProgressBar.setVisibility(View.VISIBLE);
            signInWithGoogle();
        }

        if (v == wForgotPassword) {
            hideKeyboard(v);
            resetPassword(v);
        }

        if (v == wToSignUp) {
            hideKeyboard(v);
            toSignUp(v);
        }
    }

    //    Method to login user with google
    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, Constants.RC_SIGN_IN, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    //    Method for activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Adding the activity result to the callback
        if (mCallbackManager == null) {
            // Update UI
            wGoogleProgressBar.setVisibility(View.GONE);
            wCvSignInWithGoogle.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Please Try Again!", Toast.LENGTH_SHORT).show();
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);

            super.onActivityResult(requestCode, resultCode, data);

            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == Constants.RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                if (task.isSuccessful()) {
                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                        firebaseAuthWithGoogle(account.getIdToken());

                    } catch (ApiException e) {
                        // Update UI
                        wGoogleProgressBar.setVisibility(View.GONE);
                        wCvSignInWithGoogle.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Please Try Again!", Toast.LENGTH_SHORT).show();

                        Log.w(TAG, "Google sign in failed", e);
                    }
                } else {
                    // Update UI
                    wGoogleProgressBar.setVisibility(View.GONE);
                    wCvSignInWithGoogle.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Please Try Again!", Toast.LENGTH_SHORT).show();

                    String exception = Objects.requireNonNull(task.getException()).toString();
                    Log.d(TAG, "onActivityResult: Error ------ " + exception);
                }
            }
        }
    }

    //    Method that will sign in user
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");

                            Intent toSearchActivity = new Intent(LoginActivity.this, SearchActivity.class);
                            toSearchActivity.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
                            startActivity(toSearchActivity);
                            finish();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Method to login user with access token passed
    private void handleFacebookToken(AccessToken accessToken) {

        // Print the access token
        Log.d(TAG, "handleFacebookToken: Access token: " + accessToken.getToken());

        // Get credential to sign in
        AuthCredential facebookAuthCredential = FacebookAuthProvider.getCredential(accessToken.getToken());

        // sign in user using the access token
        mFirebaseAuth.signInWithCredential(facebookAuthCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithFacebookCredential:success");
                            Intent toSearchActivity = new Intent(LoginActivity.this, SearchActivity.class);
                            toSearchActivity.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
                            startActivity(toSearchActivity);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithFacebookCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "This email address already exists! Login with Google.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //    Method to log in a user
    private void loginUser(View v) {

        // Get data
        userEmail = wUserEmail.getText().toString().trim();
        userPassword = wUserPassword.getText().toString().trim();

        // Validate the data
        boolean isEmailValid = emailValidity(userEmail);
        boolean isPasswordValid = passwordValidity(userPassword);

        if (!(isEmailValid) || !(isPasswordValid)) return;

        // Hide button & show progress bar
        wCvLoginButton.setVisibility(View.GONE);
        wLoginProgressBar.setVisibility(View.VISIBLE);

        // Sign in
        mFirebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                // Make the progress bar invisible
                wLoginProgressBar.setVisibility(View.GONE);
                wCvLoginButton.setVisibility(View.VISIBLE);

                if ( task.isSuccessful() ) {

                    Intent toSearchActivity = new Intent(LoginActivity.this, SearchActivity.class);
                    toSearchActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(toSearchActivity);
                    finish();

                }else {

                    // Hide progress bar & return button
                    wLoginProgressBar.setVisibility(View.GONE);
                    wCvLoginButton.setVisibility(View.VISIBLE);
                    wForgotPassword.setVisibility(View.VISIBLE);

                    Snackbar.make(v, "Incorrect email or password. Try again", Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                            .setTextColor(getResources().getColor(R.color.white))
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                            .setAction("Action", null).show();

                }
            }
        });
    }

    //    Data validation
    // For email
    private boolean emailValidity(String email) {

        if (email.isEmpty()) {
            String error = "This field must not be left blank";
            wUserEmail.setError(error);
            return false;
        } else {

            boolean isEmailGood = (email != null &&
                    Patterns.EMAIL_ADDRESS.matcher(email).matches());

            if (!(isEmailGood)) {
                wUserEmail.setError("Email Address is not valid. Try again");
                return false;
            }
        }

        return true;
    }

    // For password
    private boolean passwordValidity(String password) {

        if (password.isEmpty() || password.equals("")) {
            String error = "This field must not be left blank";
            wUserPassword.setError(error);
            return false;
        } else {
            if (password.length() < 8) {
                wUserPassword.setError("Minimum of 8 characters required");
                wForgotPassword.setVisibility(View.VISIBLE);
                return false;
            }
        }

        wForgotPassword.setVisibility(View.GONE);
        return true;

    }

    //    Method to reset password if forgotten
    public void resetPassword(View v) {

        Context context = v.getContext();

        TextInputEditText textInputEditText = new TextInputEditText(context);

        // Alert Dialog texts
        String title = "Enter your email";
        String message = "Please enter your email below so that the reset link can be sent.";

        AlertDialog.Builder resetPasswordDialog = new AlertDialog.Builder(context);

        resetPasswordDialog.setTitle(title);
        resetPasswordDialog.setMessage(message);
        resetPasswordDialog.setView(textInputEditText);

        resetPasswordDialog.setPositiveButton("Send Link", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String email = textInputEditText.getText().toString().trim();

                if (email.isEmpty()) {
                    Snackbar.make(v, "You must provide a valid email address", Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                            .setTextColor(getResources().getColor(R.color.white))
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                            .setAction("Action", null).show();
                } else {
                    // Send email
                    mFirebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Snackbar.make(v, "Reset email has been sent", Snackbar.LENGTH_SHORT)
                                        .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                                        .setTextColor(getResources().getColor(R.color.white))
                                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                                        .setAction("Action", null).show();
                            } else {
                                Snackbar.make(v, "Reset email has not been sent", Snackbar.LENGTH_SHORT)
                                        .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                                        .setTextColor(getResources().getColor(R.color.white))
                                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                                        .setAction("Action", null).show();
                            }
                        }
                    });
                }
            }
        });
        resetPasswordDialog.setNegativeButton("Cancel", null);

        // Display the dialog
        resetPasswordDialog.create().show();
    }

    //    Method to create a new user account
    public void toSignUp(View v) {

        Intent toSignUp = new Intent(this, SignUpActivity.class);
        toSignUp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(toSignUp, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        finish();

    }

    //    Methods to hide the keyboard
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == wUserEmail) {
            hideKeyboard(v);
        }

        if (v == wUserPassword) {
            hideKeyboard(v);
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // For the firebase to start and end the activity
    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

        FirebaseUser loggedInUser = mFirebaseAuth.getCurrentUser();

        if (loggedInUser != null) {

            Intent toSearchActivity = new Intent(this, SearchActivity.class);
            toSearchActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(toSearchActivity);
            finish();

        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

}