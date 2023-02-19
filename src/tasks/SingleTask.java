package tasks;

public class SingleTask extends Task {
    private final Status status;

    public SingleTask(String title, String description, Status status) {
        super(title, description);
        this.status = status;
    }

    public SingleTask(int id, String title, String description, Status status) {
        super(id, title, description);
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return super.toString() +
                "status='" + status + "'}";
    }
}
