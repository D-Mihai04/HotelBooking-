package model;

public class Room {
    private int id;
    private int hotelId;
    private String roomNumber;
    private String type;
    private int capacity;
    private double pricePerNight;
    private boolean available;

    public Room() {}

    public Room(int id, int hotelId, String roomNumber, String type, int capacity, double pricePerNight, boolean available) {
        this.id = id;
        this.hotelId = hotelId;
        this.roomNumber = roomNumber;
        this.type = type;
        this.capacity = capacity;
        this.pricePerNight = pricePerNight;
        this.available = available;
    }

    public int getId()                              { return id; }
    public void setId(int id)                       { this.id = id; }

    public int getHotelId()                         { return hotelId; }
    public void setHotelId(int hotelId)             { this.hotelId = hotelId; }

    public String getRoomNumber()                   { return roomNumber; }
    public void setRoomNumber(String roomNumber)    { this.roomNumber = roomNumber; }

    public String getType()                         { return type; }
    public void setType(String type)               { this.type = type; }

    public int getCapacity()                        { return capacity; }
    public void setCapacity(int capacity)           { this.capacity = capacity; }

    public double getPricePerNight()                { return pricePerNight; }
    public void setPricePerNight(double p)          { this.pricePerNight = p; }

    public boolean isAvailable()                    { return available; }
    public void setAvailable(boolean available)     { this.available = available; }
}
