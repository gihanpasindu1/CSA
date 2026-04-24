package com.smartcampus.resource;

import com.smartcampus.model.Room;
import com.smartcampus.storage.Storage;
import com.smartcampus.exception.RoomNotEmptyException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Resource class to manage rooms.
 */
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    @GET
    public Collection<Room> getAllRooms() {
        return Storage.getRooms().values();
    }

    @POST
    public Response createRoom(Room room) {
        if (room.getId() == null || room.getId().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Room ID is required").build();
        }
        Storage.addRoom(room);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    @GET
    @Path("/{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId) {
        Room room = Storage.getRoom(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(room).build();
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = Storage.getRoom(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Cannot delete room: " + roomId + " because it has sensors assigned.");
        }

        Storage.removeRoom(roomId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
