/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.billingservices.api;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.billingservices.model.sale.discount.Discount;
import org.openmrs.module.billingservices.model.sale.discount.DiscountCriteria;
import org.openmrs.module.billingservices.model.sale.order.OrderByQuote;
import org.openmrs.module.billingservices.model.sale.order.OrderByQuoteLine;
import org.openmrs.module.billingservices.model.sale.payment.OrderPayInstallment;
import org.openmrs.module.billingservices.model.sale.price.FinancialPeriod;
import org.openmrs.module.billingservices.model.sale.price.ItemPrice;
import org.openmrs.module.billingservices.model.sale.price.ListVersion;
import org.openmrs.module.billingservices.model.sale.quote.QuoteStatus;
import org.openmrs.module.billingservices.model.sale.quote.SaleQuote;
import org.openmrs.module.billingservices.model.sale.quote.SaleQuoteLine;
import org.openmrs.module.billingservices.model.saleable.ServiceType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * This service exposes module's core functionality.
 */
@Transactional
public interface BillingService extends OpenmrsService {




/**
 Items' price templates, down/up loading  & related processes
 @Author: Eric Mwailunga
 October,2019
 */

    void generateServicesTemplate(String typesJsonString, HttpServletResponse response);
    String uploadServicesTemplate(String listVersionUuid, MultipartFile templateFile, HttpServletResponse response);

/**
  Service types & related processes
  @Author: Eric Mwailunga
  October,2019
*/

    String createServiceType(String name);

    ServiceType fetchServiceTypeByName(String name);
    List<ServiceType> fetchServiceTypeBySubName(String name, boolean includeRetired);
    ServiceType fetchServiceTypeByUuid(String serviceTypeUuid);
    ServiceType fetchServiceTypeById(int serviceTypeId);
    List<ServiceType> fetchAllServiceTypes(boolean includeRetired);

    String updateServiceTypeByUuid(String serviceTypeUuid, String name);


/**
  Financial period & related processes
  @Author: Eric Mwailunga
  October,2019
*/

    String createFinancialPeriod(String name, String startDate, String endDate);

    FinancialPeriod fetchFinancialPeriodByName(String name);
    List<FinancialPeriod> fetchFinancialPeriodBySubName(String name);
    FinancialPeriod fetchFinancialPeriodByUuid(String uuid);
    FinancialPeriod fetchFinancialPeriodById(int id);
    List<FinancialPeriod> fetchAllFinancialPeriods(boolean includeRetired);

    String updateFinancialPeriodByUuid(String financialPeriodUuid,String name,String startDate,String endDate);

    String retireFinancialPeriodByUuid(String financialPeriodUuid);


/**
  Price list, versions & related processes
  @Author: Eric Mwailunga
  October,2019
*/

    String createPriceListVersion(String financialPeriodUuid, String versionName, String dateApproved);

    ListVersion fetchPriceListVersionByName(String financialPeriodUuid, String versionName);
    List<ListVersion> fetchPriceListVersionBySubName(String financialPeriodUuid, String versionSubName);
    ListVersion fetchPriceListVersionByUuid(String priceListUuid);
    ListVersion fetchPriceListVersionById(int id);
    List<ListVersion> fetchAllPriceLists(boolean includeRetired);

    String retirePriceListVersionByUuid(String priceListUuid);


/**
  Items' price & related processes
  @Author: Eric Mwailunga
  October,2019
*/

    String createItemPrice(int itemId, int serviceTypeId, int priceListVersion, int paymentCategory, int paymentSubCategory, double sellingPrice);

    ItemPrice fetchItemPriceByItemPriceId(int itemPriceId);
    ItemPrice fetchItemPriceByItemAndPaymentCategory(String jsonDataObject);
    List<ItemPrice> fetchItemsPricesByFinancialYear(String financialYearUuid);
    List<ItemPrice> fetchItemsPricesByPriceList(String priceListUuid, int includeRetired);
    //List<ItemPrice> fetchItemsPricesByPriceVersion();

    String retireItemPriceByPriceListVersion(String priceListUuid, String itemPriceUuid);


/**
 Quotation status codes & related processes
 @Author: Eric Mwailunga
 October,2019
*/
    String createQuoteStatusCode(String name, String quoteType);

    QuoteStatus fetchQuoteStatusCodeById(int codeId);
    QuoteStatus fetchQuoteStatusCodeByUuid(String uuid);
    List<QuoteStatus> fetchAllQuoteStatusCodes();

/**
 Quotation & related processes
 @Author: Eric Mwailunga
 October,2019
*/

    String createSaleQuote(String quotationJson);

    SaleQuote fetchSaleQuoteById(int quoteId);
    SaleQuote fetchSaleQuoteByUuid(String uuid);
    List<SaleQuote> fetchAllSaleQuotesByPatient(String patientUuid, boolean isAscending);
    List<SaleQuote> fetchAllSaleQuotesByDates(String startDate, String endDate, boolean isAscending);
    List<SaleQuote> fetchAllSaleQuotes(int numberOfRecords, boolean isAscending);


/**
 Quotation line & related processes
 @Author: Eric Mwailunga
 October,2019
*/

    SaleQuoteLine fetchSaleQuoteLineById(int Id);
    SaleQuoteLine fetchSaleQuoteLineByUuid(String uuid);
    List<SaleQuoteLine> fetchSaleQuoteLinesByQuoteId(int quoteId);

    //String retireSaleQuoteLine(String );

/**
 Discount criteria & related processes
 @Author: Eric Mwailunga
 October,2019
*/
    String createDiscountCriteria(String name, String description);

    DiscountCriteria fetchDiscountCriteriaById(int criteriaNo);
    DiscountCriteria fetchDiscountCriteriaByUuid(String uuid);
    List<DiscountCriteria> fetchAllDiscountCriteria(boolean includeRetired);

    String updateDiscountCriteria(String uuid, String name, String description);

    String retireDiscountCriteria(String uuid);

/**
 Discount & related data processes
 @Author: Eric Mwailunga
 October,2019
*/
    String createDiscount(int quoteLine, double originalQuotedAmount, double discountedAmount, int criteria);
    String approveDiscountByUuid(String discountUuid, int criteria, double approvedAmount);

    Discount fetchDiscountById(int discountId);
    Discount fetchDiscountByUuid(String uuid);
    List<Discount> fetchDiscountsByInitiator(String startDate, String endDate, boolean isAscending);
    List<Discount> fetchDiscountsByApprovedBy(String startDate, String endDate, boolean isAscending);
    List<Discount> fetchAllDiscountsByDates(String startDate, String endDate, boolean isAscending);
    List<Discount> fetchAllDiscountsByStatus(String status, boolean isAscending);

    String updateDiscountInitiation(String discountUuid, double discountedAmount, int criteria);


/**
 Order & related processes
 @Author: Eric Mwailunga
 November,2019
*/

    String createSaleOrder(String quotationJson);

    OrderByQuote fetchSaleOrderById(int quoteId);
    OrderByQuote fetchSaleOrderByUuid(String uuid);
    List<OrderByQuote> fetchAllSaleOrderByPatient(String patientUuid, boolean isAscending);
    List<OrderByQuote> fetchAllSaleOrderByDates(String startDate, String endDate, boolean isAscending);
    List<OrderByQuote> fetchAllSaleOrders(int startAt, int offset, boolean isAscending);


/**
 Order lines & related processes
 @Author: Eric Mwailunga
 November,2019
*/
    OrderByQuoteLine fetchSaleOrderLineById(int quoteId);

    OrderByQuoteLine fetchSaleOrderLineByUuid(String uuid);
    OrderByQuoteLine fetchSaleOrderLineByQuoteLine(int quoteLine);
    List<OrderByQuoteLine> fetchSaleOrderLineBySaleOrderNo(int saleOrderNo);


/**
 Order pay installment & related processes
 @Author: Eric Mwailunga
 November,2019
*/
    String createPaymentInstallment(String saleOrderLineUuid, double paidAmount, String receiptNo);

    OrderPayInstallment fetchInstallmentById(int entryNo);
    OrderPayInstallment fetchInstallmentByUuid(String uuid);
    List<OrderPayInstallment> fetchInstallmentByOrderLine(int orderLineNo);
    List<OrderPayInstallment> fetchInstallmentByDates(String startDate, String endDate, boolean isAscending);
    List<OrderPayInstallment> fetchAllInstallments(int numberOfRecords, boolean isAscending);


}