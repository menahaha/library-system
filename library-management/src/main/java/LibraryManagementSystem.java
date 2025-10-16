import java.sql.*;
import java.util.Scanner;

public class LibraryManagementSystem {

    static final String URL = "jdbc:mysql://localhost:3306/library_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    static final String USER = "root"; // replace with your MySQL username
    static final String PASSWORD = "Menaha~17"; // replace with your MySQL password

    public static void main(String[] args) {

        try {
            // Explicitly load MySQL driver (sometimes required)
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ MySQL Driver loaded successfully!");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ MySQL Driver not found!");
            e.printStackTrace();
            return;
        }

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Scanner sc = new Scanner(System.in)) {

            System.out.println("📚 Welcome to Library Management System");

            int choice;
            do {
                System.out.println("\n===== Menu =====");
                System.out.println("1. Add Book");
                System.out.println("2. Issue Book");
                System.out.println("3. Return Book");
                System.out.println("4. View All Books");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");
                choice = sc.nextInt();
                sc.nextLine(); // consume newline

                switch (choice) {
                    case 1 -> addBook(conn, sc);
                    case 2 -> issueBook(conn, sc);
                    case 3 -> returnBook(conn, sc);
                    case 4 -> viewBooks(conn);
                    case 5 -> System.out.println("👋 Exiting system...");
                    default -> System.out.println("❌ Invalid choice!");
                }

            } while (choice != 5);

        } catch (SQLException e) {
            System.out.println("❌ Database connection error!");
            e.printStackTrace();
        }
    }

    // Add a new book
    static void addBook(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter Book Title: ");
        String title = sc.nextLine();
        System.out.print("Enter Author: ");
        String author = sc.nextLine();
        System.out.print("Enter Quantity: ");
        int quantity = sc.nextInt();

        String sql = "INSERT INTO books (title, author, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setInt(3, quantity);
            ps.executeUpdate();
            System.out.println("✅ Book added successfully!");
        }
    }

    // Issue a book
    static void issueBook(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter Book ID to issue: ");
        int id = sc.nextInt();

        String checkSql = "SELECT quantity FROM books WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int quantity = rs.getInt("quantity");
                if (quantity > 0) {
                    String updateSql = "UPDATE books SET quantity=? WHERE id=?";
                    try (PreparedStatement ps2 = conn.prepareStatement(updateSql)) {
                        ps2.setInt(1, quantity - 1);
                        ps2.setInt(2, id);
                        ps2.executeUpdate();
                        System.out.println("✅ Book issued successfully!");
                    }
                } else {
                    System.out.println("❌ Book is out of stock!");
                }
            } else {
                System.out.println("❌ Book not found!");
            }
        }
    }

    // Return a book
    static void returnBook(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter Book ID to return: ");
        int id = sc.nextInt();

        String checkSql = "SELECT quantity FROM books WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int quantity = rs.getInt("quantity");
                String updateSql = "UPDATE books SET quantity=? WHERE id=?";
                try (PreparedStatement ps2 = conn.prepareStatement(updateSql)) {
                    ps2.setInt(1, quantity + 1);
                    ps2.setInt(2, id);
                    ps2.executeUpdate();
                    System.out.println("✅ Book returned successfully!");
                }
            } else {
                System.out.println("❌ Book not found!");
            }
        }
    }

    // View all books
    static void viewBooks(Connection conn) throws SQLException {
        String sql = "SELECT * FROM books";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n📖 All Books:");
            System.out.println("ID\tTitle\tAuthor\tQuantity");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + "\t" +
                        rs.getString("title") + "\t" +
                        rs.getString("author") + "\t" +
                        rs.getInt("quantity"));
            }
        }
    }
}
