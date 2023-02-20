import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.SingleTask;

public class Main {

    public static void main(String[] args) {
        TaskManager inMemoryTaskManager = Managers.getDefault();

        inMemoryTaskManager.addSingleTask(new SingleTask("Задача 1", "Описание задачи 1", Status.NEW)); // Создаем таск 1 id 0
        inMemoryTaskManager.addSingleTask(new SingleTask("Задача 2", "Описание задачи 2", Status.NEW)); // Создаем таск 2 id 1
        inMemoryTaskManager.addSingleTask(new SingleTask("Задача 3", "Описание задачи 3", Status.NEW)); // Создаем таск 3 id 2
        inMemoryTaskManager.addEpic(new Epic("Эпик 1", "Описание эпика 1")); // Создаем эпик 1 id 3
        inMemoryTaskManager.addEpic(new Epic("Эпик 2", "Описание эпика 2")); // Создаем эпик 2 id 4
        inMemoryTaskManager.addEpic(new Epic("Эпик 3", "Описание эпика 3")); // Создаем эпик 3 id 5
        inMemoryTaskManager.addSubtask(new Subtask("Сабтаск 1", "Описание сабтаска 1", Status.NEW, 3)); // Создаем сабтаск 1 эпика 1 id 6
        inMemoryTaskManager.addSubtask(new Subtask("Сабтаск 2", "Описание сабтакса 2", Status.IN_PROGRESS, 3)); // Создаем сабтаск 2 эпика 1 id 7
        inMemoryTaskManager.addSubtask(new Subtask("Сабтаск 3", "Описание сабтакса 3", Status.DONE, 4)); // Создаем сабтаск 1 эпика 2 id 8
        inMemoryTaskManager.addSubtask(new Subtask("Сабтаск 4", "Описание сабтакса 4", Status.IN_PROGRESS, 4)); // Создаем сабтаск 2 эпика 2 id 9
        inMemoryTaskManager.updateSingleTask(new SingleTask(0, "Обновленная адача 1", "Обновленное описание задачи 1", Status.DONE)); // Обновляем таск 1
        inMemoryTaskManager.updateSingleTask(new SingleTask(1, "Обновленная адача 2", "Обновленное описание задачи 2", Status.IN_PROGRESS)); // Обновляем таск 2
        inMemoryTaskManager.updateEpic(new Epic(3, "Обновленный эпик 1", "Обновленное описание эпика 1")); // Обновляем эпик 1
        inMemoryTaskManager.updateEpic(new Epic(4, "Обновленный эпик 2", "Обновленное описание эпика 2")); // Обновляем эпик 2
        inMemoryTaskManager.updateSubtask(new Subtask(6, "Обновленный сабтаск 1", "Обновленное описание сабтаска 1", Status.DONE, 3)); // Обновляем сабтаск 1
        inMemoryTaskManager.updateSubtask(new Subtask(9, "Обновленный сабтаск 4", "Обновленное описание сабтаска 4", Status.DONE, 4)); // Обновляем сабтаск 4
        System.out.println(inMemoryTaskManager.getSingleTaskById(0)); // Получаем таск по id
        System.out.println(inMemoryTaskManager.getSingleTaskById(1)); // Получаем таск по id
        System.out.println(inMemoryTaskManager.getSingleTaskById(2)); // Получаем таск по id
        System.out.println(inMemoryTaskManager.getEpicById(3)); // Получаем эпик по id
        System.out.println(inMemoryTaskManager.getEpicById(4)); // Получаем эпик по id
        System.out.println(inMemoryTaskManager.getEpicById(5)); // Получаем эпик по id
        System.out.println(inMemoryTaskManager.getSubtaskById(6)); // Получаем сабтаск по id
        System.out.println(inMemoryTaskManager.getSubtaskById(7)); // Получаем сабтаск по id
        System.out.println(inMemoryTaskManager.getSubtaskById(8)); // Получаем сабтаск по id
        System.out.println(inMemoryTaskManager.getSubtaskById(9)); // Получаем сабтаск по id
        System.out.println(inMemoryTaskManager.getSingleTaskById(0)); // Получаем таск по id
        System.out.println(inMemoryTaskManager.getListOfAllSingleTasks());
        System.out.println(inMemoryTaskManager.getListOfAllEpics());
        System.out.println(inMemoryTaskManager.getListOfAllSubtasks());
        System.out.println(inMemoryTaskManager.getListOfEpicsSubtasks(3));
        System.out.println(inMemoryTaskManager.getHistory());
        inMemoryTaskManager.removeAllSubtasks();
        inMemoryTaskManager.removeAllTasks();
        inMemoryTaskManager.removeAllEpics();
        inMemoryTaskManager.deleteSingleTaskById(0); // Удаляем таск 1
        inMemoryTaskManager.deleteSubtaskById(3); // Удаляем сабтаск 2
        inMemoryTaskManager.deleteEpicById(1); // Удаляем эпик 1
    }
}
