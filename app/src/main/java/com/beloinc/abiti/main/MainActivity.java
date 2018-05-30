package com.beloinc.abiti.main;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.beloinc.abiti.R;
import com.beloinc.abiti.upload.UploadActivity;
import com.beloinc.abiti.utils.PhotosDatabase;
import com.beloinc.abiti.utils.RecyclerGridFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements RecyclerGridFragment.OnPublicationSelected {

    //TAG TO TRACK LOG
    private static final String TAG = "MainActivity";

    //FIREBASE UI
    private static final int RC_SIGN_IN = 123;

    //CONSTANTS TO REFERENCE AT INTENT
    public static final String GET_LEFT_DESCRIPTION = "leftDescription";
    public static final String GET_RIGHT_DESCRIPTION = "rightDescription";
    public static final String GET_LEFT_URL = "photoLeftUrl";
    public static final String GET_RIGHT_URL = "photoRightUrl";
    public static final String GET_TAG = "tag";

    //FIREBASE
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mFirebaseAuth;
    private String userId;

    private Context mContext;
    private Button mButton;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: activity created");

        mContext = MainActivity.this;
        mFirebaseAuth = FirebaseAuth.getInstance();

        setupWidgets();
        setupWidgetsClickListener(new WidgetClickListener());

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFireBaseUser = firebaseAuth.getCurrentUser();
                if (mFireBaseUser != null) {
                    //user signed in
                    onSignedInInitialize(mFireBaseUser.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged: user not signed in, starting login activity");
                    //user signed out
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(MainActivity.this, "User signed in", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    private void onSignedOutCleanup() {
        //clean fragments TBD
    }

    private void onSignedInInitialize(String userId) {
        this.userId = userId;
        Fragment grid = getSupportFragmentManager().findFragmentByTag("grid");
        if (grid == null) {
            RecyclerGridFragment gridFragment = new RecyclerGridFragment();

            //USING FRAGMENT TO ADD FUTURE FUNCTIONALITY
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, gridFragment, "grid")
                    .commit();
        }
    }

    private void setupWidgets() {
        //mButton = findViewById(R.id.new_content);
        fab = findViewById(R.id.fab);
    }

    private void setupWidgetsClickListener(WidgetClickListener clickListener) {
        // HANDLE CLICK TO GO TO CREATION OF NEW PUBLICATION
        //mButton.setOnClickListener(clickListener);
        fab.setOnClickListener(clickListener);
    }


    // METHOD FROM INNER INTERFACE AT RECYCLERGRIDFRAGMENT TO HANDLE CLICKS AT A SINGLE PUBLICATION
    @Override
    public void onPublicationSelected(PhotosDatabase publication) {
        Intent intent = new Intent(mContext, SinglePublicationActivity.class);
        intent.putExtra(GET_LEFT_DESCRIPTION, publication.getLeftDescription());
        intent.putExtra(GET_RIGHT_DESCRIPTION, publication.getRightDescription());
        intent.putExtra(GET_LEFT_URL, publication.getPhotoLeftUrl());
        intent.putExtra(GET_RIGHT_URL, publication.getPhotoRightUrl());
        startActivity(intent);
    }

    private class WidgetClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fab:
                    Log.d(TAG, "onClick: new publication fab clicked");
                    Intent intent = new Intent(mContext, UploadActivity.class);
                    intent.putExtra("uid", userId);
                    startActivity(intent);
                    break;
            }
        }
    }
}
