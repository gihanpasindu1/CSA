package com.smartcampus.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Catch-all ExceptionMapper to prevent raw stack traces from leaking.
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable throwable) {
        // If it's already a JAX-RS exception (like 404 Not Found), let it through
        if (throwable instanceof javax.ws.rs.WebApplicationException) {
            return ((javax.ws.rs.WebApplicationException) throwable).getResponse();
        }

        // Log the actual error internally (not shown here for brevity)
        System.err.println("Unexpected Error: " + throwable.getMessage());
        throwable.printStackTrace();

        Map<String, String> error = new HashMap<>();
        error.put("error", "Internal Server Error");
        error.put("message", "An unexpected error occurred. Please contact the administrator.");
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
