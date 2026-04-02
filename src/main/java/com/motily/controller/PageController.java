package com.motily.controller;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/")
@ApplicationScoped
public class PageController {

    @Inject
    Template index;

    @Inject
    Template human;

    @Inject
    Template simulation;

    @Inject
    Template indicator;

    @Inject
    Template family;

    @Inject
    Template event;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance index() {
        return index.instance();
    }

    @GET
    @Path("human")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance human() {
        return human.instance();
    }

    @GET
    @Path("simulation")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance simulation() {
        return simulation.instance();
    }

    @GET
    @Path("indicator")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance indicator() {
        return indicator.instance();
    }

    @GET
    @Path("family")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance family() {
        return family.instance();
    }

    @GET
    @Path("event")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance event() {
        return event.instance();
    }

    @GET
    @Path("test")
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        return "Test endpoint works!";
    }
}