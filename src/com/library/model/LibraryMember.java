package com.library.model;

public abstract class LibraryMember {
    private String id;
    private String name;
    private String contact;

    public LibraryMember(String id, String name, String contact) {
        this.id = id;
        this.name = name;
        this.contact = contact;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public abstract double calculateFine(int daysOverdue);
    
    public abstract String getMemberType();

    @Override
    public String toString() {
        return String.format("Member [ID: %s, Name: %s, Type: %s, Contact: %s]",
                id, name, getMemberType(), contact);
    }
}
