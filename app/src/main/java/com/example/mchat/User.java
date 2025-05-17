package com.example.mchat;
public class User {
    private String name;
    private String surname;
    private String username;
    private String phone;
    private String password;

    public User() {}
    public User(String name, String surname, String username, String phone, String password)
    {
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.phone = phone;
        this.password = password;
    }

    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getUsername() { return username; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }

    @Override
    public String toString() {
        return name + " " + surname;
    }


}