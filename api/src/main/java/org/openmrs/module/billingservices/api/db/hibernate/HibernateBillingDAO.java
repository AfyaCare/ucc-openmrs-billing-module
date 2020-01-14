/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.billingservices.api.db.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.billingservices.api.db.BillingDAO;
import org.openmrs.module.billingservices.model.sale.discount.raw.DiscountCriteriaRaw;
import org.openmrs.module.billingservices.model.sale.discount.raw.DiscountRaw;
import org.openmrs.module.billingservices.model.sale.order.raw.OrderByQuoteLineRaw;
import org.openmrs.module.billingservices.model.sale.order.raw.OrderByQuoteRaw;
import org.openmrs.module.billingservices.model.sale.payment.raw.OrderPayInstallmentRaw;
import org.openmrs.module.billingservices.model.sale.price.FinancialPeriod;
import org.openmrs.module.billingservices.model.sale.price.raw.FinancialPeriodRaw;
import org.openmrs.module.billingservices.model.sale.price.raw.ItemPriceRaw;
import org.openmrs.module.billingservices.model.sale.price.raw.ListVersionRaw;
import org.openmrs.module.billingservices.model.sale.quote.raw.QuoteStatusRaw;
import org.openmrs.module.billingservices.model.sale.quote.raw.SaleQuoteLineRaw;
import org.openmrs.module.billingservices.model.sale.quote.raw.SaleQuoteRaw;
import org.openmrs.module.billingservices.model.saleable.ServiceType;
import org.openmrs.module.billingservices.model.saleable.raw.ServiceTypeRaw;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Implementation of  {@link BillingDAO}.
 */
public class HibernateBillingDAO implements BillingDAO {
    
    protected final Log log = LogFactory.getLog(this.getClass());
	
    private DbSessionFactory sessionFactory;
	
    /**
        * @param sessionFactory sessionFactory to be set
    */
    public void setSessionFactory(DbSessionFactory sessionFactory) {
	    this.sessionFactory = sessionFactory;
    }
    
    /**
        * @return DbSessionFactory instance
    */
    public DbSessionFactory getSessionFactory() {
	    return sessionFactory;
    }

/**
  DAO specified methods
  @Author: Eric Mwailunga
  October,2019
*/

    //...................Query building with results transformation...........................//
    private Query createSQLQueryAndTransform(String hql, Class transformationBean)
    {
        DbSession session=sessionFactory.getCurrentSession();
        return session.createSQLQuery(hql).setResultTransformer(Transformers.aliasToBean(transformationBean));
    }

    //...................Query building with no transformation.........................//
    private Query createSQLQuery(String hql) {
        DbSession session = sessionFactory.getCurrentSession();
       return session.createSQLQuery(hql);
    }

    //...............Service type....................//
    public String checkServiceTypeExistence(String name)
    {
        String hql="select service_type_id as 'serviceTypeId', name as 'name' from bl_service_type where name='"+name+"'";
        List serviceTypes=createSQLQueryAndTransform(hql, ServiceType.class).list();
        if(serviceTypes.size()>0) {
            return "exists";
        }
        else {
            return "not-existing";
        }
    }

    //...................Financial period.....................//
    public String checkFinancialPeriodExistence(String name)
    {
        String hql="select period_id as 'periodId', name as 'name' from bl_financial_period where name='"+name+"'";
        List financialPeriods=createSQLQueryAndTransform(hql,FinancialPeriod.class).list();
        if(financialPeriods.size()>0) {
            return "exists";
        }
        else {
            return "not-existing";
        }
    }

    //...................Sale quote.....................//
    private String markSaleQuoteAsDiscounted(SaleQuoteLineRaw saleQuoteLineRaw)
    {
        String hql="update table bl_sale_quote set discounted = '1' where quote_id ='"+saleQuoteLineRaw.getSaleQuote()+"'";
        Query query=createSQLQuery(hql);
        if(query.executeUpdate()>0) {
            return "success";
        }
        else {
            return "failed";
        }
    }

    //...................Price list, versions.....................//
    public String checkPriceListVersionExistence(int financialPeriod, String versionName)
    {
        String hql="select list_id as 'listId', version_name as 'versionName', uuid as 'uuid'" +
                " from bl_price_list_version" +
                " where financial_period='"+financialPeriod+"' and version_name='"+versionName+"'";
        List priceList=createSQLQueryAndTransform(hql, ListVersionRaw.class).list();
        if(priceList.size()>0) {
            return "exists";
        }
        else {
            return "not-existing";
        }
    }

    //...............Discount criteria....................//
    public String checkDiscountCriteriaExistence(String name)
    {
        String hql="select criteria_no as 'criteriaNo', name as 'name' from bl_discount_criteria where name='"+name+"'";
        List discountCriteriaRaws=createSQLQueryAndTransform(hql, DiscountCriteriaRaw.class).list();
        if(discountCriteriaRaws.size()>0) {
            return "exists";
        }
        else {
            return "not-existing";
        }
    }

    /*...............Discount.....................*/
    private String checkDiscountExistence(String hql)
    {
        List discountRaws=createSQLQueryAndTransform(hql, DiscountRaw.class).list();
        if(discountRaws.size()>0) {
            return "exists";
        }
        else {
            return "not-existing";
        }
    }

    public String checkDiscountExistenceByQuoteLine(int quoteLine)
    {
        String hql="select discount_id as 'discountId' from bl_discount where quote_line='"+quoteLine+"'";
        return checkDiscountExistence(hql);
    }

    public String checkDiscountExistenceByUuid(String uuid)
    {
        String hql="select discount_id as 'discountId' from bl_discount where uuid='"+uuid+"'";
        return checkDiscountExistence(hql);
    }

    /*...............Maximum dated saleOrder id.....................*/
    public String getNewDatedSaleId()
    {
        DateFormat simpleDateFormat = new SimpleDateFormat("dd-mm-yyyy");
        Date today = new Date();
        String todayDate = simpleDateFormat.format(today);
        String hql  = "select data.dated_sale_id as datedSaleId from (select soq_no, dated_sale_id from bl_sale_order_by_quote where date_created like '%"+todayDate+"%') as data where data.soq_no = max(data.soq_no)";
        List results = createSQLQueryAndTransform(hql,OrderByQuoteRaw.class).list();
        if(results.size()>0)
        {
            for(Object object : results)
            {
                OrderByQuoteRaw orderByQuoteRaw = (OrderByQuoteRaw) object;
                String maxDatedSale = orderByQuoteRaw.getDatedSaleId();
                String[] parts = maxDatedSale.split("-");
                int newSaleNo = (Integer.parseInt(parts[1]))+1;
                String prefix="";
                if(newSaleNo<10)
                {
                    prefix="000";
                }
                else if(newSaleNo<100)
                {
                    prefix="00";
                }
                else if(newSaleNo<1000)
                {
                    prefix="0";
                }
                return todayDate + "-"+prefix+newSaleNo;
            }
        }
        return todayDate+"-0001";
    }

    /*...............Installment no for order line .................*/
    public int getPaymentInstallmentNumber(int saleOrderLine)
    {
        String hql = "select entry_no as entryNo, installment_no as 'installmentNo' " +
                     "from bl_order_pay_installment " +
                     "where soql_no = '"+saleOrderLine+"' order by entry_no desc";
        List installments = createSQLQueryAndTransform(hql, OrderPayInstallmentRaw.class).list();
        if(installments.size()>0)
        {
            Iterator iterator = installments.iterator();
            return ((OrderPayInstallmentRaw) iterator.next()).getInstallmentNo()+1;
        }
        return 1;
    }

/**
  Service types & related data
  @Author: Eric Mwailunga
  October,2019
*/

    public String createServiceType(String name, int creator)
    {
        String hql="insert into bl_service_type (name, creator, date_created, retired, uuid) " +
                "values ('"+name+"','"+creator+"',now(),0,uuid())";
        int rowsAffected=createSQLQuery(hql).executeUpdate();
        if(rowsAffected==1)
        {
            return "success";
        }
        return "failed";
    }

    public ServiceTypeRaw fetchServiceTypeByName(String name)
    {
        String hql="select " +
                "service_type_id as 'serviceTypeId', " +
                "name as 'name', " +
                "creator as 'creator', " +
                "date_created as 'dateCreated', " +
                "changed_by as 'changedBy', " +
                "date_changed as 'dateChanged', " +
                "retired as 'retired', " +
                "uuid as 'uuid' " +
                "from bl_service_type where name='"+name+"'";
        List serviceTypeRaws=createSQLQueryAndTransform(hql, ServiceTypeRaw.class).list();
        if(serviceTypeRaws.size()>0) {
            Iterator iterator=serviceTypeRaws.iterator();
            return (ServiceTypeRaw) iterator.next();
        }
        return null;
    }

    public List<ServiceTypeRaw> fetchServiceTypeBySubName(String subName, boolean includeRetired)
    {
        String hql="select " +
                "service_type_id as 'serviceTypeId', " +
                "name as 'name', " +
                "creator as 'creator', " +
                "date_created as 'dateCreated', " +
                "changed_by as 'changedBy', " +
                "date_changed as 'dateChanged', " +
                "retired as 'retired', " +
                "uuid as 'uuid' " +
                "from bl_service_type where name like '%"+subName+"%' ";
        if(!includeRetired)
        {
            hql+=" and retired='0'";
        }
        List serviceTypeRaws=createSQLQueryAndTransform(hql, ServiceTypeRaw.class).list();
        List<ServiceTypeRaw> serviceTypeRawList=new ArrayList<>();
        if(serviceTypeRaws.size()>0)
        {
            for(Object serviceTypeRaw : serviceTypeRaws)
            {
                serviceTypeRawList.add((ServiceTypeRaw) serviceTypeRaw);
            }
        }
        return serviceTypeRawList;
    }

    public ServiceTypeRaw fetchServiceTypeByUuid(String serviceTypeUuid)
    {
        String hql="select " +
                "service_type_id as 'serviceTypeId', " +
                "name as 'name', " +
                "creator as 'creator', " +
                "date_created as 'dateCreated', " +
                "changed_by as 'changedBy', " +
                "date_changed as 'dateChanged', " +
                "retired as 'retired', " +
                "uuid as 'uuid' " +
                "from bl_service_type where uuid='"+serviceTypeUuid+"'";
        List serviceTypeRaws=createSQLQueryAndTransform(hql, ServiceTypeRaw.class).list();
        if(serviceTypeRaws.size()>0) {
            Iterator iterator=serviceTypeRaws.iterator();
            return (ServiceTypeRaw) iterator.next();
        }
        return null;
    }

    public ServiceTypeRaw fetchServiceTypeById(int serviceTypeId)
    {
        String hql="select " +
                "service_type_id as 'serviceTypeId', " +
                "name as 'name', " +
                "creator as 'creator', " +
                "date_created as 'dateCreated', " +
                "changed_by as 'changedBy', " +
                "date_changed as 'dateChanged', " +
                "retired as 'retired', " +
                "uuid as 'uuid' " +
                "from bl_service_type where service_type_id='"+serviceTypeId+"'";
        List serviceTypeRaws=createSQLQueryAndTransform(hql, ServiceTypeRaw.class).list();
        if(serviceTypeRaws.size()>0) {
            Iterator iterator=serviceTypeRaws.iterator();
            return (ServiceTypeRaw) iterator.next();
        }
        return null;
    }

    public List<ServiceTypeRaw> fetchAllServiceTypes(boolean includeRetired)
    {
        DbSession session = sessionFactory.getCurrentSession();
        String hql="select " +
                "service_type_id as 'serviceTypeId', " +
                "name as 'name', " +
                "creator as 'creator', " +
                "date_created as 'dateCreated', " +
                "changed_by as 'changedBy', " +
                "date_changed as 'dateChanged', " +
                "retired as 'retired', " +
                "uuid as 'uuid' " +
                "from bl_service_type ";
        if(!includeRetired)
        {
            hql+=" where retired='0'";
        }

        List serviceTypeRaws = session.createSQLQuery(hql).setResultTransformer(Transformers.aliasToBean(ServiceTypeRaw.class)).list();
        List<ServiceTypeRaw> serviceTypeRawList=new ArrayList<>();
        if(serviceTypeRaws.size()>0)
        {
            for(Object serviceTypeRaw : serviceTypeRaws)
            {
                serviceTypeRawList.add((ServiceTypeRaw) serviceTypeRaw);
            }
        }
        return serviceTypeRawList;
    }


    public String updateServiceTypeByUuid(String serviceTypeUuid, String name, int changedBy)
    {
        String hql="update table bl_service_type " +
                "set name='"+name+"', changed_by='"+changedBy+"', date_changed=now() "+
                "where uuid='"+serviceTypeUuid+"'";
        int rowsAffected=createSQLQuery(hql).executeUpdate();
        if(rowsAffected>=1)
        {
            return "success";
        }
        return "failed";
    }


    public String retireServiceTypeByUuid(String serviceTypeUuid, int changedBy)
    {
        String hql="update table bl_service_type " +
                "set changed_by='"+changedBy+"', date_changed=now() "+
                "where uuid='"+serviceTypeUuid+"'";
        int rowsAffected=createSQLQuery(hql).executeUpdate();
        if(rowsAffected>=1)
        {
            return "success";
        }
        return "failed";
    }

/*
  Financial period & related data
  @Author: Eric Mwailunga
  October,2019
*/

    public String createFinancialPeriod(String name, String startDate, String endDate, int creator)
    {
        String hql="insert into bl_financial_period (name, start_date, end_date, creator, date_created, retired, uuid) " +
                "values ('"+name+"','"+startDate+"','"+endDate+"','"+creator+"',now(),0,uuid())";
        int rowsAffected=createSQLQuery(hql).executeUpdate();
        if(rowsAffected==1)
        {
            return "success";
        }
        return "failed";
    }

    public FinancialPeriodRaw fetchFinancialPeriodByName(String name)
    {
        String hql="select " +
                "period_id as 'periodId', " +
                "name as 'name', " +
                "start_date as 'startDate', " +
                "end_date as 'endDate', " +
                "creator as 'creator', " +
                "date_created as 'dateCreated', " +
                "changed_by as 'changedBy', " +
                "date_changed as 'dateChanged', " +
                "retired as 'retired', " +
                "uuid as 'uuid' " +
                "from bl_financial_period where name='"+name+"'";
        List financialPeriods=createSQLQueryAndTransform(hql, FinancialPeriodRaw.class).list();
        if(financialPeriods.size()>0) {
            Iterator iterator=financialPeriods.iterator();
           return (FinancialPeriodRaw) iterator.next();
        }
        return null;
    }

    public List<FinancialPeriodRaw> fetchFinancialPeriodBySubName(String subName)
    {
        String hql="select " +
                "period_id as 'periodId', " +
                "name as 'name', " +
                "start_date as 'startDate', " +
                "end_date as 'endDate', " +
                "creator as 'creator', " +
                "date_created as 'dateCreated', " +
                "changed_by as 'changedBy', " +
                "date_changed as 'dateChanged', " +
                "retired as 'retired', " +
                "uuid as 'uuid' " +
                "from bl_financial_period where name like '%"+subName+"%'";
        List financialPeriods=createSQLQueryAndTransform(hql,FinancialPeriodRaw.class).list();
        if(financialPeriods.size()>0) {
            List<FinancialPeriodRaw> financialPeriodList=new ArrayList<>();
            for(Object financialPeriod : financialPeriods)
            {
                financialPeriodList.add((FinancialPeriodRaw) financialPeriod);
            }
            return financialPeriodList;
        }
        return null;
    }

    public FinancialPeriodRaw fetchFinancialPeriodByUuid(String uuid)
    {
        String hql="select " +
                "period_id as 'periodId', " +
                "name as 'name', " +
                "start_date as 'startDate', " +
                "end_date as 'endDate', " +
                "creator as 'creator', " +
                "date_created as 'dateCreated', " +
                "changed_by as 'changedBy', " +
                "date_changed as 'dateChanged', " +
                "retired as 'retired', " +
                "uuid as 'uuid' " +
                "from bl_financial_period where uuid='"+uuid+"'";
        List financialPeriodRaw=createSQLQueryAndTransform(hql,FinancialPeriodRaw.class).list();
        if(financialPeriodRaw.size()>0) {
            Iterator iterator=financialPeriodRaw.iterator();
            return (FinancialPeriodRaw) iterator.next();
        }
        return null;
    }

    public FinancialPeriodRaw fetchFinancialPeriodById(int id)
    {
        String hql="select " +
                "period_id as 'periodId', " +
                "name as 'name', " +
                "start_date as 'startDate', " +
                "end_date as 'endDate', " +
                "creator as 'creator', " +
                "date_created as 'dateCreated', " +
                "changed_by as 'changedBy', " +
                "date_changed as 'dateChanged', " +
                "retired as 'retired', " +
                "uuid as 'uuid' " +
                "from bl_financial_period where period_id='"+id+"'";
        List financialPeriods=createSQLQueryAndTransform(hql,FinancialPeriodRaw.class).list();
        if(financialPeriods.size()>0) {
            Iterator iterator=financialPeriods.iterator();
            return (FinancialPeriodRaw) iterator.next();
        }
        return null;
    }


    public List<FinancialPeriodRaw> fetchAllFinancialPeriods(int includeRetired)
    {
        String hql="select " +
                "period_id as 'periodId', " +
                "name as 'name', " +
                "start_date as 'startDate', " +
                "end_date as 'endDate', " +
                "creator as 'creator', " +
                "date_created as 'dateCreated', " +
                "changed_by as 'changedBy', " +
                "date_changed as 'dateChanged', " +
                "retired as 'retired', " +
                "uuid as 'uuid' " +
                "from bl_financial_period";

        if(includeRetired==0)
        {
            hql+=" where retired='"+includeRetired+"'";
        }

        List financialPeriods=createSQLQueryAndTransform(hql,FinancialPeriodRaw.class).list();
        List<FinancialPeriodRaw> financialPeriodList=new ArrayList<>();
        if(financialPeriods.size()>0) {
            for(Object financialPeriod : financialPeriods)
            {
                financialPeriodList.add((FinancialPeriodRaw) financialPeriod);
            }
        }
        return financialPeriodList;
    }

    public String updateFinancialPeriodByUuid(String financialPeriodUuid,String name,String startDate,String endDate,int changedBy)
    {
        String hql="update table bl_financial_period " +
                "set name='"+name+"', start_date='"+startDate+"', end_date='"+endDate+"', changed_by='"+changedBy+"', date_changed=now() "+
                "where uuid='"+financialPeriodUuid+"'";
        int rowsAffected=createSQLQuery(hql).executeUpdate();
        if(rowsAffected>=1)
        {
            return "success";
        }
        return "failed";
    }

    public String retireFinancialPeriodByUuid(String financialPeriodUuid,int changedBy)
    {
        String hql="update table bl_financial_period " +
                "set retired = 1, changed_by='"+changedBy+"', date_changed=now() where uuid='"+financialPeriodUuid+"'";
        int rowsAffected=createSQLQuery(hql).executeUpdate();
        if(rowsAffected>=1)
        {
            return "success";
        }
        return "failed";
    }

/**
  Price list, versions & related data
  @Author: Eric Mwailunga
  October,2019
*/

    public String createPriceListVersion(int financialPeriodId, String versionName, String dateApproved, int creator)
    {
        String hql="insert into bl_price_list_version (financial_period, version_name, active, date_approved, creator, date_created, uuid) " +
                "values ('"+financialPeriodId+"','"+versionName+"','0','"+dateApproved+"','"+creator+"',now(),uuid())";
        int rowsAffected=createSQLQuery(hql).executeUpdate();
        if(rowsAffected==1)
        {
            return "success";
        }
        return null;
    }

    public ListVersionRaw fetchPriceListVersionById(int listId)
    {
        String hql="select " +
                "list_id as 'listId', " +
                "financial_period as 'financialPeriodId', " +
                "version_name as 'versionName', " +
                "active as 'active', " +
                "date_approved as 'dateApproved', " +
                "creator as 'creator', " +
                "date_created as 'dateCreated', " +
                "changed_by as 'changedBy', " +
                "date_changed as 'dateChanged', " +
                "uuid as 'uuid' " +
                "from bl_price_list_version where list_id='"+listId+"'";
        List ListVersionRaws=createSQLQueryAndTransform(hql, ListVersionRaw.class).list();
        if(ListVersionRaws.size()>0) {
            Iterator iterator=ListVersionRaws.iterator();
            return (ListVersionRaw) iterator.next();
        }
        return null;
    }

    public ListVersionRaw fetchPriceListVersionByName(int financialPeriodId, String versionName)
    {
        String hql="select " +
                "list_id as 'listId', " +
                "financial_period as 'financialPeriodId', " +
                "version_name as 'versionName', " +
                "active as 'active', " +
                "date_approved as 'dateApproved', " +
                "creator as 'creator', " +
                "date_created as 'dateCreated', " +
                "changed_by as 'changedBy', " +
                "date_changed as 'dateChanged', " +
                "uuid as 'uuid' " +
                "from bl_price_list_version where financial_period = '"+financialPeriodId+"' and version_name='"+versionName+"'";
        List ListVersionRaws=createSQLQueryAndTransform(hql, ListVersionRaw.class).list();
        if(ListVersionRaws.size()>0) {
            Iterator iterator=ListVersionRaws.iterator();
            return (ListVersionRaw) iterator.next();
        }
        return null;
    }

    public List<ListVersionRaw> fetchPriceListVersionBySubName(int financialPeriodId, String versionSubName)
    {
        String hql="select " +
                "list_id as 'listId', " +
                "financial_period as 'financialPeriodId', " +
                "version_name as 'versionName', " +
                "active as 'active', " +
                "date_approved as 'dateApproved', " +
                "creator as 'creator', " +
                "date_created as 'dateCreated', " +
                "changed_by as 'changedBy', " +
                "date_changed as 'dateChanged', " +
                "uuid as 'uuid' " +
                "from bl_price_list_version where financial_period = '"+financialPeriodId+"' and version_name like '%"+versionSubName+"%'";
        List ListVersionRaws=createSQLQueryAndTransform(hql, ListVersionRaw.class).list();
        if(ListVersionRaws.size()>0) {
            List<ListVersionRaw> ListVersionRawList=new ArrayList<>();
            for(Object ListVersionRaw : ListVersionRaws)
            {
                ListVersionRawList.add((ListVersionRaw) ListVersionRaw);
            }
            return ListVersionRawList;
        }
        return null;
    }

    public ListVersionRaw fetchPriceListVersionByUuid(String priceListUuid)
    {
        String hql="select " +
                "list_id as 'listId', " +
                "financial_period as 'financialPeriodId', " +
                "version_name as 'versionName', " +
                "active as 'active', " +
                "date_approved as 'dateApproved', " +
                "creator as 'creator', " +
                "date_created as 'dateCreated', " +
                "changed_by as 'changedBy', " +
                "date_changed as 'dateChanged', " +
                "uuid as 'uuid' " +
                "from bl_price_list_version where uuid='"+priceListUuid+"'";
        List ListVersionRaws=createSQLQueryAndTransform(hql, ListVersionRaw.class).list();
        if(ListVersionRaws.size()>0) {
            Iterator iterator=ListVersionRaws.iterator();
            return (ListVersionRaw) iterator.next();
        }
        return null;
    }

    public List<ListVersionRaw> fetchPriceListsByFinancialPeriodId(int financialPeriodId)
    {
        String hql="select " +
                "list_id as 'listId', " +
                "financial_period as 'financialPeriodId', " +
                "version_name as 'versionName', " +
                "active as 'active', " +
                "date_approved as 'dateApproved', " +
                "creator as 'creator', " +
                "date_created as 'dateCreated', " +
                "changed_by as 'changedBy', " +
                "date_changed as 'dateChanged', " +
                "uuid as 'uuid' " +
                "from" +
                " bl_price_list_version " +
                "where financial_period='"+financialPeriodId+"'";
        List ListVersionRaws=createSQLQueryAndTransform(hql, ListVersionRaw.class).list();
        List<ListVersionRaw> ListVersionRawList=new ArrayList<>();
        if(ListVersionRaws.size()>0) {
            for(Object ListVersionRaw : ListVersionRaws)
            {
                ListVersionRawList.add((ListVersionRaw) ListVersionRaw);
            }
        }
        return ListVersionRawList;
    }

    public List<ListVersionRaw> fetchAllPriceLists(int includeRetired)
    {
        String hql="select " +
                "list_id as 'listId', " +
                "financial_period as 'financialPeriodId', " +
                "version_name as 'versionName', " +
                "active as 'active', " +
                "date_approved as 'dateApproved', " +
                "creator as 'creator', " +
                "date_created as 'dateCreated', " +
                "changed_by as 'changedBy', " +
                "date_changed as 'dateChanged', " +
                "uuid as 'uuid' " +
                "from bl_price_list_version";

        if(includeRetired==0)
        {
            hql+=" where retired='"+includeRetired+"'";
        }

        List ListVersionRaws=createSQLQueryAndTransform(hql, ListVersionRaw.class).list();
        List<ListVersionRaw> ListVersionRawList=new ArrayList<>();
        if(ListVersionRaws.size()>0) {
            for(Object ListVersionRaw : ListVersionRaws)
            {
                ListVersionRawList.add((ListVersionRaw) ListVersionRaw);
            }
        }
        return ListVersionRawList;
    }

    public List<ListVersionRaw> fetchActivePriceLists()
    {
        String hql="select " +
                "list_id as 'listId', " +
                "financial_period as 'financialPeriodId', " +
                "version_name as 'versionName', " +
                "active as 'active', " +
                "date_approved as 'dateApproved', " +
                "creator as 'creator', " +
                "date_created as 'dateCreated', " +
                "changed_by as 'changedBy', " +
                "date_changed as 'dateChanged', " +
                "uuid as 'uuid' " +
                "from bl_price_list_version" +
                " where retired='0'";

        List ListVersionRaws=createSQLQueryAndTransform(hql, ListVersionRaw.class).list();
        if(ListVersionRaws.size()>0) {
            List<ListVersionRaw> ListVersionRawList=new ArrayList<>();
            for(Object ListVersionRaw : ListVersionRaws)
            {
                ListVersionRawList.add((ListVersionRaw) ListVersionRaw);
            }
            return ListVersionRawList;
        }
        return null;
    }

    public String retirePriceListVersionByUuid(String priceListUuid, int changedBy)
    {
        String hql="update table bl_price_list_version " +
                "set retired = 1 , changed_by='"+changedBy+"', date_changed=now() where uuid='"+priceListUuid+"'";
        int rowsAffected=createSQLQuery(hql).executeUpdate();
        if(rowsAffected>=1)
        {
            return "success";
        }
        return "failed";
    }


/*
  Items price & related data
  @Author: Eric Mwailunga
  October,2019
*/
    public String createItemPrice(int itemId, int serviceTypeId, int priceListVersion, int paymentCategory,  int paymentSubCategory, double sellingPrice, int creator)
    {
        String hql="insert into bl_item_price (price_list_version, item, service_type, payment_category, payment_sub_category, selling_price, creator, date_created, retired, uuid) " +
                "values ('"+priceListVersion+"','"+itemId+"','"+serviceTypeId+"','"+paymentCategory+"','"+paymentSubCategory+"','"+sellingPrice+"','"+creator+"',now(),0,uuid())";
        int rowsAffected=createSQLQuery(hql).executeUpdate();
        if(rowsAffected==1)
        {
            return "success";
        }
        return null;
    }

    public ItemPriceRaw fetchItemPriceByItemPriceId(int itemPriceId)
    {
        String hql="select " +
                "item_price_id as 'itemPriceId', " +
                "price_list_version as 'priceListVersion', " +
                "item as 'item', " +
                "service_type as 'serviceType', " +
                "payment_category as 'paymentCategory', " +
                "selling_price as 'sellingPrice', " +
                "creator as 'creator', " +
                "date_created as 'dateCreated', " +
                "changed_by as 'changedBy', " +
                "date_changed as 'dateChanged', " +
                "retired as 'retired', " +
                "uuid as 'uuid' " +
                "from bl_item_price where item_price_id='"+itemPriceId+"'";
        List itemPriceRaws=createSQLQueryAndTransform(hql, ItemPriceRaw.class).list();
        if(itemPriceRaws.size()>0) {
            Iterator iterator=itemPriceRaws.iterator();
            return (ItemPriceRaw) iterator.next();
        }
        return null;
    }


    public ItemPriceRaw fetchItemPriceByItemAndPaymentCategory(int item,int serviceType,int paymentCategory,int priceListVersion)
    {
        String hql="select " +
                "item_price_id as 'itemPriceId', " +
                "price_list_version as 'priceListVersion', " +
                "item as 'item', " +
                "service_type as 'serviceType', " +
                "payment_category as 'paymentCategory', " +
                "selling_price as 'sellingPrice', " +
                "creator as 'creator', " +
                "date_created as 'dateCreated', " +
                "changed_by as 'changedBy', " +
                "date_changed as 'dateChanged', " +
                "retired as 'retired', " +
                "uuid as 'uuid' " +
                "from" +
                " bl_item_price " +
                "where" +
                " item='"+item+"' and" +
                " price_list_version='"+priceListVersion+"' and" +
                " service_type='"+serviceType+"' and" +
                " payment_category='"+paymentCategory+"' and " +
                " retired='0'";

        List itemPriceRaws=createSQLQueryAndTransform(hql, ItemPriceRaw.class).list();
        if(itemPriceRaws.size()>0) {
            Iterator iterator=itemPriceRaws.iterator();
            return (ItemPriceRaw) iterator.next();
        }
        return null;
    }


/**
 Quotation status codes & related data
 @Author: Eric Mwailunga
 October,2019
*/

    public String createQuoteStatusCode(String name , String quoteType, int creator)
    {
        String uuid = UUID.randomUUID().toString();
        String hql="insert into bl_quote_status_code (name, quote_type, creator, date_created, retired, uuid) " +
                "values ('"+name+"','"+quoteType+"','"+creator+"',now(),'0','"+uuid+"')";
        int rowsAffected=createSQLQuery(hql).executeUpdate();
        if(rowsAffected==1)
        {
            return "success";
        }
        return null;
    }

    public QuoteStatusRaw fetchQuoteStatusCodeById(int statusCodeId)
    {
        String hql="select " +
                "status_code_id as 'statusCodeId', " +
                "name as 'name', " +
                "quote_type as 'quoteType', " +
                "creator as 'creator', " +
                "date_created as 'dateCreated', " +
                "changed_by as 'changedBy', "+
                "date_changed as 'dateChanged', " +
                "retired as 'retired', " +
                "uuid as 'uuid' "+
                " from bl_quote_status_code " +
                "where status_code_id='"+statusCodeId+"' and retired = '0'";

        List quoteStatusRaws=createSQLQueryAndTransform(hql, QuoteStatusRaw.class).list();
        if(quoteStatusRaws.size()>0) {
            Iterator iterator=quoteStatusRaws.iterator();
            return (QuoteStatusRaw) iterator.next();
        }
        return null;
    }

    public QuoteStatusRaw fetchQuoteStatusCodeByUuid(String statusCodeUuid)
    {
        String hql="select " +
                "status_code_id as 'statusCodeId', " +
                "name as 'name', " +
                "quote_type as 'quoteType', " +
                "creator as 'creator', " +
                "date_created as 'dateCreated', " +
                "changed_by as 'changedBy', "+
                "date_changed as 'dateChanged', " +
                "retired as 'retired', " +
                "uuid as 'uuid' "+
                " from bl_quote_status_code " +
                "where uuid='"+statusCodeUuid+"' and retired = '0'";

        List quoteStatusRaws=createSQLQueryAndTransform(hql, QuoteStatusRaw.class).list();
        if(quoteStatusRaws.size()>0) {
            Iterator iterator=quoteStatusRaws.iterator();
            return (QuoteStatusRaw) iterator.next();
        }
        return null;
    }

    public List<QuoteStatusRaw> fetchAllQuoteStatusCodes()
    {
        String hql="select " +
                "status_code_id as 'statusCodeId', " +
                "name as 'name', " +
                "quote_type as 'quoteType', " +
                "creator as 'creator', " +
                "date_created as 'dateCreated', " +
                "changed_by as 'changedBy', "+
                "date_changed as 'dateChanged', " +
                "retired as 'retired', " +
                "uuid as 'uuid' "+
                " from bl_quote_status_code " +
                "where retired = '0'";

        List quoteStatusRaws=createSQLQueryAndTransform(hql, QuoteStatusRaw.class).list();
        List<QuoteStatusRaw> quoteStatusRawList = new ArrayList<>();
        if(quoteStatusRaws.size()>0) {
            for(Object quoteStatusRaw : quoteStatusRaws)
            {
                quoteStatusRawList.add((QuoteStatusRaw) quoteStatusRaw);
            }
            return quoteStatusRawList;
        }
        return null;
    }

/**
   Sale quote & related data processes
   @Author: Eric Mwailunga
   October,2019
*/

    public SaleQuoteRaw createSaleQuote(int patient, int visit , int paymentCategory, int paymentSubCategoryId, int orderedBy)
    {
        String uuid = UUID.randomUUID().toString();
        String hql="insert into bl_sale_quote (patient, visit, payment_category, payment_sub_category, ordered_by, status, discounted, date_ordered, uuid) " +
                "values ('"+patient+"','"+visit+"','"+paymentCategory+"','"+paymentSubCategoryId+"','"+orderedBy+"','1','0',now(),'"+uuid+"')";
        int rowsAffected=createSQLQuery(hql).executeUpdate();
        if(rowsAffected==1)
        {
            return fetchSaleQuoteByUuid(uuid);
        }
        return null;
    }

    public SaleQuoteRaw fetchSaleQuoteById(int quoteId)
    {
        String hql="select " +
                "quote_id as 'quoteId', " +
                "patient as 'patient', " +
                "payment_category as 'paymentCategory', " +
                "payment_sub_category as 'paymentSubCategory', " +
                "visit as 'visit', " +
                "ordered_by as 'orderedBy', " +
                "total_quote as 'totalQuote', " +
                "payable_amount as 'payableAmount', " +
                "discounted as 'discounted', " +
                "date_ordered as 'dateOrdered', " +
                "changed_by as 'changedBy', " +
                "date_changed as 'dateChanged', " +
                "uuid as 'uuid' " +
                "from bl_sale_quote where quote_id='"+quoteId+"'";

        List saleQuoteRaws=createSQLQueryAndTransform(hql, SaleQuoteRaw.class).list();
        if(saleQuoteRaws.size()>0) {
            Iterator iterator=saleQuoteRaws.iterator();
            return (SaleQuoteRaw) iterator.next();
        }
        return null;
    }

    public SaleQuoteRaw fetchSaleQuoteByUuid(String uuid)
    {
        String hql="select " +
                "quote_id as 'quoteId', " +
                "patient as 'patient', " +
                "payment_category as 'paymentCategory', " +
                "payment_sub_category as 'paymentSubCategory', " +
                "visit as 'visit', " +
                "ordered_by as 'orderedBy', " +
                "total_quote as 'totalQuote', " +
                "payable_amount as 'payableAmount', " +
                "discounted as 'discounted', " +
                "date_ordered as 'dateOrdered', " +
                "changed_by as 'changedBy', " +
                "date_changed as 'dateChanged', " +
                "uuid as 'uuid' " +
                "from bl_sale_quote where uuid='"+uuid+"'";

        List saleQuoteRaws=createSQLQueryAndTransform(hql, SaleQuoteRaw.class).list();
        if(saleQuoteRaws.size()>0) {
            Iterator iterator=saleQuoteRaws.iterator();
            return (SaleQuoteRaw) iterator.next();
        }
        return null;
    }

    public List<SaleQuoteRaw> fetchAllSaleQuotesByPatient(int patientId, boolean isAscending)
    {
        List<SaleQuoteRaw> saleQuoteRawList = new ArrayList<>();
        String sortBy = "Desc";
        if(isAscending)
        {
            sortBy="Asc";
        }

        String hql="select " +
                "quote_id as 'quoteId', " +
                "patient as 'patient', " +
                "payment_category as 'paymentCategory', " +
                "payment_sub_category as 'paymentSubCategory', " +
                "visit as 'visit', " +
                "ordered_by as 'orderedBy', " +
                "total_quote as 'totalQuote', " +
                "payable_amount as 'payableAmount', " +
                "discounted as 'discounted', " +
                "date_ordered as 'dateOrdered', " +
                "changed_by as 'changedBy', " +
                "date_changed as 'dateChanged', " +
                "uuid as 'uuid' " +
                "from bl_sale_quote where patient = '"+patientId+"' order by quote_id "+sortBy+"";

        List saleQuoteRaws=createSQLQueryAndTransform(hql, SaleQuoteRaw.class).list();
        if(saleQuoteRaws.size()>0)
        {
            for(Object saleQuoteRaw : saleQuoteRaws)
            {
                saleQuoteRawList.add((SaleQuoteRaw) saleQuoteRaw);
            }
            return saleQuoteRawList;
        }
        return null;
    }

    public List<SaleQuoteRaw> fetchAllSaleQuotes(int numberOfRecords, boolean isAscending)
    {
        List<SaleQuoteRaw> saleQuoteRawList = new ArrayList<>();
        String sortBy = "Desc";
        if(isAscending)
        {
            sortBy="Asc";
        }

        String hql="select " +
                "quote_id as 'quoteId', " +
                "patient as 'patient', " +
                "payment_category as 'paymentCategory', " +
                "payment_sub_category as 'paymentSubCategory', " +
                "visit as 'visit', " +
                "ordered_by as 'orderedBy', " +
                "total_quote as 'totalQuote', " +
                "payable_amount as 'payableAmount', " +
                "discounted as 'discounted', " +
                "date_ordered as 'dateOrdered', " +
                "changed_by as 'changedBy', " +
                "date_changed as 'dateChanged', " +
                "uuid as 'uuid' " +
                "from bl_sale_quote order by quote_id "+sortBy+" limit '"+numberOfRecords+"'";

        List saleQuoteRaws=createSQLQueryAndTransform(hql, SaleQuoteRaw.class).list();
        if(saleQuoteRaws.size()>0)
        {
            for(Object saleQuoteRaw : saleQuoteRaws)
            {
                saleQuoteRawList.add((SaleQuoteRaw) saleQuoteRaw);
            }
            return saleQuoteRawList;
        }
        return null;
    }


    public List<SaleQuoteRaw> fetchAllSaleQuotesByDates(String startDate, String endDate, boolean isAscending)
    {
        List<SaleQuoteRaw> saleQuoteRawList = new ArrayList<>();
        String sortBy = "Desc";
        if(isAscending)
        {
            sortBy="Asc";
        }

        String hql="select " +
                "quote_id as 'quoteId', " +
                "patient as 'patient', " +
                "payment_category as 'paymentCategory', " +
                "payment_sub_category as 'paymentSubCategory', " +
                "visit as 'visit', " +
                "ordered_by as 'orderedBy', " +
                "total_quote as 'totalQuote', " +
                "payable_amount as 'payableAmount', " +
                "discounted as 'discounted', " +
                "date_ordered as 'dateOrdered', " +
                "changed_by as 'changedBy', " +
                "date_changed as 'dateChanged', " +
                "uuid as 'uuid' " +
                "from bl_sale_quote where date_ordered between '"+startDate+"' and '"+endDate+"' order by quote_id "+sortBy+"";

        List saleQuoteRaws=createSQLQueryAndTransform(hql, SaleQuoteRaw.class).list();
        if(saleQuoteRaws.size()>0)
        {
            for(Object saleQuoteRaw : saleQuoteRaws)
            {
                saleQuoteRawList.add((SaleQuoteRaw) saleQuoteRaw);
            }
        }
        return saleQuoteRawList;
    }

/**
 Sale quote lines & related data processes
 @Author: Eric Mwailunga
 October,2019
 */
    public String createSaleQuoteLines(String valuesClause)
    {
        String hql="insert into bl_sale_quote_line (sale_quote, item, service_type, item_price, quantity, unit, quoted_amount, payable_amount, status, date_created, uuid) " +
                "values "+valuesClause;
        int rowsAffected=createSQLQuery(hql).executeUpdate();
        if(rowsAffected>=1)
        {
            return "success";
        }
        return "failed";
    }

    public SaleQuoteLineRaw fetchSaleQuoteLineById(int quoteLineId)
    {
        String hql="select " +
                "quote_line_id as 'quoteLineId', " +
                "sale_quote as 'saleQuote', " +
                "item as 'item', " +
                "service_type as 'serviceType', " +
                "item_price as 'itemPrice', " +
                "quantity as 'quantity', " +
                "unit as 'unit', " +
                "quoted_amount as 'quotedAmount', " +
                "payable_amount as 'payableAmount', " +
                "status as 'status', " +
                "date_created as 'dateCreated', " +
                "changed_by as 'changedBy', " +
                "date_changed as 'dateChanged', " +
                "uuid as 'uuid' " +
                "from bl_sale_quote_line where quote_line_id='"+quoteLineId+"'";

        List saleQuoteLineRaws=createSQLQueryAndTransform(hql, SaleQuoteLineRaw.class).list();
        if(saleQuoteLineRaws.size()>0) {
            Iterator iterator=saleQuoteLineRaws.iterator();
            return (SaleQuoteLineRaw) iterator.next();
        }
        return new SaleQuoteLineRaw();
    }

    public SaleQuoteLineRaw fetchSaleQuoteLineByUuid(String uuid)
    {
        String hql="select " +
                "quote_line_id as 'quoteLineId', " +
                "sale_quote as 'saleQuote', " +
                "item as 'item', " +
                "service_type as 'serviceType', " +
                "item_price as 'itemPrice', " +
                "quantity as 'quantity', " +
                "unit as 'unit', " +
                "quoted_amount as 'quotedAmount', " +
                "payable_amount as 'payableAmount', " +
                "status as 'status', " +
                "date_created as 'dateCreated', " +
                "changed_by as 'changedBy', " +
                "date_changed as 'dateChanged', " +
                "uuid as 'uuid' " +
                "from bl_sale_quote_line where uuid='"+uuid+"'";

        List saleQuoteLineRaws=createSQLQueryAndTransform(hql, SaleQuoteLineRaw.class).list();
        if(saleQuoteLineRaws.size()>0) {
            Iterator iterator=saleQuoteLineRaws.iterator();
            return (SaleQuoteLineRaw) iterator.next();
        }
        return new SaleQuoteLineRaw();
    }

    public List<SaleQuoteLineRaw> fetchSaleQuoteLinesByQuoteId(int saleQuoteId)
    {
        List<SaleQuoteLineRaw> saleQuoteLineRawsList = new ArrayList<>();
        String hql="select " +
                        "quote_line_id as 'quoteLineId', " +
                        "sale_quote as 'saleQuote', " +
                        "item as 'item', " +
                        "service_type as 'serviceType', " +
                        "item_price as 'itemPrice', " +
                        "quantity as 'quantity', " +
                        "unit as 'unit', " +
                        "quoted_amount as 'quotedAmount', " +
                        "payable_amount as 'payableAmount', " +
                        "status as 'status', " +
                        "date_created as 'dateCreated', " +
                        "changed_by as 'changedBy', " +
                        "date_changed as 'dateChanged', " +
                        "uuid as 'uuid' " +
                    "from bl_sale_quote_line where sale_quote='"+saleQuoteId+"'";

        List saleQuoteLineRaws=createSQLQueryAndTransform(hql, SaleQuoteLineRaw.class).list();
        if(saleQuoteLineRaws.size()>0)
        {
            for( Object saleQuoteLineRaw : saleQuoteLineRaws)
            {
                saleQuoteLineRawsList.add((SaleQuoteLineRaw) saleQuoteLineRaw);
            }
        }
        return saleQuoteLineRawsList;
    }


/**
   Discount criteria & related processes
   @Author: Eric Mwailunga
   October,2019
*/
    public String createDiscountCriteria(String name, String description, int creator)
    {
        if(checkDiscountCriteriaExistence(name).equals("exists"))
        {
            return "exists";
        }

        String hql="insert into " +
                "bl_discount_criteria (name, description, creator, date_created, uuid) " +
                "values ('"+name+"','"+description+"','"+creator+"',now(),uuid())";
        int rowsAffected=createSQLQuery(hql).executeUpdate();
        if(rowsAffected>=1)
        {
            return "success";
        }
        return "failed";
    }

    public DiscountCriteriaRaw fetchDiscountCriteriaById(int criteriaNo)
    {
        String hql="select " +
                        "criteria_no as 'criteriaNo', " +
                        "name as 'name', " +
                        "description as 'description', "+
                        "creator as 'creator', " +
                        "date_created as 'dateCreated', " +
                        "changed_by as 'changedBy', " +
                        "date_changed as 'dateChanged', " +
                        "retired as 'retired', " +
                        "uuid as 'uuid'" +
                    "from bl_discount_criteria " +
                    "where criteriaNo='"+criteriaNo+"'";

        List discountCriteriaRaws=createSQLQueryAndTransform(hql, DiscountCriteriaRaw.class).list();
        if(discountCriteriaRaws.size()>0)
        {
           Iterator iterator = discountCriteriaRaws.iterator();
            return (DiscountCriteriaRaw) iterator.next();
        }
        return new DiscountCriteriaRaw();
    }

    public DiscountCriteriaRaw fetchDiscountCriteriaByUuid(String uuid)
    {
        String hql="select " +
                        "criteria_no as 'criteriaNo', " +
                        "name as 'name', " +
                        "description as 'description', "+
                        "creator as 'creator', " +
                        "date_created as 'dateCreated', " +
                        "changed_by as 'changedBy', " +
                        "date_changed as 'dateChanged', " +
                        "retired as 'retired', " +
                        "uuid as 'uuid'" +
                    "from bl_discount_criteria " +
                    "where uuid='"+uuid+"'";

        List discountCriteriaRaws=createSQLQueryAndTransform(hql, DiscountCriteriaRaw.class).list();
        if(discountCriteriaRaws.size()>0)
        {
            Iterator iterator = discountCriteriaRaws.iterator();
            return (DiscountCriteriaRaw) iterator.next();
        }
        return new DiscountCriteriaRaw();
    }

    public List<DiscountCriteriaRaw> fetchAllDiscountCriteria(boolean includeRetired)
    {
        List<DiscountCriteriaRaw> discountCriteriaRawList = new ArrayList<>();
        String hql="select " +
                "criteria_no as 'criteriaNo', " +
                "name as 'name', " +
                "description as 'description', "+
                "creator as 'creator', " +
                "date_created as 'dateCreated', " +
                "changed_by as 'changedBy', " +
                "date_changed as 'dateChanged', " +
                "retired as 'retired', " +
                "uuid as 'uuid'" +
                "from bl_discount_criteria";

        if(!includeRetired)
        {
            hql+=" where retired='0'";
        }

        List discountCriteriaRaws=createSQLQueryAndTransform(hql, DiscountCriteriaRaw.class).list();
        if(discountCriteriaRaws.size()>0)
        {
            for(Object discountCriteriaRaw : discountCriteriaRaws)
            {
                discountCriteriaRawList.add((DiscountCriteriaRaw) discountCriteriaRaw);
            }
        }
        return discountCriteriaRawList;
    }

    public String updateDiscountCriteria(String uuid, String name, String description, int user)
    {

        return null;
    }

    public String retireDiscountCriteria(String uuid)
    {

        return null;
    }

/**
 Discount & related data processes
 @Author: Eric Mwailunga
 October,2019
*/
    public String createDiscount(int quoteLine, double originalQuotedAmount, double discountedAmount, int criteria, int initiator, String uuid)
    {
        String hql= " insert into bl_discount (quote_line, original_quoted_amount, proposed_discount_amount, discount_criteria, initiated_by, date_created, uuid)" +
                " values('"+quoteLine+"', '"+originalQuotedAmount+"', '"+discountedAmount+"', '"+criteria+"', '"+initiator+"', now(), '"+uuid+"')";

        int rowsAffected=createSQLQuery(hql).executeUpdate();
        if(rowsAffected>=1)
        {
            if(markSaleQuoteAsDiscounted(this.fetchSaleQuoteLineById(quoteLine)).equals("success"))
            {
                return "success";
            }
        }
        return null;
    }

    public String approveDiscountByUuid(String discountUuid, int criteria, double approvedAmount, int approvedBy)
    {
        String hql= " update table bl_discount set approved_discount_amount = '"+approvedAmount+"', discount_criteria = '"+criteria+"', approved = '1', approved_by = '"+approvedBy+"', date_approved = now()" +
                " where uuid = '"+discountUuid+"'";

        int rowsAffected=createSQLQuery(hql).executeUpdate();
        if(rowsAffected>=1)
        {
            return "success";
        }
        return null;
    }

    public DiscountRaw fetchDiscountByQuoteLineId(int quoteLineId)
    {
        String hql="select " +
                "discount_id as 'discountId', " +
                "quote_line as 'quoteLine', " +
                "original_quoted_amount as 'originalQuotedAmount', "+
                "proposed_discount_amount as 'proposedDiscountAmount', " +
                "approved_discount_amount as 'approvedDiscountAmount', " +
                "discount_criteria as 'discountCriteria', " +
                "date_created as 'dateCreated', " +
                "initiated_by as 'initiatedBy', " +
                "approved as 'approved', " +
                "approved_by as 'approvedBy', " +
                "date_approved as 'dateApproved', " +
                "uuid as 'uuid' " +
                "from bl_discount " +
                "where quote_line='"+quoteLineId+"'";

        List discountRaws=createSQLQueryAndTransform(hql, DiscountRaw.class).list();
        if(discountRaws.size()>0)
        {
            Iterator iterator = discountRaws.iterator();
            return (DiscountRaw) iterator.next();
        }

        return null;
    }

    public DiscountRaw fetchDiscountById(int discountId)
    {
        String hql="select " +
                "discount_id as 'discountId', " +
                "quote_line as 'quoteLine', " +
                "original_quoted_amount as 'originalQuotedAmount', "+
                "proposed_discount_amount as 'proposedDiscountAmount', " +
                "approved_discount_amount as 'approvedDiscountAmount', " +
                "discount_criteria as 'discountCriteria', " +
                "date_created as 'dateCreated', " +
                "initiated_by as 'initiatedBy', " +
                "approved as 'approved', " +
                "approved_by as 'approvedBy', " +
                "date_approved as 'dateApproved', " +
                "uuid as 'uuid' " +
                "from bl_discount " +
                "where discountId='"+discountId+"'";

        List discountRaws=createSQLQueryAndTransform(hql, DiscountRaw.class).list();
        if(discountRaws.size()>0)
        {
            Iterator iterator = discountRaws.iterator();
            return (DiscountRaw) iterator.next();
        }

        return null;
    }

    public DiscountRaw fetchDiscountByUuid(String uuid)
    {
        String hql="select " +
                "discount_id as 'discountId', " +
                "quote_line as 'quoteLine', " +
                "original_quoted_amount as 'originalQuotedAmount', "+
                "proposed_discount_amount as 'proposedDiscountAmount', " +
                "approved_discount_amount as 'approvedDiscountAmount', " +
                "discount_criteria as 'discountCriteria', " +
                "date_created as 'dateCreated', " +
                "initiated_by as 'initiatedBy', " +
                "approved as 'approved', " +
                "approved_by as 'approvedBy', " +
                "date_approved as 'dateApproved', " +
                "uuid as 'uuid' " +
                "from bl_discount " +
                "where uuid='"+uuid+"'";

        List discountRaws=createSQLQueryAndTransform(hql, DiscountRaw.class).list();
        if(discountRaws.size()>0)
        {
            Iterator iterator = discountRaws.iterator();
            return (DiscountRaw) iterator.next();
        }

        return null;
    }

    public List<DiscountRaw> fetchDiscountsByInitiator(int initiator, String startDate, String endDate, boolean isAscending)
    {
        String sortBy = "Desc";
        if(isAscending)
        {
            sortBy="Asc";
        }

        String hql="select " +
            "discount_id as 'discountId', " +
            "quote_line as 'quoteLine', " +
            "original_quoted_amount as 'originalQuotedAmount', "+
            "proposed_discount_amount as 'proposedDiscountAmount', " +
            "approved_discount_amount as 'approvedDiscountAmount', " +
            "discount_criteria as 'discountCriteria', " +
            "date_created as 'dateCreated', " +
            "initiated_by as 'initiatedBy', " +
            "approved as 'approved', " +
            "approved_by as 'approvedBy', " +
            "date_approved as 'dateApproved', " +
            "uuid as 'uuid' " +
            "from bl_discount " +
            "where initiated_by='"+initiator+"' and date_created between '"+startDate+"' and '"+endDate+"' order by discount_id "+sortBy+"";

        List discountRaws=createSQLQueryAndTransform(hql, DiscountRaw.class).list();
        List<DiscountRaw> discountRawList = new ArrayList<>();
        if(discountRaws.size()>0)
        {
            for(Object discountRaw : discountRaws)
            {
                discountRawList.add((DiscountRaw)discountRaw);
            }
            return discountRawList;
        }
        return null;
    }

    public List<DiscountRaw> fetchDiscountsByApprovedBy(int approvedBy, String startDate, String endDate, boolean isAscending)
    {
        String sortBy = "Desc";
        if(isAscending)
        {
            sortBy="Asc";
        }

        String hql="select " +
                "discount_id as 'discountId', " +
                "quote_line as 'quoteLine', " +
                "original_quoted_amount as 'originalQuotedAmount', "+
                "proposed_discount_amount as 'proposedDiscountAmount', " +
                "approved_discount_amount as 'approvedDiscountAmount', " +
                "discount_criteria as 'discountCriteria', " +
                "date_created as 'dateCreated', " +
                "initiated_by as 'initiatedBy', " +
                "approved as 'approved', " +
                "approved_by as 'approvedBy', " +
                "date_approved as 'dateApproved', " +
                "uuid as 'uuid' " +
                "from bl_discount " +
                "where approved_by='"+approvedBy+"' and date_created between '"+startDate+"' and '"+endDate+"' order by discount_id "+sortBy+"";

        List discountRaws=createSQLQueryAndTransform(hql, DiscountRaw.class).list();
        List<DiscountRaw> discountRawList = new ArrayList<>();
        if(discountRaws.size()>0)
        {
            for(Object discountRaw : discountRaws)
            {
                discountRawList.add((DiscountRaw)discountRaw);
            }
            return discountRawList;
        }
        return null;
    }

    public List<DiscountRaw> fetchAllDiscountsByDates(String startDate, String endDate, boolean isAscending)
    {
        String sortBy = "Desc";
        if(isAscending)
        {
            sortBy="Asc";
        }

        String hql="select " +
                "discount_id as 'discountId', " +
                "quote_line as 'quoteLine', " +
                "original_quoted_amount as 'originalQuotedAmount', "+
                "proposed_discount_amount as 'proposedDiscountAmount', " +
                "approved_discount_amount as 'approvedDiscountAmount', " +
                "discount_criteria as 'discountCriteria', " +
                "date_created as 'dateCreated', " +
                "initiated_by as 'initiatedBy', " +
                "approved as 'approved', " +
                "approved_by as 'approvedBy', " +
                "date_approved as 'dateApproved', " +
                "uuid as 'uuid' " +
                "from bl_discount " +
                "where date_created between '"+startDate+"' and '"+endDate+"' order by discount_id "+sortBy+"";

        List discountRaws=createSQLQueryAndTransform(hql, DiscountRaw.class).list();
        List<DiscountRaw> discountRawList = new ArrayList<>();
        if(discountRaws.size()>0)
        {
            for(Object discountRaw : discountRaws)
            {
                discountRawList.add((DiscountRaw)discountRaw);
            }
            return discountRawList;
        }
        return null;
    }

    public List<DiscountRaw> fetchAllDiscountsByStatus(String status, boolean isAscending)
    {
        String sortBy = "Desc";
        if(isAscending)
        {
            sortBy="Asc";
        }

        String hql="select " +
                "discount_id as 'discountId', " +
                "quote_line as 'quoteLine', " +
                "original_quoted_amount as 'originalQuotedAmount', "+
                "proposed_discount_amount as 'proposedDiscountAmount', " +
                "approved_discount_amount as 'approvedDiscountAmount', " +
                "discount_criteria as 'discountCriteria', " +
                "date_created as 'dateCreated', " +
                "initiated_by as 'initiatedBy', " +
                "approved as 'approved', " +
                "approved_by as 'approvedBy', " +
                "date_approved as 'dateApproved', " +
                "uuid as 'uuid' " +
                "from bl_discount ";

        if(status.equals("initiated"))
        {
            hql += "where approved = '0' order by discount_id = "+sortBy+"";
        }
        else if(status.equals("approved")){
            hql += "where approved = '1' order by discount_id = "+sortBy+"";
        }

        List discountRaws=createSQLQueryAndTransform(hql, DiscountRaw.class).list();
        List<DiscountRaw> discountRawList = new ArrayList<>();
        if(discountRaws.size()>0)
        {
            for(Object discountRaw : discountRaws)
            {
                discountRawList.add((DiscountRaw)discountRaw);
            }
            return discountRawList;
        }
        return null;
    }

    public String updateDiscountInitiation(String discountUuid, double discountedAmount, int criteria, int initiator )
    {
        String hql= " update table bl_discount set initiated_by = '"+initiator+"', proposed_discount_amount = '"+discountedAmount+"', discount_criteria = '"+criteria+"', date_created = now()" +
                " where uuid = '"+discountUuid+"'";

        int rowsAffected=createSQLQuery(hql).executeUpdate();
        if(rowsAffected>=1)
        {
            return "success";
        }
        return null;
    }


/**
 Sale Order & related processes
 @Author: Eric Mwailunga
 November,2019
*/

    public String createSaleOrder(int saleQuote, String datedSaleId, int creator, String uuid)
    {
        String hql="insert into bl_sale_order_by_quote " +
                    "(dated_sale_id, sale_quote, creator, date_created, uuid) " +
                    "values ('"+datedSaleId+"','"+creator+"','now()','"+uuid+"')";
        Query query = createSQLQuery(hql);
        if(query.executeUpdate()>0)
        {
            return "success";
        }
        return null;
    }

    public OrderByQuoteRaw fetchSaleOrderById(int quoteId)
    {
        String hql="select " +
                "soq_no as 'soqNo', " +
                "dated_sale_id as 'datedSaleId', " +
                "sale_quote as 'saleQuote', "+
                "payment_method as 'paymentMethod', " +
                "payable_amount as 'payableAmount', " +
                "paid_amount as 'paidAmount', " +
                "debt_amount as 'debtAmount', " +
                "installment_frequency as 'installmentFrequency', " +
                "date_created as 'dateCreated', " +
                "uuid as 'uuid' " +
                "from bl_sale_order_by_quote " +
                "where soq_no = '"+quoteId+"'";

        List orderByQuoteRaws=createSQLQueryAndTransform(hql, OrderByQuoteRaw.class).list();
        if(orderByQuoteRaws.size()>0)
        {
            Iterator iterator = orderByQuoteRaws.iterator();
            return (OrderByQuoteRaw) iterator.next();
        }
        return null;
    }


    public OrderByQuoteRaw fetchSaleOrderByUuid(String quoteUuid)
    {
        String hql="select " +
                "soq_no as 'soqNo', " +
                "dated_sale_id as 'datedSaleId', " +
                "sale_quote as 'saleQuote', "+
                "payment_method as 'paymentMethod', " +
                "payable_amount as 'payableAmount', " +
                "paid_amount as 'paidAmount', " +
                "debt_amount as 'debtAmount', " +
                "installment_frequency as 'installmentFrequency', " +
                "date_created as 'dateCreated', " +
                "uuid as 'uuid' " +
                "from bl_sale_order_by_quote " +
                "where uuid = '"+quoteUuid+"'";

        List orderByQuoteRaws=createSQLQueryAndTransform(hql, OrderByQuoteRaw.class).list();
        if(orderByQuoteRaws.size()>0)
        {
            Iterator iterator = orderByQuoteRaws.iterator();
            return (OrderByQuoteRaw) iterator.next();
        }
        return null;
    }

    public List<OrderByQuoteRaw> fetchAllSaleOrderByPatient(int patient, boolean isAscending)
    {

        return null;
    }

    public List<OrderByQuoteRaw> fetchAllSaleOrderByDates(String startDate, String endDate, boolean isAscending)
    {
        String sortBy = "Desc";
        if(isAscending)
        {
            sortBy="Asc";
        }
        String hql="select " +
                "soq_no as 'soqNo', " +
                "dated_sale_id as 'datedSaleId', " +
                "sale_quote as 'saleQuote', "+
                "payment_method as 'paymentMethod', " +
                "payable_amount as 'payableAmount', " +
                "paid_amount as 'paidAmount', " +
                "debt_amount as 'debtAmount', " +
                "installment_frequency as 'installmentFrequency', " +
                "date_created as 'dateCreated', " +
                "uuid as 'uuid' " +
                "from bl_sale_order_by_quote " +
                "where date_created between '"+startDate+"' and  '"+endDate+"' order by soq_no "+sortBy;

        List orderByQuoteRaws=createSQLQueryAndTransform(hql, OrderByQuoteRaw.class).list();
        List<OrderByQuoteRaw> orderByQuoteRawList = new ArrayList<>();
        if(orderByQuoteRaws.size()>0)
        {
            for(Object orderByQuote : orderByQuoteRaws)
            {
                orderByQuoteRawList.add((OrderByQuoteRaw) orderByQuote);
            }
        }
        return orderByQuoteRawList;
    }

    public List<OrderByQuoteRaw> fetchAllSaleOrders(int startAt, int offset, boolean isAscending)
    {
        
        return null;
    }
/**
 Sale Order line & related processes
 @Author: Eric Mwailunga
 November,2019
*/

    public String createSaleOrderLines(String valuesClause)
    {
        String hql="insert into bl_sale_order_by_quote_line" +
                "(sale_order_quote, quote_line, date_created, uuid) values " + valuesClause;

        Query query = createSQLQuery(hql);
        if(query.executeUpdate()>=1)
        {
            return "success";
        }
        return "failed";
    }

    public OrderByQuoteLineRaw fetchSaleOrderLineBySOQLNo(int soqlNo)
    {
        String hql = "select soql_no as 'orderByQuoteLineId', " +
                            "sale_order_quote as 'saleOrderQuote', " +
                            "quote_line as 'quoteLine', " +
                            "paid_amount as 'paidAmount', " +
                            "debt_amount as 'debtAmount', " +
                            "date_created as dateCreated, " +
                            "uuid as 'uuid'" +
                      " from bl_sale_order_by_quote_line" +
                      " where soql_no = '"+soqlNo+"'";
        List saleOrderLines = createSQLQueryAndTransform(hql,OrderByQuoteLineRaw.class).list();
        if(saleOrderLines.size()>0)
        {
            Iterator iterator = saleOrderLines.iterator();
            return (OrderByQuoteLineRaw) iterator.next();
        }
        return null;
    }

    public OrderByQuoteLineRaw fetchSaleOrderLineByUuid(String uuid)
    {
        String hql = "select soql_no as 'orderByQuoteLineId', " +
                "sale_order_quote as 'saleOrderQuote', " +
                "quote_line as 'quoteLine', " +
                "paid_amount as 'paidAmount', " +
                "debt_amount as 'debtAmount', " +
                "date_created as dateCreated, " +
                "uuid as 'uuid'" +
                " from bl_sale_order_by_quote_line" +
                " where uuid = '"+uuid+"'";
        List saleOrderLines = createSQLQueryAndTransform(hql,OrderByQuoteLineRaw.class).list();
        if(saleOrderLines.size()>0)
        {
            Iterator iterator = saleOrderLines.iterator();
            return (OrderByQuoteLineRaw) iterator.next();
        }
        return null;
    }

    public OrderByQuoteLineRaw fetchSaleOrderLineByQuoteLine(int quoteLine)
    {
        String hql = "select soql_no as 'orderByQuoteLineId', " +
                "sale_order_quote as 'saleOrderQuote', " +
                "quote_line as 'quoteLine', " +
                "paid_amount as 'paidAmount', " +
                "debt_amount as 'debtAmount', " +
                "date_created as dateCreated, " +
                "uuid as 'uuid'" +
                " from bl_sale_order_by_quote_line" +
                " where quote_line = '"+quoteLine+"'";
        List saleOrderLines = createSQLQueryAndTransform(hql,OrderByQuoteLineRaw.class).list();
        if(saleOrderLines.size()>0)
        {
            Iterator iterator = saleOrderLines.iterator();
            return (OrderByQuoteLineRaw) iterator.next();
        }
        return null;
    }

    public List<OrderByQuoteLineRaw> fetchSaleOrderLineBySaleOrderNo(int saleOrderNo)
    {
        List<OrderByQuoteLineRaw> saleOrderLineRaws = new ArrayList<>();
        String hql = "select soql_no as 'orderByQuoteLineId', " +
                "sale_order_quote as 'saleOrderQuote', " +
                "quote_line as 'quoteLine', " +
                "paid_amount as 'paidAmount', " +
                "debt_amount as 'debtAmount', " +
                "date_created as dateCreated, " +
                "uuid as 'uuid'" +
                " from bl_sale_order_by_quote_line" +
                " where sale_order_quote = '"+saleOrderNo+"'";
        List saleOrderLines = createSQLQueryAndTransform(hql,OrderByQuoteLineRaw.class).list();
        if(saleOrderLines.size()>0)
        {
            for(Object object : saleOrderLines)
            {
                saleOrderLineRaws.add((OrderByQuoteLineRaw)object);
            }
        }
        return saleOrderLineRaws;
    }


/**
 Payment installments & related data processes
 @Author: Eric Mwailunga
 November,2019
*/
    public String createPaymentInstallment(int saleOrderLine, double paidAmount, String receiptNo, int receivedBy)
    {
        int installmentNo = this.getPaymentInstallmentNumber(saleOrderLine);
        String hql = "insert into " +
                     "bl_order_pay_installment(installment_no, soql_no, paid_amount, receipt, received_by, date_created, uuid) " +
                     "values('"+installmentNo+"','"+saleOrderLine+"','"+paidAmount+"','"+receiptNo+"','"+receivedBy+"',now(),uuid())";
        Query query = createSQLQuery(hql);
        if(query.executeUpdate()>=1)
        {
            return "success";
        }
        return null;
    }

    public OrderPayInstallmentRaw fetchInstallmentById(int entryNo)
    {
        String hql = "select entry_no as 'entryNo', " +
                             "installment_no as 'installmentNo', " +
                             "soql_no as 'soqlNo', " +
                             "paid_amount as 'paidAmount', " +
                             "receipt as 'receipt', " +
                             "received_by as 'receivedBy', " +
                             "date_created as 'dateCreated', " +
                             "uuid as 'uuid' " +
                     "from bl_order_pay_installment " +
                     "where entry_no ='"+entryNo+"'";
        List installmentRaws = createSQLQueryAndTransform(hql, OrderPayInstallmentRaw.class).list();
        if(installmentRaws.size()>0)
        {
            Iterator iterator = installmentRaws.iterator();
            return (OrderPayInstallmentRaw) iterator.next();
        }
        return null;
    }

    public OrderPayInstallmentRaw fetchInstallmentByUuid(String uuid)
    {
        String hql = "select entry_no as 'entryNo', " +
                        "installment_no as 'installmentNo', " +
                        "soql_no as 'soqlNo', " +
                        "paid_amount as 'paidAmount', " +
                        "receipt as 'receipt', " +
                        "received_by as 'receivedBy', " +
                        "date_created as 'dateCreated', " +
                        "uuid as 'uuid' " +
                    "from bl_order_pay_installment " +
                    "where uuid ='"+uuid+"'";
        List installmentRaws = createSQLQueryAndTransform(hql, OrderPayInstallmentRaw.class).list();
        if(installmentRaws.size()>0)
        {
            Iterator iterator = installmentRaws.iterator();
            return (OrderPayInstallmentRaw) iterator.next();
        }
        return null;
    }

    public List<OrderPayInstallmentRaw> fetchInstallmentByOrderLine(int orderLineNo)
    {
        String hql = "select entry_no as 'entryNo', " +
                        "installment_no as 'installmentNo', " +
                        "soql_no as 'soqlNo', " +
                        "paid_amount as 'paidAmount', " +
                        "receipt as 'receipt', " +
                        "received_by as 'receivedBy', " +
                        "date_created as 'dateCreated', " +
                        "uuid as 'uuid' " +
                    "from bl_order_pay_installment " +
                    "where soql_no ='"+orderLineNo+"'";
        List installmentRaws = createSQLQueryAndTransform(hql, OrderPayInstallmentRaw.class).list();
        List<OrderPayInstallmentRaw> installmentRawList = new ArrayList<>();
        if(installmentRaws.size()>0)
        {
            for(Object object : installmentRaws)
            {
                installmentRawList.add((OrderPayInstallmentRaw) object);
            }
        }
        return installmentRawList;
    }

    public List<OrderPayInstallmentRaw> fetchInstallmentByDates(String startDate, String endDate, boolean isAscending)
    {
        String sortBy = "Desc";
        if(isAscending)
        {
            sortBy="Asc";
        }

        String hql = "select entry_no as 'entryNo', " +
                        "installment_no as 'installmentNo', " +
                        "soql_no as 'soqlNo', " +
                        "paid_amount as 'paidAmount', " +
                        "receipt as 'receipt', " +
                        "received_by as 'receivedBy', " +
                        "date_created as 'dateCreated', " +
                        "uuid as 'uuid' " +
                      "from bl_order_pay_installment " +
                      "where date_created between '"+startDate+"' and '"+endDate+"' order by entry_no "+sortBy;

        List installmentRaws = createSQLQueryAndTransform(hql, OrderPayInstallmentRaw.class).list();
        List<OrderPayInstallmentRaw> installmentRawList = new ArrayList<>();
        if(installmentRaws.size()>0)
        {
            for(Object object : installmentRaws)
            {
                installmentRawList.add((OrderPayInstallmentRaw) object);
            }
        }
        return installmentRawList;
    }

    public List<OrderPayInstallmentRaw> fetchAllInstallments(int numberOfRecords, boolean isAscending)
    {
        String sortBy = "Desc";
        if(isAscending)
        {
            sortBy="Asc";
        }

        String hql = "select entry_no as 'entryNo', " +
                        "installment_no as 'installmentNo', " +
                        "soql_no as 'soqlNo', " +
                        "paid_amount as 'paidAmount', " +
                        "receipt as 'receipt', " +
                        "received_by as 'receivedBy', " +
                        "date_created as 'dateCreated', " +
                        "uuid as 'uuid' " +
                      "from bl_order_pay_installment " +
                      "order by entry_no "+sortBy+" limit "+numberOfRecords;

        List installmentRaws = createSQLQueryAndTransform(hql, OrderPayInstallmentRaw.class).list();
        List<OrderPayInstallmentRaw> installmentRawList = new ArrayList<>();
        if(installmentRaws.size()>0)
        {
            for(Object object : installmentRaws)
            {
                installmentRawList.add((OrderPayInstallmentRaw) object);
            }
        }

        return installmentRawList;
    }

}