import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Console UI for the Hotel Reservation System.
 */
public class HotelApp {
    private static final Scanner in = new Scanner(System.in);
    private static final Hotel hotel = new Hotel(Path.of("bookings.csv"));

    public static void main(String[] args) {
        System.out.println("=== CodeAlpha Grand Hotel - Reservation System ===");
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("1. Search available rooms");
            System.out.println("2. Book a room");
            System.out.println("3. Cancel a reservation");
            System.out.println("4. View booking details");
            System.out.println("5. List all bookings");
            System.out.println("0. Exit");
            switch (readLine("Choose an option: ")) {
                case "1" -> search();
                case "2" -> book();
                case "3" -> cancel();
                case "4" -> view();
                case "5" -> listAll();
                case "0" -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
        System.out.println("Thank you for choosing CodeAlpha Grand Hotel!");
    }

    private static void search() {
        try {
            RoomCategory category = readCategory(true);
            LocalDate checkIn = readDate("Check-in  (yyyy-MM-dd): ");
            LocalDate checkOut = readDate("Check-out (yyyy-MM-dd): ");
            List<Room> rooms = hotel.searchAvailable(category, checkIn, checkOut);
            if (rooms.isEmpty()) {
                System.out.println("No rooms available for those dates.");
            } else {
                System.out.println("Available rooms:");
                rooms.forEach(r -> System.out.println("  " + r));
            }
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void book() {
        try {
            String guest = readLine("Guest name: ");
            RoomCategory category = readCategory(false);
            LocalDate checkIn = readDate("Check-in  (yyyy-MM-dd): ");
            LocalDate checkOut = readDate("Check-out (yyyy-MM-dd): ");
            List<Room> rooms = hotel.searchAvailable(category, checkIn, checkOut);
            if (rooms.isEmpty()) {
                System.out.println("Sorry, no " + category + " rooms available for those dates.");
                return;
            }
            System.out.println("Available rooms:");
            rooms.forEach(r -> System.out.println("  " + r));
            int roomNumber = Integer.parseInt(readLine("Enter room number to book: "));

            System.out.println("Payment method: 1) CARD  2) UPI  3) CASH");
            String method = switch (readLine("Choose: ")) {
                case "1" -> "CARD";
                case "2" -> "UPI";
                case "3" -> "CASH";
                default -> throw new IllegalArgumentException("Invalid payment method.");
            };
            String card = method.equals("CARD") ? readLine("Card number (16 digits, simulated): ") : null;

            Reservation r = hotel.book(guest, roomNumber, checkIn, checkOut, method, card);
            System.out.println("\nPayment successful! Booking confirmed.");
            System.out.println(r.details());
        } catch (NumberFormatException e) {
            System.out.println("Error: please enter a valid number.");
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void cancel() {
        try {
            Reservation r = hotel.cancel(readLine("Booking ID to cancel: "));
            System.out.println("Booking cancelled.");
            System.out.println(r.details());
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void view() {
        Reservation r = hotel.findReservation(readLine("Booking ID: "));
        System.out.println(r == null ? "No booking found." : r.details());
    }

    private static void listAll() {
        if (hotel.getReservations().isEmpty()) {
            System.out.println("No bookings yet.");
            return;
        }
        System.out.printf("%-8s %-16s %-6s %-9s %-11s %-11s %-10s%n", "ID", "Guest", "Room", "Category", "Check-in",
                "Check-out", "Status");
        for (Reservation r : hotel.getReservations()) {
            System.out.printf("%-8s %-16s %-6d %-9s %-11s %-11s %-10s%n", r.getId(), r.getGuestName(),
                    r.getRoomNumber(), r.getCategory(), r.getCheckIn(), r.getCheckOut(), r.getStatus());
        }
    }

    private static RoomCategory readCategory(boolean allowAny) {
        System.out.println("Room category: 1) STANDARD  2) DELUXE  3) SUITE" + (allowAny ? "  4) ANY" : ""));
        return switch (readLine("Choose: ")) {
            case "1" -> RoomCategory.STANDARD;
            case "2" -> RoomCategory.DELUXE;
            case "3" -> RoomCategory.SUITE;
            case "4" -> {
                if (!allowAny) throw new IllegalArgumentException("Please choose a specific category.");
                yield null;
            }
            default -> throw new IllegalArgumentException("Invalid category.");
        };
    }

    private static LocalDate readDate(String prompt) {
        try {
            return LocalDate.parse(readLine(prompt));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date. Use the format yyyy-MM-dd.");
        }
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        if (!in.hasNextLine()) {
            return "0";
        }
        return in.nextLine().trim();
    }
}
