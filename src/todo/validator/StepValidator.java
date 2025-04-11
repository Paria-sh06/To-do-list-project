package todo.validator;

import db.Database;
import db.Entity;
import db.Validator;
import exception.EntityNotFoundException;
import exception.InvalidEntityException;
import todo.entity.Step;
import todo.entity.Task;

public class StepValidator implements Validator {
    @Override
    public void validate(Entity entity) throws InvalidEntityException {
        if (!(entity instanceof Step step)) {
            throw new InvalidEntityException("Invalid entity type: expected Step");
        }

        if (step.getTitle() == null || step.getTitle().isEmpty()) {
            throw new InvalidEntityException("Step title must not be null or empty");
        }

        try {
            Entity task = Database.get(step.getTaskRef());
            if (!(task instanceof Task)) {
                throw new InvalidEntityException("taskRef does not refer to a valid Task");
            }
        } catch (EntityNotFoundException e) {
            throw new InvalidEntityException("taskRef does not refer to an existing Task");
        }

    }
}
