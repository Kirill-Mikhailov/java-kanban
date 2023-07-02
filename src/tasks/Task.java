package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public abstract class Task {
    private Integer id;
    private final String title;
    private final String description;
    private Status status;
    private LocalDateTime startTime;
    private Duration duration;

    public Task(String title, String description, Status status) {
        this.title = title;
        this.description = description;
        this.status = status;
    } // Конструктор для добавления эпика

    public Task(Integer id, String title, String description, Status status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    } // Конструктор для изменения эпика

    public Task(String title, String description, Status status, LocalDateTime startTime, int duration) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(duration);
    } // Конструктор для добавления таски и сабтаски

    public Task(Integer id, String title, String description, Status status, LocalDateTime startTime, int duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(duration);
    } // Конструктор для изменения таски и сабтаски

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        return getStartTime().plus(getDuration());
    }

    @Override
    public String toString() {
        return getClass() + "{" +
                "id='" + this.getId() + '\'' +
                "title='" + this.getTitle() + '\'' +
                "description='" + this.getDescription() + '\'' +
                "status='" + status + '\'' +
                "startTime" + this.getStartTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) && Objects.equals(title, task.title) &&
                Objects.equals(description, task.description) && status == task.status &&
                Objects.equals(startTime, task.startTime) && Objects.equals(duration, task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status, startTime, duration);
    }
}
