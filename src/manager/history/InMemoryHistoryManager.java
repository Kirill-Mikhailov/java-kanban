package manager.history;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Node head;
    private Node tail;
    private int size;
    private final Map<Integer, Node> idToNode;

    public InMemoryHistoryManager() {
        this.idToNode = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            remove(task.getId()); //Удалили старый узел из списка по id (если он там есть)
            idToNode.put(task.getId(), linkLast(new Node(task))); /* Создали новый узел из задачи и передали в метод
            для добавления узла в список. Положили в мапу этот узел с новой задачей */
        }
    }

    @Override
    public void remove(int id) {
        if (idToNode.containsKey(id)) { //Если в мапе idToNode есть ключ id
            removeNode(idToNode.get(id)); //Передаем в метод удаления узлов узел из мапы - узел удален
            idToNode.remove(id); //Удалили этот узел из мапы
        }
    }

    private Node linkLast (Node newNode) {
        if (size == 0) { //Если список пуст
            head = newNode; //Голова стала новым узлом
        } else { //Иначе
            tail.next = newNode; //Создаем ссылку на следующий узел у хвоста
        }
        newNode.prev = tail; //Создаем ссылку на предыдущий узел у нового узла
        tail = newNode; //Переназначили хвост на новый узел - добавили задачу в CustomLinkedList
        size++;
        return newNode;
    }

    private void removeNode (Node node) {
        if (size == 1) { //Если это и голова и хвост
            head = null; //Удаляем из полей, ссылок внутри node и так нет
            tail = null;

        } else if (node.prev == null){ //Если удаляется голова
            Node nextNode = node.next; //Сохранили следующий узел
            nextNode.prev = null; //Удалили у следующего узла ссылку на текущий
            head = nextNode; //Переназналили голову
        } else if (node.next == null) { //Если удаляется хвост
            Node prevNode = node.prev; //Сохранили предыдущий узел
            prevNode.next = null; //Удалили у предыдущего узла ссылку на текущий
            tail = prevNode; //Переназналили хвост
        } else {
            Node prevNode = node.prev; //Сохранили предыдущий узел
            Node nextNode = node.next; //Сохранили следующий узел
            prevNode.next = nextNode;
            nextNode.prev = prevNode; //Изменили ссылки у предыдущего и следующего узлов на друг друга
        }
        size--;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node node = head; //Начинаем с головы
        if (head != null) { //Если список не пустой
            while (node.next != null) { //Пока не дошли до последней ноды
                history.add(node.data); //Добавили задачу в список
                node = node.next; //Перешли на следующую ноду
            }
            history.add(node.data); //Добавили последнюю ноду
        }
        return Collections.unmodifiableList(history);
    }

    private static final class Node {
        public Task data;
        public Node next;
        public Node prev;

        public Node(Task data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }
    }
}
