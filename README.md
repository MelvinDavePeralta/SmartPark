# SmartPark

SmartPark is a Spring Boot REST API for managing parking lots, vehicles, and parking sessions.

The application supports:

- JWT authentication
- Parking lot registration
- Vehicle registration
- Vehicle check-in
- Vehicle check-out
- Parking lot occupancy and availability
- Viewing vehicles currently parked in a parking lot
- Automatic removal of vehicles parked for more than 15 minutes
- H2 in-memory database
- JUnit and Mockito unit tests

## Requirements

Before running the application, make sure you have the following installed:

- Java 11
- Maven

The project includes Maven Wrapper (`mvnw`), so Maven does not need to be installed globally if you use the wrapper.

## Project Setup

Clone or extract the project and open it in IntelliJ IDEA or another Java IDE.

Make sure Java 11 is configured as the project's SDK.

## Build the Application

On Windows, run:

mvnw clean compile


## Importing the Postman Collection

The project includes two Postman files:

Smart Park Postman collection.postman_collection.json
Smart Park Environment.postman_environment.json