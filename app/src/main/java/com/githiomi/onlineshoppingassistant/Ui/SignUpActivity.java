package com.githiomi.onlineshoppingassistant.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

import com.githiomi.onlineshoppingassistant.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

//    TAG
    private static final String TAG = SignUpActivity.class.getSimpleName();

//    Binding widgets using Butter knife
    @BindView(R.id.ivBackToLogin) ImageView wBackToLogin;
    @BindView(R.id.edUsername) TextInputEditText wUsername;
    @BindView(R.id.edEmail) TextInputEditText wEmail;
    @BindView(R.id.edPassword) TextInputEditText wPassword;
    @BindView(R.id.edConfirmPassword) TextInputEditText wConfirmPassword;
    @BindView(R.id.btnSignUp) Button wBtnSignUp;

//    Local variables
    // User
    private String username;
    // Firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Binding widgets
        ButterKnife.bind(this);

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
        // Focus change listeners
        wUsername.setOnFocusChangeListener(this);
        wEmail.setOnFocusChangeListener(this);
        wPassword.setOnFocusChangeListener(this);
        wConfirmPassword.setOnFocusChangeListener(this);

    }

//    The on click listener implementation
    @Override
    public void onClick(View v) {

        if ( v == wBackToLogin ){
            hideKeyboard(v);
            Intent backToLogin = new Intent(this, LoginActivity.class);
            backToLogin.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
            startActivity(backToLogin);
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
        String password = wPassword.getText().toString().trim();
        String confirmPassword = wConfirmPassword.getText().toString().trim();

        // Validating the input
        boolean isNameValid = isUsernameValid(username);
        boolean isEmailValid = isEmailValid(email);
        boolean isPasswordsValid = isPasswordsValid(password, confirmPassword);

        if ( !(isNameValid) || !(isEmailValid) || !(isPasswordsValid) ) return;

        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if ( task.isSuccessful() ){

                    Snackbar.make( v, "Your account has been created", Snackbar.LENGTH_SHORT )
                            .setAction( "Action", null ).show();

                    // Add the username to the account created
                    FirebaseUser firebaseUser = task.getResult().getUser();
                    assert firebaseUser != null;
                    addUsernameToAccount(firebaseUser);

                    // Go to the search activity
                    Intent toSearchActivity = new Intent( v.getContext(), SearchActivity.class );
                    toSearchActivity.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
                    startActivity(toSearchActivity);
                    finish();

                }else {

                    Snackbar.make( v, "Couldn't create your account", Snackbar.LENGTH_SHORT )
                            .setBackgroundTint(getResources().getColor(R.color.white)).setActionTextColor(getResources().getColor(R.color.colorPrimary)).setAction( "Action", null ).show();

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

//    Methods to hide the keyboard
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == wUsername) {
            hideKeyboard(v);
        }

        if (v == wEmail) {
            hideKeyboard(v);
        }

        if ( v == wPassword ){
            hideKeyboard(v);
        }

        if ( v == wConfirmPassword ){
            hideKeyboard(v);
        }

    }

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