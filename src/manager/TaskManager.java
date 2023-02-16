package manager;

import tasks.BaseTask;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private Integer newId;
    HashMap<Integer, BaseTask> allTasksById;

    public TaskManager() {
        this.allTasksById = new HashMap<>();
        this.newId = 0;
    }

    private Integer getNewId() {
        return newId++; // Генератор уникальных id
    }

    public void calculateStatus(Epic epic) {
        if (epic.getSubtasksId().size() == 0) { // Если у эпика нет сабтасков
            epic.setStatus("NEW"); // Статус эпика  "NEW"
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
                    epic.setStatus("IN_PROGRESS"); // Иначе статус эпика IN_PROGRESS
                }
            }
        }
    }

    public void addTask(Task task) {
        task.setId(getNewId()); // Присваиваем таску новый id
        allTasksById.put(task.getId(), task); // Кладем новый таск в общую мапу
    }

    public void addEpic(Epic epic) {
        epic.setId(getNewId()); // Присваиваем эпику новый id
        allTasksById.put(epic.getId(), epic); // Кладем новый эпик в общую мапу
    }

    public void addSubtask(Subtask subtask) {
        subtask.setId(getNewId()); // Присваиваем сабтаску новый id
        Epic rightEpic = (Epic) allTasksById.get(subtask.getEpicId()); // Получаем нужный эпик из мапы
        if (rightEpic == null) {
            return;
        }
        rightEpic.addSubtasksId(subtask.getId()); // Кладем у эпика в список subtasksId id нового сабтаска
        allTasksById.put(subtask.getId(), subtask); // Кладем новый сабтаск в общую мапу
        calculateStatus(rightEpic); // Пересчитываем статус эпика
    }

    public void updateTask(Task task) {
        allTasksById.put(task.getId(), task); // Кладем обновленный таск в мапу по индексу
    }

    public void updateEpic(Epic epic) {
        Epic oldEpic = (Epic) allTasksById.get(epic.getId()); // Достаем старый эпик из мапы и приводим к классу Epic
        if (oldEpic == null) {
            return;
        }
        epic.setSubtasksId(oldEpic.getSubtasksId()); // Перекладываем список subtasksId из старого эпика в обновленный
        epic.setStatus(oldEpic.getStatus()); // Перекладываем статус из старого эпика в обновленный
        allTasksById.put(epic.getId(), epic); // Кладем обновленный эпик в мапу по индексу
    }

    public void updateSubtask(Subtask subtask) {
        allTasksById.put(subtask.getId(), subtask); // Кладем обновленный сабтаск в мапу по индексу
        Epic epic = (Epic) allTasksById.get(subtask.getEpicId()); // Получаем нужный эпик из мапы
        if (epic == null) {
            return;
        }
        calculateStatus(epic); // Пересчитываем статус эпика
    }

    public Task getTaskById(Integer id) {
        return (Task) allTasksById.get(id); // Возвращает таск типа Task по id
    }

    public Epic getEpicById(Integer id) {
        return (Epic) allTasksById.get(id); // Возвращает эпик типа Epic по id
    }

    public Subtask getSubtaskById(Integer id) {
        return (Subtask) allTasksById.get(id); // Возвращает сабтаск типа Subtask по id
    }

    public void deleteTaskById(Integer id) {
        allTasksById.remove(id); // Удаляет таск по id
    }

    public void deleteEpicById(Integer id) {
        Epic epic = (Epic) allTasksById.get(id); // Получаем эпик по id
        if (epic == null) {
            return;
        }
        for (Integer subtaskId : epic.getSubtasksId()) { // Для каждого id из списка subtasksId нашего эпика
            allTasksById.remove(subtaskId); // Удаляем сабтаск по id из общей мапы
        } // Удалили все сабтаски эпика
        allTasksById.remove(id); // Теперь удаляем сам эпик
    }

    public void deleteSubtaskById(Integer id) {
        Subtask subtask = (Subtask) allTasksById.get(id); // Получаем сабтаск по id
        if (subtask == null) {
            return;
        } // Если же такой subtask != null, то эпик точно != null (иначе мы не смогли бы добавить этот сабтаск ранее)
        Epic epic = (Epic) allTasksById.get(subtask.getEpicId()); // Получаем эпик, к которому относится сабтаск
        epic.getSubtasksId().remove(id); // Удаляем id сабтаска из списка subtasksId эпика
        allTasksById.remove(id); // Удаляем сабтаск по id из общей мапы
        calculateStatus(epic); // Пересчитываем статус эпика
    }

    public ArrayList<Task> getListOfAllTasks() {
        ArrayList<Task> listOfAllTasks = new ArrayList<>(); // Создаем список
        for (BaseTask baseTask : allTasksById.values()) { // Для каждого элемента мапы со всеми задачами
            if (baseTask.getClass().getSimpleName().equals("Task")) { // Если класс задачи совпал с искомым
                listOfAllTasks.add((Task) baseTask); // Добавляем в список таск
            }
        }
        return listOfAllTasks;
    }

    public ArrayList<Epic> getListOfAllEpics() {
        ArrayList<Epic> listOfAllEpics = new ArrayList<>(); // Создаем список
        for (BaseTask baseTask : allTasksById.values()) { // Для каждого элемента мапы со всеми задачами
            if (baseTask.getClass().getSimpleName().equals("Epic")) { // Если класс задачи совпал с искомым
                listOfAllEpics.add((Epic) baseTask); // Добавляем в список эпик
            }
        }
        return listOfAllEpics;
    }

    public ArrayList<Subtask> getListOfAllSubtasks() {
        ArrayList<Subtask> listOfAllSubtasks = new ArrayList<>(); // Создаем список
        for (BaseTask baseTask : allTasksById.values()) { // Для каждого элемента мапы со всеми задачами
            if (baseTask.getClass().getSimpleName().equals("Subtask")) { // Если класс задачи совпал с искомым
                listOfAllSubtasks.add((Subtask) baseTask);
            }
        }
        return listOfAllSubtasks;
    }

    public void removeAllTasks() {
        ArrayList<Task> listOfAllTasks = getListOfAllTasks(); // Получили список тасков
        for (Task task : listOfAllTasks) { // Для каждого таска из списка
            allTasksById.remove(task.getId()); // Берем id таска и удаляем его из общей мапы
        }
    }

    public void removeAllEpics() {
        removeAllSubtasks(); // Удалили все сабтаски (не могут существовать без эпиков)
        ArrayList<Epic> listOfAllEpics = getListOfAllEpics(); // Получили список эпиков
        for (Epic epic : listOfAllEpics) { // Для каждого эпика из списка
            allTasksById.remove(epic.getId()); // Берем id эпика и удаляем его из общей мапы
        }
    }

    public void removeAllSubtasks() {
        ArrayList<Subtask> listOfAllSubtasks = getListOfAllSubtasks(); // Получили список сабтасков
        for (Subtask subtask : listOfAllSubtasks) { // Для каждого сабтаска из списка
            allTasksById.remove(subtask.getId()); // Берем id сабтаска и удаляем его из общей мапы
        }
        ArrayList<Epic> listOfAllEpics = getListOfAllEpics(); // Получили список всех эпиков
        for (Epic epic : listOfAllEpics) { // Для каждого эпика из списка
            epic.getSubtasksId().clear(); // Удалили все id из списка subtasksId
        } // Теперь все эпики без информации и сабтасках
    }

    public ArrayList<Subtask> getListOfEpicsSubtasks(Integer id) {
        ArrayList<Subtask> listOfEpicsSubtasks = new ArrayList<>();
        Epic epic = (Epic) allTasksById.get(id); // Получили доступ к полям эпика
        for (Integer subtaskId : epic.getSubtasksId()) { // Для каждого id из списка subtasksId эпика
            listOfEpicsSubtasks.add((Subtask) allTasksById.get(subtaskId)); // Берем сабтаск из мапы и кладем в список
        }
        return listOfEpicsSubtasks;
    }
}
