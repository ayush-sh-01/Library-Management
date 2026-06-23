# Library Management System (Console-Based)

A lightweight, high-performance, console-based Library Management System written in pure Core Java. The system supports full CRUD operations, book issuing/returning, custom CSV persistence, and polymorphic overdue fine calculations, tested and validated at a scale of 1,000+ records.

---

## Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or higher installed on your system.

### Compilation
To compile the system, open your terminal (or PowerShell on Windows) in the project directory and run:
```bash
javac -target 1.8 -source 1.8 -d bin -sourcepath src src/com/library/Main.java src/com/library/util/VerificationTest.java
```
*(This compiles all classes into the `bin/` directory targeting Java 8 compatibility).*

### Running the Data Seeder
To generate 1,000+ realistic book and member records for demoing the app at full scale:
```bash
java -cp bin com.library.util.DataSeeder
```
*(This creates `data/books.csv` and `data/members.csv` at the project root).*

### Running the Application
To launch the console-based library system menu:
```bash
java -cp bin com.library.Main
```

### Running Verification Tests
To run the automated verification suite which proves polymorphic fine calculations, fine caps, and issue/return states:
```bash
java -cp bin com.library.util.VerificationTest
```

---

## Resume Claim Mapping & Technical Architecture

Here is how the project maps directly to core Java and Object-Oriented Programming (OOP) claims on a resume:

### 1. Inheritance & Polymorphism
- **Abstract Base Class**: [`LibraryMember.java`](file:///src/com/library/model/LibraryMember.java) defines the common fields (`id`, `name`, `contact`) and declares the abstract method:
  ```java
  public abstract double calculateFine(int daysOverdue);
  ```
- **Concrete Subclasses**: 
  - [`Student.java`](file:///src/com/library/model/Student.java) overrides `calculateFine(int)` with a rate of **$2.00/day** and a maximum cap of **$50.00**.
  - [`Faculty.java`](file:///src/com/library/model/Faculty.java) overrides `calculateFine(int)` with a rate of **$1.00/day** and a maximum cap of **$20.00**.
- **Polymorphic Invocation**: In [`LibraryService.java`](file:///src/com/library/service/LibraryService.java) within the `returnBook()` method, the fine is calculated using:
  ```java
  double fine = member.calculateFine(daysOverdue);
  ```
  The Java Virtual Machine (JVM) dynamically resolves this call at runtime to either `Student` or `Faculty` depending on the object type, demonstrating **polymorphism** (dynamic method dispatch).

### 2. Java Collections Framework (Performance at Scale)
- **$O(1)$ Lookup Complexity**: In [`LibraryService.java`](file:///src/com/library/service/LibraryService.java), books and members are cached in memory using `HashMap`:
  ```java
  private Map<String, Book> books = new HashMap<>();
  private Map<String, LibraryMember> members = new HashMap<>();
  ```
  Using the book ID or member ID as the key allows retrieving record details in **constant time ($O(1)$)**, keeping searches fast even with 1,000+ records. You can verify this by running the **HashMap Lookup Benchmark** from the Reports menu.
- **Chronological Auditing**: Transactions are stored in an `ArrayList<Transaction>`:
  ```java
  private List<Transaction> transactions = new ArrayList<>();
  ```
  This preserves the order of borrow transactions, facilitating linear search filters (e.g., searching by active/overdue checkouts).

### 3. Fine Calculation & File I/O Persistence
- **Date Arithmetic**: The system utilizes Java 8 Date and Time API (`java.time.LocalDate` and `java.time.temporal.ChronoUnit.DAYS`) to calculate the exact days elapsed between the due date and the return date.
- **Custom CSV Storage**: Rather than binary serialization (which is brittle and prone to class compatibility issues), data is persisted using custom comma-separated values (CSV) in the [`data/`](file:///data/) directory.
- **Atomic Operations & Escaping**: [`FileHandler.java`](file:///src/com/library/io/FileHandler.java) includes a robust parser that handles double quotes and commas (e.g. books with commas in their titles), and ensures the CSV files are re-saved atomically on every record creation, update, checkout, or return.
