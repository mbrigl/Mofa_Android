package it.schmid.android.mofa;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

@SuppressLint("ValidFragment")
public class DatePickerDialogFragment extends DialogFragment {
	private Fragment mFragment = null;
	private Activity mActivity;
	
	public DatePickerDialogFragment(Activity acallback){
		mActivity = acallback;
	}
	public DatePickerDialogFragment(Fragment callback) {
        mFragment = callback;
    }
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
	// Use the current date as the default date in the picker
    final Calendar c = Calendar.getInstance();
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    int day = c.get(Calendar.DAY_OF_MONTH);

    // Create a new instance of DatePickerDialog and return it
    if (mFragment==null){//activity
    	return new DatePickerDialog(getActivity(), (OnDateSetListener) mActivity, year, month, day);
    }else{ //fragment
    	return new DatePickerDialog(getActivity(), (OnDateSetListener) mFragment, year, month, day);
    }
    
	}
}
