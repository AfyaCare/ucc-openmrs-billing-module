package org.openmrs.module.billingservices.controller;


import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.billingservices.api.BillingService;
import org.openmrs.module.billingservices.model.sale.price.ItemPrice;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "rest/"+ RestConstants.VERSION_1+"/billing/itemprice")
public class ItemPriceController  extends BaseRestController {
    //private final BillingService billingService = Context.getService(BillingService.class);
    private final Log log= LogFactory.getLog(ListVersionController.class);

    @RequestMapping(value="/new",method = RequestMethod.GET)
    @ResponseBody
    public String createItemPrice(@RequestParam("itemId") int itemId,
                                  @RequestParam("serviceTypeId") int serviceTypeId,
                                  @RequestParam("priceListVersion") int priceListVersion,
                                  @RequestParam("paymentCategory") int paymentCategory,
                                  @RequestParam("paymentSubCategory") int paymentSubCategory,
                                  @RequestParam("sellingPrice") double sellingPrice)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService !=null)
        {
            log.info("ItemPriceController.createItemPrice()..");
            String response=billingService.createItemPrice(itemId,serviceTypeId,priceListVersion,paymentCategory,paymentSubCategory,sellingPrice);
            if(response!=null){
                return new Gson().toJson(response);
            }
            return new Gson().toJson("failed");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/id",method = RequestMethod.GET)
    @ResponseBody
    public String readItemPriceByItemPriceId(@RequestParam("itemPriceId") int itemPriceId)
    {
        BillingService billingService= Context.getService(BillingService.class);
        if(billingService !=null)
        {
            log.info("ItemPriceController.readItemPriceByItemPriceId()..");
            ItemPrice itemPrice=billingService.fetchItemPriceByItemPriceId(itemPriceId);
            if(itemPrice!=null) {
                return new Gson().toJson(itemPrice);
            }
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }

    @RequestMapping(value="/read/custom",method = RequestMethod.GET)
    @ResponseBody
    public String readItemPriceByItemAndPaymentCategory(@RequestParam("v") String jsonDataObject)
    {
        BillingService billingService = Context.getService(BillingService.class);
        if(billingService !=null)
        {
            log.info("ItemPriceController.readItemPriceByItemPriceId()..");
            ItemPrice itemPrice=billingService.fetchItemPriceByItemAndPaymentCategory(jsonDataObject);
            if(itemPrice!=null) {
                return new Gson().toJson(itemPrice);
            }
            return new Gson().toJson("empty");
        }
        return "EC.207.1";
    }
}
