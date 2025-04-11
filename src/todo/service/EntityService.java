package todo.service;

import db.Database;
import db.Entity;
import exception.EntityNotFoundException;
import todo.entity.Step;
import todo.entity.Task;

import java.util.ArrayList;

public class EntityService {

    public static void deleteEntity(int id) {
        try {
            Entity entity = Database.get(id);

            if (entity instanceof Task) {
                ArrayList<Entity> allSteps = Database.getAll(new Step("", Step.Status.NOT_STARTED, 0).getEntityCode());

                for (Entity e : allSteps) {
                    Step step = (Step) e;
                    if (step.getTaskRef() == id) {
                        Database.delete(step.getId());
                    }
                }
            }

            Database.delete(id);

            System.out.println("Entity with ID=" + id + " successfully deleted.");
        } catch (EntityNotFoundException e) {
            System.out.println("Cannot delete entity with ID=" + id + ".");
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Cannot delete entity with ID=" + id + ".");
            System.out.println("Error: Something happend");
        }
    }
}
