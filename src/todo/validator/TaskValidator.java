package todo.validator;

import db.Entity;
import db.Validator;
import exception.InvalidEntityException;
import todo.entity.Task;

public class TaskValidator implements Validator {
    @Override
    public void validate(Entity entity) throws InvalidEntityException {
        if (!(entity instanceof Task)) {
            throw new IllegalArgumentException("Not a Task entity");
        }

        Task task = (Task) entity;

        if (task.getTitle() == null || task.getTitle().isEmpty()) {
            throw new InvalidEntityException("Task title must not be null or empty");
        }
    }
}
