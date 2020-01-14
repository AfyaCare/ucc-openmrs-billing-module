package org.openmrs.module.billingservices.model.sale.discount.raw;

import java.util.Date;

public class DiscountCriteriaRaw {
    private int criteriaNo;
    private String name;
    private String description;
    private int creator;
    private Date dateCreated;
    private Integer changedBy;
    private Date dateChanged;
    private int retired;
    private String uuid;


    public int getCriteriaNo() {
        return criteriaNo;
    }

    public void setCriteriaNo(int criteriaNo) {
        this.criteriaNo = criteriaNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Integer getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(Integer changedBy) {
        this.changedBy = changedBy;
    }

    public Date getDateChanged() {
        return dateChanged;
    }

    public void setDateChanged(Date dateChanged) {
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
