package com.motily.api;

import com.motily.human.Human;
import com.motily.human.HumanService;
import com.motily.society.SocialIndicator;
import com.motily.society.SocietyService;
import com.motily.timeline.Timeline;
import com.motily.timeline.TimelineService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api")
@ApplicationScoped
public class ApiResource {
    @Inject
    HumanService humanService;
    
    @Inject
    TimelineService timelineService;
    
    @Inject
    SocietyService societyService;
    
    // 数字人管理接口
    @POST
    @Path("/human/generate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateHuman(@QueryParam("count") int count) {
        for (int i = 0; i < count; i++) {
            String name = "Person" + i;
            int gender = i % 2;
            int birthYear = 2000;
            humanService.createHuman(name, gender, birthYear, null, null);
        }
        return Response.ok().entity("{\"code\": 200, \"data\": {\"generatedCount\": " + count + "}}")
                .build();
    }
    
    @GET
    @Path("/human/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listHumans(@QueryParam("page") int page, @QueryParam("size") int size) {
        List<Human> humans = humanService.listHumans(page, size);
        long total = humanService.countHumans();
        return Response.ok().entity("{\"code\": 200, \"data\": {\"list\": " + humans + ", \"total\": " + total + "}}")
                .build();
    }
    
    @GET
    @Path("/human/detail")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHumanDetail(@QueryParam("id") Long id) {
        Human human = humanService.getHumanById(id);
        return Response.ok().entity("{\"code\": 200, \"data\": " + human + "}")
                .build();
    }
    
    @PUT
    @Path("/human/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateHuman(Human human) {
        humanService.updateHuman(human);
        return Response.ok().entity("{\"code\": 200, \"data\": {\"success\": true}}")
                .build();
    }
    
    // 社会模拟接口
    @POST
    @Path("/simulation/start")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response startSimulation(@QueryParam("years") int years) {
        Timeline timeline = timelineService.createTimeline(2000);
        timelineService.runSimulation(timeline, years);
        return Response.ok().entity("{\"code\": 200, \"data\": {\"simulationId\": " + timeline.id + "}}")
                .build();
    }
    
    @POST
    @Path("/simulation/pause")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response pauseSimulation(@QueryParam("simulationId") Long simulationId) {
        Timeline timeline = timelineService.getTimelineById(simulationId);
        timelineService.pauseTimeline(timeline);
        return Response.ok().entity("{\"code\": 200, \"data\": {\"success\": true}}")
                .build();
    }
    
    @POST
    @Path("/simulation/resume")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response resumeSimulation(@QueryParam("simulationId") Long simulationId) {
        Timeline timeline = timelineService.getTimelineById(simulationId);
        timelineService.resumeTimeline(timeline);
        return Response.ok().entity("{\"code\": 200, \"data\": {\"success\": true}}")
                .build();
    }
    
    @POST
    @Path("/simulation/stop")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response stopSimulation(@QueryParam("simulationId") Long simulationId) {
        Timeline timeline = timelineService.getTimelineById(simulationId);
        timelineService.stopTimeline(timeline);
        return Response.ok().entity("{\"code\": 200, \"data\": {\"success\": true}}")
                .build();
    }
    
    @GET
    @Path("/simulation/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSimulationStatus(@QueryParam("simulationId") Long simulationId) {
        Timeline timeline = timelineService.getTimelineById(simulationId);
        return Response.ok().entity("{\"code\": 200, \"data\": {\"status\": " + timeline.status + ", \"currentYear\": " + timeline.currentYear + "}}")
                .build();
    }
    
    // 社会指标接口
    @GET
    @Path("/indicator/year")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIndicatorByYear(@QueryParam("year") int year) {
        SocialIndicator indicator = societyService.getSocialIndicatorByYear(year);
        return Response.ok().entity("{\"code\": 200, \"data\": " + indicator + "}")
                .build();
    }
    
    @GET
    @Path("/indicator/trend")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIndicatorTrend(@QueryParam("startYear") int startYear, @QueryParam("endYear") int endYear) {
        List<SocialIndicator> indicators = societyService.listSocialIndicators(startYear, endYear);
        return Response.ok().entity("{\"code\": 200, \"data\": " + indicators + "}")
                .build();
    }
}
