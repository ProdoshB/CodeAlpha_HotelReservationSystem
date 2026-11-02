public class Room {
    private final int number;
    private final RoomCategory category;

    public Room(int number, RoomCategory category) {
        this.number = number;
        this.category = category;
    }

    public int getNumber() {
        return number;
    }

    public RoomCategory getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return String.format("Room %d (%s, Rs.%.0f/night)", number, category, category.getPricePerNight());
    }
}
