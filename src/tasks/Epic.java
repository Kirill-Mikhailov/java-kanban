package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> subtasksId;
    private LocalDateTime endTime;

    public Epic() {
        super();
        this.subtasksId = new ArrayList<>();
        this.type = TaskType.EPIC;
    }

    public Epic(String title, String description) {
        super(title, description, Status.NEW);
        this.subtasksId = new ArrayList<>();
        this.type = TaskType.EPIC;
    } // Конструктор для добавления эпика

    public Epic(Integer id, String title, String description) {
        super(id, title, description, Status.NEW);
        this.subtasksId = new ArrayList<>();
        this.type = TaskType.EPIC;
    }  // Конструктор для изменения эпика

    public Epic(Integer id, String title, String description, Status status) {
        super(id, title, description, status);
        this.subtasksId = new ArrayList<>();
        this.type = TaskType.EPIC;
    } // Конструктор для загрузки эпика из файла

    public void addSubtasksId(Integer id) {
        subtasksId.add(id);
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void setSubtasksId(ArrayList<Integer> subtasksId) {
        this.subtasksId = subtasksId;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return super.toString() +
                "subtasksId=" + subtasksId + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasksId, epic.subtasksId) && Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksId, endTime);
    }
}