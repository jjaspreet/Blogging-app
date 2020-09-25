package com.example.practiceapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.practiceapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_FOR_PERMISSION = 1;
    public static final int REQUEST_CODE_FOR_GALLERY = 2;
    public static final String TAG = "MainActivity";



    private ImageView profileImageView;
    private Button registerButton;
    private Uri pickedImageUri;
    private int isShowRationaleForTheFirstTym = 0;
    private EditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    private FirebaseStorage firebaseStorage;
    private boolean isPickedFromPhone =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        profileImageView = findViewById(R.id.profileImageView);
        nameEditText = findViewById(R.id.nameEditTextView);
        emailEditText = findViewById(R.id.emailEditTextView);
        passwordEditText = findViewById(R.id.passwordEditTextView);
        confirmPasswordEditText = findViewById(R.id.confirmditTextView);
        registerButton = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseStorage = FirebaseStorage.getInstance();



        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateFields()){
                    registerButton.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);

                    String email = emailEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    storeDataToFirebase(email, password);
                }
            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                checkForPermissions();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkForPermissions() {
        if (ActivityCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            openGallery();

        } else {
            askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_CODE_FOR_PERMISSION);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (!ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{permission}, requestCode);


            } else {

                Log.d(TAG, "askForPermission: " + permission);

                ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{permission}, requestCode);
                boolean showRationale = true;
                if (++isShowRationaleForTheFirstTym > 1) {
                    showRationale = shouldShowRequestPermissionRationale(permission);
                    if (!showRationale) {
                        new AlertDialog.Builder(this)
                                .setTitle("PERMISSION DENIED")
                                .setMessage("Please allow the required permissions")

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Continue with delete operation
                                        dialog.cancel();
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.parse("package:" + getPackageName()));
                                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                }
            }
        } else {
            openGallery();
            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }


    private void openGallery(){
        Intent galleryIntent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent , REQUEST_CODE_FOR_GALLERY );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_FOR_GALLERY && resultCode == RESULT_OK){
            if(data != null){
               pickedImageUri = data.getData();
               profileImageView.setImageURI(pickedImageUri);
               isPickedFromPhone = true;
            }
        }
    }

    private boolean validateFields(){
        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        if(TextUtils.isEmpty(name)){
            nameEditText.setError("Mandatory fields");
            return false;
        }


        if(TextUtils.isEmpty(email)){
            emailEditText.setError("Mandatory fields");
            return false;
        }

        if(!emailValidator(email)){
            emailEditText.setError("Please enter valid email id");
            return false;
        }


        if(TextUtils.isEmpty(password)){
            passwordEditText.setError("Mandatory fields");
            return false;
        }


        if(TextUtils.isEmpty(confirmPassword)){
            confirmPasswordEditText.setError("Mandatory fields");
            return false;
        }

        if(profileImageView.getDrawable() == null){
            Toast.makeText(this, "Please select an post image", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(profileImageView.getDrawable() != getResources().getDrawable(R.drawable.no_profile_img)){
            Toast.makeText(this, "Please select an post image", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!password.equals(confirmPassword)){
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Please verify your password");

            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            })
                    .setNegativeButton(null, null);
            alertDialog.create();
            alertDialog.show();
            return false;
        }
        return true;

    }

    private void showMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void storeDataToFirebase(final String email, final String password){

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            showMessage("Account is created");

                            //Update the user profile picture to the firebase storage
                            updateUserInfo(email, password , nameEditText.getText().toString());


                        }else{
                           showMessage("Account creation failed due to " + task.getException());

                           registerButton.setVisibility(View.VISIBLE);
                           progressBar.setVisibility(View.GONE);
                        }
                    }
                });


    }

    //Update user photo and his name
    private void updateUserInfo(String email, String password, final String  name) {

       StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("users_photo");

       final StorageReference userPhoto = storageReference.child(pickedImageUri.getLastPathSegment());

       userPhoto.putFile(pickedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
           @Override
           public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

               //Image uploaded successfully
               userPhoto.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                   @Override
                   public void onSuccess(Uri uri) {

                       UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                               .setDisplayName(name)
                               .setPhotoUri(uri)
                               .build();

                       firebaseAuth.getCurrentUser().updateProfile(profileChangeRequest)
                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {

                                       if(task.isSuccessful()){
                                           showMessage("Registration completed successfully ");

                                           registerButton.setVisibility(View.VISIBLE);
                                           progressBar.setVisibility(View.GONE);

                                           moveToHomeActivity();
                                       }
                                       else{
                                           registerButton.setVisibility(View.VISIBLE);
                                           progressBar.setVisibility(View.GONE);
                                           showMessage("Registration Failed! Please try again ");
                                       }
                                   }
                               });

                   }
               });
           }
       });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode== REQUEST_CODE_FOR_PERMISSION && grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            openGallery();
        }
    }

    private void moveToHomeActivity(){
        startActivity(new Intent(this, HomeActivity.class));
    }

    public boolean emailValidator(String email)
    {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
