package com.noble.bikeshare;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by richie on 3/17/16.
 */
public class UnreserveBikeFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.unreserve_bike_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                StationActivity activity = (StationActivity) getActivity();
                                activity.updateReserveStatus(true);
                            }
                        })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }
}
