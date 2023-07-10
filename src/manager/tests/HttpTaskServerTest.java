package manager.tests;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.HttpTaskManager;
import manager.exceptions.TaskManagerException;
import manager.utils.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servers.HttpTaskServer;
import servers.KVServer;
import tasks.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class HttpTaskServerTest {

    HttpClient client;
    HttpTaskServer httpTaskServer;
    KVServer kvServer;
    Gson gson;

    SingleTask task1;
    SingleTask task2;
    Epic epic1;
    Epic epic2;
    Subtask subtask1;
    Subtask subtask2;
    Subtask subtask3;
    String jsonTask1;
    String jsonTask2;
    String jsonEpic1;
    String jsonEpic2;
    String jsonSubtask1;
    String jsonSubtask2;
    String jsonSubtask3;


    @BeforeEach
    void beforeEach() throws IOException {
        this.kvServer = new KVServer();
        kvServer.start();
        this.httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();

        this.client = HttpClient.newHttpClient();

        this.gson = Managers.getGson();

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

        this.jsonTask1 = gson.toJson(task1);
        this.jsonTask2 = gson.toJson(task2);
        this.jsonEpic1 = "{\"title\":\"title1E\",\"description\":\"description1E\"}";
        this.jsonEpic2 = "{\"title\":\"title2E\",\"description\":\"description2E\"}";
        this.jsonSubtask1 = gson.toJson(subtask1);
        this.jsonSubtask2 = gson.toJson(subtask2);
        this.jsonSubtask3 = gson.toJson(subtask3);
    }

    @AfterEach
    void afterEach() {
        httpTaskServer.stop();
        kvServer.stop();
    }

    @Test
    void addSingleTask() { // Добавление задачи
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask1))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode());
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте URL-адрес и повторите попытку.");
        }
    }

    @Test
    void addEpic() { // Добавление эпика
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic1))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode());
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте URL-адрес и повторите попытку.");
            e.printStackTrace();
        }
    }

    @Test
    void addSubtask() { // Добавление сабтаска
        addEpic();

        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubtask1))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode());
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте URL-адрес и повторите попытку.");
        }
    }

    @Test
    void getSingleTaskById() { // Получить задачу по id
        addSingleTask();

        URI url = URI.create("http://localhost:8080/tasks/task/?id=0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            SingleTask task = gson.fromJson(response.body(), SingleTask.class);
            SingleTask expectedTask = new SingleTask(0, "title1T", "description1T", Status.NEW,
                    LocalDateTime.of(2023, 7, 1, 14, 0), 240);
            assertEquals(expectedTask, task);
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте URL-адрес и повторите попытку.");
        }
    }

    @Test
    void getEpicById() { // Получить эпик по id
        addEpic();

        URI url = URI.create("http://localhost:8080/tasks/epic/?id=0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            Epic epic = gson.fromJson(response.body(), Epic.class);
            Epic expectedEpic = new Epic(0, "title1E", "description1E", Status.NEW);
            assertEquals(expectedEpic, epic);
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте URL-адрес и повторите попытку.");
        }
    }

    @Test
    void getSubtaskById() { // Получить сабтаск по id
        addSubtask();

        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            Subtask subtask = gson.fromJson(response.body(), Subtask.class);
            Subtask expectedSubtask = new Subtask(1, "title1S", "description1S", Status.NEW, 0,
                    LocalDateTime.of(2023, 7, 3, 14, 0), 240);
            assertEquals(expectedSubtask, subtask);
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте URL-адрес и повторите попытку.");
        }
    }

    @Test
    void updateSingleTask() { // Обновить задачу
        addSingleTask();

        SingleTask updateTask1 = new SingleTask(0, "updateTitle1T", "updateDescription1T", Status.DONE,
                LocalDateTime.of(2023, 7, 1, 14, 0), 240);
        String jsonUpdatedTask1 = gson.toJson(updateTask1);

        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonUpdatedTask1))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode());
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте URL-адрес и повторите попытку.");
        }

    }

    @Test
    void updateEpic() { // Обновить эпик
        addEpic();

        String jsonUpdatedEpic1 = "{\"id\":0,\"title\":\"title1E\",\"description\":\"description1E\"}";

        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonUpdatedEpic1))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode());
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте URL-адрес и повторите попытку.");
        }
    }

    @Test
    void updateSubtask() { // Обновить сабтаск
        addSubtask();

        Subtask updateSubtask1 = new Subtask(1, "updateTitle1S", "updateDescription1S", Status.DONE,
                0, LocalDateTime.of(2023, 7, 3, 14, 0), 240);
        String jsonUpdatedSubtask1 = gson.toJson(updateSubtask1);

        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonUpdatedSubtask1))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response.statusCode());
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте URL-адрес и повторите попытку.");
        }
    }

    @Test
    void deleteSingleTaskById() { // Удалить задачу по id
        addSingleTask();

        URI url = URI.create("http://localhost:8080/tasks/task/?id=0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            URI urlGetList = URI.create("http://localhost:8080/tasks/task/");
            HttpRequest requestGetList = HttpRequest.newBuilder()
                    .uri(urlGetList)
                    .GET()
                    .build();

            response = client.send(requestGetList, HttpResponse.BodyHandlers.ofString());
            Type tasksType = new TypeToken<List<SingleTask>>() {
            }.getType();
            List<SingleTask> tasks = gson.fromJson(response.body(), tasksType);

            assertTrue(tasks.isEmpty(), "Список задач не пустой");
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте URL-адрес и повторите попытку.");
        }
    }

    @Test
    void deleteEpicById() { // Удалить эпик по id
        addEpic();

        URI url = URI.create("http://localhost:8080/tasks/epic/?id=0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            URI urlGetList = URI.create("http://localhost:8080/tasks/epic/");
            HttpRequest requestGetList = HttpRequest.newBuilder()
                    .uri(urlGetList)
                    .GET()
                    .build();

            response = client.send(requestGetList, HttpResponse.BodyHandlers.ofString());
            Type epicsType = new TypeToken<List<Epic>>() {
            }.getType();
            List<Epic> epics = gson.fromJson(response.body(), epicsType);

            assertTrue(epics.isEmpty(), "Список задач не пустой");
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте URL-адрес и повторите попытку.");
        }
    }

    @Test
    void deleteSubtaskById() { // Удалить сабтаск по Id
        addSubtask();

        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            URI urlGetList = URI.create("http://localhost:8080/tasks/subtask/");
            HttpRequest requestGetList = HttpRequest.newBuilder()
                    .uri(urlGetList)
                    .GET()
                    .build();

            response = client.send(requestGetList, HttpResponse.BodyHandlers.ofString());
            Type subtasksType = new TypeToken<List<Subtask>>() {
            }.getType();
            List<Subtask> subtasks = gson.fromJson(response.body(), subtasksType);

            assertTrue(subtasks.isEmpty(), "Список задач не пустой");
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте URL-адрес и повторите попытку.");
        }
    }

    @Test
    void getListOfAllSingleTasks() { // Получить список всех задач
        addSingleTask();

        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Type tasksType = new TypeToken<List<SingleTask>>() {
            }.getType();
            List<SingleTask> tasks = gson.fromJson(response.body(), tasksType);

            assertEquals(200, response.statusCode());
            assertNotNull(tasks, "Список задач пустой");
            assertEquals(1, tasks.size());
            task1.setId(0);
            assertEquals(task1, tasks.get(0));
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте URL-адрес и повторите попытку.");
        }
    }

    @Test
    void getListOfAllEpics() { // Получить список всех эпиков
        addEpic();

        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Type epicsType = new TypeToken<List<Epic>>() {
            }.getType();
            List<Epic> epics = gson.fromJson(response.body(), epicsType);

            assertEquals(200, response.statusCode());
            assertNotNull(epics, "Список задач пустой");
            assertEquals(1, epics.size());
            epic1.setId(0);
            assertEquals(epic1, epics.get(0));
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте URL-адрес и повторите попытку.");
        }
    }

    @Test
    void getListOfAllSubtasks() { // Получить список всех сабтасков
        addSubtask();

        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Type subtasksType = new TypeToken<List<Subtask>>() {
            }.getType();
            List<Subtask> subtasks = gson.fromJson(response.body(), subtasksType);

            assertEquals(200, response.statusCode());
            assertNotNull(subtasks, "Список задач пустой");
            assertEquals(1, subtasks.size());
            subtask1.setId(1);
            assertEquals(subtask1, subtasks.get(0));
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте URL-адрес и повторите попытку.");
        }
    }

    @Test
    void getListOfEpicsSubtasks() { // Получить список всех сабтасков эпика
        addSubtask();

        URI url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Type subtasksType = new TypeToken<List<Subtask>>() {
            }.getType();
            List<Subtask> subtasks = gson.fromJson(response.body(), subtasksType);

            assertEquals(200, response.statusCode());
            assertNotNull(subtasks, "Список задач пустой");
            assertEquals(1, subtasks.size());
            subtask1.setId(1);
            assertEquals(subtask1, subtasks.get(0));
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте URL-адрес и повторите попытку.");
        }
    }

    @Test
    void removeAllTasks() { // Удаление всех задач
        addSingleTask();

        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            URI urlGetTasks = URI.create("http://localhost:8080/tasks/task/");
            HttpRequest requestGetList = HttpRequest.newBuilder()
                    .uri(urlGetTasks)
                    .GET()
                    .build();
            response = client.send(requestGetList, HttpResponse.BodyHandlers.ofString());
            Type tasksType = new TypeToken<List<SingleTask>>() {
            }.getType();
            List<SingleTask> tasks = gson.fromJson(response.body(), tasksType);
            assertTrue(tasks.isEmpty());
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте URL-адрес и повторите попытку.");
        }
    }

    @Test
    void removeAllEpics() { // Удаление всех эпиков
        addEpic();

        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            URI urlGetTasks = URI.create("http://localhost:8080/tasks/epic/");
            HttpRequest requestGetList = HttpRequest.newBuilder()
                    .uri(urlGetTasks)
                    .GET()
                    .build();
            response = client.send(requestGetList, HttpResponse.BodyHandlers.ofString());
            Type epicsType = new TypeToken<List<Epic>>() {
            }.getType();
            List<Epic> epics = gson.fromJson(response.body(), epicsType);
            assertTrue(epics.isEmpty());
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте URL-адрес и повторите попытку.");
        }
    }

    @Test
    void removeAllSubtasks() { // Удаление всех сабтасков
        addSubtask();

        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            URI urlGetTasks = URI.create("http://localhost:8080/tasks/subtask/");
            HttpRequest requestGetList = HttpRequest.newBuilder()
                    .uri(urlGetTasks)
                    .GET()
                    .build();
            response = client.send(requestGetList, HttpResponse.BodyHandlers.ofString());
            Type subtasksType = new TypeToken<List<Subtask>>() {}.getType();
            List<Subtask> subtasks = gson.fromJson(response.body(), subtasksType);
            assertTrue(subtasks.isEmpty());
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте URL-адрес и повторите попытку.");
        }
    }

    @Test
    void getPrioritizedTasks() { // Получить список задач по приоритету
        addSubtask();
        addSingleTask();

        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            List<Task> tasks = HttpTaskManager.createTaskListFromJson(response.body());

            assertNotNull(tasks);
            assertEquals(3, tasks.size());
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте URL-адрес и повторите попытку.");
        }
    }

    @Test
    void getHistory() { // Проверка получения истории
        getSubtaskById();

        URI urlGetEpic = URI.create("http://localhost:8080/tasks/epic/?id=0");
        HttpRequest requestGetEpic = HttpRequest.newBuilder()
                .uri(urlGetEpic)
                .GET()
                .build();
        try {
            client.send(requestGetEpic, HttpResponse.BodyHandlers.ofString());

            URI url = URI.create("http://localhost:8080/tasks/history/");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());


            List<Task> tasks = HttpTaskManager.createTaskListFromJson(response.body());

            assertNotNull(tasks);
            assertEquals(2, tasks.size());
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка. Проверьте URL-адрес и повторите попытку.");
        }
    }
}
