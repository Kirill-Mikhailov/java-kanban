package manager.tests;

import manager.exceptions.TaskManagerException;
import manager.oldTaskManager.TaskManager;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

abstract class TaskManagerTest<T extends TaskManager> {
    T manager;
    SingleTask task1; // "title1T", "description1T", Status.NEW, LocalDateTime.of(2023, 7, 1, 14, 0), 240
    SingleTask task2; // "title2T", "description2T", Status.IN_PROGRESS, LocalDateTime.of(2023, 7, 2, 14, 0), 240
    Epic epic1; // "title1E", "description1E"
    Epic epic2; // "title2E", "description2E"
    Subtask subtask1; // "title1S", "description1S", Status.NEW, 0, LocalDateTime.of(2023, 7, 3, 14, 0), 240
    Subtask subtask2; // "title2S", "description2S", Status.DONE, 0, LocalDateTime.of(2023, 7, 4, 14, 0), 240
    Subtask subtask3; // "title3S", "description3S", Status.IN_PROGRESS, 0, LocalDateTime.of(2023, 7, 5, 14, 0), 240

    @Test
    void addSingleTask() { // Добавление задачи
        manager.addSingleTask(task1);
        SingleTask savedTask = manager.getSingleTaskById(0);
        assertNotNull(savedTask, "Задача не найдена");
        assertEquals(task1, savedTask, "Задачи не совпадают");
    }

    @Test
    void addEpic() { // Добавление эпика
        manager.addEpic(epic1);
        Epic savedEpic = manager.getEpicById(0);
        assertNotNull(savedEpic, "Эпик не найден");
        assertEquals(epic1, savedEpic, "Эпики не совпадают");
        assertTrue(savedEpic.getSubtasksId().isEmpty(), "У нового эпика не может быть подзадач");
    }

    @Test
    void addSubtask() { // Добавление сабтаска
        manager.addEpic(epic1);

        Subtask subtask3 = new Subtask("title3S", "description3S", Status.NEW, null,
                LocalDateTime.of(2023, 7, 4, 14, 0), 240);
        TaskManagerException nullEpicId = assertThrows(
                TaskManagerException.class,
                () -> manager.addSubtask(subtask3)
        );
        assertEquals("incorrect epicId", nullEpicId.getMessage()); // Добавление сабтаска с пустым EpicId

        Subtask subtask4 = new Subtask("title4S", "description4S", Status.NEW, 5,
                LocalDateTime.of(2023, 7, 4, 14, 0), 240);
        TaskManagerException nonexistentEpicId = assertThrows(
                TaskManagerException.class,
                () -> manager.addSubtask(subtask4)
        );
        assertEquals("incorrect epicId", nonexistentEpicId.getMessage()); // С несуществующим EpicId

        manager.addSubtask(subtask1);
        Subtask savedSubtask = manager.getSubtaskById(1);
        assertNotNull(savedSubtask, "Сабтаск не найден");
        assertEquals(subtask1, savedSubtask, "Сабтаски не совпадают"); // Стандартный кейс
        assertEquals(subtask1.getEpicId(), epic1.getId()); // Проверяем наличие эпика у сабтаски
        assertTrue(epic1.getSubtasksId().contains(savedSubtask.getId())); // Наличие сабтаски у эпика
    }

    @Test
    void calculateStatusWithEmptyListOfSubtasksAndWithNewSubtasks() { // Расчет статуса эпика
        manager.addEpic(epic1);
        Epic savedEpic = manager.getEpicById(0);
        assertEquals(Status.NEW, savedEpic.getStatus(), "Статус эпика без подзадач должен быть NEW");

        manager.addSubtask(subtask1);
        savedEpic = manager.getEpicById(0);
        assertEquals(Status.NEW, savedEpic.getStatus(),
                "Если у всех подзадач статус NEW, то и у эпика должен быть NEW");
    }

    @Test
    void calculateStatusWithDoneSubtasksAndWithNewAndDoneSubtasks() {
        manager.addEpic(epic1);
        manager.addSubtask(subtask2);
        Epic savedEpic = manager.getEpicById(0);
        assertEquals(Status.DONE, savedEpic.getStatus(),
                "Если у всех подзадач статус DONE, то и у эпика должен быть DONE");

        manager.addSubtask(subtask1);
        savedEpic = manager.getEpicById(0);
        assertEquals(Status.IN_PROGRESS, savedEpic.getStatus(),
                "Если у подзадач разные статусы, то у эпика должен быть IN_PROGRESS");
    }

    @Test
    void calculateStatusWithInProgressSubtasks() {
        manager.addEpic(epic1);
        manager.addSubtask(subtask3);
        Epic savedEpic = manager.getEpicById(0);
        assertEquals(Status.IN_PROGRESS, savedEpic.getStatus(),
                "Если у всех подзадач статус IN_PROGRESS, то и у эпика должен быть IN_PROGRESS");
    }

    @Test
    void calculateDateAndDurationWithEmptyListOfSubtasksAndWithSubtasks() {
        manager.addEpic(epic1);
        Epic savedEpic = manager.getEpicById(0);
        assertNull(savedEpic.getStartTime(), "Если у эпика нет подзадач, то startTime должно быть null");
        assertNull(savedEpic.getDuration(), "Если у эпика нет подзадач, то duration должно быть null");
        assertNull(savedEpic.getEndTime(), "Если у эпика нет подзадач, то endTime должно быть null");

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        savedEpic = manager.getEpicById(0);
        assertNotNull(savedEpic.getStartTime(), "Если у эпика есть подзадачи, то startTime не должно быть null");
        assertNotNull(savedEpic.getDuration(), "Если у эпика есть подзадачи, то duration не должно быть null");
        assertNotNull(savedEpic.getEndTime(), "Если у эпика есть подзадачи, то endTime не должно быть null");
        assertEquals(subtask1.getStartTime(), savedEpic.getStartTime(),
                "Если у эпика есть подзадачи, то startTime должно быть как у сабтаски с самым ранним началом");
        assertEquals(subtask2.getEndTime(), savedEpic.getEndTime(),
                "Если у эпика есть подзадачи, то endTime должно быть как у сабтаски с самым поздним окончанием");
        assertEquals(subtask1.getDuration().plus(subtask2.getDuration()), savedEpic.getDuration(),
                "Если у эпика есть подзадачи, то duration должно быть равно сумме duration всех подзадач");
    }

    @Test
    void getSingleTaskById() { // Получить задачу по id
        TaskManagerException nonexistentId = assertThrows(
                TaskManagerException.class,
                () -> manager.getSingleTaskById(0)
        );
        assertEquals("incorrect id", nonexistentId.getMessage()); /* Получение задачи из пустого списка задач
                                                                             или по несуществующему id */
        TaskManagerException nullId = assertThrows(
                TaskManagerException.class,
                () -> manager.getSingleTaskById(null)
        );
        assertEquals("id must not be null", nullId.getMessage()); // По пустому id

        // Реализация стандартного кейса совпадает с методом addSingleTask() => уже проверяется
    }

    @Test
    void getEpicById() { // Получить эпик по id
        TaskManagerException nonexistentId = assertThrows(
                TaskManagerException.class,
                () -> manager.getEpicById(0)
        );
        assertEquals("incorrect id", nonexistentId.getMessage()); /* Получение эпика из пустого списка задач
                                                                             или по несуществующему id */
        TaskManagerException nullId = assertThrows(
                TaskManagerException.class,
                () -> manager.getEpicById(null)
        );
        assertEquals("id must not be null", nullId.getMessage()); // По пустому id

        // Реализация стандартного кейса совпадает с методом addEpic() => уже проверяется
    }

    @Test
    void getSubtaskById() { // Получить сабтаск по id
        TaskManagerException nonexistentId = assertThrows(
                TaskManagerException.class,
                () -> manager.getSubtaskById(0)
        );
        assertEquals("incorrect id", nonexistentId.getMessage()); /* Получение сабтаски из пустого списка задач
                                                                             или по несуществующему id */
        TaskManagerException nullId = assertThrows(
                TaskManagerException.class,
                () -> manager.getSubtaskById(null)
        );
        assertEquals("id must not be null", nullId.getMessage()); // По пустому id

        // Стандартный кейс уже реализуется в методе addSubtask() => уже проверяется
    }

    @Test
    void updateSingleTask() { // Обновить задачу
        SingleTask updateTask1 = new SingleTask(0,"updateTitle1T", "updateDescription1T", Status.DONE,
                LocalDateTime.of(2023, 7, 1, 14, 0), 240);
        TaskManagerException nonexistentId = assertThrows(
                TaskManagerException.class,
                () -> manager.updateSingleTask(updateTask1)
        );
        assertEquals("incorrect id", nonexistentId.getMessage()); /* Обновление задачи при пустом списке задач
                                                                             или по несуществующему id */
        manager.addSingleTask(task1);
        manager.updateSingleTask(updateTask1);
        SingleTask updatedTask = manager.getSingleTaskById(0);
        assertEquals(updateTask1, updatedTask, "Задачи не совпадают"); // Стандартный кейс

        SingleTask updateTaskWithNullId = new SingleTask(null,"updateTitle1T", "updateDescription1T",
                Status.DONE, LocalDateTime.of(2023, 7, 1, 14, 0), 240);
        TaskManagerException nullId = assertThrows(
                TaskManagerException.class,
                () -> manager.updateSingleTask(updateTaskWithNullId)
        );
        assertEquals("id must not be null", nullId.getMessage()); // По пустому id
    }

    @Test
    void updateEpic() { // Обновить эпик
        Epic updateEpic1 = new Epic(0, "updateTitle1E", "updateDescription1E");
        TaskManagerException nonexistentId = assertThrows(
                TaskManagerException.class,
                () -> manager.updateEpic(updateEpic1)
        );
        assertEquals("incorrect id", nonexistentId.getMessage()); /* Обновление эпика при пустом списке задач
                                                                             или по несуществующему id */
        manager.addEpic(epic1);
        manager.updateEpic(updateEpic1);
        Epic updatedEpic = manager.getEpicById(0);
        assertEquals(updateEpic1, updatedEpic, "Эпики не совпадают"); // Стандартный кейс

        Epic updateEpicWithNullId = new Epic(null, "updateTitle1E", "updateDescription1E");
        TaskManagerException nullId = assertThrows(
                TaskManagerException.class,
                () -> manager.updateEpic(updateEpicWithNullId)
        );
        assertEquals("id must not be null", nullId.getMessage()); // По пустому id
    }

    @Test
    void updateSubtask() { // Обновить сабтаск
        Subtask updateSubtask1 = new Subtask(1,"updateTitle1S", "updateDescription1S", Status.DONE,
                0, LocalDateTime.of(2023, 7, 3, 14, 0), 240);
        TaskManagerException nonexistentId = assertThrows(
                TaskManagerException.class,
                () -> manager.updateSubtask(updateSubtask1)
        );
        assertEquals("incorrect id", nonexistentId.getMessage()); /* Обновление задачи при пустом списке задач
                                                                             или по несуществующему id */
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        manager.updateSubtask(updateSubtask1);
        Subtask updateSubtask = manager.getSubtaskById(1);
        assertEquals(updateSubtask1, updateSubtask, "Сабтаски не совпадают"); // Стандартный кейс
        assertEquals(epic1.getId(), updateSubtask.getEpicId()); // Проверили наличие эпика у сабтаски
        assertEquals(Status.DONE, epic1.getStatus()); // Проверили статус эпика

        Subtask updateSubtaskWithNullId = new Subtask(null,"updateTitle1S", "updateDescription1S",
                Status.DONE, 0, LocalDateTime.of(2023, 7, 3, 14, 0), 240);
        TaskManagerException nullId = assertThrows(
                TaskManagerException.class,
                () -> manager.updateSubtask(updateSubtaskWithNullId)
        );
        assertEquals("id must not be null", nullId.getMessage()); // По пустому id

        Subtask updateSubtaskWithNonexistentEpicId = new Subtask(1,"updateTitle1S",
                "updateDescription1S", Status.DONE, 5,
                LocalDateTime.of(2023, 7, 3, 14, 0), 240);
        TaskManagerException nonexistentEpicId = assertThrows(
                TaskManagerException.class,
                () -> manager.updateSubtask(updateSubtaskWithNonexistentEpicId)
        );
        assertEquals("incorrect epicId", nonexistentEpicId.getMessage()); // По несуществующему epicId

        Subtask updateSubtaskWithNullEpicId = new Subtask(1,"updateTitle1S",
                "updateDescription1S", Status.DONE, null,
                LocalDateTime.of(2023, 7, 3, 14, 0), 240);
        TaskManagerException nullEpicId = assertThrows(
                TaskManagerException.class,
                () -> manager.updateSubtask(updateSubtaskWithNullEpicId)
        );
        assertEquals("incorrect epicId", nullEpicId.getMessage()); // По пустому epicId
    }

    @Test
    void deleteSingleTaskById() { // Удалить задачу по id
        TaskManagerException emptyListOfTasks = assertThrows(
                TaskManagerException.class,
                () -> manager.deleteSingleTaskById(0)
        );
        assertEquals("incorrect id", emptyListOfTasks.getMessage()); // Удаление задачи с пустым списком задач
                                                                             // или по неверному id
        manager.addSingleTask(task1);

        TaskManagerException nullId = assertThrows(
                TaskManagerException.class,
                () -> manager.deleteSingleTaskById(null)
        );
        assertEquals("id must not be null", nullId.getMessage()); // По пустому id

        manager.deleteSingleTaskById(0);
        assertTrue(manager.getListOfAllSingleTasks().isEmpty(), "Задача не удалена"); // Стандартный кейс
    }

    @Test
    void deleteEpicById() { // Удалить эпик по id
        TaskManagerException emptyListOfTasks = assertThrows(
                TaskManagerException.class,
                () -> manager.deleteEpicById(0)
        );
        assertEquals("incorrect id", emptyListOfTasks.getMessage()); // Удаление эпика с пустым списком задач
                                                                             // или по неверному id
        manager.addEpic(epic1);

        TaskManagerException nullId = assertThrows(
                TaskManagerException.class,
                () -> manager.deleteEpicById(null)
        );
        assertEquals("id must not be null", nullId.getMessage()); // По пустому id

        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
        manager.deleteEpicById(0);
        assertTrue(manager.getListOfAllEpics().isEmpty(), "Эпик не удален"); // Стандартный кейс
        assertTrue(manager.getListOfAllSubtasks().isEmpty(), "Сабтаски эпика не удалены"); // Удаление сабтасков
    }

    @Test
    void deleteSubtaskById() { // Удалить сабтаск по Id
        TaskManagerException emptyListOfTasks = assertThrows(
                TaskManagerException.class,
                () -> manager.deleteSubtaskById(0)
        );
        assertEquals("incorrect id", emptyListOfTasks.getMessage()); // Удаление сабтаска с пустым списком задач
                                                                             // или по неверному id
        manager.addEpic(epic1);
        manager.addSubtask(subtask2);

        TaskManagerException nullId = assertThrows(
                TaskManagerException.class,
                () -> manager.deleteSubtaskById(null)
        );
        assertEquals("id must not be null", nullId.getMessage()); // По пустому id

        manager.deleteSubtaskById(1);
        assertTrue(manager.getListOfAllSubtasks().isEmpty(), "Сабтаск не удален"); // Стандартный кейс
        Epic epicAfterDeletingSubtask = manager.getEpicById(0);
        assertEquals(Status.NEW, epicAfterDeletingSubtask.getStatus(), "Если у эпика нет сабтасок - статус NEW");
        assertTrue(epicAfterDeletingSubtask.getSubtasksId().isEmpty(), "Не удален сабтаск у эпика");
        assertNull(epicAfterDeletingSubtask.getStartTime(), "Не обновлено поле startTime");
        assertNull(epicAfterDeletingSubtask.getDuration(), "Не обновлено поле duration");
        assertNull(epicAfterDeletingSubtask.getEndTime(), "Не обновлено поле endTime");
    }

    @Test
    void getListOfAllSingleTasks() { // Получить список всех задач
        assertTrue(manager.getListOfAllSingleTasks().isEmpty(), "Список не пустой"); // С пустым списком задач

        List<SingleTask> checklist = new ArrayList<>();
        checklist.add(task1);
        manager.addSingleTask(task1);
        checklist.add(task2);
        manager.addSingleTask(task2);
        assertFalse(manager.getListOfAllSingleTasks().isEmpty(), "Список пустой"); // Стандартный кейс
        assertIterableEquals(checklist, manager.getListOfAllSingleTasks(), "Списки не совпадают");
    }

    @Test
    void getListOfAllEpics() { // Получить список всех эпиков
        assertTrue(manager.getListOfAllEpics().isEmpty(), "Список не пустой"); // С пустым списком задач

        List<Epic> checklist = new ArrayList<>();
        checklist.add(epic1);
        manager.addEpic(epic1);
        checklist.add(epic2);
        manager.addEpic(epic2);
        assertFalse(manager.getListOfAllEpics().isEmpty(), "Список пустой"); // Стандартный кейс
        assertIterableEquals(checklist, manager.getListOfAllEpics(), "Списки не совпадают");
    }

    @Test
    void getListOfAllSubtasks() { // Получить список всех сабтасков
        assertTrue(manager.getListOfAllSubtasks().isEmpty(), "Список не пустой"); // С пустым списком задач

        List<Subtask> checklist = new ArrayList<>();
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        checklist.add(subtask1);
        manager.addSubtask(subtask1);
        checklist.add(subtask2);
        manager.addSubtask(subtask2);
        checklist.add(subtask3);
        manager.addSubtask(subtask3);
        Subtask subtask4 = new Subtask("title4S", "description4S", Status.IN_PROGRESS, 1,
                LocalDateTime.of(2023, 7, 6, 14, 0), 240);
        checklist.add(subtask4);
        manager.addSubtask(subtask4);
        assertFalse(manager.getListOfAllSubtasks().isEmpty(), "Список пустой"); // Стандартный кейс
        assertIterableEquals(checklist, manager.getListOfAllSubtasks(), "Списки не совпадают");
    }

    @Test
    void getListOfEpicsSubtasks() { // Получить список всех сабтасков эпика
        TaskManagerException emptyListOfTasks = assertThrows(
                TaskManagerException.class,
                () -> manager.getListOfEpicsSubtasks(0)
        );
        assertEquals("incorrect epicId", emptyListOfTasks.getMessage()); // Получение списка подзадач эпика с
                                                                // пустым списком задач или по несуществующему epicId
        TaskManagerException nullId = assertThrows(
                TaskManagerException.class,
                () -> manager.getListOfEpicsSubtasks(null)
        );
        assertEquals("id must not be null", nullId.getMessage()); // По пустому epicId

        List<Subtask> checklist = new ArrayList<>();
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        assertTrue(manager.getListOfEpicsSubtasks(0).isEmpty(), "Список не пустой");
        // С пустым списком сабтасков эпика
        checklist.add(subtask1);
        manager.addSubtask(subtask1);
        checklist.add(subtask2);
        manager.addSubtask(subtask2);
        checklist.add(subtask3);
        manager.addSubtask(subtask3);
        Subtask subtask4 = new Subtask("title4S", "description4S", Status.IN_PROGRESS, 1,
                LocalDateTime.of(2023, 7, 6, 14, 0), 240);
        manager.addSubtask(subtask4);
        assertFalse(manager.getListOfEpicsSubtasks(epic1.getId()).isEmpty(), "Список пустой"); // Стандартный кейс
        assertIterableEquals(checklist, manager.getListOfEpicsSubtasks(0), "Списки не совпадают");
    }

    @Test
    void removeAllTasks() { // Удаление всех задач
        manager.removeAllTasks();
        assertTrue(manager.getListOfAllSingleTasks().isEmpty()); // С пустым списком

        manager.addSingleTask(task1);
        manager.addSingleTask(task2);
        manager.removeAllTasks();
        assertTrue(manager.getListOfAllSingleTasks().isEmpty(), "Задачи не удалены"); // Стандартный кейс
    }

    @Test
    void removeAllEpics() { // Удаление всех эпиков
        manager.removeAllEpics();
        assertTrue(manager.getListOfAllEpics().isEmpty()); // С пустым списком

        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
        manager.removeAllEpics();
        assertTrue(manager.getListOfAllEpics().isEmpty(), "Эпики не удалены"); // Стандартный кейс
        assertTrue(manager.getListOfAllSubtasks().isEmpty(), "Без эпиков не может быть сабтасков");
    }

    @Test
    void removeAllSubtasks() { // Удаление всех сабтасков
        manager.removeAllSubtasks();
        assertTrue(manager.getListOfAllSubtasks().isEmpty()); // С пустым списком

        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
        manager.removeAllSubtasks();
        assertTrue(manager.getListOfAllSubtasks().isEmpty(), "Сабтаски не удалены"); // Стандартный кейс
        assertEquals(Status.NEW, epic1.getStatus(), "Статус эпика без сабтасков должен быть NEW");
        assertTrue(epic1.getSubtasksId().isEmpty(), "Если сабтасков нет, то и у эпика их не должно быть");
        assertNull(epic1.getStartTime(), "Не обновлено поле startTime");
        assertNull(epic1.getDuration(), "Не обновлено поле duration");
        assertNull(epic1.getEndTime(), "Не обновлено поле endTime");
    }

    @Test
    void getPrioritizedTasks() { // Получить список задач по приоритету
        assertTrue(manager.getPrioritizedTasks().isEmpty(), "Список задач по приоритету не пустой"); // С пустым

        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask3);
        manager.addSubtask(subtask2);
        manager.addSingleTask(task2);
        manager.addSingleTask(task1);
        manager.addEpic(epic2);
        List<Task> checklist = new ArrayList<>();
        checklist.add(task1);
        checklist.add(task2);
        checklist.add(epic1);
        checklist.add(subtask1);
        checklist.add(subtask2);
        checklist.add(subtask3);
        checklist.add(epic2);
        assertIterableEquals(checklist, manager.getPrioritizedTasks(), "Списки не совпадают"); //Стандартный кейс
    }

    @Test
    void timeValidation() { // Проверка валидации времени
        manager.addEpic(epic1);
        manager.addSingleTask(task1);
        manager.addSubtask(subtask1);

        SingleTask invalidTask = new SingleTask("titleT", "descriptionT", Status.NEW, // Пересекается
                LocalDateTime.of(2023, 7, 3, 14, 30), 240); // с subtask1

        Subtask invalidSubtask = new Subtask("titleS", "descriptionS", Status.NEW, 0, //Пересекается
                LocalDateTime.of(2023, 7, 1, 13, 30), 240); // с task1

        TaskManagerException addInvalidTask = assertThrows(
                TaskManagerException.class,
                () -> manager.addSingleTask(invalidTask)
        );
        assertEquals("timeValidationError", addInvalidTask.getMessage(),
                "Не должно быть пересечений при добавлении задачи");

        TaskManagerException addInvalidSubtask = assertThrows(
                TaskManagerException.class,
                () -> manager.addSubtask(invalidSubtask)
        );
        assertEquals("timeValidationError", addInvalidSubtask.getMessage(),
                "Не должно быть пересечений при добавлении сабтаска");

        SingleTask updateInvalidTask1 = new SingleTask(task1.getId(), "titleT", "descriptionT",
                Status.NEW, LocalDateTime.of(2023, 7, 3, 14, 30), 240);
        // Пересекается с subtask1

        Subtask updateInvalidSubtask1 = new Subtask(subtask1.getId(), "titleS", "descriptionS", Status.NEW,
                0, LocalDateTime.of(2023, 7, 1, 13, 30), 240);
        //Пересекается с task1

        TaskManagerException updateInvalidTask = assertThrows(
                TaskManagerException.class,
                () -> manager.updateSingleTask(updateInvalidTask1)
        );
        assertEquals("timeValidationError", updateInvalidTask.getMessage(),
                "Не должно быть пересечений при изменении задачи");

        TaskManagerException updateInvalidSubtask = assertThrows(
                TaskManagerException.class,
                () -> manager.updateSubtask(updateInvalidSubtask1)
        );
        assertEquals("timeValidationError", updateInvalidSubtask.getMessage(),
                "Не должно быть пересечений при изменении сабтаска");
    }

    @Test
    void getHistory() { // Проверка получения истории
        assertTrue(manager.getHistory().isEmpty(), "История не пустая"); // С пустым списком задач

        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask3);
        manager.addSubtask(subtask2);
        manager.addSingleTask(task1);
        manager.addSingleTask(task2);

        manager.getSingleTaskById(task1.getId());
        manager.getSubtaskById(subtask1.getId());
        manager.getSingleTaskById(task2.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(subtask2.getId());
        manager.getSubtaskById(subtask3.getId());
        manager.getEpicById(epic2.getId());

        manager.getSingleTaskById(task1.getId());
        manager.getEpicById(epic1.getId());

        manager.deleteSubtaskById(subtask2.getId());

        List<Task> checklist = new ArrayList<>();
        checklist.add(subtask1);
        checklist.add(task2);
        checklist.add(subtask3);
        checklist.add(epic2);
        checklist.add(task1);
        checklist.add(epic1);

        assertIterableEquals(checklist, manager.getHistory(), "Списки не совпадают"); // Стандартный кейс
    }
}
