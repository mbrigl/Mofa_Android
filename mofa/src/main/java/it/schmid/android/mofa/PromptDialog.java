package it.schmid.android.mofa;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public abstract class PromptDialog extends AlertDialog.Builder implements OnClickListener {
	 private final TextView input;
	 private final SeekBar seek;
	 private Double defaultValue;
	 private int currPos;
	 /** 
	  * @param context 
	  * @param title resource id 
	  * @param message resource id 
	  */  
	 public PromptDialog(Context context, int title, int message, Double proposedValue) {  
	  super(context);
	  setTitle(title);  
	  setMessage(message);
		 input = new TextView(context);
	 // SeekBar seekbar = new SeekBar(context);
	//  input = new EditText(context);
		 LinearLayout linear=new LinearLayout(context);

		 linear.setOrientation(LinearLayout.VERTICAL);
		// TextView input=new TextView(context);
		 //input.setText("Hello Android");
		 input.setPadding(10, 10, 10, 10);
		 input.setGravity(Gravity.CENTER);
		 input.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		 seek=new SeekBar(context);
		 seek.setMax(56);
		 linear.addView(input);
		 linear.addView(seek);


		 setView(linear);
		 seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			 public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				 Double value = ((double) progress / 4);
				 input.setText(value.toString());
			 }


			 public void onStartTrackingTouch(SeekBar seekBar) {
			 }


			 public void onStopTrackingTouch(SeekBar seekBar) {
			 }
		 });
		 //input.setSelectAllOnFocus(true);
	 // input.requestFocus();
	 // input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
	  if (proposedValue != 0.00){
			 input.setText(proposedValue.toString());
		 }
		 proposedValue*=4;
		 currPos = proposedValue.intValue();
		 seek.setProgress(currPos);
		 //setView(input);
		 setPositiveButton(R.string.ok, this);
	  setNegativeButton(R.string.cancel, this);  
	 }

	  
	 /** 
	  * will be called when "cancel" pressed. 
	  * closes the dialog. 
	  * can be overridden. 
	  * @param dialog 
	  */  
	 public void onCancelClicked(DialogInterface dialog) {  
	  dialog.dismiss();  
	 }  
	  
	 public void onClick(DialogInterface dialog, int which) {  
	 
	 
	 if (which == DialogInterface.BUTTON_POSITIVE) {  
		 if (input.getText().toString().trim().length() == 0){ //special case no input, therefore 0
			 onOkClicked(0.00);
			 dialog.dismiss();
		 }
		 else if (onOkClicked(Double.valueOf (input.getText().toString()))) {   //normal case, user inputs a number
			 dialog.dismiss();  
		 }  
	  } else {  
	  			onCancelClicked(dialog);  
	  }  
	 }  
	  
	 /** 
	  * called when "ok" pressed. 
	  * @param input 
	  * @return true, if the dialog should be closed. false, if not. 
	  */  
	 abstract public boolean onOkClicked(Double input);  
}
