package it.schmid.android.mofa;

import it.schmid.android.mofa.db.DatabaseManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

public class AboutDialog extends Dialog{
	private static Context mContext = null;
	
	public AboutDialog(Context context) {
		super(context);
		mContext = context;
	}

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		String versionName="";
		setContentView(R.layout.about);
		try {
			versionName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TextView tv = (TextView)findViewById(R.id.legal_text);
		
		Integer versionDb = DatabaseManager.getInstance().getDbVersion();
		
		String aboutText = "<b>Mo</b>bile <b>Fa</b>rmer<br>"
				+ " Version " + versionName +"<br>	Copyright 2023<br> <b>Territorium Online srl</b>";
		aboutText += "<br><a href=\"https://www.tol.info/ueber-uns/datenschutz/#c14274\">Privacy Policy</a>";
		
		tv.setText(readRawTextFile(R.raw.legal));
		tv = (TextView)findViewById(R.id.info_text);
		tv.setText(Html.fromHtml(aboutText)) ;
		//tv.setText(Html.fromHtml(readRawTextFile(R.raw.info))) ;
		tv.setLinkTextColor(Color.rgb(0, 126, 128));
		Linkify.addLinks(tv, Linkify.ALL);
		tv.setMovementMethod(LinkMovementMethod.getInstance());
		
	}

	public static String readRawTextFile(int id) {
		InputStream inputStream = mContext.getResources().openRawResource(id);
		InputStreamReader in = new InputStreamReader(inputStream);
		BufferedReader buf = new BufferedReader(in);
		String line;
		StringBuilder text = new StringBuilder();
		try {
			while (( line = buf.readLine()) != null) text.append(line);
		} catch (IOException e) {
			return null;
		}
		return text.toString();
	}
}
