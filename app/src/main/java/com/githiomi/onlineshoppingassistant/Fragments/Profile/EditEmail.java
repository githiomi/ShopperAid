package com.githiomi.onlineshoppingassistant.Fragments.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.githiomi.onlineshoppingassistant.R;
import com.githiomi.onlineshoppingassistant.Ui.ProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class EditEmail extends Fragment implements View.OnClickListener {

    //    TAG
    private static final String TAG = EditEmail.class.getSimpleName();

    //    Widgets
    // Verification
    @BindView(R.id.verificationPassword) TextInputEditText wPassword;
    @BindView(R.id.btnVerifyPassword) Button wVerifyPasswordButton;
    // Email
    @BindView(R.id.editedEmail) TextInputEditText wEditedEmail;
    @BindView(R.id.btnEditEmail) Button wEditEmailButton;
    // Linear layouts
    @BindView(R.id.layoutPasswordVerify) LinearLayout wPasswordVerification;
    @BindView(R.id.layoutNewEmail) LinearLayout wNewEmail;
    // Progress Bar
    @BindView(R.id.progressBar) ProgressBar wProgressBar;

    //    Firebase
    // Current user
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    public EditEmail() {
        // Required empty public constructor
    }

    public static EditEmail newInstance() {
        return new EditEmail();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View editEmailView = inflater.inflate(R.layout.fragment_edit_email, container, false);

        // Butter Knife binding
        ButterKnife.bind(this, editEmailView);

        // On click listener
        wEditEmailButton.setOnClickListener(this);
        wVerifyPasswordButton.setOnClickListener(this);

        return editEmailView;
    }

    // Methods to alt the view
    private void changeToEmail(){
        wPasswordVerification.setVisibility(View.GONE);
        wNewEmail.setVisibility(View.VISIBLE);
    }

    // Method init to update email
    @Override
    public void onClick(View view) {

        if ( view == wVerifyPasswordButton ) {

            verifyUser();

        }

        if ( view == wEditEmailButton ){

            updateUserEmail(view);

        }
    }

    // Method to verify the user
    private void verifyUser(){

        String newPassword = wPassword.getText().toString().trim();

        boolean verifyPassword = isValidPassword(newPassword);

        if ( ! (verifyPassword) ) return;

        AuthCredential userAuth = EmailAuthProvider.getCredential( currentUser.getEmail(), newPassword );

        currentUser.reauthenticate(userAuth).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Context context = getContext();

                if ( task.isSuccessful() ){

                    changeToEmail();

                }else {

                    String error = "Passwords don't match. Cannot Authenticate";
                    wPassword.setError(error);
                    wPassword.requestFocus();

                    Toast.makeText(context, "Cannot Authenticate! Try Again.", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8 ) {
            String error = "Enter a password containing at least 8 characters";
            wPassword.setError(error);
            wPassword.requestFocus();
            return false;
        }else if ( password.isEmpty() ){
            String error = "Not a valid password.";
            wPassword.setError(error);
            wPassword.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String newEmail) {
        boolean isGoodEmail =
                (newEmail != null && android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches());
        if (!isGoodEmail) {
            wEditedEmail.setError("Please enter a valid email address");
            wProgressBar.setVisibility(View.GONE);
            return false;
        }else if (newEmail.equals(currentUser.getEmail())){
            wEditedEmail.setError("New email cannot match previous");
            wProgressBar.setVisibility(View.GONE);
            return false;
        }
        return true;
    }

    // Method to update the user email init
    private void updateUserEmail(View view) {

        // Show progress bar
        hideKeyboard(view);

        String newEmail = wEditedEmail.getText().toString().trim();

        boolean validEmail = isValidEmail(newEmail);

        if ( ! (validEmail) ) return;

        wProgressBar.setVisibility(View.VISIBLE);
        assert currentUser != null;

        currentUser.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                // Hide progress bar
                wProgressBar.setVisibility(View.GONE);
                Context context = getContext();

                if (task.isSuccessful()){

                    Intent backToProfile = new Intent(getActivity(), ProfileActivity.class);
                    Toast.makeText(context, "Email updated", Toast.LENGTH_SHORT).show();
                    backToProfile.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(backToProfile);
                    getActivity().finish();

                }else{

                    String error = task.getException().getMessage().toString();
                    Log.d(TAG, "onComplete: " + error);
                    Toast.makeText(context, "Couldn't update email. Try again", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    //    Custom method to hide the keyboard
    public void hideKeyboard(View view){
        InputMethodManager inputMethodManager =(InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}