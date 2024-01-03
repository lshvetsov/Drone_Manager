# Task for the position of Java Software Engineer (Summer 2023)
---------------------
# Requirements

## Functional

**Initial**

| REQ |                                     Requirement                                     |                                      Notes                                      |
|:---:|:-----------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------:|
|  1  |                              I can registering a drone                              | We have a fleet of 10 drones but we can add more drones, starting status - IDLE |
|  2  |                      I can load a drone with medication items                       |       Status LOADING in the beginning, LOADED at the end of the operation       |
|  3  |                I can check loaded medication items for a given drone                |                                        -                                        |
|  4  |                     I can checking available drones for loading                     |                               In the IDLE status                                |
|  5  |                  I can check drone battery level for a given drone                  |                              Identifications by id                              |
|  6  |       Prevent the drone from being loaded with more weight that it can carry        |                         Validation for the UC **REQ2**                          |
|  7  | Prevent the drone from being in LOADING state if the battery level is **below 25%** |                         Validation for the UC **REQ2**                          |

**Missed requirements/assumptions**

|  #  |                                             Requirement                                              |                                                    Notes                                                     |
|:---:|:----------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------------------:|
|  8  |                              I need to launch a drone when it's loaded                               |                                    The drone status: LOADED -> DELIVERING                                    |
|  9  |                             I need to be able to receive the drone back                              |                        The drone status: DELIVERING -> IDLE (later RETURNING -> IDLE)                        |
| 10  |        During a flight the drone's status should be changed depending on the delivery status         |               Look at the status table below, *assumption* - out of the current project scope                |
| 11  |                    A drone's battery level should decreased while it in delivery                     |                   Scheduled task for drones on mission - DELIVERING, DELIVERED, RETURNING                    |
| 12  | A drone's should control its charge and stop the mission at the minimum charge needed to return back |                               *Assumption* - out of the current project scope                                |
| 13  |                         We need to audit changes in the drone's change level                         |             For now - just console output: *"Current change level of drone {id} is {percent}%%"*             |
| 14  |                     We need to keep track of what medicines have been delivered                      | We introduce the new entity *ORDER* which matches medicines and drones, *restrictions* - one drone per order |

### Status model

```plantuml
@startuml
IDLE: the drone is at the base
LOADING: the drone is in the loading process
LOADED: the drone is ready to be launched
DELIVERING: the drone is on a mission, and the package hasn't been delivered
DELIVERED: the drone is on a mission, and the package has been delivered
RETURNING: the drone is on a mission returning back. 

[*] --> IDLE
IDLE -down-> LOADING: loading process started
LOADING -up-> IDLE  
LOADING -down-> LOADED: loading process finished
LOADED -up-> LOADING
LOADED -down-> DELIVERING: drone is launched
DELIVERING -down-> DELIVERED: order is delivered
DELIVERING -down-> RETURNING: drone is returning without delivering
DELIVERED -down-> RETURNING: drone is returning after the delivery
RETURNING -up-> IDLE: drone has returned
@enduml
```
![Status_model](/docs/status_model.png)

## Non-functional*

- [*] Input/output data must be in JSON format;
- [*] Your project must be buildable and runnable;
- [*] Your project must have a README file with build/run/test instructions (use DB that can be run locally, e.g. in-memory, via container);
- [*] Any data required by the application to run (e.g. reference tables, dummy data) must be preloaded in the database;
- [*] Unit tests;
- [*] Use a framework of your choice, but popular, up-to-date, and long-term support versions are recommended.

## Backlog
- add dto level and its own validation
- add the number to the medicines in the order
- integration test
- delete medication | order

- replace real deletion with changing status
- add schema initialization script or migration tool (liquibase | flyway)

## Out of scope

1) At this stage, we don't consider interaction with drone while they are on mission so the drone status is managed manually (it's a black box from the moment we launch the drone to the moment it returns).

# Architecture

**Technology**
- Java 17
- Spring boot 3.1.2
- Local H2 DB

**Prerequisites**
- We don't have any requirements for performance and data durability so for the MVP purpose we are going to use embedded H2 DB.
- We don't implement real communication with drones so for now we just run a job to update a charge level of drones. 
    
**Current architecture**
```plantuml
@startuml
package "Application" {
  API- [MainComponent]
  [ChargeUpdateJob]
}

database "H2" {
   [DB]
}


[MainComponent] --> [DB]
[ChargeUpdateJob] --> [DB]
@enduml
```
![Current architecture](/docs/current_architecture.png)

**Target architecture (draft)**

```plantuml
@startuml
skinparam linetype ortho

package "Application" {
  [DroneManagementModule]
  [DroneMessageProcessor]
  [OrderManagementModule]
  [FleetManagementModule]
  [DrugManagementModule]
}

cloud "Drone" {
  [DroneSoftware]
}

cloud "Users" {
  [DroneOperator]
}


database "DataBase" {
   [DrugDB]
   [FleetDB]
   [OrderDB]
}

database "In memory" {
   [IMDB]
}

database "Queue" {
   [Kafka]
}

[DroneOperator] --> [OrderManagementModule]: 1
[DroneOperator] --> [FleetManagementModule]: 2
[DroneOperator]--> [DrugManagementModule]: 3
[OrderManagementModule] --> [OrderDB]
[FleetManagementModule] --> [FleetDB]
[DrugManagementModule] --> [DrugDB]
[OrderManagementModule] --> [DroneManagementModule]: 4
[DroneSoftware] ..> [Kafka]: 5
[DroneMessageProcessor]..>[Kafka]: 6
[DroneMessageProcessor] --> [IMDB]: 6
[DroneManagementModule] --> [IMDB]
[DroneManagementModule] ..> [DroneSoftware]: 7
@enduml
```
![Draft of the target architecture](/docs/target_architecture.png)

1) We are going to separate interfaces for users (operators) and drones.
2) The DB is responsible for order and fleet management whereas the IMDB is responsible for data from drones (charge level, GPS coordinates, etc).
3) Modules: 
  - DroneManagementModule - manage communication with drones (calculate when to return, send commands) -> probably later will be split into several modules (commands, video processing, navigation, etc)
  - DroneMessageProcessor - process heartbeat messages from drones (state: charge level, GPS coordinates, etc)
  - OrderManagementModule - manage orders (load and launch drones)
  - FleetManagementModule - manage the drone fleet (add, remove, replace, change metadata, get idle etc)
  - DrugManagementModule - manage medicines
4) API and operations
   - (1) sync order management
   - (2) sync fleet management 
   - (3) sync drug management
   - (4) notification on the drone launch
   - (5) drones heartbeats
   - (6) async processing heartbeat messages from drones (update IMDB)
   - (7) commands to drones

# DB

```plantuml
@startuml
' hide the spot
' hide circle

' avoid problems with angled crows feet
skinparam linetype ortho

entity "Drone" as drone {
  *serial_number : number (100 character max)
  --
  *model: text (enum)
  *weight_limit: number (0 - 500)
  *battery_capacity (0-100)
  *state: text (enum)
}

entity "Medication" as medication{
  *id : number <<generated>>
  --
  *name: text
  *weight: number
  *code: text
  image: text (URL)
}

entity "Order" as order {
  *order_id: number <<generated>>
  --
  *drone_id: number <<FK>>
  *state: text (enum)
  *customer_name
}


drone ||..|| order
medication }|..|| order_medicine
order }|..|| order_medicine
@enduml
```
![DB](/docs/DB.png)

# API

Open API documentation are available at: ```localhost:{{port}}/swagger-ui/index.html``` 

# Build & Run

1) After the first application run, it need to set ```spring.jpa.hibernate.ddl-auto=valuate``` in ```application.yml```. 
2) Run ```mvn package```
3) Run ```java -jar .\DEV_DRONES-373200e7-c296-004f-96a9-410418c98c47\target\DroneManager-0.0.2-SNAPSHOT.jar```.                  



