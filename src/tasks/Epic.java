package tasks;

import java.util.ArrayList;
import java.util.HashMap;

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

    public void calculateStatus(HashMap<Integer, BaseTask> allTasksById) { // Передаем мапу со всеми задачами
        if (subtasksId.size() == 0) { // Если у эпика нет сабтасков
            this.status = "NEW"; // Статус эпика  "NEW"
        } else {
            for (Integer subtaskId : subtasksId) { // Для каждого id из списка subtasksId
                Subtask firstSubtask = (Subtask) allTasksById.get(this.subtasksId.get(0));
                // Достаем из мапы таскменеджера первый сабтаск и приводим его к типу Subtask для работы с полем status
                Subtask rightSubtask = (Subtask) allTasksById.get(subtaskId);
                // Достаем из мапы таскменеджера нужный сабтаск и приводим его к типу Subtask для работы с полем status
                if (rightSubtask.getStatus().equals(firstSubtask.getStatus())) { /* Если статус каждого нового сабтаска
                равен статусу самого первого сабтаска из списка subtasksId */
                    this.status = firstSubtask.getStatus(); // Статус эпика равен статусу его первого сабтаска
                } else {
                    this.status = "IN_PROGRESS"; // Иначе статус эпика IN_PROGRESS
                }
            }
        }
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
