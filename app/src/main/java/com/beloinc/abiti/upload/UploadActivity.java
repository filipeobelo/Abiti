package com.beloinc.abiti.upload;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.beloinc.abiti.R;
import com.beloinc.abiti.utils.PhotosDatabase;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class UploadActivity extends AppCompatActivity {

    private static final String TAG = "UploadActivity";

    public static final int RC_PHOTO_PICKER_LEFT = 1;
    private static final int RC_PHOTO_PICKER_RIGHT = 2;

    private ImageView mLeftImage;
    private ImageView mRightImage;
    private EditText mLeftText;
    private EditText mRightText;
    private EditText mTagsText;
    private Button mUploadButton;
    private ProgressBar mProgressBar;

    private String mLeftDescription;
    private String mRightDescription;
    private String mTags;

    //Firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference mPhotosUrlReference;
    private FirebaseStorage mStorage;

    private Uri selectedLeftUrl;
    private Uri selectedRightUrl;
    private Uri downloadLeftUrl;
    private Uri downloadRightUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        Log.d(TAG, "onCreate: started upload activity");

        mStorage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        mPhotosUrlReference = mDatabase.getReference().child("user_uploads");

        setupWidgets();
        setupWidgetsClickListener(new WidgetClickListener());

        mProgressBar.setVisibility(View.GONE);
    }

    private void setupWidgets() {
        mLeftImage = findViewById(R.id.image_left);
        mRightImage = findViewById(R.id.image_right);
        mLeftText = findViewById(R.id.text_left);
        mRightText = findViewById(R.id.text_right);
        mUploadButton = findViewById(R.id.upload_button);
        mProgressBar = findViewById(R.id.progress_bar);
        mTagsText = findViewById(R.id.upload_tags);
    }

    private void setupWidgetsClickListener(WidgetClickListener clickListener) {
        mLeftImage.setOnClickListener(clickListener);
        mRightImage.setOnClickListener(clickListener);
        mUploadButton.setOnClickListener(clickListener);
    }


    private class WidgetClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                //click on left image
                case R.id.image_left:
                    Log.d(TAG, "onClick: left image");
                    Intent intentLeft = new Intent(Intent.ACTION_GET_CONTENT);
                    intentLeft.setType("image/jpeg");
                    intentLeft.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(Intent.createChooser(intentLeft, "Complete action using"), RC_PHOTO_PICKER_LEFT);
                    break;

                // click on right image
                case R.id.image_right:
                    Log.d(TAG, "onClick: right image");
                    Intent intentRight = new Intent(Intent.ACTION_GET_CONTENT);
                    intentRight.setType("image/jpeg");
                    intentRight.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(Intent.createChooser(intentRight, "Complete action using"), RC_PHOTO_PICKER_RIGHT);
                    break;

                //click on upload button
                case R.id.upload_button:
                    Log.d(TAG, "onClick: upload button");
                    getEditTexts();
                    if (selectedLeftUrl == null || selectedRightUrl == null) {
                        Toast.makeText(UploadActivity.this, "Não esqueça de selecionar as duas fotos :)", Toast.LENGTH_SHORT).show();
                    } else if (selectedLeftUrl == selectedRightUrl) {
                        Toast.makeText(UploadActivity.this, "Escolha imagens diferentes para publicar!!", Toast.LENGTH_SHORT).show();
                    } else if (mLeftDescription.isEmpty() || mRightDescription.isEmpty()) {
                        Toast.makeText(UploadActivity.this, "Faça uma descrição de cada foto!!", Toast.LENGTH_SHORT).show();
                    } else if (mTags.isEmpty()) {
                        Toast.makeText(UploadActivity.this, "Coloque hashtags para melhor identifcar sua publicação", Toast.LENGTH_SHORT).show();
                    } else {
                        uploadHelper();
                    }
                    break;
            }
        }
    }


    /*private class WidgetsClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                //click on left image
                case R.id.image_left:
                    Log.d(TAG, "onClick: left image");
                    Intent intentLeft = new Intent(Intent.ACTION_GET_CONTENT);
                    intentLeft.setType("image/jpeg");
                    intentLeft.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(Intent.createChooser(intentLeft, "Complete action using"), RC_PHOTO_PICKER_LEFT);
                    break;

                // click on right image
                case R.id.image_right:
                    Log.d(TAG, "onClick: right image");
                    Intent intentRight = new Intent(Intent.ACTION_GET_CONTENT);
                    intentRight.setType("image/jpeg");
                    intentRight.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(Intent.createChooser(intentRight, "Complete action using"), RC_PHOTO_PICKER_RIGHT);
                    break;

                //click on upload button
                case R.id.upload_button:
                    Log.d(TAG, "onClick: upload button");
                    getEditTexts();
                    if (selectedLeftUrl == null || selectedRightUrl == null) {
                        Toast.makeText(UploadActivity.this, "Não esqueça de selecionar as duas fotos :)", Toast.LENGTH_SHORT).show();
                    } else if (mLeftDescription.isEmpty() || mRightDescription.isEmpty()) {
                        Toast.makeText(UploadActivity.this, "Faça uma descrição de cada foto!!", Toast.LENGTH_SHORT).show();
                    } else if (mTags.isEmpty()) {
                        Toast.makeText(UploadActivity.this, "Coloque hashtags para melhor identifcar sua publicação", Toast.LENGTH_SHORT).show();
                    } else {
                        uploadHelper();
                    }
                    break;
            }
        }

    }*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER_LEFT && resultCode == RESULT_OK) {
            selectedLeftUrl = data.getData();
            Glide.with(UploadActivity.this)
                    .load(selectedLeftUrl)
                    .into(mLeftImage);
        } else if (requestCode == RC_PHOTO_PICKER_RIGHT && resultCode == RESULT_OK) {
            selectedRightUrl = data.getData();
            Glide.with(UploadActivity.this)
                    .load(selectedRightUrl)
                    .into(mRightImage);
        }
    }


    private void getEditTexts() {
        mLeftDescription = mLeftText.getText().toString();
        mRightDescription = mRightText.getText().toString();
        mTags = mTagsText.getText().toString();
    }


    private void uploadHelper() {
        String pathLeft = "user_photos/" + UUID.randomUUID();
        StorageReference mLeftPhotoReference = mStorage.getReference(pathLeft);
        mProgressBar.setVisibility(View.VISIBLE);
        mUploadButton.setEnabled(false);
        UploadTask uploadTaskLeft = mLeftPhotoReference.putFile(selectedLeftUrl);
        uploadTaskLeft.addOnSuccessListener(UploadActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onSuccess: left photo uploaded successfully");
                downloadLeftUrl = taskSnapshot.getDownloadUrl();

                Log.d(TAG, "onSuccess: starting right photo upload");

                //starting right photo upload
                String pathRight = "user_photos/" + UUID.randomUUID();
                StorageReference mRightPhotoReference = mStorage.getReference(pathRight);
                UploadTask uploadTaskRight = mRightPhotoReference.putFile(selectedRightUrl);
                uploadTaskRight.addOnSuccessListener(UploadActivity.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: right photo uploaded succesfully");
                        downloadRightUrl = taskSnapshot.getDownloadUrl();
                        PhotosDatabase photosDatabase = new PhotosDatabase(downloadLeftUrl.toString(), downloadRightUrl.toString(), mTags, mLeftDescription, mRightDescription);
                        mPhotosUrlReference.push().setValue(photosDatabase);
                        mProgressBar.setVisibility(View.GONE);
                        mUploadButton.setEnabled(true);
                    }
                });
            }
        });
    }
}
