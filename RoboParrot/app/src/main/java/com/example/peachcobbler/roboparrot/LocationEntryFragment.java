package com.example.peachcobbler.roboparrot;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class LocationEntryFragment extends DialogFragment implements TextView.OnEditorActionListener {

    private EditText locationEntry;

    public LocationEntryFragment() {

    }

    public static LocationEntryFragment newInstance() {
        LocationEntryFragment frag = new LocationEntryFragment();
        Bundle args = new Bundle();
        args.putString("title", "Enter your destination");
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_location, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        locationEntry = (EditText) view.findViewById(R.id.location);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter your destination");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        locationEntry.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        // 2. Setup a callback when the "Done" button is pressed on keyboard
        locationEntry.setOnEditorActionListener(this);
    }

    // Fires whenever the textfield has an action performed
    // In this case, when the "Done" button is pressed
    // REQUIRES a 'soft keyboard' (virtual keyboard)
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text back to activity through the implemented listener
            LocationEntryListener listener = (LocationEntryListener) getActivity();
            listener.onFinishEditDialog(locationEntry.getText().toString());
            // Close the dialog and return back to the parent activity
            dismiss();
            return true;
        }
        return false;
    }


    public interface LocationEntryListener {
        void onFinishEditDialog(String inputText);
    }
}
