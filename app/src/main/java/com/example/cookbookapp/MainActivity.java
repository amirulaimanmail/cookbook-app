package com.example.cookbookapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actv_main);  // Create a layout for this activity

        // Initialize views
        imageView = findViewById(R.id.imageView);
        nameTextView = findViewById(R.id.name_tv);
        emailTextView = findViewById(R.id.email_tv);
        updateProfileButton = findViewById(R.id.button_update);

        loadData();

        updateProfileButton.setOnClickListener(v ->{
            Intent intent = new Intent(MainActivity.this, SubmitActivity.class);

            intent.putExtra("photoUri", photoUriString);  // Send the photo URI
            intent.putExtra("name", name);  // Use the updated name
            intent.putExtra("email", email);  // Use the updated email

            startActivityForResult(intent, 1);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Get the data sent back from SubmitActivity
            photoUriString = data.getStringExtra("photoUri");
            name = data.getStringExtra("name");
            email = data.getStringExtra("email");

            saveData();
            displayData();
        }
        else{
            Toast.makeText(this, "Cancel update profile.", Toast.LENGTH_SHORT).show();
        }
    }

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
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
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
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Retrieve the saved data
        photoUriString = sharedPreferences.getString(PREF_PHOTO_URI, null);  // If no photo, return null
        name = sharedPreferences.getString(PREF_NAME, null);  // If no name, return null
        email = sharedPreferences.getString(PREF_EMAIL, null);  // If no email, return null

        // Display the data (update UI)
        displayData();
    }
}

