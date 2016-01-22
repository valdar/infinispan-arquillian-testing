package com.github.valdar.iat.rest.endpoints;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface KeyValue {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{id}")
    String getById( @PathParam("id")String id );

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{id}")
    void putById( @PathParam("id")String id, @FormParam("value") String value );

}
