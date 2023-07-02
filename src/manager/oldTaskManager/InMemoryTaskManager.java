package manager.oldTaskManager;

import manager.Managers;
import manager.exceptions.TaskManagerException;
import manager.history.HistoryManager;
import tasks.*;

import java.time.Duration;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private Integer newId;
    protected final HashMap<Integer, Task> allTasksById;
    protected final HistoryManager inMemoryHistoryManager;
    protected TreeSet<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        this.allTasksById = new HashMap<>();
        this.newId = 0;
        this.inMemoryHistoryManager = Managers.getDefaultHistory();
        prioritizedTasks = new TreeSet<>((t1, t2) -> {
            if (t1.getStartTime() == null && t2.getStartTime() != null) {
                return 1;
            } else if (t1.getStartTime() != null && t2.getStartTime() == null) {
                return -1;
            } else if (t1.getStartTime() != null && t2.getStartTime() != null) {
                if (t1.getStartTime().isAfter(t2.getStartTime())) {
                    return 1;
                } else if (t1.getStartTime().isBefore(t2.getStartTime())) {
                    return -1;
                } else {
                    return t1.getId() - t2.getId();
                }
            } else {
                return t1.getId() - t2.getId();
            }
        });
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
    public void calculateDateAndDuration(Epic epic) {
        if (!epic.getSubtasksId().isEmpty()) { // Если у эпика есть сабтаски
            TreeSet<Task> epicSubtasks = new TreeSet<>((s1, s2) -> {
                if (s1.getStartTime().isAfter(s2.getStartTime())) {
                    return 1;
                } else if (s1.getStartTime().isBefore(s2.getStartTime())) {
                    return -1;
                } else {
                    return 0;
                }
            });
            long duration = 0;
            for (Integer subtaskId : epic.getSubtasksId()) { // Для каждого id из списка subtasksId
                epicSubtasks.add(allTasksById.get(subtaskId)); // Достаем из мапы сабтаск и кладем его в TreeSet
                duration += allTasksById.get(subtaskId).getDuration().toMinutes(); // Суммируем длительность сабтасков
            } // Заполнили epicSubtasks сабтасками эпика
            epic.setStartTime(epicSubtasks.first().getStartTime());
            epic.setEndTime(epicSubtasks.last().getEndTime());
            epic.setDuration(Duration.ofMinutes(duration));
        } else { // Если сабтасков нет
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(null);
        }
    }

    @Override
    public void addSingleTask(SingleTask singleTask) throws TaskManagerException {
        timeValidation(singleTask);
        singleTask.setId(getNewId()); // Присваиваем таску новый id
        allTasksById.put(singleTask.getId(), singleTask); // Кладем новый таск в общую мапу
        prioritizedTasks.add(singleTask); // Кладем новый таск в TreeSet
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(getNewId()); // Присваиваем эпику новый id
        allTasksById.put(epic.getId(), epic); // Кладем новый эпик в общую мапу
        prioritizedTasks.add(epic); // Кладем новый эпик в TreeSet
    }

    @Override
    public void addSubtask(Subtask subtask) throws TaskManagerException {
        try {
            timeValidation(subtask);
            Epic rightEpic = (Epic) allTasksById.get(subtask.getEpicId()); // Получаем нужный эпик из мапы
            prioritizedTasks.remove(rightEpic); // Удаляем эпик из TreeSet || NPE
            subtask.setId(getNewId()); // Присваиваем сабтаску новый id
            rightEpic.addSubtasksId(subtask.getId()); // Кладем у эпика в список subtasksId id нового сабтаска
            allTasksById.put(subtask.getId(), subtask); // Кладем новый сабтаск в общую мапу
            calculateStatus(rightEpic); // Пересчитываем статус эпика
            calculateDateAndDuration(rightEpic); // Пересчитываем поля времени эпика
            prioritizedTasks.add(rightEpic); // Кладем обновленный эпик в TreeSet
            prioritizedTasks.add(subtask); // Кладем новый сабтаск в TreeSet
        } catch (NullPointerException e) {
            throw new TaskManagerException("incorrect epicId");
        }
    }

    @Override
    public void updateSingleTask(SingleTask singleTask) throws TaskManagerException {
        if (singleTask.getId() == null) { // Если id == null
            throw new TaskManagerException("id must not be null");
        } else if (allTasksById.get(singleTask.getId()) == null) { // Или в мапе нет задачи с таким id
            throw new TaskManagerException("incorrect id");
        }
        timeValidation(singleTask);
        prioritizedTasks.remove(allTasksById.get(singleTask.getId())); // Удаляем таск из TreeSet
        allTasksById.put(singleTask.getId(), singleTask); // Кладем обновленный таск в мапу по индексу
        prioritizedTasks.add(singleTask); // Кладем обновленный таск в TreeSet
    }

    @Override
    public void updateEpic(Epic epic) throws TaskManagerException {
        if (epic.getId() == null) { // Если id == null
            throw new TaskManagerException("id must not be null");
        } else if (allTasksById.get(epic.getId()) == null) { // Или в мапе нет эпика с таким id
            throw new TaskManagerException("incorrect id");
        }
        Epic oldEpic = (Epic) allTasksById.get(epic.getId()); // Достаем старый эпик из мапы и приводим к классу Epic
        prioritizedTasks.remove(oldEpic); // Удаляем эпик из TreeSet
        epic.setSubtasksId(oldEpic.getSubtasksId()); // Перекладываем список subtasksId из старого эпика в обновленный
        epic.setStatus(oldEpic.getStatus()); // Перекладываем статус из старого эпика в обновленный
        epic.setStartTime(oldEpic.getStartTime()); // Перекладываем startTime
        epic.setDuration(oldEpic.getDuration()); // Перекладываем duration
        epic.setEndTime(oldEpic.getEndTime()); // Перекладываем endTime
        allTasksById.put(epic.getId(), epic); // Кладем обновленный эпик в мапу по индексу
        prioritizedTasks.add(epic); // Кладем обновленный эпик в TreeSet
    }

    @Override
    public void updateSubtask(Subtask subtask) throws TaskManagerException {
        if (subtask.getId() == null) { // Если id == null
            throw new TaskManagerException("id must not be null");
        } else if (allTasksById.get(subtask.getId()) == null) { // Или в мапе нет сабтаска с таким id
            throw new TaskManagerException("incorrect id");
        } else if (allTasksById.get(subtask.getEpicId()) == null) { // Или в мапе нет эпика с таким epicId
            throw new TaskManagerException("incorrect epicId");
        }
        timeValidation(subtask);
        Epic epic = (Epic) allTasksById.get(subtask.getEpicId()); // Получаем нужный эпик из мапы
        prioritizedTasks.remove(epic); // Удаляем эпик из TreeSet || NPE
        prioritizedTasks.remove(allTasksById.get(subtask.getId())); // Удаляем сабтаск из TreeSet || NPE
        allTasksById.put(subtask.getId(), subtask); // Кладем обновленный сабтаск в мапу по индексу
        calculateStatus(epic); // Пересчитываем статус эпика
        calculateDateAndDuration(epic); // Пересчитываем поля времени и продолжительности эпика
        prioritizedTasks.add(epic); // Кладем обновленный эпик в TreeSet
        prioritizedTasks.add(subtask); // Кладем обновленный сабтаск в TreeSet
    }

    @Override
    public SingleTask getSingleTaskById(Integer id) throws TaskManagerException {
        if (id == null) { // Если id == null
            throw new TaskManagerException("id must not be null");
        } else if (allTasksById.get(id) == null) { // Или в мапе нет задачи с таким id
            throw new TaskManagerException("incorrect id");
        }
        inMemoryHistoryManager.add(allTasksById.get(id));
        return (SingleTask) allTasksById.get(id); // Возвращает таск типа Task по id
    }

    @Override
    public Epic getEpicById(Integer id) throws TaskManagerException {
        if (id == null) { // Если id == null
            throw new TaskManagerException("id must not be null");
        } else if (allTasksById.get(id) == null) { // Или в мапе нет задачи с таким id
            throw new TaskManagerException("incorrect id");
        }
        inMemoryHistoryManager.add(allTasksById.get(id)); // Добавили в историю просмотров
        return (Epic) allTasksById.get(id); // Возвращает эпик типа Epic по id
    }

    @Override
    public Subtask getSubtaskById(Integer id) throws TaskManagerException {
        if (id == null) { // Если id == null
            throw new TaskManagerException("id must not be null");
        } else if (allTasksById.get(id) == null) { // Или в мапе нет задачи с таким id
            throw new TaskManagerException("incorrect id");
        }
        inMemoryHistoryManager.add(allTasksById.get(id)); // Добавили в историю просмотров
        return (Subtask) allTasksById.get(id); // Возвращает сабтаск типа Subtask по id
    }

    @Override
    public void deleteSingleTaskById(Integer id) throws TaskManagerException {
        if (id == null) { // Если id == null
            throw new TaskManagerException("id must not be null");
        } else if (allTasksById.get(id) == null) { // Или в мапе нет задачи с таким id
            throw new TaskManagerException("incorrect id");
        }
        prioritizedTasks.remove(allTasksById.get(id)); // Удаляем задачу из TreeSet
        allTasksById.remove(id); // Удаляет таск по id
        inMemoryHistoryManager.remove(id); // Удаляем из истории просмотров
    }

    @Override
    public void deleteEpicById(Integer id) throws TaskManagerException {
        if (id == null) { // Если id == null
            throw new TaskManagerException("id must not be null");
        } else if (allTasksById.get(id) == null) { // Или в мапе нет задачи с таким id
            throw new TaskManagerException("incorrect id");
        }
        Epic epic = (Epic) allTasksById.get(id); // Получаем эпик по id
        prioritizedTasks.remove(epic); // Удаляем эпик из TreeSet
        for (Integer subtaskId : epic.getSubtasksId()) { // Для каждого id из списка subtasksId нашего эпика
            prioritizedTasks.remove(allTasksById.get(subtaskId)); // Удаляем сабтаск из TreeSet
            allTasksById.remove(subtaskId); // Удаляем сабтаск по id из общей мапы
            inMemoryHistoryManager.remove(subtaskId); // Удаляем сабтаск из истории просмотров
        } // Удалили все сабтаски эпика
        allTasksById.remove(id); // Теперь удаляем сам эпик из мапы
        inMemoryHistoryManager.remove(id); // Удаляем эпик из истории просмотров
    }

    @Override
    public void deleteSubtaskById(Integer id) throws TaskManagerException {
        if (id == null) { // Если id == null
            throw new TaskManagerException("id must not be null");
        } else if (allTasksById.get(id) == null) { // Или в мапе нет задачи с таким id
            throw new TaskManagerException("incorrect id");
        } // Если же такой subtask != null, то эпик точно != null (иначе мы не смогли бы добавить этот сабтаск ранее)
        Subtask subtask = (Subtask) allTasksById.get(id); // Получаем сабтаск по id
        prioritizedTasks.remove(subtask); // Удаляем сабтаск из TreeSet
        Epic epic = (Epic) allTasksById.get(subtask.getEpicId()); // Получаем эпик, к которому относится сабтаск
        prioritizedTasks.remove(epic); // Удаляем эпик из TreeSet
        epic.getSubtasksId().remove(id); // Удаляем id сабтаска из списка subtasksId эпика
        allTasksById.remove(id); // Удаляем сабтаск по id из общей мапы
        calculateStatus(epic); // Пересчитываем статус эпика
        calculateDateAndDuration(epic); // Пересчитываем поля эпика
        prioritizedTasks.add(epic); // Добавляем обновленный эпик в TreeSet
        inMemoryHistoryManager.remove(id); // Удаляем сабтаск из истории просмотров
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
            prioritizedTasks.remove(singleTask); // Удаляем таск из TreeSet
            allTasksById.remove(singleTask.getId()); // Берем id таска и удаляем его из общей мапы
            inMemoryHistoryManager.remove(singleTask.getId()); // Удаляем из истории просмотров
        }
    }

    @Override
    public void removeAllEpics() {
        removeAllSubtasks(); // Удалили все сабтаски (не могут существовать без эпиков)
        ArrayList<Epic> listOfAllEpics = getListOfAllEpics(); // Получили список эпиков
        for (Epic epic : listOfAllEpics) { // Для каждого эпика из списка
            prioritizedTasks.remove(epic); // Удаляем эпик из TreeSet
            allTasksById.remove(epic.getId()); // Берем id эпика и удаляем его из общей мапы
            inMemoryHistoryManager.remove(epic.getId()); // Удаляем из истории просмотров
        }
    }

    @Override
    public void removeAllSubtasks() {
        ArrayList<Subtask> listOfAllSubtasks = getListOfAllSubtasks(); // Получили список сабтасков
        for (Subtask subtask : listOfAllSubtasks) { // Для каждого сабтаска из списка
            prioritizedTasks.remove(subtask); // Удаляем сабтаск из TreeSet
            allTasksById.remove(subtask.getId()); // Берем id сабтаска и удаляем его из общей мапы
            inMemoryHistoryManager.remove(subtask.getId()); // Удаляем из истории просмотров
        }
        ArrayList<Epic> listOfAllEpics = getListOfAllEpics(); // Получили список всех эпиков
        for (Epic epic : listOfAllEpics) { // Для каждого эпика из списка
            if (!epic.getSubtasksId().isEmpty()) { // Если список subtasksId у эпика не пустой
                prioritizedTasks.remove(epic); // Удаляем эпик из TreeSet
                epic.getSubtasksId().clear(); // Удалили все id из списка subtasksId
                calculateStatus(epic); // Пересчитали статус эпика
                calculateDateAndDuration(epic); // Пересчитываем поля эпика
                prioritizedTasks.add(epic); // Добавили в TreeSet обновленный эпик
            }
        } // Теперь все эпики без информации и сабтасках
    }

    @Override
    public ArrayList<Subtask> getListOfEpicsSubtasks(Integer id) {
        if (id == null) { // Если id == null
            throw new TaskManagerException("id must not be null");
        } else if (allTasksById.get(id) == null) { // Или в мапе нет задачи с таким id
            throw new TaskManagerException("incorrect epicId");
        }
        ArrayList<Subtask> listOfEpicsSubtasks = new ArrayList<>();
        Epic epic = (Epic) allTasksById.get(id); // Получили доступ к полям эпика
        for (Integer subtaskId : epic.getSubtasksId()) { // Для каждого id из списка subtasksId эпика
            listOfEpicsSubtasks.add((Subtask) allTasksById.get(subtaskId)); // Берем сабтаск из мапы и кладем в список
            inMemoryHistoryManager.add(allTasksById.get(subtaskId)); /* Добавляем сабтаск в список просмотров
                                                                        (ведь он тоже просматривается) */
        }
        return listOfEpicsSubtasks;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        if (prioritizedTasks.size() != 0) {
            return new ArrayList<>(prioritizedTasks);
        }
        return new ArrayList<>();
    }

    @Override
    public void timeValidation(SingleTask task) throws TaskManagerException { //Валидацию эпика делать не нужно,
        for (Task t: prioritizedTasks) {                                      //поэтому исключим такую возможность
            if (t.getClass().getSimpleName().equals("Epic") || task.getId() != null && task.getId().equals(t.getId())) {
                continue;
            } /* Если это эпик - пропускаем: их границы времени зависят от сабтасков + предполагается, что между двумя
            сабтасками эпика можно выполнять другие задачи. Или если id != null (задача обновляется) и встретили эту же
            задачу в множестве - пропускаем, чтобы избежать ложной ошибки валидации (нет смысла проверять саму себя) */
            if (!(task.getEndTime().isBefore(t.getStartTime()) || task.getStartTime().isAfter(t.getEndTime()))) {
                throw new TaskManagerException("timeValidationError");
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryTaskManager that = (InMemoryTaskManager) o;
        return Objects.equals(allTasksById, that.allTasksById) && Objects.equals(inMemoryHistoryManager,
                that.inMemoryHistoryManager) && Objects.equals(prioritizedTasks, that.prioritizedTasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allTasksById, inMemoryHistoryManager, prioritizedTasks);
    }
}
