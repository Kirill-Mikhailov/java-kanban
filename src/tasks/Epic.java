package tasks;

import java.util.ArrayList;

public class Epic extends BaseTask {
    private String status;
    private ArrayList<Integer> subtasksId;

    public Epic(String title, String description) {
        super(title, description);
        this.subtasksId = new ArrayList<>();
        this.status = "NEW";
    }

    public Epic(Integer id, String title, String description) {
        super(id, title, description);
        this.subtasksId = new ArrayList<>(); // Неверно
    }

    public void addSubtasksId (Integer id) {
        subtasksId.add(id);
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void setSubtasksId(ArrayList<Integer> subtasksId) {
        this.subtasksId = subtasksId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return super.toString() +
                "status='" + status + '\'' +
                "subtasksId=" + subtasksId + "}";
    }
}
