import java.util.*;

class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

class BookingRequestQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) {
        queue.add(r);
    }

    public Reservation getRequest() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

class RoomInventory {
    private Map<String, Integer> rooms = new HashMap<>();
    private Map<String, Integer> counters = new HashMap<>();

    public RoomInventory() {
        rooms.put("Single", 5);
        rooms.put("Double", 3);
        rooms.put("Suite", 2);

        counters.put("Single", 1);
        counters.put("Double", 1);
        counters.put("Suite", 1);
    }

    public boolean allocateRoom(String roomType) {
        if (rooms.getOrDefault(roomType, 0) > 0) {
            rooms.put(roomType, rooms.get(roomType) - 1);
            return true;
        }
        return false;
    }

    public String generateRoomId(String roomType) {
        int id = counters.get(roomType);
        counters.put(roomType, id + 1);
        return roomType + "-" + id;
    }

    public int getAvailability(String roomType) {
        return rooms.getOrDefault(roomType, 0);
    }

    public Map<String, Integer> getAll() {
        return rooms;
    }
}

class RoomAllocationService {
    public void allocateRoom(Reservation reservation, RoomInventory inventory) {
        String type = reservation.getRoomType();
        if (inventory.allocateRoom(type)) {
            String roomId = inventory.generateRoomId(type);
            System.out.println("Booking confirmed for Guest: " +
                    reservation.getGuestName() + ", Room ID: " + roomId);
        }
    }
}

class ConcurrentBookingProcessor implements Runnable {
    private BookingRequestQueue bookingQueue;
    private RoomInventory inventory;
    private RoomAllocationService allocationService;

    public ConcurrentBookingProcessor(
            BookingRequestQueue bookingQueue,
            RoomInventory inventory,
            RoomAllocationService allocationService
    ) {
        this.bookingQueue = bookingQueue;
        this.inventory = inventory;
        this.allocationService = allocationService;
    }

    @Override
    public void run() {
        while (true) {
            Reservation reservation;

            synchronized (bookingQueue) {
                if (bookingQueue.isEmpty()) break;
                reservation = bookingQueue.getRequest();
            }

            synchronized (inventory) {
                allocationService.allocateRoom(reservation, inventory);
            }
        }
    }
}

public class BookMyStayApp {
    public static void main(String[] args) {

        System.out.println("Concurrent Booking Simulation");

        BookingRequestQueue bookingQueue = new BookingRequestQueue();
        RoomInventory inventory = new RoomInventory();
        RoomAllocationService allocationService = new RoomAllocationService();

        bookingQueue.addRequest(new Reservation("Abhi", "Single"));
        bookingQueue.addRequest(new Reservation("Vanmathi", "Double"));
        bookingQueue.addRequest(new Reservation("Kural", "Suite"));
        bookingQueue.addRequest(new Reservation("Subha", "Single"));

        Thread t1 = new Thread(new ConcurrentBookingProcessor(
                bookingQueue, inventory, allocationService));

        Thread t2 = new Thread(new ConcurrentBookingProcessor(
                bookingQueue, inventory, allocationService));

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            System.out.println("Thread execution interrupted.");
        }

        System.out.println("\nRemaining Inventory:");
        for (Map.Entry<String, Integer> entry : inventory.getAll().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}