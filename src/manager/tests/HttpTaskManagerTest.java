package manager.tests;

import manager.HttpTaskManager;
import manager.oldTaskManager.FileBackedTasksManager;
import manager.oldTaskManager.TaskManager;
import manager.utils.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servers.HttpTaskServer;
import servers.KVServer;
import tasks.Epic;
import tasks.SingleTask;
import tasks.Status;
import tasks.Subtask;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTest extends TaskManagerTest<TaskManager>{
    KVServer kvServer;

    @BeforeEach
    void beforeEach() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        manager = Managers.getDefault("http://localhost:8078");
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

    @AfterEach
    void afterEach() {
        kvServer.stop();
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

        HttpTaskManager loadedManager = Managers.getDefault("http://localhost:8078");
        loadedManager.load();
        assertEquals(manager, loadedManager, "Менеджеры не совпадают");
    }
}

