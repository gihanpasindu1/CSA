package com.smartcampus.model;

/**
 * Represents a sensor in the Smart Campus.
 */
public class Sensor {
    private String id;
    private String type; // e.g., "CO2", "Temperature", "Occupancy"
    private String status; // "ACTIVE", "MAINTENANCE", or "OFFLINE"
    private double currentValue;
    private String roomId; // Foreign key to Room

    public Sensor() {}

    public Sensor(String id, String type, String status, String roomId) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.roomId = roomId;
        this.currentValue = 0.0;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getCurrentValue() { return currentValue; }
    public void setCurrentValue(double currentValue) { this.currentValue = currentValue; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
}
