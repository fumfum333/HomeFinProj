/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shiba.test;

import java.math.BigDecimal;
import java.util.Date;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

/**
 *
 * @author mpfleeger
 */
public class TestTran {

    public static void main(String[] args) {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("Trans");
        
        Form form = new Form();
        form.param("sourceId", "CAPITAL_ONE");
       // form.param("transactionDate", "01/20/2019");
        form.param("description", "TEST");
        form.param("categoryId", "AUTO");
        //form.param("transAmount", "20");
        
        
        Response res = webTarget.path("create").request(javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED).post(Entity.form(form), Response.class);
        System.out.println("post doen"+res.getStatus());
                
    }

    private static WebTarget webTarget;
    private static Client client;
    private static final String BASE_URI = "http://localhost:8080/HomeFinServices/rest";

    public <T> T getTransactionItems(Class<T> responseType, String year, String category) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}/{1}", new Object[]{year, category}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T getTotalByYear(Class<T> responseType, String year) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{year}));
        return resource.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(responseType);
    }

    public <T> T getTransactionItemsByMonth(Class<T> responseType, String year, String month) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("list/month/{0}/{1}", new Object[]{year, month}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public Response createTranItem() throws ClientErrorException {
        return webTarget.path("create").request(javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED).post(null, Response.class);
    }

    public <T> T testMsg(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        return resource.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(responseType);
    }

    public <T> T fetchTotalTransAmountByYearCat(Class<T> responseType, String year) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("sum/{0}", new Object[]{year}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

    public <T> T fetchTotalAmountByMonthCat(Class<T> responseType, String year, String cat) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource = resource.path(java.text.MessageFormat.format("sum/{0}/{1}", new Object[]{year, cat}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(responseType);
    }

}
