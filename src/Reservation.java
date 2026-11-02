import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Reservation {
    public enum Status { CONFIRMED, CANCELLED }

    private final String id;
    private final String guestName;
    private final int roomNumber;
    private final RoomCategory category;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final double totalAmount;
    private final String paymentMethod;
    private final String transactionId;
    private Status status;
    private double refundAmount;

    public Reservation(String id, String guestName, int roomNumber, RoomCategory category, LocalDate checkIn,
                       LocalDate checkOut, double totalAmount, String paymentMethod, String transactionId,
                       Status status, double refundAmount) {
        this.id = id;
        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.category = category;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.status = status;
        this.refundAmount = refundAmount;
    }

    public String getId() { return id; }
    public String getGuestName() { return guestName; }
    public int getRoomNumber() { return roomNumber; }
    public RoomCategory getCategory() { return category; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public double getTotalAmount() { return totalAmount; }
    public Status getStatus() { return status; }
    public double getRefundAmount() { return refundAmount; }

    public long nights() {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    public boolean isActive() {
        return status == Status.CONFIRMED;
    }

    /** True if the two date ranges [checkIn, checkOut) overlap. */
    public boolean overlaps(LocalDate in, LocalDate out) {
        return checkIn.isBefore(out) && in.isBefore(checkOut);
    }

    public void cancel(double refund) {
        this.status = Status.CANCELLED;
        this.refundAmount = refund;
    }

    public String toCsv() {
        return String.join("|", id, guestName.replace("|", " "), String.valueOf(roomNumber), category.name(),
                checkIn.toString(), checkOut.toString(), String.valueOf(totalAmount), paymentMethod, transactionId,
                status.name(), String.valueOf(refundAmount));
    }

    public static Reservation fromCsv(String line) {
        String[] p = line.split("\\|");
        return new Reservation(p[0], p[1], Integer.parseInt(p[2]), RoomCategory.valueOf(p[3]),
                LocalDate.parse(p[4]), LocalDate.parse(p[5]), Double.parseDouble(p[6]), p[7], p[8],
                Status.valueOf(p[9]), Double.parseDouble(p[10]));
    }

    public String details() {
        StringBuilder sb = new StringBuilder();
        sb.append("Booking ID   : ").append(id).append('\n');
        sb.append("Guest        : ").append(guestName).append('\n');
        sb.append("Room         : ").append(roomNumber).append(" (").append(category).append(")\n");
        sb.append("Check-in     : ").append(checkIn).append('\n');
        sb.append("Check-out    : ").append(checkOut).append(" (").append(nights()).append(" nights)\n");
        sb.append(String.format("Total paid   : Rs.%.2f via %s%n", totalAmount, paymentMethod));
        sb.append("Transaction  : ").append(transactionId).append('\n');
        sb.append("Status       : ").append(status);
        if (status == Status.CANCELLED) {
            sb.append(String.format(" (refund Rs.%.2f)", refundAmount));
        }
        return sb.toString();
    }
}
