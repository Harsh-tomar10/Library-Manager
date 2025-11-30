import java.io.*;
import java.util.*;

public class LibraryManager {

    // -------- Book Class --------
    static class Book implements Comparable<Book> {
        int bookId;
        String title, author, category;
        boolean isIssued;

        public Book(int bookId, String title, String author, String category) {
            this.bookId = bookId;
            this.title = title;
            this.author = author;
            this.category = category;
            this.isIssued = false;
        }

        public void markAsIssued() {
            isIssued = true;
        }

        public void markAsReturned() {
            isIssued = false;
        }

        public void displayBookDetails() {
            System.out.println(bookId + " | " + title + " | " + author + " | " + category + " | Issued: " + isIssued);
        }

        @Override
        public int compareTo(Book b) {
            return this.title.compareToIgnoreCase(b.title);
        }
    }

    // -------- Member Class --------
    static class Member {
        int memberId;
        String name, email;
        List<Integer> issuedBooks = new ArrayList<>();

        public Member(int memberId, String name, String email) {
            this.memberId = memberId;
            this.name = name;
            this.email = email;
        }

        public void addIssuedBook(int bookId) {
            issuedBooks.add(bookId);
        }

        public void returnIssuedBook(int bookId) {
            issuedBooks.remove(Integer.valueOf(bookId));
        }

        public void displayMemberDetails() {
            System.out.println(memberId + " | " + name + " | " + email + " | Books: " + issuedBooks);
        }
    }

    // -------- Collection Storage --------
    static Map<Integer, Book> books = new HashMap<>();
    static Map<Integer, Member> members = new HashMap<>();
    static Set<String> categories = new HashSet<>();

    static int bookIdCounter = 100;
    static int memberIdCounter = 1;

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadFromFile();
        int choice;

        do {
            System.out.println("\n=== City Library Digital Management System ===");
            System.out.println("1. Add Book");
            System.out.println("2. Add Member");
            System.out.println("3. Issue Book");
            System.out.println("4. Return Book");
            System.out.println("5. Search Books");
            System.out.println("6. Sort Books");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> addBook();
                case 2 -> addMember();
                case 3 -> issueBook();
                case 4 -> returnBook();
                case 5 -> searchBooks();
                case 6 -> sortBooks();
                case 7 -> saveToFile();
                default -> System.out.println("Invalid choice!");
            }
        } while (choice != 7);
    }

    // -------- Methods --------
    static void addBook() {
        try {
            System.out.print("Enter Title: ");
            String title = sc.nextLine();
            System.out.print("Enter Author: ");
            String author = sc.nextLine();
            System.out.print("Enter Category: ");
            String category = sc.nextLine();

            Book b = new Book(++bookIdCounter, title, author, category);
            books.put(b.bookId, b);
            categories.add(category);

            saveToFile();
            System.out.println("Book added with ID: " + b.bookId);
        } catch (Exception e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }

    static void addMember() {
        try {
            System.out.print("Enter Name: ");
            String name = sc.nextLine();
            System.out.print("Enter Email: ");
            String email = sc.nextLine();

            Member m = new Member(++memberIdCounter, name, email);
            members.put(m.memberId, m);

            saveToFile();
            System.out.println("Member added with ID: " + m.memberId);
        } catch (Exception e) {
            System.out.println("Error adding member: " + e.getMessage());
        }
    }

    static void issueBook() {
        System.out.print("Enter Book ID: ");
        int bid = sc.nextInt();
        System.out.print("Enter Member ID: ");
        int mid = sc.nextInt();

        Book b = books.get(bid);
        Member m = members.get(mid);

        if (b != null && m != null && !b.isIssued) {
            b.markAsIssued();
            m.addIssuedBook(bid);
            saveToFile();
            System.out.println("Book issued successfully.");
        } else {
            System.out.println("Book not available or invalid IDs.");
        }
    }

    static void returnBook() {
        System.out.print("Enter Book ID: ");
        int bid = sc.nextInt();
        System.out.print("Enter Member ID: ");
        int mid = sc.nextInt();

        Book b = books.get(bid);
        Member m = members.get(mid);

        if (b != null && m != null) {
            b.markAsReturned();
            m.returnIssuedBook(bid);
            saveToFile();
            System.out.println("Book returned successfully.");
        } else {
            System.out.println("Invalid details.");
        }
    }

    static void searchBooks() {
        System.out.print("Search by title/author/category: ");
        String key = sc.nextLine().toLowerCase();

        for (Book b : books.values()) {
            if (b.title.toLowerCase().contains(key)
                    || b.author.toLowerCase().contains(key)
                    || b.category.toLowerCase().contains(key)) {
                b.displayBookDetails();
            }
        }
    }

    static void sortBooks() {
        List<Book> list = new ArrayList<>(books.values());
        Collections.sort(list); 

        for (Book b : list) {
            b.displayBookDetails();
        }
    }

    // -------- File Handling --------
    static void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("books.txt"))) {
            for (Book b : books.values()) {
                bw.write(b.bookId + "," + b.title + "," + b.author + "," + b.category + "," + b.isIssued);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving books.");
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("members.txt"))) {
            for (Member m : members.values()) {
                bw.write(m.memberId + "," + m.name + "," + m.email + "," + m.issuedBooks);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving members.");
        }
    }

    static void loadFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader("books.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                Book b = new Book(Integer.parseInt(data[0]), data[1], data[2], data[3]);
                b.isIssued = Boolean.parseBoolean(data[4]);
                books.put(b.bookId, b);
                bookIdCounter = b.bookId;
            }
        } catch (Exception e) {
            System.out.println("No existing books file found.");
        }

        try (BufferedReader br = new BufferedReader(new FileReader("members.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                Member m = new Member(Integer.parseInt(data[0]), data[1], data[2]);
                members.put(m.memberId, m);
                memberIdCounter = m.memberId;
            }
        } catch (Exception e) {
            System.out.println("No existing members file found.");
        }
    }
}
