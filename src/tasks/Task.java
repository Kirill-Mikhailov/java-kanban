package tasks;

public abstract class Task {
    private Integer id;
    private final String title;
    private final String description;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Task(Integer id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return getClass() + "{" +
                "id='" + this.getId() + '\'' +
                "title='" + this.getTitle() + '\'' +
                "description='" + this.getDescription() + '\'';
    }
}
