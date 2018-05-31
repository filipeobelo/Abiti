package com.beloinc.abiti.upload;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.beloinc.abiti.R;
import com.beloinc.abiti.main.SinglePublicationActivity;
import com.beloinc.abiti.utils.PhotosCloudDatabase;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {

    private static final String TAG = "UploadActivity";

    public static final int RC_PHOTO_PICKER_LEFT = 1;
    private static final int RC_PHOTO_PICKER_RIGHT = 2;

    private ImageView mLeftImage;
    private ImageView mRightImage;
    private EditText mLeftTextView;
    private EditText mRightTextView;
    private EditText mTagsTextView;
    private Button mUploadButton;
    private ProgressBar mProgressBar;

    private String userId;
    private String mLeftDescription;
    private String mRightDescription;
    private String[] mTags;
    private List<String> mTagsList = new ArrayList<>();
    private Map<String, String> description = new HashMap<>();
    private Map<String, String> photoUrls = new HashMap<>();

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

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Log.d(TAG, "onCreate: started upload activity. UID: " + userId);


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
        mLeftTextView = findViewById(R.id.text_left);
        mRightTextView = findViewById(R.id.text_right);
        mUploadButton = findViewById(R.id.upload_button);
        mProgressBar = findViewById(R.id.progress_bar);
        mTagsTextView = findViewById(R.id.upload_tags);
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
                    if (selectedLeftUrl == null || selectedRightUrl == null) {
                        Toast.makeText(UploadActivity.this, "Não esqueça de selecionar as duas fotos :)", Toast.LENGTH_SHORT).show();
                        break;
                    } else if (getEditTexts()) {
                        break;
                    } else {
                        uploadHelper();
                        break;
                    }
            }
        }
    }


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


    private Boolean getEditTexts() {
        View focusView = null;
        boolean cancel = false;

        String stringTags = mTagsTextView.getText().toString();
        mLeftDescription = mLeftTextView.getText().toString();
        mRightDescription = mRightTextView.getText().toString();

        if (TextUtils.isEmpty(mLeftDescription)) {
            mLeftTextView.setError(getString(R.string.error_description_required));
            focusView = mLeftTextView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mRightDescription)) {
            mRightTextView.setError(getString(R.string.error_description_required));
            focusView = mRightTextView;
            cancel = true;
        }
        if (TextUtils.isEmpty(stringTags)) {
            mTagsTextView.setError(getString(R.string.error_tag_required));
            focusView = mTagsTextView;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt upload and focus the first
            // form field with an error.
            focusView.requestFocus();
            return true;
        } else {
            description.put("leftDescription", mLeftDescription);
            description.put("rightDescription", mRightDescription);
            if (mTagsTextView.getText().toString().contains(" ")) {
                mTags = mTagsTextView.getText().toString().split(" ");
                mTagsList = Arrays.asList(mTags);
            } else {
                mTags = new String[1];
                mTags[0] = mTagsTextView.getText().toString();
                mTagsList = Arrays.asList(mTags);
            }
            return false;
        }
    }


    private void uploadHelper() {
        final String uuId = UUID.randomUUID().toString();
        String pathLeft = "user_photos/" + "left" + uuId;
        final StorageReference mLeftPhotoReference = mStorage.getReference(pathLeft);

        setupProgress(true);

        UploadTask uploadTaskLeft = mLeftPhotoReference.putFile(selectedLeftUrl);
        Task<Uri> urlLeftTask = uploadTaskLeft.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return mLeftPhotoReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: left image uploaded successfully");

                    downloadLeftUrl = task.getResult();
                    photoUrls.put("leftUrl", downloadLeftUrl.toString());

                    //Starting right image upload
                    String pathRight = "user_photos/" + "right" + uuId;
                    final StorageReference mRightPhotoReference = mStorage.getReference(pathRight);
                    UploadTask uploadTaskRight = mRightPhotoReference.putFile(selectedRightUrl);
                    Task<Uri> urlRightTask = uploadTaskRight.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return mRightPhotoReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: right image uploaded successfully");

                                downloadRightUrl = task.getResult();
                                photoUrls.put("rightUrl", downloadRightUrl.toString());

                                PhotosCloudDatabase photosCloudDatabase = new PhotosCloudDatabase(photoUrls, description, mTagsList, userId, 0, 0);

                                //for loop to update each hashtag at database with the publication **review this, maybe not the best way** (to many repeated data)
                                for (int i = 0; i < mTagsList.size(); i++) {
                                    String path = "/publications/tags/" + mTagsList.get(i) + "/" + uuId;
                                    sendToCloud(photosCloudDatabase, path, false);
                                }
                                String pathUser = "/users/" + userId + "/userPublications/" + uuId;
                                sendToCloud(photosCloudDatabase, pathUser, false);
                                String pathGlobal = "/globalPublications/" + uuId;
                                sendToCloud(photosCloudDatabase, pathGlobal, true);
                            } else {
                                Toast.makeText(UploadActivity.this, "Upload da publicação falhou, tente novamente.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(UploadActivity.this, "Upload da publicação falhou, tente novamente.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendToCloud(final PhotosCloudDatabase photosCloudDatabase, final String path, final boolean goToSingleActivity) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.document(path)
                .set(photosCloudDatabase)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Document written with path: " + path);
                        setupProgress(false);
                        if (goToSingleActivity) {
                            Intent intent = new Intent(UploadActivity.this, SinglePublicationActivity.class);
                            intent.putExtra(SinglePublicationActivity.PHOTO_OBJECT, photosCloudDatabase);
                            startActivity(intent);
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: error adding document" + e);
                        setupProgress(false);
                    }
                });
    }

    private void setupProgress(Boolean progress) {
        if (progress) {
            mProgressBar.setVisibility(View.VISIBLE);
            mUploadButton.setEnabled(false);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mUploadButton.setEnabled(true);
        }
    }
}

