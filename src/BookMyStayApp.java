import java.util.*;

class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

class RoomInventory {
    private Set<String> validRoomTypes;

    public RoomInventory() {
        validRoomTypes = new HashSet<>();
        validRoomTypes.add("Single");
        validRoomTypes.add("Double");
        validRoomTypes.add("Suite");
    }

    public boolean isValidRoomType(String roomType) {
        return validRoomTypes.contains(roomType);
    }
}

class BookingRequestQueue {
    private Queue<String> queue;

    public BookingRequestQueue() {
        queue = new LinkedList<>();
    }

    public void addRequest(String request) {
        queue.add(request);
    }
}

class ReservationValidator {
    public void validate(String guestName, String roomType, RoomInventory inventory)
            throws InvalidBookingException {

        if (guestName == null || guestName.trim().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }

        if (!inventory.isValidRoomType(roomType)) {
            throw new InvalidBookingException("Invalid room type selected.");
        }
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {

        System.out.println("Booking Validation");

        Scanner scanner = new Scanner(System.in);

        RoomInventory inventory = new RoomInventory();
        ReservationValidator validator = new ReservationValidator();
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        try {
            System.out.print("Enter guest name: ");
            String guestName = scanner.nextLine();

            System.out.print("Enter room type (Single/Double/Suite): ");
            String roomType = scanner.nextLine();

            validator.validate(guestName, roomType, inventory);

            bookingQueue.addRequest(guestName + " - " + roomType);

            System.out.println("Booking successful!");

        } catch (InvalidBookingException e) {
            System.out.println("Booking failed: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}