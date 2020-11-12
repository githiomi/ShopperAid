package com.githiomi.onlineshoppingassistant.Fragments.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.githiomi.onlineshoppingassistant.R;
import com.githiomi.onlineshoppingassistant.Ui.ProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class EditUsername extends Fragment implements View.OnClickListener {

    //    Widgets
    @BindView(R.id.editedUsername) TextInputEditText wEditedUsername;
    @BindView(R.id.btnEditUsername) Button wEditUsernameButton;
    // Progress bar
    @BindView(R.id.progressBar) ProgressBar wProgressBar;

    //    Firebase
    // Current user
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    public EditUsername() {
        // Required empty public constructor
    }

    public static EditUsername newInstance() {
        return new EditUsername();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View editUsernameView = inflater.inflate(R.layout.fragment_edit_username, container, false);

        // Butter knife binding
        ButterKnife.bind(this, editUsernameView);

        // Setting on click listener to the button
        wEditUsernameButton.setOnClickListener(this);

        return editUsernameView;
    }

    //    Methods to validate user input for update
    private boolean isValidName(String newUsername) {
        if (newUsername.equals("")) {
            wEditedUsername.setError("Please enter a new username");
            return false;
        }else if(newUsername.equals(currentUser.getDisplayName())){
            wEditedUsername.setError("New username cannot match previous");
            return false;
        }
        return true;
    }

    // On click listener to allow
    @Override
    public void onClick(View view) {

        if ( view == wEditUsernameButton ){

            updateUserUsername(view);

        }
    }

    // Method to update the user username init
    private void updateUserUsername(View view) {

        hideKeyboard(view);

        String newUsername = wEditedUsername.getText().toString().trim();

        boolean validUsername = isValidName(newUsername);

        if ( ! (validUsername) ) return;

        // Show progress bar
        wProgressBar.setVisibility(View.VISIBLE);

        UserProfileChangeRequest addedUsername = new UserProfileChangeRequest.Builder()
                .setDisplayName(newUsername)
                .build();

        currentUser.updateProfile(addedUsername).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                // Hide progress bar
                wProgressBar.setVisibility(View.GONE);

                Context context = getContext();

                if (task.isSuccessful()){

                    Intent backToProfile = new Intent(getActivity(), ProfileActivity.class);
                    Toast.makeText(context, "Username updated", Toast.LENGTH_SHORT).show();
                    context.startActivity(backToProfile);

                }else{

                    Toast.makeText(context, "Couldn't update username. Try again", Toast.LENGTH_SHORT).show();

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