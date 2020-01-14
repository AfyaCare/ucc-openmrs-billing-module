package org.openmrs.module.billingservices.controller;

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.billingservices.api.BillingService;
import org.openmrs.module.billingservices.model.saleable.ServiceType;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "rest/"+ RestConstants.VERSION_1+"/billing/servicetype")
public class ServiceTypeController extends BaseRestController {
    private final Log log= LogFactory.getLog(ListVersionController.class);

    @RequestMapping(value="/new",method = RequestMethod.GET)
    @ResponseBody
    public String createServiceType(@RequestParam("name") String name) {
        BillingService billingService = Context.getService(BillingService.class);
        if (billingService != null) {
            log.info("ServiceTypeController.createServiceType()..");
            String response=billingService.createServiceType(name);
            if(response!=null)
                return new Gson().toJson(response);
        }
        return "EC.207.1";
    }


    @RequestMapping(value="/read/name",method = RequestMethod.GET)
    @ResponseBody
    public String readServiceTypeByName(@RequestParam("name") String name) {
        BillingService billingService = Context.getService(BillingService.class);
        if (billingService != null) {
            log.info("ServiceTypeController.readServiceTypeByName()..");
            ServiceType serviceType=billingService.fetchServiceTypeByName(name);
            if(serviceType!=null)
            {
                return new Gson().toJson(serviceType);
            }
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/subname",method = RequestMethod.GET)
    @ResponseBody
    public String readServiceTypeBySubName(@RequestParam("subName") String subName,
                                           @RequestParam("includeRetired") boolean includeRetired) {
        BillingService billingService = Context.getService(BillingService.class);
        if (billingService != null) {
            log.info("ServiceTypeController.readServiceTypeBySubName()..");
            List<ServiceType> serviceTypes=billingService.fetchServiceTypeBySubName(subName,includeRetired);
            if(serviceTypes!=null)
            {
                return new Gson().toJson(serviceTypes);
            }
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/{uuid}/read",method = RequestMethod.GET)
    @ResponseBody
    public String readServiceTypeByUuid(@PathVariable("uuid") String serviceTypeUuid) {
        BillingService billingService = Context.getService(BillingService.class);
        if (billingService != null) {
            log.info("ServiceTypeController.readServiceTypeByUuid()..");
            ServiceType serviceType=billingService.fetchServiceTypeByUuid(serviceTypeUuid);
            if(serviceType!=null)
            {
                return new Gson().toJson(serviceType);
            }
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/id",method = RequestMethod.GET)
    @ResponseBody
    public String readServiceTypeById(@RequestParam("id") int serviceTypeId) {
        BillingService billingService = Context.getService(BillingService.class);
        if (billingService != null) {
            log.info("ServiceTypeController.readServiceTypeById()..");
            ServiceType serviceType=billingService.fetchServiceTypeById(serviceTypeId);
            if(serviceType!=null)
            {
                return new Gson().toJson(serviceType);
            }
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/all",method = RequestMethod.GET)
    @ResponseBody
    public String readAllServiceTypes(@RequestParam("includeRetired") boolean includeRetired) {
        BillingService billingService = Context.getService(BillingService.class);
        if (billingService != null) {
            log.info("ServiceTypeController.readAllServiceTypes()..");
            List<ServiceType> serviceTypes=billingService.fetchAllServiceTypes(includeRetired);
            if(serviceTypes.size()>0)
            {
                return new Gson().toJson(serviceTypes);
            }
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }


    @RequestMapping(value="/{uuid}/update",method = RequestMethod.GET)
    @ResponseBody
    public String updateServiceTypeByUuid(@PathVariable("uuid") String serviceTypeUuid,
                                          @RequestParam("name") String name) {
        BillingService billingService = Context.getService(BillingService.class);
        if (billingService != null) {
            log.info("ServiceTypeController.updateServiceTypeByUuid()..");
            String response=billingService.updateServiceTypeByUuid(serviceTypeUuid,name);
            if(response!=null)
                return new Gson().toJson(response);
        }
        return "EC.207.1";
    }
}
