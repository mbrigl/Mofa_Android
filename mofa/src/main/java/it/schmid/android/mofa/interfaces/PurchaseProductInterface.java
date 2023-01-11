package it.schmid.android.mofa.interfaces;

import it.schmid.android.mofa.interfaces.ProductInterface;
import it.schmid.android.mofa.model.Purchase;

public interface PurchaseProductInterface {
	
	public Integer getId();
	public Purchase getPurchase();
	public Double getAmount();
	public ProductInterface getProduct();
	public String getData();
}
