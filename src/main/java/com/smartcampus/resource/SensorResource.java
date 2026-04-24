package com.smartcampus.resource;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.storage.Storage;
import com.smartcampus.exception.LinkedResourceNotFoundException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Resource class to manage sensors.
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    @GET
    public Collection<Sensor> getSensors(@QueryParam("type") String type) {
        Collection<Sensor> allSensors = Storage.getSensors().values();
        if (type == null || type.isEmpty()) {
            return allSensors;
        }
        return allSensors.stream()
                .filter(s -> type.equalsIgnoreCase(s.getType()))
                .collect(Collectors.toList());
    }

    @POST
    public Response createSensor(Sensor sensor) {
        Room room = Storage.getRoom(sensor.getRoomId());
        if (room == null) {
            throw new LinkedResourceNotFoundException("Room " + sensor.getRoomId() + " does not exist.");
        }

        Storage.addSensor(sensor);
        room.getSensorIds().add(sensor.getId());
        
        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    @GET
    @Path("/{id}")
    public Sensor getSensor(@PathParam("id") String id) {
        Sensor sensor = Storage.getSensor(id);
        if (sensor == null) {
            throw new LinkedResourceNotFoundException("Sensor " + id + " not found.");
        }
        return sensor;
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
