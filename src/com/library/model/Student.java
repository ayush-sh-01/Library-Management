package com.library.model;

public class Student extends LibraryMember {
    private static final double FINE_RATE = 2.00;
    private static final double MAX_FINE = 50.00;

    public Student(String id, String name, String contact) {
        super(id, name, contact);
    }

    @Override
    public double calculateFine(int daysOverdue) {
        if (daysOverdue <= 0) {
            return 0.0;
        }
        double fine = daysOverdue * FINE_RATE;
        return Math.min(fine, MAX_FINE);
    }

    @Override
    public String getMemberType() {
        return "Student";
    }
}
