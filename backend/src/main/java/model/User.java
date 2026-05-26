package model;

public class User {
    private int id;
    private String name;
    private String password;
    private String email;
    private String role;
    private int hotelId;

    public User() {}

    public User(int id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public int getId()                       { return id; }
    public void setId(int id)                { this.id = id; }

    public String getName()                  { return name; }
    public void setName(String name)         { this.name = name; }

    public String getPassword()              { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail()                 { return email; }
    public void setEmail(String email)       { this.email = email; }

    public String getRole()                  { return role; }
    public void setRole(String role)         { this.role = role; }

    public int getHotelId()              { return hotelId; }
    public void setHotelId(int hotelId)  { this.hotelId = hotelId; }
}
