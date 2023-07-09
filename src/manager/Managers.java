package manager;
import manager.history.*;
import manager.oldTaskManager.FileBackedTasksManager;
import manager.oldTaskManager.InMemoryTaskManager;
import manager.oldTaskManager.TaskManager;

import java.nio.file.Path;

public abstract class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getDefault(Path autoSaveFile) {
        return new FileBackedTasksManager(autoSaveFile);
    }
}
