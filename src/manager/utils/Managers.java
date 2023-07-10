package manager.utils;
import com.google.gson.*;
import manager.HttpTaskManager;
import manager.history.*;
import manager.oldTaskManager.FileBackedTasksManager;
import manager.oldTaskManager.InMemoryTaskManager;
import manager.oldTaskManager.TaskManager;
import tasks.SingleTask;
import tasks.Task;
import tasks.TaskType;

import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public static HttpTaskManager getDefault(String url) {
        return new HttpTaskManager(url);
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }
}
