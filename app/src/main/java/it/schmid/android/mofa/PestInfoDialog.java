package it.schmid.android.mofa;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.model.Pesticide;
import it.schmid.android.mofa.util.PDFTools;

/**
 * Created by schmida on 07.10.14.
 */
public class PestInfoDialog extends DialogFragment {
    static int pestNr;

    public static PestInfoDialog newInstance(int pestId) {
        PestInfoDialog info = new PestInfoDialog();
        info.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        pestNr = pestId;
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
        final Pesticide p = DatabaseManager.getInstance().getPesticideWithId(pestNr);
        try {
            if (p.getConstraints() != null) {
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
                if (jsonString.has("maxDose")) {
                    TextView maxDoseText = (TextView) rootView.findViewById(R.id.maxDose);
                    maxDoseText.setText(Util.getJSONDouble(jsonString, "maxDose") + "");
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
            Button btnEti = (Button) rootView.findViewById(R.id.btn_etich);
            btnEti.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ParsePage().execute(p.getProductName());
                }
            });
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

    private void setLink(List<Element> etList) {

        Button btnShowEti = (Button) getView().findViewById(R.id.btn_show_etich);
        final TextView txtStatusEti = (TextView) getView().findViewById(R.id.etich_Statustxt);
        btnShowEti.setVisibility(View.GONE);
        txtStatusEti.setVisibility(View.GONE);
        if (etList.size() > 0) {
            for (final Element link : etList) {
                btnShowEti.setVisibility(View.VISIBLE);
                btnShowEti.setText(link.text());
                btnShowEti.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isPdfSupported = PDFTools.showPDFUrl(getActivity(), link.attr("abs:href"));
                        if (!isPdfSupported) {
                            txtStatusEti.setVisibility(View.VISIBLE);
                            txtStatusEti.setText("Keine APP zum Lesen von PDF installiert");
                        }
                    }
                });
            }
        } else {
            txtStatusEti.setVisibility(View.VISIBLE);
            txtStatusEti.setText(getResources().getString(R.string.etichNotFound));
        }
    }

    class ParsePage extends AsyncTask<String, Void, List<Element>> {
        List<Element> linkList = new ArrayList<Element>();
        String urlPart1 = "http://www.fitosanitari.salute.gov.it/fitosanitariwsWeb_new/FitosanitariServlet?ACTION=cercaProdotti&FROM=0&TO=49&PROVENIENZA=RICERCA&NOME=&NOME_SOSTANZA=&NUMERO_REGISTRAZIONE=";
        String urlPart2 = "&ATTIVITA=&STATO_AMMINISTRATIVO=&DT_IN_REGISTRAZIONE=&DT_FN_REGISTRAZIONE=&DT_IN_SCADENZA=&DT_FN_SCADENZA=&PRODOTTO_IP=&PRODOTTO_PPO=&PRODOTTO_PFnPE=";

        @Override
        protected List<Element> doInBackground(String... params) {
            String prodName = params[0];
            String url = "";

            try {
                Connection.Response pestForm = Jsoup.connect("http://www.fitosanitari.salute.gov.it/fitosanitariwsWeb_new/FitosanitariServlet")
                        .method(Connection.Method.GET)
                        .execute();
                Document doc = Jsoup.connect("http://www.fitosanitari.salute.gov.it/fitosanitariwsWeb_new/FitosanitariServlet")
                        .data("cookieexists", "false")
                        .data("ACTION", "cercaProdotti")
                        .data("FROM", "0")
                        .data("TO", "49")
                        .data("PROVENIENZA", "RICERCA")
                        .data("NOME", prodName)
                        .data("NOME_SOSTANZA", "")
                        .data("NUMERO_REGISTRAZIONE", "")
                        .data("ATTIVITA", "")
                        .data("STATO_AMMINISTRATIVO", "")
                        .data("DT_IN_REGISTRAZIONE", "")
                        .data("DT_FN_REGISTRAZIONE", "")
                        .data("DT_IN_SCADENZA", "")
                        .data("DT_FN_SCADENZA", "")
                        .data("PRODOTTO_IP", "")
                        .data("PRODOTTO_PPO", "")
                        .data("PRODOTTO_PFnPE", "")
                        .cookies(pestForm.cookies())
                        .post();

                Elements links = doc.select("a[href]");
                for (Element link : links) {
                    if (link.text().startsWith("Etichetta")) {
                        linkList.add(link);
                    }

                }
                return linkList;
            } catch (IOException ex) {
                Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG);

            } catch (Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Element> result) {
            setLink(result);

        }

    }
}
