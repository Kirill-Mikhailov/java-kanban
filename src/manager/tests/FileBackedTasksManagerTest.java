package manager.tests;

import manager.oldTaskManager.FileBackedTasksManager;
import manager.Managers;
import manager.exceptions.ManagerSaveException;
import manager.oldTaskManager.TaskManager;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SingleTask;
import tasks.Status;
import tasks.Subtask;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class FileBackedTasksManagerTest extends TaskManagerTest<TaskManager> {
    Path autoSaveFile = Paths.get("..\\kanban.csv");

    @BeforeEach
    void beforeEach() {
        try {
            Files.writeString(autoSaveFile, ""); // Стерли содержимое
        } catch (IOException e) {
            throw new ManagerSaveException("unable to write to file"); //Бросили собственное непроверяемое исключение
        }
        super.manager = Managers.getDefault(autoSaveFile);
        this.task1 = new SingleTask("title1T", "description1T", Status.NEW,
                LocalDateTime.of(2023, 7, 1, 14, 0), 240);
        this.task2 = new SingleTask("title2T", "description2T", Status.IN_PROGRESS,
                LocalDateTime.of(2023, 7, 2, 14, 0), 240);
        this.epic1 = new Epic("title1E", "description1E");
        this.epic2 = new Epic("title2E", "description2E");
        this.subtask1 = new Subtask("title1S", "description1S", Status.NEW, 0,
                LocalDateTime.of(2023, 7, 3, 14, 0), 240);
        this.subtask2 = new Subtask("title2S", "description2S", Status.DONE, 0,
                LocalDateTime.of(2023, 7, 4, 14, 0), 240);
        this.subtask3 = new Subtask("title3S", "description3S", Status.IN_PROGRESS, 0,
                LocalDateTime.of(2023, 7, 5, 14, 0), 240);
    }

    @Test
    void loadFromFileWithEmptyListOfTasks() { // Проверка сохранения и восстановления с пустым списком подзадач
        FileBackedTasksManager loadedManager = FileBackedTasksManager.loadFromFile(
               autoSaveFile);
        assertEquals(this.manager, loadedManager, "Менеджеры не совпадают");
    }

    @Test
    void loadFromFileWithEpicWithoutSubtasks() { // Проверка сохранения и восстановления с эпиком без подзадач
        manager.addEpic(epic1);

        FileBackedTasksManager loadedManager = FileBackedTasksManager.loadFromFile(
               autoSaveFile);
        assertEquals(this.manager, loadedManager, "Менеджеры не совпадают");
    }

    @Test
    void loadFromFileWithEmptyListOfHistory() { // Проверка сохранения и восстановления без истории
        manager.addEpic(epic1);
        manager.addSingleTask(task1);
        manager.addSubtask(subtask1);
        manager.addEpic(epic2);
        manager.addSingleTask(task2);
        manager.addSubtask(subtask3);
        manager.addSubtask(subtask2);

        manager.deleteSubtaskById(subtask2.getId());
        manager.deleteSingleTaskById(task1.getId());
        manager.deleteEpicById(epic2.getId());

        FileBackedTasksManager loadedManager = FileBackedTasksManager.loadFromFile(
                autoSaveFile);
        assertEquals(this.manager, loadedManager, "Менеджеры не совпадают");
    }

    @Test
    void loadFromFile() { // Стандартный кейс проверки сохранения и восстановления
        manager.addEpic(epic1);
        manager.addSingleTask(task1);
        manager.addSubtask(subtask1);
        manager.addEpic(epic2);
        manager.addSingleTask(task2);
        manager.addSubtask(subtask3);
        manager.addSubtask(subtask2);

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

        FileBackedTasksManager loadedManager = FileBackedTasksManager.loadFromFile(
                autoSaveFile);
        assertEquals(this.manager, loadedManager, "Менеджеры не совпадают");
    }
}
