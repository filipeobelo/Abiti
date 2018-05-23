package com.beloinc.abiti.main;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beloinc.abiti.R;
import com.beloinc.abiti.utils.PhotosDatabase;
import com.bumptech.glide.Glide;


/**
 * A simple {@link Fragment} subclass.
 */
public class SinglePublicationFragment extends Fragment {

    private PhotosDatabase mPublication;

    public SinglePublicationFragment() {
        // Required empty public constructor
    }

    public void setPublication(PhotosDatabase publication) {
        this.mPublication = publication;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_single_publication, container, false);

        ImageView mLeftImage = view.findViewById(R.id.image_left);
        ImageView mRightImage = view.findViewById(R.id.image_right);
        TextView mLeftDescription = view.findViewById(R.id.text_left);
        TextView mRightDescription = view.findViewById(R.id.text_right);

        Glide.with(mLeftImage.getContext())
                .load(mPublication.getPhotoLeftUrl())
                .into(mLeftImage);

        Glide.with(mRightImage.getContext())
                .load(mPublication.getPhotoRightUrl())
                .into(mRightImage);

        mLeftDescription.setText(mPublication.getLeftDescription());
        mRightDescription.setText(mPublication.getRightDescription());

        return view;
    }

}
