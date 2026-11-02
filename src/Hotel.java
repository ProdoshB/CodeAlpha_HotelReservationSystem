import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Core hotel logic: room inventory, availability search, booking, cancellation and persistence.
 */
public class Hotel {
    private final List<Room> rooms = new ArrayList<>();
    private final List<Reservation> reservations = new ArrayList<>();
    private final Path storage;
    private int nextId = 1001;

    public Hotel(Path storage) {
        this.storage = storage;
        for (int i = 101; i <= 104; i++) rooms.add(new Room(i, RoomCategory.STANDARD));
        for (int i = 201; i <= 203; i++) rooms.add(new Room(i, RoomCategory.DELUXE));
        for (int i = 301; i <= 302; i++) rooms.add(new Room(i, RoomCategory.SUITE));
        load();
    }

    public List<Room> searchAvailable(RoomCategory category, LocalDate in, LocalDate out) {
        validateDates(in, out);
        List<Room> result = new ArrayList<>();
        for (Room room : rooms) {
            if ((category == null || room.getCategory() == category) && isAvailable(room.getNumber(), in, out)) {
                result.add(room);
            }
        }
        return result;
    }

    public boolean isAvailable(int roomNumber, LocalDate in, LocalDate out) {
        for (Reservation r : reservations) {
            if (r.isActive() && r.getRoomNumber() == roomNumber && r.overlaps(in, out)) {
                return false;
            }
        }
        return true;
    }

    public Reservation book(String guest, int roomNumber, LocalDate in, LocalDate out, String method,
                            String cardNumber) {
        validateDates(in, out);
        if (guest == null || guest.isBlank()) {
            throw new IllegalArgumentException("Guest name is required.");
        }
        Room room = findRoom(roomNumber);
        if (room == null) {
            throw new IllegalArgumentException("Room " + roomNumber + " does not exist.");
        }
        if (!isAvailable(roomNumber, in, out)) {
            throw new IllegalStateException("Room " + roomNumber + " is not available for those dates.");
        }
        long nights = ChronoUnit.DAYS.between(in, out);
        double total = nights * room.getCategory().getPricePerNight();
        String txn = Payment.process(method, total, cardNumber);
        Reservation r = new Reservation("BK" + nextId++, guest.trim(), roomNumber, room.getCategory(), in, out,
                total, method, txn, Reservation.Status.CONFIRMED, 0);
        reservations.add(r);
        save();
        return r;
    }

    /** Full refund if cancelled 2+ days before check-in, otherwise 50%. */
    public Reservation cancel(String id) {
        Reservation r = findReservation(id);
        if (r == null) {
            throw new IllegalArgumentException("No booking found with ID " + id);
        }
        if (!r.isActive()) {
            throw new IllegalStateException("Booking is already cancelled.");
        }
        long daysBefore = ChronoUnit.DAYS.between(LocalDate.now(), r.getCheckIn());
        if (daysBefore < 0) {
            throw new IllegalStateException("This stay has already started and cannot be cancelled.");
        }
        double refund = daysBefore >= 2 ? r.getTotalAmount() : r.getTotalAmount() * 0.5;
        r.cancel(refund);
        save();
        return r;
    }

    public Reservation findReservation(String id) {
        for (Reservation r : reservations) {
            if (r.getId().equalsIgnoreCase(id.trim())) {
                return r;
            }
        }
        return null;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    private Room findRoom(int number) {
        for (Room room : rooms) {
            if (room.getNumber() == number) {
                return room;
            }
        }
        return null;
    }

    private void validateDates(LocalDate in, LocalDate out) {
        if (in.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past.");
        }
        if (!out.isAfter(in)) {
            throw new IllegalArgumentException("Check-out must be after check-in.");
        }
    }

    private void save() {
        try {
            List<String> lines = new ArrayList<>();
            for (Reservation r : reservations) {
                lines.add(r.toCsv());
            }
            Files.write(storage, lines);
        } catch (IOException e) {
            System.out.println("Warning: could not save bookings: " + e.getMessage());
        }
    }

    private void load() {
        if (!Files.exists(storage)) {
            return;
        }
        try {
            for (String line : Files.readAllLines(storage)) {
                if (line.isBlank()) continue;
                Reservation r = Reservation.fromCsv(line);
                reservations.add(r);
                int num = Integer.parseInt(r.getId().substring(2));
                if (num >= nextId) nextId = num + 1;
            }
        } catch (IOException | RuntimeException e) {
            System.out.println("Warning: could not read saved bookings: " + e.getMessage());
        }
    }
}
