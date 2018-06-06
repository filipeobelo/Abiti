package com.beloinc.abiti.main;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.beloinc.abiti.R;
import com.beloinc.abiti.upload.UploadActivity;
import com.beloinc.abiti.utils.PhotosCloudDatabase;
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

    //FIREBASE
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mFirebaseAuth;
    private String userId;

    private Context mContext;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: activity created");

        mContext = MainActivity.this;
        mFirebaseAuth = FirebaseAuth.getInstance();

        setToolbar();

        setupWidgets();
        setupWidgetsClickListener(new WidgetClickListener());

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFireBaseUser = firebaseAuth.getCurrentUser();
                if (mFireBaseUser != null) {
                    Log.d(TAG, "onAuthStateChanged: user signed in");
                    setRecyclerFeed(mFireBaseUser.getUid());
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

    private void setRecyclerFeed(String userId) {
        this.userId = userId;
        Fragment grid = getSupportFragmentManager().findFragmentByTag("grid");
        if (grid == null) {
            RecyclerGridFragment gridFragment = new RecyclerGridFragment();
            gridFragment.setPath("/globalPublications");
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
        fab.setOnClickListener(clickListener);
    }


    // METHOD FROM INNER INTERFACE AT RECYCLERGRIDFRAGMENT TO HANDLE CLICKS AT A SINGLE PUBLICATION
    @Override
    public void onPublicationSelected(PhotosCloudDatabase publication) {
        Intent intent = new Intent(mContext, SinglePublicationActivity.class);
        intent.putExtra(SinglePublicationActivity.PHOTO_OBJECT, publication);
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

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_menu:
                Intent intent = new Intent(mContext, ProfileActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
