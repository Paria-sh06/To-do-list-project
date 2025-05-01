package todo.service;

import db.Database;
import db.Entity;
import exception.EntityNotFoundException;
import exception.InvalidEntityException;
import todo.entity.Step;
import todo.entity.Task;

import java.util.List;

public class EntityService {

    public static void deleteEntity(int id) {
        try {
            Entity entity = Database.get(id);

            if (entity instanceof Task) {
                List<Entity> allSteps = Database.getAll(Step.STEP_ENTITY_CODE);

                for (Entity e : allSteps) {
                    if (e instanceof Step step && step.getTaskRef() == id) {
                        Database.delete(step.getId());
                    }
                }
            }

            Database.delete(id);

            System.out.println("Entity with ID=" + id + " successfully deleted.");
        } catch (EntityNotFoundException e) {
            System.err.println("Cannot delete entity with ID=" + id + ". Not found.");
        } catch (Exception e) {
            System.err.println("Unexpected error while deleting entity with ID=" + id);
        }
    }
}
