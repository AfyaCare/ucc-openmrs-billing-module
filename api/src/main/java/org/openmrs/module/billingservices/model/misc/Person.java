package org.openmrs.module.billingservices.model.misc;

/**
  @Name   : Person
  @Author : Eric Mwailunga
  October,2019
*/
public class Person {
    private int personId;
    private String firstName;
    private String LastName;
    private String uuid;

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
