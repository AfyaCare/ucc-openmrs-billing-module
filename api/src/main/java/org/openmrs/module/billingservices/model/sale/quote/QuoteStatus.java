package org.openmrs.module.billingservices.model.sale.quote;

import org.openmrs.module.billingservices.model.misc.User;

/**
  @Name   : QuoteStatusCode
  @Author : Eric Mwailunga
  October,2019
*/
public class QuoteStatus {
    private int statusCodeId;
    private String name;
    private String quoteType;
    private User creator;
    private String dateCreated;
    private User changedBy;
    private String dateChanged;
    private int retired;
    private String uuid;

    public int getStatusCodeId() {
        return statusCodeId;
    }

    public void setStatusCodeId(int statusCodeId) {
        this.statusCodeId = statusCodeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuoteType() {
        return quoteType;
    }

    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType;
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
