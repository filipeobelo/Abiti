package com.beloinc.abiti.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beloinc.abiti.R;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;

public class CardContainerAdapter extends RecyclerView.Adapter<CardContainerAdapter.ViewHolder> {

    private List<PhotosCloudDatabase> photosDatabaseList;
    private Listener listener;

    public interface Listener {
        public void onClick(PhotosCloudDatabase publication);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public CardContainerAdapter(List<PhotosCloudDatabase> photosDatabaseList) {
        this.photosDatabaseList = photosDatabaseList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView itemView) {
            super(itemView);
            cardView = itemView;
        }
    }

    @NonNull
    @Override
    public CardContainerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_container, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(@NonNull CardContainerAdapter.ViewHolder holder, int position) {
        CardView cardView = holder.cardView;

        final PhotosCloudDatabase photosDatabase = photosDatabaseList.get(position);

        //widgets
        ImageView leftImage = cardView.findViewById(R.id.image_left);
        ImageView rightImage = cardView.findViewById(R.id.image_right);
        TextView tagsView = cardView.findViewById(R.id.text_tags);

        Map<String, String> photoUrls = photosDatabase.getPhotoUrls();
        List<String> photoTags = photosDatabase.getTags();

        if (photoTags != null) {
            StringBuilder builder = new StringBuilder();
            for (String s : photoTags) {
                builder.append(s);
            }
            String stringTags = builder.toString();
            tagsView.setText(stringTags);
        }

        //widgets attribution
        Glide.with(leftImage.getContext())
                .load(photoUrls.get("leftUrl"))
                .into(leftImage);
        Glide.with(rightImage.getContext())
                .load(photoUrls.get("rightUrl"))
                .into(rightImage);


        //set click listener on card view
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(photosDatabase);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return photosDatabaseList.size();
    }


}
