/*
 * Needed for check the wateramount spinner on item selected events
 * We need this class to implement a call only if the selection changes
 */

package it.schmid.android.mofa;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class OnItemSelectedListenerWrapper implements OnItemSelectedListener {
    private int lastPosition;
    private final int savePosition;
    private final OnItemSelectedListener listener;

    public OnItemSelectedListenerWrapper(int savePosition, OnItemSelectedListener aListener) {
        lastPosition = 0;
        this.savePosition = savePosition;
        listener = aListener;
    }

    public void onItemSelected(AdapterView<?> aParentView, View aView, int aPosition, long anId) {
        if ((lastPosition == aPosition) || (savePosition == aPosition)) {
            //  Log.d(getClass().getName(), "Ignoring onItemSelected for same position: " + aPosition);
        } else {
            Log.d(getClass().getName(), "Passing on onItemSelected for different position: " + aPosition + ", " + savePosition);
            listener.onItemSelected(aParentView, aView, aPosition, anId);
        }
        lastPosition = aPosition;
    }

    public void onNothingSelected(AdapterView<?> aParentView) {
        listener.onNothingSelected(aParentView);
    }
}
