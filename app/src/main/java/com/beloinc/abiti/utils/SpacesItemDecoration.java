package com.beloinc.abiti.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int spacing;

    public SpacesItemDecoration(int spanCount, int spacing) {
        this.spanCount = spanCount;
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        int column = position % spanCount;

        //one column
        if (spanCount == 1) {
            outRect.left = 16;
            outRect.right = 16;
        }

        //no edges
        if (position >= spanCount) {
            outRect.top = spacing;
        }
        if (column < spanCount -1) {
            outRect.right = spacing;
        }
    }
}
