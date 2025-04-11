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

    public static void saveStep(int taskRef, String title) {
        try {
            Step step = new Step(title, Step.Status.IN_PROGRESS, taskRef);
            Database.add(step);
            System.out.println("Step saved successfully with ID: " + step.getId());
        } catch (InvalidEntityException e) {
            System.err.println("Failed to save step: " + e.getMessage());
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
            Database.update(step);
            System.out.println("Step with ID " + stepId + " marked as COMPLETED.");
        } catch (EntityNotFoundException e) {
            System.err.println("Step not found with ID: " + stepId);
        } catch (InvalidEntityException e) {
            System.err.println("Step update failed: " + e.getMessage());
        }
    }

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

    public static void updateStep(int stepId, String field, String newValue) {
        try {
            Step step = (Step) Database.get(stepId);

            String oldValue = step.getStatus().toString();
            Date modificationDate = new Date();

            if (field.equals("status")) {
                step.setStatus(Step.Status.valueOf(newValue));
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
            System.err.println("Cannot update step with ID=" + stepId + ".");
            System.err.println("Error: Cannot find entity with ID=" + stepId);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid field or status: " + e.getMessage());
        } catch (InvalidEntityException e) {
            throw new RuntimeException(e);
        }
    }

    private static void updateTaskStatusIfNeeded(Step step) throws InvalidEntityException {
        List<Entity> entities = Database.getAll(56);
        List<Step> steps = new ArrayList<>();

        for (Entity entity : entities) {
            if (entity instanceof Step) {
                steps.add((Step) entity);
            }
        }

        boolean allCompleted = true;
        boolean anyInProgress = false;

        for (Step s : steps) {
            if (s.getTaskRef() == step.getTaskRef()) {
                if (s.getStatus() != Step.Status.COMPLETED) {
                    allCompleted = false;
                }
                if (s.getStatus() == Step.Status.IN_PROGRESS) {
                    anyInProgress = true;
                }
            }
        }

        Task task = (Task) Database.get(step.getTaskRef());

        if (allCompleted) {
            task.setStatus(Task.Status.COMPLETED);
            Database.update(task);
        } else if (anyInProgress && task.getStatus() == Task.Status.NOT_STARTED) {
            task.setStatus(Task.Status.IN_PROGRESS);
            Database.update(task);
        }
    }

    public static List<Step> getStepsByTaskId(int taskId) {
        List<Step> steps = new ArrayList<>();

        List<Entity> allEntities = Database.getAll(56);

        for (Entity entity : allEntities) {
            if (entity instanceof Step) {
                Step step = (Step) entity;
                if (step.getTaskRef() == taskId) {
                    steps.add(step);
                }
            }
        }

        return steps;
    }
}

