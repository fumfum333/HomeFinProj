/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shiba.inu.cntlr;

import com.shiba.inu.base.BaseCntlr;
import com.shiba.inu.imp.ImportTranItem;
import com.shiba.inu.imp.ImportTransService;
import com.shiba.inu.imp.ImportTransServiceImpl;
import com.shiba.inu.util.CsvWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 *
 * @author fmatsuura
 */
@Path("/Import")
public class CsvFileImportCtrl extends BaseCntlr {

    ImportTransService service = null;

    public CsvFileImportCtrl() {
        service = new ImportTransServiceImpl();
    }

    private static final String FILE_PATH = "/Users/mpfleeger/Desktop/temp";

    @Context
    private UriInfo context;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadCsvFile(@FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @FormDataParam("fileType") String fileType) {
        //System.out.println("HERE fileType" + fileType);
        if (uploadedInputStream == null || fileDetail == null) {
            return Response.status(400).entity("Invalid form data").build();
        }
        try {
            saveFile(uploadedInputStream, FILE_PATH + File.separator + fileDetail.getFileName());
        } catch (IOException ex) {
            Logger.getLogger(CsvFileImportCtrl.class.getName()).log(Level.SEVERE, null, ex);
        }

        int num = service.importFileRecords(FILE_PATH + File.separator + fileDetail.getFileName());

        return Response.status(200)
                .entity("File uploaded, " + num + " records have been saved. ").build();
    }

    private void saveFile(InputStream inStream, String target) throws IOException {
        //System.out.println(target + "==");
        OutputStream out = null;
        int read = 0;
        byte[] bytes = new byte[1024];
        try {
            out = new FileOutputStream(new File(target));
            while ((read = inStream.read(bytes)) != -1) {

                out.write(bytes, 0, read);

            }
            out.flush();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CsvFileImportCtrl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CsvFileImportCtrl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (out != null) {
                out.close();
            }

        }
    }

    @GET
    @Path("/markInd/{importDate}")
    @Produces(MediaType.TEXT_PLAIN)
    public String createFile(@PathParam("importDate") String importDate) {
        List<ImportTranItem> items = service.getMarkIndImportItems(importDate);
        
        return createCsvFile(items);
    }

    public String createCsvFile(List<ImportTranItem> items) {
        FileWriter writer = null;
        Date todayDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        String filename = "mark_" + sdf.format(todayDate) + ".csv";

        String fileFullPath = FILE_PATH + File.separator + "" + filename;

        try {
            writer = new FileWriter(fileFullPath);

            CsvWriter.writeLine(writer, Arrays.asList("Transaction Date", "Description", "Category", "Debit", "Credit"));

            for (ImportTranItem item : items) {
                String debit = item.getDebit() != null ? item.getDebit().toString() : "";
                String credit = item.getCredit() != null ? item.getCredit().toString() : "";

                CsvWriter.writeLine(writer, Arrays.asList(sdf.format(item.getTransactionDate()), item.getDescription(), item.getCategory(), debit, credit));

            }
            return fileFullPath;
        } catch (IOException ex) {
            Logger.getLogger(CsvFileImportCtrl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                try {
                    writer.flush();
                } catch (IOException ex) {
                    Logger.getLogger(CsvFileImportCtrl.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (writer != null) {
                    writer.close();
                }

            } catch (IOException ex) {
                Logger.getLogger(CsvFileImportCtrl.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return null;
    }

}
