# Smart Campus Sensor & Room Management API

This is a high-performance RESTful web service built using **JAX-RS (Jakarta REST)** and the **Grizzly Embedded HTTP Server**. It is designed to manage university campus resources, specifically Rooms and Sensors, with full support for historical data logging and complex error handling.

##  How to Run the Project
1.  **Open NetBeans IDE**.
2.  Go to `File` -> `Open Project` and select the **CSA** folder.
3.  Wait for Maven to download the dependencies.
4.  In the `Source Packages` folder, find `com.smartcampus` -> **`App.java`**.
5.  **Right-click `App.java`** and select **Run File**.
6.  The API will start at: `http://localhost:8080/api/v1`

---

##  API Documentation & Testing
You can use **Postman** or **cURL** to interact with the following endpoints.

### 1. Discovery & Metadata
*   **URL:** `GET /api/v1`
*   **Description:** Returns API versioning and primary collection links (HATEOAS).

### 2. Room Management
*   **Create Room:** `POST /api/v1/rooms`
    *   *Body:* `{"id": "LIB-301", "name": "Quiet Study", "capacity": 50}`
*   **List All Rooms:** `GET /api/v1/rooms`
*   **Get Specific Room:** `GET /api/v1/rooms/{id}`
*   **Delete Room:** `DELETE /api/v1/rooms/{id}`
    *   *Constraint:* Returns **409 Conflict** if the room contains active sensors.

### 3. Sensor Management
*   **Create Sensor:** `POST /api/v1/sensors`
    *   *Body:* `{"id": "T-01", "type": "Temperature", "status": "ACTIVE", "roomId": "LIB-301"}`
    *   *Constraint:* Returns **422 Unprocessable Entity** if the `roomId` does not exist.
*   **Search by Type:** `GET /api/v1/sensors?type=Temperature`

### 4. Sensor Readings (Nested Resources)
*   **Add Reading:** `POST /api/v1/sensors/{sensorId}/readings`
    *   *Body:* `{"id": "R1", "timestamp": 1713888000, "value": 22.5}`
    *   *Side Effect:* Automatically updates the `currentValue` of the parent sensor.
    *   *Constraint:* Returns **403 Forbidden** if the sensor status is `MAINTENANCE`.
*   **Get History:** `GET /api/v1/sensors/{sensorId}/readings`

---

##  Coursework Conceptual Answers

### Part 1: Service Architecture
*   **Resource Lifecycle:** JAX-RS resources are **request-scoped** by default, meaning a new instance is instantiated for every incoming request. While this prevents instance variables from crossing over between requests, the underlying in-memory storage must still use thread-safe data structures like `ConcurrentHashMap`. This prevents data loss and race conditions when multiple concurrent requests attempt to read or modify the shared state simultaneously.
*   **HATEOAS Benefits:** Providing hypermedia links makes the API self-descriptive. It allows client developers to dynamically discover actions and navigate the API's relationships (like exploring a room's sensors) without hardcoding URLs or relying entirely on static documentation.

### Part 2: Room Management
*   **IDs vs Full Objects:** Returning only IDs reduces the payload size, saving network bandwidth. However, returning full objects prevents the "N+1 request problem," saving the client from having to make multiple subsequent network calls to process or display the details of each room.
*   **DELETE Idempotency:** The DELETE operation is strictly idempotent. If a client mistakenly sends the exact same DELETE request multiple times, the first request will delete the room (returning `204 No Content`), and subsequent requests will simply return `404 Not Found`. Crucially, the final state of the server remains the same: the room no longer exists.

### Part 3: Sensor Operations
*   **@Consumes Mismatch:** By explicitly using `@Consumes(MediaType.APPLICATION_JSON)`, we instruct JAX-RS to only accept JSON payloads. If a client sends `text/plain` or `application/xml`, JAX-RS intercepts the mismatch before the method is invoked and automatically returns an **HTTP 415 Unsupported Media Type** error.
*   **Query vs Path Parameters:** Query parameters (`?type=Temperature`) are superior for filtering because they are optional, composable, and do not define the hierarchical identity of the resource. Path parameters should be reserved strictly for pointing to a unique resource identifier.

### Part 4: Deep Nesting
*   **Sub-Resource Locator Benefits:** Delegating logic via sub-resource locators prevents a single controller class from becoming massive and unmaintainable. It promotes the **Single Responsibility Principle**, allowing the nested `SensorReadingResource` to handle its own specific logic independently from the parent `SensorResource`.

### Part 5: Error Handling & Logging
*   **422 vs 404 for Dependencies:** HTTP 422 (Unprocessable Entity) is semantically more accurate than 404 (Not Found) when validating a payload. A 404 implies the endpoint itself doesn't exist, whereas a 422 correctly indicates that the endpoint and syntax are valid, but a semantic error exists inside the payload (e.g., a missing room reference).
*   **Stack Trace Risks:** Exposing internal Java stack traces is a severe cybersecurity risk. An attacker can gather sensitive information such as the framework versions, internal file paths, database structures, and logic flaws, which can be exploited to launch targeted attacks.
*   **Logging Filters:** Implementing JAX-RS filters for cross-cutting concerns centralizes the logic. Instead of repeating `Logger.info()` in every method (which is prone to human error and clutters business logic), the filter automatically intercepts and observes all traffic globally.

---

##  Sample cURL Commands

**1. Discovery:**
`curl -X GET http://localhost:8080/api/v1`

**2. Create a Room:**
`curl -X POST http://localhost:8080/api/v1/rooms -H "Content-Type: application/json" -d "{\"id\": \"LIB-301\", \"name\": \"Quiet Study\", \"capacity\": 50}"`

**3. Create a Sensor:**
`curl -X POST http://localhost:8080/api/v1/sensors -H "Content-Type: application/json" -d "{\"id\": \"T-01\", \"type\": \"Temperature\", \"status\": \"ACTIVE\", \"roomId\": \"LIB-301\"}"`

**4. Add a Reading:**
`curl -X POST http://localhost:8080/api/v1/sensors/T-01/readings -H "Content-Type: application/json" -d "{\"id\": \"R1\", \"timestamp\": 1713888000, \"value\": 22.5}"`

**5. Get Filtered Sensors:**
`curl -X GET "http://localhost:8080/api/v1/sensors?type=Temperature"`

---

##  Technology Stack
*   **Language:** Java 17+
*   **Framework:** Jersey (JAX-RS Implementation)
*   **Server:** Grizzly (Embedded HTTP Container)
*   **Build Tool:** Maven
*   **Data Format:** JSON (Jackson Provider)
