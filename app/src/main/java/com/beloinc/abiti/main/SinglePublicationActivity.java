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
import com.bumptech.glide.Glide;


public class SinglePublicationActivity extends AppCompatActivity {

    private static final String TAG = "SinglePublicationActivi";

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

        // GET INFORMATION PASSED BY MAIN ACTIVITY
        String photoLeftUrl = intent.getExtras().getString(MainActivity.GET_LEFT_URL);
        String photoRightUrl = intent.getExtras().getString(MainActivity.GET_RIGHT_URL);
        String leftDescription = intent.getExtras().getString(MainActivity.GET_LEFT_DESCRIPTION);
        String rightDescription = intent.getExtras().getString(MainActivity.GET_RIGHT_DESCRIPTION);

        setupWidgets();
        setupWidgetsClickListener(new WidgetClickListener());

        Glide.with(mLeftImage.getContext())
                .load(photoLeftUrl)
                .into(mLeftImage);

        Glide.with(mRightImage.getContext())
                .load(photoRightUrl)
                .into(mRightImage);

        mLeftDescription.setText(leftDescription);
        mRightDescription.setText(rightDescription);
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
