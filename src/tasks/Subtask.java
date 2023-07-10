package tasks;

import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends SingleTask {
    private final Integer epicId;

    public Subtask(String title, String description, Status status, Integer epicId,
                   LocalDateTime startTime, int duration) {
        super(title, description, status, startTime, duration);
        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
    }

    public Subtask(Integer id, String title, String description, Status status, Integer epicId,
                   LocalDateTime startTime, int duration) {
        super(id, title, description, status, startTime, duration);
        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
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
                "epicId='" + epicId + '\'' +
                "startTime" + this.getStartTime() + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
