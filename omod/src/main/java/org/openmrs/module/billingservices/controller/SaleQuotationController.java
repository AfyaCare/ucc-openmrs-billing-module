package org.openmrs.module.billingservices.controller;

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.billingservices.api.BillingService;
import org.openmrs.module.billingservices.model.sale.quote.SaleQuote;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "rest/"+ RestConstants.VERSION_1+"/billing/quotation")
public class SaleQuotationController extends BaseRestController {

    private final Log log = LogFactory.getLog(this.getClass());

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    @ResponseBody
    public String createSaleQuote(@RequestParam("quotationJson") String quotationJson)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            log.info("SaleQuotationController.createSaleQuote()..");
            String response=billingService.createSaleQuote(quotationJson);
            if(response!=null) {
                return new Gson().toJson(response);
            }
            return new Gson().toJson("failed");
        }
        return "EC.207.1";
    }

    @RequestMapping(value = "/read/id", method = RequestMethod.GET)
    @ResponseBody
    public String readSaleQuoteById(@RequestParam("quoteId") int quoteId)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            log.info("SaleQuotationController.fetchSaleQuoteById()..");
            SaleQuote saleQuote = billingService.fetchSaleQuoteById(quoteId);
            if(saleQuote!=null) {
                return new Gson().toJson(saleQuote);
            }
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

    @RequestMapping(value = "/read/uuid", method = RequestMethod.GET)
    @ResponseBody
    public String readSaleQuoteByUuid(@RequestParam("quoteUuid") String quoteUuid)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            log.info("SaleQuotationController.fetchSaleQuoteByUuid()..");
            SaleQuote saleQuote = billingService.fetchSaleQuoteByUuid(quoteUuid);
            if(saleQuote!=null) {
                return new Gson().toJson(saleQuote);
            }
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

    @RequestMapping(value = "/read/patient", method = RequestMethod.GET)
    @ResponseBody
    public String readAllSaleQuotesByPatient(@RequestParam("patient") String patientUuid,
                                              @RequestParam("isAscending") boolean isAscending)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            log.info("SaleQuotationController.fetchSaleQuoteByUuid()..");
            List<SaleQuote> saleQuotes = billingService.fetchAllSaleQuotesByPatient(patientUuid,isAscending);
            if(saleQuotes.size()>0) {
                return new Gson().toJson(saleQuotes);
            }
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

    @RequestMapping(value = "/read/dates", method = RequestMethod.GET)
    @ResponseBody
    public String readAllSaleQuotesByDates(@RequestParam("startDate") String startDate,
                                            @RequestParam("endDate") String endDate,
                                            @RequestParam("isAscending") boolean isAscending)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            log.info("SaleQuotationController.fetchSaleQuoteByUuid()..");
            List<SaleQuote> saleQuotes = billingService.fetchAllSaleQuotesByDates(startDate,endDate,isAscending);
            if(saleQuotes.size()>0) {
                return new Gson().toJson(saleQuotes);
            }
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }


    @RequestMapping(value = "/read/all", method = RequestMethod.GET)
    @ResponseBody
    public String readAllSaleQuotes(@RequestParam("numberOfRecords") int numberOfRecords,
                                     @RequestParam("isAscending") boolean isAscending)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            log.info("SaleQuotationController.fetchSaleQuoteByUuid()..");
            List<SaleQuote> saleQuotes = billingService.fetchAllSaleQuotes(numberOfRecords,isAscending);
            if(saleQuotes.size()>0) {
                return new Gson().toJson(saleQuotes);
            }
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }


}
