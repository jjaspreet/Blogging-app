package com.example.practiceapp.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.example.practiceapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.practiceapp.activities.RegisterActivity.REQUEST_CODE_FOR_GALLERY;
import static com.example.practiceapp.activities.RegisterActivity.REQUEST_CODE_FOR_PERMISSION;

public class LoginActivity extends AppCompatActivity {



    private EditText emailEditTextView, passwordEditTextView;

    private FirebaseAuth mAuth;
    private ImageView profileImageView;
    private Uri pickedImageUri;
    private FirebaseUser firebaseUser;

    private ProgressBar progressBar;
    private int isShowRationaleForTheFirstTym = 0;

    private TextView signInTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditTextView = findViewById(R.id.emailEditTextView);
        passwordEditTextView = findViewById(R.id.passwordEditTextView);

        final Button loginButton = findViewById(R.id.loginButton);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser  = mAuth.getCurrentUser();

        profileImageView = findViewById(R.id.profileImageView);

        progressBar = findViewById(R.id.progressBar);
        signInTextView = findViewById(R.id.signInTextView);

        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToRegisterActivity();
            }
        });



        profileImageView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                checkForPermissions();
            }
        });



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateFields()){
                    String email = emailEditTextView.getText().toString();
                    String password = passwordEditTextView.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    loginButton.setVisibility(View.GONE);

                    mAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if(task.isSuccessful()){
                                        progressBar.setVisibility(View.GONE);
                                        loginButton.setVisibility(View.VISIBLE);
                                        showMessage("User logged in successfully");
                                        startHomeActivity();

                                    }
                                    else{

                                        progressBar.setVisibility(View.GONE);
                                        loginButton.setVisibility(View.VISIBLE);
                                        showMessage(task.getException().toString());
                                    }
                                }
                            });

                }


            }

        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkForPermissions() {
        if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            openGallery();

        } else {
            askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_CODE_FOR_PERMISSION);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(LoginActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{permission}, requestCode);
                boolean showRationale  = true;

                if(++ isShowRationaleForTheFirstTym >1){
                    showRationale= shouldShowRequestPermissionRationale( permission );
                    if(!showRationale){
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
            }
        }
    }


    private boolean validateFields(){
        emailEditTextView.setError(null);
        passwordEditTextView.setError(null);

        String email = emailEditTextView.getText().toString();
        String password = passwordEditTextView.getText().toString();

        if(TextUtils.isEmpty(email)){
            emailEditTextView.setError("Mandatory Field");
            return false;
        }

        if(TextUtils.isEmpty(password)){
            passwordEditTextView.setError("Mandatory Fields");
            return false;
        }

        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void startHomeActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    private void showMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void moveToRegisterActivity(){
        startActivity(new Intent(this, RegisterActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode== REQUEST_CODE_FOR_PERMISSION && grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            openGallery();
        }
    }
}
