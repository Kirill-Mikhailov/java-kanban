import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        taskManager.addTask(new Task("Задача 1", "Описание задачи 1", "NEW")); // Создаем таск 1
        taskManager.addEpic(new Epic("Эпик 1", "Описание эпика 1")); // Создаем эпик 1
        taskManager.addSubtask(new Subtask("Сабтаск 1", "Описание сабтаска 1", "NEW", 1)); // Создаем сабтаск 1 эпика 1
        taskManager.addSubtask(new Subtask("Сабтаск 2", "Описание сабтакса 2", "IN_PROGRESS", 1)); // Создаем сабтаск 2 эпика 1
        taskManager.updateTask(new Task(0, "Обновленная адача 1", "Обновленное описание задачи 1", "DONE")); // Обновляем таск 1
        taskManager.updateEpic(new Epic(1, "Обновленный эпик 1", "Обновленное описание эпика 1")); // Обновляем эпик 1
        taskManager.updateSubtask(new Subtask(2, "Обновленный сабтаск 1", "Обновленное описание сабтаска 1", "DONE", 1)); // Обновляем сабтаск 1
        taskManager.updateSubtask(new Subtask(3, "Обновленный сабтаск 2", "Обновленное описание сабтаска 2", "DONE", 1)); // Обновляем сабтаск 2

        System.out.println(taskManager.getTaskById(0)); // Получаем таск по id
        System.out.println(taskManager.getEpicById(1)); // Получаем эпик по id
        System.out.println(taskManager.getSubtaskById(2)); // Получаем сабтаск по id

        taskManager.addTask(new Task("Задача 2", "Описание задачи 2", "NEW")); // Создаем таск 2
        taskManager.addEpic(new Epic("Эпик 2", "Описание эпика 2")); // Создаем эпик 2

        System.out.println(taskManager.getListOfAllTasks());
        System.out.println(taskManager.getListOfAllEpics());
        System.out.println(taskManager.getListOfAllSubtasks());
        System.out.println(taskManager.getListOfEpicsSubtasks(1));

        taskManager.removeAllTasks();
        taskManager.removeAllEpics();

        taskManager.deleteTaskById(0); // Удаляем таск 1
        taskManager.deleteSubtaskById(3); // Удаляем сабтаск 2
        taskManager.deleteEpicById(1); // Удаляем эпик 1
    }
}
