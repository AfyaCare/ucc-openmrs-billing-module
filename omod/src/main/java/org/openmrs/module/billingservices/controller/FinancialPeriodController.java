package org.openmrs.module.billingservices.controller;

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.billingservices.api.BillingService;
import org.openmrs.module.billingservices.model.sale.price.FinancialPeriod;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping(value = "/rest/"+ RestConstants.VERSION_1+"/billing/financialperiod")
public class FinancialPeriodController extends BaseRestController {
    private final Log log = LogFactory.getLog(getClass());

    /*
    * Error Codes: EC
    * EC.2017.1 - Service not found in openmrs context.
    *
    */
    @RequestMapping(value="/new",method = RequestMethod.GET)
    @ResponseBody
    public String createFinancialPeriod(@RequestParam("name") String name,
                              @RequestParam("startDate") String startDate,
                              @RequestParam("endDate") String endDate)
    {
        BillingService billingService = Context.getService(BillingService.class);
        if(billingService !=null){
            log.info("FinancialPeriodController.createFinancialPeriod()..");
            String response=billingService.createFinancialPeriod(name,startDate,endDate);
            if(response!=null)
                return new Gson().toJson(response);
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/id",method = RequestMethod.GET)
    @ResponseBody
    public String readFinancialPeriodById(@RequestParam("id") int id)
    {
        BillingService billingService = Context.getService(BillingService.class);
        if(billingService !=null){
            log.info("FinancialPeriodController.readFinancialPeriodById()..");
            FinancialPeriod financialPeriod = billingService.fetchFinancialPeriodById(id);
            if(financialPeriod!=null)
            {
                return new Gson().toJson(financialPeriod);
            }
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/name",method = RequestMethod.GET)
    @ResponseBody
    public String readFinancialPeriodByName(@RequestParam("name") String name)
    {
        BillingService billingService = Context.getService(BillingService.class);
        if(billingService !=null){
            log.info("FinancialPeriodController.readFinancialPeriodByName()..");
            FinancialPeriod financialPeriod = billingService.fetchFinancialPeriodByName(name);
            if(financialPeriod!=null)
            {
                return new Gson().toJson(financialPeriod);
            }
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/subname",method = RequestMethod.GET)
    @ResponseBody
    public String readFinancialPeriodBySubName(@RequestParam("subName") String subName)
    {
        BillingService billingService = Context.getService(BillingService.class);
        if(billingService !=null)
        {
            log.info("FinancialPeriodController.readFinancialPeriodBySubName()..");
            List<FinancialPeriod> financialPeriods = billingService.fetchFinancialPeriodBySubName(subName);
            if(financialPeriods!=null)
            {
                return new Gson().toJson(financialPeriods);
            }
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/{uuid}/read",method = RequestMethod.GET)
    @ResponseBody
    public String readFinancialPeriodByUuid(@PathVariable("uuid") String uuid)
    {
        BillingService billingService = Context.getService(BillingService.class);
        if(billingService !=null)
        {
            log.info("FinancialPeriodController.readFinancialPeriodByUuid()..");
            FinancialPeriod financialPeriod = billingService.fetchFinancialPeriodByUuid(uuid);
            if(financialPeriod!=null)
            {
                return new Gson().toJson(financialPeriod);
            }
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/all",method = RequestMethod.GET)
    @ResponseBody
    public String readAllFinancialPeriods(@RequestParam("includeRetired") boolean includeRetired)
    {
        BillingService billingService = Context.getService(BillingService.class);
        if(billingService !=null)
        {
            log.info("FinancialPeriodController.readAllFinancialPeriods()..");
            List<FinancialPeriod> financialPeriods = billingService.fetchAllFinancialPeriods(includeRetired);
            if(financialPeriods!=null)
            {
                return new Gson().toJson(financialPeriods);
            }
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/{uuid}/update",method = RequestMethod.GET)
    @ResponseBody
    public String updateFinancialPeriodByUuid(@PathVariable("uuid") String uuid,
                                              @RequestParam("name") String name,
                                              @RequestParam("startDate") String startDate,
                                              @RequestParam("endDate") String endDate)
    {
        BillingService billingService = Context.getService(BillingService.class);
        if(billingService !=null){
            log.info("FinancialPeriodController.updateFinancialPeriod()..");
            String response=billingService.updateFinancialPeriodByUuid(uuid,name,startDate,endDate);
            if(response!=null)
                return new Gson().toJson(response);
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/{uuid}/delete",method = RequestMethod.GET)
    @ResponseBody
    public String deleteFinancialPeriodByUuid(@PathVariable("uuid") String financialPeriodUuid)
    {
        BillingService billingService = Context.getService(BillingService.class);
        if(billingService !=null){
            log.info("FinancialPeriodController.deleteFinancialPeriodByUuid()..");
            String response=billingService.retireFinancialPeriodByUuid(financialPeriodUuid);
            if(response!=null)
                return new Gson().toJson(response);
        }
        return "EC.207.1";
    }
}
