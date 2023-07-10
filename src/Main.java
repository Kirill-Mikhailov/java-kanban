import com.google.gson.Gson;
import manager.HttpTaskManager;
import manager.utils.Managers;
import servers.HttpTaskServer;
import servers.KVServer;
import servers.KVTaskClient;
import tasks.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws IOException {
        /* Gson gson = Managers.getGson();
        KVServer kvServer = new KVServer();
        kvServer.start();
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();

        SingleTask task1 = new SingleTask("title1T", "description1T", Status.NEW,
                LocalDateTime.of(2023, 7, 1, 14, 0), 240);
        SingleTask task2 = new SingleTask("title2T", "description2T", Status.IN_PROGRESS,
                LocalDateTime.of(2023, 7, 2, 14, 0), 240);
        Epic epic1 = new Epic("title1E", "description1E");
        Epic epic2 = new Epic("title2E", "description2E");
        Subtask subtask1 = new Subtask("title1S", "description1S", Status.NEW, 0,
                LocalDateTime.of(2023, 7, 3, 14, 0), 240);
        Subtask subtask2 = new Subtask("title2S", "description2S", Status.DONE, 0,
                LocalDateTime.of(2023, 7, 4, 14, 0), 240);
        Subtask subtask3 = new Subtask("title3S", "description3S", Status.IN_PROGRESS, 0,
                LocalDateTime.of(2023, 7, 5, 14, 0), 240);

        System.out.println(gson.toJson(epic1));

        System.out.println(gson.toJson(subtask1)); */

        SingleTask task1 = new SingleTask("title1T", "description1T", Status.NEW,
                LocalDateTime.of(2023, 7, 1, 14, 0), 240);

    }

}
