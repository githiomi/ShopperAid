package com.githiomi.onlineshoppingassistant.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.githiomi.onlineshoppingassistant.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

//    TAG
    private static final String TAG = LoginActivity.class.getSimpleName();

//    Binding widgets using butter knife
    @BindView(R.id.edEmail) TextInputEditText wUserEmail;
    @BindView(R.id.edPassword) TextInputEditText wUserPassword;
    @BindView(R.id.btnLogin) Button wLoginButton;
    @BindView(R.id.tvToSignUp) TextView wToSignUp;
    @BindView(R.id.tvForgotPassword) TextView wForgotPassword;

//    Local variables
    // Date entry
    private String userEmail;
    private String userPassword;
    // Firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Binding widgets using butter knife
        ButterKnife.bind(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser loggedInUser = mFirebaseAuth.getCurrentUser();

                if ( loggedInUser != null ){

                    Toast.makeText(LoginActivity.this, "User logged in", Toast.LENGTH_SHORT).show();

//                    Snackbar.make(this, "User logged in", Snackbar.LENGTH_SHORT)
//                            .setAction("Action", null).show();
                }
            }
        };

        // All listeners
        wLoginButton.setOnClickListener(this);
        wForgotPassword.setOnClickListener(this);
        wToSignUp.setOnClickListener(this);
    }

//    Implementation of the on click listener
    @Override
    public void onClick(View v) {

        if ( v == wLoginButton ){
            loginUser(v);
        }else if ( v == wForgotPassword ){
            resetPassword(v);
        }else if ( v == wToSignUp ){
            toSignUp(v);
        }
    }

//    Method to log in a user
    private void loginUser( View v ){

        // Get data
        userEmail = wUserEmail.getText().toString().trim();
        userPassword = wUserPassword.getText().toString().trim();

        // Validate the data
        boolean isEmailValid = emailValidity(userEmail);
        boolean isPasswordValid = passwordValidity(userPassword);

        if ( !(isEmailValid) && !(isPasswordValid) ) return;

        // Sign in
        mFirebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if ( task.isSuccessful() ){
                    Log.d(TAG, "onComplete: " + mFirebaseAuth.getCurrentUser().getDisplayName() + " logged in" );
                }else{
                    Log.d(TAG, "onComplete: FAILED! Cannot log in");
                }
            }
        });

    }

//    Data validation
    // For email
    private boolean emailValidity( String email ){

        boolean isEmailGood = ( email != null &&
                                Patterns.EMAIL_ADDRESS.matcher(email).matches() );

        if ( !(isEmailGood) ){
            wUserEmail.setError("Email Address is not valid. Try again");
            return false;
        }

        return true;

    }
    // For password
    private boolean passwordValidity( String password ){

        if ( password.length() < 8 ){
            wUserPassword.setError("Minimum of 8 characters required");
            return false;
        }

        return true;

    }

//    Method to reset password if forgotten
    public void resetPassword( View v ){

        Context context = v.getContext();

        TextInputEditText textInputEditText = new TextInputEditText(context);
        textInputEditText.setPadding(10, 10, 10,10);

        // Alert Dialog texts
        String title = "Enter your email";
        String message = "Please enter your email below so that the reset link can be sent.";

        AlertDialog.Builder resetPasswordDialog = new AlertDialog.Builder(context);

        resetPasswordDialog.setTitle(title);
        resetPasswordDialog.setMessage(message);
        resetPasswordDialog.setView(textInputEditText);

        resetPasswordDialog.setPositiveButton("Send Reset Link", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String email = textInputEditText.getText().toString().trim();

                if ( email.isEmpty() ){
                    Snackbar.make(v, "Cannot leave the field empty", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }

                // Send email
                mFirebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if ( task.isSuccessful() ){
                            Snackbar.make(v, "Reset email has been sent", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                        }else{
                            Snackbar.make(v, "Reset email has been sent", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                        }
                    }
                });
            }
        });

        resetPasswordDialog.setNegativeButton("Cancel", null);

        // Display the dialog
        resetPasswordDialog.create().show();
    }

//    Method to create a new user account
    public void toSignUp( View v ){

        Intent toSignUp = new Intent(this, SignUpActivity.class);
        startActivity(toSignUp);

    }

// For the firebase to start and end the activity
    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

        FirebaseUser loggedInUser = mFirebaseAuth.getCurrentUser();

        if ( loggedInUser != null ){

            Intent toSearchActivity = new Intent(this, SearchActivity.class);
            toSearchActivity.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
            startActivity(toSearchActivity);
            finish();

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

}