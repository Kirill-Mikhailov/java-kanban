package servers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.HttpTaskManager;
import manager.utils.Managers;
import manager.oldTaskManager.TaskManager;
import tasks.Epic;
import tasks.SingleTask;
import tasks.Subtask;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private HttpServer server;
    private TaskManager manager;

    public HttpTaskServer() throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        this.server.createContext("/tasks", new TaskHandler());
        this.manager = Managers.getDefault("http://localhost:8078");
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Остановили сервер на порту: " + PORT);
    }

     class TaskHandler implements HttpHandler {
        private Gson gson;
        public TaskHandler() {
            this.gson = Managers.getGson();
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String method = exchange.getRequestMethod();
                String justPath = exchange.getRequestURI().getPath();
                String path;
                if (exchange.getRequestURI().getQuery() != null) {
                    path = justPath  + exchange.getRequestURI().getRawQuery();
                } else {
                    path = justPath;
                }
                System.out.println("Началась обработка " + method + " " + path + " запроса от клиента.");

                switch (method) {
                    case "GET":
                        if (Pattern.matches("^/tasks/task/id=\\d+$", path)) { // getSingleTaskById(Integer id)
                            int id = idParser(path);
                            if (id != -1) {
                                String response = gson.toJson(manager.getSingleTaskById(id));
                                sendText(exchange, response);
                            } else {
                                System.out.println("Получен некорректный id: " + id);
                                exchange.sendResponseHeaders(400, 0);
                            }
                            break;
                        } else if (Pattern.matches("^/tasks/epic/id=\\d+$", path)) { // getEpicById(Integer id)
                            int id = idParser(path);
                            if (id != -1) {
                                String response = gson.toJson(manager.getEpicById(id));
                                sendText(exchange, response);
                            } else {
                                System.out.println("Получен некорректный id: " + id);
                                exchange.sendResponseHeaders(400, 0);
                            }
                            break;
                        } else if (Pattern.matches("^/tasks/subtask/id=\\d+$", path)) { // getSubtaskById(Integer id)
                            int id = idParser(path);
                            if (id != -1) {
                                String response = gson.toJson(manager.getSubtaskById(id));
                                sendText(exchange, response);
                            } else {
                                System.out.println("Получен некорректный id: " + id);
                                exchange.sendResponseHeaders(400, 0);
                            }
                            break;
                        } else if (Pattern.matches("^/tasks/task/$", path)) { // getListOfAllSingleTasks()
                            String response = gson.toJson(manager.getListOfAllSingleTasks());
                            sendText(exchange, response);
                            break;
                        } else if (Pattern.matches("^/tasks/epic/$", path)) { // getListOfAllEpics()
                            String response = gson.toJson(manager.getListOfAllEpics());
                            sendText(exchange, response);
                            break;
                        } else if (Pattern.matches("^/tasks/subtask/$", path)) { // getListOfAllSubtasks()
                            String response = gson.toJson(manager.getListOfAllSubtasks());
                            sendText(exchange, response);
                            break;
                        } else if (Pattern.matches("^/tasks/subtask/epic/id=\\d+$", path)) { // getListOfEpicsSubtasks(Integer id)
                            int epicId = idParser(path);
                            if (epicId != -1) {
                                String response = gson.toJson(manager.getListOfEpicsSubtasks(epicId));
                                sendText(exchange, response);
                            } else {
                                System.out.println("Получен некорректный id эпика: " + epicId);
                                exchange.sendResponseHeaders(400, 0);
                            }
                            break;
                        } else if (Pattern.matches("^/tasks/$", path)) { // getPrioritizedTasks()
                            String response = gson.toJson(manager.getPrioritizedTasks());
                            sendText(exchange, response);
                            break;
                        } else if (Pattern.matches("^/tasks/history/$", path)) { // getHistory()
                            String response = gson.toJson(manager.getHistory());
                            sendText(exchange, response);
                            break;
                        } else {
                            System.out.println("Некорректный запрос: " + path);
                            exchange.sendResponseHeaders(400, 0);
                        }
                        break;
                    case "POST":
                        if (Pattern.matches("^/tasks/task/$", path)) {// add/update task
                            try {
                                String gsonTask = readText(exchange);
                                SingleTask task = gson.fromJson(gsonTask, SingleTask.class);
                                if (task.getId() == null) {
                                    manager.addSingleTask(task);
                                } else {
                                    manager.updateSingleTask(task);
                                }
                                exchange.sendResponseHeaders(201, 0);
                                break;
                            } catch (JsonSyntaxException e) {
                                exchange.sendResponseHeaders(400, 0);
                            }
                        } else if (Pattern.matches("^/tasks/epic/$", path)) { // add/update epic
                            try {
                                String gsonEpic = readText(exchange);
                                Epic epic = gson.fromJson(gsonEpic, Epic.class);
                                if (epic.getId() == null) {
                                    manager.addEpic(epic);
                                } else {
                                    manager.updateEpic(epic);
                                }
                                exchange.sendResponseHeaders(201, 0);
                                break;
                            } catch (JsonSyntaxException e) {
                                exchange.sendResponseHeaders(400, 0);
                            }
                        } else if (Pattern.matches("^/tasks/subtask/$", path)) { // add/update subtask
                            try {
                                String gsonSubtask = readText(exchange);
                                Subtask subtask = gson.fromJson(gsonSubtask, Subtask.class);
                                if (subtask.getId() == null) {
                                    manager.addSubtask(subtask);
                                } else {
                                    manager.updateSubtask(subtask);
                                }
                                exchange.sendResponseHeaders(201, 0);
                                break;
                            } catch (JsonSyntaxException e) {
                                exchange.sendResponseHeaders(400, 0);
                            }
                        } else {
                            System.out.println("Некорректный запрос: " + path);
                            exchange.sendResponseHeaders(400, 0);
                        }
                        break;
                    case "DELETE":
                        if (Pattern.matches("^/tasks/task/id=\\d+$", path)) { // deleteSingleTaskById(Integer id)
                            int id = idParser(path);
                            if (id != -1) {
                                manager.deleteSingleTaskById(id);
                                exchange.sendResponseHeaders(200, 0);
                            } else {
                                System.out.println("Получен некорректный id: " + id);
                                exchange.sendResponseHeaders(400, 0);
                            }
                            break;
                        } else if (Pattern.matches("^/tasks/epic/id=\\d+$", path)) { // deleteEpicById(Integer id)
                            int id = idParser(path);
                            if (id != -1) {
                                manager.deleteEpicById(id);
                                exchange.sendResponseHeaders(200, 0);
                            } else {
                                System.out.println("Получен некорректный id: " + id);
                                exchange.sendResponseHeaders(400, 0);
                            }
                            break;
                        } else if (Pattern.matches("^/tasks/subtask/id=\\d+$", path)) { // deleteSubtaskById(Integer id)
                            int id = idParser(path);
                            if (id != -1) {
                                manager.deleteSubtaskById(id);
                                exchange.sendResponseHeaders(200, 0);
                            } else {
                                System.out.println("Получен некорректный id: " + id);
                                exchange.sendResponseHeaders(400, 0);
                            }
                            break;
                        } else if (Pattern.matches("^/tasks/task/$", path)) { // removeAllTasks()
                            manager.removeAllTasks();
                            exchange.sendResponseHeaders(200, 0);
                            break;
                        } else if (Pattern.matches("^/tasks/epic/$", path)) { // removeAllEpics()
                            manager.removeAllEpics();
                            exchange.sendResponseHeaders(200, 0);
                            break;
                        } else if (Pattern.matches("^/tasks/subtask/$", path)) { // removeAllSubtasks()
                            manager.removeAllSubtasks();
                            exchange.sendResponseHeaders(200, 0);
                            break;
                        } else {
                            System.out.println("Некорректный запрос: " + path);
                            exchange.sendResponseHeaders(400, 0);
                            break;
                        }
                    default:
                        System.out.println("Запрошенный метод " + method + " не поддерживается");
                        exchange.sendResponseHeaders(405, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                exchange.close();
            }
        }

        private int idParser(String query) {
            try {
                String stringId = query.substring(query.lastIndexOf("=")+1);
                return Integer.parseInt(stringId);
            } catch (NumberFormatException e) {
                return -1;
            }
        }

        private void sendText(HttpExchange h, String text) throws IOException {
            byte[] resp = text.getBytes(DEFAULT_CHARSET);
            h.getResponseHeaders().add("Content-Type", "application/json");
            h.sendResponseHeaders(200, resp.length);
            try (OutputStream os = h.getResponseBody()) {
                os.write(resp);
            }
        }

        private String readText(HttpExchange h) throws IOException {
            return new String(h.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        }
    }
}