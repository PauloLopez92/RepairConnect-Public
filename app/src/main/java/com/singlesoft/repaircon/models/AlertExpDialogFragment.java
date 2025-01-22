package com.singlesoft.repaircon.models;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class AlertExpDialogFragment extends DialogFragment {
    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";

    public static AlertExpDialogFragment newInstance(String title, String message) {
        AlertExpDialogFragment frag = new AlertExpDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(ARG_TITLE);
        String message = getArguments().getString(ARG_MESSAGE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null);

        return builder.create();
    }

    public void showDialog(FragmentManager fragmentManager, String tag) {
        show(fragmentManager, tag);
    }
}

    // To call it at another class
    //AlertExpDialogFragment dialog = AlertExpDialogFragment.newInstance("Session Expired", "Your session has expired. Please log in again.");
    //dialog.showDialog(getSupportFragmentManager(), "dialog_tag");