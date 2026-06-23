package com.library.util;

import com.library.model.*;
import com.library.service.LibraryService;
import java.io.File;
import java.time.LocalDate;

public class VerificationTest {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("      Running System Verification Tests          ");
        System.out.println("=================================================");

        // Setup clean files for verification test
        String testBooks = "data/test_books.csv";
        String testMembers = "data/test_members.csv";
        String testTransactions = "data/test_transactions.csv";

        // Delete any leftover test files
        new File(testBooks).delete();
        new File(testMembers).delete();
        new File(testTransactions).delete();

        LibraryService service = new LibraryService(testBooks, testMembers, testTransactions);

        // 1. Setup Test Books and Members
        Book book1 = new Book("B001", "Introduction to Java", "Author A", "11111", "Tech", true);
        Book book2 = new Book("B002", "Effective OOP Designs", "Author B", "22222", "Tech", true);

        // Student fine: $2.00/day, cap $50.00
        LibraryMember student = new Student("S101", "Alice Student", "123456");
        // Faculty fine: $1.00/day, cap $20.00
        LibraryMember faculty = new Faculty("F202", "Dr. Bob Faculty", "654321");

        service.addBook(book1);
        service.addBook(book2);
        service.addMember(student);
        service.addMember(faculty);

        System.out.println("1. Test Data Setup: Done.");
        System.out.println("   Student: " + student);
        System.out.println("   Faculty: " + faculty);

        // 2. Test Normal Issue & Return (No Fine)
        System.out.println("\n2. Testing normal issue and return (no fine)...");
        LocalDate issueDate = LocalDate.of(2026, 6, 1);
        LocalDate returnDateNoFine = LocalDate.of(2026, 6, 10); // Within 14 days (due June 15)

        service.issueBook("S101", "B001", issueDate);
        double studentFine1 = service.returnBook("B001", returnDateNoFine);
        System.out.printf("   Student Alice returned B001. Fine: $%.2f (Expected: $0.00)%n", studentFine1);

        // 3. Test Overdue Return (Polymorphism Proof)
        System.out.println("\n3. Testing overdue return (10 days late)...");
        // Issue June 1, Due June 15
        service.issueBook("S101", "B001", issueDate);
        service.issueBook("F202", "B002", issueDate);

        LocalDate returnDate10DaysLate = LocalDate.of(2026, 6, 25); // 10 days late
        double studentFine2 = service.returnBook("B001", returnDate10DaysLate);
        double facultyFine2 = service.returnBook("B002", returnDate10DaysLate);

        System.out.printf("   Student Alice (10 days late): Fine calculated = $%.2f (Expected: $20.00)%n", studentFine2);
        System.out.printf("   Faculty Bob (10 days late): Fine calculated = $%.2f (Expected: $10.00)%n", facultyFine2);

        // Verification checks
        if (studentFine2 == 20.00 && facultyFine2 == 10.00) {
            System.out.println("   [SUCCESS] Polymorphic fine rate calculation works!");
        } else {
            System.out.println("   [FAILED] Polymorphic fine rate calculation is incorrect.");
        }

        // 4. Test Fine Cap Overdue Return (Max Caps)
        System.out.println("\n4. Testing overdue return with fine caps (40 days late)...");
        // Issue June 1, Due June 15
        service.issueBook("S101", "B001", issueDate);
        service.issueBook("F202", "B002", issueDate);

        LocalDate returnDate40DaysLate = LocalDate.of(2026, 7, 25); // 40 days late
        // Student: 40 * $2 = $80, capped at $50
        double studentFine3 = service.returnBook("B001", returnDate40DaysLate);
        // Faculty: 40 * $1 = $40, capped at $20
        double facultyFine3 = service.returnBook("B002", returnDate40DaysLate);

        System.out.printf("   Student Alice (40 days late): Fine calculated = $%.2f (Expected: $50.00 - capped)%n", studentFine3);
        System.out.printf("   Faculty Bob (40 days late): Fine calculated = $%.2f (Expected: $20.00 - capped)%n", facultyFine3);

        // Verification checks
        if (studentFine3 == 50.00 && facultyFine3 == 20.00) {
            System.out.println("   [SUCCESS] Polymorphic maximum fine caps work!");
        } else {
            System.out.println("   [FAILED] Polymorphic maximum fine caps are incorrect.");
        }

        // Clean up test files
        new File(testBooks).delete();
        new File(testMembers).delete();
        new File(testTransactions).delete();
        System.out.println("\nCleaned up verification test data files.");
        System.out.println("=================================================");
    }
}
