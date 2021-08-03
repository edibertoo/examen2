package mx.ucol.models;

public class Todo {
    private int id;
    private String title;
    private boolean completed;

    public Todo(int id, String title, boolean completed){
        this.id = id;
        this.title = title;
        this.completed = completed;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean getCompleted() {
        return this.completed;
    }

    public String toString() {
        return "ID: " + this.id + ", Title: " + this.title + ", Completed: " + this.completed;
    }
}
