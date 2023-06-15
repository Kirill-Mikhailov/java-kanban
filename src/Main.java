import manager.*;
import tasks.*;

public class Main {

    public static void main(String[] args) {
        TaskManager inMemoryTaskManager = Managers.getDefault();

        inMemoryTaskManager.addSingleTask(new SingleTask("Задача 1", "Описание задачи 1", Status.NEW)); // 0
        inMemoryTaskManager.addSingleTask(new SingleTask("Задача 2", "Описание задачи 2", Status.NEW)); // 1

        inMemoryTaskManager.addEpic(new Epic("Эпик 1", "Описание эпика 1")); // 2

        inMemoryTaskManager.addSubtask(new Subtask("Сабтаск 1", "Описание сабтаска 1", Status.NEW, 2)); // 3
        inMemoryTaskManager.addSubtask(new Subtask("Сабтаск 2", "Описание сабтакса 2", Status.IN_PROGRESS, 2)); // 4
        inMemoryTaskManager.addSubtask(new Subtask("Сабтаск 3", "Описание сабтакса 3", Status.DONE, 2)); // 5

        inMemoryTaskManager.addEpic(new Epic("Эпик 2", "Описание эпика 2")); // 6

        System.out.println(inMemoryTaskManager.getSingleTaskById(0));;
        System.out.println(inMemoryTaskManager.getHistory());

        System.out.println(inMemoryTaskManager.getSubtaskById(4));
        System.out.println(inMemoryTaskManager.getHistory());

        System.out.println(inMemoryTaskManager.getEpicById(2));
        System.out.println(inMemoryTaskManager.getHistory());

        System.out.println(inMemoryTaskManager.getSingleTaskById(0));
        System.out.println(inMemoryTaskManager.getHistory());

        System.out.println(inMemoryTaskManager.getSubtaskById(4));
        System.out.println(inMemoryTaskManager.getHistory());

        inMemoryTaskManager.deleteSingleTaskById(0);
        System.out.println(inMemoryTaskManager.getHistory());

        inMemoryTaskManager.deleteEpicById(2);
        System.out.println(inMemoryTaskManager.getHistory());
    }
}
