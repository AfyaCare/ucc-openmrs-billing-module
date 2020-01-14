package org.openmrs.module.billingservices.model.sale.quote.raw;

public class SaleQuoteRaw {
    private int quoteId;
    private int patient;
    private int visit;
    private int paymentCategory;
    private int paymentSubCategory;
    private Integer orderedBy;
    private double totalQuote;
    private double totalPayable;
    private int status;
    private int discounted;
    private String dateOrdered;
    private Integer changedBy;
    private String dateChanged;
    private String uuid;


    public int getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(int quoteId) {
        this.quoteId = quoteId;
    }

    public int getPatient() {
        return patient;
    }

    public void setPatient(int patient) {
        this.patient = patient;
    }

    public int getVisit() {
        return visit;
    }

    public void setVisit(int visit) {
        this.visit = visit;
    }

    public int getPaymentCategory() {
        return paymentCategory;
    }

    public void setPaymentCategory(int paymentCategory) {
        this.paymentCategory = paymentCategory;
    }

    public int getPaymentSubCategory() {
        return paymentSubCategory;
    }

    public void setPaymentSubCategory(int paymentSubCategory) {
        this.paymentSubCategory = paymentSubCategory;
    }

    public Integer getOrderedBy() {
        return orderedBy;
    }

    public void setOrderedBy(Integer orderedBy) {
        this.orderedBy = orderedBy;
    }

    public double getTotalQuote() {
        return totalQuote;
    }

    public void setTotalQuote(double totalQuote) {
        this.totalQuote = totalQuote;
    }

    public double getTotalPayable() {
        return totalPayable;
    }

    public void setTotalPayable(double totalPayable) {
        this.totalPayable = totalPayable;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDiscounted() {
        return discounted;
    }

    public void setDiscounted(int discounted) {
        this.discounted = discounted;
    }

    public String getDateOrdered() {
        return dateOrdered;
    }

    public void setDateOrdered(String dateOrdered) {
        this.dateOrdered = dateOrdered;
    }

    public Integer getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(Integer changedBy) {
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
