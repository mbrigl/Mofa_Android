package it.schmid.android.mofa.interfaces;

import it.schmid.android.mofa.model.Wirkung;

/**
 * Created by schmida on 10.05.17.
 */

public interface InputDoseASANewFragmentListener {
    void onFinishEditDialog(Double doseInput, Double amountInput, Wirkung w);
}
