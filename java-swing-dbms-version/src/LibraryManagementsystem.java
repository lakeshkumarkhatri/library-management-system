import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.sql.*;
import java.util.List;

class Book {
    int id;
    String title;
    String author;
    int year;
    String shelfNumber;

     Book(){}

    Book(int id, String title, String author, int year, String shelfNumber) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.shelfNumber = shelfNumber;
    }
}

class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/library_system";
    private static final String USER = "root";
    private static final String PASSWORD = "admin123$";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class LibraryManagementsystem extends JFrame implements ActionListener {

    // GUI Components
    private JButton displayBtn, addBtn, editBtn, searchBtn, deleteBtn, exitBtn;
    private JTextArea outputArea;

    public LibraryManagementsystem() {
        setTitle("Library Management System");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());


        // Create buttons panel (using FlowLayout)
        JPanel buttonPanel = new JPanel(new FlowLayout());

        displayBtn = new JButton("Display Books");
        addBtn = new JButton("Add Book");
        editBtn = new JButton("Edit Book");
        searchBtn = new JButton("Search Book");
        deleteBtn = new JButton("Delete Book");
        exitBtn = new JButton("Exit");

        // Add action listeners
        displayBtn.addActionListener(this);
        addBtn.addActionListener(this);
        editBtn.addActionListener(this);
        searchBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        exitBtn.addActionListener(this);

        // Add buttons to panel
        buttonPanel.add(displayBtn);
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(searchBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(exitBtn);

        // Output area
        outputArea = new JTextArea();
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        // Add components to frame
        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == displayBtn) {
            displayBooks();
        } else if (e.getSource() == addBtn) {
            addBook();
        } else if (e.getSource() == editBtn) {
            editBook();
        } else if (e.getSource() == searchBtn) {
            searchBook();
        } else if (e.getSource() == deleteBtn) {
            deleteBook();
        } else if (e.getSource() == exitBtn) {
            JOptionPane.showMessageDialog(this, "Thank you for using our Library Management System");
            System.exit(0);
        }
    }

    private List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Book book = new Book();
                book.id = rs.getInt("id");
                book.title = rs.getString("title");
                book.author = rs.getString("author");
                book.year = rs.getInt("publication_year");
                book.shelfNumber = rs.getString("shelf_number");
                books.add(book);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading books: " + ex.getMessage());
        }
        return books;
    }

    private void displayBooks() {
        List<Book> books = getAllBooks();
        if (books.isEmpty()) {
            outputArea.setText("Currently no books available in the library");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-5s %-25s %-20s %-6s %-8s\n",
                "ID", "TITLE", "AUTHOR", "YEAR", "SHELF"));
        sb.append("-----------------------------------------------------------------------\n");

        for (Book book : books) {
            sb.append(String.format("%-5d %-25s %-20s %-6d %-8s\n",
                    book.id,
                    truncate(book.title, 28),
                    truncate(book.author, 18),
                    book.year,
                    book.shelfNumber));
        }
        outputArea.setText(sb.toString());
    }

    private void addBook() {
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField yearField = new JTextField();
        JTextField shelfField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(5, 2));
        panel.add(new JLabel("ID: (Auto-generated)"));
        panel.add(new JLabel(""));
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Author:"));
        panel.add(authorField);
        panel.add(new JLabel("Year:"));
        panel.add(yearField);
        panel.add(new JLabel("Shelf Number:"));
        panel.add(shelfField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Book",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                         "INSERT INTO books (title, author, publication_year, shelf_number) VALUES (?, ?, ?, ?)")) {

                pstmt.setString(1, titleField.getText());
                pstmt.setString(2, authorField.getText());
                pstmt.setInt(3, Integer.parseInt(yearField.getText()));
                pstmt.setString(4, shelfField.getText());
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Book added successfully!");
            } catch (SQLException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void editBook() {
        String input = JOptionPane.showInputDialog(this, "Enter book ID to edit:");
        if (input == null) return;

        try {
            int id = Integer.parseInt(input);
            Book book = getBookById(id);

            if (book == null) {
                JOptionPane.showMessageDialog(this, "Book not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JTextField titleField = new JTextField(book.title);
            JTextField authorField = new JTextField(book.author);
            JTextField yearField = new JTextField(String.valueOf(book.year));
            JTextField shelfField = new JTextField(book.shelfNumber);

            JPanel panel = new JPanel(new GridLayout(4, 2));
            panel.add(new JLabel("Title:"));
            panel.add(titleField);
            panel.add(new JLabel("Author:"));
            panel.add(authorField);
            panel.add(new JLabel("Year:"));
            panel.add(yearField);
            panel.add(new JLabel("Shelf Number:"));
            panel.add(shelfField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Edit Book",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                try (Connection conn = DatabaseManager.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(
                             "UPDATE books SET title=?, author=?, publication_year=?, shelf_number=? WHERE id=?")) {

                    pstmt.setString(1, titleField.getText());
                    pstmt.setString(2, authorField.getText());
                    pstmt.setInt(3, Integer.parseInt(yearField.getText()));
                    pstmt.setString(4, shelfField.getText());
                    pstmt.setInt(5, id);
                    pstmt.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Book updated successfully!");
                } catch (SQLException | NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid book ID!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private Book getBookById(int id) {
        String sql = "SELECT * FROM books WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Book book = new Book();
                    book.id = rs.getInt("id");
                    book.title = rs.getString("title");
                    book.author = rs.getString("author");
                    book.year = rs.getInt("publication_year");
                    book.shelfNumber = rs.getString("shelf_number");
                    return book;
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
        return null;
    }

    private void searchBook() {
        String input = JOptionPane.showInputDialog(this, "Enter book ID to search:");
        if (input == null) return;

        try {
            int id = Integer.parseInt(input);
            Book book = getBookById(id);  // Use existing database query method
            
            if (book != null) {
                outputArea.setText("Book found:\n\n" +
                        "ID: " + book.id + "\n" +
                        "Title: " + book.title + "\n" +
                        "Author: " + book.author + "\n" +
                        "Year: " + book.year + "\n" +
                        "Shelf: " + book.shelfNumber);
            } else {
                JOptionPane.showMessageDialog(this, "Book not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid book ID!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBook() {
        String input = JOptionPane.showInputDialog(this, "Enter book ID to delete:");
        if (input == null) return;

        try {
            int id = Integer.parseInt(input);

            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM books WHERE id=?")) {

                pstmt.setInt(1, id);
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Book deleted successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Book not found!");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid book ID!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private String truncate(String text, int maxLength) {
        return text.length() > maxLength ? text.substring(0, maxLength-3) + "..." : text;
    }

    public static void main(String[] args) {
        // Load MySQL JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found!", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        SwingUtilities.invokeLater(new Runnable() {
    @Override
    public void run() {
        new LibraryManagementsystem();
    }
});
    }
}