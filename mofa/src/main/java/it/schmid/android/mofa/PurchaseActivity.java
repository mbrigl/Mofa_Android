package it.schmid.android.mofa;

import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.schmid.android.mofa.adapter.ExpandablePurchaseAdapter;
import it.schmid.android.mofa.db.DatabaseManager;
import it.schmid.android.mofa.interfaces.PurchaseProductInterface;
import it.schmid.android.mofa.model.Purchase;
import it.schmid.android.mofa.model.PurchaseFertilizer;
import it.schmid.android.mofa.model.PurchasePesticide;

public class PurchaseActivity extends DashboardActivity implements OnDateSetListener, ExpandablePurchaseAdapter.OnExpandableListener, SendingProcess.RemoveEntries {
    private static final String TAG = "PurchaseActivity";
    private List<Purchase> purchasingList;
    Boolean firstCallDatePicker = false; // Bug Datepicker
    SparseArray<List<PurchaseProductInterface>> purchasedProducts;
    ExpandableListView purchaseListView;
    ExpandablePurchaseAdapter listPurchaseAdapter;
    Boolean expandList = false;
    Integer groupPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        DatabaseManager.init(this);
        //inflating the layout
        setContentView(R.layout.purchasing_list);
        // getting the Listview
        purchaseListView = (ExpandableListView) findViewById(R.id.purchasinglistview);
        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createPurchase();
            }
        });
        // loading data
        loadData();

    }

    private void loadData() {

        purchasingList = DatabaseManager.getInstance().getAllPurchasesOrderByDate();
        purchasedProducts = new SparseArray<List<PurchaseProductInterface>>();
        for (Purchase p : purchasingList) {
            List<PurchaseProductInterface> productList = new ArrayList<PurchaseProductInterface>();
            List<PurchasePesticide> purPest = DatabaseManager.getInstance().getPurchasePesticideByPurchaseId(p.getId());
            List<PurchaseFertilizer> purFert = DatabaseManager.getInstance().getPurchaseFertilizerByPurchaseId(p.getId());
            productList.addAll(purPest);
            productList.addAll(purFert);
            addChildItems(p.getId(), productList);


        }
        listPurchaseAdapter = new ExpandablePurchaseAdapter(this, purchasingList, purchasedProducts);
        purchaseListView.setAdapter(listPurchaseAdapter);
    }

    private void addChildItems(Integer purchaseId, List<? extends PurchaseProductInterface> products) {
        purchasedProducts.put(purchaseId, (List<PurchaseProductInterface>) products);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //inflating the menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.purchase_menu, menu);
        return true;
    }

    // Reaction to the menu selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //				case R.id.purchase_menu_add:
        //					Log.d(TAG, "Adding an new purchase");
        //					createPurchase();
        //					return true;
        if (item.getItemId() == R.id.purchase_menu_upload) {
            Log.d(TAG, "Upload purchases");
            showUploadDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createPurchase() {
        DialogFragment newFragment = new DatePickerDialogFragment(this);
        // newFragment.setCancelable(true);
        firstCallDatePicker = true;
        newFragment.show(getSupportFragmentManager(), "datePicker");

    }

    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {

        if (firstCallDatePicker) {
            Date newDate = new Date(year - 1900, monthOfYear, dayOfMonth);
            Purchase pur = new Purchase();
            pur.setDate(newDate);
            DatabaseManager.getInstance().addPurchase(pur);
            loadData();
        }
        firstCallDatePicker = false;

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "[onResume]- Loading Data ");
        loadData();
        if (expandList) {
            purchaseListView.expandGroup(groupPosition);
        }


    }

    public void onExpanded(int groupPos) {
        expandList = true;
        this.groupPosition = groupPos;

    }

    /**
     * export dialog
     */
    private void showUploadDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PurchaseActivity.this);
        alertDialog.setTitle(getString(R.string.export_purchase_title));
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.export_purchase_message));
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        alertDialog.setView(linearLayout);
        alertDialog.setMessage(sb);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //sendData();
                SendingProcess sending = new SendingProcess(PurchaseActivity.this, ActivityConstants.PURCHASING_ACTIVITY);
                sending.sendData();
            }
        });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public void deleteAllEntries() {
        for (Purchase p : purchasingList) {
            DatabaseManager.getInstance().deletePurchaseAndProducts(p);
        }
        loadData();

    }

}
