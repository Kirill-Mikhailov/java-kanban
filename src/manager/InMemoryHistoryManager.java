package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> listOfViewedTasks;

    public InMemoryHistoryManager() {
        this.listOfViewedTasks = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (listOfViewedTasks.size() == 10) {
            listOfViewedTasks.remove(0);
        }
        listOfViewedTasks.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return Collections.unmodifiableList(listOfViewedTasks);
    }
}
