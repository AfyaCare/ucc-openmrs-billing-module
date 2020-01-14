package org.openmrs.module.billingservices.util;

import org.openmrs.module.webservices.rest.web.RestConstants;

public class Constants {
    public final static String URI_PHARMACY_ITEM="https://openmrs/ws/rest/"+ RestConstants.VERSION_1+"/pharmacy/item/read/id";


    public static String getUriPharmacyItem()
    {
        return URI_PHARMACY_ITEM;
    }
}
