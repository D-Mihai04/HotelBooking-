package model;

import java.util.ArrayList;

public class Hotel {
    private int id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private ArrayList<Room> rooms;

    public Hotel() {}

    public Hotel(int id, ArrayList<Room> rooms, String email, String phone, String address, String name) {
        this.id = id;
        this.rooms = rooms;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.name = name;
    }

    public int getId()                       { return id; }
    public void setId(int id)                { this.id = id; }

    public String getName()                  { return name; }
    public void setName(String name)         { this.name = name; }

    public String getAddress()               { return address; }
    public void setAddress(String address)   { this.address = address; }

    public String getPhone()                 { return phone; }
    public void setPhone(String phone)       { this.phone = phone; }

    public String getEmail()                 { return email; }
    public void setEmail(String email)       { this.email = email; }

    public ArrayList<Room> getRooms()        { return rooms; }
    public void setRooms(ArrayList<Room> rooms) { this.rooms = rooms; }
}
