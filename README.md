# Rule Engine Scala Project
## Overview

The project aims to develop a Scala-based ETL (Extract, Transform, Load) system equipped with a rule engine. The system will monitor a designated directory for incoming data files, treating this directory as the primary source layer for data generation. Upon detecting a new file, the system will immediately initiate processing through a watcher service. The objective is to ingest the data, apply qualification checks to ensure the validity of each transaction, and subsequently calculate discounts based on predetermined conditions. The qualifiers are intricately linked to specific discount calculators, with each qualifier corresponding to only one calculator.

## Features and Functionalities

* **Data Ingestion**: 
  * The system will efficiently read and process data from incoming files within the monitored directory.
* **Qualification Checks**: 
  * Each transaction undergoes qualification checks to ascertain its validity before further processing.
* **Discount Calculation**: 
   * Based on the qualifiers associated with each transaction, the system computes applicable discounts following predefined conditions.
* **Database Interaction**: 
  * Postgres database, operating within a Docker container, serves as the repository for storing processed orders alongside their calculated discounts and final product prices. Integration with Metabase, also within the same Docker container network, facilitates business intelligence operations.
* **Logging Mechanism**: 
  * A comprehensive logger system captures every operation within the system for auditing purposes, ensuring thorough tracking of system activities.
* **Data Lineage Logging**: 
  * To monitor the data lifecycle within the system, a dedicated data lineage logger records any operation within the rule engine model that influences data, facilitating effective monitoring and automation.

## **Architecture Diagram** 
![arch.jpg](src%2Fmain%2Fresources%2Farch.jpg)

## **Implementation Approach**

* Utilize Scala for its robustness and conciseness in handling complex data processing tasks.
* Implement a modular system architecture to ensure scalability and maintainability.
* Using the Functional Programming Approach to implement the system functionalities.
* Employ watcher services to enable real-time detection and processing of incoming files.
* Leverage Docker containers for encapsulating the Postgres database and Metabase, simplifying deployment and management.
* Build a logging system for tracking the system operation and the data lineage.


## Tech Stack and Dependencies

```bash
libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
  "ch.qos.logback" % "logback-classic" % "1.3.5",
  "org.scalatest" %% "scalatest" % "3.2.15" % "test",
  "com.typesafe.slick" %% "slick" % "3.5.0",
  "org.postgresql" % "postgresql" % "42.7.3",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.5.1",
  "com.github.tminglei" %% "slick-pg" % "0.22.1"
  )
```
```bash
CONTAINERS NAMES
scala_project-db-1
scala_project-metabase-1
````

**Scala:** Scala serves as the primary programming language for developing the ETL system. Its functional and object-oriented features enable concise and expressive code for data processing, qualification checks, discount calculation, and database interaction, and are implemented in a functional approach as much as possible I can.

**Watcher Service (Directory Monitoring):** The Scala application incorporates a watcher service for monitoring a designated directory in real-time. This service immediately detects new files added to the directory, triggering data processing workflows upon file arrival. Directory monitoring ensures timely ingestion and processing of incoming data files, facilitating near real-time data processing capabilities.

**Docker:** Docker is utilized for containerization, allowing seamless deployment and management of the PostgreSQL database and Metabase instances.

**PostgreSQL:** PostgreSQL serves as the RDBMS, storing data processed by the Scala application. It provides persistence and retrieval capabilities for the DWH and ETL systems.

**Metabase:** Metabase is employed as the business intelligence and visualization layer. It enables users to analyze and visualize data stored in the PostgreSQL database, facilitating informed decision-making and data exploration.

**Scala Logging:** The Scala Logging library provides logging capabilities for the Scala application, ensuring comprehensive monitoring and troubleshooting of system operations.

**Logback:** Logback is utilized as the logging backend, offering powerful features for configuring log output, formatting, and management.

**Slick:** Slick serves as the functional-relational mapping (FRM) library for interacting with the PostgreSQL database. It enables type-safe database queries and data manipulation operations using idiomatic Scala code.

**PostgreSQL JDBC Driver:** The PostgreSQL JDBC driver allows the Scala application to connect to the PostgreSQL database and execute SQL queries, facilitating data retrieval and storage.

**HikariCP:** HikariCP is integrated with Slick to provide efficient connection pooling, enhancing database performance and scalability.

**Slick-pg:** Slick-pg extends Slick with support for PostgreSQL-specific data types and features, enabling seamless integration with PostgreSQL databases.


## Project Structure
```bash
scala_project
│
├── db
│   └── init_scripts.sql        # SQL scripts for initializing the database
│
├── src
│   └── main
│       ├── configuration
│       │   └── application.conf  # PostgreSQL configuration
│       │
│       ├── generation_data_source
│       │   └── ...               # Directory acting as the source system for appended files
│       │
│       ├── localstorage
│       │   ├── landing_zone     # Directory for landing zone storage
│       │   └── save_zone        # Directory for save zone storage
│       │
│       ├── logs
│       │   ├── rule_engine.log   # Log file for rule engine
│       │   └── data_lineage.log  # Log file for data lineage
│       │
│       ├── resources
│       │   └── logback.xml       # Logback configuration
│       │
│       ├── scala
│       │   ├── filecommunication
│       │   │   ├── FileReader.scala        # Module for reading files
│       │   │   ├── FileStorageWriter.scala # Module for writing files
│       │   │   └── FileWatcher.scala    # Watcher service for file ingestion
│       │   │
│       │   ├── businesslogic
│       │   │   ├── RuleEngine.scala # Module for qualification logic
│       │   │   ├── RefacoredRuleEngine.scala # Module for discount calculation
│       │   │   └── OrderProcessor.scala     # Module for handling communication between components
│       │   │
│       │   ├── businessmodels
│       │   │   ├── order.scala          # Model representing orders
│       │   │   ├── processedorder.scala # Model representing processed orders
│       │   │   └── orderwithdiscount.scala # Model representing orders with discounts
│       │   │
│       │   └── datarepository
│       │       ├── dbconnection.scala # Module for connecting to the database
│       │       └── QueryHandler.scala     # Module for performing database queries using Slick
│       │       └── SlickTables.scala     # Module for represent the data models for db
|                └── TimeConvertor.scala     # Module for performing time/date converisons operations
│       └── ...
│
├── build.sbt                    # SBT file for managing project dependencies
└── docker-compose.yml           # Docker Compose configuration for PostgreSQL and Metabase
```

## Usage

### Prerequisites
Before running the project, ensure you have the following installed:
- [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) 11 or higher
- [Scala Build Tool (SBT)](https://www.scala-sbt.org/download.html)
- [Docker](https://www.docker.com/get-started) (if using Docker for PostgreSQL and Metabase)

### Setup
1. Clone the repository to your local machine:
   ```bash
   git clone https://github.com/your-username/your-repo.git
   ```

2. Navigate to the project root directory:
   ```bash
   cd project_root
   ```

3. Initialize the database by running the SQL scripts:
   ```bash
   psql -U <username> -d <database_name> -a -f db/init_scripts.sql
   ```
   Replace `<username>` and `<database_name>` with your PostgreSQL username and the name of your database.

4. Start PostgreSQL and Metabase using Docker Compose:
   ```bash
   docker-compose up -d
   ```

### Running the Application
1. Compile the Scala code using SBT:
   ```bash
   sbt compile
   ```

2. Run the application:
   ```bash
   sbt run
   ```

### Using the Application
- Once the application is running, and once your triggred the monitored dir generation_data_source with the Orders.csv file in the resources directory it will perform various operations such as file reading, database querying, and processing orders.


### Additional Notes
- Ensure that the PostgreSQL configuration in `src/main/configuration/application.conf` matches your database setup.
- View logs in the `logs` directory to monitor the application's activity.
- Adjust Docker Compose configuration (`docker-compose.yml`) if necessary, especially if you want to customize the PostgreSQL or Metabase settings.

# Demo
[demo.webm](https://github.com/emadeldinadel2022/scala_fp_rule_engine/assets/62083769/e03d2a66-596e-4772-8f2b-51942f19cf67)


## **Appendix**
* https://youtu.be/6uwRajbkaqI?si=xZxCYli-mxJQrgeD
* https://www.baeldung.com/scala/file-io
* https://www.baeldung.com/scala/scala-logging
* https://blog.rockthejvm.com/slick/

