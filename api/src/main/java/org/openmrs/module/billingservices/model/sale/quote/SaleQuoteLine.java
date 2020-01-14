package org.openmrs.module.billingservices.model.sale.quote;

import org.openmrs.module.billingservices.model.misc.User;
import org.openmrs.module.billingservices.model.sale.discount.Discount;
import org.openmrs.module.billingservices.model.sale.price.ItemPrice;
import org.openmrs.module.billingservices.model.saleable.Item;
import org.openmrs.module.billingservices.model.saleable.ServiceType;

/**
  @Name   : SaleQuoteLine
  @Author : Eric Mwailunga
  October,2019
*/
public class SaleQuoteLine {
    private int quoteLineId;
    private SaleQuote saleQuote;
    private Item item;
    private ServiceType serviceType;
    private ItemPrice itemPrice;
    private int quantity;
    private String unit;
    private double quotedAmount;
    private Discount discount;
    private double payableAmount;
    private QuoteStatus quoteStatus;
    private String dateCreated;
    private User changedBy;
    private String dateChanged;
    private String uuid;

    public int getQuoteLineId() {
        return quoteLineId;
    }

    public void setQuoteLineId(int quoteLineId) {
        this.quoteLineId = quoteLineId;
    }

    public SaleQuote getSaleQuote() {
        return saleQuote;
    }

    public void setSaleQuote(SaleQuote saleQuote) {
        this.saleQuote = saleQuote;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public ItemPrice getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(ItemPrice itemPrice) {
        this.itemPrice = itemPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getQuotedAmount() {
        return quotedAmount;
    }

    public void setQuotedAmount(double quotedAmount) {
        this.quotedAmount = quotedAmount;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    public double getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(double payableAmount) {
        this.payableAmount = payableAmount;
    }

    public QuoteStatus getQuoteStatus() {
        return quoteStatus;
    }

    public void setQuoteStatus(QuoteStatus quoteStatus) {
        this.quoteStatus = quoteStatus;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public User getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(User changedBy) {
        this.changedBy = changedBy;
    }

    public String getDateChanged() {
        return dateChanged;
    }

    public void setDateChanged(String dateChanged) {
        this.dateChanged = dateChanged;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
