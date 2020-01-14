package org.openmrs.module.billingservices.model.sale.order.raw;

public class OrderByQuoteRaw {
    private int soqNo;
    private String datedSaleId;
    private int saleQuote;
    private String paymentMethod;
    private double payableAmount;
    private double paidAmount;
    private double debtAmount;
    private int installmentFrequency;
    private int creator;
    private String dateCreated;
    private String uuid;

    public int getSoqNo() {
        return soqNo;
    }

    public void setSoqNo(int soqNo) {
        this.soqNo = soqNo;
    }

    public String getDatedSaleId() {
        return datedSaleId;
    }

    public void setDatedSaleId(String datedSaleId) {
        this.datedSaleId = datedSaleId;
    }

    public int getSaleQuote() {
        return saleQuote;
    }

    public void setSaleQuote(int saleQuote) {
        this.saleQuote = saleQuote;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(double payableAmount) {
        this.payableAmount = payableAmount;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public double getDebtAmount() {
        return debtAmount;
    }

    public void setDebtAmount(double debtAmount) {
        this.debtAmount = debtAmount;
    }

    public int getInstallmentFrequency() {
        return installmentFrequency;
    }

    public void setInstallmentFrequency(int installmentFrequency) {
        this.installmentFrequency = installmentFrequency;
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
