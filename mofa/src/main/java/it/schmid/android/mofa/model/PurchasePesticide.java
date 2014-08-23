package it.schmid.android.mofa.model;

import it.schmid.android.mofa.adapter.PurchaseProductInterface;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;

public class PurchasePesticide implements PurchaseProductInterface{
	public final static String PURCHASE_ID_FIELD_NAME = "purchase_id";
	public final static String PESTICIDE_ID_FIELD_NAME = "pest_id";
	@DatabaseField(generatedId = true)
	private Integer id;
	@DatabaseField(foreign = true, columnName = PURCHASE_ID_FIELD_NAME)
	@Expose
	private Purchase purchase;
	@DatabaseField(foreign = true, columnName = PESTICIDE_ID_FIELD_NAME)
	@Expose
	private Pesticide pesticide;
	@DatabaseField
	@Expose
	private Double amount;
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
	public Pesticide getProduct() {
		return pesticide;
	}
	public void setProduct(Pesticide pesticide) {
		this.pesticide = pesticide;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	
}
