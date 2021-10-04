# Delivery Api Service
Simple Delivery Service with API, Multithreading, Geocoding by GoogleMaps and checks holidays by HolidayAPI

A simple example of an Order and Delivery processing service has been implemented, taking into account multithreading and work with different APIs inside.
The service is implemented in JAVA using Spring Boot and REST. H2 acts as a database, and interaction with it is performed using Hibernate.
Implemented some features of GoogleMaps geocoding and HolidaysAPI. 

**Implementation features**
* Each timeslot can be used for 2 deliveries (max)
* System supports up to 16 deliveries per day (up to 8 timeslot with 2 deliveries, or 16 timeslots with 1)
* Courier API - will receive JSON file with the available timeslots for the next week and fill DB with them 
* In case the user does not want to use the Courier API - implemented autocomplete timeslots for the next week, which can be connected if desired (service.TimeslotsBase)
* Checking that the timeslot does not fall on the day off is carried out using the service https://holidayapi.com/docs
* Added processing of concurrent requests (two or more consumers trying to reserve the same timeslot) and Recovery booking requests if there are a some issues

**Models description**
* Timeslot - a delivery window containing start time, end time, one from supported addresses(for this example I decided to support just addresses from Tel-Aviv), 1-2 delivery owners (depends on how many deliveries will be in the timeslot)
* Delivery - contains status, selected timeslot, Delivery owner's name, generated ID
* Address - resolved and formatted by https://developers.google.com/maps/documentation/geocoding/overview + ID
* BookingDeliveryData - intermediate structure with data for booking delivery (DTO - BookingDeliveryData - Delivery). Used because of handling concurrent requests.
* Status - enum with statuses of Delivery. From NEW (booking try), and ORDERED (booking success) to COMPLETE (delivery finished)
* DTO (AddressDto, CourierApiObject, DeliveryDto) - objects for receiving data from user by controllers in 'api'-package

**Controller Methods**
* POST /timeslots - retrieve all available timeslots for a formatted/structured address
* POST /resolve-address - resolves a single line non-structured address into a structured one
* POST /deliveries - try to book a delivery (will be checked by mechanism of handling concurrent requests)
* POST /deliveries/{DELIVERY_ID}/complete - mark desired delivery as completed
* DELETE /deliveries/{DELIVERY_ID} - cancel a desired delivery
* GET /deliveries/daily - retrieve a list of all today’s deliveries
* GET /deliveries/weekly - retrieve a list of all deliveries for current week
* POST /courier/set/timeslots - send a list with available timeslots for the next week

**Technologies used**
    
Java + Maven + Spring Boot MVC + REST + H2 (as Database) + Hibernate + Lombok + HolidayAPI + GoogleMaps geocoding
