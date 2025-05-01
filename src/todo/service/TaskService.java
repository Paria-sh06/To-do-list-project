package todo.service;

import db.Database;
import db.Entity;
import exception.EntityNotFoundException;
import exception.InvalidEntityException;
import todo.entity.Step;
import todo.entity.Task;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static todo.service.StepService.getStepsByTaskId;

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
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty.");
        }

        if (description == null || description.isEmpty()) {
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

    public static void getAllTasks() {
        List<Entity> taskEntities = Database.getAll(Task.TASK_ENTITY_CODE);
        List<Task> tasks = new ArrayList<>();
        for (Entity entity : taskEntities) {
            tasks.add((Task) entity);
        }

        Collections.sort(tasks, Comparator.comparing(Task::getDueDate));

        List<Entity> stepEntities = Database.getAll(Step.STEP_ENTITY_CODE);
        Map<Integer, List<Step>> stepsByTaskId = new HashMap<>();
        for (Entity entity : stepEntities) {
            Step step = (Step) entity;
            int taskId = step.getTaskRef();
            if (!stepsByTaskId.containsKey(taskId)) {
                stepsByTaskId.put(taskId, new ArrayList<>());
            }
            stepsByTaskId.get(taskId).add(step);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Task task : tasks) {
            System.out.println("ID: " + task.getId());
            System.out.println("Title: " + task.getTitle());
            System.out.println("Description: " + task.getDescription());
            System.out.println("Due Date: " + dateFormat.format(task.getDueDate()));
            System.out.println("Status: " + task.getStatus());

            List<Step> taskSteps = stepsByTaskId.get(task.getId());
            if (taskSteps != null && !taskSteps.isEmpty()) {
                System.out.println("Steps:");
                for (Step step : taskSteps) {
                    System.out.println("\t+ " + step.getTitle() + ":");
                    System.out.println("\t\tID: " + step.getId());
                    System.out.println("\t\tStatus: " + step.getStatus());
                }
            }
        }
    }

    public static void getIncompleteTasks() {
        List<Task> allTasks = new ArrayList<>();
        for (Entity entity : Database.getAll(Task.TASK_ENTITY_CODE)) {
            Task task = (Task) entity;
            if (task.getStatus() != Task.Status.COMPLETED) {
                allTasks.add(task);
            }
        }

        for (int i = 0; i < allTasks.size(); i++) {
            for (int j = i + 1; j < allTasks.size(); j++) {
                if (allTasks.get(i).getDueDate().after(allTasks.get(j).getDueDate())) {
                    Task temp = allTasks.get(i);
                    allTasks.set(i, allTasks.get(j));
                    allTasks.set(j, temp);
                }
            }
        }

        for (Task task : allTasks) {
            DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

            System.out.println("ID: " + task.getId());
            System.out.println("Title: " + task.getTitle());
            System.out.println("Description: " + task.getDescription());
            System.out.println("Due Date: " + dateformat.format(task.getDueDate()));
            System.out.println("Status: " + task.getStatus());

            List<Step> steps = getStepsByTaskId(task.getId());

            if (!steps.isEmpty()) {
                System.out.println("Steps:");
                for (Step step : steps) {
                    System.out.println("\t+ " + step.getTitle() + ":");
                    System.out.println("\t\tID: " + step.getId());
                    System.out.println("\t\tStatus: " + step.getStatus());
                }
            }
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





