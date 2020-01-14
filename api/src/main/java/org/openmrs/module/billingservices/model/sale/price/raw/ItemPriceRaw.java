package org.openmrs.module.billingservices.model.sale.price.raw;

public class ItemPriceRaw {
    private int itemPriceId;
    private int priceList;
    private int item;
    private int serviceType;
    private int paymentCategory;
    private double sellingPrice;
    private int creator;
    private String dateCreated;
    private int changedBy;
    private String dateChanged;
    private int retired;
    private String uuid;

    public int getItemPriceId() {
        return itemPriceId;
    }

    public void setItemPriceId(int itemPriceId) {
        this.itemPriceId = itemPriceId;
    }

    public int getPriceList() {
        return priceList;
    }

    public void setPriceList(int priceList) {
        this.priceList = priceList;
    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }

    public int getPaymentCategory() {
        return paymentCategory;
    }

    public void setPaymentCategory(int paymentCategory) {
        this.paymentCategory = paymentCategory;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(int changedBy) {
        this.changedBy = changedBy;
    }

    public String getDateChanged() {
        return dateChanged;
    }

    public void setDateChanged(String dateChanged) {
        this.dateChanged = dateChanged;
    }

    public int getRetired() {
        return retired;
    }

    public void setRetired(int retired) {
        this.retired = retired;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
