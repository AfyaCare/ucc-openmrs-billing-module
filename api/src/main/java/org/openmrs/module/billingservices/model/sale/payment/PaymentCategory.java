package org.openmrs.module.billingservices.model.sale.payment;

/**
  @Name   : PaymentCategory
  @Author : Eric Mwailunga
  October,2019
*/
public class PaymentCategory {
    private int Id;
    private String name;


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
