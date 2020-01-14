package org.openmrs.module.billingservices.controller;

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.billingservices.api.BillingService;
import org.openmrs.module.billingservices.model.sale.discount.DiscountCriteria;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Controller
@RequestMapping(value = "/rest/"+ RestConstants.VERSION_1+"/billing/discountcriteria")
public class DiscountCriteriaController extends BaseRestController{

    private final Log log = LogFactory.getLog(DiscountCriteriaController.class);

    @RequestMapping(value="/new", method = RequestMethod.GET)
    @ResponseBody
    public String createDiscountCriteria(@RequestParam("name") String name, @RequestParam("description") String description)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            String response = billingService.createDiscountCriteria(name, description);
            if(response!=null)
                return new Gson().toJson(response);
            return new Gson().toJson("failed");
        }
        return "EC.207.1";
    }


    @RequestMapping(value="/read/id", method = RequestMethod.GET)
    @ResponseBody
    public String readDiscountCriteriaById(@RequestParam("criteriaId") int criteriaId) {
        BillingService billingService= Context.getService(BillingService.class);
        if (billingService != null) {
            DiscountCriteria discountCriteria = billingService.fetchDiscountCriteriaById(criteriaId);
            if(discountCriteria!=null)
                return new Gson().toJson(discountCriteria);
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/uuid", method = RequestMethod.GET)
    @ResponseBody
    public String readDiscountCriteriaByUuid(@RequestParam("criteriaUuid") String criteriaUuid) {
        BillingService billingService= Context.getService(BillingService.class);
        if (billingService != null) {
            DiscountCriteria discountCriteria = billingService.fetchDiscountCriteriaByUuid(criteriaUuid);
            if(discountCriteria!=null)
                return new Gson().toJson(discountCriteria);
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/all", method = RequestMethod.GET)
    @ResponseBody
    public String readAllDiscountCriteria(@RequestParam("includeRetired") boolean includeRetired) {
        BillingService billingService= Context.getService(BillingService.class);
        if (billingService != null) {
            List<DiscountCriteria> discountCriteriaList = billingService.fetchAllDiscountCriteria(includeRetired);
            if(discountCriteriaList!=null) {
                return new Gson().toJson(discountCriteriaList);
            }
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }


    @RequestMapping(value="/update", method = RequestMethod.GET)
    @ResponseBody
    public String updateDiscountCriteria(@RequestParam("uuid") String uuid,
                                         @RequestParam("name") String name,
                                         @RequestParam("description") String description)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            String response = billingService.updateDiscountCriteria(uuid, name, description);
            if(response!=null)
                return new Gson().toJson(response);
            return new Gson().toJson("failed");
        }
        return "EC.207.1";
    }


    @RequestMapping(value="/delete", method = RequestMethod.GET)
    @ResponseBody
    public String deleteDiscountCriteria(@RequestParam("uuid") String uuid)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService!=null)
        {
            String response = billingService.retireDiscountCriteria(uuid);
            if(response!=null)
                return new Gson().toJson(response);
            return new Gson().toJson("failed");
        }
        return "EC.207.1";
    }

}
