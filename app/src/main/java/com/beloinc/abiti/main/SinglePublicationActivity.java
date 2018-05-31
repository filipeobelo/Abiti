package com.beloinc.abiti.main;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.beloinc.abiti.R;
import com.beloinc.abiti.upload.UploadActivity;
import com.beloinc.abiti.utils.PhotosCloudDatabase;
import com.bumptech.glide.Glide;


public class SinglePublicationActivity extends AppCompatActivity {

    private static final String TAG = "SinglePublicationActivity";
    //CONSTANT TO REFERENCE AT INTENT
    public static final String PHOTO_OBJECT = "photo_object";

    private Context mContext;
    private ImageView mLeftImage;
    private ImageView mRightImage;
    private TextView mLeftDescription;
    private TextView mRightDescription;
    private Button mButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_publication);

        mContext = SinglePublicationActivity.this;

        Intent intent = getIntent();
        PhotosCloudDatabase photosCloudDatabase = (PhotosCloudDatabase) intent.getSerializableExtra(PHOTO_OBJECT);

        setupWidgets();
        setupWidgetsClickListener(new WidgetClickListener());
        updateWidgets(photosCloudDatabase);

    }

    private void setupWidgets() {
        mLeftImage = findViewById(R.id.image_left);
        mRightImage = findViewById(R.id.image_right);
        mLeftDescription = findViewById(R.id.text_left);
        mRightDescription = findViewById(R.id.text_right);
        mButton = findViewById(R.id.new_content);
    }

    private void setupWidgetsClickListener(WidgetClickListener clickListener) {
        mButton.setOnClickListener(clickListener);
    }

    private void updateWidgets(PhotosCloudDatabase photosCloudDatabase) {
        Glide.with(mLeftImage.getContext())
                .load(photosCloudDatabase.getPhotoUrls().get("leftUrl"))
                .into(mLeftImage);

        Glide.with(mRightImage.getContext())
                .load(photosCloudDatabase.getPhotoUrls().get("rightUrl"))
                .into(mRightImage);

        mLeftDescription.setText(photosCloudDatabase.getDescription().get("leftDescription"));
        mRightDescription.setText(photosCloudDatabase.getDescription().get("rightDescription"));
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
