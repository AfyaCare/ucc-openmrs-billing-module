package org.openmrs.module.billingservices.controller;

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.billingservices.api.BillingService;
import org.openmrs.module.billingservices.model.sale.discount.Discount;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/rest/"+ RestConstants.VERSION_1+"/billing/discount")
public class DiscountController extends BaseRestController {

    //private final BillingService billingService= Context.getService(BillingService.class);
    private final Log log = LogFactory.getLog(this.getClass());

    @RequestMapping(value="/new",  method = RequestMethod.GET)
    @ResponseBody
    public String createDiscount(@RequestParam("quoteLineId") int quoteLineId,
                                 @RequestParam("originalQuotedAmount") double originalQuotedAmount,
                                 @RequestParam("discountedAmount") double discountedAmount,
                                 @RequestParam("criteriaId") int criteriaId)
    {
        BillingService billingService= Context.getService(BillingService.class);

        if(billingService!=null)
        {
            String response = billingService.createDiscount(quoteLineId, originalQuotedAmount, discountedAmount, criteriaId);
            if(response!=null)
                return new Gson().toJson(response);
            return new Gson().toJson("failed");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/{uuid}/approve",  method = RequestMethod.GET)
    @ResponseBody
    public String approveDiscount(@PathVariable("uuid") String discountUuid,
                                 @RequestParam("approvedAmount") double approvedAmount,
                                 @RequestParam("criteriaId") int criteriaId)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            String response = billingService.approveDiscountByUuid(discountUuid, criteriaId, approvedAmount);
            if(response!=null)
                return new Gson().toJson(response);
            return new Gson().toJson("failed");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/id",  method = RequestMethod.GET)
    @ResponseBody
    public String readDiscountById(@RequestParam("discountId") int discountId)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            Discount discount = billingService.fetchDiscountById(discountId);
            if(discount!=null)
                return new Gson().toJson(discount);
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/uuid",  method = RequestMethod.GET)
    @ResponseBody
    public String readDiscountByUuid(@RequestParam("uuid") String discountUuid)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            Discount discount = billingService.fetchDiscountByUuid(discountUuid);
            if(discount!=null)
                return new Gson().toJson(discount);
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/initiator",  method = RequestMethod.GET)
    @ResponseBody
    public String readDiscountsByInitiator(@RequestParam("startDate") String startDate,
                                           @RequestParam("endDate") String endDate,
                                           @RequestParam("isAscending") boolean isAscending)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            List<Discount> discountList = billingService.fetchDiscountsByInitiator(startDate, endDate, isAscending);
            if(discountList.size()>0)
                return new Gson().toJson(discountList);
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/approvedby",  method = RequestMethod.GET)
    @ResponseBody
    public String readDiscountsByApprovedBy(@RequestParam("startDate") String startDate,
                                           @RequestParam("endDate") String endDate,
                                           @RequestParam("isAscending") boolean isAscending)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            List<Discount> discountList = billingService.fetchDiscountsByApprovedBy(startDate, endDate, isAscending);
            if(discountList.size()>0)
                return new Gson().toJson(discountList);
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/dates",  method = RequestMethod.GET)
    @ResponseBody
    public String readDiscountsByDates(@RequestParam("startDate") String startDate,
                                            @RequestParam("endDate") String endDate,
                                            @RequestParam("isAscending") boolean isAscending)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            List<Discount> discountList = billingService.fetchAllDiscountsByDates(startDate, endDate, isAscending);
            if(discountList.size()>0)
                return new Gson().toJson(discountList);
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/status",  method = RequestMethod.GET)
    @ResponseBody
    public String readDiscountsByStatus(@RequestParam("status") String status,
                                       @RequestParam("isAscending") boolean isAscending)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            List<Discount> discountList = billingService.fetchAllDiscountsByStatus(status, isAscending);
            if(discountList.size()>0)
                return new Gson().toJson(discountList);
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }


    @RequestMapping(value="/{uuid}/update",  method = RequestMethod.GET)
    @ResponseBody
    public String updateDiscountInitiation(@PathVariable("uuid") String discountUuid,
                                           @RequestParam("discountedAmount") double discountedAmount,
                                           @RequestParam("criteriaId") int criteriaId)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            String response = billingService.updateDiscountInitiation(discountUuid, discountedAmount, criteriaId);
            if(response!=null)
                return new Gson().toJson(response);
            return new Gson().toJson("failed");
        }
        return "EC.207.1";
    }


}
