/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shiba.inu.cntlr;

import com.shiba.inu.base.BaseCntlr;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shiba.inu.tran.TransactionItem;
import com.shiba.inu.tran.TransactionService;
import com.shiba.inu.tran.TransactionServiceImpl;
import com.shiba.inu.tran.TransactionSum;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * http://localhost:8080/HomeFinServices/rest/Trans/2018
 * @author mpfleeger
 */
@Path("/Trans")
public class TransCntlr extends BaseCntlr {

    TransactionService service = null;

    public TransCntlr() {
        service = new TransactionServiceImpl();
    }

    @GET
    @Path("/list/{year}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransactionItems(@PathParam("year") Integer year) {

        List<TransactionItem> list = service.getTransactions();
        Type listType = new TypeToken<List<TransactionItem>>() {
        }.getType();
        Gson gson = new Gson();
        String tranItemList = gson.toJson(list, listType);

        return Response.ok(tranItemList).build();

    }
    
    @GET
    @Path("/list/month/{year}/{month}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransactionItemsByMonth(@PathParam("year") Integer year, @PathParam("month") Integer month) {

        List<TransactionItem> list = service.getTransactionByMonth(year, month);
        Type listType = new TypeToken<List<TransactionItem>>() {
        }.getType();
        Gson gson = new Gson();
        String tranItemList = gson.toJson(list, listType);

        return Response.ok(tranItemList).build();

    }
    
    @GET
    @Path("/{year}/{category}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransactionItems(@PathParam("year") Integer year,@PathParam("category") String category) {

        List<TransactionSum> list = service.getTransactionSumByYearCat(year, category);
        Type listType = new TypeToken<List<TransactionSum>>() {
        }.getType();
        Gson gson = new Gson();
        String tranItemList = gson.toJson(list, listType);

        return Response.ok(tranItemList).build();

    }
    
    @GET
    @Path("/sum/{year}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchTotalTransAmountByYearCat(@PathParam("year") Integer year) {
        List<TransactionSum> list = service.fetchTranSummaryByYear(year);
        Type listType = new TypeToken<List<TransactionSum>>() {
        }.getType();
        Gson gson = new Gson();
        String tranItemList = gson.toJson(list, listType);
        return Response.ok(tranItemList).build();
    }
    
    @GET
    @Path("/sum/{year}/{cat}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fetchTotalAmountByMonthCat(@PathParam("year") Integer year, @PathParam("cat") String categoryId) {
        List<TransactionSum> list = service.fetchSumByCategory(categoryId, year);
        Type listType = new TypeToken<List<TransactionSum>>() {
        }.getType();
        Gson gson = new Gson();
        String tranItemList = gson.toJson(list, listType);
        return Response.ok(tranItemList).build();
    }
    
    @GET
    @Path("/{year}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getTotalByYear(@PathParam("year") Integer year) {
        BigDecimal amount = service.getTotalYear(year);
        return Response.ok(amount).build();
    }
    
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response createTranItem(@FormParam("sourceId") String sourceId, @FormParam("transactionDate") String transactionDate, @FormParam("description") String description,
            @FormParam("categoryId") String categoryId, @FormParam("transAmount") String transAmount) throws ParseException {
        TransactionItem tranItem = new TransactionItem();
        tranItem.setSourceId(sourceId);
        
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date tranDate = df.parse(transactionDate);
        
        tranItem.setTransactionDate(tranDate);
        tranItem.setDescription(description);
        tranItem.setCategoryId(categoryId);
        tranItem.setTransAmount(new BigDecimal(transAmount));

        tranItem = service.createTransactionItem(tranItem);
        
        Long tranitemId = tranItem.getTransactionId();

        return Response.ok(tranitemId+"").build();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response testMsg() {
        System.out.println("TestMsg Here");
        return Response.status(Response.Status.OK).entity("Hello World LALALA").build();
    }

}
