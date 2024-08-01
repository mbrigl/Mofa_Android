package it.schmid.android.mofa;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

@SuppressLint("ValidFragment")
public class DatePickerDialogFragment extends DialogFragment {
    private int year;
    private int month;
    private int day;
    private boolean setArgs = false;
    private Fragment mFragment = null;
    private Activity mActivity;
    private boolean callFromAdapter = false;
    OnDateSetListener ondateSet;

    public DatePickerDialogFragment() {
    }

    public DatePickerDialogFragment(Fragment callback) {
        mFragment = callback;
    }

    @SuppressLint("NewApi")
    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        year = args.getInt("year");
        month = args.getInt("month");
        day = args.getInt("day");
        setArgs = true;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        if (!setArgs) {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }

        if (callFromAdapter) {
            return new DatePickerDialog(getActivity(), ondateSet, year, month, day);
        }
        // Create a new instance of DatePickerDialog and return it
        if (mFragment == null) {//activity
            return new DatePickerDialog(getActivity(), (OnDateSetListener) mActivity, year, month, day);
        } else { //fragment
            return new DatePickerDialog(getActivity(), (OnDateSetListener) mFragment, year, month, day);
        }
    }
}
