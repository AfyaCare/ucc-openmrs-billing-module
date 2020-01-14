package org.openmrs.module.billingservices.model.sale.price;


import org.openmrs.module.billingservices.model.misc.User;
import java.util.List;

/**
 @Name  : FinancialPeriod
 @Author: Eric Mwailunga
 October,2019
*/
public class FinancialPeriod {
    private int periodId;
    private String name;
    private String startDate;
    private String endDate;
    private User creator;
    private String dateCreated;
    private User changedBy;
    private String dateChanged;
    private int retired;
    private List<ListVersion> listVersions;
    private String uuid;


    public int getPeriodId() {
        return periodId;
    }

    public void setPeriodId(int periodId) {
        this.periodId = periodId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
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

    public List<ListVersion> getListVersions() {
        return listVersions;
    }

    public void setListVersions(List<ListVersion> listVersions) {
        this.listVersions = listVersions;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
