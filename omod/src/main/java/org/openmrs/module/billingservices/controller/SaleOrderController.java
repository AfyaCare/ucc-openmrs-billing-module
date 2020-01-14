package org.openmrs.module.billingservices.controller;

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.billingservices.api.BillingService;
import org.openmrs.module.billingservices.model.sale.order.OrderByQuote;
import org.openmrs.module.billingservices.model.sale.order.OrderByQuoteLine;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/rest/"+ RestConstants.VERSION_1+"/billing/order")
public class SaleOrderController extends BaseRestController {

    //private final BillingService billingService= Context.getService(BillingService.class);
    private final Log log = LogFactory.getLog(this.getClass());

    @RequestMapping(value="/new",  method = RequestMethod.GET)
    @ResponseBody
    public String createSaleOrder(@RequestParam("quotationJson") String quotationJson)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            String response = billingService.createSaleOrder(quotationJson);
            if(response!=null)
                return new Gson().toJson(response);
            return new Gson().toJson("failed");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/id",  method = RequestMethod.GET)
    @ResponseBody
    public String readSaleOrderById(@RequestParam("orderId") int orderId)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            OrderByQuote orderByQuote = billingService.fetchSaleOrderById(orderId);
            if(orderByQuote!=null)
                return new Gson().toJson(orderByQuote);
            return new Gson().toJson("failed");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/uuid",  method = RequestMethod.GET)
    @ResponseBody
    public String readSaleOrderByUuid(@RequestParam("orderUuid") String orderUuid)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            OrderByQuote orderByQuote = billingService.fetchSaleOrderByUuid(orderUuid);
            if(orderByQuote!=null)
                return new Gson().toJson(orderByQuote);
            return new Gson().toJson("failed");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/patient",  method = RequestMethod.GET)
    @ResponseBody
    public String readSaleOrderByPatient(@RequestParam("patientUuid") String patientUuid,
                                      @RequestParam("isAscending") boolean isAscending)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            List<OrderByQuote> ordersByQuoteList = billingService.fetchAllSaleOrderByPatient(patientUuid, isAscending);
            if(ordersByQuoteList.size()>0)
                return new Gson().toJson(ordersByQuoteList);
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/dates",  method = RequestMethod.GET)
    @ResponseBody
    public String readSaleOrderByDates(@RequestParam("startDate") String startDate,
                                       @RequestParam("endDate") String endDate,
                                       @RequestParam("isAscending") boolean isAscending)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            List<OrderByQuote> ordersByQuoteList = billingService.fetchAllSaleOrderByDates(startDate, endDate, isAscending);
            if(ordersByQuoteList.size()>0)
                return new Gson().toJson(ordersByQuoteList);
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/all",  method = RequestMethod.GET)
    @ResponseBody
    public String readAllSaleOrders(@RequestParam("startAt") int startAt,
                                    @RequestParam("offset") int offset,
                                    @RequestParam("isAscending") boolean isAscending)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            List<OrderByQuote> ordersByQuoteList = billingService.fetchAllSaleOrders(startAt, offset, isAscending);
            if(ordersByQuoteList.size()>0)
                return new Gson().toJson(ordersByQuoteList);
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

/*
    Sales order lines
*/

    @RequestMapping(value="/line/read/id",  method = RequestMethod.GET)
    @ResponseBody
    public String readSaleOrderLineById(@RequestParam("orderLineId") int orderLineId)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            OrderByQuoteLine orderByQuoteLine = billingService.fetchSaleOrderLineById(orderLineId);
            if(orderByQuoteLine!=null)
                return new Gson().toJson(orderByQuoteLine);
            return new Gson().toJson("failed");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/line/read/uuid",  method = RequestMethod.GET)
    @ResponseBody
    public String readSaleOrderLineByUuid(@RequestParam("orderLineUuid") String orderLineUuid)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            OrderByQuoteLine orderByQuoteLine = billingService.fetchSaleOrderLineByUuid(orderLineUuid);
            if(orderByQuoteLine!=null)
                return new Gson().toJson(orderByQuoteLine);
            return new Gson().toJson("failed");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/line/read/quoteline",  method = RequestMethod.GET)
    @ResponseBody
    public String readSaleOrderLineByQuoteLine(@RequestParam("quoteLineId") int quoteLineId)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            OrderByQuoteLine orderByQuoteLine = billingService.fetchSaleOrderLineByQuoteLine(quoteLineId);
            if(orderByQuoteLine!=null)
                return new Gson().toJson(orderByQuoteLine);
            return new Gson().toJson("failed");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/line/read/order",  method = RequestMethod.GET)
    @ResponseBody
    public String readSaleOrderLineBySOQNo(@RequestParam("quoteId") int quoteId)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            List<OrderByQuoteLine> orderByQuoteLines = billingService.fetchSaleOrderLineBySaleOrderNo(quoteId);
            if(orderByQuoteLines.size()>0)
                return new Gson().toJson(orderByQuoteLines);
            return new Gson().toJson("failed");
        }
        return "EC.207.1";
    }


}
