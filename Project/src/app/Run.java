import java.util.*;

import db.*;
import students.Student;

public class Test {
    private static Scanner scanner = new Scanner(System.in);
    private static DatabaseManager dbManager = new DatabaseManager();

    public static void main(String[] args) {
        System.out.println("University Student Database Management System");

        // Add shutdown hook to save on exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            dbManager.saveAllToDatabase();
            dbManager.close();
            System.out.println("\nData saved. Database connection closed.");
        }));

        boolean running = true;
        while (running) {
            printMenu();
            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    addGrade();
                    break;
                case 3:
                    removeStudent();
                    break;
                case 4:
                    findStudent();
                    break;
                case 5:
                    performStudentSkill();
                    break;
                case 6:
                    dbManager.printAllStudents();
                    break;
                case 7:
                    dbManager.printDepartmentAverages();
                    break;
                case 8:
                    dbManager.printStudentCounts();
                    break;
                case 9:
                    saveStudentToFile();
                    break;
                case 10:
                    loadStudentFromFile();
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        System.out.println("Program terminated. Data will be saved on exit.");
    }

    private static void printMenu() {
        System.out.println("\nMenu:");
        System.out.println("1. Add new student");
        System.out.println("2. Add grade to student");
        System.out.println("3. Remove student");
        System.out.println("4. Find student by ID");
        System.out.println("5. Perform student's skill");
        System.out.println("6. List all students (alphabetically)");
        System.out.println("7. Show department averages");
        System.out.println("8. Show student counts by department");
        System.out.println("9. Save student to file");
        System.out.println("10. Load student from file");
        System.out.println("0. Exit");
    }

    private static int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.println("Please enter a valid number.");
            scanner.next();
        }
        return scanner.nextInt();
    }

    private static void addStudent() {
        System.out.println("\nAdd New Student");
        System.out.println("1. Telecommunications");
        System.out.println("2. Cybersecurity");
        int type = getIntInput("Select department: ");

        if (type != 1 && type != 2) {
            System.out.println("Invalid department selection.");
            return;
        }

        scanner.nextLine(); // consume newline
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        int birthYear = getIntInput("Enter birth year: ");

        dbManager.addStudent(type, firstName, lastName, birthYear);
        System.out.println("Student added successfully.");
    }

    private static void addGrade() {
        int studentId = getIntInput("Enter student ID: ");
        int grade = getIntInput("Enter grade (1-5): ");

        if (grade < 1 || grade > 5) {
            System.out.println("Grade must be between 1 and 5.");
            return;
        }

        dbManager.addGrade(studentId, grade);
        System.out.println("Grade added successfully.");
    }

    private static void removeStudent() {
        int studentId = getIntInput("Enter student ID to remove: ");
        dbManager.removeStudent(studentId);
    }

    private static void findStudent() {
        int studentId = getIntInput("Enter student ID: ");
        Student student = dbManager.findStudentById(studentId);
        if (student != null) {
            System.out.println(student);
        } else {
            System.out.println("Student not found.");
        }
    }

    private static void performStudentSkill() {
        int studentId = getIntInput("Enter student ID: ");
        dbManager.performStudentSkill(studentId);
    }

    private static void saveStudentToFile() {
        int studentId = getIntInput("Enter student ID to save: ");
        scanner.nextLine(); // consume newline
        System.out.print("Enter filename: ");
        String filename = scanner.nextLine();
        dbManager.saveStudentToFile(studentId, filename);
    }

    private static void loadStudentFromFile() {
    	int studentId = getIntInput("Enter student ID to load: ");
        scanner.nextLine(); // consume newline
        System.out.print("Enter filename: ");
        String filename = scanner.nextLine();
        dbManager.loadStudentFromFile(filename, studentId);
    }
}