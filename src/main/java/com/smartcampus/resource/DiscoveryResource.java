package com.smartcampus.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

/**
 * Root "Discovery" endpoint to provide API metadata.
 */
@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getDiscovery() {
        Map<String, Object> discovery = new HashMap<>();
        
        discovery.put("name", "Smart Campus Sensor & Room Management API");
        discovery.put("version", "v1.0.0");
        discovery.put("admin_contact", "student@westminster.ac.uk");
        
        // HATEOAS-style links to primary resource collections
        Map<String, String> links = new HashMap<>();
        links.put("rooms", "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        
        discovery.put("links", links);
        
        return discovery;
    }
}
