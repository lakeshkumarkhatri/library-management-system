import java.sql.*;
import java.util.*;

public class LibraryData {

    private ArrayList<Book> books = new ArrayList<>();
    private HashMap<Integer, Book> bookIndex = new HashMap<>();
    private Stack<Action> undoStack = new Stack<>();

    // --- DATABASE & LOADING ---
    public void loadBooksFromDatabase() {
        books.clear();
        bookIndex.clear();
        String sql = "SELECT * FROM books";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Book book = new Book(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getInt("publication_year"),
                    rs.getString("shelf_number"),
                    rs.getBoolean("is_available")
                );
                books.add(book);
                bookIndex.put(book.getId(), book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Book> getAllBooks() { return books; }

    // --- SEARCHING ---
    public List<Book> searchByTitle(String title) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(title.toLowerCase())) {
                result.add(book);
            }
        }
        return result;
    }

    public List<Book> searchByAuthor(String author) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.getAuthor().toLowerCase().contains(author.toLowerCase())) {
                result.add(book);
            }
        }
        return result;
    }


    // --- SORTING ---
    public void sortByIdBubble() {
        books.sort(Comparator.comparingInt(Book::getId)); // Simplified for brevity
    }

    public void sortByTitleBubble() {
        int n = books.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                Book b1 = books.get(j);
                Book b2 = books.get(j + 1);
                if (b1.getTitle().compareToIgnoreCase(b2.getTitle()) > 0) {
                    books.set(j, b2);
                    books.set(j + 1, b1);
                }
            }
        }
    }

    public void sortByYearBubble() {
        int n = books.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                Book b1 = books.get(j);
                Book b2 = books.get(j + 1);
                if (b1.getYear() > b2.getYear()) {
                    books.set(j, b2);
                    books.set(j + 1, b1);
                }
            }
        }
    }

    // --- ADD / DELETE / UPDATE + UNDO ---

    // 1. UPDATE THIS METHOD
    // --- CORRECT ADD BOOK (Prevents Duplicates) ---
    // --- CORRECT ADD BOOK (Prevents Duplicates) ---
    public boolean addBook(Book book) {
        // 1. Check if ID already exists
        if (bookIndex.containsKey(book.getId())) {
            return false; // Fail: ID already exists
        }

        // 2. Add to memory
        books.add(book);
        bookIndex.put(book.getId(), book);

        // 3. Add to Undo Stack
        undoStack.push(new Action(Action.ActionType.ADD, null, book));

        // 4. Add to Database
        insertBookToDatabase(book);

        return true; // Success
    }

    public Book searchByIdBinary(int id) {
        sortByIdBubble(); // Ensure list is sorted first

        int left = 0;
        int right = books.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int midId = books.get(mid).getId();

            if (midId == id) {
                return books.get(mid);
            }

            if (midId < id) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return null; // Not found
    }


    public void deleteBook(int bookId) {
        Book book = bookIndex.get(bookId);
        if (book == null) return;
        undoStack.push(new Action(Action.ActionType.DELETE, book, null));
        books.remove(book);
        bookIndex.remove(bookId);
        deleteBookFromDatabase(bookId);
    }

    public void updateBook(Book updatedBook) {
        Book existing = bookIndex.get(updatedBook.getId());
        if (existing == null) return;
        
        // Snapshot before
        Book before = new Book(existing.getId(), existing.getTitle(), existing.getAuthor(), existing.getYear(), existing.getShelf(), existing.isAvailable());
        
        // Update in memory
        existing.setTitle(updatedBook.getTitle());
        existing.setAuthor(updatedBook.getAuthor());
        existing.setYear(updatedBook.getYear());
        existing.setShelf(updatedBook.getShelf());

        // Snapshot after
        Book after = new Book(existing.getId(), existing.getTitle(), existing.getAuthor(), existing.getYear(), existing.getShelf(), existing.isAvailable());

        undoStack.push(new Action(Action.ActionType.UPDATE, before, after));
        updateBookInDatabase(existing);
    }

    public boolean canUndo() { return !undoStack.isEmpty(); }

    public void undoLastAction() {
        if (undoStack.isEmpty()) return;
        Action action = undoStack.pop();

        switch (action.getType()) {
            case ADD:
                // Undo Add = Delete
                Book addedBook = action.getAfter();
                books.remove(addedBook);
                bookIndex.remove(addedBook.getId());
                deleteBookFromDatabase(addedBook.getId());
                break;
            case DELETE:
                // Undo Delete = Add
                Book deletedBook = action.getBefore();
                books.add(deletedBook);
                bookIndex.put(deletedBook.getId(), deletedBook);
                insertBookToDatabase(deletedBook);
                break;
            case UPDATE:
                // Undo Update = Restore old values
                Book before = action.getBefore();
                Book current = bookIndex.get(before.getId());
                if (current != null) {
                    current.setTitle(before.getTitle());
                    current.setAuthor(before.getAuthor());
                    current.setYear(before.getYear());
                    current.setShelf(before.getShelf());
                    updateBookInDatabase(current);
                }
                break;
        }
    }

    // --- PRIVATE SQL HELPERS ---
    private void insertBookToDatabase(Book book) {
        String sql = "INSERT INTO books (id, title, author, publication_year, shelf_number, is_available) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, book.getId());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setInt(4, book.getYear());
            ps.setString(5, book.getShelf());
            ps.setBoolean(6, book.isAvailable());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void deleteBookFromDatabase(int bookId) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void updateBookInDatabase(Book book) {
        String sql = "UPDATE books SET title=?, author=?, publication_year=?, shelf_number=? WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setInt(3, book.getYear());
            ps.setString(4, book.getShelf());
            ps.setInt(5, book.getId());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}