package com.github.valdar.iat.rest.endpoints.impl;

import com.github.valdar.iat.rest.endpoints.KeyValue;
import com.github.valdar.iat.services.GridService;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
public class KeyValueImpl implements KeyValue {

    @Inject
    private Logger LOG;

    @Inject
    private GridService gridService;

    @Override
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{id}")
    public String getById( @PathParam("id")String id ){
        return gridService.getById(id);
    }

    @Override
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{id}")
    public void putById( @PathParam("id")String id, @FormParam("value") String value ){
        gridService.putById(id,value);
    }

}
