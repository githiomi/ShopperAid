package com.githiomi.onlineshoppingassistant.Ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.githiomi.onlineshoppingassistant.Models.Constants;
import com.githiomi.onlineshoppingassistant.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    //    Widgets
    @BindView(R.id.userProfilePicture)
    CircleImageView wUserProfilePicture;
    @BindView(R.id.tvProfileUsername)
    TextView wUsername;
    @BindView(R.id.tvProfileEmail)
    TextView wEmail;
    @BindView(R.id.editProfilePicture)
    ImageButton wEditProfilePicture;
    @BindView(R.id.drawerLayout)
    DrawerLayout wDrawerLayout;
    @BindView(R.id.userNavigation)
    NavigationView wNavigationView;
    @BindView(R.id.profileProgressBar)
    ProgressBar wProfileProgressBar;

    // Navigation view
    View navigationView;
    CircleImageView wNavImage;
    TextView wNavUsername;

    //    Firebase current user
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

//        Binding widgets
        ButterKnife.bind(this);

//        Navigation Drawer Menu
        wNavigationView.bringToFront();
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, wDrawerLayout, R.string.open_navigation_drawer, R.string.close_navigation_drawer);
        wDrawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        // Setting default checked item
        wNavigationView.setCheckedItem(R.id.toProfileNav);

//        Customize the navigation
        navigationView = wNavigationView.getHeaderView(0);

        wNavImage = (CircleImageView) navigationView.findViewById(R.id.navUserProfilePicture);
        wNavUsername = (TextView) navigationView.findViewById(R.id.navUserUsername);

//        Navigation selected item listener
        wNavigationView.setNavigationItemSelectedListener(this);

//        On click listener
        wUserProfilePicture.setOnClickListener(this);
        wEditProfilePicture.setOnClickListener(this);
        wUsername.setOnClickListener(this);
        wEmail.setOnClickListener(this);
        wNavImage.setOnClickListener(this);
        wNavUsername.setOnClickListener(this);

        getUserData();

    }

    // To collect data to show
    private void getUserData() {

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        assert currentUser != null;

        String username = currentUser.getDisplayName();
        wNavUsername.setText(username);
        String email = currentUser.getEmail();

        // Setting the profile picture
        Uri userUri = currentUser.getPhotoUrl();

        if (userUri != null) {

            Picasso.get()
                    .load(userUri)
                    .into(wUserProfilePicture);

            Picasso.get()
                    .load(userUri)
                    .into(wNavImage);

        }

        wUsername.setText(username);
        wEmail.setText(email);

    }

    //    The method that will open the drawer layout
    public void clickMenu(View view) {
        wDrawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.toSearchNav:
                Intent backToSearch = new Intent(this, SearchActivity.class);
                backToSearch.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(backToSearch);
                finish();
                break;

            case R.id.toProfileNav:
                break;

            case R.id.toLogoutNav:
                logout();
                break;

        }

        wDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    //    Custom method for the user to logout
    private void logout() {

        FirebaseAuth.getInstance().signOut();
        Intent backToLogin = new Intent(ProfileActivity.this, LoginActivity.class);
        backToLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(backToLogin);
        finish();

    }

    //    For the side navigation drawer
    @Override
    public void onBackPressed() {

        if (wDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            wDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public void onClick(View view) {

        if (view == wEditProfilePicture) {

            Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (openCamera.resolveActivity(this.getPackageManager()) != null) {
                startActivityForResult(openCamera, Constants.REQUEST_IMAGE_CAPTURE);
            }

        }

        if (view == wUserProfilePicture) {

            Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (openCamera.resolveActivity(this.getPackageManager()) != null) {
                startActivityForResult(openCamera, Constants.REQUEST_IMAGE_CAPTURE);
            }

        }

        if (view == wUsername) {

            Intent editUsername = new Intent(this, EditActivity.class);
            editUsername.putExtra(Constants.TO_EDIT, Constants.TO_EDIT_USERNAME);
            startActivity(editUsername);

        }

        if (view == wEmail) {

            Intent editEmail = new Intent(this, EditActivity.class);
            editEmail.putExtra(Constants.TO_EDIT, Constants.TO_EDIT_EMAIL);
            startActivity(editEmail);

        }

        if (view == wNavImage) {
            wDrawerLayout.closeDrawer(GravityCompat.START);
        }

        if (view == wNavUsername) {
            wDrawerLayout.closeDrawer(GravityCompat.START);
        }

    }

    // Storing the image to display it
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            // Hide profile picture and show progress bar
            wUserProfilePicture.setVisibility(View.GONE);
            wProfileProgressBar.setVisibility(View.VISIBLE);

            Bundle bundleExtra = data.getExtras();
            Bitmap imageTaken = (Bitmap) bundleExtra.get("data");
            wUserProfilePicture.setImageBitmap(imageTaken);

            // Save the photo to firebase
            savePhotoToFirebase(imageTaken);

        }
    }

    // Method init to save profile photo
    private void savePhotoToFirebase(Bitmap userProfilePicture) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        userProfilePicture.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        // User data
        String username = currentUser.getDisplayName();
        String uId = currentUser.getUid();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("user_profile_pictures")
                .child(username)
                .child(uId + ".jpeg");

        storageReference.putBytes(byteArrayOutputStream.toByteArray())
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {

                            // Download the picture URI
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    // Method to set the new profile picture
                                    setProfilePicture(uri);

                                }
                            });


                        } else {

                            wProfileProgressBar.setVisibility(View.GONE);
                            wUserProfilePicture.setVisibility(View.VISIBLE);
                            Picasso.get().load(R.drawable.user_profile_picture)
                                    .resize(100, 100)
                                    .centerCrop()
                                    .into(wUserProfilePicture);
                            String error = "Could not update the profile picture";
                            Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void setProfilePicture(Uri photoUri) {

        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                .setPhotoUri(photoUri)
                .build();

        currentUser.updateProfile(userProfileChangeRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        // Hide progress bar and show profile picture
                        wProfileProgressBar.setVisibility(View.GONE);
                        wUserProfilePicture.setVisibility(View.VISIBLE);

                        if (task.isSuccessful()) {

                            String success = "Profile picture updated";
                            Toast.makeText(ProfileActivity.this, success, Toast.LENGTH_SHORT).show();

                            // Set new image to the nav view
                            Picasso.get()
                                    .load(currentUser.getPhotoUrl())
                                    .into(wNavImage);

                        } else {

                            wProfileProgressBar.setVisibility(View.GONE);
                            wUserProfilePicture.setVisibility(View.VISIBLE);
                            Picasso.get().load(R.drawable.user_profile_picture)
                                    .resize(100, 100)
                                    .centerCrop()
                                    .into(wUserProfilePicture);
                            String error = "Could not update the profile picture";
                            Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}