package com.example.practiceapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.room.Room;

import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
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
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.practiceapp.R;
import com.example.practiceapp.storage.UserDao;
import com.example.practiceapp.storage.UserDataBase;
import com.example.practiceapp.storage.UserEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.List;

import static com.example.practiceapp.R.layout.layout_add_post;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView textView ;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private FloatingActionButton fabButton;
    private Dialog addPostDialog;
    private String addedDescription;

    private ImageView profileImageView, userPostImageView, postImageView, editPostImageView;
    ProgressBar progressBar;
    private  Uri imageUri;
    private FirebaseUser user;
    private EditText titleTextView,descriptionTextView;
    private TextView headerName, emailAddress;
    private Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                editPostImageView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
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
