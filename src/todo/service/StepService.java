package todo.service;

import db.Database;
import db.Entity;
import exception.EntityNotFoundException;
import exception.InvalidEntityException;
import todo.entity.Step;
import todo.entity.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StepService {

    public static void addStep(int taskId, String title) {
        try {
            Task task = (Task) Database.get(taskId);

            Step step = new Step(title, Step.Status.NOT_STARTED, taskId);

            Database.add(step);

            System.out.println("Step saved successfully.");
            System.out.println("ID: " + step.getId());

        } catch (EntityNotFoundException e) {
            System.out.println("Cannot save step.");
            System.out.println("Error: Cannot find task with ID=" + taskId + ".");
        } catch (InvalidEntityException e) {
            System.out.println("Cannot save step.");
            System.out.println("Error: " + e.getMessage());
        } catch (ClassCastException e) {
            System.out.println("Cannot save step.");
            System.out.println("Error: The entity with ID=" + taskId + " is not a Task.");
        }
    }

    public static void saveStep(int taskId, String title) {
        try {
            Task task = (Task) Database.get(taskId); // بررسی صحت taskId
            Step step = new Step(title, Step.Status.NOT_STARTED, taskId);
            Database.add(step);
            System.out.println("Step saved successfully with ID: " + step.getId());
        } catch (EntityNotFoundException e) {
            System.err.println("Cannot find task with ID=" + taskId + ".");
        } catch (InvalidEntityException e) {
            System.err.println("Failed to save step: " + e.getMessage());
        } catch (ClassCastException e) {
            System.err.println("Entity with ID=" + taskId + " is not a Task.");
        }
    }

    public static void markStepAsCompleted(int stepId) {
        try {
            Entity entity = Database.get(stepId);
            if (!(entity instanceof Step step)) {
                System.err.println("Entity with ID " + stepId + " is not a Step.");
                return;
            }
            step.setStatus(Step.Status.COMPLETED);
            step.setLastModificationDate(new Date());
            Database.update(step);
            System.out.println("Step with ID " + stepId + " marked as COMPLETED.");
            updateTaskStatusIfNeeded(step);
        } catch (EntityNotFoundException e) {
            System.err.println("Step not found with ID: " + stepId);
        } catch (InvalidEntityException e) {
            System.err.println("Step update failed: " + e.getMessage());
        }
    }

    public static void updateStep(int stepId, String field, String newValue) {
        try {
            Step step = (Step) Database.get(stepId);

            String oldValue = "";
            Date modificationDate = new Date();

            if ("status".equalsIgnoreCase(field)) {
                oldValue = step.getStatus().toString();
                step.setStatus(Step.Status.valueOf(newValue.toUpperCase()));
            } else {
                throw new IllegalArgumentException("Invalid field");
            }

            step.setLastModificationDate(modificationDate);
            Database.update(step);

            System.out.println("Successfully updated the step.");
            System.out.println("Field: " + field);
            System.out.println("Old Value: " + oldValue);
            System.out.println("New Value: " + newValue);
            System.out.println("Modification Date: " + modificationDate);

            updateTaskStatusIfNeeded(step);

        } catch (EntityNotFoundException e) {
            System.err.println("Step not found with ID: " + stepId);
        } catch (InvalidEntityException | IllegalArgumentException e) {
            System.err.println("Failed to update step: " + e.getMessage());
        }
    }

    private static void updateTaskStatusIfNeeded(Step step) throws InvalidEntityException {
        List<Step> relatedSteps = getStepsByTaskId(step.getTaskRef());

        boolean allCompleted = true;
        boolean anyInProgress = false;

        for (Step s : relatedSteps) {
            if (s.getStatus() != Step.Status.COMPLETED) {
                allCompleted = false;
            }
            if (s.getStatus() == Step.Status.IN_PROGRESS) {
                anyInProgress = true;
            }
        }

        try {
            Task task = (Task) Database.get(step.getTaskRef());

            if (allCompleted && task.getStatus() != Task.Status.COMPLETED) {
                task.setStatus(Task.Status.COMPLETED);
                Database.update(task);
            } else if (anyInProgress && task.getStatus() == Task.Status.NOT_STARTED) {
                task.setStatus(Task.Status.IN_PROGRESS);
                Database.update(task);
            }
        } catch (EntityNotFoundException e) {
            System.err.println("Related task not found for step ID=" + step.getId());
        }
    }

    public static List<Step> getStepsByTaskId(int taskId) {
        List<Step> steps = new ArrayList<>();
        List<Entity> allEntities = Database.getAll(Step.STEP_ENTITY_CODE);

        for (Entity entity : allEntities) {
            if (entity instanceof Step step && step.getTaskRef() == taskId) {
                steps.add(step);
            }
        }

        return steps;
    }
}
