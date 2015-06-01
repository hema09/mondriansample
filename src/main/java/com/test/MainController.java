package com.test;

import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Named
@Path("/")
public class MainController {

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/query/{type}")
    public Response testController2(@QueryParam("col") String col, @QueryParam("row") String row,
                                    @PathParam("type") String type) throws Exception {
        String result = TestMondrian.executeAndReturnResult(row, col, type);
        System.out.println(result);
        return Response.ok(result).build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/dimensions")
    public Response getDimensions() throws Exception {
        String result = null;
        System.out.println(result);
        return Response.ok(result).build();
    }
}

/*
* send query
* get result
* display graph
* add ability to drill/rollup (with back button)
*/
