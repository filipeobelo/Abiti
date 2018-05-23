package com.beloinc.abiti.main;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.beloinc.abiti.R;
import com.beloinc.abiti.upload.UploadActivity;
import com.beloinc.abiti.utils.PhotosDatabase;

public class MainActivity extends AppCompatActivity implements RecyclerGridFragment.OnPublicationSelected {

    //TAG TO TRACK LOG
    private static final String TAG = "MainActivity";

    //CONSTANTS TO REFERENCE AT INTENT
    public static final String GET_LEFT_DESCRIPTION = "leftDescription";
    public static final String GET_RIGHT_DESCRIPTION = "rightDescription";
    public static final String GET_LEFT_URL = "photoLeftUrl";
    public static final String GET_RIGHT_URL = "photoRightUrl";
    public static final String GET_TAG = "tag";

    private Context mContext;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: activity created");

        mContext = MainActivity.this;

        RecyclerGridFragment gridFragment = new RecyclerGridFragment();

        //USING FRAGMENT TO ADD FUTURE FUNCTIONALITY
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, gridFragment)
                .commit();

        setupWidgets();
        setupWidgetsClickListener(new WidgetClickListener());
    }

    private void setupWidgets() {
        mButton = findViewById(R.id.new_content);
    }

    private void setupWidgetsClickListener(WidgetClickListener clickListener) {
        mButton.setOnClickListener(clickListener);                          // HANDLE CLICK TO GO TO CREATION OF NEW PUBLICATION
    }


    @Override
    public void onPublicationSelected(PhotosDatabase publication) {        // METHOD FROM INNER INTERFACE AT RECYCLERGRIDFRAGMENT TO HANDLE CLICKS AT A SINGLE PUBLICATION
        Intent intent = new Intent(mContext, SinglePublicationActivity.class);
        intent.putExtra(GET_LEFT_DESCRIPTION, publication.getLeftDescription());
        intent.putExtra(GET_RIGHT_DESCRIPTION, publication.getRightDescription());
        intent.putExtra(GET_LEFT_URL, publication.getPhotoLeftUrl());
        intent.putExtra(GET_RIGHT_URL, publication.getPhotoRightUrl());
        intent.putExtra(GET_TAG, publication.getTag());
        startActivity(intent);
    }

    private class WidgetClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.new_content:
                    Log.d(TAG, "onClick: new publication button clicked");
                    Intent intent = new Intent(mContext, UploadActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }
}
