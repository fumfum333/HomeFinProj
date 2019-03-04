/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shiba.inu.cntlr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shiba.inu.base.BaseCntlr;
import com.shiba.inu.imp.ImportTranItem;
import com.shiba.inu.imp.ImportTransService;
import com.shiba.inu.imp.ImportTransServiceImpl;

import java.lang.reflect.Type;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author mpfleeger
 */
@Path("/Pend")
public class ImportTranItemCtrl extends BaseCntlr {

    private ImportTransService service = null;

    public ImportTranItemCtrl() {
        service = new ImportTransServiceImpl();
    }
    
    @GET
    @Path("/{importDate}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getImportItems(@PathParam("importDate") String importDate) {
        System.out.println("importDate:"+importDate);
        List<ImportTranItem> list = service.getAllPendingTransactions(importDate);
        Type listType = new TypeToken<List<ImportTranItem>>() {
        }.getType();
        Gson gson = new Gson();
        GsonBuilder builder = new GsonBuilder();
        gson = builder.serializeNulls().create();
        String tranItemList = gson.toJson(list, listType);
        return Response.ok(tranItemList).build();
    }
    
    @GET
    @Path("/dates")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getImportDates() {
        List<String> list = service.getListOfImportedDates();
        Type listType = new TypeToken<List<String>>() {
        }.getType();
        Gson gson = new Gson();
        GsonBuilder builder = new GsonBuilder();
        gson = builder.serializeNulls().create();
        String dateList = gson.toJson(list, listType);
        return Response.ok(dateList).build();
    }
    
    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateImportData(@FormParam("impSeqIdSave") String impSeqId, @FormParam("markIndSave") String markInd) {
        System.out.println("impSeqId:"+impSeqId+" markINd:"+impSeqId);
        String ret = service.updateImportTransaction(markInd, Long.parseLong(impSeqId));
        return Response.ok(ret).build();
    }
      
}
