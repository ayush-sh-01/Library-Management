package com.library.service;

import com.library.model.*;
import com.library.io.FileHandler;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class LibraryService {
    private Map<String, Book> books = new HashMap<>();
    private Map<String, LibraryMember> members = new HashMap<>();
    private List<Transaction> transactions = new ArrayList<>();

    private final String booksPath;
    private final String membersPath;
    private final String transactionsPath;

    public LibraryService(String booksPath, String membersPath, String transactionsPath) {
        this.booksPath = booksPath;
        this.membersPath = membersPath;
        this.transactionsPath = transactionsPath;
        loadAllData();
    }

    public LibraryService() {
        this("data/books.csv", "data/members.csv", "data/transactions.csv");
    }

    // --- Loading and Saving ---
    public void loadAllData() {
        try {
            this.books = FileHandler.loadBooks(booksPath);
            this.members = FileHandler.loadMembers(membersPath);
            this.transactions = FileHandler.loadTransactions(transactionsPath);
        } catch (IOException e) {
            System.err.println("Error loading library data: " + e.getMessage());
        }
    }

    private void saveBooks() {
        try {
            FileHandler.saveBooks(books.values(), booksPath);
        } catch (IOException e) {
            System.err.println("Error saving books: " + e.getMessage());
        }
    }

    private void saveMembers() {
        try {
            FileHandler.saveMembers(members.values(), membersPath);
        } catch (IOException e) {
            System.err.println("Error saving members: " + e.getMessage());
        }
    }

    private void saveTransactions() {
        try {
            FileHandler.saveTransactions(transactions, transactionsPath);
        } catch (IOException e) {
            System.err.println("Error saving transactions: " + e.getMessage());
        }
    }

    // --- Book CRUD ---
    public boolean addBook(Book book) {
        if (books.containsKey(book.getId())) {
            return false;
        }
        books.put(book.getId(), book);
        saveBooks();
        return true;
    }

    public boolean updateBook(String id, String title, String author, String isbn, String category) {
        Book book = books.get(id);
        if (book == null) {
            return false;
        }
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setCategory(category);
        saveBooks();
        return true;
    }

    public boolean deleteBook(String id) {
        Book book = books.get(id);
        if (book == null) {
            return false;
        }
        // Verify book is not checked out
        boolean isIssued = transactions.stream()
                .anyMatch(t -> t.getBookId().equals(id) && !t.isReturned());
        if (isIssued) {
            throw new IllegalStateException("Cannot delete book. It is currently checked out.");
        }
        books.remove(id);
        saveBooks();
        return true;
    }

    public Book getBookById(String id) {
        return books.get(id);
    }

    public List<Book> searchBooks(String query) {
        String lowerQuery = query.toLowerCase();
        return books.values().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(lowerQuery) ||
                             b.getAuthor().toLowerCase().contains(lowerQuery) ||
                             b.getCategory().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }

    public Map<String, Book> getAllBooks() {
        return books;
    }

    // --- Member CRUD ---
    public boolean addMember(LibraryMember member) {
        if (members.containsKey(member.getId())) {
            return false;
        }
        members.put(member.getId(), member);
        saveMembers();
        return true;
    }

    public boolean updateMember(String id, String name, String contact) {
        LibraryMember member = members.get(id);
        if (member == null) {
            return false;
        }
        member.setName(name);
        member.setContact(contact);
        saveMembers();
        return true;
    }

    public boolean deleteMember(String id) {
        LibraryMember member = members.get(id);
        if (member == null) {
            return false;
        }
        // Verify member has returned all books
        boolean hasActiveCheckouts = transactions.stream()
                .anyMatch(t -> t.getMemberId().equals(id) && !t.isReturned());
        if (hasActiveCheckouts) {
            throw new IllegalStateException("Cannot delete member. They have active checked-out books.");
        }
        members.remove(id);
        saveMembers();
        return true;
    }

    public LibraryMember getMemberById(String id) {
        return members.get(id);
    }

    public List<LibraryMember> searchMembers(String query) {
        String lowerQuery = query.toLowerCase();
        return members.values().stream()
                .filter(m -> m.getName().toLowerCase().contains(lowerQuery) ||
                             m.getId().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }

    public Map<String, LibraryMember> getAllMembers() {
        return members;
    }

    // --- Issue and Return ---
    public Transaction issueBook(String memberId, String bookId, LocalDate issueDate) {
        LibraryMember member = members.get(memberId);
        if (member == null) {
            throw new IllegalArgumentException("Member ID not found: " + memberId);
        }
        Book book = books.get(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Book ID not found: " + bookId);
        }
        if (!book.isAvailable()) {
            throw new IllegalStateException("Book is already checked out: " + book.getTitle());
        }

        // Generate dynamic transaction ID
        String txId = "TX" + String.format("%06d", transactions.size() + 1);
        // Default borrow period is 14 days
        LocalDate dueDate = issueDate.plusDays(14);

        Transaction tx = new Transaction(txId, memberId, bookId, issueDate, dueDate);
        
        // Update status and save
        book.setAvailable(false);
        transactions.add(tx);
        
        saveBooks();
        saveTransactions();
        
        return tx;
    }

    public double returnBook(String bookId, LocalDate returnDate) {
        Book book = books.get(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Book ID not found: " + bookId);
        }
        if (book.isAvailable()) {
            throw new IllegalStateException("Book is already available in the library.");
        }

        // Find active transaction
        Transaction activeTx = transactions.stream()
                .filter(t -> t.getBookId().equals(bookId) && !t.isReturned())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No active transaction found for book ID: " + bookId));

        LibraryMember member = members.get(activeTx.getMemberId());
        if (member == null) {
            throw new IllegalStateException("Member associated with this transaction no longer exists: " + activeTx.getMemberId());
        }

        activeTx.setReturnDate(returnDate);

        // Calculate fine (Polymorphic call)
        long daysBetween = ChronoUnit.DAYS.between(activeTx.getDueDate(), returnDate);
        int daysOverdue = daysBetween > 0 ? (int) daysBetween : 0;
        double fine = member.calculateFine(daysOverdue);
        
        activeTx.setFineAmount(fine);

        // Update book availability
        book.setAvailable(true);

        saveBooks();
        saveTransactions();

        return fine;
    }

    // --- View/History Operations ---
    public List<Transaction> getActiveTransactions() {
        return transactions.stream()
                .filter(t -> !t.isReturned())
                .collect(Collectors.toList());
    }

    public List<Transaction> getOverdueTransactions(LocalDate date) {
        return transactions.stream()
                .filter(t -> !t.isReturned() && date.isAfter(t.getDueDate()))
                .collect(Collectors.toList());
    }

    public List<Transaction> getMemberBorrowHistory(String memberId) {
        return transactions.stream()
                .filter(t -> t.getMemberId().equals(memberId))
                .collect(Collectors.toList());
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    // --- Reports ---
    public Map<Book, Long> getMostIssuedBooks(int limit) {
        Map<String, Long> countMap = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getBookId, Collectors.counting()));

        return countMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .filter(entry -> books.containsKey(entry.getKey()))
                .collect(Collectors.toMap(
                        entry -> books.get(entry.getKey()),
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public double getTotalFinesCollected() {
        return transactions.stream()
                .mapToDouble(Transaction::getFineAmount)
                .sum();
    }
}
