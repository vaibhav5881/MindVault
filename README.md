# MindVault

MindVault is a sleek, intuitive digital journal built with Java and Spring Boot, offering secure user authentication, note creation/editing, and persistence via a lightweight database. With a clean REST API and optional Spring Security, it enables seamless integration and flexible expansion.

## Features and Functionality

*   **User Authentication:** Secure user registration and login using Spring Security and JWT (JSON Web Tokens).
*   **Journal Entry Management:** Create, read, update, and delete journal entries via a REST API.
*   **Data Persistence:** Uses MongoDB for persistent storage of user accounts and journal entries.
*   **Sentiment Analysis (Optional):**  Users can enable sentiment analysis for their journal entries (though the current implementation returns a hardcoded "Happy" sentiment).  A scheduled task can analyze user's entries over the past week and send them an email with the most frequent sentiment.  The `UserScheduler` class handles this using a Kafka topic.
*   **Admin Interface:**  Provides administrative endpoints for user management and cache reloading.
*   **Google Authentication:** Allows users to sign in with their Google accounts.
*   **Weather Integration:**  Fetches weather information based on location (currently hardcoded to "Varanasi" in the greeting endpoint) using an external API.  The API key and city are configured in a database-backed cache.
*   **API Documentation:**  Swagger integration for easy API exploration and testing.

## Technology Stack

*   Java 11+
*   Spring Boot
*   Spring Security
*   Spring Data MongoDB
*   JWT (JSON Web Tokens)
*   Redis (for caching)
*   MongoDB
*   Lombok
*   Swagger (OpenAPI 3)
*   Kafka (for sending weekly sentiments)

## Prerequisites

*   Java Development Kit (JDK) 11 or higher
*   Maven
*   MongoDB installed and running
*   Redis installed and running
*   Kafka installed and running

## Installation Instructions

1.  **Clone the repository:**

    ```bash
    git clone https://github.com/vaibhav5881/MindVault.git
    cd MindVault
    ```

2.  **Configure MongoDB:**

    *   Ensure MongoDB is running on its default port (27017).
    *   The application connects to a MongoDB database named `test`.

3.  **Configure Redis:**

    *   Ensure Redis is running on its default port (6379).
    *   The Redis configuration is located in `src/main/java/net/engineeringdigest/journalApp/config/RedisConfig.java`.

4.  **Configure Weather API Key:**

    *   Obtain a Weatherstack API key from [Weatherstack](https://weatherstack.com/).
    *   Insert this API key into the `config_journal_app` collection in MongoDB:

        ```json
        db.config_journal_app.insert({key: "WEATHER_API", value: "https://api.weatherstack.com/current?access_key=API_KEY&query=CITY"})
        ```

        Replace `API_KEY` with your actual API key.  Also, ensure `CITY` is the placeholder. The `Placeholders` interface in `src/main/java/net/engineeringdigest/journalApp/constants/Placeholders.java` defines the placeholder constants. The `AppCache` class in `src/main/java/net/engineeringdigest/journalApp/cache/AppCache.java` initializes the cache from this MongoDB collection.

5. **Configure Google Authentication:**

    * Register your application with Google to obtain a `client-id` and `client-secret`.
    * Set the following environment variables or add them to your `application.properties` or `application.yml`:

    ```properties
    spring.security.oauth2.client.registration.google.client-id=YOUR_CLIENT_ID
    spring.security.oauth2.client.registration.google.client-secret=YOUR_CLIENT_SECRET
    ```

6. **Build the application:**

    ```bash
    mvn clean install
    ```

7.  **Run the application:**

    ```bash
    mvn spring-boot:run
    ```

## Usage Guide

1.  **Access the application:**

    *   The application will be running on `http://localhost:8080`.

2.  **API Endpoints:**

    *   **/public/signup:**  Register a new user.  Requires a JSON payload with `userName`, `password`, `email`, and `sentimentAnalysis` fields (see `net/engineeringdigest/journalApp/dto/UserDto.java`).

        ```json
        {
          "userName": "newuser",
          "password": "password123",
          "email": "newuser@example.com",
          "sentimentAnalysis": true
        }
        ```

    *   **/public/login:**  Login with existing credentials.  Requires `userName` and `password` in the request body.  Returns a JWT token.

        ```json
        {
          "userName": "newuser",
          "password": "password123"
        }
        ```

    *   **/journal:** (Requires authentication) Get all journal entries for the logged-in user.

    *   **/journal:** (Requires authentication) Create a new journal entry. Requires the following JSON payload:

        ```json
        {
          "title": "My Entry Title",
          "content": "My entry content",
          "sentiment": "HAPPY"  // Can be HAPPY, SAD, ANGRY, ANXIOUS (net/engineeringdigest/journalApp/enums/Sentiment.java)
        }
        ```

    *   **/journal/id/{id}**: (Requires authentication) Get, update, or delete a specific journal entry by its ID.  The ID is a MongoDB ObjectId.

    *   **/user:** (Requires authentication) Get a list of all users.

    *   **/user/id/{id}**: (Requires authentication) Get a specific user by ID. The ID is a MongoDB ObjectId.

    *   **/admin/all-users:** (Requires ADMIN role) Get all users. Requires authentication and ADMIN role.

    *   **/admin/create-admin:** (Requires ADMIN role) Create a new administrator user. Requires authentication and ADMIN role. Payload is the same as signup.

    *   **/admin/reload-app-cache:** (Requires ADMIN role) Reload the application cache.  Requires authentication and ADMIN role.

    *   **/user/greeting**: (Requires authentication) Returns a greeting message that includes weather information for Varanasi.

    *   **/auth/google/callback**: Endpoint for handling Google authentication callback.

3.  **Authentication:**

    *   Most endpoints require authentication via a JWT token.
    *   After logging in via `/public/login`, include the JWT token in the `Authorization` header of subsequent requests in the format `Bearer <token>`.
    *  For Google Authentication, navigate to Google OAuth Playground to generate code , then hit this api with `code` param.

4.  **Swagger UI:**

    *   Access the Swagger UI at `http://localhost:8080/swagger-ui/index.html` to explore the API endpoints and their documentation.  The `SwaggerConfig` class at `src/main/java/net/engineeringdigest/journalApp/config/SwaggerConfig.java` configures the Swagger UI. You'll need to configure the JWT authentication in Swagger UI using the Authorize button.

## API Documentation

The API documentation is generated using Swagger and is accessible at `http://localhost:8080/swagger-ui/index.html`. The configuration for Swagger is found in `src/main/java/net/engineeringdigest/journalApp/config/SwaggerConfig.java`.  It includes information on how to configure JWT authentication via the UI.

## Contributing Guidelines

1.  Fork the repository.
2.  Create a new branch for your feature or bug fix.
3.  Make your changes and commit them with descriptive commit messages.
4.  Push your changes to your fork.
5.  Submit a pull request to the `master` branch of the original repository.

## License Information

License information is not specified.

## Contact/Support Information

For questions or support, please contact vaibhavkumarchaurasiya5881@gmail.com.
