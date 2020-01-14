package org.openmrs.module.billingservices.model.saleable;


/**
  @Name   : Item
  @Author : Eric Mwailunga
  October,2019
*/
public class Item {
    private int itemId;
    private String name;
    private String uuid;

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
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
