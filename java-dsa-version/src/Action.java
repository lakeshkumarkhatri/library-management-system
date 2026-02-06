public class Action {
    public enum ActionType {
        ADD, DELETE, UPDATE
    }

    private ActionType type;
    private Book before;
    private Book after;

    public Action(ActionType type, Book before, Book after) {
        this.type = type;
        this.before = before;
        this.after = after;
    }

    public ActionType getType() { return type; }
    public Book getBefore() { return before; }
    public Book getAfter() { return after; }
}