public enum RoomCategory {
    STANDARD(2500),
    DELUXE(4500),
    SUITE(8000);

    private final double pricePerNight;

    RoomCategory(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }
}
