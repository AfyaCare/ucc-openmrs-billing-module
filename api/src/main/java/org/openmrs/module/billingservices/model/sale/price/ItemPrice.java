package org.openmrs.module.billingservices.model.sale.price;

import org.openmrs.module.billingservices.model.misc.User;
import org.openmrs.module.billingservices.model.sale.payment.PaymentCategory;
import org.openmrs.module.billingservices.model.saleable.Item;
import org.openmrs.module.billingservices.model.saleable.ServiceType;

/**
  @Name   : ItemPrice
  @Author : Eric Mwailunga
  October,2019
*/
public class ItemPrice {
    private int itemPriceId;
    private ListVersion listVersion;
    private Item item;
    private ServiceType serviceType;
    private PaymentCategory paymentCategory;
    private PaymentCategory paymentSubCategory;
    private double sellingPrice;
    private User creator;
    private String dateCreated;
    private User changedBy;
    private String dateChanged;
    private boolean retired;
    private String uuid;


    public int getItemPriceId() {
        return itemPriceId;
    }

    public void setItemPriceId(int itemPriceId) {
        this.itemPriceId = itemPriceId;
    }

    public ListVersion getListVersion() {
        return listVersion;
    }

    public void setListVersion(ListVersion listVersion) {
        this.listVersion = listVersion;
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

    public PaymentCategory getPaymentCategory() {
        return paymentCategory;
    }

    public void setPaymentCategory(PaymentCategory paymentCategory) {
        this.paymentCategory = paymentCategory;
    }

    public PaymentCategory getPaymentSubCategory() {
        return paymentSubCategory;
    }

    public void setPaymentSubCategory(PaymentCategory paymentSubCategory) {
        this.paymentSubCategory = paymentSubCategory;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
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

    public boolean isRetired() {
        return retired;
    }

    public void setRetired(boolean retired) {
        this.retired = retired;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
