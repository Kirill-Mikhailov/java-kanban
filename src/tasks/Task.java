package tasks;

public class Task extends BaseTask{
    private String status;

    public Task(String title, String description, String status) {
        super(title, description);
        this.status = status;
    }

    public Task(int id, String title, String description, String status) {
        super(id, title, description);
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return  super.toString() +
                "status='" + status + "'}";
    }
}
