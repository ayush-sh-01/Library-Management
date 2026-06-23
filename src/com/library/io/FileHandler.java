package com.library.io;

import com.library.model.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class FileHandler {

    private static String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private static List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"');
                    i++; // Skip the second quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',') {
                if (inQuotes) {
                    sb.append(c);
                } else {
                    result.add(sb.toString());
                    sb.setLength(0);
                }
            } else {
                sb.append(c);
            }
        }
        result.add(sb.toString());
        return result;
    }

    public static void ensureDirectoryExists(String filepath) {
        File file = new File(filepath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    // --- BOOKS I/O ---
    public static void saveBooks(Collection<Book> books, String filepath) throws IOException {
        ensureDirectoryExists(filepath);
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            // Header
            writer.println("id,title,author,isbn,category,available");
            for (Book book : books) {
                writer.println(String.join(",",
                        escapeCsv(book.getId()),
                        escapeCsv(book.getTitle()),
                        escapeCsv(book.getAuthor()),
                        escapeCsv(book.getIsbn()),
                        escapeCsv(book.getCategory()),
                        String.valueOf(book.isAvailable())
                ));
            }
        }
    }

    public static Map<String, Book> loadBooks(String filepath) throws IOException {
        Map<String, Book> books = new HashMap<>();
        File file = new File(filepath);
        if (!file.exists()) {
            return books;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                List<String> parts = parseCsvLine(line);
                if (parts.size() < 6) continue;

                String id = parts.get(0);
                String title = parts.get(1);
                String author = parts.get(2);
                String isbn = parts.get(3);
                String category = parts.get(4);
                boolean available = Boolean.parseBoolean(parts.get(5));

                Book book = new Book(id, title, author, isbn, category, available);
                books.put(id, book);
            }
        }
        return books;
    }

    // --- MEMBERS I/O ---
    public static void saveMembers(Collection<LibraryMember> members, String filepath) throws IOException {
        ensureDirectoryExists(filepath);
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            // Header
            writer.println("id,name,contact,memberType");
            for (LibraryMember member : members) {
                writer.println(String.join(",",
                        escapeCsv(member.getId()),
                        escapeCsv(member.getName()),
                        escapeCsv(member.getContact()),
                        escapeCsv(member.getMemberType())
                ));
            }
        }
    }

    public static Map<String, LibraryMember> loadMembers(String filepath) throws IOException {
        Map<String, LibraryMember> members = new HashMap<>();
        File file = new File(filepath);
        if (!file.exists()) {
            return members;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                List<String> parts = parseCsvLine(line);
                if (parts.size() < 4) continue;

                String id = parts.get(0);
                String name = parts.get(1);
                String contact = parts.get(2);
                String memberType = parts.get(3);

                LibraryMember member;
                if ("Student".equalsIgnoreCase(memberType)) {
                    member = new Student(id, name, contact);
                } else if ("Faculty".equalsIgnoreCase(memberType)) {
                    member = new Faculty(id, name, contact);
                } else {
                    // Default fallback
                    member = new Student(id, name, contact);
                }
                members.put(id, member);
            }
        }
        return members;
    }

    // --- TRANSACTIONS I/O ---
    public static void saveTransactions(Collection<Transaction> transactions, String filepath) throws IOException {
        ensureDirectoryExists(filepath);
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            // Header
            writer.println("transactionId,memberId,bookId,issueDate,dueDate,returnDate,fineAmount");
            for (Transaction t : transactions) {
                String returnDateStr = t.getReturnDate() != null ? t.getReturnDate().toString() : "";
                writer.println(String.join(",",
                        escapeCsv(t.getTransactionId()),
                        escapeCsv(t.getMemberId()),
                        escapeCsv(t.getBookId()),
                        escapeCsv(t.getIssueDate().toString()),
                        escapeCsv(t.getDueDate().toString()),
                        escapeCsv(returnDateStr),
                        String.valueOf(t.getFineAmount())
                ));
            }
        }
    }

    public static List<Transaction> loadTransactions(String filepath) throws IOException {
        List<Transaction> transactions = new ArrayList<>();
        File file = new File(filepath);
        if (!file.exists()) {
            return transactions;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                List<String> parts = parseCsvLine(line);
                if (parts.size() < 7) continue;

                String txId = parts.get(0);
                String memberId = parts.get(1);
                String bookId = parts.get(2);
                
                LocalDate issueDate = null;
                LocalDate dueDate = null;
                LocalDate returnDate = null;
                try {
                    issueDate = LocalDate.parse(parts.get(3));
                    dueDate = LocalDate.parse(parts.get(4));
                    String returnDateStr = parts.get(5);
                    if (!returnDateStr.trim().isEmpty()) {
                        returnDate = LocalDate.parse(returnDateStr);
                    }
                } catch (DateTimeParseException e) {
                    // Skip malformed dates
                    continue;
                }

                double fineAmount = 0.0;
                try {
                    fineAmount = Double.parseDouble(parts.get(6));
                } catch (NumberFormatException e) {
                    // Default to 0
                }

                Transaction t = new Transaction(txId, memberId, bookId, issueDate, dueDate, returnDate, fineAmount);
                transactions.add(t);
            }
        }
        return transactions;
    }
}
