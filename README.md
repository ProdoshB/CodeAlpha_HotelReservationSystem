# CodeAlpha_HotelReservationSystem

Console-based hotel room reservation system in Java, built for the **CodeAlpha Java Programming Internship** (Task 4).

## Features
- **Room categories:** Standard (Rs.2500), Deluxe (Rs.4500), Suite (Rs.8000) per night; 9 rooms in total
- **Search** available rooms by category and date range (overlapping bookings are blocked)
- **Book** a room with a simulated payment (Card / UPI / Cash) and get a transaction ID
- **Cancel** a booking: full refund if 2+ days before check-in, otherwise 50%
- **View** booking details or list all bookings
- **Persistence:** bookings are saved to `bookings.csv` using File I/O and reloaded on start
- OOP design: `Room`, `RoomCategory`, `Reservation`, `Hotel`, `Payment`, `HotelApp`

## Run
Requires JDK 17+.

```bash
javac -d out src/*.java
java -cp out HotelApp
```

Date format is `yyyy-MM-dd`. To see a simulated declined payment, use a card number starting with `0000`.
Payments are simulated only; card numbers are never stored.

## Project structure
```
src/RoomCategory.java  - enum of room types and prices
src/Room.java          - a physical room
src/Reservation.java   - booking record, CSV (de)serialisation
src/Payment.java       - simulated payment gateway
src/Hotel.java         - availability, booking, cancellation, file persistence
src/HotelApp.java      - console menu
```

Author: Prodosh Bhattacharya
