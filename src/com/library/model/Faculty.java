package com.library.model;

public class Faculty extends LibraryMember {
    private static final double FINE_RATE = 1.00;
    private static final double MAX_FINE = 20.00;

    public Faculty(String id, String name, String contact) {
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
        return "Faculty";
    }
}
