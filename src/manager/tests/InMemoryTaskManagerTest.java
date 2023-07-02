package manager.tests;

import manager.Managers;
import manager.oldTaskManager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import tasks.Epic;
import tasks.SingleTask;
import tasks.Status;
import tasks.Subtask;

import java.time.LocalDateTime;

public class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {
    @BeforeEach
    void beforeEach() {
        super.manager = Managers.getDefault();
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
}
