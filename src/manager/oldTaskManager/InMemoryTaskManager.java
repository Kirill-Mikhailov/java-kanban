package manager.oldTaskManager;

import manager.Managers;
import manager.history.HistoryManager;
import tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private Integer newId;
    protected final HashMap<Integer, Task> allTasksById;
    protected final HistoryManager inMemoryHistoryManager;

    public InMemoryTaskManager() {
        this.allTasksById = new HashMap<>();
        this.newId = 0;
        this.inMemoryHistoryManager = Managers.getDefaultHistory();
    }

    private Integer getNewId() {
        return newId++; // Генератор уникальных id
    }

    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }

    @Override
    public void calculateStatus(Epic epic) {
        if (epic.getSubtasksId().isEmpty()) { // Если у эпика нет сабтасков
            epic.setStatus(Status.NEW); // Статус эпика  "NEW"
        } else {
            for (Integer subtaskId : epic.getSubtasksId()) { // Для каждого id из списка subtasksId
                Subtask firstSubtask = (Subtask) allTasksById.get(epic.getSubtasksId().get(0));
                // Достаем из мапы таскменеджера первый сабтаск и приводим его к типу Subtask для работы с полем status
                Subtask rightSubtask = (Subtask) allTasksById.get(subtaskId);
                // Достаем из мапы таскменеджера нужный сабтаск и приводим его к типу Subtask для работы с полем status
                if (rightSubtask.getStatus().equals(firstSubtask.getStatus())) { /* Если статус каждого нового сабтаска
                равен статусу самого первого сабтаска из списка subtasksId */
                    epic.setStatus(firstSubtask.getStatus()); // Статус эпика равен статусу его первого сабтаска
                } else {
                    epic.setStatus(Status.IN_PROGRESS); // Иначе статус эпика IN_PROGRESS
                }
            }
        }
    }

    @Override
    public void addSingleTask(SingleTask singleTask) {
        singleTask.setId(getNewId()); // Присваиваем таску новый id
        allTasksById.put(singleTask.getId(), singleTask); // Кладем новый таск в общую мапу
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(getNewId()); // Присваиваем эпику новый id
        allTasksById.put(epic.getId(), epic); // Кладем новый эпик в общую мапу
    }

    @Override
    public void addSubtask(Subtask subtask) {
        Epic rightEpic = (Epic) allTasksById.get(subtask.getEpicId()); // Получаем нужный эпик из мапы
        if (rightEpic == null) {
            return;
        }
        subtask.setId(getNewId()); // Присваиваем сабтаску новый id
        rightEpic.addSubtasksId(subtask.getId()); // Кладем у эпика в список subtasksId id нового сабтаска
        allTasksById.put(subtask.getId(), subtask); // Кладем новый сабтаск в общую мапу
        calculateStatus(rightEpic); // Пересчитываем статус эпика
    }

    @Override
    public void updateSingleTask(SingleTask singleTask) {
        allTasksById.put(singleTask.getId(), singleTask); // Кладем обновленный таск в мапу по индексу
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic oldEpic = (Epic) allTasksById.get(epic.getId()); // Достаем старый эпик из мапы и приводим к классу Epic
        if (oldEpic == null) {
            return;
        }
        epic.setSubtasksId(oldEpic.getSubtasksId()); // Перекладываем список subtasksId из старого эпика в обновленный
        epic.setStatus(oldEpic.getStatus()); // Перекладываем статус из старого эпика в обновленный
        allTasksById.put(epic.getId(), epic); // Кладем обновленный эпик в мапу по индексу
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Epic epic = (Epic) allTasksById.get(subtask.getEpicId()); // Получаем нужный эпик из мапы
        if (epic == null) {
            return;
        }
        allTasksById.put(subtask.getId(), subtask); // Кладем обновленный сабтаск в мапу по индексу
        calculateStatus(epic); // Пересчитываем статус эпика
    }

    @Override
    public SingleTask getSingleTaskById(Integer id) {
        inMemoryHistoryManager.add(allTasksById.get(id));
        return (SingleTask) allTasksById.get(id); // Возвращает таск типа Task по id
    }

    @Override
    public Epic getEpicById(Integer id) {
        inMemoryHistoryManager.add(allTasksById.get(id));
        return (Epic) allTasksById.get(id); // Возвращает эпик типа Epic по id
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        inMemoryHistoryManager.add(allTasksById.get(id));
        return (Subtask) allTasksById.get(id); // Возвращает сабтаск типа Subtask по id
    }

    @Override
    public void deleteSingleTaskById(Integer id) {
        allTasksById.remove(id); // Удаляет таск по id
        inMemoryHistoryManager.remove(id); // Удаляем из истории просмотров
    }

    @Override
    public void deleteEpicById(Integer id) {
        Epic epic = (Epic) allTasksById.get(id); // Получаем эпик по id
        if (epic == null) {
            return;
        }
        for (Integer subtaskId : epic.getSubtasksId()) { // Для каждого id из списка subtasksId нашего эпика
            allTasksById.remove(subtaskId); // Удаляем сабтаск по id из общей мапы
            inMemoryHistoryManager.remove(subtaskId); // Удаляем сабтаск из истории просмотров
        } // Удалили все сабтаски эпика
        allTasksById.remove(id); // Теперь удаляем сам эпик
        inMemoryHistoryManager.remove(id); // Удаляем эпик из истории просмотров
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        Subtask subtask = (Subtask) allTasksById.get(id); // Получаем сабтаск по id
        if (subtask == null) {
            return;
        } // Если же такой subtask != null, то эпик точно != null (иначе мы не смогли бы добавить этот сабтаск ранее)
        Epic epic = (Epic) allTasksById.get(subtask.getEpicId()); // Получаем эпик, к которому относится сабтаск
        epic.getSubtasksId().remove(id); // Удаляем id сабтаска из списка subtasksId эпика
        allTasksById.remove(id); // Удаляем сабтаск по id из общей мапы
        calculateStatus(epic); // Пересчитываем статус эпика
        inMemoryHistoryManager.remove(id); // Удаляем из истории просмотров
    }

    @Override
    public ArrayList<SingleTask> getListOfAllSingleTasks() {
        ArrayList<SingleTask> listOfAllSingleTasks = new ArrayList<>(); // Создаем список
        for (Task task : allTasksById.values()) { // Для каждого элемента мапы со всеми задачами
            if (task.getClass().getSimpleName().equals("SingleTask")) { // Если класс задачи совпал с искомым
                listOfAllSingleTasks.add((SingleTask) task); // Добавляем в список таск
                inMemoryHistoryManager.add(task); // Добавляем таск в список просмотров (ведь он тоже просматривается)
            }
        }
        return listOfAllSingleTasks;
    }

    @Override
    public ArrayList<Epic> getListOfAllEpics() {
        ArrayList<Epic> listOfAllEpics = new ArrayList<>(); // Создаем список
        for (Task task : allTasksById.values()) { // Для каждого элемента мапы со всеми задачами
            if (task.getClass().getSimpleName().equals("Epic")) { // Если класс задачи совпал с искомым
                listOfAllEpics.add((Epic) task); // Добавляем в список эпик
                inMemoryHistoryManager.add(task); // Добавляем таск в список просмотров (ведь он тоже просматривается)
            }
        }
        return listOfAllEpics;
    }

    @Override
    public ArrayList<Subtask> getListOfAllSubtasks() {
        ArrayList<Subtask> listOfAllSubtasks = new ArrayList<>(); // Создаем список
        for (Task task : allTasksById.values()) { // Для каждого элемента мапы со всеми задачами
            if (task.getClass().getSimpleName().equals("Subtask")) { // Если класс задачи совпал с искомым
                listOfAllSubtasks.add((Subtask) task);
                inMemoryHistoryManager.add(task); // Добавляем таск в список просмотров (ведь он тоже просматривается)
            }
        }
        return listOfAllSubtasks;
    }

    @Override
    public void removeAllTasks() {
        ArrayList<SingleTask> listOfAllSingleTasks = getListOfAllSingleTasks(); // Получили список тасков
        for (SingleTask singleTask : listOfAllSingleTasks) { // Для каждого таска из списка
            allTasksById.remove(singleTask.getId()); // Берем id таска и удаляем его из общей мапы
            inMemoryHistoryManager.remove(singleTask.getId()); // Удаляем из истории просмотров
        }
    }

    @Override
    public void removeAllEpics() {
        removeAllSubtasks(); // Удалили все сабтаски (не могут существовать без эпиков)
        ArrayList<Epic> listOfAllEpics = getListOfAllEpics(); // Получили список эпиков
        for (Epic epic : listOfAllEpics) { // Для каждого эпика из списка
            allTasksById.remove(epic.getId()); // Берем id эпика и удаляем его из общей мапы
            inMemoryHistoryManager.remove(epic.getId()); // Удаляем из истории просмотров
        }
    }

    @Override
    public void removeAllSubtasks() {
        ArrayList<Subtask> listOfAllSubtasks = getListOfAllSubtasks(); // Получили список сабтасков
        for (Subtask subtask : listOfAllSubtasks) { // Для каждого сабтаска из списка
            allTasksById.remove(subtask.getId()); // Берем id сабтаска и удаляем его из общей мапы
            inMemoryHistoryManager.remove(subtask.getId()); // Удаляем из истории просмотров
        }
        ArrayList<Epic> listOfAllEpics = getListOfAllEpics(); // Получили список всех эпиков
        for (Epic epic : listOfAllEpics) { // Для каждого эпика из списка
            epic.getSubtasksId().clear(); // Удалили все id из списка subtasksId
        } // Теперь все эпики без информации и сабтасках
    }

    @Override
    public ArrayList<Subtask> getListOfEpicsSubtasks(Integer id) {
        ArrayList<Subtask> listOfEpicsSubtasks = new ArrayList<>();
        Epic epic = (Epic) allTasksById.get(id); // Получили доступ к полям эпика
        for (Integer subtaskId : epic.getSubtasksId()) { // Для каждого id из списка subtasksId эпика
            listOfEpicsSubtasks.add((Subtask) allTasksById.get(subtaskId)); // Берем сабтаск из мапы и кладем в список
            inMemoryHistoryManager.add(allTasksById.get(subtaskId)); /* Добавляем таск в список просмотров
            (ведь он тоже просматривается) */
        }
        return listOfEpicsSubtasks;
    }
}
