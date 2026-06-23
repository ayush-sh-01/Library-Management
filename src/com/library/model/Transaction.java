package com.library.model;

import java.time.LocalDate;

public class Transaction {
    private String transactionId;
    private String memberId;
    private String bookId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate; // Nullable
    private double fineAmount;

    public Transaction(String transactionId, String memberId, String bookId, LocalDate issueDate, LocalDate dueDate) {
        this.transactionId = transactionId;
        this.memberId = memberId;
        this.bookId = bookId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = null;
        this.fineAmount = 0.0;
    }

    public Transaction(String transactionId, String memberId, String bookId, LocalDate issueDate, LocalDate dueDate, LocalDate returnDate, double fineAmount) {
        this.transactionId = transactionId;
        this.memberId = memberId;
        this.bookId = bookId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fineAmount = fineAmount;
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public double getFineAmount() { return fineAmount; }
    public void setFineAmount(double fineAmount) { this.fineAmount = fineAmount; }

    public boolean isReturned() {
        return returnDate != null;
    }

    @Override
    public String toString() {
        return String.format("Transaction [ID: %s, Member ID: %s, Book ID: %s, Issued: %s, Due: %s, Returned: %s, Fine: $%.2f]",
                transactionId, memberId, bookId, issueDate, dueDate, 
                (returnDate != null ? returnDate.toString() : "Not Returned"), fineAmount);
    }
}
