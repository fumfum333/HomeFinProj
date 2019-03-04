/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shiba.test;

import java.io.File;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

/**
 *
 * @author mpfleeger
 */
public class Tester {

    private static WebTarget webTarget;
    private static Client client;
    private static final String BASE_URI = "http://localhost:8080/HomeFinServices/rest";

//    static class CsvFileImportCtrl_JerseyClient {
//
//        
//
////        public CsvFileImportCtrl_JerseyClient() {
////            client = javax.ws.rs.client.ClientBuilder.newClient();
////            webTarget = client.target(BASE_URI).path("/Import");
////        }
//
//        public Response uploadCsvFile() throws ClientErrorException {
//            //return webTarget.request().post(null, Response.class);
//            
//        }
//
//        public void close() {
//            client.close();
//        }
//    }
    private static Response createClient() {

        client = javax.ws.rs.client.ClientBuilder.newClient();
        client.register(MultiPartFeature.class);
        webTarget = client.target(BASE_URI).path("/Import");

        File tempFile = new File("/Users/mpfleeger/Desktop/temp2/fumiko.txt");
        //return webTarget.request().post(Entity.entity(tempFile, MediaType.MULTIPART_FORM_DATA_TYPE), Response.class);

        FormDataMultiPart mp = new FormDataMultiPart();

        FormDataContentDisposition dispo = FormDataContentDisposition.name("file").fileName("fumiko.txt").size(tempFile.length()).build();
        FormDataBodyPart p = new FormDataBodyPart(dispo, tempFile, MediaType.MULTIPART_FORM_DATA_TYPE);
        mp.field("fileType", "TXT");
        
        mp.bodyPart(p);
        
        

        return webTarget.request().post(Entity.entity(mp, MediaType.MULTIPART_FORM_DATA_TYPE), Response.class);

    }

    public static void main(String[] args) {
        Tester mainTester = new Tester();

        //Tester.CsvFileImportCtrl_JerseyClient tstClient = new Tester.CsvFileImportCtrl_JerseyClient();
        Response res = mainTester.createClient();
        System.out.println("test done");
    }

}
