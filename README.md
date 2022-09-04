# PostIT - quick notes

---

## Introduction
PostIT is an evaluation project for Efecte recruitment process

## Requirements
1. [NodeJS 16.17.0 LTS](https://nodejs.org/en/)
2. npm 8.11.0
3. [PostgreSQL 14.5](https://www.postgresql.org/download/windows/)
4. [JDK 17 LTS](https://bell-sw.com/pages/downloads/#/java-17-lts)

## Building
1. Create a new database in PostgreSQL
2. Change configuration in `src/main/resources/application.properties`
   - `YOUR_DATABASE_NAME`: The name of the database you've created
   - `YOUR_USERNAME`: User with access to the database
   - `YOUR_PASSWORD`: Password for the user
   - If you've changed the default port while installing PostgreSQL, change `5432` in `jdbc:postgresql://localhost:5432` to your desired port
3. Run `.\mvnw.cmd package` (for Windows) or `./mvnw package` (for Linux)
from the root of the project

## Using
Run `postit-1.0.0.jar` from the `target` folder using `java -jar`