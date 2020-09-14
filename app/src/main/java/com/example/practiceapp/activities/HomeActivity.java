package com.example.practiceapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.practiceapp.R;
import com.example.practiceapp.adapters.PostAdapter;
import com.example.practiceapp.data.Post;
import com.example.practiceapp.storage.UserDao;
import com.example.practiceapp.storage.UserDataBase;
import com.example.practiceapp.storage.UserEntity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import static com.example.practiceapp.R.layout.layout_add_post;
import static com.example.practiceapp.activities.RegisterActivity.REQUEST_CODE_FOR_GALLERY;
import static com.example.practiceapp.activities.RegisterActivity.REQUEST_CODE_FOR_PERMISSION;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView textView ;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private FloatingActionButton fabButton;
    private Dialog addPostDialog;
    private String addedDescription;

    private ImageView profileImageView, userPostImageView, postImageView, editPostImageView;
    ProgressBar progressBar, listProgressBar;
    private  Uri imageUri;
    private FirebaseUser user;
    private EditText titleTextView,descriptionTextView;
    private TextView headerName, emailAddress;
    private Toolbar toolbar;
    private Uri postUri;
    private Uri pickedImageUri;
    private int isShowRationaleForTheFirstTym = 0;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<Post> postsList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.postRecyclerView);

        listProgressBar = findViewById(R.id.listProgressBar);

        firebaseDatabase  = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Posts");



        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        user = FirebaseAuth.getInstance().getCurrentUser();
        initPopup();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View hView =  navigationView.inflateHeaderView(R.layout.nav_header_main);

        headerName = hView.findViewById(R.id.headerNameTextView);
        emailAddress = hView.findViewById(R.id.headerEmailTextView);
        profileImageView = hView.findViewById(R.id.headerImageView);


        fabButton = findViewById(R.id.fabButton);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPostDialog.show();
                editPostImageView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

                titleTextView.setText("");
                descriptionTextView.setText("");
                postImageView.setImageResource(0);
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        toolbar.setTitle("Practice App");
        toolbar.setTitleTextColor(ContextCompat.getColor(this,android.R.color.white));

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_navigation_drawer, R.string.close_navigation_drawer);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();



        // init Description Dialog

        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_add_description, null);

        final EditText editText = (EditText) dialogView.findViewById(R.id.edt_comment);
        Button button1 = (Button) dialogView.findViewById(R.id.buttonSubmit);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addedDescription = editText.getText().toString();
                descriptionTextView.setText(addedDescription);
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);

        descriptionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.show();
            }
        });

        editPostImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validateFields()){

                    editPostImageView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    pushDataToFirebase();
                }
            }
        });




        if(user != null){
            String name = user.getDisplayName();
            imageUri = user.getPhotoUrl();
            String email = user.getEmail();

            headerName.setText(name.toUpperCase());
            emailAddress.setText(email);

            if(imageUri != null){
                Glide.with(this).load(imageUri).into(profileImageView);
            }else{
                Glide.with(this).load(R.drawable.no_profile_img).into(profileImageView);
//                Glide.with(this).load(R.drawable.no_profile_img).into(userPostImageView);
            }

        }

        listProgressBar.setVisibility(View.VISIBLE);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot postSnap: snapshot.getChildren()){
                    Post post = postSnap.getValue(Post.class);

                    postsList.add(post);

                }

                postAdapter = new PostAdapter(HomeActivity.this, postsList, new PostAdapter.PostClickListener() {
                    @Override
                    public void onPOstClick() {
                        Toast.makeText(HomeActivity.this, "Post Clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                listProgressBar.setVisibility(View.GONE);
                recyclerView.setAdapter(postAdapter);
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private boolean validateFields(){
        String title = titleTextView.getText().toString();
        String description = descriptionTextView.getText().toString();

        if(TextUtils.isEmpty(title)){
            Toast.makeText(this, "Please enter title", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(TextUtils.isEmpty(description)){
            Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(postImageView.getDrawable() == null){
            Toast.makeText(this, "Please select an post image", Toast.LENGTH_SHORT).show();
            return false;
        }

        return  true;
    }

    private void pushDataToFirebase(){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Post_Image");
        final StorageReference postImage = storageReference.child(pickedImageUri.getLastPathSegment());

        postImage.putFile(pickedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
              postImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                  @Override
                  public void onSuccess(Uri uri) {
                      String imageDownloadLink = uri.toString();


                      // create a post object and push it to the firebase
                      Post post = new Post(titleTextView.getText().toString(), descriptionTextView.getText().toString()
                      , imageDownloadLink, user.getUid(),
                              user.getPhotoUrl().toString());

                      addPost(post);
                  }
              });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                editPostImageView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void addPost(Post post){

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference databaseReference = firebaseDatabase.getReference().child("Posts").push();

        // get the post unique id

        String key = databaseReference.getKey();
        post.setUniqueKey(key);

        databaseReference.setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(HomeActivity.this, "Post added successfully", Toast.LENGTH_SHORT).show();
                editPostImageView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                addPostDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initPopup() {

        addPostDialog = new Dialog(this);
        addPostDialog.setContentView(R.layout.layout_add_post);
        addPostDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addPostDialog.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        addPostDialog.getWindow().getAttributes().gravity = Gravity.TOP;

        titleTextView = addPostDialog.findViewById(R.id.titleTextView);
        descriptionTextView = addPostDialog.findViewById(R.id.descriptionEditTextView);

        userPostImageView = addPostDialog.findViewById(R.id.userImageView);
        postImageView = addPostDialog.findViewById(R.id.postImageView);
        editPostImageView =addPostDialog.findViewById(R.id.editPostImageView);
        progressBar = addPostDialog.findViewById(R.id.progressBar);

        Glide.with(this).load(user.getPhotoUrl()).into(userPostImageView);

        postImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
           }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkForPermissions() {
        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            openGallery();

        } else {
            askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_CODE_FOR_PERMISSION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_FOR_GALLERY && resultCode == RESULT_OK){
            if(data != null){
                postUri = data.getData();
                postImageView.setImageURI(postUri);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(HomeActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{permission}, requestCode);
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
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}
