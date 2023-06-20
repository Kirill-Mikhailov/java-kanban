package manager.oldTaskManager;

import tasks.Epic;
import tasks.SingleTask;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    List<Task> getHistory();

    void calculateStatus(Epic epic);

    void addSingleTask(SingleTask singleTask);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask);

    void updateSingleTask(SingleTask singleTask);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    SingleTask getSingleTaskById(Integer id);

    Epic getEpicById(Integer id);

    Subtask getSubtaskById(Integer id);

    void deleteSingleTaskById(Integer id);

    void deleteEpicById(Integer id);

    void deleteSubtaskById(Integer id);

    ArrayList<SingleTask> getListOfAllSingleTasks();

    ArrayList<Epic> getListOfAllEpics();

    ArrayList<Subtask> getListOfAllSubtasks();

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    ArrayList<Subtask> getListOfEpicsSubtasks(Integer id);
}
