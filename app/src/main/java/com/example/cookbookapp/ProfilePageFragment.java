package com.example.cookbookapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class ProfilePageFragment extends Fragment {

    private ImageView imageView;
    private TextView nameTextView, emailTextView;
    Button updateProfileButton;
    String photoUriString;
    String name;
    String email;

    private static final String PREFS_NAME = "UserProfilePrefs";
    private static final String PREF_PHOTO_URI = "photoUri";
    private static final String PREF_NAME = "name";
    private static final String PREF_EMAIL = "email";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_page, container, false);

        imageView = view.findViewById(R.id.imageView);
        nameTextView = view.findViewById(R.id.name_tv);
        emailTextView = view.findViewById(R.id.email_tv);
        updateProfileButton = view.findViewById(R.id.button_update);

        loadData();

        updateProfileButton.setOnClickListener(v ->{
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);

            intent.putExtra("photoUri", photoUriString);  // Send the photo URI
            intent.putExtra("name", name);  // Use the updated name
            intent.putExtra("email", email);  // Use the updated email

            editProfileLauncher.launch(intent);
        });

        return view;
    }

        private final ActivityResultLauncher<Intent> editProfileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    photoUriString = data.getStringExtra("photoUri");
                    name = data.getStringExtra("name");
                    email = data.getStringExtra("email");

                    saveData();
                    displayData();
                } else {
                    Toast.makeText(getContext(), "Cancel update profile.", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private void displayData(){
        if (photoUriString != null) {
            Uri photoUri = Uri.parse(photoUriString);
            Glide.with(this)
                    .load(photoUri)  // Load the image URI
                    .into(imageView);  // Set the ImageView to load the image into
        }

        if (name != null && email != null) {
            nameTextView.setText(name);  // Update the name
            emailTextView.setText(email);  // Update the email
        }
    }

    private void saveData() {
        // Get SharedPreferences instance
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save data to SharedPreferences
        editor.putString(PREF_PHOTO_URI, photoUriString); // Save the photo URI string
        editor.putString(PREF_NAME, name);  // Save the name
        editor.putString(PREF_EMAIL, email); // Save the email

        // Apply changes asynchronously
        editor.apply();
    }

    private void loadData() {
        // Get SharedPreferences instance
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, getActivity().MODE_PRIVATE);

        // Retrieve the saved data
        photoUriString = sharedPreferences.getString(PREF_PHOTO_URI, null);  // If no photo, return null
        name = sharedPreferences.getString(PREF_NAME, null);  // If no name, return null
        email = sharedPreferences.getString(PREF_EMAIL, null);  // If no email, return null

        // Display the data (update UI)
        displayData();
    }
}


