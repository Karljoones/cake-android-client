package com.waracle.androidtest.fragments;


import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.waracle.androidtest.MainActivity;
import com.waracle.androidtest.R;
import com.waracle.androidtest.templates.Cake;
import com.waracle.androidtest.utils.ImageLoader;

import java.util.ArrayList;


/**
 * Fragment is responsible for loading in some JSON and
 * then displaying a list of cakes with images.
 * Fix any crashes
 * Improve any performance issues
 * Use good coding practices to make code more secure
 */
public class CakeListFragment extends Fragment {

    private RecyclerView mListView;
    private ListAdapter mAdapter;

    public CakeListFragment() { /**/ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mListView = rootView.findViewById(R.id.list);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Create and set the list adapter.
        mAdapter = new ListAdapter(((MainActivity) getActivity()).getCakes());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mListView.setLayoutManager(layoutManager);
        mListView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(8), true));
        mListView.setItemAnimator(new DefaultItemAnimator());
        mListView.setAdapter(mAdapter);
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    // This code was replaced with a recycler view as this is more efficient than a list view.
    private class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
        private ImageLoader mImageLoader;
        private ArrayList<Cake> cakes;

        public ListAdapter(ArrayList<Cake> cakes) {
            mImageLoader = new ImageLoader();
            this.cakes = cakes;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(getActivity()).inflate(R.layout.list_item_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final int pos = holder.getAdapterPosition();

            holder.title.setText(cakes.get(pos).getTitle());
            holder.desc.setText(cakes.get(pos).getDesc());

            mImageLoader.load(cakes.get(pos).getImageData(), holder.image);
        }

        @Override
        public int getItemCount() {
            return ((MainActivity) getContext()).getCakes().size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView image;
            TextView title, desc;

            public ViewHolder(View view) {
                super(view);

                title = view.findViewById(R.id.list_item_layout_title);
                desc = view.findViewById(R.id.list_item_layout_desc);
                image = view.findViewById(R.id.list_item_layout_image);
            }
        }
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    private class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        private GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
}