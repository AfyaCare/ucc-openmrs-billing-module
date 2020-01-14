/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.billingservices.controller;

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openmrs.api.context.Context;
import org.openmrs.module.billingservices.api.BillingService;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The main controller.
 * @Author : Eric Mwailunga
 * October,2019
 */
@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 +"/billing")
public class MainController extends BaseRestController {
	
	private final Log log = LogFactory.getLog(getClass());

	@RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
	public String testMessage()
	{
        BillingService billingService =Context.getService(BillingService.class);
	    if(billingService !=null){
			log.info("Printed from Main Controller :: Billing services module");
		}
	    return "It is working..";
	}


	@RequestMapping(value = "/template", method = RequestMethod.GET)
	@ResponseBody
	public void getPriceTemplate(@RequestParam("types") String typesJsonString, HttpServletResponse response)
	{
		BillingService billingService =Context.getService(BillingService.class);
		if(billingService !=null){
			billingService.generateServicesTemplate(typesJsonString, response);
		}
	}

    @RequestMapping(value = "/template/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE ,method = RequestMethod.POST)
    @ResponseBody
    public String getTemplate(@RequestParam("templateFile") MultipartFile templateFile,
                              @RequestParam("financialPeriod") String financialPeriodUuid,
                              @RequestParam("listVersion") String listVersionUuid,
                              HttpServletResponse httpServletResponse)
    {
        BillingService billingService =Context.getService(BillingService.class);
        if(billingService !=null){
           String fileNameOg = templateFile.getOriginalFilename();
           if(fileNameOg.endsWith(".xlsx")) {
               String response = billingService.uploadServicesTemplate(listVersionUuid,templateFile,httpServletResponse);
                return new Gson().toJson(response);
           } else {
               return new Gson().toJson("Wrong file");
           }
        }
        return new Gson().toJson("failed");
    }

    @RequestMapping(value = "/template/test/1", method = RequestMethod.GET)
    @ResponseBody
    public void testTemplate(HttpServletResponse response)
    {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("sheet_moja");
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("Test");
        row.createCell(1).setCellValue("Ok");
        ServletOutputStream servletOutputStream;
        try {
            servletOutputStream = response.getOutputStream();
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=sample.xlsx");
            workbook.write(servletOutputStream);
            workbook.close();
            servletOutputStream.flush();
            servletOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
