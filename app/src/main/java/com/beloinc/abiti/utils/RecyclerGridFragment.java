package com.beloinc.abiti.utils;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beloinc.abiti.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    private List<PhotosCloudDatabase> photosDatabaseList;

    //activities must set path
    private String path;

    //default docId == not applicable
    private String docId = "na";

    //default spacing and number of columns, activity may change via setRecyclerLayout method
    private int spacing = 5;
    private int spanCount = 1;

    //interface object
    OnPublicationSelected publicationSelected;

    //Firebase
    private FirebaseFirestore db;


    public interface OnPublicationSelected {
        public void onPublicationSelected(PhotosCloudDatabase publication);
    }

    public RecyclerGridFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: fragment attached activity");
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

        db = FirebaseFirestore.getInstance();

        setupRecyclerView(view, inflater);

        accessDatabase();

        return view;
    }

    private void accessDatabase() {
        if (docId.equals("na")) {
            db.collection(path)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    PhotosCloudDatabase photosCloudDatabase = document.toObject(PhotosCloudDatabase.class);
                                    photosDatabaseList.add(photosCloudDatabase);
                                    mAdapter.notifyItemInserted(photosDatabaseList.size() - 1);
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    private void setupRecyclerView(View view, LayoutInflater inflater) {
        photosDatabaseList = new ArrayList<>();
        mAdapter = new CardContainerAdapter(photosDatabaseList);
        mRecyclerView = view.findViewById(R.id.recycler_grid);
        mLayoutManager = new GridLayoutManager(inflater.getContext(), spanCount);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(spanCount, spacing));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setListener(new CardContainerAdapter.Listener() {
            @Override
            public void onClick(PhotosCloudDatabase publication) {
                publicationSelected.onPublicationSelected(publication);
            }
        });

        final FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        if (fab != null) {
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                        fab.hide();
                    } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                        fab.show();
                    }
                }
            });
        }
    }

    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }

    public void setSpanCount(int spanCount) {
        this.spanCount = spanCount;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: fragment stopped");  //JUST TO TRACK FRAGMENT LIFECYCLE
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
        clear();            // CLEAR RECYCLERVIEW WHEN FRAGMENT IS DESTROYED... POSSIBLE CHANGES HERE
    }


    private void clear() {
        int size = photosDatabaseList.size();
        photosDatabaseList.clear();
        Log.d(TAG, "clear: is photosDatabaseList empty? " + photosDatabaseList.isEmpty());
        mAdapter.notifyItemRangeRemoved(0, size);
    }
}
