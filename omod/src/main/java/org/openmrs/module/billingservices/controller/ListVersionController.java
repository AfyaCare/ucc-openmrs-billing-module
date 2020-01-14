package org.openmrs.module.billingservices.controller;

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.billingservices.api.BillingService;
import org.openmrs.module.billingservices.model.sale.price.ListVersion;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "rest/"+ RestConstants.VERSION_1+"/billing/listversion")
public class ListVersionController extends BaseRestController {
    private final Log log= LogFactory.getLog(ListVersionController.class);

    @RequestMapping(value="/new",method = RequestMethod.GET)
    @ResponseBody
    public String createPriceListVersion(@RequestParam("financialPeriodUuid") String financialPeriodUuid,
                                         @RequestParam("versionName") String versionName,
                                         @RequestParam("dateApproved") String dateApproved)
    {
        BillingService billingService = Context.getService(BillingService.class);
        if(billingService !=null)
        {
            log.info("PriceListController.createPriceListVersion()..");
            String response=billingService.createPriceListVersion(financialPeriodUuid,versionName,dateApproved);
            if(response!=null)
                return new Gson().toJson(response);
            return new Gson().toJson("failed");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/name",method = RequestMethod.GET)
    @ResponseBody
    public String fetchPriceListVersionByName(@RequestParam("financialPeriodUuid") String financialPeriodUuid,
                                         @RequestParam("versionName") String versionName)
    {
        BillingService billingService = Context.getService(BillingService.class);
        if(billingService !=null)
        {
            log.info("PriceListController.fetchPriceListVersionByName()..");
            String response;
            ListVersion priceList = billingService.fetchPriceListVersionByName(financialPeriodUuid,versionName);
            if(priceList!=null) {
                return new Gson().toJson(priceList);
            }
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/subname",method = RequestMethod.GET)
    @ResponseBody
    public String fetchPriceListVersionBySubName(@RequestParam("financialPeriodUuid") String financialPeriodUuid,
                                              @RequestParam("versionSubName") String versionSubName)
    {
        BillingService billingService = Context.getService(BillingService.class);
        if(billingService !=null)
        {
            log.info("PriceListController.fetchPriceListVersionBySubName()..");
            String response;
            List<ListVersion> priceLists = billingService.fetchPriceListVersionBySubName(financialPeriodUuid,versionSubName);
            if(priceLists!=null) {
                return new Gson().toJson(priceLists);
            }
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/{uuid}/read",method = RequestMethod.GET)
    @ResponseBody
    public String fetchPriceListVersionByUuid(@PathVariable("uuid") String priceListUuid)
    {
        BillingService billingService = Context.getService(BillingService.class);
        if(billingService !=null)
        {
            log.info("PriceListController.fetchPriceListVersionBySubName()..");
            String response;
            ListVersion priceList = billingService.fetchPriceListVersionByUuid(priceListUuid);
            if(priceList!=null) {
                return new Gson().toJson(priceList);
            }
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/id",method = RequestMethod.GET)
    @ResponseBody
    public String fetchPriceListVersionById(@RequestParam("priceListId") int priceListId)
    {
        BillingService billingService = Context.getService(BillingService.class);
        if(billingService !=null)
        {
            log.info("PriceListController.fetchPriceListVersionById()..");
            String response;
            ListVersion priceList = billingService.fetchPriceListVersionById(priceListId);
            if(priceList!=null) {
                return new Gson().toJson(priceList);
            }
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/all",method = RequestMethod.GET)
    @ResponseBody
    public String fetchAllPriceLists(@RequestParam("includeRetired") boolean includeRetired)
    {
        BillingService billingService = Context.getService(BillingService.class);
        if(billingService !=null)
        {
            log.info("PriceListController.fetchAllPriceLists()..");
            String response;
            List<ListVersion> priceLists = billingService.fetchAllPriceLists(includeRetired);
            if(priceLists!=null) {
                return new Gson().toJson(priceLists);
            }
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }


    @RequestMapping(value="/{uuid}/delete",method = RequestMethod.GET)
    @ResponseBody
    public String deleteFinancialPeriodByUuid(@PathVariable("uuid") String priceListUuid)
    {
        BillingService billingService = Context.getService(BillingService.class);
        if(billingService !=null){
            log.info("PriceListController.deleteFinancialPeriodByUuid()..");
            String response=billingService.retirePriceListVersionByUuid(priceListUuid);
            if(response!=null)
                return new Gson().toJson(response);
        }
        return "EC.207.1";
    }

}
