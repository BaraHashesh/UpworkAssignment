# Simple Ticketing System

## getNextAvailableTicket
This is implemented under the /hall/{hall_id}/nextAvailableSeat API
Each Seat is handled as a full reservation 

## Concurrency
To avoid race conditions & booking of adjusting seats a lock is introduced
Into the DB elements via LockModeType.PESSIMISTIC_WRITE 

Note as performance of DB locking could be considered somewhat negatively
It's possible to do one of the following:
     
    1. If we only have a single instance we can simply move the locking to an in memory cache/map.
    2. If we have multiple instances we may need to move away to using a high perfomance cache service such as 
    Redis to handle locks.

## Scalability
Given the provided description/possible load the current implementation should be sufficient.
However if load becomes a problem it could be possible to solve this via multiple ways.
    
    If load becomes very large & other solutions are inpracticle we could move from real time responses to a callback based system. 
    Such as requests for the Next Available seats are moved from the foreground to the background.
    And sending the result to the user via a predefined callback method such as an email.