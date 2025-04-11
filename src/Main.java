import exception.EntityNotFoundException;
import todo.entity.Step;
import todo.entity.Task;
import todo.service.EntityService;
import todo.service.TaskService;
import todo.service.StepService;
import exception.InvalidEntityException;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter command: ");
            String command = scanner.nextLine().trim();

            if (command.equals("exit")) {
                System.out.println("Exiting the program.");
                break;
            }

            if (command.equals("add task")) {
                System.out.print("Title: ");
                String title = scanner.nextLine();

                System.out.print("Description: ");
                String description = scanner.nextLine();

                System.out.print("Due date (yyyy-MM-dd): ");
                String dueDateString = scanner.nextLine();

                try {
                    TaskService.addTask(title, description, dueDateString);
                    System.out.println("Task saved successfully.");
                } catch (InvalidEntityException | IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else if (command.equals("add step")) {
                System.out.print("TaskID: ");
                String taskIdInput = scanner.nextLine();

                System.out.print("Title: ");
                String title = scanner.nextLine();

                try {
                    int taskId = Integer.parseInt(taskIdInput);
                    StepService.addStep(taskId, title);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid task ID. Please enter a number.");
                }
            } else if (command.equals("delete")) {
                System.out.print("ID: ");
                String idInput = scanner.nextLine();

                try {
                    int id = Integer.parseInt(idInput);
                    EntityService.deleteEntity(id);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid ID format.");
                }
            } else if (command.equals("update task")) {
                System.out.print("ID: ");
                String idInput = scanner.nextLine();

                System.out.print("Field: ");
                String field = scanner.nextLine();

                System.out.print("New Value: ");
                String newValue = scanner.nextLine();

                try {
                    int id = Integer.parseInt(idInput);
                    TaskService.updateTask(id, field, newValue);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid ID format.");
                }
            } else if (command.equals("update step")) {
                System.out.print("ID: ");
                String idInput = scanner.nextLine();

                System.out.print("Field: ");
                String field = scanner.nextLine();

                System.out.print("New Value: ");
                String newValue = scanner.nextLine();

                try {
                    int id = Integer.parseInt(idInput);
                    StepService.updateStep(id, field, newValue);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid ID format.");
                }
            } else if (command.equals("get task-by-id")) {
                System.out.print("ID: ");
                String idInput = scanner.nextLine();

                try {
                    int id = Integer.parseInt(idInput);
                    Task task = TaskService.getTaskById(id);

                    if (task != null) {
                        System.out.println("ID: " + task.getId());
                        System.out.println("Title: " + task.getTitle());
                        System.out.println("Due Date: " + task.getDueDate());
                        System.out.println("Status: " + task.getStatus());

                        List<Step> steps = StepService.getStepsByTaskId(id);
                        if (steps.isEmpty()) {
                            System.out.println("Steps: None");
                        } else {
                            System.out.println("Steps:");
                            for (Step step : steps) {
                                System.out.println("    + " + step.getTitle() + ":");
                                System.out.println("        ID: " + step.getId());
                                System.out.println("        Status: " + step.getStatus());
                            }
                        }
                    } else {
                        System.out.println("Cannot find task with ID=" + id);
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Invalid ID format.");
                } catch (EntityNotFoundException e) {
                    System.out.println("Cannot find task with ID=" + idInput);
                }
            } else {
                System.out.println("Unknown command.");
            }

        }

        scanner.close();
    }
}
