package org.openmrs.module.billingservices.model.sale.discount;

import org.openmrs.module.billingservices.model.misc.User;
import org.openmrs.module.billingservices.model.sale.quote.SaleQuoteLine;

/**
  @Name   : Discount
  @Author : Eric Mwailunga
  October,2019
*/
public class Discount {
    private int discountId;
    private SaleQuoteLine saleQuoteLine;
    private double originalQuotedAmount;
    private double proposedDiscountAmount;
    private double approvedDiscountAmount;
    private DiscountCriteria discountCriteria;
    private User initiatedBy;
    private int approved;
    private User approvedBy;
    private String dateCreated;
    private String dateApproved;
    private String uuid;


    public int getDiscountId() {
        return discountId;
    }

    public void setDiscountId(int discountId) {
        this.discountId = discountId;
    }

    public SaleQuoteLine getSaleQuoteLine() {
        return saleQuoteLine;
    }

    public void setSaleQuoteLine(SaleQuoteLine saleQuoteLine) {
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

    public DiscountCriteria getDiscountCriteria() {
        return discountCriteria;
    }

    public void setDiscountCriteria(DiscountCriteria discountCriteria) {
        this.discountCriteria = discountCriteria;
    }

    public User getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(User initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    public int getApproved() {
        return approved;
    }

    public void setApproved(int approved) {
        this.approved = approved;
    }

    public User getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(User approvedBy) {
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
