package com.noble.bikeshare;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richie on 3/20/16.
 */
public class OtherBikesFragment extends Fragment {
    private static final String ARG_BIKE_TYPE = "bike_type";

    private RecyclerView mOtherBikesRecyclerView;
    private OtherBikesAdapter mAdapter;

    private String mBikeType;

    public static OtherBikesFragment newInstance(String bikeType) {
        Bundle args = new Bundle();
        args.putString(ARG_BIKE_TYPE, bikeType);

        OtherBikesFragment fragment = new OtherBikesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBikeType = getArguments().getString(ARG_BIKE_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_other_bikes, container, false);

        mOtherBikesRecyclerView = (RecyclerView) view.findViewById(R.id.other_bikes_recycler_view);
        mOtherBikesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    private void updateUI() {
        BikeDatabase database = BikeDatabase.get(getActivity());
        List<Bike> bikes = database.getBikes();

        // filter for sublist of bikes we want
        List<Bike> subListBikes = new ArrayList<>();
        for (Bike bike : bikes) {
            if (bike.getBikeType().equals(mBikeType) && bike.isAtStation()) {
                subListBikes.add(bike);
            }
        }

        mAdapter = new OtherBikesAdapter(subListBikes);
        mOtherBikesRecyclerView.setAdapter(mAdapter);
    }

    private class OtherBikesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mBikeIdTextView;
        private int mId;

        public OtherBikesHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mBikeIdTextView = (TextView) itemView;
        }

        public void setBikeId(int id) {
            mId = id;
        }

        @Override
        public void onClick(View v) {
            OtherBikesActivity activity = (OtherBikesActivity) getActivity();
            activity.updateBikeId(mId);
            activity.setBikeOptions(mId);
            getActivity().finish();
        }
    }

    private class OtherBikesAdapter extends RecyclerView.Adapter<OtherBikesHolder> {
        private List<Bike> mBikes;

        public OtherBikesAdapter(List<Bike> bikes) {
            mBikes = bikes;
        }

        @Override
        public OtherBikesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new OtherBikesHolder(view);
        }

        @Override
        public void onBindViewHolder(OtherBikesHolder holder, int position) {
            Bike bike = mBikes.get(position);
            holder.mBikeIdTextView.setText(bike.getBikeType() + ": " + bike.getId());
            holder.setBikeId(bike.getId());
        }

        @Override
        public int getItemCount() {
            return mBikes.size();
        }
    }
}
