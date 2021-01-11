package com.githiomi.onlineshoppingassistant.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.githiomi.onlineshoppingassistant.Models.Constants;
import com.githiomi.onlineshoppingassistant.Models.PhoneNumber;
import com.githiomi.onlineshoppingassistant.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

//    TAG
    private static final String TAG = SignUpActivity.class.getSimpleName();

//    Binding widgets using Butter knife
    @BindView(R.id.ivBackToLogin) ImageView wBackToLogin;
    @BindView(R.id.edUsername) TextInputEditText wUsername;
    @BindView(R.id.edEmail) TextInputEditText wEmail;
    @BindView(R.id.edPhoneNumber) TextInputEditText wPhoneNumber;
    @BindView(R.id.edPassword) TextInputEditText wPassword;
    @BindView(R.id.edConfirmPassword) TextInputEditText wConfirmPassword;
    @BindView(R.id.btnSignUp) Button wBtnSignUp;
    @BindView(R.id.signUpProgressBar) ProgressBar wSignUpProgressBar;
    @BindView(R.id.tvBackToLogin) TextView wTvBackToLogin;
    @BindView(R.id.adContainer) FrameLayout wAdContainer;

//    Local variables
    // User
    private String username;
    // Firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    // Ads
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Binding widgets
        ButterKnife.bind(this);

        // Init ads
        MobileAds.initialize(this);

        // Loading the banner ad
        adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        wAdContainer.addView(adView);
        loadBanner();

        //        Firebase variables authentication
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            }
        };

        // Listeners
        wBackToLogin.setOnClickListener(this);
        wBtnSignUp.setOnClickListener(this);
        wTvBackToLogin.setOnClickListener(this);

    }

    //    For the adaptive banner
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

//    The on click listener implementation
    @Override
    public void onClick(View v) {

        if ( v == wBackToLogin ){
            hideKeyboard(v);
            Intent backToLogin = new Intent(this, LoginActivity.class);
            backToLogin.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
            startActivity(backToLogin, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            finish();
        }

        if ( v == wTvBackToLogin ){
            hideKeyboard(v);
            Intent backToLogin = new Intent(this, LoginActivity.class);
            backToLogin.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
            startActivity(backToLogin, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            finish();
        }

        if( v == wBtnSignUp ){
            hideKeyboard(v);
            signUpNewUser(v);
        }
    }

//    Method that will collect data and create the new user profile
    private void signUpNewUser( View v ){

        username = wUsername.getText().toString().trim();
        String email = wEmail.getText().toString().trim();
        String phoneNumber = wPhoneNumber.getText().toString().trim();
        String password = wPassword.getText().toString().trim();
        String confirmPassword = wConfirmPassword.getText().toString().trim();

        // Validating the input
        boolean isNameValid = isUsernameValid(username);
        boolean isEmailValid = isEmailValid(email);
        boolean isNumberValid = isNumberValid(phoneNumber);
        boolean isPasswordsValid = isPasswordsValid(password, confirmPassword);

        if ( !(isNameValid) || !(isEmailValid) || !(isNumberValid) || !(isPasswordsValid) ) return;

        // Exchange button with progress bar
        wBtnSignUp.setVisibility(View.GONE);
        wSignUpProgressBar.setVisibility(View.VISIBLE);

        // Activity
        Activity activity = this;

        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if ( task.isSuccessful() ){

                    // Add the username to the account created
                    FirebaseUser firebaseUser = task.getResult().getUser();
                    assert firebaseUser != null;
                    addUsernameToAccount(firebaseUser);

                    // Save the phone number to firebase
                    // Creating reference
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                                                                          .getReference("Users' Phone Numbers")
                                                                          .child(username);

                    String completeNumber = "+254" + phoneNumber;
                    PhoneNumber phoneNumberClass = new PhoneNumber(username, completeNumber);

                    databaseReference.setValue(phoneNumberClass);

                    // Exchange button with progress bar
                    wSignUpProgressBar.setVisibility(View.GONE);
                    wBtnSignUp.setVisibility(View.VISIBLE);

                    // Go to the search activity
                    Intent toSearchActivity = new Intent( v.getContext(), SearchActivity.class );
                    toSearchActivity.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
                    startActivity(toSearchActivity, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
                    finish();

                }else {

                    // Exchange button with progress bar
                    wSignUpProgressBar.setVisibility(View.GONE);
                    wBtnSignUp.setVisibility(View.VISIBLE);

                    Snackbar.make( v, "Couldn't create your account. Try again.", Snackbar.LENGTH_SHORT )
                            .setAction( "Action", null ).show();

                }
            }
        });
    }

// To add the username to the account
    private void addUsernameToAccount( FirebaseUser firebaseUser ){

        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                                                                        .setDisplayName(username)
                                                                                        .build();

        firebaseUser.updateProfile(userProfileChangeRequest);
    }

//    Validation methods
    // Validate Username
    private boolean isUsernameValid( String username ){
        if ( username.isEmpty() ){
            wUsername.setError("This field cannot be left empty");
            return false;
        }
        return true;
    }

    // Validate Email
    private boolean isEmailValid( String email ){
        boolean emailGood = ( email != null &&
                              Patterns.EMAIL_ADDRESS.matcher(email).matches() );

        if ( !(emailGood) ){
            wEmail.setError("Enter a valid email address");
            return false;
        }
        return true;
    }

    // Validate Phone number
    private boolean isNumberValid( String phoneNumber ){
        int digitLimit = 9;
        int numberLength = phoneNumber.length();

        if ( numberLength == digitLimit ){

            char firstDigit = phoneNumber.charAt(0);

            return firstDigit == '7' || firstDigit == '1';

        }else {
            wPhoneNumber.setError("Incorrect Phone number format");
            return false;
        }
    }

    // Validate Passwords
    private boolean isPasswordsValid( String password, String confirmPassword ){
        if (password.length() < 8 ) {
            String error = "Password must contain at least 8 characters";
            wPassword.setError(error);
            return false;
        }

        if ( confirmPassword.length() < 8 ){
            String error = "Password must contain at least 8 characters";
            wConfirmPassword.setError(error);
            return false;
        }

        if ( !password.equals(confirmPassword) ) {
            wPassword.setError("Passwords do not match");
            wConfirmPassword.setError("Passwords do not match");
            return false;
        }
        return true;
    }

//    Method to hide the keyboard
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

//    Adding auth state listeners
    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        if ( mAuthStateListener != null ) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        super.onStop();
    }
}