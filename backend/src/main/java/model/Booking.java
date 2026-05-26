package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Booking {
    private int bookingID;
    private int customerID;
    private int roomID;
    private int hotelID;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    public Booking() {}

    public Booking(int bookingID, int customerID, int roomID, LocalDate checkInDate, LocalDate checkOutDate) {
        this.bookingID = bookingID;
        this.customerID = customerID;
        this.roomID = roomID;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    public int getBookingID()                        { return bookingID; }
    public void setBookingID(int bookingID)          { this.bookingID = bookingID; }

    public int getCustomerID()                       { return customerID; }
    public void setCustomerID(int customerID)        { this.customerID = customerID; }

    public int getRoomID()                           { return roomID; }
    public void setRoomID(int roomID)                { this.roomID = roomID; }

    public int getHotelID()                          { return hotelID; }
    public void setHotelID(int hotelID)              { this.hotelID = hotelID; }

    public LocalDate getCheckInDate()                { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate){ this.checkInDate = checkInDate; }

    public LocalDate getCheckOutDate()                       { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate)      { this.checkOutDate = checkOutDate; }

    public long getNights() {
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }
}
