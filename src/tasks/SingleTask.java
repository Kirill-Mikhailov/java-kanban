package tasks;

import java.time.LocalDateTime;

public class SingleTask extends Task {

    public SingleTask(String title, String description, Status status, LocalDateTime startTime, int duration) {
        super(title, description, status, startTime, duration);
        this.type = TaskType.TASK;
    }

    public SingleTask(Integer id, String title, String description, Status status, LocalDateTime startTime, int duration) {
        super(id, title, description, status, startTime, duration);
        this.type = TaskType.TASK;
    }

    @Override
    public String toString() {
        return super.toString() + "}";
    }
}
