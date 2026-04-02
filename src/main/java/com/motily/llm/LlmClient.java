package com.motily.llm;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "http://localhost:8000")
public interface LlmClient {
    @POST
    @Path("/v1/generate")
    @Consumes("application/json")
    @Produces("application/json")
    LlmResponse generate(LlmRequest request);
}
