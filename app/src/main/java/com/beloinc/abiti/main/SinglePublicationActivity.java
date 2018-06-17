package com.beloinc.abiti.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beloinc.abiti.R;
import com.beloinc.abiti.upload.UploadActivity;
import com.beloinc.abiti.utils.PhotosCloudDatabase;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.Map;


public class SinglePublicationActivity extends AppCompatActivity {

    private static final String TAG = "SinglePublicationActivi";

    //CONSTANT TO REFERENCE AT INTENT
    public static final String PHOTO_OBJECT = "photo_object";

    //widgets
    private Context mContext;
    private ImageView mLeftImage;
    private ImageView mRightImage;
    private TextView mLeftDescription;
    private TextView mRightDescription;
    private Button mNewPublication;
    private Button mVoteLeft;
    private Button mVoteRight;
    private TextView mNumVotesLeft;
    private TextView mNumVotesRight;
    private LinearLayout mVotesLayoutLeft;
    private LinearLayout mVotesLayoutRight;
    private TextView mPreVote;
    private TextView mAfterVote;
    private Button mSeeComments;

    //database object
    private PhotosCloudDatabase photosCloudDatabase;

    //FIREBASE
    private FirebaseFirestore db;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_publication);

        mContext = SinglePublicationActivity.this;

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        photosCloudDatabase = (PhotosCloudDatabase) intent.getSerializableExtra(PHOTO_OBJECT);

        setupWidgets();
        setupWidgetsClickListener(new WidgetClickListener());
        updateWidgets();

        searchVoter();

    }


    private void setupWidgets() {
        mLeftImage = findViewById(R.id.image_left);
        mRightImage = findViewById(R.id.image_right);
        mLeftDescription = findViewById(R.id.text_left);
        mRightDescription = findViewById(R.id.text_right);
        mNewPublication = findViewById(R.id.new_content);
        mVoteLeft = findViewById(R.id.button_left);
        mVoteRight = findViewById(R.id.button_right);
        mVotesLayoutLeft = findViewById(R.id.votes_layoutLeft);
        mVotesLayoutRight = findViewById(R.id.votes_layoutRight);
        mNumVotesLeft = findViewById(R.id.votes_left);
        mNumVotesRight = findViewById(R.id.votes_right);
        mPreVote = findViewById(R.id.preVote);
        mAfterVote = findViewById(R.id.after_vote);
        mSeeComments = findViewById(R.id.see_all_comments);
    }

    private void setupWidgetsClickListener(WidgetClickListener clickListener) {
        mNewPublication.setOnClickListener(clickListener);
        mVoteLeft.setOnClickListener(clickListener);
        mVoteRight.setOnClickListener(clickListener);
    }

    private void votesVisibility(boolean visibility) {
        if (visibility) {
            mPreVote.setVisibility(View.GONE);
            mVoteLeft.setVisibility(View.GONE);
            mVoteRight.setVisibility(View.GONE);
            mVotesLayoutLeft.setVisibility(View.VISIBLE);
            mVotesLayoutRight.setVisibility(View.VISIBLE);
            mSeeComments.setVisibility(View.VISIBLE);
            mAfterVote.setVisibility(View.VISIBLE);
        } else {
            mSeeComments.setVisibility(View.GONE);
            mAfterVote.setVisibility(View.GONE);
            mVotesLayoutLeft.setVisibility(View.GONE);
            mVotesLayoutRight.setVisibility(View.GONE);
            mPreVote.setVisibility(View.VISIBLE);
            mVoteLeft.setVisibility(View.VISIBLE);
            mVoteRight.setVisibility(View.VISIBLE);
        }
    }

    private void setupVotes() {
        String path = "/globalPublications/" + photosCloudDatabase.getDocId();
        DocumentReference documentReference = db.document(path);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "onComplete: document retrieved successfully");
                        double totalVotesLeft = document.getDouble("countLeft");
                        double totalVotesRight = document.getDouble("countRight");
                        @SuppressLint("DefaultLocale") String votesLeft = String.format("%.0f", totalVotesLeft);
                        @SuppressLint("DefaultLocale") String votesRight = String.format("%.0f", totalVotesRight);
                        mNumVotesLeft.setText(votesLeft);
                        mNumVotesRight.setText(votesRight);
                    } else {
                        Log.d(TAG, "onComplete: no such document");
                    }
                } else {
                    Log.d(TAG, "onComplete: get failed with ", task.getException());
                }
            }
        });
    }

    private void searchVoter() {
        String path = "/globalPublications/" + photosCloudDatabase.getDocId() + "/votedBy/" + userId;
        DocumentReference documentReference = db.document(path);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        setupVotes();
                        votesVisibility(true);
                    } else {
                        votesVisibility(false);
                    }
                } else {
                    Log.d(TAG, "onComplete: get failed with", task.getException());
                }
            }
        });
    }

    private void updateWidgets() {
        Glide.with(mLeftImage.getContext())
                .load(photosCloudDatabase.getPhotoUrls().get("leftUrl"))
                .into(mLeftImage);

        Glide.with(mRightImage.getContext())
                .load(photosCloudDatabase.getPhotoUrls().get("rightUrl"))
                .into(mRightImage);

        mLeftDescription.setText(photosCloudDatabase.getDescription().get("leftDescription"));
        mRightDescription.setText(photosCloudDatabase.getDescription().get("rightDescription"));
    }

    private void updateVotes(final String count) {
        String path = "/globalPublications/" + photosCloudDatabase.getDocId();
        final DocumentReference publicationReference = db.document(path);
        db.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(publicationReference);
                //the app must make sure that there is a count field at the database, that is done when the publication is uploaded
                double newCount = snapshot.getDouble(count) + 1;
                transaction.update(publicationReference, count, newCount);
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Transaction success!");
                updateVotedBy(count);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "onFailure: Transaction failure.", e);
            }
        });
    }


    private void updateVotedBy(String count) {
        Map<String, Object> votedBy = new HashMap<>();
        votedBy.put("userId", userId);
        votedBy.put("vote", count);
        String path = "/globalPublications/" + photosCloudDatabase.getDocId() + "/votedBy/" + userId;
        db.document(path)
                .set(votedBy)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: DocumentSnapshot successfully written!");
                        setupVotes();
                        votesVisibility(true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "onFailure: error writing document", e);
            }
        });
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

                case R.id.button_left:
                    Log.d(TAG, "onClick: voted left");
                    disableButtons(true);
                    updateVotes("countLeft");
                    break;

                case R.id.button_right:
                    Log.d(TAG, "onClick: voted right");
                    disableButtons(true);
                    updateVotes("countRight");
                    break;
            }
        }
    }

    private void disableButtons(boolean b) {
        if (b) {
            mVoteLeft.setEnabled(false);
            mVoteRight.setEnabled(false);
        } else {
            mVoteRight.setEnabled(true);
            mVoteLeft.setEnabled(true);
        }
    }

}
