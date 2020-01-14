package org.openmrs.module.billingservices.model.misc;

/**
  @Name   : User
  @Author : Eric Mwailunga
  October,2019
*/
public class User {
    private int userId;
    private Person person;
    private String uuid;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
