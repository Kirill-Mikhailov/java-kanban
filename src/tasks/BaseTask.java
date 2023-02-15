package tasks;

public abstract class BaseTask {
    private Integer id;
    private String title;
    private String description;

    public BaseTask(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public BaseTask(Integer id, String title, String description) {
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
        return  getClass() + "{" +
                "id='" + this.getId() + '\'' +
                "title='" + this.getTitle() + '\'' +
                "description='" + this.getDescription() + '\'';
    }
}
