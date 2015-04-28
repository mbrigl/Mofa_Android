package it.schmid.android.mofa.adapter;

import it.schmid.android.mofa.ActivityConstants;
import it.schmid.android.mofa.PromptDialog;
import it.schmid.android.mofa.PromptDialogKeyboard;
import it.schmid.android.mofa.R;
import it.schmid.android.mofa.WorkProductTabActivity;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.interfaces.PurchaseProductInterface;
import it.schmid.android.mofa.model.Fertilizer;
import it.schmid.android.mofa.model.Pesticide;
import it.schmid.android.mofa.model.Purchase;
import it.schmid.android.mofa.model.PurchaseFertilizer;
import it.schmid.android.mofa.model.PurchasePesticide;

import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ExpandablePurchaseAdapter extends BaseExpandableListAdapter{
	private static final String TAG = "ExpandablePurchaseAdapter";
	private List<Purchase> purchases;
	private SparseArray<List<PurchaseProductInterface>> products;
	private Activity context;
	private LayoutInflater inflater;
	
	public interface OnExpandableListener {
	    public void onExpanded(int groupPos);

	}
	
	public ExpandablePurchaseAdapter(Activity context, List<Purchase>purchases, SparseArray<List<PurchaseProductInterface>> products){
		this.context = context;
		this.purchases = purchases;
		this.products = products;
		
		
	}
	
	public Object getChild(int groupPosition, int childPosition) {
		return products.get(purchases.get(groupPosition).getId()).get(childPosition);
		}
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}
	public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		Integer bGround;
		final String childText;
		final PurchaseProductInterface p = (PurchaseProductInterface) getChild(groupPosition, childPosition);
		if (p instanceof PurchasePesticide){
			PurchasePesticide prodName = (PurchasePesticide) p;
			Pesticide pest = DatabaseManager.getInstance().getPesticideWithId(prodName.getProduct().getId());
			childText = pest.getProductName();
			bGround = context.getResources().getColor(R.color.lightred);
		}else{
			PurchaseFertilizer prodName = (PurchaseFertilizer) p;
			Fertilizer fert = DatabaseManager.getInstance().getFertilizerWithId(prodName.getProduct().getId());
			childText = fert.getProductName();
			bGround = context.getResources().getColor(R.color.lightgreen);
		}
		
		final Double childAmount = p.getAmount();
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.purchase_child_row,parent,false);
            }
		TextView txtListChild = (TextView) convertView.findViewById(R.id.product);
		TextView txtAmount = (TextView) convertView.findViewById(R.id.amount);
		ImageView delIcon = (ImageView) convertView.findViewById(R.id.delete_product_icon);
		delIcon.setClickable(true);
        delIcon.setOnClickListener(new OnClickListener() {
                    
                    public void onClick(View v) {
                        showYesNoDeleteProductDialog(p,groupPosition);
                    }

					
                });
        txtListChild.setBackgroundColor(bGround);
		txtListChild.setText(childText);
		txtListChild.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				showPurchaseDialog(p);
				
			}
			
		});
		txtAmount.setBackgroundColor(bGround);
		txtAmount.setText(childAmount.toString());
        return convertView;
        
	}
	public int getChildrenCount(int groupPosition) {
		return this.products.get(this.purchases.get(groupPosition).getId())
                .size();
	}
	public Object getGroup(int groupPosition) {
		 return this.purchases.get(groupPosition);
	}
	public int getGroupCount() {
		return this.purchases.size();
	}
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}
	public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		final String DATE_FORMAT = "dd.MM.yyyy";
		final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		final Purchase p = (Purchase) getGroup(groupPosition);
		String headerText = dateFormat.format (p.getDate());
		
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.purchase_group_row, parent,false);
            }
		TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.dateLabel);
		ImageView delIcon = (ImageView) convertView.findViewById(R.id.delete_icon);
		ImageView addItemIcon = (ImageView) convertView.findViewById(R.id.additem_icon);
		delIcon.setClickable(true);
        delIcon.setOnClickListener(new OnClickListener() {
                    
                    public void onClick(View v) {
                        showYesNoDeleteDialog(p,groupPosition);
                    }
                });
        addItemIcon.setClickable(true);
        addItemIcon.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				OnExpandableListener x;
				Intent i = new Intent(context,WorkProductTabActivity.class);
				i.putExtra("Purchase_ID",p.getId() );
				i.putExtra("Calling_Activity", ActivityConstants.PURCHASING_ACTIVITY);
				context.startActivity(i);
				notifyDataSetChanged(); 
				x = (OnExpandableListener) context;
				x.onExpanded(groupPosition);
				
			}
        	
        });
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerText);
        
        return convertView;
		
	}
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return true;
	}
	private void showYesNoDeleteDialog(final Purchase p, final int position){
		AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
		builder.setTitle(R.string.dialogdeletetitel);
		builder.setMessage(R.string.dialogdeletemsg);
				 
		builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
		
		   public void onClick(DialogInterface dialog, int which) {
		        // Deleting the entry
			   DatabaseManager.getInstance().deletePurchaseAndProducts(p);
			   purchases.remove(purchases.get(position)); //removing the item form the list
               notifyDataSetChanged(); 
			   dialog.dismiss();
		   }
		});
		builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
		  
		   public void onClick(DialogInterface dialog, int which) {
		        // Do Nothing
		        dialog.dismiss();
		   }
		 
		});
		 
		 
		AlertDialog alert = builder.create();
		alert.show();
	}
	private void showYesNoDeleteProductDialog(final PurchaseProductInterface p, final int position) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
		builder.setTitle(R.string.dialogdeletetitel);
		builder.setMessage(R.string.dialogdeletemsg);
				 
		builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
		
		   public void onClick(DialogInterface dialog, int which) {
		        // Deleting the entry
			   if (p instanceof PurchasePesticide){
				   DatabaseManager.getInstance().deletePurchasePesticide((PurchasePesticide) p);
			   }else{
				   DatabaseManager.getInstance().deletePurchaseFertilizer((PurchaseFertilizer) p);
			   }
			   List<PurchaseProductInterface> productList = products.get(purchases.get(position).getId());
			   productList.remove(p); //remove item from the list
              
               notifyDataSetChanged(); 
			   dialog.dismiss();
		   }
		});
		builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
		  
		   public void onClick(DialogInterface dialog, int which) {
		        // Do Nothing
		        dialog.dismiss();
		   }
		 
		});
		 
		 
		AlertDialog alert = builder.create();
		alert.show();
		
	}
	private void showPurchaseDialog(final PurchaseProductInterface curProd){
		
		PromptDialogKeyboard dlg = new PromptDialogKeyboard(context, R.string.title,
				R.string.enter_amount, curProd.getAmount()) {
			@Override
			public boolean onOkClicked(Double input) {
				// do something
				Log.d(TAG, "showDialog: " + input);
				
				if (curProd instanceof PurchasePesticide){
					PurchasePesticide pest = (PurchasePesticide) curProd;
					pest.setAmount(input);
					DatabaseManager.getInstance().updatePurchasePesticide(pest);
				}else{
					PurchaseFertilizer fert = (PurchaseFertilizer) curProd;
					fert.setAmount(input);
					DatabaseManager.getInstance().updatePurchaseFertilizer(fert);
				}
				
				notifyDataSetChanged(); 
				return true; // true = close dialog

			}

			
		};
		dlg.show();
	}
	
}
