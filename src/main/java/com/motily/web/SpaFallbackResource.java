package com.motily.web;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.io.InputStream;

@Path("/")
public class SpaFallbackResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public InputStream getIndex() {
        return Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("META-INF/resources/index.html");
    }

    @Path("{path: .*}")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public InputStream getFallback() {
        return Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("META-INF/resources/index.html");
    }
}
