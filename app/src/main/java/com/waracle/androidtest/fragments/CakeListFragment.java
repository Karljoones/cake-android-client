package com.waracle.androidtest.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
public class CakeListFragment extends ListFragment {

    private ListView mListView;
    private ListAdapter mAdapter;

    public CakeListFragment() { /**/ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mListView = rootView.findViewById(android.R.id.list);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Create and set the list adapter.
        mAdapter = new ListAdapter(getContext(), ((MainActivity) getActivity()).getCakes());
        mListView.setAdapter(mAdapter);
    }

    private class ListAdapter extends ArrayAdapter<Cake> {
        private ImageLoader mImageLoader;

        public ListAdapter(Context context, ArrayList<Cake> cakes) {
            super(context, 0, cakes);
            mImageLoader = new ImageLoader();
        }

        @Override
        public int getCount() {
            return ((MainActivity) getActivity()).getCakes().size();
        }

        @Override
        public Cake getItem(int position) {
            if (!((MainActivity) getActivity()).getCakes().isEmpty())
                return ((MainActivity) getActivity()).getCakes().get(position);
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;

            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_layout, parent, false);

                viewHolder = new ViewHolder();

                viewHolder.title = convertView.findViewById(R.id.list_item_layout_title);
                viewHolder.desc = convertView.findViewById(R.id.list_item_layout_desc);
                viewHolder.image = convertView.findViewById(R.id.list_item_layout_image);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Cake cake = getItem(position);

            viewHolder.title.setText(cake.getTitle());
            viewHolder.desc.setText(cake.getDesc());

            mImageLoader.load(cake.getImageData(), viewHolder.image);

            return convertView;
        }

        class ViewHolder {
            ImageView image;
            TextView title, desc;
        }
    }
}