package com.example.mchat;

import java.util.ArrayList;

public class Contact {
    private String name;
    private long phone;
    private boolean chatOpened;
    private String lastMsg;
    private static boolean initialized = false;

    public static ArrayList<Contact> contacts = new ArrayList<>();

    public Contact(String name, long phone)
    {
        this.name = name;
        this.phone = phone;
        chatOpened = false;
        lastMsg = "";
    }

    public String getLastMsg() {
        return lastMsg;
    }
    public boolean isChatOpened() {
        return chatOpened;
    }
    public long getPhone() {
        return phone;
    }
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
    public static void initializeContacts() {
        if(!initialized)
        {
            Contact.contacts.add(new Contact("Milan", 601234567 ));
            Contact.contacts.add(new Contact("Danijela Djordjevic", 652375991 ));
            Contact.contacts.add(new Contact("Srdjan 2", 619904311 ));
            initialized = true;
        }
    }

    public static Contact findContactByName(String name) {
        for(Contact c : contacts) {
            if(c.name.equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

}

