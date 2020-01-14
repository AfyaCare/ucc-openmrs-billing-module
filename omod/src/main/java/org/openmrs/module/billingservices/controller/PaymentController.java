package org.openmrs.module.billingservices.controller;

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.billingservices.api.BillingService;
import org.openmrs.module.billingservices.model.sale.payment.OrderPayInstallment;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/rest"+ RestConstants.VERSION_1+"/billing/payment")
public class PaymentController extends BaseRestController {

    //private final BillingService billingService= Context.getService(BillingService.class);
    private final Log log = LogFactory.getLog(this.getClass());

    @RequestMapping(value="/new",  method = RequestMethod.GET)
    @ResponseBody
    public String createPaymentInstallment(@RequestParam("saleOrderLineUuid") String saleOrderLineUuid,
                                           @RequestParam("paidAmount") double paidAmount,
                                           @RequestParam("receiptNo") String receiptNo)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            String response = billingService.createPaymentInstallment(saleOrderLineUuid, paidAmount, receiptNo);
            if(response!=null)
                return new Gson().toJson(response);
            return new Gson().toJson("failed");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/id",  method = RequestMethod.GET)
    @ResponseBody
    public String readPaymentInstallmentById(@RequestParam("installmentId") int installmentId)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            OrderPayInstallment orderPayInstallment = billingService.fetchInstallmentById(installmentId);
            if(orderPayInstallment!=null)
                return new Gson().toJson(orderPayInstallment);
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/uuid",  method = RequestMethod.GET)
    @ResponseBody
    public String readPaymentInstallmentByUuid(@RequestParam("uuid") String installmentUuid)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            OrderPayInstallment orderPayInstallment = billingService.fetchInstallmentByUuid(installmentUuid);
            if(orderPayInstallment!=null)
                return new Gson().toJson(orderPayInstallment);
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/orderline",  method = RequestMethod.GET)
    @ResponseBody
    public String readPaymentInstallmentByOrderLine(@RequestParam("orderLine") int orderLine)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            List<OrderPayInstallment> orderPayInstallmentList = billingService.fetchInstallmentByOrderLine(orderLine);
            if(orderPayInstallmentList.size()>0)
                return new Gson().toJson(orderPayInstallmentList);
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/dates",  method = RequestMethod.GET)
    @ResponseBody
    public String readPaymentInstallmentByDates(@RequestParam("startDate") String startDate,
                                                @RequestParam("endDate") String endDate,
                                                @RequestParam("isAscending") boolean isAscending)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            List<OrderPayInstallment> orderPayInstallmentList = billingService.fetchInstallmentByDates(startDate, endDate, isAscending);
            if(orderPayInstallmentList.size()>0)
                return new Gson().toJson(orderPayInstallmentList);
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/all",  method = RequestMethod.GET)
    @ResponseBody
    public String readAllPaymentInstallments(@RequestParam("startDate") String startDate,
                                             @RequestParam("endDate") String endDate,
                                             @RequestParam("isAscending") boolean isAscending)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            List<OrderPayInstallment> orderPayInstallmentList = billingService.fetchInstallmentByDates(startDate, endDate, isAscending);
            if(orderPayInstallmentList.size()>0)
                return new Gson().toJson(orderPayInstallmentList);
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

}
