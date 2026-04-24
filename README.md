# Smart Campus Sensor & Room Management API

## API Design Overview
The Smart Campus API is a high-performance, strictly RESTful web service engineered to manage university infrastructure, specifically Rooms, Sensors, and their historical telemetry data. The system is built entirely on **Java and JAX-RS (Jakarta REST)**, running on a lightweight **Grizzly Embedded HTTP Server**. 

The API architecture is built around several core design principles:
1. **Resource-Oriented Architecture:** The API strictly adheres to REST principles, mapping physical campus entities to logical URI paths (`/api/v1/rooms`, `/api/v1/sensors`). It utilizes appropriate HTTP verbs (GET, POST, DELETE) for stateless CRUD operations and relies on standard HTTP status codes (200, 201, 204, 404, 409, 422, 500) to communicate operational outcomes semantically.
2. **Deep Nesting & Sub-Resource Locators:** To handle complex hierarchical data without cluttering controllers, the API employs JAX-RS Sub-Resource Locators. For example, a sensor's historical readings are accessed via nested routes (`/sensors/{sensorId}/readings`), seamlessly delegating processing to dedicated sub-resource classes.
3. **Thread-Safe Volatile Storage:** Because JAX-RS instantiates resource controllers on a per-request basis, the backend state is managed using a highly concurrent, thread-safe in-memory datastore utilizing Java's `ConcurrentHashMap`. This guarantees thread safety and prevents race conditions under heavy load.
4. **Resilient Error Handling:** The system is fortified with advanced JAX-RS Exception Mappers that intercept business logic violations (like deleting occupied rooms or querying non-existent references) and translate them into sanitized, user-friendly JSON error payloads, preventing the leakage of internal stack traces.
5. **Observability via Filters:** Cross-cutting concerns are managed transparently. A global JAX-RS logging filter intercepts all incoming requests and outgoing responses, providing real-time telemetry and auditing without polluting the core business logic.
6. **HATEOAS Discoverability:** The root discovery endpoint implements HATEOAS, providing dynamic hypermedia links to the primary resource collections, allowing clients to navigate the API state programmatically.

## How to Run the Project
1.  **Open NetBeans IDE**.
2.  Go to `File` -> `Open Project` and select the **CSA** folder.
3.  Wait for Maven to download the dependencies.
4.  In the `Source Packages` folder, find `com.smartcampus` -> **`App.java`**.
5.  **Right-click `App.java`** and select **Run File**.
6.  The API will start at: `http://localhost:8080/api/v1`

---

## API Documentation & Testing
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

## Coursework Conceptual Answers

### Part 1: Service Architecture
1.  **Resource Lifecycle:** JAX-RS resources are request-scoped by default, meaning a new instance is instantiated for every incoming request. While this prevents instance variables from crossing over between requests, the underlying in-memory storage must still use thread-safe data structures like ConcurrentHashMap. This prevents data loss and race conditions when multiple concurrent requests attempt to read or modify the shared state simultaneously.
2.  **HATEOAS Benefits:** Providing hypermedia links makes the API self-descriptive. It allows client developers to dynamically discover actions and navigate the API's relationships (like exploring a room's sensors) without hardcoding URLs or relying entirely on static documentation.

### Part 2: Room Management
1.  **IDs vs Full Objects:** Returning only IDs reduces the payload size, saving network bandwidth. However, returning full objects prevents the "N+1 request problem," saving the client from having to make multiple subsequent network calls to process or display the details of each room.
2.  **DELETE Idempotency:** The DELETE operation is strictly idempotent. If a client mistakenly sends the exact same DELETE request multiple times, the first request will delete the room (returning 204 No Content), and subsequent requests will simply return 404 Not Found. Crucially, the final state of the server remains the same: the room no longer exists.

### Part 3: Sensor Operations
1.  **Consumes Mismatch:** The explicit consumption of a media type is made through the use of the form of the method: @Consumes(MediaType.APPLICATION_JSON), we tell JAX-RS to accept only JSON payloads. Provided a client transmits text/plain or application/xml, the discrepancy would be intercepted by JAX-RS prior to the invocation of the method and an automatic error of 415 Unsupported Media Type would be returned by the HTTP.
2.  **Query vs Path Parameters:** Query parameters (?type=Temperature) are better to use as a filter as they are optional, composable and not determined by the hierarchical identity of the resource. Path parameters must be used to refer to a distinct resource identifier.

### Part 4: Deep Nesting
1.  **Sub-Resource Locator Advantages:** It is the ability to delegate logic through sub-resource locators, which helps to ensure that a single controller class is not gigantic and unsupportable. It encourages the Single Responsibility Principle, where the nested SensorReadingResource will deal with its own logic without being dependent on the parent SensorResource.

### Part 5: Error Handling & Logging
1.  **422 vs 404 Dependencies:** HTTP 422 (Unprocessable Entity) is more accurate than 404 (Not Found) in validating a payload. A 404 means the endpoint does not exist, but a 422 is right, there is an endpoint and syntax but a semantic error within the payload (e.g. a missing room reference).
2.  **Stack Trace Risks:** Java internal stack traces are a critical cybersecurity threat. Sensitive data that an attacker can collect includes the framework versions, internal file paths, database structures and logic flaws, and can be used in launching targeted attacks.
3.  **Logging Filters:** JAX-RS filters make cross-cutting concerns centralized. Rather than having to repeat Logger.info() in each and every method, the filter automatically monitors all traffic around the world.

---

## Sample cURL Commands

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

## Technology Stack
*   **Language:** Java 17+
*   **Framework:** Jersey (JAX-RS Implementation)
*   **Server:** Grizzly (Embedded HTTP Container)
*   **Build Tool:** Maven
*   **Data Format:** JSON (Jackson Provider)
