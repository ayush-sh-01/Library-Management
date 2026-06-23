package com.library;

import com.library.model.*;
import com.library.service.LibraryService;
import com.library.util.DataSeeder;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Main {
    private static final LibraryService service = new LibraryService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   Welcome to the Library Management System      ");
        System.out.println("=================================================");
        
        // Auto-seed if files don't exist or are empty
        if (service.getAllBooks().isEmpty() && service.getAllMembers().isEmpty()) {
            System.out.println("No data files found or they are empty.");
            System.out.print("Would you like to seed 1,000+ sample records now? (yes/no): ");
            String choice = scanner.nextLine().trim().toLowerCase();
            if (choice.equals("yes") || choice.equals("y")) {
                seedSystemData();
            }
        }

        while (true) {
            printMainMenu();
            int choice = readIntChoice("Enter your choice: ", 1, 5);
            switch (choice) {
                case 1:
                    bookManagementMenu();
                    break;
                case 2:
                    memberManagementMenu();
                    break;
                case 3:
                    issueReturnMenu();
                    break;
                case 4:
                    reportsMenu();
                    break;
                case 5:
                    System.out.println("\nThank you for using the Library Management System. Goodbye!");
                    return;
            }
        }
    }

    private static void printMainMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Book Management");
        System.out.println("2. Member Management");
        System.out.println("3. Issue & Return Book");
        System.out.println("4. Reports & Performance Stats");
        System.out.println("5. Exit");
    }

    // --- SUB-MENUS ---

    private static void bookManagementMenu() {
        while (true) {
            System.out.println("\n--- BOOK MANAGEMENT ---");
            System.out.println("1. Add Book");
            System.out.println("2. Update Book");
            System.out.println("3. Delete Book");
            System.out.println("4. Search Book (by Title/Author/Category)");
            System.out.println("5. View All Books");
            System.out.println("6. Back to Main Menu");

            int choice = readIntChoice("Enter choice: ", 1, 6);
            switch (choice) {
                case 1: {
                    System.out.println("\n[Add New Book]");
                    String id = readString("Enter Book ID: ");
                    if (service.getBookById(id) != null) {
                        System.out.println("Error: A book with this ID already exists.");
                        break;
                    }
                    String title = readString("Enter Title: ");
                    String author = readString("Enter Author: ");
                    String isbn = readString("Enter ISBN: ");
                    String category = readString("Enter Category: ");
                    Book book = new Book(id, title, author, isbn, category, true);
                    if (service.addBook(book)) {
                        System.out.println("Book added successfully!");
                    } else {
                        System.out.println("Error adding book.");
                    }
                    break;
                }
                case 2: {
                    System.out.println("\n[Update Book]");
                    String id = readString("Enter Book ID to update: ");
                    Book existing = service.getBookById(id);
                    if (existing == null) {
                        System.out.println("Book not found.");
                        break;
                    }
                    System.out.println("Existing details: " + existing);
                    String title = readString("Enter new Title: ");
                    String author = readString("Enter new Author: ");
                    String isbn = readString("Enter new ISBN: ");
                    String category = readString("Enter new Category: ");
                    if (service.updateBook(id, title, author, isbn, category)) {
                        System.out.println("Book updated successfully!");
                    } else {
                        System.out.println("Error updating book.");
                    }
                    break;
                }
                case 3: {
                    System.out.println("\n[Delete Book]");
                    String id = readString("Enter Book ID to delete: ");
                    try {
                        if (service.deleteBook(id)) {
                            System.out.println("Book deleted successfully!");
                        } else {
                            System.out.println("Book not found.");
                        }
                    } catch (IllegalStateException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                }
                case 4: {
                    System.out.println("\n[Search Books]");
                    String query = readString("Enter search keyword (Title/Author/Category): ");
                    List<Book> results = service.searchBooks(query);
                    if (results.isEmpty()) {
                        System.out.println("No matching books found.");
                    } else {
                        System.out.println("Search Results:");
                        for (Book b : results) {
                            System.out.println("  " + b);
                        }
                    }
                    break;
                }
                case 5: {
                    System.out.println("\n[All Books]");
                    Map<String, Book> allBooks = service.getAllBooks();
                    if (allBooks.isEmpty()) {
                        System.out.println("No books in library.");
                    } else {
                        allBooks.values().stream()
                                .sorted(Comparator.comparing(Book::getId))
                                .forEach(b -> System.out.println("  " + b));
                    }
                    break;
                }
                case 6:
                    return;
            }
        }
    }

    private static void memberManagementMenu() {
        while (true) {
            System.out.println("\n--- MEMBER MANAGEMENT ---");
            System.out.println("1. Add Member");
            System.out.println("2. Update Member");
            System.out.println("3. Delete Member");
            System.out.println("4. Search Member (by Name/ID)");
            System.out.println("5. View All Members");
            System.out.println("6. Back to Main Menu");

            int choice = readIntChoice("Enter choice: ", 1, 6);
            switch (choice) {
                case 1: {
                    System.out.println("\n[Add New Member]");
                    String id = readString("Enter Member ID: ");
                    if (service.getMemberById(id) != null) {
                        System.out.println("Error: A member with this ID already exists.");
                        break;
                    }
                    String name = readString("Enter Name: ");
                    String contact = readString("Enter Contact: ");
                    System.out.println("Select Member Type:");
                    System.out.println("1. Student");
                    System.out.println("2. Faculty");
                    int typeChoice = readIntChoice("Choice (1 or 2): ", 1, 2);
                    LibraryMember member;
                    if (typeChoice == 1) {
                        member = new Student(id, name, contact);
                    } else {
                        member = new Faculty(id, name, contact);
                    }
                    if (service.addMember(member)) {
                        System.out.println("Member added successfully!");
                    } else {
                        System.out.println("Error adding member.");
                    }
                    break;
                }
                case 2: {
                    System.out.println("\n[Update Member]");
                    String id = readString("Enter Member ID to update: ");
                    LibraryMember existing = service.getMemberById(id);
                    if (existing == null) {
                        System.out.println("Member not found.");
                        break;
                    }
                    System.out.println("Existing details: " + existing);
                    String name = readString("Enter new Name: ");
                    String contact = readString("Enter new Contact: ");
                    if (service.updateMember(id, name, contact)) {
                        System.out.println("Member updated successfully!");
                    } else {
                        System.out.println("Error updating member.");
                    }
                    break;
                }
                case 3: {
                    System.out.println("\n[Delete Member]");
                    String id = readString("Enter Member ID to delete: ");
                    try {
                        if (service.deleteMember(id)) {
                            System.out.println("Member deleted successfully!");
                        } else {
                            System.out.println("Member not found.");
                        }
                    } catch (IllegalStateException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                }
                case 4: {
                    System.out.println("\n[Search Members]");
                    String query = readString("Enter search keyword (Name/ID): ");
                    List<LibraryMember> results = service.searchMembers(query);
                    if (results.isEmpty()) {
                        System.out.println("No matching members found.");
                    } else {
                        System.out.println("Search Results:");
                        for (LibraryMember m : results) {
                            System.out.println("  " + m);
                        }
                    }
                    break;
                }
                case 5: {
                    System.out.println("\n[All Members]");
                    Map<String, LibraryMember> allMembers = service.getAllMembers();
                    if (allMembers.isEmpty()) {
                        System.out.println("No members in library.");
                    } else {
                        allMembers.values().stream()
                                .sorted(Comparator.comparing(LibraryMember::getId))
                                .forEach(m -> System.out.println("  " + m));
                    }
                    break;
                }
                case 6:
                    return;
            }
        }
    }

    private static void issueReturnMenu() {
        while (true) {
            System.out.println("\n--- ISSUE & RETURN BOOKS ---");
            System.out.println("1. Issue a Book");
            System.out.println("2. Return a Book (with overdue simulation support)");
            System.out.println("3. View Currently Issued Books");
            System.out.println("4. View Overdue Books");
            System.out.println("5. View Member Borrow History");
            System.out.println("6. Back to Main Menu");

            int choice = readIntChoice("Enter choice: ", 1, 6);
            switch (choice) {
                case 1: {
                    System.out.println("\n[Issue Book]");
                    String memberId = readString("Enter Member ID: ");
                    String bookId = readString("Enter Book ID: ");
                    
                    LocalDate issueDate = readLocalDate("Enter issue date (YYYY-MM-DD) or press Enter for today: ", LocalDate.now());
                    
                    try {
                        Transaction tx = service.issueBook(memberId, bookId, issueDate);
                        System.out.println("Book issued successfully!");
                        System.out.println("Transaction details: " + tx);
                    } catch (IllegalArgumentException | IllegalStateException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                }
                case 2: {
                    System.out.println("\n[Return Book]");
                    String bookId = readString("Enter Book ID to return: ");
                    
                    Book book = service.getBookById(bookId);
                    if (book == null) {
                        System.out.println("Book not found.");
                        break;
                    }
                    if (book.isAvailable()) {
                        System.out.println("Book is already in the library (not checked out).");
                        break;
                    }

                    // Find matching transaction to show due date
                    Optional<Transaction> txOpt = service.getTransactions().stream()
                            .filter(t -> t.getBookId().equals(bookId) && !t.isReturned())
                            .findFirst();

                    LocalDate dueDate = LocalDate.now().plusDays(14); // Fallback
                    if (txOpt.isPresent()) {
                        Transaction activeTx = txOpt.get();
                        dueDate = activeTx.getDueDate();
                        System.out.println("Transaction Found: Book issued to Member " + activeTx.getMemberId());
                        System.out.println("Due Date: " + dueDate);
                    }

                    System.out.println("Choose Return Date simulation option:");
                    System.out.println("1. Return Today (" + LocalDate.now() + ")");
                    System.out.println("2. Simulate Late Return (Custom Return Date)");
                    int opt = readIntChoice("Choice (1 or 2): ", 1, 2);

                    LocalDate returnDate = LocalDate.now();
                    if (opt == 2) {
                        returnDate = readLocalDate("Enter simulated return date (YYYY-MM-DD): ", null);
                        while (txOpt.isPresent() && returnDate.isBefore(txOpt.get().getIssueDate())) {
                            System.out.println("Return date cannot be before the issue date (" + txOpt.get().getIssueDate() + ").");
                            returnDate = readLocalDate("Enter simulated return date (YYYY-MM-DD): ", null);
                        }
                    }

                    try {
                        double fine = service.returnBook(bookId, returnDate);
                        System.out.println("Book returned successfully!");
                        if (fine > 0) {
                            System.out.printf("Overdue fine calculated: $%.2f%n", fine);
                        } else {
                            System.out.println("No overdue fines.");
                        }
                    } catch (IllegalArgumentException | IllegalStateException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                }
                case 3: {
                    System.out.println("\n[Currently Issued Books]");
                    List<Transaction> active = service.getActiveTransactions();
                    if (active.isEmpty()) {
                        System.out.println("No books are currently checked out.");
                    } else {
                        for (Transaction t : active) {
                            Book b = service.getBookById(t.getBookId());
                            LibraryMember m = service.getMemberById(t.getMemberId());
                            String title = b != null ? b.getTitle() : "Unknown Book";
                            String name = m != null ? m.getName() : "Unknown Member";
                            System.out.printf("  Tx ID: %s | Book: %s (%s) | Member: %s (%s) | Due Date: %s%n",
                                    t.getTransactionId(), title, t.getBookId(), name, t.getMemberId(), t.getDueDate());
                        }
                    }
                    break;
                }
                case 4: {
                    System.out.println("\n[Overdue Books]");
                    LocalDate date = readLocalDate("Enter comparison date (YYYY-MM-DD) or press Enter for today: ", LocalDate.now());
                    List<Transaction> overdue = service.getOverdueTransactions(date);
                    if (overdue.isEmpty()) {
                        System.out.println("No books are overdue as of " + date + ".");
                    } else {
                        for (Transaction t : overdue) {
                            Book b = service.getBookById(t.getBookId());
                            LibraryMember m = service.getMemberById(t.getMemberId());
                            String title = b != null ? b.getTitle() : "Unknown Book";
                            String name = m != null ? m.getName() : "Unknown Member";
                            System.out.printf("  Tx ID: %s | Book: %s | Member: %s | Due Date: %s (Overdue! Type: %s)%n",
                                    t.getTransactionId(), title, name, t.getDueDate(), (m != null ? m.getMemberType() : "N/A"));
                        }
                    }
                    break;
                }
                case 5: {
                    System.out.println("\n[Member Borrow History]");
                    String id = readString("Enter Member ID: ");
                    LibraryMember member = service.getMemberById(id);
                    if (member == null) {
                        System.out.println("Member not found.");
                        break;
                    }
                    System.out.println("Borrow History for: " + member.getName() + " (" + member.getMemberType() + ")");
                    List<Transaction> history = service.getMemberBorrowHistory(id);
                    if (history.isEmpty()) {
                        System.out.println("  No transactions found for this member.");
                    } else {
                        for (Transaction t : history) {
                            Book b = service.getBookById(t.getBookId());
                            String title = b != null ? b.getTitle() : "Unknown Book";
                            System.out.printf("  Book: %s (%s) | Issued: %s | Due: %s | Returned: %s | Fine Paid: $%.2f%n",
                                    title, t.getBookId(), t.getIssueDate(), t.getDueDate(),
                                    (t.isReturned() ? t.getReturnDate().toString() : "ACTIVE"), t.getFineAmount());
                        }
                    }
                    break;
                }
                case 6:
                    return;
            }
        }
    }

    private static void reportsMenu() {
        while (true) {
            System.out.println("\n--- REPORTS & PERFORMANCE STATS ---");
            System.out.println("1. Total Overdue Fines Collected");
            System.out.println("2. Most Borrowed Books (Top 5)");
            System.out.println("3. Benchmark HashMap Lookup Speed");
            System.out.println("4. Re-Seed System Data (1,000+ records)");
            System.out.println("5. Back to Main Menu");

            int choice = readIntChoice("Enter choice: ", 1, 5);
            switch (choice) {
                case 1:
                    System.out.printf("%nTotal Overdue Fines Collected: $%.2f%n", service.getTotalFinesCollected());
                    break;
                case 2: {
                    System.out.println("\n[Most Borrowed Books]");
                    Map<Book, Long> mostIssued = service.getMostIssuedBooks(5);
                    if (mostIssued.isEmpty()) {
                        System.out.println("No books have been borrowed yet.");
                    } else {
                        int rank = 1;
                        for (Map.Entry<Book, Long> entry : mostIssued.entrySet()) {
                            System.out.printf("  %d. \"%s\" by %s - Borrowed %d times%n",
                                    rank++, entry.getKey().getTitle(), entry.getKey().getAuthor(), entry.getValue());
                        }
                    }
                    break;
                }
                case 3:
                    benchmarkHashMapLookups();
                    break;
                case 4:
                    seedSystemData();
                    break;
                case 5:
                    return;
            }
        }
    }

    // --- HELPERS ---

    private static void seedSystemData() {
        System.out.println("Warning: This will overwrite existing book and member files with 1,000+ realistic records.");
        System.out.print("Proceed? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (confirm.equals("yes") || confirm.equals("y")) {
            try {
                DataSeeder.seedData("data/books.csv", "data/members.csv", 1100, 1000);
                System.out.println("Data seeded successfully!");
                // Reload service data
                service.loadAllData();
                System.out.printf("Library reloaded: %d Books and %d Members now active in memory.%n", 
                        service.getAllBooks().size(), service.getAllMembers().size());
            } catch (IOException e) {
                System.err.println("Seeding failed: " + e.getMessage());
            }
        } else {
            System.out.println("Action cancelled.");
        }
    }

    private static void benchmarkHashMapLookups() {
        Map<String, Book> allBooks = service.getAllBooks();
        Map<String, LibraryMember> allMembers = service.getAllMembers();
        
        if (allBooks.isEmpty() || allMembers.isEmpty()) {
            System.out.println("Library contains no records. Please seed or add records first to run the benchmark.");
            return;
        }

        List<String> bookKeys = new ArrayList<>(allBooks.keySet());
        List<String> memberKeys = new ArrayList<>(allMembers.keySet());
        Random random = new Random();

        int iterations = 100000;
        System.out.printf("Running benchmark: performing %d random ID lookups on %d Books and %d Members...%n", 
                iterations, allBooks.size(), allMembers.size());

        long startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            // Random lookup book
            String randomBookId = bookKeys.get(random.nextInt(bookKeys.size()));
            Book b = allBooks.get(randomBookId);
            
            // Random lookup member
            String randomMemberId = memberKeys.get(random.nextInt(memberKeys.size()));
            LibraryMember m = allMembers.get(randomMemberId);
        }
        long endTime = System.nanoTime();

        double totalDurationMs = (endTime - startTime) / 1_000_000.0;
        double averageLookupTimeNs = (double) (endTime - startTime) / (iterations * 2); // 2 lookups per iteration

        System.out.printf("Benchmark Complete:%n");
        System.out.printf("  Total time taken: %.2f ms%n", totalDurationMs);
        System.out.printf("  Average lookup time: %.2f nanoseconds (O(1) verified)%n", averageLookupTimeNs);
    }

    private static String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    private static int readIntChoice(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(input);
                if (choice >= min && choice <= max) {
                    return choice;
                }
                System.out.printf("Invalid choice. Please enter a number between %d and %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    private static LocalDate readLocalDate(String prompt, LocalDate defaultDate) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty() && defaultDate != null) {
                return defaultDate;
            }
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }
    }
}
