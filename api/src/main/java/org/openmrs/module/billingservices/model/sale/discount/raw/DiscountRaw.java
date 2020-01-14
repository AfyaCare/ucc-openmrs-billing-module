package org.openmrs.module.billingservices.model.sale.discount.raw;

public class DiscountRaw {
    private int discountId;
    private int saleQuoteLine;
    private double originalQuotedAmount;
    private double proposedDiscountAmount;
    private double approvedDiscountAmount;
    private int discountCriteria;
    private int initiatedBy;
    private int approved;
    private int approvedBy;
    private String dateCreated;
    private String dateApproved;
    private String uuid;


    public int getDiscountId() {
        return discountId;
    }

    public void setDiscountId(int discountId) {
        this.discountId = discountId;
    }

    public int getSaleQuoteLine() {
        return saleQuoteLine;
    }

    public void setSaleQuoteLine(int saleQuoteLine) {
        this.saleQuoteLine = saleQuoteLine;
    }

    public double getOriginalQuotedAmount() {
        return originalQuotedAmount;
    }

    public void setOriginalQuotedAmount(double originalQuotedAmount) {
        this.originalQuotedAmount = originalQuotedAmount;
    }

    public double getProposedDiscountAmount() {
        return proposedDiscountAmount;
    }

    public void setProposedDiscountAmount(double proposedDiscountAmount) {
        this.proposedDiscountAmount = proposedDiscountAmount;
    }

    public double getApprovedDiscountAmount() {
        return approvedDiscountAmount;
    }

    public void setApprovedDiscountAmount(double approvedDiscountAmount) {
        this.approvedDiscountAmount = approvedDiscountAmount;
    }

    public int getDiscountCriteria() {
        return discountCriteria;
    }

    public void setDiscountCriteria(int discountCriteria) {
        this.discountCriteria = discountCriteria;
    }

    public int getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(int initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    public int getApproved() {
        return approved;
    }

    public void setApproved(int approved) {
        this.approved = approved;
    }

    public int getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(int approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateApproved() {
        return dateApproved;
    }

    public void setDateApproved(String dateApproved) {
        this.dateApproved = dateApproved;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
