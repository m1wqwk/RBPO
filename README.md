# Lab Work 1: Introduction to Spring Boot

## Project Overview

This is a Spring Boot demonstration application created as part of Lab Work 1. The application showcases basic Spring Boot functionality with simple REST endpoints.

## Endpoints

The application exposes the following endpoints:

*   `GET /`
    *   Description: The main page showing the available endpoints.
    *   Response: A message with a list of available endpoints.
*   `GET /hello`
    *   Description: Returns a simple "Hello World!" message
    *   Response: "Hello World!"
*   `GET /time`
    *   Description: Returns the current server time
    *   Response:  Time in format "yyyy-MM-dd HH:mm:ss"
*   `GET /location`
    *   Description: Returns the current location based on default locale
    *   Response: Current country and locale information

## Technologies Used

*   **Java 21+**
*   **Spring Boot 3.5.6**
*   **Apache Maven**
