package manager;

import com.google.gson.*;
import manager.oldTaskManager.FileBackedTasksManager;
import manager.utils.Managers;
import servers.KVTaskClient;
import tasks.*;

import java.util.*;

public class HttpTaskManager extends FileBackedTasksManager {
     private KVTaskClient kvTaskClient;
     private Gson gson;

    public HttpTaskManager(String url) {
        this.kvTaskClient = new KVTaskClient(url);
        this.gson = Managers.getGson();
    }

    @Override
    protected void save() {
        kvTaskClient.put("allTasksById", gson.toJson(this.allTasksById));
        kvTaskClient.put("prioritizedTasks", gson.toJson(this.prioritizedTasks));
        kvTaskClient.put("inMemoryHistoryManager", gson.toJson(this.inMemoryHistoryManager.getHistory()));
        kvTaskClient.put("newId", gson.toJson(this.newId));
    }

    public void load() {
        Optional<String> optAllTasksById = kvTaskClient.load("allTasksById");
        if (optAllTasksById.isPresent()) {
            List<Task> tasks = createTaskListFromJson(optAllTasksById.get());
            for (Task t: tasks) {
                this.allTasksById.put(t.getId(), t);
            }
        } else {
            System.out.println("Не удалось загрузить состояние менеджера: список всех задач");
        }

        Optional<String> optPrioritizedTasks = kvTaskClient.load("prioritizedTasks");
        if (optPrioritizedTasks.isPresent()) {
            List<Task> tasks = createTaskListFromJson(optPrioritizedTasks.get());
            this.prioritizedTasks.addAll(tasks);
        } else {
            System.out.println("Не удалось загрузить состояние менеджера: список задач по приоритету");
        }

        Optional<String> optInMemoryHistoryManager = kvTaskClient.load("inMemoryHistoryManager");
        if (optInMemoryHistoryManager.isPresent()) {
            List<Task> tasks = createTaskListFromJson(optInMemoryHistoryManager.get());
            for (Task t: tasks) {
                this.inMemoryHistoryManager.add(t);
            }
        } else {
            System.out.println("Не удалось загрузить состояние менеджера: история задач");
        }

        Optional<String> optNewId = kvTaskClient.load("newId");
        if (optNewId.isPresent()) {
            this.newId = gson.fromJson(optNewId.get(), Integer.class);
        } else {
            System.out.println("Не удалось загрузить состояние менеджера: счетчик идентификатора задач");
        }
    }

    public static List<Task> createTaskListFromJson(String json) {
        Gson gson = Managers.getGson();
        JsonElement jsonElement = JsonParser.parseString(json);
        JsonArray jsonArray = new JsonArray();
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            for(Map.Entry<String, JsonElement> entry : jsonObject.entrySet())
                jsonArray.add(entry.getValue());
        } else {
            jsonArray = jsonElement.getAsJsonArray();
        }
        List<Task> list = new ArrayList<>();
        for (JsonElement e: jsonArray) {
            JsonObject jsonObject = e.getAsJsonObject();
            String stringType = jsonObject.get("type").getAsString();
            TaskType type = TaskType.valueOf(stringType);
            switch (type) {
                case TASK:
                    SingleTask task = gson.fromJson(jsonObject, SingleTask.class);
                    list.add(task);
                    break;
                case EPIC:
                    Epic epic = gson.fromJson(jsonObject, Epic.class);
                    list.add(epic);
                    break;
                case SUBTASK:
                    Subtask subtask = gson.fromJson(jsonObject, Subtask.class);
                    list.add(subtask);
                    break;
            }
        }
        return list;
    }
}
