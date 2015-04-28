package it.schmid.android.mofa;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public abstract class PromptDialogKeyboard extends AlertDialog.Builder implements OnClickListener {
	 private final TextView input;

	 private Double defaultValue;
	 private int currPos;
	 /**
	  * @param context
	  * @param title resource id
	  * @param message resource id
	  */
	 public PromptDialogKeyboard(Context context, int title, int message, Double proposedValue) {
	  super(context);
	  setTitle(title);  
	  setMessage(message);
		 input = new EditText(context);

	 		 input.setSelectAllOnFocus(true);
	  	input.requestFocus();
	  input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
	  if (proposedValue != 0.00){
			 input.setText(proposedValue.toString());
		 }

		 setView(input);
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
