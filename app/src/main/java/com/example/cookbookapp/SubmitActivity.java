package com.example.cookbookapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SubmitActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;

    private static final int REQUEST_CODE = 22;
    Button camButton, delButton, submitButton;
    ImageView imageView;
    EditText name_input, email_input;

    String getName,getEmail,getPhotoUri;

    private Uri photoUri;  // This will hold the URI of the temporary file


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actv_updateprofile);

        camButton = findViewById(R.id.button_update);
        imageView = findViewById(R.id.imageView);
        submitButton = findViewById(R.id.buttonSubmit);
        //delButton = findViewById(R.id.buttondelete);

        name_input = findViewById(R.id.name_input);
        email_input = findViewById(R.id.email_input);

        loadData();
        displayData();

        camButton.setOnClickListener(v -> {
            showCustomDialog();
        });

        submitButton.setOnClickListener(v -> {
            // Get the text input from EditText fields
            String name = name_input.getText().toString().trim();
            String email = email_input.getText().toString().trim();

            // Check if the fields are not empty
            if (name.isEmpty()) {
                name_input.setError("Name cannot be empty");
                return;  // Prevent proceeding if the name is empty
            }

            if (email.isEmpty()) {
                email_input.setError("Email cannot be empty");
                return;  // Prevent proceeding if the email is empty
            }

            if (photoUri == null) {
                Toast.makeText(SubmitActivity.this, "Please capture or select a photo", Toast.LENGTH_SHORT).show();
                return;  // Prevent proceeding if the photo is not selected
            }

            // Send the updated data (photoUri, name, email, etc.)
            Intent resultIntent = new Intent();

            resultIntent.putExtra("photoUri", photoUri.toString());  // Send the photo URI
            resultIntent.putExtra("name", name);  // Use the updated name
            resultIntent.putExtra("email", email);  // Use the updated email

            // Set the result and finish the activity
            setResult(RESULT_OK, resultIntent);
            finish();  // Close the SubmitActivity and return to MainActivity
        });


//            delButton.setOnClickListener(v -> {
//                clearImageDirectory();
//            });
    }

    //create a temp file with name
    private File createImageFile() throws IOException {
        // Create a unique file name using the current timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(null);  // Get app-specific directory
        File imageFile = File.createTempFile(
                imageFileName,  // Prefix
                ".jpg",         // Suffix
                storageDir      // Directory
        );
        return imageFile;
    }

    private void loadImage() {
        if (photoUri != null) {
            Glide.with(this)
                    .load(photoUri)  // Load the image URI
                    .into(imageView);  // Set the ImageView to load the image into
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the request code matches and result is okay
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                // If the image was picked from the gallery, retrieve URI and copy it
                Uri selectedImageUri = data.getData();
                photoUri = copyImageToAppDirectory(selectedImageUri);

                if (photoUri != null) {
                    loadImage();
                }
            } else {
                // Handle camera image - the photoUri should already be set when starting camera
                loadImage();
            }
        } else {
            Toast.makeText(this, "Failed to capture image.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (photoUri != null) {
            outState.putString("photoUri", photoUri.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("photoUri")) {
            photoUri = Uri.parse(savedInstanceState.getString("photoUri"));
            loadImage();
        }
    }

    public void clearImageDirectory() {
        // Get the directory where images are saved
        File directory = getExternalFilesDir(null);

        if (directory != null && directory.isDirectory()) {
            File[] files = directory.listFiles();  // Get all files in the directory

            if (files != null && files.length > 0) {
                for (File file : files) {
                    // Log the name of each file found in the directory
                    Log.d("ClearImageDirectory", "Found file: " + file.getName());

                    if (file.isFile() && file.getName().endsWith(".jpg")) {  // Ensure only image files are deleted
                        boolean deleted = file.delete();

                        // Log the deletion attempt result
                        if (deleted) {
                            Log.d("ClearImageDirectory", "Deleted file: " + file.getName());
                        } else {
                            Log.d("ClearImageDirectory", "Failed to delete file: " + file.getName());
                            Toast.makeText(this, "Failed to delete." + file.getName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                // If no files were deleted, inform the user
                if (files.length == 0) {
                    Toast.makeText(this, "No image files to delete.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Images deletion process complete.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No files found in directory.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Directory not found or not a directory.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showCustomDialog() {
        if (checkPermissions()) {
            openCameraDialog();
        } else {
            requestPermissions();
        }
    }

    private Uri copyImageToAppDirectory(Uri imageUri) {
        try {
            // Open an input stream for the selected image
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            // Create a temporary file in the app's directory
            File storageDir = getExternalFilesDir(null);
            if (storageDir == null) {
                Toast.makeText(this, "Directory not accessible", Toast.LENGTH_SHORT).show();
                return null;
            }

            File tempFile = new File(storageDir, "image_" + System.currentTimeMillis() + ".jpg");
            OutputStream outputStream = new FileOutputStream(tempFile);

            // Copy the image content to the new file
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            // Return the URI of the copied file
            return Uri.fromFile(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving image.", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void loadData(){
        Intent getIntent = getIntent();
        getName = getIntent.getStringExtra("name");
        getEmail = getIntent.getStringExtra("email");
        getPhotoUri = getIntent.getStringExtra("photoUri");

        if(getPhotoUri != null){
            photoUri = Uri.parse(getPhotoUri);
        }
    }

    private void displayData(){
        if(getName != null){
            name_input.setText(getName);
        }
        if(getEmail != null){
            email_input.setText(getEmail);
        }
        if(photoUri != null){
            loadImage();
        }
    }

    //permissions
    private boolean checkPermissions() {
        int cameraPermission = checkSelfPermission(android.Manifest.permission.CAMERA);
        int writePermission = checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return cameraPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        requestPermissions(new String[]{
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCameraDialog();
            } else {
                Toast.makeText(this, "Camera and storage permissions are required to use the camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCameraDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_imagemethod2, null);

        builder.setView(dialogView);

        Button buttonCamera = dialogView.findViewById(R.id.buttonCamera);
        Button buttonGallery = dialogView.findViewById(R.id.buttonGallery);

        AlertDialog alertDialog = builder.create();

        buttonCamera.setOnClickListener(v -> {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(SubmitActivity.this, "Error creating image file", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
                Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                camIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                if (camIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(camIntent, REQUEST_CODE);
                } else {
                    Toast.makeText(this, "No camera app found!", Toast.LENGTH_SHORT).show();
                }
            }
            alertDialog.dismiss();
        });

        buttonGallery.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, REQUEST_CODE);
            alertDialog.dismiss();
        });

        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.alert_dialog_bg_1);
        alertDialog.show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85);
        alertDialog.getWindow().setAttributes(layoutParams);
    }


}
