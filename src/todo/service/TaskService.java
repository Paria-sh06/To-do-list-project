package todo.service;

import db.Database;
import db.Entity;
import exception.EntityNotFoundException;
import exception.InvalidEntityException;
import todo.entity.Step;
import todo.entity.Task;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskService {
    public static void setAsCompleted(int taskId) {
        try {
            Task task = (Task) Database.get(taskId);

            task.setStatus(Task.Status.COMPLETED);

            Database.update(task);

            System.out.println("Task marked as COMPLETED.");
        } catch (EntityNotFoundException e) {
            System.err.println("Task with id " + taskId + " not found.");
        } catch (InvalidEntityException e) {
            System.err.println("Task is invalid: " + e.getMessage());
        } catch (ClassCastException e) {
            System.err.println("Entity with id " + taskId + " is not a Task.");
        }
    }


        public static void addTask(String title, String description, String dueDate) throws IllegalArgumentException, InvalidEntityException {
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("Task title cannot be empty.");
            }

            if (description == null || description.trim().isEmpty()) {
                throw new IllegalArgumentException("Task description cannot be empty.");
            }

            Date dueDateParsed = parseDueDate(dueDate);
            if (dueDateParsed == null) {
                throw new IllegalArgumentException("Due date format is invalid. Please use yyyy-MM-dd.");
            }

            Task task = new Task(title, description, dueDateParsed, Task.Status.NOT_STARTED);
            Database.add(task);
            System.out.println("Task saved successfully.");
            System.out.println("ID: " + task.getId());
        }

        private static Date parseDueDate(String dueDate) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                return dateFormat.parse(dueDate);
            } catch (ParseException e) {
                return null;
            }
        }

public static void updateTask(int taskId, String field, String newValue) {
    try {
        Task task = (Task) Database.get(taskId);

        String oldValue = null;
        Date modificationDate = new Date();

        switch (field.toLowerCase()) {
            case "title":
                oldValue = task.getTitle();
                task.setTitle(newValue);
                break;
            case "description":
                oldValue = task.getDescription();
                task.setDescription(newValue);
                break;
            case "duedate":
                oldValue = task.getDueDate().toString();
                task.setDueDate(new SimpleDateFormat("yyyy-MM-dd").parse(newValue));
                break;
            case "status":
                oldValue = task.getStatus().toString();
                Task.Status newStatus = Task.Status.valueOf(newValue.toUpperCase());
                task.setStatus(newStatus);

                if (newStatus == Task.Status.COMPLETED) {
                    updateStepStatusToCompleted(taskId);
                }
                break;
            default:
                System.out.println("Invalid field.");
                return;
        }

        task.setLastModificationDate(modificationDate);

        Database.update(task);

        System.out.println("Successfully updated the task.");
        System.out.println("Field: " + field);
        System.out.println("Old Value: " + oldValue);
        System.out.println("New Value: " + newValue);
        System.out.println("Modification Date: " + modificationDate);

    } catch (EntityNotFoundException e) {
        System.out.println("Cannot update task with ID=" + taskId + ".");
        System.out.println("Error: Cannot find entity with id=" + taskId);
    } catch (InvalidEntityException e) {
        System.out.println("Cannot update task with ID=" + taskId + ".");
        System.out.println("Error: " + e.getMessage());
    } catch (Exception e) {
        System.out.println("Cannot update task with ID=" + taskId + ".");
        System.out.println("Error: Something happened");
    }
}

private static Task updateStepStatusToCompleted(int taskId) {
    ArrayList<Entity> allSteps = Database.getAll(new Step("", Step.Status.NOT_STARTED, 0).getEntityCode());

    for (Entity entity : allSteps) {
        Step step = (Step) entity;
        if (step.getTaskRef() == taskId) {
            step.setStatus(Step.Status.COMPLETED);
            try {
                Database.update(step);
                System.out.println("Step with ID=" + step.getId() + " status updated to COMPLETED.");
            } catch (InvalidEntityException e) {
                System.out.println("Error updating step status for Step ID=" + step.getId());
            }
        }
    }
    return null;
}

    public static Task getTaskById(int id) {
        try {
            Task task = (Task) Database.get(id);
            return task;
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    public static List<Step> getStepsByTaskId(int taskId) {
        List<Step> allSteps = new ArrayList<>();
        List<Entity> entities = Database.getAll(Step.class.getModifiers());

        for (Entity entity : entities) {
            Step step = (Step) entity;
            if (step.getTaskRef() == taskId) {
                allSteps.add(step);
            }
        }

        return allSteps;
    }
}






