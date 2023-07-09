package servers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.oldTaskManager.TaskManager;

import javax.imageio.IIOException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private TaskManager manager = Managers.getDefault(Paths.get("..\\kanban.csv"));
    private HttpServer server;
    private Gson gson;

    public HttpTaskServer() throws IIOException {
        this.manager = Managers.getDefault(Paths.get("..\\kanban.csv"));;
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        this.server.createContext("/tasks", new TaskHandler());
        this.gson = Managers.getGson();
    }

    public void start() {

    }

    public void stop() {

    }

    static class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String method = exchange.getRequestMethod();
                String stringPath = exchange.getRequestURI().getPath();
                String[] path = stringPath.split("/");
                System.out.println("Началась обработка " + method + " " + stringPath + " запроса от клиента.");

                switch (method) {
                    case "GET":

                    case "POST":

                    case "DELETE":

                    default:

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                exchange.close();
            }
        }
    }
}
