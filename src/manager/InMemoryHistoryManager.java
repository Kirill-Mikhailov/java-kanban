package manager;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    public Node head;
    public Node tail;
    private int size;
    private final Map<Integer, Node> idToNode;

    public InMemoryHistoryManager() {
        this.head = null;
        this.tail = null;
        this.size = 0;
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
        Node node = head;
        if (head != null) { //Если список не пустой
            //if (size > 1) { //И если в списке более 1 элемента
            while (node.next != null) {
                history.add(node.data);
                node = node.next;
            }
            history.add(node.data);
        }
            //} else {
                //history.add(node.data);
            //}
        //}

        return Collections.unmodifiableList(history);
    }
}
