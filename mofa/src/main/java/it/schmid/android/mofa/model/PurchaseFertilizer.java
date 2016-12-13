package it.schmid.android.mofa.model;

import it.schmid.android.mofa.interfaces.PurchaseProductInterface;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;

public class PurchaseFertilizer implements PurchaseProductInterface{
	public final static String PURCHASE_ID_FIELD_NAME = "purchase_id";
	public final static String FERTILIZER_ID_FIELD_NAME = "fert_id";
	@DatabaseField(generatedId = true)
	private Integer id;
	@DatabaseField(foreign = true, columnName = PURCHASE_ID_FIELD_NAME)
	@Expose
	private Purchase purchase;
	@DatabaseField(foreign = true, columnName = FERTILIZER_ID_FIELD_NAME)
	@Expose
	private Fertilizer fertilizer;
	@DatabaseField
	@Expose
	private Double amount;
	@DatabaseField
	private String data;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Purchase getPurchase() {
		return purchase;
	}
	public void setPurchase(Purchase purchase) {
		this.purchase = purchase;
	}
	public Fertilizer getProduct() {
		return fertilizer;
	}
	public void setProduct(Fertilizer fertilizer) {
		this.fertilizer = fertilizer;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
