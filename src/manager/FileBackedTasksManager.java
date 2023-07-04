package manager;

import manager.exceptions.ManagerSaveException;
import manager.oldTaskManager.InMemoryTaskManager;
import tasks.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final Path autoSaveFile;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy|HH:mm");
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

    @Override
    public List<Task> getPrioritizedTasks() {
        List<Task> prioritizedTasks = super.getPrioritizedTasks();
        save();
        return prioritizedTasks;
    }

    private void save() throws ManagerSaveException {
        List<String> lines = new ArrayList<>(List.of("id,type,name,status,description,startTime,duration,epic")); //Создали шапку
        if (!allTasksById.isEmpty()) {
            for (int i = 0; i <= Collections.max(allTasksById.keySet()); i++) {
                if (allTasksById.containsKey(i)) {
                    lines.add(taskToString(allTasksById.get(i))); //Добавили все задачи в порядке возрастания id
                }
            }
        }
        lines.add(""); //Добавили отступ
        lines.add(historyToString()); //Добавили историю
        try {
            Files.write(autoSaveFile, lines, StandardCharsets.UTF_8); //Записали в файл
        } catch (IOException e) {
            throw new ManagerSaveException("unable to write to file"); //Бросили собственное непроверяемое исключение
        }
    }

    private String taskToString(Task task) {
        if (task.getClass().getSimpleName().equals("SingleTask")) {
            return String.format("%d,%s,%s,%s,%s,%s,%s", task.getId(), TaskType.TASK.name(), task.getTitle(),
                    ((SingleTask) task).getStatus().name(), task.getDescription(),
                    task.getStartTime().format(formatter), task.getDuration().toMinutes());
        } else if (task.getClass().getSimpleName().equals("Epic")) {
            if (task.getStartTime() == null) { // Если у эпика пустые поля времени (== нет сабтасков)
                return String.format("%d,%s,%s,%s,%s,%s,%s", task.getId(), TaskType.EPIC.name(), task.getTitle(),
                        ((Epic) task).getStatus().name(), task.getDescription(), null, null); //Записываем null
            } else {
                return String.format("%d,%s,%s,%s,%s,%s,%s", task.getId(), TaskType.EPIC.name(), task.getTitle(),
                        ((Epic) task).getStatus().name(), task.getDescription(),
                        task.getStartTime().format(formatter), task.getDuration().toMinutes());
            }
        } else {
            return String.format("%d,%s,%s,%s,%s,%s,%s,%d", task.getId(), TaskType.SUBTASK.name(), task.getTitle(),
                    ((Subtask) task).getStatus().name(), task.getDescription(), task.getStartTime().format(formatter),
                    task.getDuration().toMinutes(), ((Subtask) task).getEpicId());
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

    public static FileBackedTasksManager loadFromFile (Path fileBacked) throws ManagerSaveException {
        FileBackedTasksManager fileBackedTasksManager = Managers.getDefault(fileBacked);
        try {
            List<String> lines = new ArrayList<>(Files.readAllLines(fileBacked, StandardCharsets.UTF_8));
            for (int i = 1; i < lines.size() - 2; i++) {
                Task task = fileBackedTasksManager.taskFromString(lines.get(i));
                fileBackedTasksManager.allTasksById.put(task.getId(), task);
                fileBackedTasksManager.prioritizedTasks.add(task);
                if (task.getClass().getSimpleName().equals("Subtask")) {
                    Epic epic = (Epic) fileBackedTasksManager.allTasksById.get(((Subtask) task).getEpicId());
                    fileBackedTasksManager.prioritizedTasks.remove(epic); // Удаляем эпик из TreeSet
                    epic.addSubtasksId(task.getId()); //Положили в нужный эпик id сабтаска
                    fileBackedTasksManager.calculateDateAndDuration(epic); //Пересчитали поля времени у эпика
                    fileBackedTasksManager.prioritizedTasks.add(epic); // Кладем обновленный эпик в TreeSet
                }
            }
            if (lines.size() > 0) {
                List<Integer> historyIds = fileBackedTasksManager.historyFromString(lines.get(lines.size() - 1));
                for (Integer id : historyIds) {
                    fileBackedTasksManager.inMemoryHistoryManager.add(fileBackedTasksManager.allTasksById.get(id));
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("unable to load from file");
        }
        return fileBackedTasksManager;
    }

    private Task taskFromString(String taskLine) {
        String[] taskElements = taskLine.split(",");
        if (taskElements[1].equals(TaskType.TASK.name())) { //Если это обычная таска
            return new SingleTask(Integer.parseInt(taskElements[0]), taskElements[2], taskElements[4],
                    Status.valueOf(taskElements[3]), LocalDateTime.parse(taskElements[5], formatter),
                    Integer.parseInt(taskElements[6])); //Вернули SingleTask
        } else if (taskElements[1].equals(TaskType.EPIC.name())) { //Если это эпик
            return new Epic(Integer.parseInt(taskElements[0]), taskElements[2], taskElements[4],
                    Status.valueOf(taskElements[3])); //Вернули Epic
        } else { //Если сабтаск
            return new Subtask(Integer.parseInt(taskElements[0]), taskElements[2], taskElements[4],
                    Status.valueOf(taskElements[3]), Integer.parseInt(taskElements[7]),
                    LocalDateTime.parse(taskElements[5], formatter), Integer.parseInt(taskElements[6])); //Subtask
        }
    }

    private List<Integer> historyFromString(String historyLine) {
        if (!historyLine.isBlank()) {
            String[] historyElements = historyLine.split(",");
            List<Integer> historyIds = new ArrayList<>();
            for (String e : historyElements) {
                historyIds.add(Integer.parseInt(e));
            }
            return historyIds;
        } else {
            return Collections.emptyList();
        }
    }

    public static void main(String[] args) {
    }
}
