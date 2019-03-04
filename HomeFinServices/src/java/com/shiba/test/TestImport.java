/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shiba.test;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author mpfleeger
 */
public class TestImport {

    public static void main(String[] args) {
        WebTarget webTarget;
        Client client;
        final String BASE_URI = "http://localhost:8080/HomeFinServices/rest";
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("Pend");

        WebTarget resource = webTarget;
        Response res = resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(Response.class);
        System.out.println("Test done");

    }

}
