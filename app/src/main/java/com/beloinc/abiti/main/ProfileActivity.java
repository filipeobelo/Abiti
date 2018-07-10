package com.beloinc.abiti.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.beloinc.abiti.R;
import com.beloinc.abiti.utils.PhotosCloudDatabase;
import com.beloinc.abiti.utils.RecyclerGridFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity implements RecyclerGridFragment.OnPublicationSelected {
    private static final String TAG = "ProfileActivity";

    private Context mContext = ProfileActivity.this;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkLogin();
        setContentView(R.layout.activity_profile);

        setRecyclerFeed();

        setToolbar();

        findViewById(R.id.back_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void checkLogin() {
        Log.d(TAG, "checkLogin: ");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            Log.d(TAG, "checkLogin: user signed out");
            finish();
        } else {
            Log.d(TAG, "checkLogin: user signed in");
            userId = firebaseUser.getUid();
        }
    }

    private void setRecyclerFeed() {
        Fragment grid = getSupportFragmentManager().findFragmentByTag("grid");
        if (grid == null) {
            RecyclerGridFragment gridFragment = new RecyclerGridFragment();
            gridFragment.setPath("/users/" + userId + "/userPublications");
            //2 COLUMNS RECYCLERVIEW
            gridFragment.setSpanCount(2);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, gridFragment, "grid")
                    .commit();
        }
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onPublicationSelected(PhotosCloudDatabase publication) {
        Intent intent = new Intent(mContext, SinglePublicationActivity.class);
        intent.putExtra(SinglePublicationActivity.PHOTO_OBJECT, publication);
        startActivity(intent);
    }

}
