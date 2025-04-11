package todo.entity;

import db.Entity;

import java.util.Date;

public class Step extends Entity {
    private String title;
    private Status status;
    private int taskRef;
    private Date creationDate;
    private Date lastModificationDate;

    public Step(String title, Status status, int taskRef) {
        setTitle(title);
        setStatus(status);
        setTaskRef(taskRef);
        this.creationDate = new Date();
        this.lastModificationDate = new Date();
    }

    @Override
    public Entity copy() {
        Step copy = new Step(this.title, this.status, this.taskRef);
        copy.setId(this.getId());
        return copy;
    }

    @Override
    public int getEntityCode() {
        return 56;
    }

    public enum Status {
        IN_PROGRESS,
        NOT_STARTED, COMPLETED
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getTaskRef() {
        return taskRef;
    }

    public void setTaskRef(int taskRef) {
        this.taskRef = taskRef;
    }

    public Date getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(Date lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }
}
