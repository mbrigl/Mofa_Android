package it.schmid.android.mofa.adapter;

import it.schmid.android.mofa.model.Purchase;

public interface PurchaseProductInterface {
	
	public Integer getId();
	public Purchase getPurchase();
	public Double getAmount();
	public ProductInterface getProduct();
}
