public class Book {
    private int id;
    private String title;
    private String author;
    private int publicationYear;
    private String shelfNumber;
    private boolean isAvailable;

    public Book(int id, String title, String author, int publicationYear, String shelfNumber, boolean isAvailable) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.shelfNumber = shelfNumber;
        this.isAvailable = isAvailable;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getYear() { return publicationYear; }
    public String getShelf() { return shelfNumber; }
    public boolean isAvailable() { return isAvailable; }

    // Setters (Needed for the Update feature)
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setYear(int year) { this.publicationYear = year; }
    public void setShelf(String shelf) { this.shelfNumber = shelf; }
    public void setAvailable(boolean available) { isAvailable = available; }
}