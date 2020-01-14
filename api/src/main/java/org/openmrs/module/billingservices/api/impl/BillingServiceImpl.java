/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.billingservices.api.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.api.ConceptService;
import org.openmrs.api.PatientService;
import org.openmrs.api.VisitService;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.billingservices.api.BillingService;
import org.openmrs.module.billingservices.api.db.BillingDAO;
import org.openmrs.module.billingservices.model.misc.*;
import org.openmrs.module.billingservices.model.sale.discount.Discount;
import org.openmrs.module.billingservices.model.sale.discount.DiscountCriteria;
import org.openmrs.module.billingservices.model.sale.discount.raw.DiscountCriteriaRaw;
import org.openmrs.module.billingservices.model.sale.discount.raw.DiscountRaw;
import org.openmrs.module.billingservices.model.sale.order.OrderByQuote;
import org.openmrs.module.billingservices.model.sale.order.OrderByQuoteLine;
import org.openmrs.module.billingservices.model.sale.order.raw.OrderByQuoteLineRaw;
import org.openmrs.module.billingservices.model.sale.order.raw.OrderByQuoteRaw;
import org.openmrs.module.billingservices.model.sale.payment.OrderPayInstallment;
import org.openmrs.module.billingservices.model.sale.payment.PaymentCategory;
import org.openmrs.module.billingservices.model.sale.payment.raw.OrderPayInstallmentRaw;
import org.openmrs.module.billingservices.model.sale.price.FinancialPeriod;
import org.openmrs.module.billingservices.model.sale.price.ItemPrice;
import org.openmrs.module.billingservices.model.sale.price.ListVersion;
import org.openmrs.module.billingservices.model.sale.price.raw.FinancialPeriodRaw;
import org.openmrs.module.billingservices.model.sale.price.raw.ItemPriceRaw;
import org.openmrs.module.billingservices.model.sale.price.raw.ListVersionRaw;
import org.openmrs.module.billingservices.model.sale.quote.QuoteStatus;
import org.openmrs.module.billingservices.model.sale.quote.SaleQuote;
import org.openmrs.module.billingservices.model.sale.quote.SaleQuoteLine;
import org.openmrs.module.billingservices.model.sale.quote.raw.QuoteStatusRaw;
import org.openmrs.module.billingservices.model.sale.quote.raw.SaleQuoteLineRaw;
import org.openmrs.module.billingservices.model.sale.quote.raw.SaleQuoteRaw;
import org.openmrs.module.billingservices.model.saleable.ServiceType;
import org.openmrs.module.billingservices.model.saleable.raw.ServiceTypeRaw;
import org.openmrs.module.pharmacy.api.PharmacyService;
import org.openmrs.module.pharmacy.model.item.Item;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

//import org.openmrs.module.pharmacy.api.PharmacyService;
//import org.openmrs.module.pharmacy.model.item.Item;

/**
 * Implementation of {@link BillingService}.
 */
public class BillingServiceImpl extends BaseOpenmrsService implements BillingService {

    private final Log log = LogFactory.getLog(this.getClass());
    private BillingDAO billingDAO;

    public BillingDAO getBillingDAO() {
        return billingDAO;
    }

    public void setBillingDAO(BillingDAO billingDAO) {
        this.billingDAO = billingDAO;
    }


/**
  BillingService specified methods
  @Author: Eric Mwailunga
  October,2019
*/
    //...................Format date value...........................//
    private String formatDateTime(String dateTime) {
        log.info("Received datetime: "+dateTime);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss", Locale.ENGLISH);
        Date date = null;
        String formattedDateTime = dateTime;
        try {
            date = dateFormatter.parse(dateTime);
            log.info("Parsed datetime: "+date);
        } catch (ParseException e) {
            log.info(e.getMessage());
        }
        if(date!=null)
            formattedDateTime = dateFormatter.format(date);

        return formattedDateTime;
    }

    //...................Current user from context...........................//
    private org.openmrs.User getCurrentUserFromContext()
    {
        return Context.getUserContext().getAuthenticatedUser();
    }

    //...................Get Openmrs user by Id...........................//
    private org.openmrs.User getOpenmrsUserById(int userId)
    {
        return Context.getUserService().getUser(userId);
    }

    //...................Get custom user by Id...........................//
    private User getCustomUserById(int userId)
    {
        org.openmrs.User openmrsUser=Context.getUserService().getUser(userId);
        User customUser=new User();
        customUser.setUserId(userId);
        customUser.setPerson(getCustomPerson(getOpenmrsPersonByUserId(userId)));
        customUser.setUuid(openmrsUser.getUuid());
        return customUser;
    }

    //.................Get Openmrs person by user Id..................//
    private org.openmrs.Person getOpenmrsPersonByUserId(int userId)
    {
        return Context.getUserService().getUser(userId).getPerson();
    }

    //...................Get custom person.....................//
    private Person getCustomPerson(org.openmrs.Person person)
    {
        Person customPerson=new Person();
        customPerson.setPersonId(person.getPersonId());
        customPerson.setFirstName(person.getGivenName());
        customPerson.setLastName(person.getFamilyName());
        customPerson.setUuid(person.getUuid());
        return customPerson;
    }

    //...................Get custom patient.....................//
    private Patient getCustomPatient(org.openmrs.Patient openmrsPatient)
    {
        Patient patient = new Patient();
        patient.setPatientId(openmrsPatient.getPatientId());
        patient.setFirstName(openmrsPatient.getGivenName());
        patient.setLastName(openmrsPatient.getMiddleName());
        patient.setLastName(openmrsPatient.getFamilyName());
        patient.setIdentifier(openmrsPatient.getPatientIdentifier().getIdentifier());
        patient.setUuid(openmrsPatient.getUuid());
        return patient;
    }

    //...................Get custom visit.....................//
    private Visit getCustomVisit(org.openmrs.Visit openmrsVisit)
    {
        VisitType visitType = new VisitType();
        visitType.setVisitTypeId(openmrsVisit.getVisitType().getVisitTypeId());
        visitType.setName(openmrsVisit.getVisitType().getName());
        visitType.setUuid(openmrsVisit.getVisitType().getUuid());

        Visit visit = new Visit();
        visit.setVisitId(openmrsVisit.getVisitId());
        visit.setDateStarted(openmrsVisit.getStartDatetime().toString());
        visit.setEndDate(openmrsVisit.getStopDatetime().toString());
        visit.setVisitType(visitType);
        visit.setUuid(openmrsVisit.getUuid());
        return visit;
    }

    //...................Get custom payment category.....................//
    private PaymentCategory getCustomPaymentCategory(int categoryId)
    {
        ConceptService conceptService=Context.getConceptService();
        Concept concept = conceptService.getConcept(categoryId);
        PaymentCategory paymentCategory = new PaymentCategory();
        paymentCategory.setId(categoryId);
        paymentCategory.setName(concept.getName().getName());
        return paymentCategory;
    }

    //...................Getting service type from raw...........................//
    private ServiceType getServiceTypeFromServiceTypeRaw(ServiceTypeRaw serviceTypeRaw)
    {
        ServiceType serviceType=new ServiceType();

        serviceType.setServiceTypeId(serviceTypeRaw.getServiceTypeId());
        serviceType.setName(serviceTypeRaw.getName());
        serviceType.setCreator(getCustomUserById(serviceTypeRaw.getCreator()));
        serviceType.setDateCreated(serviceTypeRaw.getDateCreated().toString());
        if(serviceTypeRaw.getChangedBy()!=null)
        {
            serviceType.setChangedBy(getCustomUserById(serviceTypeRaw.getChangedBy()));
            serviceType.setDateChanged(serviceTypeRaw.getDateChanged().toString());
        }
        serviceType.setRetired(serviceTypeRaw.getRetired());
        serviceType.setUuid(serviceTypeRaw.getUuid());

        return serviceType;
    }

    //...................Getting price list from raw...........................//
    private FinancialPeriod getFinancialPeriodFromFinancialPeriodRaw(FinancialPeriodRaw financialPeriodRaw)
    {
        FinancialPeriod financialPeriod=new FinancialPeriod();

        financialPeriod.setPeriodId(financialPeriodRaw.getPeriodId());
        financialPeriod.setName(financialPeriodRaw.getName());
        financialPeriod.setStartDate(financialPeriodRaw.getStartDate().toString());
        financialPeriod.setEndDate(financialPeriodRaw.getEndDate().toString());
        financialPeriod.setCreator(getCustomUserById(financialPeriodRaw.getCreator()));
        financialPeriod.setDateCreated(financialPeriodRaw.getDateCreated().toString());
        if(financialPeriodRaw.getChangedBy()!=null) {
            financialPeriod.setChangedBy(getCustomUserById(financialPeriodRaw.getChangedBy()));
            financialPeriod.setDateChanged(financialPeriodRaw.getDateChanged().toString());
        }
        financialPeriod.setRetired(financialPeriodRaw.getRetired());
        //financialPeriod.setPriceList(null);
        financialPeriod.setUuid(financialPeriodRaw.getUuid());
        return financialPeriod;
    }

    //...................Getting price list from raw...........................//
    private ListVersion getPriceListFromPriceListRaw(ListVersionRaw listVersionRaw)
    {
        ListVersion listVersion=new ListVersion();

        listVersion.setListId(listVersionRaw.getListId());
        listVersion.setFinancialPeriod(fetchFinancialPeriodById(listVersionRaw.getFinancialPeriodId()));
        listVersion.setVersionName(listVersionRaw.getVersionName());
        listVersion.setActive(listVersionRaw.getActive());
        listVersion.setDateApproved(listVersionRaw.getDateApproved().toString());
        listVersion.setCreator(getCustomUserById(listVersionRaw.getCreator()));
        listVersion.setDateCreated(listVersionRaw.getDateCreated().toString());
        if(listVersionRaw.getChangedBy()!=null)
        {
            listVersion.setChangedBy(getCustomUserById(listVersionRaw.getChangedBy()));
            listVersion.setDateChanged(listVersionRaw.getDateChanged().toString());
        }
        listVersion.setUuid(listVersionRaw.getUuid());
        return listVersion;
    }

    //...................Setting price lists for financial period lists...........................//
    private List<FinancialPeriod> setPriceListsForFinancialPeriods(List<FinancialPeriod> financialPeriodList)
    {
        List<FinancialPeriod> financialPeriods=new ArrayList<>();
        if(financialPeriodList.size()>0)
        {
            for(FinancialPeriod financialPeriod : financialPeriodList)
            {
                List<ListVersion> listVersions=new ArrayList<>();
                List<ListVersionRaw> listVersionRaws=billingDAO.fetchPriceListsByFinancialPeriodId(financialPeriod.getPeriodId());
                if(listVersionRaws.size()>0)
                {
                    for(ListVersionRaw listVersionRaw : listVersionRaws)
                    {
                        listVersions.add(getPriceListFromPriceListRaw(listVersionRaw));
                    }
                }
                financialPeriod.setListVersions(listVersions);
                financialPeriods.add(financialPeriod);
            }
        }

        return financialPeriods;
    }

    //................... Service category URI.................................//

    //...................Getting item price from it's raw...........................//
    private ItemPrice getItemPriceFromItemPriceRaw(ItemPriceRaw itemPriceRaw)
    {
        PharmacyService pharmacyService = Context.getService(PharmacyService.class);
        ConceptService conceptService=Context.getConceptService();
        ItemPrice itemPrice=new ItemPrice();
        ServiceTypeRaw serviceTypeRaw=billingDAO.fetchServiceTypeById(itemPriceRaw.getItemPriceId());
        if(serviceTypeRaw!=null)
        {
            Concept concept; String itemName;
            ServiceType serviceType=getServiceTypeFromServiceTypeRaw(serviceTypeRaw);

            org.openmrs.module.billingservices.model.saleable.Item billingItem= new org.openmrs.module.billingservices.model.saleable.Item();
            billingItem.setItemId(itemPriceRaw.getItem());

            switch (serviceType.getName())
            {
                case "Laboratory":
                case "Radiology":
                case "Procedure":
                    concept= conceptService.getConcept(itemPriceRaw.getItem());
                    itemName=concept.getShortNameInLocale(Locale.ENGLISH).getName();
                    if(itemName==null)
                    {
                        itemName=concept.getFullySpecifiedName(Locale.ENGLISH).getName();
                    }
                    billingItem.setName(itemName);
                    billingItem.setUuid(concept.getUuid());
                    break;

                case "Pharmaceutical":
                    Item item = pharmacyService.getItemById(itemPriceRaw.getItem());
                    billingItem.setName(item.getItemName());
                    billingItem.setUuid(item.getItemUuid());
                    break;

                case "Registration Fees":
                    //
                    break;
                case "Admission Fees":
                    //
                    break;
            }

            PaymentCategory paymentCategory=new PaymentCategory();
            paymentCategory.setId(itemPriceRaw.getPaymentCategory());
            paymentCategory.setName(conceptService.getConcept(itemPriceRaw.getPaymentCategory()).getName().getName());

            itemPrice.setItemPriceId(itemPriceRaw.getItemPriceId());
            itemPrice.setListVersion(fetchPriceListVersionById(itemPriceRaw.getPriceList()));
            itemPrice.setServiceType(serviceType);
            itemPrice.setItem(billingItem);
            itemPrice.setPaymentCategory(paymentCategory);
            itemPrice.setSellingPrice(itemPriceRaw.getSellingPrice());
            itemPrice.setCreator(getCustomUserById(itemPriceRaw.getCreator()));
            itemPrice.setDateCreated(itemPriceRaw.getDateCreated());
            itemPrice.setChangedBy(getCustomUserById(itemPriceRaw.getChangedBy()));
            itemPrice.setDateChanged(itemPriceRaw.getDateChanged());
            boolean retired=false;
            if(itemPriceRaw.getRetired()==1)
            {
                retired=true;
            }
            itemPrice.setRetired(retired);
            itemPrice.setUuid(itemPriceRaw.getUuid());

        }
        return itemPrice;
    }

    //...................Getting item from it's uuid and service type...........................//
    private org.openmrs.module.billingservices.model.saleable.Item getItemFromItemUuidAndServiceType(String itemUuid, ServiceType serviceType)
    {
        PharmacyService pharmacyService = Context.getService(PharmacyService.class);
        ConceptService conceptService=Context.getConceptService();
        org.openmrs.module.billingservices.model.saleable.Item item = new org.openmrs.module.billingservices.model.saleable.Item();

        switch (serviceType.getName())
        {
            case "Laboratory":
            case "Radiology":
            case "Procedure":
                Concept concept= conceptService.getConceptByUuid(itemUuid);
                String itemName=concept.getShortNameInLocale(Locale.ENGLISH).getName();
                if(itemName==null)
                {
                    itemName=concept.getFullySpecifiedName(Locale.ENGLISH).getName();
                }
                item.setName(itemName);
                item.setUuid(concept.getUuid());
                break;

            case "Pharmacy":
                Item pharmacyItem=pharmacyService.getItemByUuid(itemUuid);
                item.setName(pharmacyItem.getItemName());
                item.setUuid(pharmacyItem.getItemUuid());
                break;

            case "Registration Fees":
                //
                break;
            case "Admission Fees":
                //
                break;
        }

        return item;
    }

    //...................Getting item from it's id and service type...........................//
    private org.openmrs.module.billingservices.model.saleable.Item getItemFromItemIdAndServiceType(int itemId, ServiceType serviceType)
    {
        PharmacyService pharmacyService = Context.getService(PharmacyService.class);
        ConceptService conceptService=Context.getConceptService();
        org.openmrs.module.billingservices.model.saleable.Item item = new org.openmrs.module.billingservices.model.saleable.Item();

        switch (serviceType.getName())
        {
            case "Laboratory":
            case "Radiology":
            case "Procedure":
                Concept concept= conceptService.getConcept(itemId);
                String itemName=concept.getShortNameInLocale(Locale.ENGLISH).getName();
                if(itemName==null) {
                    itemName=concept.getFullySpecifiedName(Locale.ENGLISH).getName();
                }
                item.setName(itemName);
                item.setUuid(concept.getUuid());
                break;

            case "Pharmacy":
                Item pharmacyItem = pharmacyService.getItemById(itemId);
                item.setName(pharmacyItem.getItemName());
                item.setUuid(pharmacyItem.getItemUuid());
                break;

            case "Registration Fees":
                //
                break;
            case "Admission Fees":
                //
                break;
        }

        return item;
    }

    //...................Getting quote status from raw...........................//
    private QuoteStatus getQuoteStatusFromQuoteStatusRaw(QuoteStatusRaw quoteStatusRaw)
    {
        QuoteStatus quoteStatus = new QuoteStatus();

        quoteStatus.setStatusCodeId(quoteStatusRaw.getStatusCodeId());
        quoteStatus.setName(quoteStatusRaw.getName());
        quoteStatus.setQuoteType(quoteStatusRaw.getQuoteType());
        quoteStatus.setCreator(getCustomUserById(quoteStatusRaw.getCreator()));
        quoteStatus.setDateCreated(quoteStatusRaw.getDateCreated());
        quoteStatus.setChangedBy(getCustomUserById(quoteStatusRaw.getChangedBy()));
        quoteStatus.setRetired(quoteStatusRaw.getRetired());
        quoteStatus.setUuid(quoteStatusRaw.getUuid());

        return quoteStatus;
    }

    //...................Getting sale quote from raw...........................//
    private SaleQuote getSaleQuoteFromSaleQuoteRaw(SaleQuoteRaw saleQuoteRaw)
    {
        PatientService patientService = Context.getPatientService();
        VisitService visitService = Context.getVisitService();

        SaleQuote saleQuote = new SaleQuote();
        List<SaleQuoteLine> saleQuoteLines = new ArrayList<>();
        org.openmrs.Patient openmrsPatient = patientService.getPatient(saleQuoteRaw.getPatient());
        org.openmrs.Visit openmrsVisit = visitService.getVisit(saleQuoteRaw.getVisit());
        QuoteStatus quoteStatus = this.fetchQuoteStatusCodeById(saleQuoteRaw.getStatus());

        saleQuote.setQuoteId(saleQuoteRaw.getQuoteId());
        saleQuote.setPatient(getCustomPatient(openmrsPatient));
        saleQuote.setVisit(getCustomVisit(openmrsVisit));
        saleQuote.setPaymentCategory(getCustomPaymentCategory(saleQuoteRaw.getPaymentCategory()));
        saleQuote.setOrderedBy(getCustomUserById(saleQuoteRaw.getOrderedBy()));
        saleQuote.setTotalQuotedAmount(saleQuoteRaw.getTotalQuote());
        saleQuote.setTotalPayableAmount(saleQuoteRaw.getTotalPayable());
        saleQuote.setQuoteStatus(quoteStatus);
        saleQuote.setDiscounted(saleQuoteRaw.getDiscounted());
        saleQuote.setDateOrdered(saleQuoteRaw.getDateOrdered());
        saleQuote.setDateChanged(saleQuoteRaw.getDateChanged());
        saleQuote.setChangedBy(getCustomUserById(saleQuoteRaw.getChangedBy()));
        saleQuote.setUuid(saleQuoteRaw.getUuid());

        saleQuote.setSaleQuoteLineList(saleQuoteLines);
        return saleQuote;
    }

    //...................Getting sale quote line from raw...........................//
    private SaleQuoteLine getSaleQuoteLineFromSaleQuoteLineRaw(SaleQuoteLineRaw saleQuoteLineRaw)
    {
        SaleQuoteLine saleQuoteLine = new SaleQuoteLine();
        ServiceType serviceType = this.fetchServiceTypeById(saleQuoteLineRaw.getServiceType());
        org.openmrs.module.billingservices.model.saleable.Item item = this.getItemFromItemIdAndServiceType(saleQuoteLineRaw.getItem(),serviceType);
        ItemPrice itemPrice = this.fetchItemPriceByItemPriceId(saleQuoteLineRaw.getQuoteLineId());
        SaleQuote saleQuote = this.fetchSaleQuoteById(saleQuoteLineRaw.getSaleQuote());
        QuoteStatus quoteStatus = this.fetchQuoteStatusCodeById(saleQuoteLineRaw.getStatus());
        User user = this.getCustomUserById(saleQuoteLineRaw.getChangedBy());

        Discount discount = new Discount();
        saleQuoteLine.setQuoteLineId(saleQuoteLineRaw.getQuoteLineId());
        saleQuoteLine.setSaleQuote(saleQuote);
        saleQuoteLine.setItem(item);
        saleQuoteLine.setServiceType(serviceType);
        saleQuoteLine.setItemPrice(itemPrice);
        saleQuoteLine.setQuantity(saleQuoteLineRaw.getQuantity());
        saleQuoteLine.setUnit(saleQuoteLineRaw.getUnit());
        saleQuoteLine.setQuotedAmount(saleQuoteLineRaw.getQuotedAmount());
        saleQuoteLine.setPayableAmount(saleQuoteLineRaw.getPayableAmount());
        saleQuoteLine.setQuoteStatus(quoteStatus);
        saleQuoteLine.setDateCreated(saleQuoteLineRaw.getDateCreated());
        saleQuoteLine.setChangedBy(user);
        saleQuoteLine.setDateChanged(saleQuoteLineRaw.getDateChanged());
        saleQuoteLine.setUuid(saleQuoteLineRaw.getUuid());
        DiscountRaw discountRaw = billingDAO.fetchDiscountByQuoteLineId(saleQuoteLineRaw.getQuoteLineId());
        if(discountRaw!=null)
        {
            discount = getDiscountFromDiscountRaw(discountRaw);
        }
        saleQuoteLine.setDiscount(discount);
        return null;
    }

    //...................Getting discount criteria from raw...........................//
    private DiscountCriteria getDiscountCriteriaFromDiscountCriteriaRaw(DiscountCriteriaRaw discountCriteriaRaw)
    {
        DiscountCriteria discountCriteria = new DiscountCriteria();
        User user = this.getCustomUserById(discountCriteriaRaw.getCreator());
        //User changer = this.getCustomUserById(discountCriteriaRaw.getChangedBy());

        discountCriteria.setCriteriaId(discountCriteriaRaw.getCriteriaNo());
        discountCriteria.setName(discountCriteriaRaw.getName());
        discountCriteria.setDescription(discountCriteriaRaw.getDescription());
        discountCriteria.setCreator(user);
        discountCriteria.setDateCreated(discountCriteriaRaw.getDateCreated().toString());
        if(discountCriteriaRaw.getChangedBy()!=null) {
            discountCriteria.setChangedBy(this.getCustomUserById(discountCriteriaRaw.getChangedBy()));
            discountCriteria.setDateChanged(discountCriteriaRaw.getDateChanged().toString());
        }
        discountCriteria.setRetired(discountCriteriaRaw.getRetired());
        discountCriteria.setUuid(discountCriteriaRaw.getUuid());

        return discountCriteria;
    }


    //...................Getting discount from raw...........................//
    private Discount getDiscountFromDiscountRaw(DiscountRaw discountRaw)
    {
        Discount discount = new Discount();
        User initiator = this.getCustomUserById(discountRaw.getInitiatedBy());
        User approvedBy = this.getCustomUserById(discountRaw.getApprovedBy());
        SaleQuoteLine saleQuoteLine = getSaleQuoteLineFromSaleQuoteLineRaw(billingDAO.fetchSaleQuoteLineById(discountRaw.getSaleQuoteLine()));
        DiscountCriteria discountCriteria = getDiscountCriteriaFromDiscountCriteriaRaw(billingDAO.fetchDiscountCriteriaById(discountRaw.getDiscountCriteria()));

        discount.setDiscountId(discountRaw.getDiscountId());
        discount.setSaleQuoteLine(saleQuoteLine);
        discount.setOriginalQuotedAmount(discountRaw.getOriginalQuotedAmount());
        discount.setProposedDiscountAmount(discountRaw.getProposedDiscountAmount());
        discount.setApprovedDiscountAmount(discount.getApprovedDiscountAmount());
        discount.setDiscountCriteria(discountCriteria);
        discount.setInitiatedBy(initiator);
        discount.setApproved(discountRaw.getApproved());
        discount.setApprovedBy(approvedBy);
        discount.setDateApproved(discountRaw.getDateApproved());
        discount.setUuid(discountRaw.getUuid());

        return discount;
    }

    //...................Getting sale order by quote from raw...........................//
    private OrderByQuote getOrderByQuoteFromOrderByQuoteRaw(OrderByQuoteRaw orderByQuoteRaw, boolean includeLinesList)
    {
        SaleQuote saleQuote = getSaleQuoteFromSaleQuoteRaw(billingDAO.fetchSaleQuoteById(orderByQuoteRaw.getSaleQuote()));
        User creator = getCustomUserById(orderByQuoteRaw.getCreator());
        OrderByQuote orderByQuote = new OrderByQuote();

        orderByQuote.setOrderByQuoteId(orderByQuoteRaw.getSoqNo());
        orderByQuote.setDatedSaleId(orderByQuoteRaw.getDatedSaleId());
        orderByQuote.setSaleQuote(saleQuote);
        orderByQuote.setPaymentMethod(orderByQuoteRaw.getPaymentMethod());
        orderByQuote.setPayableAmount(orderByQuoteRaw.getPayableAmount());
        orderByQuote.setPaidAmount(orderByQuoteRaw.getPaidAmount());
        orderByQuote.setDebtAmount(orderByQuoteRaw.getDebtAmount());
        orderByQuote.setInstallmentFrequency(orderByQuoteRaw.getInstallmentFrequency());
        orderByQuote.setCreator(creator);
        orderByQuote.setDateCreated(orderByQuoteRaw.getDateCreated());
        orderByQuote.setUuid(orderByQuoteRaw.getUuid());

        if(includeLinesList)
        {
            //Logic here!
            orderByQuote.setOrderByQuoteLines(null);
        }
        return orderByQuote;
    }

    //...................Getting sale order by quote from raw...........................//
    private OrderByQuoteLine getOrderByQuoteLineFromOrderByQuoteLineRaw(OrderByQuoteLineRaw orderByQuoteLineRaw)
    {
        SaleQuoteLine saleQuoteLine = getSaleQuoteLineFromSaleQuoteLineRaw(billingDAO.fetchSaleQuoteLineById(orderByQuoteLineRaw.getQuoteLine()));
        OrderByQuote orderByQuote = getOrderByQuoteFromOrderByQuoteRaw(billingDAO.fetchSaleOrderById(orderByQuoteLineRaw.getSaleOrderQuote()),false);
        OrderByQuoteLine orderByQuoteLine = new OrderByQuoteLine();

        orderByQuoteLine.setOrderByQuoteLineId(orderByQuoteLineRaw.getSoqlNo());
        orderByQuoteLine.setOrderByQuote(orderByQuote);
        orderByQuoteLine.setSaleQuoteLine(saleQuoteLine);
        orderByQuoteLine.setPaidAmount(orderByQuoteLineRaw.getPaidAmount());
        orderByQuoteLine.setDebtAmount(orderByQuoteLineRaw.getDebtAmount());
        orderByQuoteLine.setDateCreated(orderByQuoteLineRaw.getDateCreated());
        orderByQuoteLine.setUuid(orderByQuoteLineRaw.getUuid());

        return orderByQuoteLine;
    }


    //...................Getting sale order by quote from raw...........................//
    private OrderPayInstallment getOrderPayInstallmentFromOrderPayInstallmentRaw(OrderPayInstallmentRaw orderPayInstallmentRaw)
    {
        OrderByQuoteLine orderByQuoteLine = getOrderByQuoteLineFromOrderByQuoteLineRaw(billingDAO.fetchSaleOrderLineBySOQLNo(orderPayInstallmentRaw.getOrderByQuoteLine()));
        OrderPayInstallment orderPayInstallment = new OrderPayInstallment();
        orderPayInstallment.setEntryNo(orderPayInstallmentRaw.getEntryNo());
        orderPayInstallment.setInstallmentNo(orderPayInstallmentRaw.getInstallmentNo());
        orderPayInstallment.setOrderByQuoteLine(orderByQuoteLine);
        orderPayInstallment.setReceiptNo(orderPayInstallmentRaw.getReceiptNo());
        orderPayInstallment.setReceivedBy(getCustomUserById(orderPayInstallmentRaw.getReceivedBy()));
        orderPayInstallment.setDateCreated(orderPayInstallmentRaw.getDateCreated());
        orderPayInstallment.setUuid(orderPayInstallmentRaw.getUuid());
        return orderPayInstallment;
    }

/**
  Items' price templates, down/up loading  & related processes
  @Author: Eric Mwailunga
  October,2019
*/

    private boolean checkConceptExistence(String conceptUuid)
    {
        ConceptService conceptService = Context.getConceptService();
        Concept concept = conceptService.getConceptByUuid(conceptUuid);
        return concept != null;
    }

    private List<org.openmrs.module.billingservices.model.saleable.Item> getItemsForServiceType(String serviceType)
    {
        List<org.openmrs.module.billingservices.model.saleable.Item> items = new ArrayList<>();
        PharmacyService pharmacyService = Context.getService(PharmacyService.class);
        ConceptService conceptService=Context.getConceptService();

        switch (serviceType)
        {
            //Kumbuka kuchambua panel items
            case "Laboratory":
                List<Concept> labDepartments = conceptService.getConceptByUuid("81e79a45-3f10-11e4-adec-0800271c1b75").getSetMembers();
                List<Concept> testsInDepartments = new ArrayList<>();
                for(Concept labDeptConcept : labDepartments)
                {
                    List<Concept> deptMemberTests = labDeptConcept.getSetMembers();
                    if(deptMemberTests.size()>0)
                    {
                        testsInDepartments.addAll(deptMemberTests.stream().filter(concept -> !concept.getRetired()).collect(Collectors.toList()));
                    }
                }
                for(Concept test : testsInDepartments)
                {
                    org.openmrs.module.billingservices.model.saleable.Item item = new org.openmrs.module.billingservices.model.saleable.Item();
                    String itemName=test.getShortNameInLocale(Locale.ENGLISH).getName();
                    if(itemName==null) {
                        itemName=test.getFullySpecifiedName(Locale.ENGLISH).getName();
                    }
                    item.setItemId(test.getConceptId());
                    item.setName(itemName);
                    item.setUuid(test.getUuid());
                    items.add(item);
                }

                /*
                List<Concept> labTestConcepts = conceptService.getConceptsByClass(conceptService.getConceptClassByName("Lab Test"));
                List<Concept> labPanelConcepts = conceptService.getConceptsByClass(conceptService.getConceptClassByName("LabSet"));
                */
                break;
            case "Radiology":

                break;
            case "Procedure":

                /*
                Concept concept= conceptService.getConcept(itemId);

                String itemName=concept.getShortNameInLocale(Locale.ENGLISH).getName();
                if(itemName==null) {
                    itemName=concept.getFullySpecifiedName(Locale.ENGLISH).getName();
                }
                item.setName(itemName);
                item.setUuid(concept.getUuid());
                */
                break;

            case "Pharmacy":
                /*
                Item pharmacyItem = pharmacyService.getItemById(itemId);
                item.setName(pharmacyItem.getItemName());
                item.setUuid(pharmacyItem.getItemUuid());
                */
                break;

            case "Registration Fees":
                //
                break;
            case "Admission Fees":
                //
                break;
        }

        return items;
    }

    private Map<Integer, String> getSchemesForPaymentType(String paymentMethodUuid, String prefix)
    {
        Map<Integer, String> schemesMap = new LinkedHashMap<>();
        ConceptService conceptService = Context.getConceptService();
        Concept concept = conceptService.getConceptByUuid(paymentMethodUuid);
        String conceptType = concept.getDatatype().getName();
        if(conceptType.equalsIgnoreCase("coded") && concept.getAnswers(false).size()>0)
        {
            for(Object object : concept.getAnswers(false).toArray())
            {
                ConceptAnswer conceptAnswer = (ConceptAnswer)object;
                Concept answerConcept = conceptAnswer.getAnswerConcept();
                schemesMap.put(answerConcept.getConceptId(), prefix+"."+answerConcept.getName().getName().toLowerCase());
            }
        }
        return schemesMap;
    }

    private Map<Integer, String> getSchemesMap()
    {
        ConceptService conceptService = Context.getConceptService();
        Map<Integer, String> schemesMap = new LinkedHashMap<>();
        String cashUuid = Context.getAdministrationService().getGlobalProperty("billing.cash.uuid");
        String insuranceUuid = Context.getAdministrationService().getGlobalProperty("billing.insurance.uuid");

        String cashSchemesUuid = Context.getAdministrationService().getGlobalProperty("billing.cash_schemes.uuid");
        String insuranceSchemeUuid = Context.getAdministrationService().getGlobalProperty("billing.insurance_types.uuid");

        if(cashSchemesUuid!=null && checkConceptExistence(cashSchemesUuid))
        {
            schemesMap = getSchemesForPaymentType(cashSchemesUuid, "cash");
        }else if(cashUuid!=null && checkConceptExistence(cashUuid)){
            schemesMap.put(conceptService.getConceptByUuid(cashUuid).getConceptId(),"cash");
        }

        if(insuranceSchemeUuid!=null && checkConceptExistence(insuranceSchemeUuid))
        {
            Map<Integer, String> insuranceTypesMap = getSchemesForPaymentType(insuranceSchemeUuid, "insurance");
            if(insuranceTypesMap.size()>0)
                schemesMap.putAll(insuranceTypesMap);
        }else if(insuranceUuid!=null && checkConceptExistence(insuranceUuid)){
            schemesMap.put(conceptService.getConceptByUuid(insuranceUuid).getConceptId(),"insurance.nhif");
        }

        return schemesMap;
    }
    public void generateServicesTemplate(String typesJsonString, HttpServletResponse response)
    {

        Map<Integer , String> schemesMap = this.getSchemesMap();
        XSSFWorkbook workbook = new XSSFWorkbook();
        JSONObject typesJsonObject = new JSONObject(typesJsonString);
        JSONArray typesJsonArray = typesJsonObject.getJSONArray("serviceTypes");

        XSSFColor color = new XSSFColor(new java.awt.Color(226,239,218));
        XSSFCellStyle topRowStyle = workbook.createCellStyle();
        topRowStyle.setBorderBottom(CellStyle.BORDER_THIN);
        topRowStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        topRowStyle.setFillForegroundColor(color);
        topRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        for(Object object : typesJsonArray)
        {
            String serviceName = (String) object;
            ServiceTypeRaw serviceTypeRaw = billingDAO.fetchServiceTypeByName(serviceName);
            if(serviceTypeRaw!=null) {
               Sheet worksheet = workbook.createSheet(serviceTypeRaw.getName());
                Row row = worksheet.createRow(0);
                Cell cell1 = row.createCell(0, Cell.CELL_TYPE_STRING);
                cell1.setCellStyle(topRowStyle);
                cell1.setCellValue("item.id");

                Cell cell2 =row.createCell(1, Cell.CELL_TYPE_STRING);
                cell2.setCellValue("service.type");
                cell2.setCellStyle(topRowStyle);

                Cell cell3 =row.createCell(2, Cell.CELL_TYPE_STRING);
                cell3.setCellValue("item.name");
                cell3.setCellStyle(topRowStyle);

                int counter = 2;
                for(String scheme : getSchemesMap().values())
                {
                    counter++;
                    Cell cellX = row.createCell(counter, Cell.CELL_TYPE_STRING);
                    cellX.setCellValue(scheme);
                    cellX.setCellStyle(topRowStyle);
                }

                counter=1;
                List<org.openmrs.module.billingservices.model.saleable.Item> items = getItemsForServiceType(serviceTypeRaw.getName());
                for(org.openmrs.module.billingservices.model.saleable.Item item : items)
                {
                    row = worksheet.createRow(counter);
                    row.createCell(0, Cell.CELL_TYPE_NUMERIC).setCellValue(item.getItemId());
                    row.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(serviceTypeRaw.getName());
                    row.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(item.getName());
                    counter++;
                }
            }
        }

        if(workbook.getNumberOfSheets()>0)
        {
            for(int index = 0; index < workbook.getNumberOfSheets(); index++)
            {
                Sheet sheet = workbook.getSheetAt(index);
                sheet.setColumnHidden(0,true);
                sheet.setColumnHidden(1,true);
                sheet.createFreezePane(0,1,0,1);
                for(int columnNo = 2; columnNo <= schemesMap.size()+2; columnNo++)
                {
                    sheet.autoSizeColumn(columnNo);
                }
            }

            ServletOutputStream servletOutputStream;
            try {
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-Disposition", "attachment;filename=Template-Price-Upload.xlsx");
                servletOutputStream = response.getOutputStream();
                workbook.write(servletOutputStream);
                workbook.close();
                servletOutputStream.flush();
                servletOutputStream.close();
            } catch (IOException e) {
                log.error(e.getMessage().toString());
            }
        }
    }


    public String uploadServicesTemplate(String listVersionUuid, MultipartFile templateFile, HttpServletResponse response)
    {
        InputStream inputStream = null;
        try {
            inputStream = templateFile.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(OPCPackage.open(inputStream));
            int sheetsCount = workbook.getNumberOfSheets();
            int listVersionId = billingDAO.fetchPriceListVersionByUuid(listVersionUuid).getListId();
            if(sheetsCount>0)
            {
                ConceptService conceptService = Context.getConceptService();

                Map<Integer, String> paySchemesMap = this.getSchemesMap();
                Integer[] categoryIds = new Integer[paySchemesMap.size()];
                Integer [] subCategoryIds = new Integer[paySchemesMap.size()];

                String cashUuid = Context.getAdministrationService().getGlobalProperty("billing.cash.uuid");
                String insuranceUuid = Context.getAdministrationService().getGlobalProperty("billing.insurance.uuid");

                Map<Integer, String> paymentCategoriesMap = new LinkedHashMap<>();
                Concept cashConcept = conceptService.getConceptByUuid(cashUuid);
                Concept insuranceConcept = conceptService.getConceptByUuid(insuranceUuid);

                paymentCategoriesMap.put(cashConcept.getConceptId(), cashConcept.getFullySpecifiedName(Locale.ENGLISH).getName());
                paymentCategoriesMap.put(insuranceConcept.getConceptId(), insuranceConcept.getFullySpecifiedName(Locale.ENGLISH).getName());

                XSSFSheet sheetX = workbook.getSheetAt(0);
                sheetX.setColumnHidden(0, false);
                sheetX.setColumnHidden(1,false);
                sheetX.createFreezePane(0,0,0,0);
                XSSFRow row = sheetX.getRow(0);
                int paycatIdIndex = 0;

                for(int columnIndex=3 ; columnIndex < row.getPhysicalNumberOfCells(); columnIndex ++)
                {
                    for(Map.Entry<Integer, String> mapScheme : paySchemesMap.entrySet())
                    {
                        if(row.getCell(columnIndex).getStringCellValue().equalsIgnoreCase(mapScheme.getValue()))
                        {
                            subCategoryIds[paycatIdIndex] = mapScheme.getKey();
                            break;
                        }
                        for(Map.Entry<Integer, String> mapPayCat : paymentCategoriesMap.entrySet())
                        {
                            if(row.getCell(columnIndex).getStringCellValue().contains(mapPayCat.getValue()))
                            {
                                categoryIds[paycatIdIndex] = mapPayCat.getKey();
                            }
                        }
                    }
                    paycatIdIndex++;
                }

                for(int sheetIndex = 0; sheetIndex<sheetsCount; sheetIndex++)
                {
                    XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
                    sheet.setColumnHidden(0, false);
                    sheet.setColumnHidden(1,false);
                    sheet.createFreezePane(0,0,0,0);
                    for(int rowIndex = 1; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex++)
                    {
                        XSSFRow rowCurrent = sheet.getRow(rowIndex);
                        int itemId = (int) rowCurrent.getCell(0).getNumericCellValue();
                        String serviceName = rowCurrent.getCell(1).getStringCellValue();
                        int serviceTypeId = billingDAO.fetchServiceTypeByName(serviceName).getServiceTypeId();
                        for(int payId = 0; payId < categoryIds.length; payId++)
                        {
                            if(rowCurrent.getCell(payId + 3)!=null && rowCurrent.getCell(payId + 3).getCellType()!=Cell.CELL_TYPE_BLANK)
                            {
                                double price = rowCurrent.getCell(payId+3).getNumericCellValue();
                                billingDAO.createItemPrice(itemId,serviceTypeId,listVersionId,categoryIds[payId],subCategoryIds[payId],price,getCurrentUserFromContext().getUserId());
                            }
                        }
                    }
                }
                return "success";
            }
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }
        return "failed";
    }

    /**
  Service types & related data
  @Author: Eric Mwailunga
  October,2019
*/

    public String createServiceType(String name)
    {
        String response;
        response = billingDAO.checkServiceTypeExistence(name);
        if(response.equals("exists")) {
            return "exists";
        }
        response = billingDAO.createServiceType(name,getCurrentUserFromContext().getUserId());
        return response;
    }

    public ServiceType fetchServiceTypeByName(String name)
    {
        ServiceTypeRaw serviceTypeRaw=billingDAO.fetchServiceTypeByName(name);
        return getServiceTypeFromServiceTypeRaw(serviceTypeRaw);
    }

    public List<ServiceType> fetchServiceTypeBySubName(String subName, boolean includeRetired)
    {
        List<ServiceType> serviceTypes=new ArrayList<>();
        List<ServiceTypeRaw> serviceTypeRaws = billingDAO.fetchServiceTypeBySubName(subName,includeRetired);
        for(ServiceTypeRaw serviceTypeRaw : serviceTypeRaws)
        {
            serviceTypes.add(getServiceTypeFromServiceTypeRaw(serviceTypeRaw));
        }
        return serviceTypes;
    }

    public ServiceType fetchServiceTypeByUuid(String serviceTypeUuid)
    {
        ServiceTypeRaw serviceTypeRaw=billingDAO.fetchServiceTypeByUuid(serviceTypeUuid);
        return getServiceTypeFromServiceTypeRaw(serviceTypeRaw);
    }

    public ServiceType fetchServiceTypeById(int serviceTypeId)
    {
        ServiceTypeRaw serviceTypeRaw=billingDAO.fetchServiceTypeById(serviceTypeId);
        return getServiceTypeFromServiceTypeRaw(serviceTypeRaw);
    }

    public List<ServiceType> fetchAllServiceTypes(boolean includeRetired)
    {
        List<ServiceType> serviceTypes=new ArrayList<>();
        List<ServiceTypeRaw> serviceTypeRaws = billingDAO.fetchAllServiceTypes(includeRetired);
        if(serviceTypeRaws.size()>0)
        {
            for(ServiceTypeRaw serviceTypeRaw : serviceTypeRaws)
            {
                serviceTypes.add(getServiceTypeFromServiceTypeRaw(serviceTypeRaw));
            }
        }
        return serviceTypes;
    }


    public String updateServiceTypeByUuid(String serviceTypeUuid, String name)
    {
        return billingDAO.updateServiceTypeByUuid(serviceTypeUuid,name,getCurrentUserFromContext().getUserId());
    }

    public String retireServiceTypeByUuid(String serviceTypeUuid)
    {
        return billingDAO.retireServiceTypeByUuid(serviceTypeUuid,getCurrentUserFromContext().getUserId());
    }

/**
  Financial period & related data
  @Author: Eric Mwailunga
  October,2019
*/

    public String createFinancialPeriod(String name, String startDate, String endDate)
    {
        String response;
        response = billingDAO.checkFinancialPeriodExistence(name);
        if(response.equals("exists")) {
            return "exists";
        }
        response = billingDAO.createFinancialPeriod(name,startDate,endDate,getCurrentUserFromContext().getUserId());
        return response;
    }

    public FinancialPeriod fetchFinancialPeriodByName(String name)
    {
        FinancialPeriodRaw financialPeriodRaw = billingDAO.fetchFinancialPeriodByName(name);

        return getFinancialPeriodFromFinancialPeriodRaw(financialPeriodRaw);
    }

    public List<FinancialPeriod> fetchFinancialPeriodBySubName(String subName)
    {
        List<FinancialPeriod> financialPeriods=new ArrayList<>();
        List<FinancialPeriodRaw> financialPeriodRaws = billingDAO.fetchFinancialPeriodBySubName(subName);
        for(FinancialPeriodRaw financialPeriodRaw : financialPeriodRaws)
        {
            financialPeriods.add(getFinancialPeriodFromFinancialPeriodRaw(financialPeriodRaw));
        }
        return financialPeriods;
    }

    public FinancialPeriod fetchFinancialPeriodByUuid(String uuid)
    {
        FinancialPeriodRaw financialPeriodRaw = billingDAO.fetchFinancialPeriodByUuid(uuid);
        return getFinancialPeriodFromFinancialPeriodRaw(financialPeriodRaw);
    }

    public FinancialPeriod fetchFinancialPeriodById(int id)
    {
        FinancialPeriodRaw financialPeriodRaw = billingDAO.fetchFinancialPeriodById(id);
        return getFinancialPeriodFromFinancialPeriodRaw(financialPeriodRaw);
    }

    public List<FinancialPeriod> fetchAllFinancialPeriods(boolean includeRetired)
    {
        int include=0;
        if(includeRetired) {
            include=1;
        }
        List<FinancialPeriod> financialPeriods=new ArrayList<>();
        List<FinancialPeriodRaw> financialPeriodRaws=billingDAO.fetchAllFinancialPeriods(include);
        for(FinancialPeriodRaw financialPeriodRaw : financialPeriodRaws)
        {
            financialPeriods.add(getFinancialPeriodFromFinancialPeriodRaw(financialPeriodRaw));
        }
        return setPriceListsForFinancialPeriods(financialPeriods);
    }

    public String updateFinancialPeriodByUuid(String financialPeriodUuid,String name,String startDate,String endDate)
    {
        return  billingDAO.updateFinancialPeriodByUuid(financialPeriodUuid,name,startDate,endDate,getCurrentUserFromContext().getUserId());
    }

    public String retireFinancialPeriodByUuid(String financialPeriodUuid)
    {
        return billingDAO.retireFinancialPeriodByUuid(financialPeriodUuid,getCurrentUserFromContext().getUserId());
    }


/**
  Price list, versions & related data
  @Author: Eric Mwailunga
  October,2019
*/

    public String createPriceListVersion(String financialPeriodUuid, String versionName, String dateApproved)
    {
        String response=null;
        FinancialPeriod financialPeriod = fetchFinancialPeriodByUuid(financialPeriodUuid);
        if(financialPeriod!=null)
        {
            response = billingDAO.checkPriceListVersionExistence(financialPeriod.getPeriodId(),versionName);
            if(response.equals("exists")) {
                return "exists";
            }
            response = billingDAO.createPriceListVersion(financialPeriod.getPeriodId(),versionName,dateApproved,getCurrentUserFromContext().getUserId());
        }
        return response;
    }

    public ListVersion fetchPriceListVersionByName(String financialPeriodUuid, String versionName)
    {
        ListVersion listVersion=null;
        FinancialPeriod financialPeriod = fetchFinancialPeriodByUuid(financialPeriodUuid);
        if(financialPeriod!=null)
        {
            ListVersionRaw priceListRaw=billingDAO.fetchPriceListVersionByName(financialPeriod.getPeriodId(),versionName);
            listVersion=getPriceListFromPriceListRaw(priceListRaw);
        }
        return listVersion;
    }

    public List<ListVersion> fetchPriceListVersionBySubName(String financialPeriodUuid, String versionSubName)
    {
        List<ListVersion> priceLists=new ArrayList<>();
        FinancialPeriod financialPeriod = fetchFinancialPeriodByUuid(financialPeriodUuid);
        if(financialPeriod!=null)
        {
            List<ListVersionRaw> priceListRaws=billingDAO.fetchPriceListVersionBySubName(financialPeriod.getPeriodId(),versionSubName);
            for(ListVersionRaw priceListRaw: priceListRaws)
            {
                ListVersion priceList=getPriceListFromPriceListRaw(priceListRaw);
                priceLists.add(priceList);
            }
        }
        return priceLists;
    }

    public ListVersion fetchPriceListVersionById(int priceListId)
    {
        ListVersionRaw priceListRaw=billingDAO.fetchPriceListVersionById(priceListId);
        return getPriceListFromPriceListRaw(priceListRaw);
    }

    public ListVersion fetchPriceListVersionByUuid(String priceListUuid)
    {
        ListVersionRaw priceListRaw=billingDAO.fetchPriceListVersionByUuid(priceListUuid);
        return getPriceListFromPriceListRaw(priceListRaw);
    }

    public List<ListVersion> fetchAllPriceLists(boolean includeRetired)
    {
        int include=0;
        if(includeRetired) {
            include=1;
        }
        List<ListVersion> priceLists=new ArrayList<>();
        List<ListVersionRaw> priceListRaws=billingDAO.fetchAllPriceLists(include);
        if(priceListRaws.size()>0)
        {
            for(ListVersionRaw priceListRaw : priceListRaws)
            {
                ListVersion priceList=getPriceListFromPriceListRaw(priceListRaw);
                priceLists.add(priceList);
            }
        }
        return priceLists;
    }


    public String retirePriceListVersionByUuid(String priceListUuid)
    {
        return billingDAO.retirePriceListVersionByUuid(priceListUuid,getCurrentUserFromContext().getUserId());
    }


/**
  Items' price & related data
  @Author: Eric Mwailunga
  October,2019
*/

    public String createItemPrice(int itemId, int serviceTypeId, int priceListVersion, int paymentCategory, int paymentSubCategory, double sellingPrice)
    {
        return billingDAO.createItemPrice(itemId,serviceTypeId,priceListVersion,paymentCategory,paymentSubCategory,sellingPrice,getCurrentUserFromContext().getUserId());
    }

    public ItemPrice fetchItemPriceByItemPriceId(int itemPriceId)
    {
        ItemPriceRaw itemPriceRaw = billingDAO.fetchItemPriceByItemPriceId(itemPriceId);
        return getItemPriceFromItemPriceRaw(itemPriceRaw);
    }

    public ItemPrice fetchItemPriceByItemAndPaymentCategory(String jsonDataObject)
    {
        ConceptService conceptService=Context.getConceptService();
        JSONObject jsonObject=new JSONObject(jsonDataObject);
        log.info("Received json data object: "+jsonDataObject);

        String itemUuid=jsonObject.getString("itemUuid");
        String serviceTypeUuid=jsonObject.getString("serviceTypeUuid");
        String paymentCategoryUuid=jsonObject.getString("paymentCategoryUuid");

        ServiceType serviceType=fetchServiceTypeByUuid(serviceTypeUuid);
        org.openmrs.module.billingservices.model.saleable.Item item = getItemFromItemUuidAndServiceType(itemUuid,serviceType);

        ListVersion priceListToUse= new ListVersion();
        List<ListVersionRaw> listVersionRaws=billingDAO.fetchActivePriceLists();
        if(listVersionRaws!=null)
        {
            //List<PriceList> priceLists=new ArrayList<>();

            if(listVersionRaws.size()==1)
            {
                priceListToUse=getPriceListFromPriceListRaw(listVersionRaws.get(0));
                //priceLists.add(priceListToUse);
            }
            if(listVersionRaws.size()>1)
            {
                //A certain logic has to be implemented here!
            }
        }

        int itemId=item.getItemId();
        int serviceTypeId=serviceType.getServiceTypeId();
        int paymentCategoryId=conceptService.getConceptByUuid(paymentCategoryUuid).getId();
        int priceListVersionId=priceListToUse.getListId();

        ItemPriceRaw itemPriceRaw=billingDAO.fetchItemPriceByItemAndPaymentCategory(itemId,serviceTypeId,paymentCategoryId,priceListVersionId);
        return getItemPriceFromItemPriceRaw(itemPriceRaw);
    }

    public List<ItemPrice> fetchItemsPricesByFinancialYear(String financialYearUuid)
    {

        return null;
    }

    public List<ItemPrice> fetchItemsPricesByPriceList(String priceListUuid, int includeRetired)
    {

        return null;
    }

    public String retireItemPriceByPriceListVersion(String priceListUuid, String itemPriceUuid)
    {
        
        return null;
    }


/**
 Quotation status codes & related data
 @Author: Eric Mwailunga
 October,2019
*/
    public String createQuoteStatusCode(String name, String quoteType)
    {
        return billingDAO.createQuoteStatusCode(name,quoteType,getCurrentUserFromContext().getUserId());
    }

    public QuoteStatus fetchQuoteStatusCodeById(int statusCodeId)
    {
        QuoteStatusRaw quoteStatusRaw = billingDAO.fetchQuoteStatusCodeById(statusCodeId);
        return this.getQuoteStatusFromQuoteStatusRaw(quoteStatusRaw);
    }

    public QuoteStatus fetchQuoteStatusCodeByUuid(String statusCodeUuid)
    {
        QuoteStatusRaw quoteStatusRaw = billingDAO.fetchQuoteStatusCodeByUuid(statusCodeUuid);
        return this.getQuoteStatusFromQuoteStatusRaw(quoteStatusRaw);
    }

    public List<QuoteStatus> fetchAllQuoteStatusCodes()
    {
        List<QuoteStatus> quoteStatuses = new ArrayList<>();
        List<QuoteStatusRaw> quoteStatusRaws = billingDAO.fetchAllQuoteStatusCodes();
        for(QuoteStatusRaw quoteStatusRaw : quoteStatusRaws)
        {
            quoteStatuses.add(getQuoteStatusFromQuoteStatusRaw(quoteStatusRaw));
        }
        return quoteStatuses;
    }


/**
 Quotation & related data
 @Author: Eric Mwailunga
 October,2019
*/

    public String createSaleQuote(String quotationJson)
    {
        ConceptService conceptService=Context.getConceptService();
        PatientService patientService = Context.getPatientService();
        VisitService visitService = Context.getVisitService();

        JSONObject quotationJsonObject = new JSONObject(quotationJson);
        String patientUuid = quotationJsonObject.getString("patientUuid");
        String visitUuid = quotationJsonObject.getString("visitUuid");

        JSONObject paymentJsonObject = quotationJsonObject.getJSONObject("payment");

        String paymentCategoryUuid = paymentJsonObject.getString("categoryUuid");
        String paymentSubCategoryUuid = paymentJsonObject.getString("subCategoryUuid");

        int patientId = patientService.getPatientByUuid(patientUuid).getPatientId();
        int visitId = visitService.getVisitByUuid(visitUuid).getVisitId();
        int paymentCategoryId = conceptService.getConceptByUuid(paymentCategoryUuid).getConceptId();
        int paymentSubCategoryId = conceptService.getConceptByUuid(paymentSubCategoryUuid).getConceptId();
        int authenticatedUserId = Context.getAuthenticatedUser().getUserId();

        SaleQuote saleQuote = getSaleQuoteFromSaleQuoteRaw(billingDAO.createSaleQuote(patientId, visitId, paymentCategoryId, paymentSubCategoryId, authenticatedUserId));
        //if(saleQuote!=null)
        //{
            JSONArray jsonItemsArray = quotationJsonObject.getJSONArray("items");
            return createSaleQuoteLine(saleQuote, paymentCategoryUuid, jsonItemsArray);
        //}
        //return null;
    }

    public SaleQuote fetchSaleQuoteById(int quoteId)
    {
        SaleQuoteRaw saleQuoteRaw = billingDAO.fetchSaleQuoteById(quoteId);
        if(saleQuoteRaw!=null)
            return getSaleQuoteFromSaleQuoteRaw(saleQuoteRaw);
        return new SaleQuote();
    }

    public SaleQuote fetchSaleQuoteByUuid(String quoteUuid)
    {
        SaleQuoteRaw saleQuoteRaw = billingDAO.fetchSaleQuoteByUuid(quoteUuid);
        if(saleQuoteRaw!=null)
            return getSaleQuoteFromSaleQuoteRaw(saleQuoteRaw);
        return new SaleQuote();
    }

    public List<SaleQuote> fetchAllSaleQuotesByPatient(String patientUuid, boolean isAscending)
    {
        PatientService patientService = Context.getPatientService();

        List<SaleQuote> saleQuotes = new ArrayList<>();
        org.openmrs.Patient openmrsPatient = patientService.getPatientByUuid(patientUuid);
        Patient patient = getCustomPatient(openmrsPatient);
        List<SaleQuoteRaw> saleQuoteRaws = billingDAO.fetchAllSaleQuotesByPatient(patient.getPatientId(), isAscending);
        for(SaleQuoteRaw saleQuoteRaw : saleQuoteRaws)
        {
            saleQuotes.add(getSaleQuoteFromSaleQuoteRaw(saleQuoteRaw));
        }
        return saleQuotes;
    }

    public List<SaleQuote> fetchAllSaleQuotesByDates(String startDate, String endDate, boolean isAscending)
    {
        List<SaleQuote> saleQuotes = new ArrayList<>();
        List<SaleQuoteRaw> saleQuoteRaws = billingDAO.fetchAllSaleQuotesByDates(startDate, endDate, isAscending);
        for(SaleQuoteRaw saleQuoteRaw : saleQuoteRaws)
        {
            saleQuotes.add(getSaleQuoteFromSaleQuoteRaw(saleQuoteRaw));
        }
        return saleQuotes;
    }

    public List<SaleQuote> fetchAllSaleQuotes(int numberOfRecords, boolean isAscending)
    {
        List<SaleQuote> saleQuotes = new ArrayList<>();
        List<SaleQuoteRaw> saleQuoteRaws = billingDAO.fetchAllSaleQuotes(numberOfRecords, isAscending);
        for(SaleQuoteRaw saleQuoteRaw : saleQuoteRaws)
        {
            saleQuotes.add(getSaleQuoteFromSaleQuoteRaw(saleQuoteRaw));
        }
        return saleQuotes;
    }


/**
 Sale Quotation lines & related data
 @Author: Eric Mwailunga
 October,2019
*/
    private String createSaleQuoteLine(SaleQuote saleQuote, String paymentCategoryUuid, JSONArray jsonItemsArray)
    {
        String valuesClause = "";
        int count = 0;
        for(Object  jsonItemObject : jsonItemsArray)
        {
            String itemUuid = ((JSONObject) jsonItemObject).getString("itemUuid");
            String serviceTypeUuid = ((JSONObject) jsonItemObject).getString("serviceTypeUuid");
            String units = ((JSONObject) jsonItemObject).getString("unit");
            int quantity = ((JSONObject) jsonItemObject).getInt("quantity");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("itemUuid",itemUuid);
            jsonObject.put("serviceTypeUuid",serviceTypeUuid);
            jsonObject.put("paymentCategoryUuid",paymentCategoryUuid);

            ItemPrice itemPrice = fetchItemPriceByItemAndPaymentCategory(jsonObject.toString());
            if(itemPrice!=null)
            {
                double quoteAmount = quantity * itemPrice.getSellingPrice();
                String quoteLineUuid = UUID.randomUUID().toString();
                valuesClause += "(" +
                        "'"+ saleQuote.getQuoteId() +"', " +
                        "'"+ itemPrice.getItem().getItemId() +"', " +
                        "'"+ itemPrice.getServiceType().getServiceTypeId() +"', " +
                        "'"+ itemPrice.getItemPriceId() +"', " +
                        "'"+ quantity +"', " +
                        "'"+ units +"', " +
                        "'"+ quoteAmount +"', " +
                        "'"+ quoteAmount +"', " +
                        "'5', " +
                        "now(), " +
                        "'"+ quoteLineUuid +"'" +
                        ")";
                if(count<jsonItemsArray.length()-1)
                {
                    valuesClause+=", ";
                }
            }
            count++;
        }
        if(!valuesClause.equalsIgnoreCase(""))
        {
            return billingDAO.createSaleQuoteLines(valuesClause);
        }
        return "failed";
    }


    public SaleQuoteLine fetchSaleQuoteLineById(int saleQuoteLineId)
    {
        SaleQuoteLineRaw saleQuoteLineRaw = billingDAO.fetchSaleQuoteLineById(saleQuoteLineId);
        return getSaleQuoteLineFromSaleQuoteLineRaw(saleQuoteLineRaw);
    }

    public SaleQuoteLine fetchSaleQuoteLineByUuid(String uuid)
    {
        SaleQuoteLineRaw saleQuoteLineRaw = billingDAO.fetchSaleQuoteLineByUuid(uuid);
        return getSaleQuoteLineFromSaleQuoteLineRaw(saleQuoteLineRaw);
    }

    public List<SaleQuoteLine> fetchSaleQuoteLinesByQuoteId(int saleQuoteId)
    {
        List<SaleQuoteLineRaw> saleQuoteLineRaws = billingDAO.fetchSaleQuoteLinesByQuoteId(saleQuoteId);
        List<SaleQuoteLine> saleQuoteLines = new ArrayList<>();
        for(SaleQuoteLineRaw saleQuoteLineRaw : saleQuoteLineRaws)
        {
            SaleQuoteLine saleQuoteLine = getSaleQuoteLineFromSaleQuoteLineRaw(saleQuoteLineRaw);
            if(saleQuoteLine!=null)
            {
                saleQuoteLines.add(saleQuoteLine);
            }
        }
        return saleQuoteLines;
    }

/**
  Discount criteria & related processes
  @Author: Eric Mwailunga
  October,2019
*/
    public String createDiscountCriteria(String name, String description)
    {
        String existence=billingDAO.checkDiscountCriteriaExistence(name);
        if(existence.equals("exists"))
            return "exists";
        else if (existence.equals("not-existing"))
            return billingDAO.createDiscountCriteria(name, description,getCurrentUserFromContext().getUserId());
        return null;
    }

    public DiscountCriteria fetchDiscountCriteriaById(int criteriaNo)
    {
        DiscountCriteriaRaw discountCriteriaRaw = billingDAO.fetchDiscountCriteriaById(criteriaNo);
        return getDiscountCriteriaFromDiscountCriteriaRaw(discountCriteriaRaw);
    }

    public DiscountCriteria fetchDiscountCriteriaByUuid(String uuid)
    {
        DiscountCriteriaRaw discountCriteriaRaw = billingDAO.fetchDiscountCriteriaByUuid(uuid);
        return getDiscountCriteriaFromDiscountCriteriaRaw(discountCriteriaRaw);
    }

    public List<DiscountCriteria> fetchAllDiscountCriteria(boolean includeRetired)
    {
        List<DiscountCriteriaRaw> discountCriteriaRaws = billingDAO.fetchAllDiscountCriteria(includeRetired);
        List<DiscountCriteria> discountCriteriaList = new ArrayList<>();
        if(discountCriteriaRaws.size()>0)
        {
            for (DiscountCriteriaRaw discountCriteriaRaw : discountCriteriaRaws) {
                discountCriteriaList.add(getDiscountCriteriaFromDiscountCriteriaRaw(discountCriteriaRaw));
            }
        }
        return discountCriteriaList;
    }

    public String updateDiscountCriteria(String uuid, String name, String description)
    {
        return billingDAO.updateDiscountCriteria(uuid,name,description,getCurrentUserFromContext().getUserId());
    }

    public String retireDiscountCriteria(String uuid)
    {
        return billingDAO.retireDiscountCriteria(uuid);
    }


/**
 Discount & related data processes
 @Author: Eric Mwailunga
 October,2019
*/
    public String createDiscount(int quoteLine, double originalQuotedAmount, double discountedAmount, int criteria)
    {
        String uuid = UUID.randomUUID().toString();
        return billingDAO.createDiscount(quoteLine,billingDAO.fetchSaleQuoteLineById(quoteLine).getQuotedAmount(),discountedAmount,criteria,getCurrentUserFromContext().getUserId(),uuid);
    }

    public String approveDiscountByUuid(String discountUuid, int criteria, double approvedAmount)
    {
        return billingDAO.approveDiscountByUuid(discountUuid,criteria,approvedAmount,getCurrentUserFromContext().getUserId());
    }

    public Discount fetchDiscountById(int discountId)
    {
        DiscountRaw discountRaw = billingDAO.fetchDiscountById(discountId);
        return getDiscountFromDiscountRaw(discountRaw);
    }

    public Discount fetchDiscountByUuid(String uuid)
    {
        DiscountRaw discountRaw = billingDAO.fetchDiscountByUuid(uuid);
        return getDiscountFromDiscountRaw(discountRaw);
    }

    public List<Discount> fetchDiscountsByInitiator(String startDate, String endDate, boolean isAscending)
    {
        List<DiscountRaw> discountRaws = billingDAO.fetchDiscountsByInitiator(getCurrentUserFromContext().getUserId(), startDate, endDate, isAscending);
        List<Discount> discounts = new ArrayList<>();
        for(DiscountRaw discountRaw : discountRaws)
        {
            discounts.add(getDiscountFromDiscountRaw(discountRaw));
        }
        return discounts;
    }

    public List<Discount> fetchDiscountsByApprovedBy(String startDate, String endDate, boolean isAscending)
    {
        List<DiscountRaw> discountRaws = billingDAO.fetchDiscountsByApprovedBy(getCurrentUserFromContext().getUserId(), startDate, endDate, isAscending);
        List<Discount> discounts = new ArrayList<>();
        for(DiscountRaw discountRaw : discountRaws)
        {
            discounts.add(getDiscountFromDiscountRaw(discountRaw));
        }
        return discounts;
    }

    public List<Discount> fetchAllDiscountsByDates(String startDate, String endDate, boolean isAscending)
    {
        List<DiscountRaw> discountRaws = billingDAO.fetchAllDiscountsByDates(startDate, endDate, isAscending);
        List<Discount> discounts = new ArrayList<>();
        for(DiscountRaw discountRaw : discountRaws)
        {
            discounts.add(getDiscountFromDiscountRaw(discountRaw));
        }
        return discounts;
    }

    public List<Discount> fetchAllDiscountsByStatus(String status, boolean isAscending)
    {

        List<DiscountRaw> discountRaws = billingDAO.fetchAllDiscountsByStatus(status, isAscending);
        List<Discount> discounts = new ArrayList<>();
        for(DiscountRaw discountRaw : discountRaws)
        {
            discounts.add(getDiscountFromDiscountRaw(discountRaw));
        }
        return discounts;
    }


    public String updateDiscountInitiation(String discountUuid, double discountedAmount, int criteria)
    {
        return billingDAO.updateDiscountInitiation(discountUuid,discountedAmount,criteria,getCurrentUserFromContext().getUserId());
    }


/**
 Sale Order & related processes
 @Author: Eric Mwailunga
 November,2019
*/

    public String createSaleOrder(String saleOrderJson)
    {
        JSONObject orderJsonObject = new JSONObject(saleOrderJson);
        String saleQuoteUuid = orderJsonObject.getString("saleQuoteUuid");
        JSONArray quoteLines = orderJsonObject.getJSONArray("quoteLines");

        String uuid = UUID.randomUUID().toString();
        SaleQuoteRaw saleQuoteRaw = billingDAO.fetchSaleQuoteByUuid(saleQuoteUuid);
        String message = billingDAO.createSaleOrder(saleQuoteRaw.getQuoteId(), billingDAO.getNewDatedSaleId(), getCurrentUserFromContext().getUserId(), uuid);
        OrderByQuoteRaw quoteRaw = billingDAO.fetchSaleOrderByUuid(saleQuoteUuid);
        if(message.equals("success"))
        {
            String valuesClause = "";
            int count = 0; int length = quoteLines.length();
            for( Object object : quoteLines)
            {
                JSONObject jsonQuoteLineObject = (JSONObject) object;
                String quoteLineUuid = jsonQuoteLineObject.getString("uuid");
                int saleQuoteLineId = billingDAO.fetchSaleQuoteLineByUuid(quoteLineUuid).getQuoteLineId();
                valuesClause+="('"+quoteRaw.getSoqNo()+"','"+saleQuoteLineId+"',now(),uuid())";
                if(count < (length - 1))
                {
                    valuesClause +=",";
                }
                count++;
            }
            if(!valuesClause.equals(""))
            {
                return billingDAO.createSaleOrderLines(valuesClause);
            }
            return "failed";
        }
        return null;
    }

    public OrderByQuote fetchSaleOrderById(int quoteId)
    {
        OrderByQuoteRaw orderByQuoteRaw = billingDAO.fetchSaleOrderById(quoteId);
        return getOrderByQuoteFromOrderByQuoteRaw(orderByQuoteRaw, false);
    }

    public OrderByQuote fetchSaleOrderByUuid(String uuid)
    {
        OrderByQuoteRaw orderByQuoteRaw = billingDAO.fetchSaleOrderByUuid(uuid);
        return getOrderByQuoteFromOrderByQuoteRaw(orderByQuoteRaw, false);
    }

    public List<OrderByQuote> fetchAllSaleOrderByPatient(String patientUuid, boolean isAscending)
    {
        PatientService patientService = Context.getPatientService();

        List<OrderByQuote> orderByQuotes = new ArrayList<>();
        int patientId = patientService.getPatientByUuid(patientUuid).getPatientId();
        List<OrderByQuoteRaw> orderByQuoteRaws = billingDAO.fetchAllSaleOrderByPatient(patientId, isAscending);
        for(OrderByQuoteRaw orderByQuoteRaw : orderByQuoteRaws)
        {
            OrderByQuote orderByQuote = getOrderByQuoteFromOrderByQuoteRaw(orderByQuoteRaw, false);
            orderByQuotes.add(orderByQuote);
        }
        return orderByQuotes;
    }

    public List<OrderByQuote> fetchAllSaleOrderByDates(String startDate, String endDate, boolean isAscending)
    {
        List<OrderByQuote> orderByQuotes = new ArrayList<>();
        List<OrderByQuoteRaw> orderByQuoteRaws = billingDAO.fetchAllSaleOrderByDates(startDate, endDate, isAscending);
        if(orderByQuoteRaws.size()>0) {
            for (OrderByQuoteRaw orderByQuoteRaw : orderByQuoteRaws) {
                OrderByQuote orderByQuote = getOrderByQuoteFromOrderByQuoteRaw(orderByQuoteRaw, false);
                orderByQuotes.add(orderByQuote);
            }
        }
        return orderByQuotes;
    }

    public List<OrderByQuote> fetchAllSaleOrders(int startAt, int offset, boolean isAscending)
    {
        
        return null;
    }


/**
 Order lines & related processes
 @Author: Eric Mwailunga
 November,2019
*/
    public OrderByQuoteLine fetchSaleOrderLineById(int soqlNo)
    {
        OrderByQuoteLineRaw saleOrderLineRaw = billingDAO.fetchSaleOrderLineBySOQLNo(soqlNo);
        return getOrderByQuoteLineFromOrderByQuoteLineRaw(saleOrderLineRaw);
    }

    public OrderByQuoteLine fetchSaleOrderLineByUuid(String uuid)
    {
        OrderByQuoteLineRaw saleOrderLineRaw = billingDAO.fetchSaleOrderLineByUuid(uuid);
        return getOrderByQuoteLineFromOrderByQuoteLineRaw(saleOrderLineRaw);
    }

    public OrderByQuoteLine fetchSaleOrderLineByQuoteLine(int quoteLine)
    {
        OrderByQuoteLineRaw saleOrderLineRaw = billingDAO.fetchSaleOrderLineByQuoteLine(quoteLine);
        return getOrderByQuoteLineFromOrderByQuoteLineRaw(saleOrderLineRaw);
    }

    public List<OrderByQuoteLine> fetchSaleOrderLineBySaleOrderNo(int saleOrderNo)
    {
        List<OrderByQuoteLine> saleOrderLines = new ArrayList<>();
        List<OrderByQuoteLineRaw> saleOrderLineRaws = billingDAO.fetchSaleOrderLineBySaleOrderNo(saleOrderNo);
        for(OrderByQuoteLineRaw saleOrderLineRaw : saleOrderLineRaws)
        {
            saleOrderLines.add(getOrderByQuoteLineFromOrderByQuoteLineRaw(saleOrderLineRaw));
        }
        return saleOrderLines;
    }


/**
 Order pay installment & related processes
 @Author: Eric Mwailunga
 November,2019
*/
    public String createPaymentInstallment(String saleOrderLineUuid, double paidAmount, String receiptNo)
    {
        OrderByQuoteLineRaw orderByQuoteLineRaw =  billingDAO.fetchSaleOrderLineByUuid(saleOrderLineUuid);
        return billingDAO.createPaymentInstallment(orderByQuoteLineRaw.getQuoteLine(),paidAmount,receiptNo,getCurrentUserFromContext().getUserId());
    }


    public OrderPayInstallment fetchInstallmentById(int entryNo)
    {
        OrderPayInstallmentRaw orderPayInstallmentRaw = billingDAO.fetchInstallmentById(entryNo);
        return getOrderPayInstallmentFromOrderPayInstallmentRaw(orderPayInstallmentRaw);
    }

    public OrderPayInstallment fetchInstallmentByUuid(String uuid)
    {
        OrderPayInstallmentRaw orderPayInstallmentRaw = billingDAO.fetchInstallmentByUuid(uuid);
        return getOrderPayInstallmentFromOrderPayInstallmentRaw(orderPayInstallmentRaw);
    }

    public List<OrderPayInstallment> fetchInstallmentByOrderLine(int orderLineNo)
    {
        List<OrderPayInstallment> orderPayInstallments = new ArrayList<>();
        List<OrderPayInstallmentRaw> orderPayInstallmentRaws = billingDAO.fetchInstallmentByOrderLine(orderLineNo);
        for(OrderPayInstallmentRaw orderPayInstallmentRaw : orderPayInstallmentRaws)
        {
            orderPayInstallments.add(getOrderPayInstallmentFromOrderPayInstallmentRaw(orderPayInstallmentRaw));
        }
        return orderPayInstallments;
    }

    public List<OrderPayInstallment> fetchInstallmentByDates(String startDate, String endDate, boolean isAscending)
    {
        List<OrderPayInstallment> orderPayInstallments = new ArrayList<>();
        List<OrderPayInstallmentRaw> orderPayInstallmentRaws = billingDAO.fetchInstallmentByDates(startDate,endDate,isAscending);
        for(OrderPayInstallmentRaw orderPayInstallmentRaw : orderPayInstallmentRaws)
        {
            orderPayInstallments.add(getOrderPayInstallmentFromOrderPayInstallmentRaw(orderPayInstallmentRaw));
        }
        return orderPayInstallments;
    }

    public List<OrderPayInstallment> fetchAllInstallments(int numberOfRecords, boolean isAscending)
    {
        List<OrderPayInstallment> orderPayInstallments = new ArrayList<>();
        List<OrderPayInstallmentRaw> orderPayInstallmentRaws = billingDAO.fetchAllInstallments(numberOfRecords,isAscending);
        for(OrderPayInstallmentRaw orderPayInstallmentRaw : orderPayInstallmentRaws)
        {
            orderPayInstallments.add(getOrderPayInstallmentFromOrderPayInstallmentRaw(orderPayInstallmentRaw));
        }
        return orderPayInstallments;
    }


}