package it.schmid.android.mofa.interfaces;

import it.schmid.android.mofa.model.Purchase;

public interface PurchaseProductInterface {

    Integer getId();

    Purchase getPurchase();

    Double getAmount();

    ProductInterface getProduct();

    String getData();
}
