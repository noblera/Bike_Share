package com.noble.bikeshare;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.noble.backend.errandBikeApi.model.ErrandBike;
import com.noble.backend.genericBikeApi.model.GenericBike;
import com.noble.backend.roadBikeApi.model.RoadBike;

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
        OtherBikesActivity activity = (OtherBikesActivity) getActivity();

        List<GenericBike> subListGenBikes = null;
        List <ErrandBike> subListErrBikes = null;
        List <RoadBike> subListRBikes = null;

        // filter for sublist of bikes we want

        if (mBikeType.equals("Generic Bike")) {
            subListGenBikes = new ArrayList<>();
            List<GenericBike> genBikes = activity.setGenericBikes(database);
            for (GenericBike bike : genBikes) {
                if (bike.getAtStation()) {
                    subListGenBikes.add(bike);
                }
            }
        } else if (mBikeType.equals("Errand Bike")) {
            subListErrBikes = new ArrayList<>();
            List<ErrandBike> errBikes = activity.setErrandBikes(database);
            for (ErrandBike bike : errBikes) {
                if (bike.getAtStation()) {
                    subListErrBikes.add(bike);
                }
            }
        } else {
            subListRBikes = new ArrayList<>();
            List<RoadBike> rBikes = activity.setRoadBikes(database);
            for (RoadBike bike : rBikes) {
                if (bike.getAtStation()) {
                    subListRBikes.add(bike);
                }
            }
        }

        mAdapter = new OtherBikesAdapter(subListGenBikes, subListErrBikes, subListRBikes);
        mOtherBikesRecyclerView.setAdapter(mAdapter);
    }

    private class OtherBikesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mBikeIdTextView;
        private ImageView mBikeImage;
        private int mId;

        public OtherBikesHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mBikeIdTextView = (TextView) itemView.findViewById(R.id.list_item_bikes_id);
            mBikeImage = (ImageView) itemView.findViewById(R.id.list_item_bikes_image);
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
        private List<GenericBike> mGenericBikes;
        private List<ErrandBike> mErrandBikes;
        private List<RoadBike> mRoadBikes;

        public OtherBikesAdapter(List<GenericBike> genBikes, List<ErrandBike> errBikes, List<RoadBike> rBikes) {
            mGenericBikes = genBikes;
            mErrandBikes = errBikes;
            mRoadBikes = rBikes;
        }

        @Override
        public OtherBikesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.list_item_bikes, parent, false);
            return new OtherBikesHolder(view);
        }

        @Override
        public void onBindViewHolder(OtherBikesHolder holder, int position) {
            if (mGenericBikes != null) {
                GenericBike bike = mGenericBikes.get(position);
                holder.mBikeIdTextView.setText("Generic Bike " + bike.getId().intValue());
                holder.mBikeImage.setImageResource(R.drawable.generic_bike);
                holder.setBikeId(bike.getId().intValue());
            } else if (mErrandBikes != null) {
                ErrandBike bike = mErrandBikes.get(position);
                holder.mBikeIdTextView.setText("Errand Bike " + bike.getId().intValue());
                holder.mBikeImage.setImageResource(R.drawable.errand_bike);
                holder.setBikeId(bike.getId().intValue());
            } else {
                RoadBike bike = mRoadBikes.get(position);
                holder.mBikeIdTextView.setText("Road Bike " + bike.getId().intValue());
                holder.mBikeImage.setImageResource(R.drawable.road_bike);
                holder.setBikeId(bike.getId().intValue());
            }
        }

        @Override
        public int getItemCount() {
            if (mGenericBikes != null) {
                return mGenericBikes.size();
            } else if (mErrandBikes != null) {
                return mErrandBikes.size();
            } else {
                return mRoadBikes.size();
            }
        }
    }
}
