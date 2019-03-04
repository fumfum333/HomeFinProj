/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shiba.inu.cntlr;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shiba.inu.base.BaseCntlr;
import com.shiba.inu.setup.Category;
import com.shiba.inu.setup.CategoryService;
import com.shiba.inu.setup.CategoryServiceImpl;
import java.lang.reflect.Type;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author mpfleeger
 */
@Path("/Cat")
public class CategoryCtrl extends BaseCntlr {

    CategoryService service = null;

    public CategoryCtrl() {
        service = new CategoryServiceImpl();
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCateogryList() {

        List<Category> list = service.getCategoryList();
        Type listType = new TypeToken<List<Category>>() {
        }.getType();
        Gson gson = new Gson();
        String categoryListItem = gson.toJson(list, listType);

        return Response.ok(categoryListItem).build();

    }

}
