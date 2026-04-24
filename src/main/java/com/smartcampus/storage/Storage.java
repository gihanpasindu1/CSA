package com.smartcampus.storage;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Storage {
    private static final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private static final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private static final Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    public static Map<String, Room> getRooms() { return rooms; }
    public static void addRoom(Room room) { rooms.put(room.getId(), room); }
    public static Room getRoom(String id) { return rooms.get(id); }
    public static void removeRoom(String id) { rooms.remove(id); }

    public static Map<String, Sensor> getSensors() { return sensors; }
    public static void addSensor(Sensor sensor) { 
        sensors.put(sensor.getId(), sensor);
        readings.putIfAbsent(sensor.getId(), new ArrayList<>());
    }
    public static Sensor getSensor(String id) { return sensors.get(id); }
    public static void removeSensor(String id) { 
        sensors.remove(id);
        readings.remove(id);
    }

    public static List<SensorReading> getReadings(String sensorId) {
        return readings.getOrDefault(sensorId, new ArrayList<>());
    }
    public static void addReading(String sensorId, SensorReading reading) {
        readings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);
    }
}
