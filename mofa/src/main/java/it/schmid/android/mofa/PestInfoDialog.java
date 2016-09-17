package it.schmid.android.mofa;

import android.app.Dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;



import org.json.JSONException;
import org.json.JSONObject;

import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.Pesticide;

/**
 * Created by schmida on 07.10.14.
 */
public class PestInfoDialog extends android.support.v4.app.DialogFragment {
    static int pestNr;
    public static PestInfoDialog newInstance(int pestId){
        PestInfoDialog info = new PestInfoDialog();
        info.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        pestNr=pestId;
        return info;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pestinfodialog, container,
                false);
        Pesticide p = DatabaseManager.getInstance().getPesticideWithId(pestNr);
        try {
            if (p.getConstraints()!=null) {
                JSONObject jsonString = new JSONObject(p.getConstraints());
                if (jsonString.has("waitingPeriod")) {
                    TextView waitTimeText = (TextView) rootView.findViewById(R.id.waitingTime);
                    waitTimeText.setText(Util.getJSONString(jsonString, "waitingPeriod") + " " + getText(R.string.waitingtimeUnit));
                }
                if (jsonString.has("wez")) {
                    TextView wezTimeText = (TextView) rootView.findViewById(R.id.reenterTime);
                    wezTimeText.setText(Util.getJSONInt(jsonString, "wez") + " " + getText(R.string.reentertimeUnit));
                }
                if (jsonString.has("maxUsage")) {
                    TextView maxUsageText = (TextView) rootView.findViewById(R.id.maxTime);
                    maxUsageText.setText(Integer.toString(Util.getJSONInt(jsonString, "maxUsage")));
                }
                if (jsonString.has("maxAmount")) {
                    TextView maxAmountText = (TextView) rootView.findViewById(R.id.maxAmount);
                    maxAmountText.setText(Util.getJSONDouble(jsonString, "maxAmount") + " " + getText(R.string.maxamounthaUnit));
                }
                if (jsonString.has("maxDose")){
                    TextView maxDoseText = (TextView) rootView.findViewById(R.id.maxDose);
                    maxDoseText.setText(Util.getJSONDouble(jsonString, "maxDose")+"");
                }
                if (jsonString.has("restriction")) {
                    TextView restrictionText = (TextView) rootView.findViewById(R.id.otherconstraints);
                    restrictionText.setText(Util.getJSONString(jsonString, "restriction"));
                }
                if (jsonString.has("beeRestriction")) {
                    int beeDange = Util.getJSONInt(jsonString, "beeRestriction");
                    if (beeDange == 1) {
                        TextView beeDangerText = (TextView) rootView.findViewById(R.id.beeDanger);
                        ImageView beeIcon = (ImageView) rootView.findViewById(R.id.imageBee);
                        beeDangerText.setVisibility(View.VISIBLE);
                        beeIcon.setVisibility(View.VISIBLE);

                    }
                }
            }
            Button closeButton = (Button) rootView.findViewById(R.id.closebutton);
            closeButton.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    dismiss();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
        getDialog().setTitle(p.getProductName());

        return rootView;
    }
}
