package it.schmid.android.mofa;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.text.SpannableStringBuilder;
import android.text.style.BulletSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
import it.schmid.android.mofa.model.Wartefrist;
import it.schmid.android.mofa.model.Wirkung;
import it.schmid.android.mofa.util.PDFTools;

/**
 * Created by schmida on 07.10.14.
 */
public class PestInfoDialogASA extends DialogFragment {
    static int pestNr;
    PestInfos pestInfos;

    public static PestInfoDialogASA newInstance(int pestId) {
        PestInfoDialogASA info = new PestInfoDialogASA();
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
        View rootView = inflater.inflate(R.layout.pestinfodialog_asa, container,
                false);
        final Pesticide p = DatabaseManager.getInstance().getPesticideWithId(pestNr);
        Gson gson = new Gson();
        String jsonStr = p.getConstraints();
        pestInfos = gson.fromJson(jsonStr, PestInfos.class);
        TextView statusTxt = (TextView) rootView.findViewById(R.id.statusText);
        statusTxt.setText(getResources().getString(R.string.status) + " " + p.getStatus());
        getWartefristInfos(rootView);
        Button btnEti = (Button) rootView.findViewById(R.id.btn_etich);
        btnEti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ParsePage().execute(p.getProductName());
            }
        });

        getDialog().setTitle(p.getProductName());

        return rootView;
    }

    private void getWartefristInfos(View rootView) {
        List<Wartefrist> warteFristList = pestInfos.getWartefrist();
        if (!warteFristList.isEmpty()) {
            SpannableStringBuilder warteFristBuilder = new SpannableStringBuilder();

            if (warteFristList.get(0).getBeeRestriction() == 1) {
                TextView beeDangerText = (TextView) rootView.findViewById(R.id.beeDanger);
                        beeDangerText.setText(beeDangerText.getText() + getResources().getString(R.string.beeWarning));
                        ImageView beeIcon = (ImageView) rootView.findViewById(R.id.imageBee);
                        beeDangerText.setVisibility(View.VISIBLE);
                        beeIcon.setVisibility(View.VISIBLE);
            }else {
                TextView beeDangerText = (TextView) rootView.findViewById(R.id.beeDanger);
                beeDangerText.setText(beeDangerText.getText() + getResources().getString(R.string.beeNoWarning));
            }
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String strDefCultivationTyp = preferences.getString("listCultivationType","1");
            String anbauArt;
            switch (strDefCultivationTyp) {
                case "1":
                    anbauArt = "Agrios";
                    break;
                case "2":
                    anbauArt = "Bio";
                    break;
                case "3":
                    anbauArt = "Gesetzlich";
                    break;
                default:
                    anbauArt = "Agrios";
            }
            //Log.d("InputFragmentASANew", "Anbauart = " + strDefCultivationTyp);
            warteFristBuilder.append("\n" + getResources().getString(R.string.waitingtime) + "\n");
            for (Wartefrist w : warteFristList){

                if (w.getAnbauart().equalsIgnoreCase(anbauArt)){



                    String karenzZeit;



                    warteFristBuilder.append("\n");


                    karenzZeit = w.getKultur() +", "+w.getAnbauart() +": " + w.getKarenzzeit();


                    warteFristBuilder.append(karenzZeit);
                    warteFristBuilder.setSpan(new BulletSpan(10),warteFristBuilder.length() - karenzZeit.length(),warteFristBuilder.length(),17);

                }

            }
            TextView waitingTimeTxt = (TextView) rootView.findViewById(R.id.waitingTimetxt);
            waitingTimeTxt.setText(warteFristBuilder);

        }

    }
    private void setLink(List<Element> etList){

        Button btnShowEti = (Button) getView().findViewById(R.id.btn_show_etich);
        final TextView txtStatusEti = (TextView) getView().findViewById(R.id.etich_Statustxt);
        btnShowEti.setVisibility(View.GONE);
        txtStatusEti.setVisibility(View.GONE);
        if (etList.size()>0) {
            for (final Element link: etList){
                btnShowEti.setVisibility(View.VISIBLE);
                btnShowEti.setText(link.text());
                btnShowEti.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isPdfSupported = PDFTools.showPDFUrl(getActivity(), link.attr("abs:href"));
                        if (!isPdfSupported){
                            txtStatusEti.setVisibility(View.VISIBLE);
                            txtStatusEti.setText("Keine APP zum Lesen von PDF installiert");
                        }
                    }
                });
            }
        }else {
            txtStatusEti.setVisibility(View.VISIBLE);
            txtStatusEti.setText(getResources().getString(R.string.etichNotFound));
        }
    }

    private class PestInfos {

        @SerializedName("Wirkung")
        @Expose
        private List<Wirkung> wirkung = null;
        @SerializedName("Wartefrist")
        @Expose
        private List<Wartefrist> wartefrist = null;

        public List<Wirkung> getWirkung() {
            return wirkung;
        }


        public List<Wartefrist> getWartefrist() {
            return wartefrist;
        }


    }
    class ParsePage extends AsyncTask<String,Void,List<Element>> {
        List<Element> linkList = new ArrayList<Element>();
        String urlPart1 = "http://www.fitosanitari.salute.gov.it/fitosanitariwsWeb_new/FitosanitariServlet?ACTION=cercaProdotti&FROM=0&TO=49&PROVENIENZA=RICERCA&NOME=&NOME_SOSTANZA=&NUMERO_REGISTRAZIONE=";
        String urlPart2 = "&ATTIVITA=&STATO_AMMINISTRATIVO=&DT_IN_REGISTRAZIONE=&DT_FN_REGISTRAZIONE=&DT_IN_SCADENZA=&DT_FN_SCADENZA=&PRODOTTO_IP=&PRODOTTO_PPO=&PRODOTTO_PFnPE=";
        @Override
        protected List<Element> doInBackground(String... params) {
            String prodName = params[0];
            String url = "";

            try{
                Connection.Response pestForm = Jsoup.connect("http://www.fitosanitari.salute.gov.it/fitosanitariwsWeb_new/FitosanitariServlet")
                        .method(Connection.Method.GET)
                        .execute();
                Document doc = Jsoup.connect("http://www.fitosanitari.salute.gov.it/fitosanitariwsWeb_new/FitosanitariServlet")
                        .data("cookieexists", "false")
                        .data("ACTION","cercaProdotti")
                        .data("FROM", "0")
                        .data("TO", "49")
                        .data("PROVENIENZA", "RICERCA")
                        .data("NOME", prodName)
                        .data("NOME_SOSTANZA" , "")
                        .data("NUMERO_REGISTRAZIONE",  "")
                        .data("ATTIVITA" , "")
                        .data("STATO_AMMINISTRATIVO", "")
                        .data("DT_IN_REGISTRAZIONE", "")
                        .data("DT_FN_REGISTRAZIONE","")
                        .data("DT_IN_SCADENZA","")
                        .data("DT_FN_SCADENZA","")
                        .data("PRODOTTO_IP","")
                        .data("PRODOTTO_PPO","")
                        .data("PRODOTTO_PFnPE","")
                        .cookies(pestForm.cookies())
                        .post();

                Elements links = doc.select("a[href]");
                for (Element link : links) {
                    if (link.text().startsWith("Etichetta")){
                        linkList.add(link);
                    }

                }
                return linkList;
            }catch(IOException ex){
                Toast.makeText(getActivity(),ex.getMessage(),Toast.LENGTH_LONG);

            }catch(Exception e){
                Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG);
            }
            return null;
        }
        @Override
        protected void onPostExecute(List<Element> result) {
            setLink(result);

        }

    }
}
