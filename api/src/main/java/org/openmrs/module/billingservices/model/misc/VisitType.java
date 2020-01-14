package org.openmrs.module.billingservices.model.misc;


/**
  @Name   : VisitType
  @Author : Eric Mwailunga
  October,2019
*/
public class VisitType {
    private int visitTypeId;
    private String name;
    private String uuid;

    public int getVisitTypeId() {
        return visitTypeId;
    }

    public void setVisitTypeId(int visitTypeId) {
        this.visitTypeId = visitTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
