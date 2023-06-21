package manager;

import manager.exceptions.ManagerSaveException;
import manager.oldTaskManager.InMemoryTaskManager;
import manager.oldTaskManager.TaskManager;
import tasks.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private Path autoSaveFile;
    public FileBackedTasksManager(Path autoSaveFile) {
        this.autoSaveFile = autoSaveFile;
    }

    @Override
    public void addSingleTask(SingleTask singleTask) {
        super.addSingleTask(singleTask);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateSingleTask(SingleTask singleTask) {
        super.updateSingleTask(singleTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public SingleTask getSingleTaskById(Integer id) {
        SingleTask singleTask = super.getSingleTaskById(id);
        save();
        return singleTask;
    }

    @Override
    public Epic getEpicById(Integer id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public void deleteSingleTaskById(Integer id) {
        super.deleteSingleTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(Integer id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public ArrayList<SingleTask> getListOfAllSingleTasks() {
        ArrayList<SingleTask> listOfAllSingleTasks = super.getListOfAllSingleTasks();
        save();
        return listOfAllSingleTasks;
    }

    @Override
    public ArrayList<Epic> getListOfAllEpics() {
        ArrayList<Epic> listOfAllEpics = super.getListOfAllEpics();
        save();
        return listOfAllEpics;
    }

    @Override
    public ArrayList<Subtask> getListOfAllSubtasks() {
        ArrayList<Subtask> listOfAllSubtasks = super.getListOfAllSubtasks();
        save();
        return listOfAllSubtasks;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public ArrayList<Subtask> getListOfEpicsSubtasks(Integer id) {
        ArrayList<Subtask> listOfEpicsSubtasks = super.getListOfEpicsSubtasks(id);
        save();
        return listOfEpicsSubtasks;
    }

    private void save() {
        List<String> lines = new ArrayList<>(List.of("id,type,name,status,description,epic")); //Создали шапку
        for (int i = 0; i < allTasksById.size(); i++) {
            lines.add(taskToString(allTasksById.get(i))); //Добавили все задачи в порядке возрастания id
        }
        lines.add(""); //Добавили отступ
        lines.add(historyToString()); //Добавили историю
        try {
            Files.write(autoSaveFile, lines, StandardCharsets.UTF_8); //Записали в файл
        } catch (IOException e) {
            throw new ManagerSaveException(); //Бросили собственное непроверяемое исключение
        }
    }

    private String taskToString(Task task) {
        if (task.getClass().getSimpleName().equals("SingleTask")) {
            return String.format("%d,%s,%s,%s,%s", task.getId(), TaskType.TASK.name(), task.getTitle(),
                    ((SingleTask) task).getStatus().name(), task.getDescription());
        } else if (task.getClass().getSimpleName().equals("Epic")) {
            return String.format("%d,%s,%s,%s,%s", task.getId(), TaskType.EPIC.name(), task.getTitle(),
                    ((Epic) task).getStatus().name(), task.getDescription());
        } else {
            return String.format("%d,%s,%s,%s,%s,%d", task.getId(), TaskType.SUBTASK.name(), task.getTitle(),
                    ((Subtask) task).getStatus().name(), task.getDescription(), ((Subtask) task).getEpicId());
        }
    }

    private String historyToString() {
        List<Task> history = super.getHistory(); //Получили список истории задач
        StringBuilder historyToString = new StringBuilder();
        for (Task t: history) { //Для каждой задачи из списка
            historyToString.append(String.format("%d,", t.getId())); //Добавляем в конец строки id,
        }
        if (historyToString.length() > 0) {
            historyToString.setLength(historyToString.length() - 1); //Удалили последнюю "," для красоты)
        }
        return historyToString.toString();
    }

    private static FileBackedTasksManager loadFromFile(Path fileBacked) {
        FileBackedTasksManager fileBackedTasksManager = Managers.getDefault(fileBacked);
        try {
            List<String> lines = new ArrayList<>(Files.readAllLines(fileBacked, StandardCharsets.UTF_8));
            for (int i = 1; i < lines.size() - 2; i++) {
                Task task = fileBackedTasksManager.taskFromString(lines.get(i));
                if (task.getClass().getSimpleName().equals("Subtask")) {
                    Epic epic = (Epic) fileBackedTasksManager.allTasksById.get(((Subtask) task).getEpicId());
                    epic.addSubtasksId(task.getId()); //Положили в нужный эпик id сабтаска
                }
                fileBackedTasksManager.allTasksById.put(task.getId(), task);
            }
            List<Integer> historyIds = fileBackedTasksManager.historyFromString(lines.get(lines.size()-1));
            for (Integer id: historyIds) {
                fileBackedTasksManager.inMemoryHistoryManager.add(fileBackedTasksManager.allTasksById.get(id));
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
        return fileBackedTasksManager;
    }

    private Task taskFromString(String taskLine) {
        String[] taskElements = taskLine.split(",");
        if (taskElements[1].equals(TaskType.TASK.name())) { //Если это обычная таска
            return new SingleTask(Integer.parseInt(taskElements[0]), taskElements[2], taskElements[4],
                    Status.valueOf(taskElements[3])); //Вернули SingleTask
        } else if (taskElements[1].equals(TaskType.EPIC.name())) { //Если это эпик
            return new Epic(Integer.parseInt(taskElements[0]), taskElements[2], taskElements[4],
                    Status.valueOf(taskElements[3])); //Вернули Epic
        } else {
            return new Subtask(Integer.parseInt(taskElements[0]), taskElements[2], taskElements[4],
                    Status.valueOf(taskElements[3]), Integer.parseInt(taskElements[5]));
        }
    }

    private List<Integer> historyFromString(String historyLine) {
        String[] historyElements = historyLine.split(",");
        List<Integer> historyIds = new ArrayList<>();
        for (String e: historyElements) {
            historyIds.add(Integer.parseInt(e));
        }
        return historyIds;
    }

    public static void main(String[] args) {

        // Для проверки сохранения данных в файл

        TaskManager fileBackedTasksManager = Managers.getDefault(Paths.get("C:\\Users\\Кирилл\\Desktop\\kanban.csv"));

        fileBackedTasksManager.addSingleTask(new SingleTask("Задача 1", "Описание задачи 1", Status.NEW)); // 0
        fileBackedTasksManager.addSingleTask(new SingleTask("Задача 2", "Описание задачи 2", Status.NEW)); // 1

        fileBackedTasksManager.addEpic(new Epic("Эпик 1", "Описание эпика 1")); // 2

        fileBackedTasksManager.addSubtask(new Subtask("Сабтаск 1", "Описание сабтаска 1", Status.NEW, 2)); // 3
        fileBackedTasksManager.addSubtask(new Subtask("Сабтаск 2", "Описание сабтакса 2", Status.IN_PROGRESS, 2)); // 4
        fileBackedTasksManager.addSubtask(new Subtask("Сабтаск 3", "Описание сабтакса 3", Status.DONE, 2)); // 5

        fileBackedTasksManager.addEpic(new Epic("Эпик 2", "Описание эпика 2")); // 6

        System.out.println(fileBackedTasksManager.getSingleTaskById(0));;
        System.out.println(fileBackedTasksManager.getSubtaskById(4));
        System.out.println(fileBackedTasksManager.getEpicById(2));
        System.out.println(fileBackedTasksManager.getSingleTaskById(0));
        System.out.println(fileBackedTasksManager.getSubtaskById(4));
        System.out.println(fileBackedTasksManager.getSubtaskById(5));
        System.out.println(fileBackedTasksManager.getSingleTaskById(1));

        /* Для проверки восстановления данных из файла

        TaskManager fileBackedTasksManager = loadFromFile(Paths.get("C:\\Users\\Кирилл\\Desktop\\kanban.csv"));
        System.out.println(fileBackedTasksManager.getSingleTaskById(0));;
        System.out.println(fileBackedTasksManager.getSubtaskById(4));
        System.out.println(fileBackedTasksManager.getEpicById(2));
        System.out.println(fileBackedTasksManager.getSingleTaskById(0));
        System.out.println(fileBackedTasksManager.getSubtaskById(4));
        System.out.println(fileBackedTasksManager.getSubtaskById(5));
        System.out.println(fileBackedTasksManager.getSingleTaskById(1)); */
    }
}
