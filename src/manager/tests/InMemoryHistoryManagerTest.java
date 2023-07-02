package manager.tests;

import manager.Managers;
import manager.history.HistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {
    HistoryManager historyManager;
    SingleTask task1; // "title1T", "description1T", Status.NEW, LocalDateTime.of(2023, 7, 1, 14, 0), 240
    SingleTask task2; // "title2T", "description2T", Status.IN_PROGRESS, LocalDateTime.of(2023, 7, 2, 14, 0), 240
    Epic epic1; // "title1E", "description1E"
    Epic epic2; // "title2E", "description2E"
    Subtask subtask1; // "title1S", "description1S", Status.NEW, 0, LocalDateTime.of(2023, 7, 3, 14, 0), 240
    Subtask subtask2; // "title2S", "description2S", Status.DONE, 0, LocalDateTime.of(2023, 7, 4, 14, 0), 240
    Subtask subtask3; // "title3S", "description3S", Status.IN_PROGRESS, 0, LocalDateTime.of(2023, 7, 5, 14, 0), 240

    @BeforeEach
    void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        this.epic1 = new Epic(0,"title1E", "description1E");
        this.epic2 = new Epic(1,"title2E", "description2E");
        this.task1 = new SingleTask(2,"title1T", "description1T", Status.NEW,
                LocalDateTime.of(2023, 7, 1, 14, 0), 240);
        this.task2 = new SingleTask(3,"title2T", "description2T", Status.IN_PROGRESS,
                LocalDateTime.of(2023, 7, 2, 14, 0), 240);
        this.subtask1 = new Subtask(4,"title1S", "description1S", Status.NEW, 0,
                LocalDateTime.of(2023, 7, 3, 14, 0), 240);
        this.subtask2 = new Subtask(5,"title2S", "description2S", Status.DONE, 0,
                LocalDateTime.of(2023, 7, 4, 14, 0), 240);
        this.subtask3 = new Subtask(6,"title3S", "description3S", Status.IN_PROGRESS, 0,
                LocalDateTime.of(2023, 7, 5, 14, 0), 240);
    }

    @Test
    void getHistory() { // Получение списка истории с пустой историей задач
        assertTrue(historyManager.getHistory().isEmpty(), "История не пустая");
    }

    @Test
    void add() { // Добавление задачи: стандартный кейс + дублирование + стандартный кейс getHistory() +
        historyManager.add(task1);                                    // getHistory() с дублированием
        historyManager.add(task2);
        historyManager.add(epic1);
        historyManager.add(epic2);
        historyManager.add(subtask1);
        historyManager.add(subtask2);
        historyManager.add(subtask3);
        historyManager.add(task1);
        historyManager.add(epic2);
        historyManager.add(subtask3);

        List<Task> checkList = new ArrayList<>();
        checkList.add(task2);
        checkList.add(epic1);
        checkList.add(subtask1);
        checkList.add(subtask2);
        checkList.add(task1);
        checkList.add(epic2);
        checkList.add(subtask3);

        assertIterableEquals(checkList, historyManager.getHistory(), "Списки не совпадают");
    }

    @Test
    void remove() { // Удаление из истории: начало, середина, конец + удаление с пустым списком истории +
        historyManager.remove(4);                             // + getHistory() с удалением из истории
        assertTrue(historyManager.getHistory().isEmpty(), "Список не пустой");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(epic1);
        historyManager.add(epic2);
        historyManager.add(subtask1);
        historyManager.add(subtask2);
        historyManager.add(subtask3);

        historyManager.remove(task1.getId());
        historyManager.remove(epic2.getId());
        historyManager.remove(subtask3.getId());

        List<Task> checkList = new ArrayList<>();
        checkList.add(task2);
        checkList.add(epic1);
        checkList.add(subtask1);
        checkList.add(subtask2);

        assertIterableEquals(checkList, historyManager.getHistory(), "Списки не совпадают");
    }
}
