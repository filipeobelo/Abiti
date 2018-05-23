package com.beloinc.abiti.main;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beloinc.abiti.R;
import com.beloinc.abiti.utils.CardContainerAdapter;
import com.beloinc.abiti.utils.PhotosDatabase;
import com.beloinc.abiti.utils.SpacesItemDecoration;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecyclerGridFragment extends Fragment {

    private static final String TAG = "RecyclerGridFragment";

    private RecyclerView mRecyclerView;
    private CardContainerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<PhotosDatabase> photosDatabaseList;
    OnPublicationSelected publicationSelected;

    //Firebase
    private DatabaseReference databaseReference;
    private ChildEventListener postListener;

    public interface OnPublicationSelected {
        public void onPublicationSelected (PhotosDatabase publication);
    }

    public RecyclerGridFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: fragment attached to main activity");
        try {
            publicationSelected = (OnPublicationSelected) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnPublicationSelected");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: listener attached");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycler_grid, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("user_uploads");

        // Initialize RecyclerView photos adapter
        photosDatabaseList = new ArrayList<>();
        mAdapter = new CardContainerAdapter(photosDatabaseList);

        int spacing = 5;
        int spanCount = 1;

        mRecyclerView = view.findViewById(R.id.recycler_grid);
        mLayoutManager = new GridLayoutManager(inflater.getContext(), spanCount);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(spanCount, spacing));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setListener(new CardContainerAdapter.Listener() {
            @Override
            public void onClick(PhotosDatabase publication) {
                publicationSelected.onPublicationSelected(publication);
            }
        });

        attachDatabaseReadListener();

        return view;
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: fragment stopped");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
        detachDatabaseReadListener();
        clear();
    }

    private void attachDatabaseReadListener() {
        if (postListener == null) {
            Log.d(TAG, "attachDatabaseReadListener: listener attached");
            postListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    PhotosDatabase post = dataSnapshot.getValue(PhotosDatabase.class);
                    photosDatabaseList.add(post);
                    mAdapter.notifyItemInserted(photosDatabaseList.size() - 1);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            databaseReference.addChildEventListener(postListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (postListener != null) {
            Log.d(TAG, "detachDatabaseReadListener: listener detached");
            databaseReference.removeEventListener(postListener);
            postListener = null;
        }
    }

    private void clear() {
        int size = photosDatabaseList.size();
        photosDatabaseList.clear();
        Log.d(TAG, "clear: is photosDatabaseList empty? " + photosDatabaseList.isEmpty());
        mAdapter.notifyItemRangeRemoved(0, size);
    }
}
