package com.motily.web;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/api/web")
@ApplicationScoped
public class WebResource {

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

    void onStart(@Observes StartupEvent event) {
        System.out.println("Motily Application started - serving Vue SPA from /");
    }
}