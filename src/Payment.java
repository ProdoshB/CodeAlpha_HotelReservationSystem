import java.util.UUID;

/**
 * Simulated payment gateway. No real payment is made and card numbers are never stored.
 */
public class Payment {

    public static String process(String method, double amount, String cardNumber) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Invalid payment amount.");
        }
        if (method.equals("CARD")) {
            String digits = cardNumber == null ? "" : cardNumber.replaceAll("\\s", "");
            if (!digits.matches("\\d{16}")) {
                throw new IllegalArgumentException("Card number must be 16 digits.");
            }
            if (digits.startsWith("0000")) {
                throw new IllegalStateException("Payment declined by bank (simulated).");
            }
        }
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
