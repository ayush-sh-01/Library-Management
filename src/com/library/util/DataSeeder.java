package com.library.util;

import com.library.model.*;
import com.library.io.FileHandler;
import java.io.IOException;
import java.util.*;

public class DataSeeder {

    private static final String[] ADJECTIVES = {
        "Silent", "Hidden", "Ancient", "Forgotten", "Digital", "Quantum", "Advanced", 
        "Practical", "Modern", "Beautiful", "Dark", "Golden", "Ultimate", "Lost", 
        "Global", "Computational", "Introduction to", "Designing", "Mastering"
    };

    private static final String[] NOUNS = {
        "Whispers", "Journey", "History", "Secrets", "Patterns", "Physics", "Chemistry", 
        "Principles", "Algorithms", "Legends", "Chronicles", "Dreams", "Systems", 
        "Networks", "Engines", "Future", "Universe", "Art", "Science", "Database"
    };

    private static final String[] FIRST_NAMES = {
        "James", "Mary", "John", "Patricia", "Robert", "Jennifer", "Michael", "Linda", 
        "William", "Elizabeth", "David", "Barbara", "Richard", "Susan", "Joseph", 
        "Jessica", "Thomas", "Sarah", "Charles", "Karen"
    };

    private static final String[] LAST_NAMES = {
        "Smith", "Johnson", "Williams", "Brown", "Jones", "Miller", "Davis", "Garcia", 
        "Rodriguez", "Wilson", "Martinez", "Anderson", "Taylor", "Thomas", "Hernandez", 
        "Moore", "Martin", "Jackson", "Thompson", "White"
    };

    private static final String[] CATEGORIES = {
        "Computer Science", "Physics", "Mathematics", "Fiction", "History", 
        "Biography", "Business", "Chemistry", "Engineering", "Literature"
    };

    public static void main(String[] args) {
        String booksPath = "data/books.csv";
        String membersPath = "data/members.csv";
        
        if (args.length >= 2) {
            booksPath = args[0];
            membersPath = args[1];
        }

        System.out.println("Starting seeding process...");
        try {
            seedData(booksPath, membersPath, 1100, 1000);
            System.out.println("Seeding completed successfully!");
        } catch (IOException e) {
            System.err.println("Seeding failed: " + e.getMessage());
        }
    }

    public static void seedData(String booksPath, String membersPath, int bookCount, int memberCount) throws IOException {
        Random random = new Random(42); // Fixed seed for reproducible data

        // 1. Generate Books
        List<Book> books = new ArrayList<>();
        Set<String> uniqueTitles = new HashSet<>();
        
        for (int i = 1; i <= bookCount; i++) {
            String id = "B" + String.format("%04d", i);
            
            // Construct a realistic title
            String title;
            do {
                String adj = ADJECTIVES[random.nextInt(ADJECTIVES.length)];
                String noun = NOUNS[random.nextInt(NOUNS.length)];
                title = adj + " " + noun;
                if (random.nextDouble() > 0.6) {
                    title += " (Vol. " + (random.nextInt(3) + 1) + ")";
                }
            } while (!uniqueTitles.add(title)); // Ensure unique titles

            String author = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)] + " " +
                             LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            
            String isbn = String.format("978-%d-%d-%04d-%d", 
                    random.nextInt(9), random.nextInt(99), random.nextInt(9999), random.nextInt(9));
            
            String category = CATEGORIES[random.nextInt(CATEGORIES.length)];
            
            books.add(new Book(id, title, author, isbn, category, true));
        }

        // 2. Generate Members (half Student, half Faculty)
        List<LibraryMember> members = new ArrayList<>();
        for (int i = 1; i <= memberCount; i++) {
            String id = "M" + String.format("%04d", i);
            String name = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)] + " " +
                          LAST_NAMES[random.nextInt(LAST_NAMES.length)];
            String contact = String.format("+1-555-%03d-%04d", random.nextInt(1000), random.nextInt(10000));
            
            if (i % 2 == 0) {
                members.add(new Student(id, name, contact));
            } else {
                members.add(new Faculty(id, name, contact));
            }
        }

        // Save collections directly using FileHandler (batch mode)
        FileHandler.saveBooks(books, booksPath);
        FileHandler.saveMembers(members, membersPath);
        
        System.out.printf("Successfully generated and saved %d books and %d members to files.%n", bookCount, memberCount);
    }
}
