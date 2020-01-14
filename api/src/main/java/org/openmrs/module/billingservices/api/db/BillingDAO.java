/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.billingservices.api.db;

import org.openmrs.module.billingservices.api.BillingService;
import org.openmrs.module.billingservices.model.sale.discount.raw.DiscountCriteriaRaw;
import org.openmrs.module.billingservices.model.sale.discount.raw.DiscountRaw;
import org.openmrs.module.billingservices.model.sale.order.raw.OrderByQuoteLineRaw;
import org.openmrs.module.billingservices.model.sale.order.raw.OrderByQuoteRaw;
import org.openmrs.module.billingservices.model.sale.payment.raw.OrderPayInstallmentRaw;
import org.openmrs.module.billingservices.model.sale.price.raw.FinancialPeriodRaw;
import org.openmrs.module.billingservices.model.sale.price.raw.ItemPriceRaw;
import org.openmrs.module.billingservices.model.sale.price.raw.ListVersionRaw;
import org.openmrs.module.billingservices.model.sale.quote.raw.QuoteStatusRaw;
import org.openmrs.module.billingservices.model.sale.quote.raw.SaleQuoteLineRaw;
import org.openmrs.module.billingservices.model.sale.quote.raw.SaleQuoteRaw;
import org.openmrs.module.billingservices.model.saleable.raw.ServiceTypeRaw;

import java.util.List;

/**
 *  Database methods for {@link BillingService}.
 */
public interface BillingDAO {

/**
  DAO specified methods
  @Author: Eric Mwailunga
  October,2019
*/

   /*...............Service type..................................*/
    String checkServiceTypeExistence(String name);

   /*...............Financial period..............................*/
    String checkFinancialPeriodExistence(String name);

   /*...............Price list....................................*/
    String checkPriceListVersionExistence(int financialPeriod, String versionName);

   /*...............Discount criteria.............................*/
    String checkDiscountCriteriaExistence(String name);

   /*...............Discount checking..............................*/
    String checkDiscountExistenceByQuoteLine(int quoteLine);
    String checkDiscountExistenceByUuid(String uuid);

   /*...............Maximum dated saleOrder id.....................*/
    String getNewDatedSaleId();

   /*...............Installment no for order line .................*/
    int getPaymentInstallmentNumber(int saleOrderLine);


/**
  Service types & related data processes
  @Author: Eric Mwailunga
  October,2019
*/

    String createServiceType(String name, int creator);

    ServiceTypeRaw fetchServiceTypeByName(String name);
    List<ServiceTypeRaw> fetchServiceTypeBySubName(String subName, boolean includeRetired);
    ServiceTypeRaw fetchServiceTypeByUuid(String serviceTypeUuid);
    ServiceTypeRaw fetchServiceTypeById(int serviceTypeId);
    List<ServiceTypeRaw> fetchAllServiceTypes(boolean includeRetired);

    String updateServiceTypeByUuid(String serviceTypeUuid, String name,int changedBy);

    String retireServiceTypeByUuid(String serviceTypeUuid,int changedBy);


/**
  Financial period & related data processes
  @Author: Eric Mwailunga
  October,2019
*/
    String createFinancialPeriod(String name, String startDate, String endDate, int creator);

    FinancialPeriodRaw fetchFinancialPeriodByName(String name);
    List<FinancialPeriodRaw> fetchFinancialPeriodBySubName(String subName);
    FinancialPeriodRaw fetchFinancialPeriodByUuid(String uuid);
    FinancialPeriodRaw fetchFinancialPeriodById(int id);
    List<FinancialPeriodRaw> fetchAllFinancialPeriods(int includeRetired);

    String updateFinancialPeriodByUuid(String financialPeriodUuid,String name,String startDate,String endDate,int changedBy);

    String retireFinancialPeriodByUuid(String financialPeriodUuid,int changedBy);


/**
  Price list, versions & related data processes
  @Author: Eric Mwailunga
  October,2019
*/
    String createPriceListVersion(int financialPeriodId, String versionName, String dateApproved, int creator);

    ListVersionRaw fetchPriceListVersionById(int listId);
    ListVersionRaw fetchPriceListVersionByName(int financialPeriodId, String versionName);
    List<ListVersionRaw> fetchPriceListVersionBySubName(int financialPeriodId, String versionSubName);
    ListVersionRaw fetchPriceListVersionByUuid(String priceListUuid);
    List<ListVersionRaw> fetchPriceListsByFinancialPeriodId(int financialPeriodId);
    List<ListVersionRaw> fetchAllPriceLists(int includeRetired);
    List<ListVersionRaw> fetchActivePriceLists();

    String retirePriceListVersionByUuid(String priceListUuid,int changedBy);


/**
  Items price & related data processes
  @Author: Eric Mwailunga
  October,2019
*/

    String createItemPrice(int itemId, int serviceTypeId, int priceListVersion, int paymentCategory, int paymentSubCategory, double sellingPrice, int creator);

    ItemPriceRaw fetchItemPriceByItemPriceId(int itemPriceId);
    ItemPriceRaw fetchItemPriceByItemAndPaymentCategory(int itemId,int serviceTypeId,int paymentCategoryId,int priceListVersionId);


/**
  Quotation status codes & related data
  @Author: Eric Mwailunga
  October,2019
*/

    String createQuoteStatusCode(String name, String quoteType, int creator);

    QuoteStatusRaw fetchQuoteStatusCodeById(int statusCodeId);
    QuoteStatusRaw fetchQuoteStatusCodeByUuid(String statusCodeUuid);
    List<QuoteStatusRaw> fetchAllQuoteStatusCodes();

/**
 Sale quote & related data processes
 @Author: Eric Mwailunga
 October,2019
*/

    SaleQuoteRaw createSaleQuote(int patient, int visit, int paymentCategory, int paymentSubCategoryId, int orderedBy);

    SaleQuoteRaw fetchSaleQuoteById(int quoteId);
    SaleQuoteRaw fetchSaleQuoteByUuid(String uuid);
    List<SaleQuoteRaw> fetchAllSaleQuotesByPatient(int patient, boolean isAscending);
    List<SaleQuoteRaw> fetchAllSaleQuotesByDates(String startDate, String endDate, boolean isAscending);
    List<SaleQuoteRaw> fetchAllSaleQuotes(int numberOfRecords, boolean isAscending);


/**
  Sale quote lines & related data processes
  @Author: Eric Mwailunga
  October,2019
*/

    String createSaleQuoteLines(String valuesClause);

    SaleQuoteLineRaw fetchSaleQuoteLineById(int quoteLineId);
    SaleQuoteLineRaw fetchSaleQuoteLineByUuid(String uuid);
    List<SaleQuoteLineRaw> fetchSaleQuoteLinesByQuoteId(int quoteId);


/**
  Discount criteria & related data processes
  @Author: Eric Mwailunga
  October,2019
*/

    String createDiscountCriteria(String name, String description, int creator);

    DiscountCriteriaRaw fetchDiscountCriteriaById(int criteriaNo);
    DiscountCriteriaRaw fetchDiscountCriteriaByUuid(String uuid);
    List<DiscountCriteriaRaw> fetchAllDiscountCriteria(boolean includeRetired);

    String updateDiscountCriteria(String uuid, String name, String description, int user);

    String retireDiscountCriteria(String uuid);

/**
 Discount & related data processes
 @Author: Eric Mwailunga
 October,2019
*/

    String createDiscount(int quoteLine, double originalQuotedAmount, double discountedAmount, int criteria, int initiator, String uuid);
    String approveDiscountByUuid(String discountUuid, int criteria, double approvedAmount, int approvedBy);

    DiscountRaw fetchDiscountByQuoteLineId(int quoteLineId);
    DiscountRaw fetchDiscountById(int discountId);
    DiscountRaw fetchDiscountByUuid(String uuid);
    List<DiscountRaw> fetchDiscountsByInitiator(int initiator, String startDate, String endDate, boolean isAscending);
    List<DiscountRaw> fetchDiscountsByApprovedBy(int approvedBy, String startDate, String endDate, boolean isAscending);
    List<DiscountRaw> fetchAllDiscountsByDates(String startDate, String endDate, boolean isAscending);
    List<DiscountRaw> fetchAllDiscountsByStatus(String status, boolean isAscending);

    String updateDiscountInitiation(String discountUuid, double discountedAmount, int criteria, int initiator);
    //String deleteDiscountByUuid();


/**
 Sale Order & related data processes
 @Author: Eric Mwailunga
 November,2019
*/

    String createSaleOrder(int saleQuote, String datedSaleId, int creator, String uuid);

    OrderByQuoteRaw fetchSaleOrderById(int quoteId);
    OrderByQuoteRaw fetchSaleOrderByUuid(String uuid);
    List<OrderByQuoteRaw> fetchAllSaleOrderByPatient(int patient, boolean isAscending);
    List<OrderByQuoteRaw> fetchAllSaleOrderByDates(String startDate, String endDate, boolean isAscending);
    List<OrderByQuoteRaw> fetchAllSaleOrders(int startAt, int offset, boolean isAscending);

/**
 Sale Order line & related data processes
 @Author: Eric Mwailunga
 November,2019
*/

    String createSaleOrderLines(String valuesClause);

    OrderByQuoteLineRaw fetchSaleOrderLineBySOQLNo(int soqlNo);
    OrderByQuoteLineRaw fetchSaleOrderLineByUuid(String uuid);
    OrderByQuoteLineRaw fetchSaleOrderLineByQuoteLine(int quoteLine);
    List<OrderByQuoteLineRaw> fetchSaleOrderLineBySaleOrderNo(int saleOrderNo);


/**
 Payment installments & related data processes
 @Author: Eric Mwailunga
 November,2019
*/
    String createPaymentInstallment(int saleOrderLine, double paidAmount, String receiptNo, int receivedBy);

    OrderPayInstallmentRaw fetchInstallmentById(int entryNo);
    OrderPayInstallmentRaw fetchInstallmentByUuid(String uuid);
    List<OrderPayInstallmentRaw> fetchInstallmentByOrderLine(int orderLineNo);
    List<OrderPayInstallmentRaw> fetchInstallmentByDates(String startDate, String endDate, boolean isAscending);
    List<OrderPayInstallmentRaw> fetchAllInstallments(int numberOfRecords, boolean isAscending);



}