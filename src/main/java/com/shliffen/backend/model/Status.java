package com.shliffen.backend.model;

/**
 * I have simulated several order fulfillment statuses. Where NEW is the status of only the created order, but not
 * confirmed by the system and not accepted into the database of future deliveries. After checking by the system
 * (taking into account checking the simultaneous booking of the same timeslot) in the special stream
 * BookingTimeSlotTask, the status of the order becomes ORDERED. This is followed by intermediate statuses.  And
 * finally, at the end - the order is considered COMPLETE (used in DeliveryController)
 */
public enum Status {

    NEW,
    ORDERED,
    PREPARED_FOR_SHIPMENT,
    DELIVERY,
    COMPLETE;
}
