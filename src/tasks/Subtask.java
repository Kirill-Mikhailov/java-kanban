package tasks;

public class Subtask extends SingleTask {
    private final int epicId;

    public Subtask(String title, String description, Status status, Integer epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public Subtask(Integer id, String title, String description, Status status, Integer epicId) {
        super(id, title, description, status);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return getClass() + "{" +
                "id='" + this.getId() + '\'' +
                "title='" + this.getTitle() + '\'' +
                "description='" + this.getDescription() + '\'' +
                "status='" + getStatus() + '\'' +
                "epicId='" + epicId + "'}";
    }
}
