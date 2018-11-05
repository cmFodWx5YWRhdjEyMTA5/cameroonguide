package com.ngoucoorp.cameroonguide.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import com.ngoucoorp.cameroonguide.R;

/**
 * Created by N'gou Coorp.
 * Contact Email : ngounoubosseloic@gmail.com
 */
public class AlertDialogRadio extends DialogFragment {

    static String[] sortingType = new String[]{
            "Sort By Name Asc",
            "Sort By Name Desc",
            "Sort By Added Date Asc",
            "Sort By Added Date Desc",
            "Sort By Like Count Asc",
            "Sort By Like Count Desc"
    };

    AlertPositiveListener alertPositiveListener;

    public interface AlertPositiveListener {
        void onPositiveClick(int position);
    }

    public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
        try{
            alertPositiveListener = (AlertPositiveListener) activity;
        }catch(ClassCastException e){
            // The hosting activity does not implemented the interface AlertPositiveListener
            throw new ClassCastException(activity.toString() + " must implement AlertPositiveListener");
        }
    }


    OnClickListener positiveListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            AlertDialog alert = (AlertDialog)dialog;
            int position = alert.getListView().getCheckedItemPosition();
            alertPositiveListener.onPositiveClick(position);
        }
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        int position = bundle.getInt("position");

        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());

        b.setTitle(getString(R.string.sortingType));

        b.setSingleChoiceItems(sortingType, position, null);

        b.setPositiveButton("OK",positiveListener);

        b.setNegativeButton("Cancel", null);

        return b.create();
    }

}
