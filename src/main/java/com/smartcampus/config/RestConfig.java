package com.smartcampus.config;

import org.glassfish.jersey.server.ResourceConfig;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class RestConfig extends ResourceConfig {
    public RestConfig() {
        packages("com.smartcampus.resource", "com.smartcampus.exception", "com.smartcampus.filter");
    }
}
