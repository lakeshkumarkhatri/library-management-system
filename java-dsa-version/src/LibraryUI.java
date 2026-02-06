import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import javax.swing.border.*;

public class LibraryUI extends JFrame {

    private LibraryData libraryData;
    private DefaultTableModel tableModel;
    private JTable bookTable;
    
    // UI Input Fields
    private JTextField idField, titleField, authorField, yearField, shelfField, searchField;
    private JComboBox<String> searchType, sortCombo;

    public LibraryUI() {
        libraryData = new LibraryData();
        setTitle("Library Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        initUI();
        
        libraryData.loadBooksFromDatabase(); 
        refreshTable();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // --- MAIN TOP CONTAINER (Vertical Stack) ---
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setBorder(new EmptyBorder(10, 10, 10, 10)); // Outer padding

        // 1. INPUT FORM (Grouped in a bordered box)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Book Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 10); // Padding between items
        gbc.anchor = GridBagConstraints.WEST;

        // -- Initialize Fields --
        idField = new JTextField(5);
        titleField = new JTextField(15);
        authorField = new JTextField(15);
        yearField = new JTextField(6);
        shelfField = new JTextField(6);

        // Row 1: ID, Title, Author
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1; formPanel.add(idField, gbc);

        gbc.gridx = 2; formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 3; formPanel.add(titleField, gbc);

        gbc.gridx = 4; formPanel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 5; formPanel.add(authorField, gbc);

        // Row 2: Year, Shelf
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1; formPanel.add(yearField, gbc);

        gbc.gridx = 2; formPanel.add(new JLabel("Shelf:"), gbc);
        gbc.gridx = 3; formPanel.add(shelfField, gbc);

        // 2. ACTION BUTTONS (Centered)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton addButton = new JButton("Add Book");
        JButton updateButton = new JButton("Update Selected");
        JButton deleteButton = new JButton("Delete Selected");
        JButton undoButton = new JButton("Undo Last Action");

        actionPanel.add(addButton);
        actionPanel.add(updateButton);
        actionPanel.add(deleteButton);
        actionPanel.add(undoButton);

        // 3. SEARCH & SORT PANEL (Grouped)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search & Filter"));

        searchField = new JTextField(15);
        searchType = new JComboBox<>(new String[]{"Title", "Author"});
        JButton searchButton = new JButton("Search");
        JButton searchByIdButton = new JButton("Find by ID");

        sortCombo = new JComboBox<>(new String[]{"ID", "Title", "Year"});
        JButton sortButton = new JButton("Sort");

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchType);
        searchPanel.add(searchButton);
        searchPanel.add(searchByIdButton);
        searchPanel.add(new JSeparator(SwingConstants.VERTICAL)); // Visual divider
        searchPanel.add(new JLabel("Sort By:"));
        searchPanel.add(sortCombo);
        searchPanel.add(sortButton);

        // Add all panels to the top container
        topContainer.add(formPanel);
        topContainer.add(actionPanel);
        topContainer.add(searchPanel);

        add(topContainer, BorderLayout.NORTH);

        // --- CENTER TABLE ---
        // Added "Shelf" to columns so you can see it
        String[] columns = {"ID", "Title", "Author", "Year", "Shelf", "Available"};
        tableModel = new DefaultTableModel(columns, 0);
        bookTable = new JTable(tableModel);
        bookTable.setRowHeight(25); // Make rows slightly taller for readability
        add(new JScrollPane(bookTable), BorderLayout.CENTER);

        // --- LISTENERS (Logic) ---

        // 1. ADD
        addButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                String title = titleField.getText();
                String author = authorField.getText();
                int year = Integer.parseInt(yearField.getText());
                String shelf = shelfField.getText();

                Book book = new Book(id, title, author, year, shelf, true);

                // CHECK IF SUCCESSFUL
                boolean success = libraryData.addBook(book);

                if (success) {
                    refreshTable();
                    clearFields();
                    JOptionPane.showMessageDialog(this, "Book Added Successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Error: Book ID " + id + " already exists!");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Input (Check ID/Year)");
            }
        });

        // 2. DELETE
        deleteButton.addActionListener(e -> {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a book to delete");
                return;
            }
            int bookId = (int) tableModel.getValueAt(selectedRow, 0);
            libraryData.deleteBook(bookId);
            refreshTable();
        });

        // 3. UNDO
        undoButton.addActionListener(e -> {
            if (!libraryData.canUndo()) {
                JOptionPane.showMessageDialog(this, "Nothing to undo");
                return;
            }
            libraryData.undoLastAction();
            refreshTable();
        });

        // 4. SEARCH
        searchButton.addActionListener(e -> {
            String text = searchField.getText().trim();
            if (text.isEmpty()) {
                refreshTable();
                return;
            }
            List<Book> results;
            if (searchType.getSelectedItem().equals("Title")) {
                results = libraryData.searchByTitle(text);
            } else {
                results = libraryData.searchByAuthor(text);
            }
            updateTable(results);
        });

        // 5. SEARCH BY ID
        searchByIdButton.addActionListener(e -> {
            try {
                String input = JOptionPane.showInputDialog("Enter ID:");
                if(input != null && !input.isEmpty()) {
                    int id = Integer.parseInt(input);
                    Book book = libraryData.searchByIdBinary(id);
                    if (book == null) JOptionPane.showMessageDialog(this, "Book not found");
                    else updateTable(List.of(book));
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter valid ID");
            }
        });

        // 6. SORT
        sortButton.addActionListener(e -> {
            String choice = sortCombo.getSelectedItem().toString();

            if (choice.equals("Title")) {
                libraryData.sortByTitleBubble();
            } else if (choice.equals("Year")) {
                libraryData.sortByYearBubble();
            } else if (choice.equals("ID")) {
                libraryData.sortByIdBubble(); // Call the new method
            }

            refreshTable();
        });

        // 7. UPDATE
        updateButton.addActionListener(e -> {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a book to update");
                return;
            }
            int id = (int) tableModel.getValueAt(selectedRow, 0);

            // Pre-fill current values
            String currentTitle = (String) tableModel.getValueAt(selectedRow, 1);
            String currentAuthor = (String) tableModel.getValueAt(selectedRow, 2);
            int currentYear = (int) tableModel.getValueAt(selectedRow, 3);

            String newTitle = JOptionPane.showInputDialog("New Title:", currentTitle);
            String newAuthor = JOptionPane.showInputDialog("New Author:", currentAuthor);
            String newYearStr = JOptionPane.showInputDialog("New Year:", currentYear);

            if(newTitle != null && newAuthor != null && newYearStr != null) {
                try {
                    Book updated = new Book(id, newTitle, newAuthor, Integer.parseInt(newYearStr), "A1", true);
                    libraryData.updateBook(updated);
                    refreshTable();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid Year format");
                }
            }
        });
    }

    private void refreshTable() {
        updateTable(libraryData.getAllBooks());
    }

    private void updateTable(List<Book> list) {
        tableModel.setRowCount(0);
        for (Book b : list) {
            tableModel.addRow(new Object[]{
                    b.getId(),
                    b.getTitle(),
                    b.getAuthor(),
                    b.getYear(),
                    b.getShelf(), // Added this line
                    b.isAvailable() ? "Yes" : "No"
            });
        }
    }
    
    private void clearFields() {
        idField.setText(""); titleField.setText(""); authorField.setText("");
        yearField.setText(""); shelfField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LibraryUI().setVisible(true);
        });
    }
}