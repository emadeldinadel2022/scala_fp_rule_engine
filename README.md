# Rule Engine Scala Project
## Overview:

The project aims to develop a Scala-based ETL (Extract, Transform, Load) system equipped with a rule engine. The system will monitor a designated directory for incoming data files, treating this directory as the primary source layer for data generation. Upon detecting a new file, the system will immediately initiate processing through a watcher service. The objective is to ingest the data, apply qualification checks to ensure the validity of each transaction, and subsequently calculate discounts based on predetermined conditions. The qualifiers are intricately linked to specific discount calculators, with each qualifier corresponding to only one calculator.


![441873137_409277131922729_1624702809289776453_n.jpg](..%2F..%2FDownloads%2F441873137_409277131922729_1624702809289776453_n.jpg)
## Functionalities:

Data Ingestion: The system will efficiently read and process data from incoming files within the monitored directory.
Qualification Checks: Each transaction undergoes qualification checks to ascertain its validity before further processing.
Discount Calculation: Based on the qualifiers associated with each transaction, the system computes applicable discounts following predefined conditions.
Database Interaction: Postgres database, operating within a Docker container, serves as the repository for storing processed orders alongside their calculated discounts and final product prices. Integration with Metabase, also within the same Docker container network, facilitates business intelligence operations.
Logging Mechanism: A comprehensive logger system captures every operation within the system for auditing purposes, ensuring thorough tracking of system activities.
Data Lineage Logging: To monitor the data lifecycle within the system, a dedicated data lineage logger records any operation within the rule engine model that influences data, facilitating effective monitoring and automation.

## Implementation Approach:

Utilize Scala for its robustness and conciseness in handling complex data processing tasks.
Employ watcher services to enable real-time detection and processing of incoming files.
Implement a modular system architecture to ensure scalability and maintainability.
Leverage Docker containers for encapsulating the Postgres database and Metabase, simplifying deployment and management.



## Appendix:
https://youtu.be/6uwRajbkaqI?si=xZxCYli-mxJQrgeD
https://www.baeldung.com/scala/file-io
https://www.baeldung.com/scala/scala-logging
https://blog.rockthejvm.com/slick/

