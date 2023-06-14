package tasks;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private Status status;
    private ArrayList<Integer> subtasksId;

    public Epic(String title, String description) {
        super(title, description);
        this.subtasksId = new ArrayList<>();
        this.status = Status.NEW;
    }

    public Epic(Integer id, String title, String description) {
        super(id, title, description);
        this.subtasksId = new ArrayList<>();
    }

    public void addSubtasksId(Integer id) {
        subtasksId.add(id);
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void setSubtasksId(ArrayList<Integer> subtasksId) {
        this.subtasksId = subtasksId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return super.toString() +
                "status='" + status + '\'' +
                "subtasksId=" + subtasksId + "}";
    }
}